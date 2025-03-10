package com.czj.rbac.service;

import com.czj.rbac.model.vo.RoleVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleCacheService {
    
    private static final String ROLE_CACHE_PREFIX = "role:info:";
    private static final String ROLE_PERMISSION_CACHE_PREFIX = "role:permissions:";
    private static final String ROLE_CACHE_LOCK_PREFIX = "role:lock:";
    private static final String ROLE_CACHE_NULL_VALUE = "NULL";
    private static final long NULL_VALUE_EXPIRE = 60; // 空值缓存60秒
    private static final String ROLE_LIST_CACHE_KEY = "role:list";
    
    @Value("${rbac.cache.role.expire:3600}")
    private long roleCacheExpire;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private DistributedLockService lockService;
    
    /**
     * 将角色信息存入缓存
     */
    public void setRoleCache(Long roleId, RoleVO roleVO) {
        if (roleId == null) {
            return;
        }
        String key = ROLE_CACHE_PREFIX + roleId;
        try {
            if (roleVO == null) {
                // 缓存空值，防止缓存穿透
                redisTemplate.opsForValue().set(key, ROLE_CACHE_NULL_VALUE, NULL_VALUE_EXPIRE, TimeUnit.SECONDS);
                return;
            }
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(roleVO), roleCacheExpire, TimeUnit.SECONDS);
            log.debug("角色信息已缓存 - roleId: {}", roleId);
        } catch (Exception e) {
            log.error("缓存角色信息失败 - roleId: {}, error: {}", roleId, e.getMessage());
        }
    }
    
    /**
     * 从缓存获取角色信息
     */
    public RoleVO getRoleCache(Long roleId) {
        if (roleId == null) {
            return null;
        }
        String key = ROLE_CACHE_PREFIX + roleId;
        String lockKey = ROLE_CACHE_LOCK_PREFIX + roleId;
        
        try {
            // 1. 尝试获取缓存
            String json = (String) redisTemplate.opsForValue().get(key);
            
            // 2. 判断是否为空值缓存
            if (ROLE_CACHE_NULL_VALUE.equals(json)) {
                return null;
            }
            
            // 3. 缓存命中且数据有效
            if (json != null) {
                try {
                    return objectMapper.readValue(json, RoleVO.class);
                } catch (JsonProcessingException e) {
                    log.error("解析角色缓存数据失败 - roleId: {}, error: {}", roleId, e.getMessage());
                    deleteRoleCache(roleId); // 删除无效的缓存数据
                }
            }
            
            // 4. 缓存未命中或数据无效，使用分布式锁防止击穿
            return lockService.executeWithLock(lockKey, 10, () -> {
                try {
                    // 双重检查，重新获取缓存
                    String jsonData = (String) redisTemplate.opsForValue().get(key);
                    if (jsonData != null) {
                        if (ROLE_CACHE_NULL_VALUE.equals(jsonData)) {
                            return null;
                        }
                        return objectMapper.readValue(jsonData, RoleVO.class);
                    }
                    return null;
                } catch (JsonProcessingException e) {
                    log.error("解析角色缓存数据失败 - roleId: {}, error: {}", roleId, e.getMessage());
                    deleteRoleCache(roleId);
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("获取角色缓存失败 - roleId: {}, error: {}", roleId, e.getMessage());
            return null;
        }
    }
    
    /**
     * 删除角色缓存
     */
    public void deleteRoleCache(Long roleId) {
        if (roleId == null) {
            return;
        }
        String key = ROLE_CACHE_PREFIX + roleId;
        redisTemplate.delete(key);
        log.debug("删除角色缓存 - roleId: {}", roleId);
    }
    
    /**
     * 删除角色权限缓存
     */
    public void deleteRolePermissionCache(Long roleId) {
        if (roleId == null) {
            return;
        }
        String key = ROLE_PERMISSION_CACHE_PREFIX + roleId;
        redisTemplate.delete(key);
        log.debug("删除角色权限缓存 - roleId: {}", roleId);
    }
    
    /**
     * 批量删除角色缓存
     */
    public void batchDeleteRoleCache(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        
        try {
            // 1. 批量删除角色信息缓存
            List<String> roleKeys = roleIds.stream()
                .map(id -> ROLE_CACHE_PREFIX + id)
                .collect(Collectors.toList());
            redisTemplate.delete(roleKeys);
            
            // 2. 批量删除角色权限缓存
            List<String> permissionKeys = roleIds.stream()
                .map(id -> ROLE_PERMISSION_CACHE_PREFIX + id)
                .collect(Collectors.toList());
            redisTemplate.delete(permissionKeys);
            
            log.debug("批量删除角色缓存 - roleIds: {}", roleIds);
        } catch (Exception e) {
            log.error("批量删除角色缓存失败 - roleIds: {}, error: {}", roleIds, e.getMessage());
        }
    }
    
    /**
     * 清除所有角色缓存
     */
    public void clearAllRoleCache() {
        try {
            // 清理角色缓存
            clearCacheByPattern(ROLE_CACHE_PREFIX + "*");
            // 清理角色权限缓存
            clearCacheByPattern(ROLE_PERMISSION_CACHE_PREFIX + "*");
            log.info("清除所有角色缓存");
        } catch (Exception e) {
            log.error("清除所有角色缓存失败: {}", e.getMessage());
        }
    }
    
    /**
     * 使用scan命令按pattern清理缓存
     */
    private void clearCacheByPattern(String pattern) {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                    .match(pattern)
                    .count(100)
                    .build());
            try {
                while (cursor.hasNext()) {
                    redisTemplate.delete(new String(cursor.next()));
                }
            } finally {
                cursor.close();
            }
            return null;
        });
    }
    
    /**
     * 缓存角色列表
     */
    public void setRoleListCache(List<RoleVO> roleList) {
        try {
            String json = objectMapper.writeValueAsString(roleList);
            redisTemplate.opsForValue().set(ROLE_LIST_CACHE_KEY, json, roleCacheExpire, TimeUnit.SECONDS);
            log.debug("角色列表已缓存, size: {}", roleList.size());
        } catch (Exception e) {
            log.error("缓存角色列表失败: {}", e.getMessage());
        }
    }
    
    /**
     * 获取角色列表缓存
     */
    public List<RoleVO> getRoleListCache() {
        try {
            String json = (String) redisTemplate.opsForValue().get(ROLE_LIST_CACHE_KEY);
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, RoleVO.class));
        } catch (Exception e) {
            log.error("获取角色列表缓存失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 删除角色列表缓存
     */
    public void deleteRoleListCache() {
        redisTemplate.delete(ROLE_LIST_CACHE_KEY);
        log.debug("删除角色列表缓存");
    }
} 