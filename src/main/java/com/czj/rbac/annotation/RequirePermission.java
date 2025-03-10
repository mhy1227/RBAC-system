package com.czj.rbac.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /**
     * 权限标识，多个权限用逗号分隔，如 "sys:user:query,sys:user:add"
     */
    String value() default "";

    /**
     * 是否需要所有权限，true表示需要所有权限，false表示只需要其中一个权限
     */
    boolean requireAll() default false;
} 