package com.czj.rbac.service;

import java.util.List;

/**
 * 数据权限服务接口
 */
public interface DataPermissionService {
    
    /**
     * 检查权限
     * @param resource 资源
     * @param action 操作
     * @return 是否有权限
     */
    boolean checkPermission(String resource, String action);
    
    /**
     * 检查用户数据权限
     * @param userId 目标用户ID
     * @return 是否有权限
     */
    boolean checkUserDataPermission(Long userId);
    
    /**
     * 获取数据范围
     * @param userId 用户ID
     * @return 数据范围内的ID列表
     */
    List<Long> getDataScope(Long userId);
    
    /**
     * 获取用户数据范围
     * @param userId 用户ID
     * @return 用户数据范围内的ID列表
     */
    List<Long> getUserDataScope(Long userId);
    
    /**
     * 检查数据权限
     * @param dataId 数据ID
     * @param dataType 数据类型
     * @return 是否有权限
     */
    boolean hasDataPermission(Long dataId, String dataType);
    
    /**
     * 预热权限缓存
     */
    void warmUpPermissionCache();
} 