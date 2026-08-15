package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 景点标签实体
 *
 * @author TravelAI Team
 */
@Data
@TableName("scenic_tag")
public class ScenicTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标签名称 */
    private String name;

    /** 标签分类（nature/culture/food/shopping/activity/family/couple/photography/other） */
    private String type;

    /** 标签图标URL */
    private String icon;

    /** 排序权重 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
