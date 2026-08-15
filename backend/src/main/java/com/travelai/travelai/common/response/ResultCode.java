package com.travelai.travelai.common.response;

import lombok.Getter;

/**
 * 统一响应状态码枚举
 * <p>
 * 1xxx - 业务成功 / 提示
 * 2xxx - 客户端错误
 * 3xxx - 服务端错误
 * 5xxx - 业务异常（AI服务、数据等）
 *
 * @author TravelAI Team
 */
@Getter
public enum ResultCode {

    /* ========== 成功 ========== */
    SUCCESS(200, "操作成功"),

    /* ========== 客户端错误 4xx ========== */
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "请求资源不存在"),
    METHOD_NOT_ALLOWED(405, "不支持的请求方法"),
    CONFLICT(409, "数据冲突"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),

    /* ========== 服务端错误 5xx ========== */
    INTERNAL_ERROR(500, "服务器内部异常"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    /* ========== 业务异常 1xxx ========== */
    PARAM_ERROR(1001, "参数校验失败"),
    PARAM_MISSING(1002, "缺少必要参数"),
    DATA_NOT_FOUND(1003, "数据不存在"),
    DATA_DUPLICATE(1004, "数据重复"),
    OPERATION_FAILED(1005, "操作失败"),

    /* ========== AI 服务异常 2xxx ========== */
    AI_SERVICE_ERROR(2001, "AI服务调用异常"),
    AI_TIMEOUT(2002, "AI服务响应超时"),
    AI_CONTENT_FILTER(2003, "AI内容被安全过滤"),

    /* ========== 用户相关 3xxx ========== */
    USER_NOT_FOUND(3001, "用户不存在"),
    PASSWORD_ERROR(3002, "密码错误"),
    TOKEN_EXPIRED(3003, "Token已过期"),
    TOKEN_INVALID(3004, "Token无效"),
    USER_DISABLED(3005, "用户已被禁用"),

    /* ========== 旅游业务 4xxx ========== */
    DESTINATION_NOT_SUPPORTED(4001, "暂不支持该目的地"),
    BUDGET_INSUFFICIENT(4002, "预算不足"),
    ITINERARY_GENERATE_FAILED(4003, "行程生成失败");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
