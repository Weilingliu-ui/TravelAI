package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 行程节点实体（每日路线中的具体景点/餐饮/活动）
 *
 * @author TravelAI Team
 */
@Data
@TableName("travel_route")
public class TravelRoute {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 攻略计划ID */
    private Long planId;

    /** 所属行程日ID */
    private Long dayId;

    /** 当日顺序号 */
    private Integer sortOrder;

    /** 节点类型 */
    private String nodeType;

    /** 关联景点ID */
    private Long attractionId;

    /** 自定义节点名称 */
    private String customName;

    /** 自定义节点描述 */
    private String customDescription;

    /** 节点地址 */
    private String address;

    /** 纬度 */
    private BigDecimal latitude;

    /** 经度 */
    private BigDecimal longitude;

    /** 建议开始时间 */
    private LocalTime startTime;

    /** 建议结束时间 */
    private LocalTime endTime;

    /** 建议停留时长（分钟） */
    private Integer durationMinutes;

    /** 从上一节点到此节点的交通方式 */
    private String transportFromPrev;

    /** 交通耗时（分钟） */
    private Integer transportDuration;

    /** 距离（米） */
    private Integer transportDistance;

    /** 预估费用 */
    private BigDecimal estimatedCost;

    /** AI生成的节点小贴士 */
    private String tips;

    /** 节点配图URL */
    private String imageUrl;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
