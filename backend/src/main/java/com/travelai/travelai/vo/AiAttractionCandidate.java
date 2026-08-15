package com.travelai.travelai.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * AI 候选景点 DTO
 * <p>
 * 从数据库查询真实景点，作为 Prompt 候选数据喂给大模型
 *
 * @author TravelAI Team
 */
@Data
@AllArgsConstructor
public class AiAttractionCandidate {

    private Long id;
    private String name;
    private String cityName;
    private String tag;
    private BigDecimal rating;
    private Long heat;
    private String description;
}
