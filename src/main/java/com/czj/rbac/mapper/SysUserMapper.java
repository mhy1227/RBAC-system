package com.czj.rbac.mapper;

import com.czj.rbac.model.SysUser;
import com.czj.rbac.model.query.UserQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;
import com.czj.rbac.model.vo.PermissionVO;
@Mapper
public interface SysUserMapper {
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser findByUsername(String username);
    
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    SysUser findById(Long id);
    
    /**
     * 分页查询用户列表
     * @param query 查询条件
     * @return 用户列表
     */
    List<SysUser> findPage(@Param("query") UserQuery query);
    
    /**
     * 查询用户列表
     *
     * @param status 状态(可选)
     * @return 用户列表
     */
    List<SysUser> findList(@Param("status") Integer status);
    
    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(SysUser user);
    
    /**
     * 更新用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int update(SysUser user);
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 影响行数
     */
    int deleteBatchByIds(@Param("ids") List<Long> ids);
    
    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 影响行数
     */
    int checkUsername(@Param("username") String username);
    
    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 批量插入用户角色关系
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 影响行数
     */
    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
    
    /**
     * 删除用户角色关系
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserRoles(@Param("userId") Long userId);
    
    /**
     * 查询总记录数
     * @param query 查询条件
     * @return 总记录数
     */
    int count(@Param("query") UserQuery query);

    int count();

    List<SysUser> selectPage(@Param("query") UserQuery query);

    void delete(Long id);

    /**
     * 更新登录失败信息
     *
     * @param id 用户ID
     * @param loginFailCount 登录失败次数
     * @param lockTime 锁定时间
     * @return 影响的行数
     */
    int updateLoginFail(@Param("id") Long id, @Param("loginFailCount") Integer loginFailCount, @Param("lockTime") LocalDateTime lockTime);

    /**
     * 更新最后登录时间
     *
     * @param id 用户ID
     * @param lastLoginTime 最后登录时间
     * @return 影响的行数
     */
    int updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") LocalDateTime lastLoginTime);

    /**
     * 重置登录失败次数
     *
     * @param id 用户ID
     * @return 影响的行数
     */
    int resetLoginFail(@Param("id") Long id);

    /**
     * 根据角色ID查询用户ID列表
     */
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询部门ID
     *
     * @param userId 用户ID
     * @return 部门ID
     */
    Long findDeptIdByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询权限列表（树形结构）
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<PermissionVO> findUserPermissions(@Param("userId") Long userId);

    /**
     * 更新用户密保状态
     *
     * @param userId 用户ID
     * @param securityStatus 密保状态(0-未设置 1-已设置)
     * @return 影响行数
     */
    int updateSecurityStatus(@Param("userId") Long userId, @Param("securityStatus") Integer securityStatus);
} 