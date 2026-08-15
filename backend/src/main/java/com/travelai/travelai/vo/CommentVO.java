package com.travelai.travelai.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {

    private Long id;
    private Long userId;
    private String nickname;
    private Long attractionId;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
}
