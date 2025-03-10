package com.czj.rbac.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserLoginEvent extends ApplicationEvent {
    private final Long userId;
    private final String loginId;
    private final boolean success;
    private final String failureReason;

    public UserLoginEvent(Object source, Long userId, String loginId, boolean success, String failureReason) {
        super(source);
        this.userId = userId;
        this.loginId = loginId;
        this.success = success;
        this.failureReason = failureReason;
    }
} 