package com.czj.rbac.service;

import com.czj.rbac.model.dto.UserDTO;
import com.czj.rbac.model.query.UserQuery;
import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.model.vo.PermissionVO;
import com.czj.rbac.common.PageResult;
import java.util.List;

public interface SysUserService {
    /**
     * 根据ID查询用户
     */
    UserVO findById(Long id);
    
    /**
     * 分页查询用户
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<UserVO> findPage(UserQuery query);
    
    /**
     * 内存分页查询用户
     */
    PageResult<UserVO> findPageInMemory(UserQuery query);
    
    /**
     * 创建用户
     */
    void add(UserDTO userDTO);
    
    /**
     * 更新用户
     */
    void update(UserDTO userDTO);
    
    /**
     * 删除用户
     */
    void delete(Long id);
    
    /**
     * 更新用户状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 分配角色
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     * @param userId 用户ID
     */
    void resetPassword(Long userId);
    
    /**
     * 更新个人信息
     * @param userDTO 用户信息
     */
    void updateProfile(UserDTO userDTO);

    /**
     * 批量清理用户缓存
     */
    void clearUserCaches(List<Long> userIds);

    /**
     * 获取用户权限列表
     * @param userId 用户ID
     * @return 权限列表（树形结构）
     */
    List<PermissionVO> findUserPermissions(Long userId);
}
