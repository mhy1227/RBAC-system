package com.czj.rbac.event;

import com.czj.rbac.model.enums.LogLevel;
import com.czj.rbac.model.enums.LogType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LogEvent {
    
    /**
     * 模块名称
     */
    private String module;
    
    /**
     * 操作类型
     */
    private String operation;
    
    /**
     * 日志内容
     */
    private String content;
    
    /**
     * 操作人ID
     */
    private Long operatorId;
    
    /**
     * 操作人名称
     */
    private String operatorName;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 错误信息
     */
    private String errorMsg;
    
    /**
     * 操作IP地址
     */
    private String ipAddress;
    
    /**
     * 日志级别
     */
    private LogLevel logLevel;
    
    /**
     * 日志类型
     */
    private LogType logType;
    
    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
} 