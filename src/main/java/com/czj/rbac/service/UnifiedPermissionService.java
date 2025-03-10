package com.czj.rbac.service;

import com.czj.rbac.mapper.SysRoleMapper;
// import com.czj.rbac.mapper.SysUserMapper;
import com.czj.rbac.mapper.SysPermissionMapper;
import com.czj.rbac.model.SysRole;
// import com.czj.rbac.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import com.czj.rbac.model.SysPermission;
@Slf4j
@Service
public class UnifiedPermissionService {

    @Autowired
    private SysRoleMapper roleMapper;

    // @Autowired
    // private SysUserMapper userMapper;

    @Autowired
    private SysPermissionMapper permissionMapper;

    /**
     * 检查功能权限
     */
    @Cacheable(value = "permission", key = "'func:' + #userId + ':' + #permission")
    public boolean checkFunctionPermission(Long userId, String permission) {
        List<SysPermission> permissions = permissionMapper.findPermissionsByUserId(userId);
        return permissions != null && permissions.stream().anyMatch(p -> p.getPermissionCode().equals(permission));
    }

    /**
     * 检查数据权限
     */
    @Cacheable(value = "permission", key = "'data:' + #userId + ':' + #targetId")
    public boolean checkDataPermission(Long userId, Long targetId) {
        // 如果是查询自己的数据，直接返回true
        if (userId.equals(targetId)) {
            return true;
        }

        // 获取用户角色
        List<SysRole> userRoles = roleMapper.findRolesByUserId(userId);
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }

        // 如果是超级管理员，直接返回true
        if (userRoles.stream().anyMatch(role -> "ROLE_SUPER_ADMIN".equals(role.getRoleCode()))) {
            return true;
        }

        // 获取目标用户角色
        List<SysRole> targetRoles = roleMapper.findRolesByUserId(targetId);
        if (targetRoles == null || targetRoles.isEmpty()) {
            return true; // 目标用户没有角色，视为普通用户
        }

        // 比较角色等级
        int userMaxLevel = getMaxRoleLevel(userRoles);
        int targetMaxLevel = getMaxRoleLevel(targetRoles);

        return userMaxLevel > targetMaxLevel;
    }

    /**
     * 获取最高角色等级
     */
    private int getMaxRoleLevel(List<SysRole> roles) {
        return roles.stream()
            .map(this::getRoleLevel)
            .max(Integer::compareTo)
            .orElse(0);
    }

    /**
     * 获取角色等级
     */
    private int getRoleLevel(SysRole role) {
        switch (role.getRoleCode()) {
            case "ROLE_SUPER_ADMIN":
                return 100;
            case "ROLE_ADMIN":
                return 80;
            case "ROLE_MANAGER":
                return 60;
            case "ROLE_USER":
                return 10;
            default:
                return 0;
        }
    }
} 