package com.czj.rbac.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import io.jsonwebtoken.MalformedJwtException;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class JwtUtilTest {

    private static final String TEST_USERNAME = "admin";
    private static final Long TEST_USER_ID = 1L;
    private static final List<String> TEST_PERMISSIONS = Arrays.asList("sys:admin");
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testGenerateAndParseJwt() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", TEST_USER_ID);
        claims.put("username", TEST_USERNAME);
        claims.put("permissions", TEST_PERMISSIONS);

        String jwt = JwtUtil.generateJwt(claims);
        assertNotNull(jwt);
        
        Claims parsedClaims = JwtUtil.parseJwt(jwt);
        assertNotNull(parsedClaims);
        assertEquals(TEST_USER_ID.intValue(), ((Integer) parsedClaims.get("userId")).intValue());
        assertEquals(TEST_USERNAME, parsedClaims.get("username"));
        assertEquals(TEST_PERMISSIONS, parsedClaims.get("permissions"));
    }

    @Test
    void testValidateToken() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "admin");

        // 生成有效的JWT
        String jwt = JwtUtil.generateJwt(claims);
        assertTrue(JwtUtil.validateToken(jwt));
    }

    @Test
    void testValidateToken_Invalid() {
        assertFalse(JwtUtil.validateToken("invalid-token"));
    }

    @Test
    void testValidateToken_Expired() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", TEST_USER_ID);
        claims.put("username", TEST_USERNAME);
        claims.put("exp", new Date(System.currentTimeMillis() - 3600000L)); // 1小时前过期

        String jwt = JwtUtil.generateJwt(claims);
        assertNotNull(jwt);
        assertFalse(JwtUtil.validateToken(jwt));
    }

    @Test
    void testParseJwt_InvalidFormat() {
        assertThrows(MalformedJwtException.class, () -> {
            JwtUtil.parseJwt("invalid.token.format");
        });
    }

    @Test
    void testParseJwt_EmptyToken() {
        assertNull(JwtUtil.parseJwt(""));
        assertNull(JwtUtil.parseJwt(null));
    }

    @Test
    void testGetCurrentUsername_Success() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", TEST_USER_ID);
        claims.put("username", TEST_USERNAME);
        
        String jwt = JwtUtil.generateJwt(claims);
        request.addHeader("Authorization", jwt);
        
        assertEquals(TEST_USERNAME, JwtUtil.getCurrentUsername());
    }

    @Test
    void testGetCurrentUsername_NoToken() {
        assertNull(JwtUtil.getCurrentUsername());
    }

    @Test
    void testGetCurrentUserId_Success() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", TEST_USER_ID);
        claims.put("username", TEST_USERNAME);
        
        String jwt = JwtUtil.generateJwt(claims);
        request.addHeader("Authorization", jwt);
        
        assertEquals(TEST_USER_ID, JwtUtil.getCurrentUserId());
    }

    @Test
    void testGetCurrentUserPermissions_Success() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", TEST_USER_ID);
        claims.put("username", TEST_USERNAME);
        claims.put("permissions", TEST_PERMISSIONS);
        
        String jwt = JwtUtil.generateJwt(claims);
        request.addHeader("Authorization", jwt);
        
        assertEquals(TEST_PERMISSIONS, JwtUtil.getCurrentUserPermissions());
    }
} 