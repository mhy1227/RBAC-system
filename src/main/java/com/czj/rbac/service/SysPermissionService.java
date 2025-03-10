package com.czj.rbac.service;

import com.czj.rbac.model.dto.PermissionDTO;
import com.czj.rbac.model.query.PermissionQuery;
import com.czj.rbac.model.vo.PermissionVO;
import com.czj.rbac.common.PageResult;
import java.util.List;

public interface SysPermissionService {
    /**
     * 根据ID查询权限
     */
    PermissionVO findById(Long id);
    
    /**
     * 分页查询权限
     */
    PageResult<PermissionVO> findPage(PermissionQuery query);
    
    /**
     * 内存分页查询权限
     */
    PageResult<PermissionVO> findPageInMemory(PermissionQuery query);
    
    /**
     * 查询权限列表
     */
    List<PermissionVO> findList(Integer status, String type);
    
    /**
     * 根据父ID查询权限列表
     */
    List<PermissionVO> findByParentId(Long parentId);
    
    /**
     * 根据角色ID查询权限列表
     */
    List<PermissionVO> findPermissionsByRoleId(Long roleId);
    
    /**
     * 根据用户ID查询权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> findPermissionsByUserId(Long userId);
    
    /**
     * 获取权限树
     */
    List<PermissionVO> findPermissionTree(String type);
    
    /**
     * 创建权限
     */
    void add(PermissionDTO permissionDTO);
    
    /**
     * 更新权限
     */
    void update(PermissionDTO permissionDTO);
    
    /**
     * 删除权限
     */
    void delete(Long id);
    
    /**
     * 更新权限状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 批量删除权限
     *
     * @param ids 权限ID列表
     */
    void batchDelete(List<Long> ids);

    /**
     * 清理权限列表缓存
     */
    void clearPermissionListCache();
    
    /**
     * 清理权限树缓存
     */
    void clearPermissionTreeCache();
    
    /**
     * 清理父级权限缓存
     */
    void clearParentPermissionCache();
} 