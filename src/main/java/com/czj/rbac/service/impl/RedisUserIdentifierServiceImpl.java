package com.czj.rbac.service.impl;

import com.czj.rbac.service.UserIdentifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis实现的用户标识生成服务
 */
@Slf4j
@Service
public class RedisUserIdentifierServiceImpl implements UserIdentifierService {
    
    private static final String POOL_KEY = "user:identifier:pool";
    private static final String MAX_KEY = "user:identifier:max";
    private static final String LOCK_KEY = "user:identifier:pool:lock";
    private static final int POOL_SIZE = 1000;
    private static final int REFILL_THRESHOLD = 200;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Override
    public String generateUserIdentifier() {
        String identifier = redisTemplate.opsForSet().pop(POOL_KEY);
        if (identifier == null) {
            refillPool(true);
            identifier = redisTemplate.opsForSet().pop(POOL_KEY);
        }
        checkAndRefillPool();
        return identifier;
    }
    
    @Override
    public long getCurrentMaxSequence() {
        String maxValue = redisTemplate.opsForValue().get(MAX_KEY);
        return maxValue == null ? 0 : Long.parseLong(maxValue);
    }
    
    @Override
    public int getPoolSize() {
        Long size = redisTemplate.opsForSet().size(POOL_KEY);
        return size == null ? 0 : size.intValue();
    }
    
    @Async
    public void checkAndRefillPool() {
        Long size = redisTemplate.opsForSet().size(POOL_KEY);
        if (size != null && size < REFILL_THRESHOLD) {
            refillPool(false);
        }
    }
    
    private synchronized void refillPool(boolean urgent) {
        if (redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, "1", 10, TimeUnit.SECONDS)) {
            try {
                int batchSize = urgent ? 100 : POOL_SIZE - REFILL_THRESHOLD;
                String maxValue = redisTemplate.opsForValue().get(MAX_KEY);
                long start = maxValue == null ? 1 : Long.parseLong(maxValue) + 1;
                
                for (long i = start; i < start + batchSize; i++) {
                    String identifier = String.format("XH%04d", i);
                    redisTemplate.opsForSet().add(POOL_KEY, identifier);
                }
                
                redisTemplate.opsForValue().set(MAX_KEY, String.valueOf(start + batchSize - 1));
                log.info("补充标识符池完成，当前最大序号：{}，补充数量：{}", start + batchSize - 1, batchSize);
            } finally {
                redisTemplate.delete(LOCK_KEY);
            }
        }
    }
} 