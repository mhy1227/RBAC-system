package com.czj.rbac.mapper;

import com.czj.rbac.model.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志Mapper
 */
@Mapper
public interface SysLogMapper {
    
    /**
     * 插入日志
     *
     * @param log 日志信息
     * @return 影响行数
     */
    int insert(SysLog log);
    
    /**
     * 批量插入日志
     *
     * @param logs 日志列表
     * @return 影响行数
     */
    int batchInsert(@Param("logs") List<SysLog> logs);
    
    /**
     * 根据ID查询日志
     *
     * @param id 日志ID
     * @return 日志信息
     */
    SysLog findById(@Param("id") Long id);
    
    /**
     * 分页查询日志
     *
     * @param offset 偏移量
     * @param limit 限制
     * @param module 模块名称
     * @param operation 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<SysLog> findPage(@Param("offset") int offset,
                         @Param("limit") int limit,
                         @Param("module") String module,
                         @Param("operation") String operation,
                         @Param("startTime") String startTime,
                         @Param("endTime") String endTime);
    
    /**
     * 统计日志数量
     *
     * @param module 模块名称
     * @param operation 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志数量
     */
    int count(@Param("module") String module,
             @Param("operation") String operation,
             @Param("startTime") String startTime,
             @Param("endTime") String endTime);

    /**
     * 删除过期日志
     *
     * @param expireTime 过期时间
     * @return 删除的记录数
     */
    int deleteExpiredLogs(@Param("expireTime") LocalDateTime expireTime);
} 