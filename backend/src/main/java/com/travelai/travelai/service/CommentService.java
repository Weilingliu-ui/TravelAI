package com.travelai.travelai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travelai.travelai.entity.Comment;
import com.travelai.travelai.vo.CommentVO;

public interface CommentService extends IService<Comment> {

    CommentVO addComment(Long attractionId, String content, Integer rating);

    IPage<CommentVO> pageByAttraction(Long attractionId, int page, int size);

    boolean deleteComment(Long commentId);
}
