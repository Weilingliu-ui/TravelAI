package com.travelai.travelai.controller;

import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.entity.TravelDay;
import com.travelai.travelai.entity.TravelPlan;
import com.travelai.travelai.entity.TravelRoute;
import com.travelai.travelai.service.TravelDayService;
import com.travelai.travelai.service.TravelPlanService;
import com.travelai.travelai.service.TravelRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 旅游攻略 Controller
 *
 * @author TravelAI Team
 */
@RestController
@RequestMapping("/api/travel-plans")
@RequiredArgsConstructor
public class TravelPlanController {

    private final TravelPlanService travelPlanService;
    private final TravelDayService travelDayService;
    private final TravelRouteService travelRouteService;

    // ==================== 攻略计划 CRUD ====================

    @GetMapping("/{id}")
    public Result<TravelPlan> getById(@PathVariable Long id) {
        return Result.success(travelPlanService.getById(id));
    }

    @GetMapping
    public Result<List<TravelPlan>> list() {
        return Result.success(travelPlanService.listByUser());
    }

    @PostMapping
    public Result<TravelPlan> save(@RequestBody TravelPlan travelPlan) {
        travelPlanService.save(travelPlan);
        return Result.success(travelPlan);
    }

    @PutMapping
    public Result<TravelPlan> update(@RequestBody TravelPlan travelPlan) {
        travelPlanService.updateById(travelPlan);
        return Result.success(travelPlan);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        travelPlanService.removeById(id);
        return Result.success();
    }

    // ==================== 每日行程 CRUD ====================

    @GetMapping("/{planId}/days")
    public Result<List<TravelDay>> listDays(@PathVariable Long planId) {
        List<TravelDay> days = travelDayService.lambdaQuery()
                .eq(TravelDay::getPlanId, planId)
                .orderByAsc(TravelDay::getDayNumber)
                .list();
        return Result.success(days);
    }

    @GetMapping("/{planId}/days/{dayId}")
    public Result<TravelDay> getDay(@PathVariable Long dayId) {
        return Result.success(travelDayService.getById(dayId));
    }

    @PostMapping("/{planId}/days")
    public Result<TravelDay> saveDay(@PathVariable Long planId, @RequestBody TravelDay travelDay) {
        travelDay.setPlanId(planId);
        travelDayService.save(travelDay);
        return Result.success(travelDay);
    }

    @PutMapping("/{planId}/days")
    public Result<TravelDay> updateDay(@RequestBody TravelDay travelDay) {
        travelDayService.updateById(travelDay);
        return Result.success(travelDay);
    }

    @DeleteMapping("/{planId}/days/{dayId}")
    public Result<Void> deleteDay(@PathVariable Long dayId) {
        travelDayService.removeById(dayId);
        return Result.success();
    }

    // ==================== 行程节点 CRUD ====================

    @GetMapping("/{planId}/days/{dayId}/routes")
    public Result<List<TravelRoute>> listRoutes(@PathVariable Long dayId) {
        List<TravelRoute> routes = travelRouteService.lambdaQuery()
                .eq(TravelRoute::getDayId, dayId)
                .orderByAsc(TravelRoute::getSortOrder)
                .list();
        return Result.success(routes);
    }

    @PostMapping("/{planId}/days/{dayId}/routes")
    public Result<TravelRoute> saveRoute(@PathVariable Long planId,
                                         @PathVariable Long dayId,
                                         @RequestBody TravelRoute travelRoute) {
        travelRoute.setPlanId(planId);
        travelRoute.setDayId(dayId);
        travelRouteService.save(travelRoute);
        return Result.success(travelRoute);
    }

    @PutMapping("/{planId}/days/{dayId}/routes")
    public Result<TravelRoute> updateRoute(@RequestBody TravelRoute travelRoute) {
        travelRouteService.updateById(travelRoute);
        return Result.success(travelRoute);
    }

    @DeleteMapping("/{planId}/days/{dayId}/routes/{routeId}")
    public Result<Void> deleteRoute(@PathVariable Long routeId) {
        travelRouteService.removeById(routeId);
        return Result.success();
    }
}
