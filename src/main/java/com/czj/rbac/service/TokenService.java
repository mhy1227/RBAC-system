package com.czj.rbac.service;

import com.czj.rbac.model.TokenPair;

/**
 * Token服务
 */
public interface TokenService {
    
    /**
     * 创建Token
     *
     * @param userId 用户ID
     * @return Token字符串
     */
    String createToken(Long userId);
    
    /**
     * 验证Token
     *
     * @param token Token字符串
     * @return 是否有效
     */
    boolean validateToken(String token);
    
    /**
     * 刷新Token
     *
     * @param token 刷新Token字符串
     * @return Token对象，包含新的访问token和刷新token
     */
    TokenPair refreshToken(String token);
    
    /**
     * 移除Token
     *
     * @param userId 用户ID
     */
    void removeToken(Long userId);
    
    /**
     * 获取刷新Token
     *
     * @param userId 用户ID
     * @return 刷新Token字符串
     */
    String getRefreshToken(Long userId);
    
    /**
     * 生成Token对
     *
     * @param userId 用户ID
     * @return Token对象，包含访问token和刷新token
     */
    TokenPair generateTokenPair(Long userId);
    
    /**
     * 从Token中获取用户ID
     *
     * @param token Token字符串
     * @return 用户ID
     */
    Long getUserIdFromToken(String token);
    
    /**
     * 获取Token剩余有效期（毫秒）
     *
     * @param token Token字符串
     * @return 剩余有效期，token无效时返回-1
     */
    long getTokenRemainingTime(String token);
}
