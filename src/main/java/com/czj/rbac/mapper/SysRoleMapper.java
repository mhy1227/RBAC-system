package com.czj.rbac.mapper;

import com.czj.rbac.model.SysRole;
import com.czj.rbac.model.vo.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysRoleMapper {
    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色信息
     */
    SysRole findByRoleCode(@Param("roleCode") String roleCode);
    
    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色信息
     */
    SysRole findById(@Param("id") Long id);
    
    /**
     * 查询角色详情(包含权限信息)
     */
    RoleVO findRoleDetail(@Param("roleId") Long roleId);
    List<SysRole> findPageWithFilter(@Param("offset") Integer offset,
                                   @Param("limit") Integer limit,
                                   @Param("status") Integer status,
                                   @Param("roleName") String roleName,
                                   @Param("roleCode") String roleCode);
    
    /**
     * 统计角色数量（带过滤条件）
     */
    int countWithFilter(@Param("status") Integer status,
                       @Param("roleName") String roleName,
                       @Param("roleCode") String roleCode);
    
    /**
     * 查询角色列表
     *
     * @param status 状态(可选)
     * @return 角色列表
     */
    List<SysRole> findList(@Param("status") Integer status);
    
    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> findRolesByUserId(@Param("userId") Long userId);
    
    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 影响行数
     */
    int insert(SysRole role);
    
    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 影响行数
     */
    int update(SysRole role);
    
    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量删除角色
     */
    int deleteBatchByIds(@Param("ids") List<Long> ids);
    
    /**
     * 检查角色编码是否存在
     */
    int checkRoleCode(@Param("roleCode") String roleCode);
    
    /**
     * 更新角色状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 批量插入角色权限关系
     *
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 影响行数
     */
    int insertRolePermissions(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
    
    /**
     * 删除角色权限关系
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteRolePermissions(@Param("roleId") Long roleId);
    
    /**
     * 统计角色总数
     */
    int count(@Param("status") Integer status);
    
    /**
     * 根据ID列表查询角色
     *
     * @param ids 角色ID列表
     * @return 角色列表
     */
    List<SysRole> findByIds(@Param("ids") List<Long> ids);
    
    /**
     * 检查角色是否拥有指定权限
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 大于0表示拥有权限
     */
    int hasPermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
    
    /**
     * 获取角色的所有权限ID
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);
} 