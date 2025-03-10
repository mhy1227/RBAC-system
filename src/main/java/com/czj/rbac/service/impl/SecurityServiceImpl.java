package com.czj.rbac.service.impl;

import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.service.SecurityService;
import com.czj.rbac.service.SysUserService;
import com.czj.rbac.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 安全服务实现类
 */
@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private SysUserService userService;

    @Override
    public UserVO getCurrentUser() {
        Long userId = SecurityUtils.getLoginUserId();
        if (userId == null) {
            return null;
        }
        return userService.findById(userId);
    }

    @Override
    public Long getCurrentUserId() {
        return SecurityUtils.getLoginUserId();
    }

    @Override
    public boolean isSuperAdmin() {
              return SecurityUtils.hasAdminPermission();
    }
} 