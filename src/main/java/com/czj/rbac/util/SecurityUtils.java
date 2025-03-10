package com.czj.rbac.util;

import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

/**
 * 安全工具类
 */
public class SecurityUtils {

    private static String adminPermission = "sys:admin";

    @Value("${rbac.permission.admin:sys:admin}")
    public void setAdminPermission(String permission) {
        SecurityUtils.adminPermission = permission;
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getLoginUserId() {
        Long userId = JwtUtil.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResponseCode.GET_LOGIN_USER_ID_ERROR);
        }
        return userId;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getLoginUsername() {
        String username = JwtUtil.getCurrentUsername();
        if (username == null) {
            throw new BusinessException(ResponseCode.GET_LOGIN_USERNAME_ERROR);
        }
        return username;
    }

    /**
     * 判断是否有管理员权限
     */
    public static boolean hasAdminPermission() {
        List<String> permissions = JwtUtil.getCurrentUserPermissions();
        return permissions != null && permissions.contains(adminPermission);
    }
} 