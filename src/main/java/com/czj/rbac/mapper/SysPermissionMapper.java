package com.czj.rbac.mapper;

import com.czj.rbac.model.SysPermission;
import com.czj.rbac.model.vo.PermissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysPermissionMapper {
    // 原有方法注释开始
    /*
    SysPermission findByPermissionCode(@Param("permissionCode") String permissionCode);
    SysPermission findById(@Param("id") Long id);
    List<SysPermission> findPage(@Param("page") Integer page, 
                                @Param("size") Integer size, 
                                @Param("status") Integer status,
                                @Param("type") String type);
    List<SysPermission> findList(@Param("status") Integer status, @Param("type") String type);
    List<SysPermission> findByParentId(@Param("parentId") Long parentId);
    List<SysPermission> findPermissionsByRoleId(@Param("roleId") Long roleId);
    List<SysPermission> findPermissionsByUserId(@Param("userId") Long userId);
    List<SysPermission> findPermissionTree(@Param("type") String type);
    int insert(SysPermission permission);
    int update(SysPermission permission);
    int deleteById(@Param("id") Long id);
    int deleteBatchByIds(@Param("ids") List<Long> ids);
    int checkPermissionCode(@Param("permissionCode") String permissionCode);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int countByParentId(@Param("parentId") Long parentId);
    int count(@Param("status") Integer status, @Param("type") String type);
    int countByRolePermissions(@Param("permissionId") Long permissionId);
    List<PermissionVO> findByUserId(@Param("userId") Long userId);
    int countByIds(@Param("ids") List<Long> ids);
    List<PermissionVO> findByIds(@Param("ids") List<Long> ids);
    */
    // 原有方法注释结束

    /**
     * 根据权限编码查询权限
     *
     * @param permissionCode 权限编码
     * @return 权限信息
     */
    SysPermission findByPermissionCode(@Param("permissionCode") String permissionCode);
    
    /**
     * 根据ID查询权限
     *
     * @param id 权限ID
     * @return 权限信息
     */
    SysPermission findById(@Param("id") Long id);
    
    /**
     * 查询权限列表（带条件）
     *
     * @param status 状态(可选)
     * @param type 类型(可选)
     * @return 权限列表
     */
    List<SysPermission> findPage(@Param("status") Integer status,
                                @Param("type") String type);
    
    /**
     * 查询权限列表
     *
     * @param status 状态(可选)
     * @param type 类型(可选)
     * @return 权限列表
     */
    List<SysPermission> findList(@Param("status") Integer status, @Param("type") String type);
    
    /**
     * 根据父ID查询权限列表
     *
     * @param pid 父级ID
     * @return 权限列表
     */
    List<SysPermission> findByParentId(@Param("pid") Long pid);
    
    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<SysPermission> findPermissionsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<SysPermission> findPermissionsByUserId(@Param("userId") Long userId);
    
    /**
     * 获取权限树
     *
     * @param type 类型(可选)
     * @return 权限树
     */
    List<SysPermission> findPermissionTree(@Param("type") String type);
    
    /**
     * 创建权限
     *
     * @param permission 权限信息
     * @return 影响行数
     */
    int insert(SysPermission permission);
    
    /**
     * 更新权限
     *
     * @param permission 权限信息
     * @return 影响行数
     */
    int update(SysPermission permission);
    
    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量删除权限
     *
     * @param ids 权限ID列表
     * @return 影响行数
     */
    int deleteBatchByIds(@Param("ids") List<Long> ids);
    
    /**
     * 检查权限编码是否存在
     *
     * @param permissionCode 权限编码
     * @return 1表示存在，0表示不存在
     */
    int checkPermissionCode(@Param("permissionCode") String permissionCode);
    
    /**
     * 更新权限状态
     *
     * @param id 权限ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 获取子权限数量
     *
     * @param pid 父级ID
     * @return 子权限数量
     */
    int countByParentId(@Param("pid") Long pid);
    
    /**
     * 统计权限总数
     *
     * @param status 状态(可选)
     * @param type 类型(可选)
     * @return 权限总数
     */
    int count(@Param("status") Integer status, @Param("type") String type);
    
    /**
     * 统计权限被角色引用的次数
     *
     * @param permissionId 权限ID
     * @return 引用次数
     */
    int countByRolePermissions(@Param("permissionId") Long permissionId);

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<PermissionVO> findByUserId(@Param("userId") Long userId);

    /**
     * 根据ID列表统计权限数量
     *
     * @param ids 权限ID列表
     * @return 权限数量
     */
    int countByIds(@Param("ids") List<Long> ids);

    /**
     * 根据ID列表查询权限
     *
     * @param ids 权限ID列表
     * @return 权限列表
     */
    List<PermissionVO> findByIds(@Param("ids") List<Long> ids);
} 