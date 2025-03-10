package com.czj.rbac.service.impl;

import com.czj.rbac.service.UserIdentifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户标识生成服务实现类
 * 采用Redis和本地双模式结合的方案
 */
@Slf4j
@Service
public class UserIdentifierServiceImpl implements UserIdentifierService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final String REDIS_KEY = "user:identifier:sequence";
    private static final String POOL_KEY = "user:identifier:pool";
    private static final String PREFIX = "XH";
    private final AtomicLong localSequence = new AtomicLong(0);
    
    // 标识符池配置
    private static final int POOL_MIN_SIZE = 100;
    private static final int POOL_BATCH_SIZE = 500;
    
    @PostConstruct
    public void init() {
        try {
            // 从Redis读取当前序号
            String value = redisTemplate.opsForValue().get(REDIS_KEY);
            if (value != null) {
                localSequence.set(Long.parseLong(value));
            }
            // 检查并补充标识符池
            checkAndRefillPool();
        } catch (Exception e) {
            log.error("初始化失败: {}", e.getMessage());
        }
    }
    
    @Override
    public String generateUserIdentifier() {
        try {
            // 1. 优先从池中获取
            String identifier = redisTemplate.opsForList().leftPop(POOL_KEY);
            if (identifier != null) {
                return identifier;
            }
            
            // 2. 池为空,生成新标识符
            Long sequence = redisTemplate.opsForValue().increment(REDIS_KEY);
            localSequence.set(sequence); // 同步到本地
            identifier = PREFIX + String.format("%04d", sequence);
            
            // 3. 异步补充池
            checkAndRefillPool();
            
            return identifier;
        } catch (Exception e) {
            // Redis不可用时使用本地序号
            return PREFIX + String.format("%04d", localSequence.incrementAndGet());
        }
    }
    
    @Scheduled(fixedDelay = 60000) // 每分钟检查一次
    public void checkAndRefillPool() {
        try {
            Long size = redisTemplate.opsForList().size(POOL_KEY);
            if (size != null && size < POOL_MIN_SIZE) {
                long currentSequence = redisTemplate.opsForValue().increment(REDIS_KEY);
                List<String> identifiers = new ArrayList<>();
                for (int i = 1; i <= POOL_BATCH_SIZE; i++) {
                    identifiers.add(PREFIX + String.format("%04d", currentSequence + i));
                }
                redisTemplate.opsForList().rightPushAll(POOL_KEY, identifiers);
                redisTemplate.opsForValue().set(REDIS_KEY, String.valueOf(currentSequence + POOL_BATCH_SIZE));
                log.info("补充标识符池完成,当前序号:{},新增数量:{}", currentSequence + POOL_BATCH_SIZE, POOL_BATCH_SIZE);
            }
        } catch (Exception e) {
            log.warn("补充标识符池失败: {}", e.getMessage());
        }
    }
    
    @Override
    public long getCurrentMaxSequence() {
        try {
            String value = redisTemplate.opsForValue().get(REDIS_KEY);
            return value != null ? Long.parseLong(value) : localSequence.get();
        } catch (Exception e) {
            return localSequence.get();
        }
    }
    
    @Override
    public int getPoolSize() {
        try {
            Long size = redisTemplate.opsForList().size(POOL_KEY);
            return size != null ? size.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
} 