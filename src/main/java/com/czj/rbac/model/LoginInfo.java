package com.czj.rbac.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 登录信息实体
 */
@Data
public class LoginInfo {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 登录标识
     */
    private String loginId;
    
    /**
     * 设备类型
     */
    private String deviceType;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * 浏览器
     */
    private String browser;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * 登录IP
     */
    private String loginIp;
    
    /**
     * 登录地点
     */
    private String loginLocation;
    
    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
    
    /**
     * 登出时间
     */
    private LocalDateTime logoutTime;
    
    /**
     * 登录状态(1-成功 0-失败)
     */
    private Integer loginStatus;
    
    /**
     * 失败原因
     */
    private String failReason;
    
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