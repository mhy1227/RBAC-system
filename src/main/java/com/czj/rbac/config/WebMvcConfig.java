package com.czj.rbac.config;

import com.czj.rbac.interceptor.AuthInterceptor;
import com.czj.rbac.interceptor.UserContextInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
        "/auth/login",
        "/auth/logout",
        "/error",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/upload/avatar/**",
        "/*.html",
        "/static/**",
        "/login.html",
        "/css/**",
        "/js/**",
        "/img/**"
    );
    
    @Value("${rbac.upload.avatar.path:/upload/avatar/}")
    private String avatarPath;
    
    @Autowired
    private AuthInterceptor authInterceptor;
    
    @Autowired
    private UserContextInterceptor userContextInterceptor;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 头像资源映射
        registry.addResourceHandler("/upload/avatar/**")
                .addResourceLocations("file:" + avatarPath);
                
        // 静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
                
        // HTML页面映射
        registry.addResourceHandler("/*.html")
                .addResourceLocations("classpath:/static/");
                
        // Swagger UI资源映射
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 认证拦截器最先执行
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATHS)
                .order(1);
        
        // 用户上下文拦截器在认证之后执行
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATHS)
                .order(2);
    }
} 