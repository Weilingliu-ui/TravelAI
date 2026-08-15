package com.travelai.travelai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.service.CommentService;
import com.travelai.travelai.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评论管理", description = "景点评论")
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Resource
    private CommentService commentService;

    @Operation(summary = "发表评论")
    @PostMapping
    public Result<CommentVO> add(@Valid @RequestBody AddCommentRequest req) {
        return Result.success(commentService.addComment(req.getAttractionId(), req.getContent(), req.getRating()));
    }

    @Operation(summary = "查询景点评论")
    @GetMapping("/attraction/{attractionId}")
    public Result<IPage<CommentVO>> page(
            @Parameter(description = "景点ID") @PathVariable Long attractionId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        return Result.success(commentService.pageByAttraction(attractionId, page, size));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "评论ID") @PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }

    @Data
    public static class AddCommentRequest {
        @Parameter(description = "景点ID")
        private Long attractionId;
        @NotBlank(message = "评论内容不能为空")
        @Size(min = 1, max = 500, message = "评论长度1-500")
        private String content;
        @Min(1) @Max(5)
        private Integer rating = 5;
    }
}
