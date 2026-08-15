package com.travelai.travelai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelai.travelai.entity.TravelPlan;

import java.util.List;

public interface TravelPlanService extends IService<TravelPlan> {

    /** 查询当前用户的旅行计划列表 */
    List<TravelPlan> listByUser();
}
