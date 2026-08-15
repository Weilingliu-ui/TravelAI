package com.travelai.travelai.controller;

import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.entity.TravelPlan;
import com.travelai.travelai.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏管理 Controller
 *
 * @author TravelAI Team
 */
@Tag(name = "收藏管理", description = "攻略收藏/取消/查询")
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    @Operation(summary = "添加收藏")
    @PostMapping("/{travelPlanId}")
    public Result<Boolean> addFavorite(
            @Parameter(description = "攻略ID") @PathVariable Long travelPlanId) {
        return Result.success(favoriteService.addFavorite(travelPlanId));
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{travelPlanId}")
    public Result<Boolean> removeFavorite(
            @Parameter(description = "攻略ID") @PathVariable Long travelPlanId) {
        return Result.success(favoriteService.removeFavorite(travelPlanId));
    }

    @Operation(summary = "是否已收藏")
    @GetMapping("/{travelPlanId}/status")
    public Result<Boolean> isFavorited(
            @Parameter(description = "攻略ID") @PathVariable Long travelPlanId) {
        return Result.success(favoriteService.isFavorited(travelPlanId));
    }

    @Operation(summary = "我的收藏列表")
    @GetMapping
    public Result<List<TravelPlan>> listMyFavorites() {
        return Result.success(favoriteService.listMyFavorites());
    }
}
