package com.travelai.travelai.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * AI 生成的预算结构
 *
 * @author TravelAI Team
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiBudget {

    private Long total;

    private AiBudgetBreakdown breakdown;
}
