package com.travelai.travelai.prompt;

import lombok.Getter;

/**
 * Prompt 场景枚举
 * <p>
 * 每个场景对应数据库 prompt_template.scene 字段，
 * 也对应 TravelPromptBuilder 中的默认模板。
 *
 * @author TravelAI Team
 */
@Getter
public enum PromptScene {

    TRAVEL_SYSTEM("travel_system", "通用旅行规划"),
    BUDGET_TRIP("budget_trip", "预算优先旅行"),
    FAMILY_TRIP("family_trip", "亲子家庭旅行"),
    COUPLE_TRIP("couple_trip", "情侣浪漫旅行"),
    PHOTOGRAPHY_TRIP("photography_trip", "摄影打卡旅行"),
    FOOD_TRIP("food_trip", "美食探索旅行");

    private final String code;
    private final String description;

    PromptScene(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PromptScene fromCode(String code) {
        for (PromptScene scene : values()) {
            if (scene.code.equalsIgnoreCase(code)) {
                return scene;
            }
        }
        return TRAVEL_SYSTEM;
    }
}
