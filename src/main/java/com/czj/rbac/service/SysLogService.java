package com.czj.rbac.service;

import com.czj.rbac.common.PageResult;
import com.czj.rbac.model.SysLog;

/**
 * 系统日志服务
 */
public interface SysLogService {
    
    /**
     * 保存操作日志
     *
     * @param module 模块名称
     * @param operation 操作类型
     * @param content 操作内容
     */
    void saveLog(String module, String operation, String content);
    
    /**
     * 保存操作日志(带结果)
     *
     * @param module 模块名称
     * @param operation 操作类型
     * @param content 操作内容
     * @param success 是否成功
     * @param errorMsg 错误信息(可选)
     */
    void saveLog(String module, String operation, String content, boolean success, String errorMsg);

    /**
     * 根据ID查询日志
     *
     * @param id 日志ID
     * @return 日志信息
     */
    SysLog findById(Long id);

    /**
     * 分页查询日志
     *
     * @param page 页码
     * @param size 每页大小
     * @param module 模块名称
     * @param operation 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    PageResult<SysLog> findPage(Integer page, Integer size, String module, String operation, String startTime, String endTime);
} 