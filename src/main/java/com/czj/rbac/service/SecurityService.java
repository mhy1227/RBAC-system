package com.czj.rbac.service;

import com.czj.rbac.model.vo.UserVO;

/**
 * 安全服务接口
 */
public interface SecurityService {
    
    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户信息,未登录返回null
     */
    UserVO getCurrentUser();
    
    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID,未登录返回null
     */
    Long getCurrentUserId();
    
    /**
     * 判断当前用户是否为超级管理员
     *
     * @return true表示是超级管理员
     */
    boolean isSuperAdmin();
} 