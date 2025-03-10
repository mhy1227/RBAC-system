package com.czj.rbac.service.impl;

import com.czj.rbac.service.SysLogService;
import com.czj.rbac.mapper.SysLogMapper;
import com.czj.rbac.model.SysLog;
import com.czj.rbac.event.LogEvent;
import com.czj.rbac.context.UserContext;
import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SysLogServiceImpl implements SysLogService {
    
    @Autowired
    private SysLogMapper logMapper;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Value("${rbac.log.retention-days:30}")
    private int logRetentionDays;
    
    @Override
    public void saveLog(String module, String operation, String content) {
        // 原有的日志记录代码
        // UserVO currentUser = UserContext.getCurrentUser();
        // LogEvent event = LogEvent.builder()
        //     .module(module)
        //     .operation(operation)
        //     .content(content)
        //     .success(true)
        //     .operatorId(currentUser != null ? currentUser.getId() : null)
        //     .operatorName(currentUser != null ? currentUser.getUsername() : null)
        //     .build();
        // 
        // eventPublisher.publishEvent(event);
    }
    
    @Override
    public void saveLog(String module, String operation, String content, boolean success, String errorMsg) {
        // 原有的日志记录代码
        // UserVO currentUser = UserContext.getCurrentUser();
        // LogEvent event = LogEvent.builder()
        //     .module(module)
        //     .operation(operation)
        //     .content(content)
        //     .success(success)
        //     .errorMsg(errorMsg)
        //     .operatorId(currentUser != null ? currentUser.getId() : null)
        //     .operatorName(currentUser != null ? currentUser.getUsername() : null)
        //     .build();
        // 
        // eventPublisher.publishEvent(event);
    }

    @Override
    public SysLog findById(Long id) {
        if (id == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "日志ID不能为空");
        }
        SysLog log = logMapper.findById(id);
        if (log == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND, "日志不存在");
        }
        return log;
    }

    @Override
    public PageResult<SysLog> findPage(Integer page, Integer size, String module, String operation, String startTime, String endTime) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 查询数据
        List<SysLog> logs = logMapper.findPage(offset, size, module, operation, startTime, endTime);
        
        // 查询总数
        int total = logMapper.count(module, operation, startTime, endTime);
        
        // 返回分页结果
        return new PageResult<SysLog>(logs, Long.valueOf(total), page, size);
    }

    /**
     * 每天凌晨2点执行日志清理
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredLogs() {
        try {
            LocalDateTime expireTime = LocalDateTime.now().minusDays(logRetentionDays);
            int count = logMapper.deleteExpiredLogs(expireTime);
            log.info("清理过期日志完成，清理数量: {}", count);
        } catch (Exception e) {
            log.error("清理过期日志失败: {}", e.getMessage());
        }
    }
} 