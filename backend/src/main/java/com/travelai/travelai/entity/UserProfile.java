package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户画像实体
 *
 * @author TravelAI Team
 */
@Data
@TableName("user_profile")
public class UserProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 昵称 */
    private String nickname;

    /** 性别 0=未知 1=男 2=女 */
    private Integer gender;

    /** 出生年份 */
    private Integer birthYear;

    /** 常住城市 */
    private String homeCity;

    /** 旅行偏好 JSON */
    private String travelPreference;

    /** 预算偏好 JSON */
    private String budgetPreference;

    /** 饮食偏好 JSON */
    private String foodPreference;

    /** 用户标签 JSON */
    private String tags;

    /** 累计生成攻略次数 */
    private Integer totalTrips;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
