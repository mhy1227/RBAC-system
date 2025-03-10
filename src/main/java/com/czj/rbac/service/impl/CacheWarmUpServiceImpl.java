package com.czj.rbac.service.impl;

import com.czj.rbac.service.CacheWarmUpService;
import com.czj.rbac.service.SysUserService;
import com.czj.rbac.service.DataPermissionService;
import com.czj.rbac.model.query.UserQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class CacheWarmUpServiceImpl implements CacheWarmUpService {
    
    @Autowired
    private SysUserService userService;
    
    @Autowired
    private DataPermissionService dataPermissionService;
    
    @Override
    public void warmUpCaches() {
        Authentication originalAuth = SecurityContextHolder.getContext().getAuthentication();
        try {
            // 设置系统级别认证
            SecurityContextHolder.getContext().setAuthentication(createSystemAuthentication());
            
            // 执行预热
            warmUpUserCache();
            warmUpPermissionCache();
            
            log.info("缓存预热完成");
        } catch (Exception e) {
            log.error("缓存预热失败", e);
        } finally {
            // 恢复原有认证状态
            SecurityContextHolder.getContext().setAuthentication(originalAuth);
        }
    }
    
    /**
     * 预热用户缓存
     */
    private void warmUpUserCache() {
        log.info("开始预热用户缓存");
        UserQuery query = new UserQuery();
        query.setStatus(1);  // 只预热启用状态的用户
        query.setPage(1);
        query.setSize(100);  // 限制每批预热数量
        userService.findPage(query);
    }
    
    /**
     * 预热权限缓存
     */
    private void warmUpPermissionCache() {
        log.info("开始预热权限缓存");
        dataPermissionService.warmUpPermissionCache();
    }
    
    /**
     * 创建系统级别认证
     */
    private Authentication createSystemAuthentication() {
        return new UsernamePasswordAuthenticationToken(
            "SYSTEM",
            null,
            Collections.singleton(new SimpleGrantedAuthority("ROLE_SYSTEM"))
        );
    }

    @Override
    public void init() {
        // 系统启动时预热缓存
        warmUpCaches();
    }

    @Override
    public void destroy() {
        // 目前没有需要清理的资源
    }
} 