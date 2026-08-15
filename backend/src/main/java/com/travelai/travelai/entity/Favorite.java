package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏实体
 *
 * @author TravelAI Team
 */
@Data
@TableName("favorite")
public class Favorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 收藏类型（plan/attraction/route/knowledge） */
    private String itemType;

    /** 收藏对象ID */
    private Long itemId;

    /** 收藏备注 */
    private String note;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    /** 收藏时间 */
    private LocalDateTime createdAt;
}
