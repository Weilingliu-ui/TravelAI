package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 旅游攻略计划实体
 *
 * @author TravelAI Team
 */
@Data
@TableName("travel_plan")
public class TravelPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 攻略标题 */
    private String title;

    /** 目标城市ID */
    private Long destinationCityId;

    /** 出发城市名 */
    private String originCity;

    /** 出行天数 */
    private Integer days;

    /** 计划出发日期 */
    private LocalDate startDate;

    /** 总预算（元） */
    private BigDecimal budgetTotal;

    /** 预算明细 JSON */
    private String budgetBreakdown;

    /** 本次旅行偏好 JSON */
    private String travelPreference;

    /** 旅行风格 */
    private String travelStyle;

    /** 季节 */
    private String season;

    /** 同行人员 */
    private String companion;

    /** 攻略概览 */
    private String overview;

    /** 总体建议 */
    private String overallSuggestions;

    /** 使用的AI模型 */
    private String aiModel;

    /** Prompt模板版本 */
    private String promptVersion;

    /** AI消耗Token数 */
    private Integer aiTokensUsed;

    /** AI调用费用 */
    private BigDecimal aiCost;

    /** 预估总花费 */
    private BigDecimal totalEstimatedCost;

    /** 是否公开分享 */
    private Integer isPublic;

    /** 浏览次数 */
    private Long viewCount;

    /** 被收藏次数 */
    private Long favoriteCount;

    /** 状态 */
    private String status;

    /** 乐观锁版本号 */
    @Version
    private Integer version;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
