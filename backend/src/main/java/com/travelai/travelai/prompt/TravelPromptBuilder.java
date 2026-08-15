package com.travelai.travelai.prompt;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travelai.travelai.dto.AiTravelRequest;
import com.travelai.travelai.entity.PromptTemplate;
import com.travelai.travelai.service.PromptTemplateService;
import com.travelai.travelai.vo.AiAttractionCandidate;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 旅行规划 Prompt 构建器
 * <p>
 * 支持多场景，支持从数据库注入真实景点候选数据。
 *
 * @author TravelAI Team
 */
@Component
public class TravelPromptBuilder {

    private static final String JSON_SCHEMA = """
            {
              "overview": "攻略概览",
              "overallSuggestions": "总体建议",
              "budget": { "total": 0, "breakdown": {"accommodation":0,"food":0,"transport":0,"tickets":0,"shopping":0,"other":0} },
              "days": [
                {
                  "day": 1, "title": "主题", "description": "概述",
                  "accommodation": "住宿", "accommodationCost": 0, "estimatedCost": 0,
                  "routes": [
                    {
                      "order": 1, "nodeType": "attraction", "attractionId": 0,
                      "name": "景点名", "description": "描述",
                      "startTime": "09:00", "endTime": "11:00", "durationMinutes": 120,
                      "transportFromPrev": "walking", "transportDuration": 15, "transportCost": 0,
                      "estimatedCost": 0, "tips": "贴士"
                    },
                    {
                      "order": 4, "nodeType": "restaurant", "attractionId": 0,
                      "name": "推荐餐厅名称", "description": "招牌菜介绍",
                      "startTime": "12:00", "endTime": "13:00", "durationMinutes": 60,
                      "transportFromPrev": "walking", "transportDuration": 5, "transportCost": 0,
                      "estimatedCost": 60, "tips": "人均消费/推荐菜"
                    },
                    {
                      "order": 5, "nodeType": "hotel", "attractionId": 0,
                      "name": "推荐酒店名称", "description": "酒店简介",
                      "startTime": "18:00", "endTime": "08:00", "durationMinutes": 840,
                      "transportFromPrev": "walking", "transportDuration": 10, "transportCost": 0,
                      "estimatedCost": 250, "tips": "入住建议"
                    }
                  ]
                }
              ]
            }""";

    private static final String DEFAULT_TEMPLATE = """
            你是一位资深旅游规划师。请根据以下需求生成一份详细的{{days}}天旅行攻略。

            【用户需求】
            - 目的地：{{destination}}
            - 出发地：{{origin}}
            - 天数：{{days}}天
            - 总预算：{{budget}}元
            - 出行人数：{{travelers}}人
            - 兴趣偏好：{{interests}}
            - 出行方式：{{travelMode}}

            {{candidates}}

            【输出要求】
            1. 严格按照下方JSON格式输出
            2. 每天安排3-5个景点或活动，外加1-2家餐厅(nodeType=restaurant)，最后一个是酒店(nodeType=hotel)
            3. 路线安排合理，根据用户出行方式规划交通衔接
            4. 预算分配合理，总费用不超过预算
            5. 每个景点必须包含 attractionId（候选列表中的真实ID），候选外景点填 attractionId=0
            6. 每个节点标注建议时间和实用贴士
            7. 每个节点的 transportCost 填从前一节点到该节点的交通费用（元），步行/免费摆渡车填0
            8. 酒店节点的 estimatedCost 填每晚房价，该值等于当天的 accommodationCost
            9. 餐厅节点必须写本地特色菜名和人均消费，不要只写"中餐"
            10. budget.breakdown 各项必须等于节点费用汇总：
               transport     = 所有节点 transportCost 之和
               accommodation = 所有 hotel 节点的 estimatedCost 之和
               food          = 所有 restaurant 节点的 estimatedCost 之和
               tickets       = 所有 attraction 节点的 estimatedCost 之和
               shopping      = 所有 shopping 节点的 estimatedCost 之和
               other         = 其他节点费用
               total         = breakdown 六项之和，不得超过用户总预算

            【JSON Schema】
            {{jsonSchema}}

            【重要规则】
            - 只返回JSON，不要任何解释文字
            - 不要Markdown代码块标记
            - 直接输出 { 开头的纯JSON
            - description控制在15字以内，精炼表达
            - 行程超过4天时，每天只安排3个核心节点（景点+餐厅+酒店），不要超过5个
            """;

    private static final Map<String, String> DEFAULT_TEMPLATES = Map.of(
            "travel_system", DEFAULT_TEMPLATE,
            "budget_trip", DEFAULT_TEMPLATE.replace("预算分配合理", "预算分配合理，严格控制在预算内，优先免费/低价景点"),
            "family_trip", DEFAULT_TEMPLATE.replace("每天安排3-5个景点", "每天安排2-4个儿童友好景点"),
            "couple_trip", DEFAULT_TEMPLATE.replace("景点或活动", "浪漫景点或活动"),
            "photography_trip", DEFAULT_TEMPLATE.replace("景点或活动", "摄影打卡点"),
            "food_trip", DEFAULT_TEMPLATE.replace("景点或活动", "美食打卡点")
    );

    @Resource
    private PromptTemplateService promptTemplateService;

    public String buildTravelPrompt(AiTravelRequest request) {
        return buildTravelPrompt(request, null);
    }

    /**
     * 构建带候选景点的 Prompt
     */
    public String buildTravelPrompt(AiTravelRequest request, List<AiAttractionCandidate> candidates) {
        String scene = request.getScene() != null ? request.getScene().toLowerCase() : "travel_system";

        String templateContent = getTemplateFromDb(scene);
        if (templateContent == null || templateContent.isBlank()) {
            templateContent = DEFAULT_TEMPLATES.getOrDefault(scene, DEFAULT_TEMPLATE);
        }

        String interests = (request.getInterests() != null && !request.getInterests().isBlank())
                ? request.getInterests() : "综合体验";

        String candidatesText = buildCandidatesText(candidates);

        String travelMode = (request.getTravelMode() != null && !request.getTravelMode().isBlank())
                ? request.getTravelMode() : "未指定";

        String origin = (request.getOrigin() != null && !request.getOrigin().isBlank())
                ? request.getOrigin() : "未指定";

        return templateContent
                .replace("{{destination}}", request.getDestination())
                .replace("{{origin}}", origin)
                .replace("{{days}}", String.valueOf(request.getDays()))
                .replace("{{budget}}", request.getBudget().toPlainString())
                .replace("{{travelers}}", String.valueOf(request.getTravelers()))
                .replace("{{interests}}", interests)
                .replace("{{travelMode}}", travelMode)
                .replace("{{candidates}}", candidatesText)
                .replace("{{jsonSchema}}", JSON_SCHEMA);
    }

    private String buildCandidatesText(List<AiAttractionCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return "";
        }
        String list = IntStream.range(0, candidates.size())
                .mapToObj(i -> {
                    var c = candidates.get(i);
                    return (i + 1) + ". " + c.getName() +
                            "（ID=" + c.getId() +
                            ", 标签=" + (c.getTag() != null ? c.getTag() : "综合") +
                            ", 评分=" + (c.getRating() != null ? c.getRating() : "N/A") +
                            ", 热度=" + c.getHeat() + "）";
                })
                .collect(Collectors.joining("\n"));

        return """
                【真实景点数据库（优先从中选择）】
                以下是 %s 的真实景点，请优先从中挑选并填入 attractionId。
                如果该景点不在候选列表中，attractionId 填写 0，表示 AI 自由推荐。

                %s
                """.formatted(candidates.get(0).getCityName(), list);
    }

    private String getTemplateFromDb(String scene) {
        try {
            PromptTemplate template = promptTemplateService.getOne(
                    new LambdaQueryWrapper<PromptTemplate>()
                            .eq(PromptTemplate::getScene, scene)
                            .eq(PromptTemplate::getIsActive, 1)
                            .last("LIMIT 1")
            );
            if (template != null && template.getContent() != null) {
                return template.getContent();
            }
        } catch (Exception ignored) {}
        return null;
    }
}
