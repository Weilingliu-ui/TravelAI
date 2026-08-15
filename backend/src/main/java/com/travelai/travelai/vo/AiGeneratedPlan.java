package com.travelai.travelai.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * AI 生成的完整攻略 JSON 结构
 *
 * @author TravelAI Team
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiGeneratedPlan {

    private String overview;

    @JsonProperty("overallSuggestions")
    private String overallSuggestions;

    private AiBudget budget;

    private List<AiDay> days;
}
