package com.czj.rbac.interceptor;

import com.czj.rbac.context.UserContext;
import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.service.SysUserService;
import com.czj.rbac.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Autowired
    private SysUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            Long userId = JwtUtil.getCurrentUserId();
            if (userId != null) {
                UserVO user = userService.findById(userId);
                if (user != null) {
                    UserContext.setUser(user);
                }
            }
        } catch (Exception e) {
            log.warn("设置用户上下文失败: {}", e.getMessage());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
} 