package com.czj.rbac.aspect;

import com.czj.rbac.annotation.RequirePermission;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import com.czj.rbac.service.UnifiedPermissionService;
import com.czj.rbac.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private UnifiedPermissionService permissionService;

    @Around("@annotation(permission)")
    public Object checkPermission(ProceedingJoinPoint point, RequirePermission permission) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = point.getSignature().getName();
        
        try {
            // 获取token并验证
            String token = JwtUtil.getTokenFromRequest();
            if (!StringUtils.hasText(token)) {
                log.warn("未提供token");
                throw new BusinessException(ResponseCode.UNAUTHORIZED, "未登录或登录已过期");
            }

            // 验证token
            try {
                if (!JwtUtil.validateToken(token)) {
                    log.warn("token无效或已过期");
                    throw new BusinessException(ResponseCode.UNAUTHORIZED, "未登录或登录已过期");
                }
            } catch (Exception e) {
                log.error("验证token时发生错误: {}", e.getMessage());
                throw new BusinessException(ResponseCode.UNAUTHORIZED, "未登录或登录已过期");
            }
            
            // 获取当前用户ID
            Long userId = JwtUtil.getCurrentUserId();
            if (userId == null) {
                log.warn("无法获取用户ID");
                throw new BusinessException(ResponseCode.UNAUTHORIZED, "未登录或登录已过期");
            }

            // 获取注解中的权限值
            String[] requiredPermissions = permission.value().split(",");
            boolean requireAll = permission.requireAll();

            log.debug("权限校验 - 用户: {}, 需要权限: {}, 需要全部权限: {}", 
                     userId, Arrays.toString(requiredPermissions), requireAll);

            // 校验权限
            boolean hasPermission = false;
            if (requireAll) {
                hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(p -> permissionService.checkFunctionPermission(userId, p.trim()));
            } else {
                hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(p -> permissionService.checkFunctionPermission(userId, p.trim()));
            }

            if (!hasPermission) {
                throw new BusinessException(ResponseCode.FORBIDDEN, "权限不足");
            }

            // 执行目标方法
            Object result = point.proceed();
            
            // 记录执行时间
            long endTime = System.currentTimeMillis();
            log.debug("权限校验耗时: {}ms - {}", endTime - startTime, methodName);
            
            return result;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            log.error("权限校验失败: {}ms - {} - {}", endTime - startTime, methodName, e.getMessage());
            throw e;
        }
    }
} 