package com.czj.rbac.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DistributedLockService {
    
    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_LOCK_TIMEOUT = 10; // 默认锁超时时间（秒）
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 尝试获取分布式锁
     * @param lockKey 锁的key
     * @param timeout 超时时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long timeout) {
        String key = LOCK_PREFIX + lockKey;
        try {
            return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, "1", timeout, TimeUnit.SECONDS)
            );
        } catch (Exception e) {
            log.error("获取分布式锁失败 - key: {}, error: {}", lockKey, e.getMessage());
            return false;
        }
    }
    
    /**
     * 尝试获取分布式锁（使用默认超时时间）
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_LOCK_TIMEOUT);
    }
    
    /**
     * 释放分布式锁
     */
    public void unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("释放分布式锁失败 - key: {}, error: {}", lockKey, e.getMessage());
        }
    }
    
    /**
     * 在锁保护下执行任务
     */
    public <T> T executeWithLock(String lockKey, long timeout, DistributedTask<T> task) {
        if (!tryLock(lockKey, timeout)) {
            throw new RuntimeException("获取锁失败");
        }
        try {
            return task.execute();
        } finally {
            unlock(lockKey);
        }
    }
    
    /**
     * 分布式任务接口
     */
    @FunctionalInterface
    public interface DistributedTask<T> {
        T execute();
    }
} 