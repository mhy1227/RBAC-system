package com.czj.rbac.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserTokenInvalidationEvent extends ApplicationEvent {
    private final Long userId;
    private final String reason;

    public UserTokenInvalidationEvent(Object source, Long userId, String reason) {
        super(source);
        this.userId = userId;
        this.reason = reason;
    }
} 