package com.czj.rbac.service;

import com.czj.rbac.model.dto.LoginDTO;
import com.czj.rbac.model.vo.LoginVO;

public interface AuthService {
    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    LoginVO login(String username, String password);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户
     */
    LoginVO getCurrentUser();

    /**
     * 刷新token
     * 
     * @param refreshToken 刷新token
     * @return 新的登录信息
     */
    LoginVO refreshToken(String refreshToken);
} 