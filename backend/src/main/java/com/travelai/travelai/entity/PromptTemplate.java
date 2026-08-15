package com.travelai.travelai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI Prompt模板实体
 *
 * @author TravelAI Team
 */
@Data
@TableName("prompt_template")
public class PromptTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 模板名称 */
    private String name;

    /** 模板显示名称（中文） */
    private String displayName;

    /** 模板类型（system/user/few_shot） */
    private String templateType;

    /** 适用场景 */
    private String scene;

    /** Prompt模板内容 */
    private String content;

    /** 模板变量定义 JSON */
    private String variables;

    /** 推荐模型 */
    private String model;

    /** 推荐温度参数 */
    private BigDecimal temperature;

    /** 最大输出Token数 */
    private Integer maxTokens;

    /** 模板版本号 */
    private Integer version;

    /** 是否启用 */
    private Integer isActive;

    /** 是否为该场景的默认模板 */
    private Integer isDefault;

    /** 备注说明 */
    private String remark;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
