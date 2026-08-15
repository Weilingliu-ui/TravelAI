package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 攻略模板库实体（优秀攻略范例）
 *
 * @author TravelAI Team
 */
@Data
@TableName("travel_template")
public class TravelTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 模板名称 */
    private String name;

    /** 目的地城市ID */
    private Long destinationCityId;

    /** 攻略标题 */
    private String title;

    /** 模板描述 */
    private String description;

    /** 天数 */
    private Integer days;

    /** 旅行风格 */
    private String travelStyle;

    /** 适用季节 */
    private String season;

    /** 预算级别 */
    private String budgetLevel;

    /** 参考预算（人均/元） */
    private BigDecimal budgetReference;

    /** 适合同行 */
    private String companion;

    /** 行程模板 JSON */
    private String itinerary;

    /** 模板标签 JSON */
    private String tags;

    /** 攻略综述文案 */
    private String overview;

    /** 封面图URL */
    private String coverImage;

    /** 被引用次数 */
    private Long useCount;

    /** 点赞数 */
    private Long likeCount;

    /** 是否官方推荐 */
    private Integer isOfficial;

    /** 排序权重 */
    private Integer sortOrder;

    /** 状态 0=下架 1=上架 2=草稿 */
    private Integer status;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
