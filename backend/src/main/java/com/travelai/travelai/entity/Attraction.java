package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点实体
 *
 * @author TravelAI Team
 */
@Data
@TableName("attraction")
public class Attraction {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 景点名称 */
    private String name;

    /** 景点名称（英文/拼音） */
    private String nameEn;

    /** 所属城市ID */
    private Long cityId;

    /** 景点分类 */
    private String category;

    /** 景点描述 */
    private String description;

    /** 详细地址 */
    private String address;

    /** 纬度 */
    private BigDecimal latitude;

    /** 经度 */
    private BigDecimal longitude;

    /** 门票价格（元） */
    private BigDecimal ticketPrice;

    /** 门票说明 */
    private String ticketDesc;

    /** 开放时间描述 */
    private String openingHours;

    /** 建议游览时长（分钟） */
    private Integer duration;

    /** 评分 */
    private BigDecimal rating;

    /** 景点封面图URL */
    private String imageUrl;

    /** 景点封面图（MinIO） */
    private String coverImage;

    /** 景点图片集 JSON */
    private String images;

    /** 游览贴士 */
    private String tips;

    /** 是否必游景点 */
    private Integer isMustVisit;

    /** 是否免费 */
    private Integer isFree;

    /** 是否室内景点 */
    private Integer isIndoor;

    /** 最佳游览季节 */
    private String seasonBest;

    /** 虚拟访问量 */
    private Long visitCount;

    /** 排序权重 */
    private Integer sortOrder;

    /** 状态 0=禁用 1=启用 */
    private Integer status;

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
