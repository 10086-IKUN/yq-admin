package cn.yanque.common.exception;

import cn.yanque.common.api.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理项目中的异常，返回标准化的API响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * @param ex 业务异常
     * @return 标准化错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        log.error("业务异常: code={}, message={}", ex.getCode(), ex.getMessage(), ex);
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理请求参数校验异常（@Valid @RequestBody）
     * @param ex 参数校验异常
     * @return 标准化错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("请求参数校验异常: {}", message, ex);
        return ApiResponse.fail(400, message);
    }

    /**
     * 处理参数绑定异常
     * @param ex 参数绑定异常
     * @return 标准化错误响应
     */
    @ExceptionHandler(BindException.class)
    public ApiResponse<Void> handleBindException(BindException ex) {
        String message = buildValidationMessage(ex.getBindingResult().getFieldErrors(), "请求参数格式不正确");
        log.error("参数绑定异常: {}", message, ex);
        return ApiResponse.fail(400, message);
    }

    /**
     * 处理约束校验异常
     * @param ex 约束校验异常
     * @return 标准化错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Void> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("约束校验异常: {}", ex.getMessage(), ex);
        return ApiResponse.fail(400, "请求参数校验失败");
    }

    /**
     * 处理未捕获的系统异常（兜底）
     * @param ex 系统异常
     * @return 标准化错误响应
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {
        log.error("系统异常", ex);
        return ApiResponse.fail(500, "系统开小差了，请稍后重试");
    }

    /**
     * 构建参数校验错误消息
     * @param fieldErrors 字段错误列表
     * @param defaultMessage 默认错误消息
     * @return 拼接后的错误消息
     */
    private String buildValidationMessage(List<FieldError> fieldErrors, String defaultMessage) {
        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .collect(Collectors.joining("; "));
        return message.isBlank() ? defaultMessage : message;
    }
}
