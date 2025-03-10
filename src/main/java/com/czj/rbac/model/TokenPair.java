package com.czj.rbac.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {
    /**
     * 访问令牌
     */
    private String token;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
} 