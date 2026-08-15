package com.travelai.travelai.service.impl;

import com.travelai.travelai.common.exception.AiServiceException;
import com.travelai.travelai.dto.AiTravelRequest;
import com.travelai.travelai.entity.TravelDay;
import com.travelai.travelai.entity.TravelPlan;
import com.travelai.travelai.entity.TravelRoute;
import com.travelai.travelai.mapper.TravelDayMapper;
import com.travelai.travelai.mapper.TravelPlanMapper;
import com.travelai.travelai.mapper.TravelRouteMapper;
import com.travelai.travelai.prompt.TravelPromptBuilder;
import com.travelai.travelai.security.SecurityUtils;
import com.travelai.travelai.service.AiTravelService;
import com.travelai.travelai.service.AttractionService;
import com.travelai.travelai.service.UserProfileService;
import com.travelai.travelai.util.JsonUtils;
import com.travelai.travelai.util.RedisUtils;
import com.travelai.travelai.vo.AiAttractionCandidate;
import com.travelai.travelai.vo.AiBudget;
import com.travelai.travelai.vo.AiBudgetBreakdown;
import com.travelai.travelai.vo.AiDay;
import com.travelai.travelai.vo.AiGeneratedPlan;
import com.travelai.travelai.vo.AiRoute;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AiTravelServiceImpl implements AiTravelService {

    private static final String CACHE_PREFIX = "travel:ai:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);
    private static final int AI_CANDIDATE_LIMIT = 20;
    private static final int SEGMENT_THRESHOLD = 7;
    private static final int SEGMENT_SIZE = 3;

    @Value("${travelai.ai.token-per-day:1800}")
    private int tokenPerDay;

    @Value("${travelai.ai.min-tokens:4096}")
    private int minTokens;

    @Value("${travelai.ai.max-tokens:16384}")
    private int maxTokensLimit;

    @Resource
    private ChatClient chatClient;

    @Resource
    private TravelPromptBuilder promptBuilder;

    @Resource
    private AttractionService attractionService;

    @Resource
    private JsonUtils jsonUtils;

    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private TravelPlanMapper travelPlanMapper;

    @Resource
    private TravelDayMapper travelDayMapper;

    @Resource
    private TravelRouteMapper travelRouteMapper;

    @Autowired(required = false)
    private RedisUtils redisUtils;

    @Resource
    private UserProfileService userProfileService;

    @Override
    public String generateTravelPlan(AiTravelRequest request) {
        fillOrigin(request);

        List<AiAttractionCandidate> candidates = attractionService.recommendForAi(
                request.getDestination(), request.getInterests(), AI_CANDIDATE_LIMIT);
        log.info("[AI CANDIDATES] destination={}, count={}", request.getDestination(), candidates.size());

        // 超长行程分段生成
        if (request.getDays() > SEGMENT_THRESHOLD) {
            return generateSegmentedPlan(request, candidates);
        }

        // 短行程单次生成
        String prompt = promptBuilder.buildTravelPrompt(request, candidates);
        int tokens = dynamicTokens(request);
        String result = chatClient.prompt()
                .user(prompt)
                .options(OpenAiChatOptions.builder().maxTokens(tokens).build())
                .call().content();
        if (result == null || result.isBlank()) {
            throw new AiServiceException("AI生成失败");
        }
        return cleanJsonResponse(result);
    }

    // ==================== 分段生成 ====================

    private String generateSegmentedPlan(AiTravelRequest request, List<AiAttractionCandidate> candidates) {
        int days = request.getDays();
        log.info("[AI SEGMENT] generating segmented plan, days={}", days);

        // 1. 生成 overview + 每日主题
        String overviewJson = generateOverview(request);
        AiGeneratedPlan overview = jsonUtils.fromJson(overviewJson, AiGeneratedPlan.class);

        // 2. 逐段生成明细
        List<AiDay> allDays = new ArrayList<>();
        List<AiBudget> allBudgets = new ArrayList<>();

        for (int start = 1; start <= days; start += SEGMENT_SIZE) {
            int end = Math.min(start + SEGMENT_SIZE - 1, days);
            log.info("[AI SEGMENT] generating days {}-{}", start, end);
            String segmentJson = generateSegment(request, start, end, candidates);
            AiGeneratedPlan segment = jsonUtils.fromJson(segmentJson, AiGeneratedPlan.class);

            if (segment.getDays() != null) {
                allDays.addAll(segment.getDays());
            }
            if (segment.getBudget() != null) {
                allBudgets.add(segment.getBudget());
            }
        }

        // 3. 强制重新编号 day，确保 1,2,3,... 连续
        for (int i = 0; i < allDays.size(); i++) {
            allDays.get(i).setDay(i + 1);
        }

        // 4. 合并
        AiGeneratedPlan merged = new AiGeneratedPlan();
        merged.setOverview(overview.getOverview());
        merged.setOverallSuggestions(overview.getOverallSuggestions());
        merged.setDays(allDays);
        merged.setBudget(mergeBudgets(allBudgets));

        log.info("[AI SEGMENT] merged segments={}, totalDays={}",
                allBudgets.size(), allDays.size());
        return jsonUtils.toJson(merged);
    }

    private String generateOverview(AiTravelRequest request) {
        int days = request.getDays();
        String prompt = """
                你是资深旅游规划师。请为以下行程生成总览，只返回JSON。

                【行程信息】
                目的地：%s，天数：%d天，预算：%s元，人数：%d人，偏好：%s

                【JSON格式】
                {
                  "overview": "行程总览（50字内）",
                  "overallSuggestions": "实用建议（50字内）",
                  "budget": {"total": 0, "breakdown": {"accommodation":0,"food":0,"transport":0,"tickets":0,"shopping":0,"other":0}},
                  "days": [{"day":1,"title":"第1天主题","description":"简述"}]
                }

                【规则】只返回JSON，不要解释。days数组请包含全部%d天，每天一条主题。
                """.formatted(request.getDestination(), days, request.getBudget().toPlainString(),
                request.getTravelers(),
                request.getInterests() != null ? request.getInterests() : "综合体验",
                days);

        String result = chatClient.prompt().user(prompt)
                .options(OpenAiChatOptions.builder().maxTokens(4096).build())
                .call().content();
        if (result == null || result.isBlank()) throw new AiServiceException("Overview生成失败");
        return cleanJsonResponse(result);
    }

    private String generateSegment(AiTravelRequest request, int startDay, int endDay,
                                     List<AiAttractionCandidate> candidates) {
        String candidatesText = buildCandidatesText(candidates);
        String prompt = """
                你是资深旅游规划师。请为以下行程的第%d~%d天生成详细规划，只返回JSON。

                目的地：%s，预算：%s元，人数：%d人，偏好：%s，出行方式：%s

                %s

                【JSON格式】
                {
                  "budget": {"total": 0, "breakdown": {"accommodation":0,"food":0,"transport":0,"tickets":0,"shopping":0,"other":0}},
                  "days": [
                    {
                      "day": %d, "title": "主题", "description": "15字概述",
                      "accommodation": "酒店名", "accommodationCost": 0, "estimatedCost": 0,
                      "routes": [
                        {"order":1,"nodeType":"attraction","attractionId":0,"name":"景点","description":"15字","startTime":"09:00","endTime":"11:00","durationMinutes":120,"transportFromPrev":"walking","transportDuration":15,"transportCost":0,"estimatedCost":0,"tips":"贴士"},
                        {"order":2,"nodeType":"restaurant","name":"餐厅名","description":"招牌菜名","estimatedCost":60},
                        {"order":3,"nodeType":"hotel","name":"酒店名","description":"设施简介","estimatedCost":200}
                      ]
                    }
                  ]
                }

                【硬性要求】
                1. 每天至少4个节点: 2个attraction + 1个restaurant + 1个hotel
                2. 每天节点数量必须相同，保持一致
                3. 优先从候选景点中选择attraction，使用对应的attractionId
                4. restaurant必须写本地真实餐厅名和招牌菜
                """.formatted(startDay, endDay,
                request.getDestination(), request.getBudget().toPlainString(),
                request.getTravelers(),
                request.getInterests() != null ? request.getInterests() : "综合体验",
                request.getTravelMode() != null ? request.getTravelMode() : "未指定",
                candidatesText, startDay);

        int tokens = Math.max(4096, SEGMENT_SIZE * tokenPerDay);
        String result = chatClient.prompt().user(prompt)
                .options(OpenAiChatOptions.builder().maxTokens(tokens).build())
                .call().content();
        if (result == null || result.isBlank()) throw new AiServiceException("Segments生成失败");
        return cleanJsonResponse(result);
    }

    /** 清洗 AI 返回的 JSON：去掉 ```json ``` 包裹、前后空白 */
    private String cleanJsonResponse(String raw) {
        if (raw == null || raw.isBlank()) return raw;
        String s = raw.trim();
        if (s.startsWith("```")) {
            int start = s.indexOf("\n");
            int end = s.lastIndexOf("```");
            if (start > 0 && end > start) {
                s = s.substring(start + 1, end).trim();
            } else if (end > 3) {
                s = s.substring(3, end).trim();
            }
        }
        return s;
    }

    private String buildCandidatesText(List<AiAttractionCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("【真实景点数据库】\n");
        for (int i = 0; i < candidates.size(); i++) {
            var c = candidates.get(i);
            sb.append((i + 1)).append(". ").append(c.getName())
                    .append(" ID=").append(c.getId())
                    .append(" ").append(c.getTag()).append("\n");
        }
        return sb.toString();
    }

    private AiBudget mergeBudgets(List<AiBudget> budgets) {
        AiBudget merged = new AiBudget();
        merged.setTotal(0L);
        AiBudgetBreakdown bd = new AiBudgetBreakdown();
        bd.setAccommodation(BigDecimal.ZERO);
        bd.setFood(BigDecimal.ZERO);
        bd.setTransport(BigDecimal.ZERO);
        bd.setTickets(BigDecimal.ZERO);
        bd.setShopping(BigDecimal.ZERO);
        bd.setOther(BigDecimal.ZERO);

        for (AiBudget b : budgets) {
            if (b == null || b.getBreakdown() == null) continue;
            AiBudgetBreakdown item = b.getBreakdown();
            bd.setAccommodation(sum(bd.getAccommodation(), item.getAccommodation()));
            bd.setFood(sum(bd.getFood(), item.getFood()));
            bd.setTransport(sum(bd.getTransport(), item.getTransport()));
            bd.setTickets(sum(bd.getTickets(), item.getTickets()));
            bd.setShopping(sum(bd.getShopping(), item.getShopping()));
            bd.setOther(sum(bd.getOther(), item.getOther()));
        }
        merged.setBreakdown(bd);
        merged.setTotal(bd.getAccommodation().add(bd.getFood()).add(bd.getTransport())
                .add(bd.getTickets()).add(bd.getShopping()).add(bd.getOther()).longValue());
        return merged;
    }

    private BigDecimal sum(BigDecimal a, BigDecimal b) {
        return (a != null ? a : BigDecimal.ZERO).add(b != null ? b : BigDecimal.ZERO);
    }

    // ==================== 工具方法 ====================

    private void fillOrigin(AiTravelRequest request) {
        if (request.getOrigin() == null || request.getOrigin().isBlank()) {
            try {
                var profile = userProfileService.getOrCreateMyProfile();
                if (profile.getHomeCity() != null && !profile.getHomeCity().isBlank()) {
                    request.setOrigin(profile.getHomeCity());
                    log.info("[AI ORIGIN] auto-filled from profile: {}", profile.getHomeCity());
                }
            } catch (Exception ignored) {}
        }
    }

    private int dynamicTokens(AiTravelRequest request) {
        int days = Math.max(1, request.getDays());
        int calculated = days * tokenPerDay;
        int result = Math.min(Math.max(minTokens, calculated), maxTokensLimit);
        log.info("[AI TOKENS] days={}, perDay={}, calculated={}, final={}",
                days, tokenPerDay, calculated, result);
        return result;
    }

    // ==================== 保存流程 ====================

    @Override
    @Transactional
    public Long generateAndSavePlan(AiTravelRequest request) {
        String cacheKey = buildCacheKey(request);

        AiGeneratedPlan aiPlan = getCachedPlan(cacheKey);
        if (aiPlan != null) {
            log.info("[AI CACHE HIT] key={}", cacheKey);
            return savePlanToDb(request, aiPlan);
        }

        log.info("[AI CACHE MISS] key={}", cacheKey);
        String json = generateTravelPlan(request);
        aiPlan = jsonUtils.fromJson(json, AiGeneratedPlan.class);

        Long planId = savePlanToDb(request, aiPlan);
        setCachedPlan(cacheKey, aiPlan);
        return planId;
    }

    private String buildCacheKey(AiTravelRequest request) {
        String scene = request.getScene() != null ? request.getScene() : "travel_system";
        String dest = request.getDestination() != null ? request.getDestination() : "";
        String days = String.valueOf(request.getDays());
        String budget = request.getBudget() != null ? request.getBudget().toPlainString() : "0";
        String travelers = String.valueOf(request.getTravelers());
        String interests = request.getInterests() != null ? request.getInterests() : "";
        return CACHE_PREFIX + scene + ":" + dest + ":" + days + ":" + budget + ":" + travelers + ":" + interests;
    }

    private AiGeneratedPlan getCachedPlan(String cacheKey) {
        try {
            if (redisUtils != null) {
                String json = redisUtils.get(cacheKey);
                if (json != null && !json.isEmpty()) {
                    return jsonUtils.fromJson(json, AiGeneratedPlan.class);
                }
            }
        } catch (Exception e) {
            log.debug("Redis 读取失败: {}", e.getMessage());
        }
        return null;
    }

    private void setCachedPlan(String cacheKey, AiGeneratedPlan aiPlan) {
        try {
            if (redisUtils != null) {
                redisUtils.set(cacheKey, jsonUtils.toJson(aiPlan), CACHE_TTL);
            }
        } catch (Exception e) {
            log.debug("Redis 写入失败: {}", e.getMessage());
        }
    }

    private Long savePlanToDb(AiTravelRequest request, AiGeneratedPlan aiPlan) {
        TravelPlan plan = new TravelPlan();
        plan.setUserId(securityUtils.getCurrentUserId());
        plan.setTitle(request.getDestination() + request.getDays() + "日游");
        plan.setDestinationCityId(findOrGetCityId(request.getDestination()));
        plan.setOriginCity(request.getDestination());
        plan.setDays(request.getDays());
        plan.setBudgetTotal(request.getBudget());
        plan.setTravelStyle(request.getInterests());
        plan.setOverview(aiPlan.getOverview());
        plan.setOverallSuggestions(aiPlan.getOverallSuggestions());
        plan.setStatus("completed");
        if (aiPlan.getBudget() != null) {
            plan.setTotalEstimatedCost(BigDecimal.valueOf(aiPlan.getBudget().getTotal()));
            if (aiPlan.getBudget().getBreakdown() != null) {
                plan.setBudgetBreakdown(jsonUtils.toJson(aiPlan.getBudget().getBreakdown()));
            }
        }
        travelPlanMapper.insert(plan);
        Long planId = plan.getId();

        if (aiPlan.getDays() != null) {
            for (AiDay aiDay : aiPlan.getDays()) {
                TravelDay day = new TravelDay();
                day.setPlanId(planId);
                day.setDayNumber(aiDay.getDay());
                day.setTitle(aiDay.getTitle());
                day.setDescription(aiDay.getDescription());
                day.setAccommodation(aiDay.getAccommodation());
                day.setAccommodationCost(aiDay.getAccommodationCost());
                day.setEstimatedCost(aiDay.getEstimatedCost());
                day.setSortOrder(aiDay.getDay());
                travelDayMapper.insert(day);

                if (aiDay.getRoutes() != null) {
                    for (AiRoute aiRoute : aiDay.getRoutes()) {
                        TravelRoute route = new TravelRoute();
                        route.setPlanId(planId);
                        route.setDayId(day.getId());
                        route.setSortOrder(aiRoute.getOrder());
                        route.setNodeType(aiRoute.getNodeType());
                        route.setCustomName(aiRoute.getName());
                        route.setCustomDescription(aiRoute.getDescription());
                        route.setEstimatedCost(aiRoute.getEstimatedCost());
                        route.setTips(aiRoute.getTips());
                        route.setDurationMinutes(aiRoute.getDurationMinutes());
                        route.setTransportFromPrev(aiRoute.getTransportFromPrev());
                        route.setTransportDuration(aiRoute.getTransportDuration());
                        try {
                            route.setStartTime(LocalTime.parse(aiRoute.getStartTime()));
                            route.setEndTime(LocalTime.parse(aiRoute.getEndTime()));
                        } catch (Exception ignored) {}
                        travelRouteMapper.insert(route);
                    }
                }
            }
        }
        return planId;
    }

    private Long findOrGetCityId(String destination) {
        return 1L;
    }
}
