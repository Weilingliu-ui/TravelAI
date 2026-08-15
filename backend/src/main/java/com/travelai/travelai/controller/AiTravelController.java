package com.travelai.travelai.controller;

import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.dto.AiTravelRequest;
import com.travelai.travelai.prompt.TravelPromptBuilder;
import com.travelai.travelai.service.AiTravelService;
import com.travelai.travelai.service.AttractionService;
import com.travelai.travelai.vo.AiAttractionCandidate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "AI 旅行规划")
@RestController
@RequestMapping("/api/ai")
public class AiTravelController {

    @Resource
    private AiTravelService aiTravelService;

    @Resource
    private AttractionService attractionService;

    @Resource
    private TravelPromptBuilder promptBuilder;

    @Operation(summary = "生成攻略并保存")
    @PostMapping("/generate")
    public Result<Long> generate(@Valid @RequestBody AiTravelRequest request) {
        Long planId = aiTravelService.generateAndSavePlan(request);
        return Result.success(planId);
    }

    @Operation(summary = "调试接口：查看候选景点和 Prompt 预览")
    @PostMapping("/travel/debug")
    public Result<Map<String, Object>> debug(@Valid @RequestBody AiTravelRequest request) {
        List<AiAttractionCandidate> candidates = attractionService.recommendForAi(
                request.getDestination(), request.getInterests(), 20);
        String promptPreview = promptBuilder.buildTravelPrompt(request, candidates);

        return Result.success(Map.of(
                "candidates", candidates,
                "candidateCount", candidates.size(),
                "promptPreview", promptPreview
        ));
    }
}
