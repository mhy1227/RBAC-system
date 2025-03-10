package com.czj.rbac.model.vo;

import lombok.Data;
import java.util.List;

@Data
public class LoginVO {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 访问令牌
     */
    private String token;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 权限列表
     */
    private List<String> permissions;  // 权限标识列表
    
    private UserVO user;
} 