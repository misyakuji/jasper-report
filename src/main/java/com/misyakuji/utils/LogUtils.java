package com.misyakuji.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 统一日志工具类
 * 提供标准化的日志记录方法
 */
@Slf4j
public class LogUtils {

    /**
     * 记录请求开始日志
     * @param methodName 方法名
     * @param args 方法参数
     */
    public static void logRequestStart(String methodName, Object... args) {
        HttpServletRequest request = getCurrentRequest();
        String clientInfo = getClientInfo(request);
        
        if (args.length > 0) {
            log.info("请求开始 - 方法: {} | 客户端: {} | 参数: {}", methodName, clientInfo, formatArgs(args));
        } else {
            log.info("请求开始 - 方法: {} | 客户端: {}", methodName, clientInfo);
        }
    }

    /**
     * 记录请求成功日志
     * @param methodName 方法名
     * @param result 返回结果
     * @param executionTime 执行时间（毫秒）
     */
    public static void logRequestSuccess(String methodName, Object result, long executionTime) {
        log.info("请求成功 - 方法: {} | 耗时: {}ms | 结果: {}", 
                methodName, executionTime, formatResult(result));
    }

    /**
     * 记录请求异常日志
     * @param methodName 方法名
     * @param exception 异常信息
     * @param executionTime 执行时间（毫秒）
     */
    public static void logRequestError(String methodName, Exception exception, long executionTime) {
        log.error("请求异常 - 方法: {} | 耗时: {}ms | 异常: {}",
                methodName, executionTime, exception.getMessage());
    }

    /**
     * 记录业务操作日志
     * @param operation 操作描述
     * @param userId 用户ID（可选）
     * @param details 操作详情
     */
    public static void logBusinessOperation(String operation, Long userId, String details) {
        if (userId != null) {
            log.info("业务操作 - 用户: {} | 操作: {} | 详情: {}", userId, operation, details);
        } else {
            log.info("业务操作 - 操作: {} | 详情: {}", operation, details);
        }
    }

    /**
     * 记录数据库操作日志
     * @param operation 操作类型（INSERT/UPDATE/DELETE/SELECT）
     * @param table 表名
     * @param condition 操作条件
     * @param affectedRows 影响行数
     */
    public static void logDatabaseOperation(String operation, String table, String condition, int affectedRows) {
        log.info("数据库操作 - 类型: {} | 表: {} | 条件: {} | 影响行数: {}", 
                operation, table, condition, affectedRows);
    }

    /**
     * 记录安全相关日志
     * @param event 安全事件
     * @param username 用户名
     * @param details 详情
     */
    public static void logSecurityEvent(String event, String username, String details) {
        log.warn("安全事件 - 事件: {} | 用户: {} | 详情: {}", event, username, details);
    }

    /**
     * 获取当前请求对象
     */
    private static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取客户端信息
     */
    private static String getClientInfo(HttpServletRequest request) {
        if (request == null) {
            return "未知";
        }
        
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("BizUser-Agent");
        String method = request.getMethod();
        String uri = request.getRequestURI();
        
        return String.format("%s %s %s (IP: %s, UA: %s)", 
                method, uri, request.getProtocol(), clientIp, userAgent);
    }

    /**
     * 获取客户端真实IP地址
     */
    private static String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR"};
        
        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 格式化方法参数
     */
    private static String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "无";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(formatObject(args[i]));
        }
        return sb.toString();
    }

    /**
     * 格式化返回结果
     */
    private static String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        
        String resultStr = result.toString();
        if (resultStr.length() > 200) {
            return resultStr.substring(0, 200) + "...";
        }
        return resultStr;
    }

    /**
     * 格式化对象
     */
    private static String formatObject(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        if (obj instanceof HttpServletRequest req) {
            return "HttpServletRequest[" + req.getMethod() + " " + req.getRequestURI() + "]";
        }
        
        String objStr = obj.toString();
        if (objStr.length() > 100) {
            return objStr.substring(0, 100) + "...";
        }
        return objStr;
    }

    /**
     * 获取异常堆栈信息
     */
    private static String getStackTrace(Exception exception) {
        StringBuilder sb = new StringBuilder();
        sb.append(exception.toString()).append("\n");
        
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append("\t").append(element.toString()).append("\n");
            if (sb.length() > 1000) { // 限制堆栈信息长度
                sb.append("...\n");
                break;
            }
        }
        
        return sb.toString();
    }

    /**
     * 获取当前认证用户信息
     */
    public static String getCurrentUser() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                Authentication auth = (Authentication) attributes.getRequest().getUserPrincipal();
                if (auth != null) {
                    return auth.getName();
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "anonymous";
    }
}