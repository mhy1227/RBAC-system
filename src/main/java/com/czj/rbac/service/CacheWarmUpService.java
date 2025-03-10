package com.czj.rbac.service;

/**
 * 缓存预热服务接口
 */
public interface CacheWarmUpService {
    
    /**
     * 预热所有缓存
     */
    void warmUpCaches();
    
    /**
     * 初始化缓存服务
     */
    void init();
    
    /**
     * 销毁缓存服务
     */
    void destroy();
}