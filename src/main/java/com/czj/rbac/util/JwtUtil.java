package com.czj.rbac.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    private static String secretKey;
    private static Long expireTime;
    private static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    @Value("${rbac.jwt.secret-key}")
    public void setSecretKey(String key) {
        // 对密钥进行Base64编码
        secretKey = Base64.getEncoder().encodeToString(key.getBytes());
    }

    @Value("${rbac.jwt.expire-time}")
    public void setExpireTime(Long expire) {
        expireTime = expire;
    }

    /**
     * 生成JWT令牌
     * @param claims 存储的内容
     * @return JWT令牌
     */
    public static String generateJwt(Map<String, Object> claims) {
        if (claims == null || claims.isEmpty()) {
            return null;
        }

        try {
            // 如果没有设置过期时间，则使用默认过期时间
            if (!claims.containsKey("exp")) {
                claims.put("exp", new Date(System.currentTimeMillis() + expireTime));
            }
            
            String jwt = Jwts.builder()
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
            log.debug("Generate JWT: {}", jwt);
            return jwt;
        } catch (Exception e) {
            log.error("Generate JWT error: ", e);
            return null;
        }
    }

    /**
     * 生成刷新令牌
     */
    public static String generateRefreshToken(Map<String, Object> claims, long refreshExpireTime) {
        if (claims == null || claims.isEmpty()) {
            return null;
        }

        try {
            // 设置刷新token的过期时间
            claims.put("exp", new Date(System.currentTimeMillis() + refreshExpireTime));
            
            // 使用原始密钥+"refresh"生成刷新token的密钥
            String refreshKey = Base64.getEncoder().encodeToString((new String(Base64.getDecoder().decode(secretKey)) + "refresh").getBytes());
            
            return Jwts.builder()
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS256, refreshKey)
                    .compact();
        } catch (Exception e) {
            log.error("Generate refresh token error: ", e);
            return null;
        }
    }

    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return Claims对象，解析失败返回null
     */
    public static Claims parseJwt(String jwt) {
        if (!StringUtils.hasText(jwt)) {
            return null;
        }

        // 去掉 Bearer 前缀
        if (jwt.startsWith(TOKEN_PREFIX)) {
            jwt = jwt.substring(TOKEN_PREFIX.length());
        }

        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            log.error("Parse JWT error: ", e);
            throw e;
        }
    }

    /**
     * 解析刷新令牌
     */
    public static Claims parseRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return null;
        }

        try {
            // 使用原始密钥+"refresh"解析刷新token
            String refreshKey = Base64.getEncoder().encodeToString((new String(Base64.getDecoder().decode(secretKey)) + "refresh").getBytes());
            
            return Jwts.parser()
                    .setSigningKey(refreshKey)
                    .parseClaimsJws(refreshToken)
                    .getBody();
        } catch (Exception e) {
            log.error("Parse refresh token error: ", e);
            throw e;
        }
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        Claims claims = getClaimsFromRequest();
        if (claims != null && claims.getSubject() != null) {
            return Long.valueOf(claims.getSubject());
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Claims claims = getClaimsFromRequest();
        if (claims != null) {
            return claims.get("username", String.class);
        }
        return null;
    }

    /**
     * 从请求中获取token
     */
    public static String getTokenFromRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader(TOKEN_HEADER);
            
            if (!StringUtils.hasText(token)) {
                return null;
            }
            
            // 去掉Bearer前缀
            if (token.startsWith(TOKEN_PREFIX)) {
                token = token.substring(TOKEN_PREFIX.length());
            }
            
            return token;
        } catch (Exception e) {
            log.error("Get token from request error: ", e);
            return null;
        }
    }

    /**
     * 从请求中获取Claims
     */
    private static Claims getClaimsFromRequest() {
        try {
            String jwt = getTokenFromRequest();
            if (!StringUtils.hasText(jwt)) {
                log.debug("No token found in request");
                return null;
            }
            return parseJwt(jwt);
        } catch (Exception e) {
            log.error("Get claims from request error: ", e);
            return null;
        }
    }

    /**
     * 验证令牌是否有效
     */
    public static boolean validateToken(String jwt) {
        if (!StringUtils.hasText(jwt)) {
            log.debug("Token is empty");
            return false;
        }

        try {
            Claims claims = parseJwt(jwt);
            Date expiration = claims.getExpiration();
            return expiration == null || expiration.after(new Date());
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证刷新令牌是否有效
     */
    public static boolean validateRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            log.debug("Refresh token is empty");
            return false;
        }

        try {
            Claims claims = parseRefreshToken(refreshToken);
            Date expiration = claims.getExpiration();
            return expiration == null || expiration.after(new Date());
        } catch (Exception e) {
            log.debug("Refresh token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取当前用户权限
     */
    public static List<String> getCurrentUserPermissions() {
        Claims claims = getClaimsFromRequest();
        return claims != null ? (List<String>) claims.get("permissions") : Collections.emptyList();
    }

    /**
     * 从Token中获取用户ID
     * @param token JWT令牌
     * @return 用户ID，解析失败返回null
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseJwt(token);
        if (claims != null) {
            Object userId = claims.get("userId");
            if (userId != null) {
                return userId instanceof Integer ? 
                    ((Integer) userId).longValue() : 
                    Long.valueOf(userId.toString());
            }
        }
        return null;
    }
} 