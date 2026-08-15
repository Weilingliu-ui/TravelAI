package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 省份/州实体
 *
 * @author TravelAI Team
 */
@Data
@TableName("province")
public class Province {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 省份名称（中文） */
    private String name;

    /** 省份名称（英文） */
    private String nameEn;

    /** 行政区划代码 */
    private String code;

    /** 所属大区 */
    private String region;

    /** 排序权重 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
