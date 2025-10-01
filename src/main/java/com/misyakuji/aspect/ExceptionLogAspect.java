package com.misyakuji.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class ExceptionLogAspect {
    // 切点：拦截全局异常处理器中的所有@ExceptionHandler方法
    @Pointcut("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public void exceptionHandlerPointcut() {}

    @Around("exceptionHandlerPointcut()")
    public Object logException(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求上下文（如URL、IP）
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestUrl = attributes.getRequest().getRequestURI();

        // 记录异常处理前的信息
        log.info("开始处理异常，请求URL：{}", requestUrl);

        // 执行原异常处理方法
        Object result = joinPoint.proceed();

        // 记录异常处理结果
        log.info("异常处理完成，响应：{}", result);
        return result;
    }
}