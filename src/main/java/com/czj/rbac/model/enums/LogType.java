package com.czj.rbac.model.enums;

import lombok.Getter;

@Getter
public enum LogType {
    OPERATION("操作日志", 1),
    SECURITY("安全日志", 2),
    ERROR("错误日志", 3);
    
    private final String desc;
    private final int value;
    
    LogType(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }
    
    public static LogType getByValue(int value) {
        for (LogType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return OPERATION; // 默认返回操作日志
    }
} 