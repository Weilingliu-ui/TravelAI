package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 景点-标签关联实体
 *
 * @author TravelAI Team
 */
@Data
@TableName("attraction_tag")
public class AttractionTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 景点ID */
    private Long attractionId;

    /** 标签ID */
    private Long tagId;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
