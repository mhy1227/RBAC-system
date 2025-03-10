package com.czj.rbac.model.enums;

import lombok.Getter;

@Getter
public enum LogLevel {
    INFO("信息", 1),
    WARN("警告", 2),
    ERROR("错误", 3);
    
    private final String desc;
    private final int value;
    
    LogLevel(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }
    
    public static LogLevel getByValue(int value) {
        for (LogLevel level : values()) {
            if (level.getValue() == value) {
                return level;
            }
        }
        return INFO; // 默认返回INFO级别
    }
} 