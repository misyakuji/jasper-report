package com.misyakuji.aspect;

import com.misyakuji.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 统一请求日志切面
 * 记录所有Controller方法的请求和响应日志
 */
@Aspect
@Component
@Slf4j
public class RequestLogAspect {

    /**
     * 切点：拦截所有Controller的public方法
     */
    @Around("execution(public * com.misyakuji.controller..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        
        long startTime = System.currentTimeMillis();
        
        // 记录请求开始日志
        LogUtils.logRequestStart(methodName, args);
        
        try {
            // 执行原方法
            Object result = joinPoint.proceed();
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录请求成功日志
            LogUtils.logRequestSuccess(methodName, result, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录请求异常日志
            LogUtils.logRequestError(methodName, e, executionTime);
            
            // 重新抛出异常
            throw e;
        } finally {
            // 清理ThreadLocal变量，防止内存泄漏
            RequestContextHolder.resetRequestAttributes();
        }
    }
}