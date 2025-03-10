package com.czj.rbac.service;

import com.czj.rbac.model.vo.UserVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.Cursor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class UserCacheService {
    
    private static final String USER_CACHE_PREFIX = "user:info:";
    private static final String USER_CACHE_LOCK_PREFIX = "user:lock:";
    private static final String USER_CACHE_NULL_VALUE = "NULL";
    private static final long NULL_VALUE_EXPIRE = 60; // 空值缓存60秒
    
    @Value("${rbac.cache.user.expire:3600}")
    private long userCacheExpire;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 获取用户缓存
     */
    public UserVO getUserCache(Long userId) {
        if (userId == null) {
            return null;
        }
        
        String key = USER_CACHE_PREFIX + userId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            
            // 处理空值缓存
            if (USER_CACHE_NULL_VALUE.equals(value)) {
                return null;
            }
            
            return objectMapper.readValue(value.toString(), UserVO.class);
        } catch (Exception e) {
            log.error("获取用户缓存失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 设置用户缓存
     */
    public void setUserCache(Long userId, UserVO userVO) {
        if (userId == null) {
            return;
        }
        
        String key = USER_CACHE_PREFIX + userId;
        try {
            if (userVO == null) {
                // 设置空值缓存，防止缓存穿透
                redisTemplate.opsForValue().set(key, USER_CACHE_NULL_VALUE, NULL_VALUE_EXPIRE, TimeUnit.SECONDS);
            } else {
                // 添加随机过期时间，防止缓存雪崩
                long expireTime = userCacheExpire + (long)(Math.random() * 300);
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(userVO), expireTime, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("设置用户缓存失败: {}", e.getMessage());
        }
    }
    
    /**
     * 删除用户缓存
     */
    public void deleteUserCache(Long userId) {
        if (userId == null) {
            return;
        }
        redisTemplate.delete(USER_CACHE_PREFIX + userId);
    }
    
    /**
     * 批量删除用户缓存
     */
    public void batchDeleteUserCache(Long... userIds) {
        if (userIds == null || userIds.length == 0) {
            return;
        }
        
        List<String> keys = Arrays.stream(userIds)
            .map(id -> USER_CACHE_PREFIX + id)
            .collect(Collectors.toList());
            
        redisTemplate.delete(keys);
    }
    
    /**
     * 更新用户缓存过期时间
     */
    public void refreshUserCache(Long userId) {
        if (userId == null) {
            return;
        }
        String key = USER_CACHE_PREFIX + userId;
        redisTemplate.expire(key, userCacheExpire, TimeUnit.SECONDS);
        log.debug("刷新用户缓存过期时间 - userId: {}", userId);
    }
    
    /**
     * 清除所有用户缓存
     */
    public void clearAllUserCache() {
        try {
            String pattern = USER_CACHE_PREFIX + "*";
            Set<String> keys = new HashSet<>();
            redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(100).build())) {
                    while (cursor.hasNext()) {
                        keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                    }
                    return keys;
                }
            });
            
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("清除所有用户缓存，共清除{}个", keys.size());
            }
        } catch (Exception e) {
            log.error("清除所有用户缓存失败: {}", e.getMessage());
        }
    }
} 