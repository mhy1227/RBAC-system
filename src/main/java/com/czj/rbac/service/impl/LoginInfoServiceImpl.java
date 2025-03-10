package com.czj.rbac.service.impl;

import com.czj.rbac.service.LoginInfoService;
import com.czj.rbac.mapper.LoginInfoMapper;
import com.czj.rbac.model.LoginInfo;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class LoginInfoServiceImpl implements LoginInfoService {

    @Autowired
    private LoginInfoMapper loginInfoMapper;
    
    @Autowired
    private HttpServletRequest request;

    @Async
    @Override
    public void recordLoginInfo(Long userId, String username, String loginId, boolean success, String failReason) {
        try {
            LoginInfo info = new LoginInfo();
            info.setUserId(userId);
            info.setUsername(username);
            info.setLoginId(loginId);
            info.setLoginIp(IpUtil.getIpAddress(request));
            info.setLoginTime(LocalDateTime.now());
            info.setLoginStatus(success ? 1 : 0);
            info.setFailReason(failReason);
            
            // TODO: 获取设备信息、浏览器信息等
            // 可以通过User-Agent解析获取
            
            loginInfoMapper.insert(info);
        } catch (Exception e) {
            log.error("记录登录信息失败: {}", e.getMessage());
        }
    }

    @Async
    @Override
    public void recordLogout(Long userId, String loginId) {
        try {
            loginInfoMapper.updateLogoutTime(userId, loginId, LocalDateTime.now());
        } catch (Exception e) {
            log.error("记录登出时间失败: {}", e.getMessage());
        }
    }

    @Override
    public PageResult<LoginInfo> findPage(Integer page, Integer size, Long userId, String username,
                                        String loginIp, LocalDateTime startTime, LocalDateTime endTime,
                                        Integer loginStatus) {
        // 计算分页参数
        int offset = (page - 1) * size;
        
        // 查询数据
        List<LoginInfo> list = loginInfoMapper.findPage(offset, size, userId, username,
                loginIp, startTime, endTime, loginStatus);
        
        // 查询总数
        int total = loginInfoMapper.count(userId, username, loginIp, startTime, endTime, loginStatus);
        
        return new PageResult<>(list, Long.valueOf(total), page, size);
    }

    @Override
    public LoginInfo findLatestByUserId(Long userId) {
        return loginInfoMapper.findLatestByUserId(userId);
    }

    @Override
    public int countLoginTimes(Long userId, LocalDateTime startTime, LocalDateTime endTime, Integer loginStatus) {
        return loginInfoMapper.countLoginTimes(userId, startTime, endTime, loginStatus);
    }

    @Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨2点执行
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cleanExpiredLogs(int days) {
        try {
            LocalDateTime time = LocalDateTime.now().minusDays(days);
            int count = loginInfoMapper.deleteBeforeTime(time);
            log.info("清理{}天前的登录日志{}条", days, count);
        } catch (Exception e) {
            log.error("清理过期登录日志失败: {}", e.getMessage());
        }
    }
} 