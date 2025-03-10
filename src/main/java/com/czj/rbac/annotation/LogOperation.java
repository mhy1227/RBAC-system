package com.czj.rbac.annotation;

import com.czj.rbac.model.enums.LogLevel;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {
    /**
     * 模块名称
     */
    String module() default "";
    
    /**
     * 操作类型
     */
    String operation() default "";
    
    /**
     * 日志内容
     */
    String content() default "";
    
    /**
     * 日志级别
     */
    LogLevel level() default LogLevel.INFO;
} 