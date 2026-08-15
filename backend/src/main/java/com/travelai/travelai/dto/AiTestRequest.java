package com.travelai.travelai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 测试请求 DTO
 *
 * @author TravelAI Team
 */
@Data
public class AiTestRequest {

    @NotBlank(message = "message不能为空")
    private String message;
}
