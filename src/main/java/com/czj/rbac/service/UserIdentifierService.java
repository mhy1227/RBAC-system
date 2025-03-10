package com.czj.rbac.service;

/**
 * 用户标识生成服务
 */
public interface UserIdentifierService {
    
    /**
     * 生成用户标识
     * 
     * @return 生成的用户标识（如：XH0001）
     */
    String generateUserIdentifier();

    /**
     * 获取当前最大序号
     */
    long getCurrentMaxSequence();

    /**
     * 获取池中剩余数量
     */
    int getPoolSize();
} 