package com.czj.rbac.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息VO
 */
@Data
public class UserVO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 状态(1:启用,0:禁用)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 权限列表
     */
    private List<String> permissions;
    
    /**
     * 判断是否为超级管理员
     */
    public boolean isSuperAdmin() {
        return false; // Assuming superAdmin is no longer a field in UserVO
    }
    
    /**
     * 获取权限列表
     */
    public List<String> getPermissions() {
        return permissions;
    }
} 