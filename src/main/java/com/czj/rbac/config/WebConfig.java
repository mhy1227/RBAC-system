package com.czj.rbac.config;

import com.czj.rbac.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")        // 拦截所有请求
                .excludePathPatterns(
                    "/error",
                    "/*.html",
                    "/favicon.ico",
                    "/static/**",
                    "/auth/login",    // 添加登录接口
                    "/auth/logout",   // 添加登出接口
                    "/auth/info"      // 添加用户信息接口
                )
                .order(1);                     // 优先级，数字越小优先级越高
    }
} 