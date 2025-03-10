package com.czj.rbac.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class SpringDocConfig {
    
  /*
   * 配置Swagger文档
   * 怎么使用
   * 1. 在pom.xml中添加依赖
   * 2. 在SpringBootApplication中添加@EnableOpenApi注解
   * 3. 在SpringDocConfig中配置OpenAPI
   */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RBAC系统API文档")
                .version("1.0.0")
                .description("RBAC权限管理系统的API文档"));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户管理")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi roleApi() {
        return GroupedOpenApi.builder()
                .group("角色管理")
                .pathsToMatch("/role/**")
                .build();
    }

    @Bean
    public GroupedOpenApi permissionApi() {
        return GroupedOpenApi.builder()
                .group("权限管理")
                .pathsToMatch("/permission/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("认证管理")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi logApi() {
        return GroupedOpenApi.builder()
                .group("日志管理")
                .pathsToMatch("/log/**", "/login-info/**")
                .build();
    }
} 