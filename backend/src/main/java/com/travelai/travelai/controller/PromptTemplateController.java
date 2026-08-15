package com.travelai.travelai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.entity.PromptTemplate;
import com.travelai.travelai.service.PromptTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * Prompt 模板管理 Controller
 *
 * @author TravelAI Team
 */
@Tag(name = "Prompt 模板管理", description = "AI Prompt 模板 CRUD")
@RestController
@RequestMapping("/api/prompt-templates")
public class PromptTemplateController {

    @Resource
    private PromptTemplateService promptTemplateService;

    @Operation(summary = "分页查询")
    @GetMapping
    public Result<IPage<PromptTemplate>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "按 name 模糊查询") @RequestParam(required = false) String name,
            @Parameter(description = "按 templateType 模糊查询") @RequestParam(required = false) String type) {
        return Result.success(promptTemplateService.pageQuery(
                new Page<>(page, size), name, type));
    }

    @Operation(summary = "查询详情")
    @GetMapping("/{id}")
    public Result<PromptTemplate> getById(@Parameter(description = "模板ID") @PathVariable Long id) {
        return Result.success(promptTemplateService.getById(id));
    }

    @Operation(summary = "新增模板")
    @PostMapping
    public Result<PromptTemplate> save(@RequestBody PromptTemplate template) {
        promptTemplateService.save(template);
        return Result.success(template);
    }

    @Operation(summary = "修改模板")
    @PutMapping
    public Result<PromptTemplate> update(@RequestBody PromptTemplate template) {
        promptTemplateService.updateById(template);
        return Result.success(template);
    }

    @Operation(summary = "删除模板（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "模板ID") @PathVariable Long id) {
        promptTemplateService.removeById(id);
        return Result.success();
    }
}
