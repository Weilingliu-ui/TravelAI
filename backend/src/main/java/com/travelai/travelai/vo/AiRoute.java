package com.travelai.travelai.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * AI 生成的行程节点结构
 *
 * @author TravelAI Team
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiRoute {

    private Integer order;

    @JsonProperty("nodeType")
    private String nodeType;

    private String name;

    private String description;

    @JsonProperty("startTime")
    private String startTime;

    @JsonProperty("endTime")
    private String endTime;

    @JsonProperty("durationMinutes")
    private Integer durationMinutes;

    @JsonProperty("transportFromPrev")
    private String transportFromPrev;

    @JsonProperty("transportDuration")
    private Integer transportDuration;

    @JsonProperty("estimatedCost")
    private BigDecimal estimatedCost;

    private String tips;
}
