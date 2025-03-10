package com.czj.rbac.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "rbac.cache")
public class CacheConfig {
    
    /**
     * 用户缓存配置
     */
    private CacheProperties user = new CacheProperties();
    
    /**
     * 角色缓存配置
     */
    private CacheProperties role = new CacheProperties();
    
    /**
     * 权限缓存配置
     */
    private CacheProperties permission = new CacheProperties();
    
    @Data
    public static class CacheProperties {
        /**
         * 缓存过期时间(秒)
         */
        private long expireTime = 3600;
        
        /**
         * 缓存前缀
         */
        private String prefix = "";
        
        /**
         * 是否允许缓存null值
         */
        private boolean cacheNull = true;
        
        /**
         * null值的过期时间(秒)
         */
        private long nullExpireTime = 60;
    }
} 