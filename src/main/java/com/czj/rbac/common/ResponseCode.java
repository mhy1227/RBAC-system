package com.czj.rbac.common;

import lombok.Getter;

@Getter
public enum ResponseCode {
    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    NEED_VERIFY(402, "需要二次验证"),

    // 服务端错误 5xx
    ERROR(500, "系统错误，请联系管理员"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),
    SYSTEM_ERROR(500, "系统错误"),
    
    // 用户相关错误 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "账号已被禁用"),
    USER_EXISTS(1003, "用户名已存在"),
    USER_LOCKED(1004, "账号已被锁定"),
    PASSWORD_ERROR(1005, "密码错误"),
    GET_LOGIN_USER_ID_ERROR(1006, "获取当前登录用户ID失败"),
    GET_LOGIN_USERNAME_ERROR(1007, "获取当前登录用户名失败"),
    
    // 角色相关错误 2xxx
    ROLE_NOT_FOUND(2001, "角色不存在"),
    ROLE_CODE_EXISTS(2002, "角色编码已存在"),
    ROLE_IN_USE(2003, "角色正在使用中"),
    
    // 权限相关错误 3xxx
    PERMISSION_NOT_FOUND(3001, "权限不存在"),
    PERMISSION_CODE_EXISTS(3002, "权限编码已存在"),
    PERMISSION_IN_USE(3003, "权限正在使用中"),
    PARENT_PERMISSION_NOT_FOUND(3004, "父级权限不存在"),
    PERMISSION_HAS_CHILDREN(3005, "该权限存在子权限，无法删除");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
} 