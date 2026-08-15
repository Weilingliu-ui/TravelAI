package com.travelai.travelai.common.constant;

/**
 * 系统常量定义
 *
 * @author TravelAI Team
 */
public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    /* ==================== 系统 ==================== */
    public static final String BASE_PACKAGE = "com.travelai.travelai";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    /* ==================== 日期格式 ==================== */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_ZONE = "Asia/Shanghai";

    /* ==================== Redis Key 前缀 ==================== */
    public static final String REDIS_KEY_PREFIX = "travelai:";
    public static final String REDIS_USER_TOKEN = REDIS_KEY_PREFIX + "token:";
    public static final String REDIS_RATE_LIMIT = REDIS_KEY_PREFIX + "ratelimit:";
    public static final String REDIS_AI_CACHE = REDIS_KEY_PREFIX + "ai:cache:";

    /* ==================== 旅游相关常量 ==================== */
    /** 单次生成景点最大数量 */
    public static final int MAX_ATTRACTIONS_PER_DAY = 10;
    /** 默认出行天数 */
    public static final int DEFAULT_TRIP_DAYS = 3;
    /** 最小出行天数 */
    public static final int MIN_TRIP_DAYS = 1;
    /** 最大出行天数 */
    public static final int MAX_TRIP_DAYS = 30;
    /** 最小预算 */
    public static final double MIN_BUDGET = 100.0;
    /** 最大预算 */
    public static final double MAX_BUDGET = 1000000.0;

    /* ==================== 分页 ==================== */
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    /* ==================== 安全 ==================== */
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /* ==================== AI 相关 ==================== */
    public static final String AI_ROLE_SYSTEM = "system";
    public static final String AI_ROLE_USER = "user";
    public static final String AI_ROLE_ASSISTANT = "assistant";
    /** AI请求超时(秒) */
    public static final int AI_TIMEOUT_SECONDS = 120;
    /** AI请求重试次数 */
    public static final int AI_MAX_RETRIES = 2;
}
