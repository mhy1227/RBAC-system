package com.czj.rbac.service.impl;

import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import com.czj.rbac.event.UserLoginEvent;
import com.czj.rbac.model.TokenPair;
import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.service.DistributedLockService;
import com.czj.rbac.service.LoginInfoService;
import com.czj.rbac.service.TokenService;
import com.czj.rbac.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class TokenServiceImpl implements TokenService {
    
    private static final String TOKEN_PREFIX = "token:";
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "token:refresh:";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Value("${rbac.jwt.expire-time}")
    private long expireTime;
    
    @Value("${rbac.jwt.refresh-expire-time:86400000}") // 默认24小时
    private long refreshExpireTime;

    @Value("${rbac.jwt.ip-check.enabled:true}")
    private boolean ipCheckEnabled;

    @Value("${rbac.jwt.ip-check.action:REJECT}")
    private String ipCheckAction;
    
    @Value("${rbac.security.session.max-sessions:1}")
    private int maxSessions;

    @Value("${rbac.security.session.kick-out:true}")
    private boolean kickOut;

    private static final String USER_SESSIONS_PREFIX = "login:sessions:";
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private DistributedLockService lockService;
    
    @Autowired
    private LoginInfoService loginInfoService;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Override
    public TokenPair generateTokenPair(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR);
        }

        String loginId = UUID.randomUUID().toString();
        try {
            // 生成访问令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userId);
            claims.put("loginId", loginId);
            String token = JwtUtil.generateJwt(claims);
            
            // 生成刷新令牌
            String refreshToken = JwtUtil.generateRefreshToken(claims, refreshExpireTime);
            
            // 保存用户会话信息
            saveUserSession(userId, loginId, token);
            
            // 保存刷新令牌
            String refreshTokenKey = REFRESH_TOKEN_PREFIX + userId;
            redisTemplate.opsForValue().set(refreshTokenKey, refreshToken, refreshExpireTime, TimeUnit.MILLISECONDS);
            
            log.info("Token生成成功 - userId: {}", userId);
            return new TokenPair(token, refreshToken);
        } catch (Exception e) {
            log.error("Token生成失败 - userId: {}", userId, e);
            throw new BusinessException(ResponseCode.ERROR);
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        if (token == null) {
            return false;
        }

        try {
            // 处理Bearer前缀
            final String tokenToValidate = token.startsWith(BEARER_PREFIX) ? 
                token.substring(BEARER_PREFIX.length()) : token;
            
            // 1. 验证token基本有效性
            if (!JwtUtil.validateToken(tokenToValidate)) {
                log.warn("Token基本验证失败");
                return false;
            }
            
            // 2. 检查是否在黑名单中
            if (isTokenBlacklisted(tokenToValidate)) {
                log.warn("Token已被加入黑名单");
                return false;
            }
            
            // 3. 获取用户ID并检查Redis中的token
            Claims claims = JwtUtil.parseJwt(tokenToValidate);
            Long userId = Long.valueOf(claims.getSubject());
            String storedToken = (String) redisTemplate.opsForValue().get(TOKEN_PREFIX + userId);
            
            // 4. 检查token是否存在于Redis中
            if (storedToken == null) {
                log.warn("Token不存在于Redis中 - userId: {}", userId);
                return false;
            }
            
            // 确保比较的token都不带Bearer前缀
            final String storedTokenToCompare = storedToken.startsWith(BEARER_PREFIX) ? 
                storedToken.substring(BEARER_PREFIX.length()) : storedToken;
            
            return tokenToValidate.equals(storedTokenToCompare);
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public TokenPair refreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "刷新令牌不能为空");
        }

        // 处理Bearer前缀
        final String finalRefreshToken = refreshToken.startsWith(BEARER_PREFIX) ? 
            refreshToken.substring(BEARER_PREFIX.length()) : refreshToken;

        String lockKey = "refresh:" + finalRefreshToken;
        
        return lockService.executeWithLock(lockKey, 10L, () -> {
            try {
                // 1. 验证刷新token
                if (!JwtUtil.validateRefreshToken(finalRefreshToken)) {
                    throw new BusinessException(ResponseCode.UNAUTHORIZED, "刷新令牌已过期或无效");
                }
                
                // 2. 解析刷新token
                Claims claims = JwtUtil.parseRefreshToken(finalRefreshToken);
                Long userId = Long.valueOf(claims.getSubject());
                
                // 3. 验证是否是当前用户的刷新token
                String storedRefreshToken = getRefreshToken(userId);
                if (storedRefreshToken == null) {
                    throw new BusinessException(ResponseCode.UNAUTHORIZED, "刷新令牌已过期");
                }
                if (!finalRefreshToken.equals(storedRefreshToken)) {
                    throw new BusinessException(ResponseCode.UNAUTHORIZED, "无效的刷新令牌");
                }
                
                // 4. 生成新的token对
                return generateTokenPair(userId);
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("Token刷新失败: {}", e.getMessage());
                throw new BusinessException(ResponseCode.UNAUTHORIZED, "Token刷新失败");
            }
        });
    }
    
    @Override
    public void removeToken(Long userId) {
        try {
            // 获取当前token并记录登出
            String currentToken = (String) redisTemplate.opsForValue().get(TOKEN_PREFIX + userId);
            if (currentToken != null) {
                try {
                    Claims claims = JwtUtil.parseJwt(currentToken);
                    String loginId = claims.get("loginId", String.class);
                    if (loginId != null) {
                        loginInfoService.recordLogout(userId, loginId);
                    }
                } catch (Exception e) {
                    log.error("记录登出信息时发生错误", e);
                }
            }
            
            // 移除用户所有会话
            String sessionsKey = USER_SESSIONS_PREFIX + userId;
            Set<Object> sessionsObj = redisTemplate.opsForSet().members(sessionsKey);
            Set<String> sessions = sessionsObj.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
            if (sessions != null) {
                for (String token : sessions) {
                    addToBlacklist(token);
                }
            }
            
            // 清理Redis数据
            redisTemplate.delete(sessionsKey);
            redisTemplate.delete(TOKEN_PREFIX + userId);
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
            
            log.info("用户token移除完成 - userId: {}", userId);
        } catch (Exception e) {
            log.error("移除Token失败: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String getRefreshToken(Long userId) {
        if (userId == null) {
            return null;
        }
        return (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
    }

    @Override
    public String createToken(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR);
        }
        
        try {
            // 生成访问令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userId);
            claims.put("loginId", UUID.randomUUID().toString());
            String token = JwtUtil.generateJwt(claims);
            
            // 保存用户会话信息
            saveUserSession(userId, claims.get("loginId").toString(), token);
            
            return BEARER_PREFIX + token;
        } catch (Exception e) {
            log.error("Token生成失败 - userId: {}", userId, e);
            throw new BusinessException(ResponseCode.ERROR);
        }
    }

    private void addToBlacklist(String token) {
        if (token == null) {
            return;
        }
        
        try {
            Claims claims = JwtUtil.parseJwt(token);
            long remainingTime = claims.getExpiration().getTime() - System.currentTimeMillis();
            
            if (remainingTime > 0) {
                String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
                boolean added = Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(blacklistKey, "1", remainingTime, TimeUnit.MILLISECONDS)
                );
                
                if (!added) {
                    log.debug("Token已在黑名单中 - token: {}", token);
                } else {
                    log.debug("Token已加入黑名单 - token: {}", token);
                }
            }
        } catch (Exception e) {
            log.error("Token加入黑名单失败: {}", e.getMessage());
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "Token加入黑名单失败");
        }
    }
    
    private boolean isTokenBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_BLACKLIST_PREFIX + token));
        } catch (Exception e) {
            log.error("检查Token黑名单失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Transactional
    private void invalidateToken(Long userId) {
        if (userId == null) {
            return;
        }
        
        try {
            // 1. 获取当前token并加入黑名单
            String token = (String) redisTemplate.opsForValue().get(TOKEN_PREFIX + userId);
            if (token != null) {
                // 处理Bearer前缀
                final String tokenToInvalidate = token.startsWith(BEARER_PREFIX) ? 
                    token.substring(BEARER_PREFIX.length()) : token;
                addToBlacklist(tokenToInvalidate);
            }
            
            // 2. 删除用户相关的所有token
            redisTemplate.delete(Arrays.asList(
                TOKEN_PREFIX + userId,
                REFRESH_TOKEN_PREFIX + userId
            ));
            
            log.info("用户token已清除 - userId: {}", userId);
        } catch (Exception e) {
            log.error("使Token失效失败 - userId: {}", userId, e);
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "清理Token失败");
        }
    }

    @Override
    public Long getUserIdFromToken(String token) {
        if (token == null) {
            return null;
        }
        
        try {
            final String tokenToUse = token.startsWith(BEARER_PREFIX) ? 
                token.substring(BEARER_PREFIX.length()) : token;
                
            Claims claims = JwtUtil.parseJwt(tokenToUse);
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            log.error("从Token中获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public long getTokenRemainingTime(String token) {
        if (token == null) {
            return -1L;
        }
        
        try {
            final String tokenToUse = token.startsWith(BEARER_PREFIX) ? 
                token.substring(BEARER_PREFIX.length()) : token;
                
            Claims claims = JwtUtil.parseJwt(tokenToUse);
            Date expiration = claims.getExpiration();
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            log.error("获取Token剩余有效期失败: {}", e.getMessage());
            return -1L;
        }
    }

    private void saveUserSession(Long userId, String loginId, String accessToken) {
        String sessionKey = USER_SESSIONS_PREFIX + userId;
        try {
            // 获取用户当前的会话列表，并进行类型转换
            List<Object> sessionObjects = redisTemplate.opsForList().range(sessionKey, 0, -1);
            List<String> sessions = sessionObjects == null ? new ArrayList<>() : 
                sessionObjects.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            
            // 如果超过最大会话数，需要删除最早的会话
            while (sessions.size() >= maxSessions) {
                if (kickOut) {
                    String oldestSession = sessions.remove(0);
                    // 将旧token加入黑名单
                    addToBlacklist(oldestSession);
                } else {
                    throw new BusinessException(ResponseCode.ERROR);
                }
            }
            
            // 添加新会话
            sessions.add(accessToken);
            
            // 更新Redis中的会话列表
            redisTemplate.delete(sessionKey);
            redisTemplate.opsForList().rightPushAll(sessionKey, sessions);
            redisTemplate.expire(sessionKey, expireTime, TimeUnit.MILLISECONDS);
            
            log.info("保存用户会话成功 - userId: {}, loginId: {}", userId, loginId);
        } catch (Exception e) {
            log.error("保存用户会话失败 - userId: {}, loginId: {}", userId, loginId, e);
            throw new BusinessException(ResponseCode.ERROR);
        }
    }
} 