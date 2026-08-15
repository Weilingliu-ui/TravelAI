package com.travelai.travelai.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一API返回结果封装
 * <p>
 * 所有Controller返回值统一使用此类包装，
 * 支持泛型数据承载和链式调用。
 *
 * @param <T> 业务数据类型
 * @author TravelAI Team
 */
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private int code;

    /** 返回消息 */
    private String message;

    /** 业务数据 */
    private T data;

    /** 时间戳 */
    private long timestamp;

    /** 链路追踪ID */
    private String traceId;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 静态工厂方法 ====================

    /** 成功 - 无数据 */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /** 成功 - 带数据 */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /** 成功 - 自定义消息 + 数据 */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    // ==================== 失败工厂方法 ====================

    /** 失败 - 使用ResultCode */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /** 失败 - 使用ResultCode + 自定义消息 */
    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    /** 失败 - 自定义code + 消息 */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    // ==================== 链式方法 ====================

    public Result<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public Result<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    // ==================== 状态判断 ====================

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }

    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }
}
