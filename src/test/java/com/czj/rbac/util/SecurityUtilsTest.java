package com.czj.rbac.util;

import com.czj.rbac.common.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityUtilsTest {

    @Test
    public void testGetLoginUserId_Success() {
        try (MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class)) {
            // Mock JWT工具类
            jwtUtil.when(JwtUtil::getCurrentUserId).thenReturn(1L);

            // 执行测试
            Long userId = SecurityUtils.getLoginUserId();

            // 验证结果
            assertEquals(1L, userId);
        }
    }

    @Test
    public void testGetLoginUserId_Fail() {
        try (MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class)) {
            // Mock JWT工具类返回null
            jwtUtil.when(JwtUtil::getCurrentUserId).thenReturn(null);

            // 执行测试并验证异常
            assertThrows(BusinessException.class, () -> {
                SecurityUtils.getLoginUserId();
            });
        }
    }

    @Test
    public void testHasAdminPermission_Success() {
        try (MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class)) {
            // Mock JWT工具类
            jwtUtil.when(JwtUtil::getCurrentUserPermissions)
                  .thenReturn(Arrays.asList("sys:admin"));

            // 执行测试
            boolean hasPermission = SecurityUtils.hasAdminPermission();

            // 验证结果
            assertTrue(hasPermission);
        }
    }

    // ... 其他测试方法
} 