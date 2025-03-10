package com.czj.rbac.listener;

import com.czj.rbac.event.UserLoginEvent;
import com.czj.rbac.event.UserTokenInvalidationEvent;
import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.service.LoginInfoService;
import com.czj.rbac.service.SysUserService;
import com.czj.rbac.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventListener {
    
    @Autowired
    private SysUserService userService;
    
    @Autowired
    private LoginInfoService loginInfoService;
    
    @Autowired
    private TokenService tokenService;

    @EventListener
    public void handleUserLoginEvent(UserLoginEvent event) {
        try {
            UserVO user = userService.findById(event.getUserId());
            loginInfoService.recordLoginInfo(
                event.getUserId(),
                user.getUsername(),
                event.getLoginId(),
                event.isSuccess(),
                event.getFailureReason()
            );
        } catch (Exception e) {
            log.error("处理用户登录事件失败", e);
        }
    }

    @EventListener
    public void handleUserTokenInvalidationEvent(UserTokenInvalidationEvent event) {
        try {
            tokenService.removeToken(event.getUserId());
            log.info("用户token已失效 - userId: {}, reason: {}", event.getUserId(), event.getReason());
        } catch (Exception e) {
            log.error("处理用户token失效事件失败", e);
        }
    }
} 