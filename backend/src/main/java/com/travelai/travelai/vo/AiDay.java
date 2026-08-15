package com.travelai.travelai.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * AI 生成的每日行程结构
 *
 * @author TravelAI Team
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiDay {

    private Integer day;

    private String title;

    private String description;

    private String accommodation;

    @JsonProperty("accommodationCost")
    private BigDecimal accommodationCost;

    @JsonProperty("estimatedCost")
    private BigDecimal estimatedCost;

    private List<AiRoute> routes;
}
