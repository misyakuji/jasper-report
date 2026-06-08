package com.misyakuji.exception;

import com.misyakuji.common.ApiResponse;
import com.misyakuji.utils.LogUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import org.springframework.security.core.Authentication;

@RestControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理 ResponseStatusException
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<?>> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        
        log.warn("响应状态异常 - 状态: {} | 消息: {} | 用户: {}", 
                status.value(), message, LogUtils.getCurrentUser());
        
        return ResponseEntity.status(status).body(ApiResponse.fail(status, message));
    }

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        log.error("业务异常 - 用户: {} | 错误代码: {} | 消息: {}", 
                LogUtils.getCurrentUser(), ex.getErrorCode(), ex.getMessage());
        
        return ResponseEntity.status(ex.getStatus())
                .body(ApiResponse.fail(ex.getStatus(), ex.getMessage()));
    }

    /**
     * 处理 Servlet 相关异常
     */
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ApiResponse<?>> handleServletException(ServletException ex) {
        String message = "Servlet 异常: " + ex.getMessage();
        
        log.error("Servlet异常 - 用户: {} | 消息: {}", LogUtils.getCurrentUser(), message);
        
        try {
            HttpStatus status = (HttpStatus) ex.getClass().getMethod("getStatusCode").invoke(ex);
            return ResponseEntity.status(status).body(ApiResponse.fail(status, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, message));
        }
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<?>> handleBindException(BindException ex) {
        String errorMsg = Optional.ofNullable(ex.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("参数校验失败");
        
        log.warn("参数绑定异常 - 用户: {} | 错误: {} | 字段: {}", 
                LogUtils.getCurrentUser(), errorMsg, 
                Optional.ofNullable(ex.getBindingResult().getFieldError())
                        .map(FieldError::getField)
                        .orElse("未知"));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, errorMsg));
    }

    /**
     * JSON 解析异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("JSON解析异常 - 用户: {} | 消息: {}", LogUtils.getCurrentUser(), ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, "请求体格式错误：" + ex.getMessage()));
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException ex) {
        String username = Optional.ofNullable(ex.getAuthenticationRequest())
                .map(Authentication::getName)
                .orElse("anonymous");
        LogUtils.logSecurityEvent("认证失败", username, ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(HttpStatus.UNAUTHORIZED, "认证失败：" + ex.getMessage()));
    }

    /**
     * 处理授权异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        LogUtils.logSecurityEvent("权限不足", LogUtils.getCurrentUser(), ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(HttpStatus.FORBIDDEN, "权限不足：" + ex.getMessage()));
    }

    /**
     * 处理 EntityNotFoundException (JPA)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("实体不存在 - 用户: {} | 消息: {}", LogUtils.getCurrentUser(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /**
     * 捕获其他未处理的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        log.error("运行时异常 - 用户: {} | 异常类型: {} | 消息: {}", 
                LogUtils.getCurrentUser(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, "系统运行时异常：" + ex.getMessage()));
    }

    /**
     * 捕获所有异常兜底
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        log.error("未处理异常 - 用户: {} | 异常类型: {} | 消息: {}", 
                LogUtils.getCurrentUser(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误：" + ex.getMessage()));
    }
}
