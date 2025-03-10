package com.czj.rbac.event;

import com.czj.rbac.mapper.SysLogMapper;
import com.czj.rbac.model.SysLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class LogEventListener {

    @Autowired
    private SysLogMapper logMapper;

    @Async("logTaskExecutor")
    @EventListener
    public void handleLogEvent(LogEvent event) {
        try {
            log.debug("开始处理日志事件: {}", event);
            
            // 转换为日志实体
            SysLog sysLog = new SysLog();
            sysLog.setModule(event.getModule());
            sysLog.setOperation(event.getOperation());
            sysLog.setContent(event.getContent());
            sysLog.setSuccess(event.isSuccess());
            sysLog.setErrorMsg(event.getErrorMsg());
            sysLog.setOperatorId(event.getOperatorId());
            sysLog.setOperatorName(event.getOperatorName());
            sysLog.setIpAddress(event.getIpAddress());
            sysLog.setLogLevel(event.getLogLevel());
            sysLog.setLogType(event.getLogType());
            sysLog.setStatus(1); // 默认状态为正常
            sysLog.setCreateTime(event.getOperateTime());
            sysLog.setUpdateTime(LocalDateTime.now());

            // 保存日志
            logMapper.insert(sysLog);
            
            log.debug("日志事件处理完成: {}", event);
        } catch (Exception e) {
            log.error("处理日志事件失败: {}", e.getMessage());
        }
    }
} 