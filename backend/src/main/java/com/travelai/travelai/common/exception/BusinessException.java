package com.travelai.travelai.common.exception;

import com.travelai.travelai.common.response.ResultCode;
import lombok.Getter;

/**
 * 业务异常基类
 * <p>
 * 所有业务层异常应继承此类或使用此类抛出，
 * GlobalExceptionHandler 统一拦截并转换为 Result 返回。
 *
 * @author TravelAI Team
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private final int code;

    /** 错误消息 */
    private final String message;

    /** 额外数据（可选，用于携带上下文信息） */
    private final transient Object data;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = null;
    }

    public BusinessException(ResultCode resultCode, String customMessage) {
        super(customMessage);
        this.code = resultCode.getCode();
        this.message = customMessage;
        this.data = null;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public BusinessException(ResultCode resultCode, String message, Object data) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
        this.data = data;
    }

    /**
     * 快速抛出 - 参数异常
     */
    public static void throwParamError(String message) {
        throw new BusinessException(ResultCode.PARAM_ERROR, message);
    }

    /**
     * 快速抛出 - 数据不存在
     */
    public static void throwNotFound(String message) {
        throw new BusinessException(ResultCode.DATA_NOT_FOUND, message);
    }

    /**
     * 快速抛出 - AI服务异常
     */
    public static void throwAiError(String message) {
        throw new BusinessException(ResultCode.AI_SERVICE_ERROR, message);
    }

    /**
     * 快速抛出 - 操作失败
     */
    public static void throwOperationFailed(String message) {
        throw new BusinessException(ResultCode.OPERATION_FAILED, message);
    }
}
