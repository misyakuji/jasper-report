package com.misyakuji.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.misyakuji.service.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        logger.warn("未认证请求拦截：请求路径={}，异常信息={}",
                request.getRequestURI(),
                authException.getMessage(),
                authException);
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<?> result = ApiResponse.fail(401, "未登录或登录过期，请重新登录");
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
