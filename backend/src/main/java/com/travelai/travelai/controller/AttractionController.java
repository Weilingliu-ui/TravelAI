package com.travelai.travelai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.entity.Attraction;
import com.travelai.travelai.service.AttractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点查询 Controller
 *
 * @author TravelAI Team
 */
@Tag(name = "景点管理", description = "景点分页/搜索/热门/详情")
@RestController
@RequestMapping("/api/attractions")
public class AttractionController {

    @Resource
    private AttractionService attractionService;

    @Operation(summary = "分页查询景点")
    @GetMapping
    public Result<IPage<Attraction>> pageQuery(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "城市ID") @RequestParam(required = false) Long cityId,
            @Parameter(description = "关键词（名称+描述）") @RequestParam(required = false) String keyword,
            @Parameter(description = "标签分类") @RequestParam(required = false) String tag,
            @Parameter(description = "排序: popularity/rating/latest") @RequestParam(defaultValue = "popularity") String sortBy) {
        return Result.success(attractionService.pageQuery(pageNum, pageSize, cityId, keyword, tag, sortBy));
    }

    @Operation(summary = "热门景点")
    @GetMapping("/hot")
    public Result<List<Attraction>> hot(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(attractionService.listHotAttractions(limit));
    }

    @Operation(summary = "搜索景点")
    @GetMapping("/search")
    public Result<List<Attraction>> search(
            @Parameter(description = "关键词") @RequestParam String keyword) {
        return Result.success(attractionService.searchByName(keyword));
    }

    @Operation(summary = "景点详情")
    @GetMapping("/{id}")
    public Result<Attraction> getDetail(
            @Parameter(description = "景点ID") @PathVariable Long id) {
        return Result.success(attractionService.getDetail(id));
    }
}
