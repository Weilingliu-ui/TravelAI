package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 旅游知识库实体（RAG数据源）
 *
 * @author TravelAI Team
 */
@Data
@TableName("travel_knowledge")
public class TravelKnowledge {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 知识标题 */
    private String title;

    /** 知识正文 */
    private String content;

    /** 知识摘要 */
    private String summary;

    /** 知识分类 */
    private String category;

    /** 关联城市ID */
    private Long cityId;

    /** 关联景点ID */
    private Long attractionId;

    /** 知识标签 JSON */
    private String tags;

    /** 来源类型 */
    private String sourceType;

    /** 知识来源名称 */
    private String source;

    /** 来源URL */
    private String sourceUrl;

    /** 向量ID */
    private String embeddingId;

    /** 可靠度 */
    private Integer reliability;

    /** 被引用次数 */
    private Long useCount;

    /** 生效开始日期 */
    private LocalDate effectiveStart;

    /** 生效结束日期 */
    private LocalDate effectiveEnd;

    /** 版本号 */
    private Integer version;

    /** 状态 0=草稿 1=已发布 2=过期 */
    private Integer status;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
