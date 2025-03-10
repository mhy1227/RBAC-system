package com.czj.rbac.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import com.czj.rbac.util.JwtUtil;
import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final List<String> WHITE_LIST = Arrays.asList(
        "/auth/login",
        "/auth/logout",
        "/auth/refresh",
        "/error",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/upload/avatar/**",
        "/*.html",
        "/",
        "/index.html",
        "/static/**",
        "/login.html",
        "/css/**",
        "/js/**",
        "/img/**",
        "/favicon.ico"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.debug("请求URI: {}", requestURI);
        
        // 1. 白名单放行
        if (isWhiteListUrl(requestURI)) {
            return true;
        }

        // 2. 校验token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            log.warn("Token为空，URI: {}", requestURI);
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }

        // 3. 验证token有效性
        try {
            if (!JwtUtil.validateToken(token)) {
                log.warn("Token无效，URI: {}", requestURI);
                throw new BusinessException(ResponseCode.UNAUTHORIZED);
            }

            // 4. 设置用户信息到请求上下文
            Claims claims = JwtUtil.parseJwt(token);
            request.setAttribute("userId", claims.get("userId"));
            request.setAttribute("username", claims.get("username"));
            
            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }

    }

    private boolean isWhiteListUrl(String requestURI) {
        return WHITE_LIST.stream().anyMatch(pattern -> 
            pattern.endsWith("/**") 
                ? requestURI.startsWith(pattern.substring(0, pattern.length() - 3))
                : pattern.equals(requestURI)
        );
    }
} 