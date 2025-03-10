package com.czj.rbac.service;

import com.czj.rbac.model.dto.RoleDTO;
import com.czj.rbac.model.query.RoleQuery;
import com.czj.rbac.model.vo.RoleVO;
import com.czj.rbac.common.PageResult;
import java.util.List;

public interface SysRoleService {
    /**
     * 根据ID查询角色
     */
    RoleVO findById(Long id);
    
    /**
     * 分页查询角色
     */
    PageResult<RoleVO> findPage(RoleQuery query);
    
    /**
     * 内存分页查询角色
     */
    PageResult<RoleVO> findPageInMemory(RoleQuery query);
    
    /**
     * 查询角色列表
     */
    List<RoleVO> findList(Integer status);
    
    /**
     * 查询角色详情(包含权限信息)
     */
    RoleVO findRoleDetail(Long roleId);
    
    /**
     * 查询用户的所有角色
     */
    List<RoleVO> findRolesByUserId(Long userId);
    
    /**
     * 创建角色
     */
    void add(RoleDTO roleDTO);
    
    /**
     * 更新角色
     */
    void update(RoleDTO roleDTO);
    
    /**
     * 删除角色
     */
    void delete(Long id);
    
    /**
     * 批量删除角色
     */
    void deleteBatch(List<Long> ids);
    
    /**
     * 更新角色状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 分配角色权限
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);
    
    /**
     * 移除角色权限
     */
    void removePermissions(Long roleId, List<Long> permissionIds);
    
    /**
     * 检查角色编码是否存在
     */
    boolean checkRoleCodeExists(String roleCode);
    
    /**
     * 检查角色是否拥有指定权限
     */
    boolean hasPermission(Long roleId, Long permissionId);
    
    /**
     * 获取角色的所有权限ID
     */
    List<Long> findPermissionIdsByRoleId(Long roleId);
    
    /**
     * 清理角色缓存
     *
     * @param roleId 角色ID
     */
    void clearRoleCache(Long roleId);
} 