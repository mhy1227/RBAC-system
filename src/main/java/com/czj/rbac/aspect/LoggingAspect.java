package com.czj.rbac.aspect;

import com.czj.rbac.annotation.LogOperation;
import com.czj.rbac.context.UserContext;
import com.czj.rbac.event.LogEvent;
import com.czj.rbac.model.enums.LogLevel;
import com.czj.rbac.model.enums.LogType;
import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Around("@annotation(logOperation)")
    public Object log(ProceedingJoinPoint point, LogOperation logOperation) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = point.getSignature().getName();
        
        try {
            // 执行目标方法
            Object result = point.proceed();
            
            // 记录成功日志
            publishLogEvent(logOperation, true, null);
            
            // 记录执行时间
            long endTime = System.currentTimeMillis();
            log.debug("方法执行完成: {}ms - {}", endTime - startTime, methodName);
            
            return result;
        } catch (Throwable e) {
            // 记录失败日志
            publishLogEvent(logOperation, false, e.getMessage());
            
            // 记录执行时间
            long endTime = System.currentTimeMillis();
            log.error("方法执行异常: {}ms - {} - {}", endTime - startTime, methodName, e.getMessage());
            
            throw e;
        }
    }

    private void publishLogEvent(LogOperation logOperation, boolean success, String errorMsg) {
        try {
            // 获取当前用户
            UserVO currentUser = UserContext.getCurrentUser();
            
            // 构建日志事件
            LogEvent event = LogEvent.builder()
                .module(logOperation.module())
                .operation(logOperation.operation())
                .content(logOperation.content())
                .success(success)
                .errorMsg(errorMsg)
                .operatorId(currentUser != null ? currentUser.getId() : null)
                .operatorName(currentUser != null ? currentUser.getUsername() : null)
                .ipAddress(getIpAddress())
                .logLevel(logOperation.level())
                .logType(LogType.OPERATION)
                .operateTime(LocalDateTime.now())
                .build();
            
            // 发布事件
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("发布日志事件失败: {}", e.getMessage());
        }
    }

    private String getIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) 
            RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return IpUtil.getIpAddress(request);
        }
        return null;
    }
} 