package com.misyakuji.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 业务异常类
 * 用于表示业务逻辑中的错误情况
 */
@Getter
public class BusinessException extends RuntimeException {
    
    // HTTP状态码
    private final HttpStatus status;
    
    // 错误代码
    private final String errorCode;
    
    /**
     * 构造函数
     * @param message 错误消息
     * @param status HTTP状态码
     * @param errorCode 错误代码
     */
    public BusinessException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数（默认错误代码）
     * @param message 错误消息
     * @param status HTTP状态码
     */
    public BusinessException(String message, HttpStatus status) {
        this(message, status, status.name());
    }
    
    /**
     * 构造函数（默认400状态码）
     * @param message 错误消息
     */
    public BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }
}