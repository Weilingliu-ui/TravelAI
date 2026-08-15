package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 每日行程实体
 *
 * @author TravelAI Team
 */
@Data
@TableName("travel_day")
public class TravelDay {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 攻略计划ID */
    private Long planId;

    /** 第几天（从1开始） */
    private Integer dayNumber;

    /** 日期描述 */
    private String dateLabel;

    /** 当日主题 */
    private String title;

    /** 当日概述 */
    private String description;

    /** 天气建议 */
    private String weatherAdvice;

    /** 住宿建议 */
    private String accommodation;

    /** 预估住宿费用 */
    private BigDecimal accommodationCost;

    /** 当日预估总费用 */
    private BigDecimal estimatedCost;

    /** 排序 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
