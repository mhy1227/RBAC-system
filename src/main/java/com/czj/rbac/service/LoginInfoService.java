package com.czj.rbac.service;

import com.czj.rbac.model.LoginInfo;
import com.czj.rbac.common.PageResult;
import java.time.LocalDateTime;

public interface LoginInfoService {
    /**
     * 记录登录信息
     */
    void recordLoginInfo(Long userId, String username, String loginId, boolean success, String failReason);
    
    /**
     * 更新登出时间
     */
    void recordLogout(Long userId, String loginId);
    
    /**
     * 分页查询登录日志
     */
    PageResult<LoginInfo> findPage(Integer page, Integer size, Long userId, String username,
                                 String loginIp, LocalDateTime startTime, LocalDateTime endTime,
                                 Integer loginStatus);
    
    /**
     * 获取用户最近一次登录记录
     */
    LoginInfo findLatestByUserId(Long userId);
    
    /**
     * 统计用户登录次数
     */
    int countLoginTimes(Long userId, LocalDateTime startTime, LocalDateTime endTime, Integer loginStatus);
    
    /**
     * 清理过期日志
     */
    void cleanExpiredLogs(int days);
} 