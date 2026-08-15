package com.travelai.travelai.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * AI 生成的预算明细结构
 *
 * @author TravelAI Team
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiBudgetBreakdown {

    private BigDecimal accommodation;
    private BigDecimal food;
    private BigDecimal transport;
    private BigDecimal tickets;
    private BigDecimal shopping;
    private BigDecimal other;
}
