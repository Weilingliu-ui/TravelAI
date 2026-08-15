package com.travelai.travelai.common.exception;

import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.common.response.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一拦截所有异常，转换为 Result 格式返回。
 * 异常处理优先级：具体异常 > 父类异常 > Exception
 *
 * @author TravelAI Team
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ==================== 业务异常 ==================== */

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("[BusinessException] path={}, code={}, message={}",
                request.getRequestURI(), e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /* ==================== 参数校验异常 ==================== */

    /** @Valid 校验失败 (JSON请求体) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("[Validation] path={}, errors={}", request.getRequestURI(), errors);
        return Result.error(ResultCode.PARAM_ERROR, errors);
    }

    /** 表单绑定校验失败 */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        String errors = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("[BindException] path={}, errors={}", request.getRequestURI(), errors);
        return Result.error(ResultCode.PARAM_ERROR, errors);
    }

    /** 方法参数校验失败 (@Validated on controller) */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        String errors = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("[ConstraintViolation] path={}, errors={}", request.getRequestURI(), errors);
        return Result.error(ResultCode.PARAM_ERROR, errors);
    }

    /** 缺少请求参数 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMissingParam(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("[MissingParam] path={}, param={}", request.getRequestURI(), e.getParameterName());
        return Result.error(ResultCode.PARAM_MISSING, "缺少必要参数: " + e.getParameterName());
    }

    /** 参数类型不匹配 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("[TypeMismatch] path={}, param={}, requiredType={}",
                request.getRequestURI(), e.getName(), e.getRequiredType());
        return Result.error(ResultCode.PARAM_ERROR,
                String.format("参数 '%s' 类型错误，期望: %s", e.getName(),
                        e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知"));
    }

    /* ==================== Spring Security 异常 ==================== */

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<?> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        log.warn("[AccessDenied] path={}, message={}", request.getRequestURI(), e.getMessage());
        return Result.error(ResultCode.FORBIDDEN);
    }

    /* ==================== HTTP 层面异常 ==================== */

    /** 请求方法不支持 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("[MethodNotSupported] path={}, method={}", request.getRequestURI(), e.getMethod());
        return Result.error(ResultCode.METHOD_NOT_ALLOWED,
                "不支持 " + e.getMethod() + " 请求，支持: " + String.join(", ", e.getSupportedMethods()));
    }

    /** Content-Type 不支持 */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Result<?> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        log.warn("[MediaTypeNotSupported] path={}, contentType={}", request.getRequestURI(), e.getContentType());
        return Result.error(ResultCode.BAD_REQUEST, "不支持的Content-Type: " + e.getContentType());
    }

    /** 请求体解析失败 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("[MessageNotReadable] path={}", request.getRequestURI());
        return Result.error(ResultCode.BAD_REQUEST, "请求体格式错误或为空");
    }

    /** 文件上传超过限制 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public Result<?> handleMaxUploadSize(MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.warn("[MaxUploadSize] path={}, maxSize={}", request.getRequestURI(), e.getMaxUploadSize());
        return Result.error(ResultCode.BAD_REQUEST, "上传文件大小超过限制");
    }

    /** 404 */
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNotFound(Exception e, HttpServletRequest request) {
        log.warn("[NotFound] path={}", request.getRequestURI());
        return Result.error(ResultCode.NOT_FOUND);
    }

    /* ==================== 兜底异常 ==================== */

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("[UnhandledException] path={}, type={}, message={}",
                request.getRequestURI(), e.getClass().getName(), e.getMessage(), e);
        return Result.error(ResultCode.INTERNAL_ERROR);
    }
}
