package com.czj.rbac.model;

import com.czj.rbac.model.enums.LogLevel;
import com.czj.rbac.model.enums.LogType;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统日志实体
 */
@Data
public class SysLog {
    
    /**
     * 主键ID
     */
    private Long id;
    
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
     * 是否成功(1-成功 0-失败)
     */
    private Boolean success;
    
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
     * 状态(1-正常 0-删除)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 