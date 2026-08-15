package com.travelai.travelai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * AI 旅行规划请求 DTO
 *
 * @author TravelAI Team
 */
@Data
public class AiTravelRequest {

    @NotBlank(message = "目的地不能为空")
    private String destination;

    /** 出发地（用户所在城市） */
    private String origin;

    @NotNull(message = "天数不能为空")
    @Min(value = 1, message = "天数至少1天")
    private Integer days;

    @NotNull(message = "预算不能为空")
    @Min(value = 0, message = "预算不能为负数")
    private BigDecimal budget;

    @NotNull(message = "出行人数不能为空")
    @Min(value = 1, message = "出行人数至少1人")
    private Integer travelers;

    private String interests;

    /** 出行方式: driving(自驾) / transit(公共交通) / walking(步行) / mixed(混合) */
    private String travelMode;

    /** Prompt 场景，默认 travel_system */
    private String scene = "travel_system";
}
