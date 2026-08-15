package com.travelai.travelai.service;

import com.travelai.travelai.dto.AiTravelRequest;

/**
 * AI 旅行规划服务接口
 *
 * @author TravelAI Team
 */
public interface AiTravelService {

    /**
     * 生成旅行攻略（返回原始JSON）
     */
    String generateTravelPlan(AiTravelRequest request);

    /**
     * 生成旅行攻略并保存到数据库
     *
     * @return 保存后的攻略计划ID
     */
    Long generateAndSavePlan(AiTravelRequest request);
}
