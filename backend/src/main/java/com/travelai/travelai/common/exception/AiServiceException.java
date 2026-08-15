package com.travelai.travelai.common.exception;

/**
 * AI服务异常
 * <p>
 * 专门用于AI服务调用过程中出现的异常（超时、网络错误、内容过滤等）
 *
 * @author TravelAI Team
 */
public class AiServiceException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * 重试次数
     */
    private final int retryCount;

    /**
     * 原始错误信息
     */
    private final String rawError;

    public AiServiceException(String message) {
        super(com.travelai.travelai.common.response.ResultCode.AI_SERVICE_ERROR, message);
        this.retryCount = 0;
        this.rawError = message;
    }

    public AiServiceException(String message, Throwable cause) {
        super(com.travelai.travelai.common.response.ResultCode.AI_SERVICE_ERROR, message);
        this.retryCount = 0;
        this.rawError = cause != null ? cause.getMessage() : message;
    }

    public AiServiceException(String message, int retryCount, String rawError) {
        super(com.travelai.travelai.common.response.ResultCode.AI_SERVICE_ERROR, message);
        this.retryCount = retryCount;
        this.rawError = rawError;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getRawError() {
        return rawError;
    }
}
