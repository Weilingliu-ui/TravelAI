package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 城市实体
 *
 * @author TravelAI Team
 */
@Data
@TableName("city")
public class City {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 城市名称（中文） */
    private String name;

    /** 城市名称（英文/拼音） */
    private String nameEn;

    /** 所属省份ID */
    private Long provinceId;

    /** 纬度 */
    private BigDecimal latitude;

    /** 经度 */
    private BigDecimal longitude;

    /** 城市等级 */
    private String level;

    /** 是否热门目的地 */
    private Integer isHot;

    /** 是否沿海城市 */
    private Integer isCoastal;

    /** 城市封面图URL */
    private String imageUrl;

    /** 城市简介 */
    private String description;

    /** 排序权重 */
    private Integer sortOrder;

    /** 状态 0=禁用 1=启用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
