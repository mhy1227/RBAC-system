package com.czj.rbac.controller;

import com.czj.rbac.service.LoginInfoService;
import com.czj.rbac.model.LoginInfo;
import com.czj.rbac.common.Result;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.annotation.RequirePermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/login-info")
public class LoginInfoController {

    @Autowired
    private LoginInfoService loginInfoService;

    /**
     * 分页查询登录日志
     */
    @GetMapping("/page")
    @RequirePermission("sys:log:query")
    public Result<PageResult<LoginInfo>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String loginIp,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) Integer loginStatus) {
        
        PageResult<LoginInfo> result = loginInfoService.findPage(
            page, size, userId, username, loginIp, startTime, endTime, loginStatus);
        return Result.success(result);
    }

    /**
     * 获取用户最近一次登录记录
     */
    @GetMapping("/latest/{userId}")
    @RequirePermission("sys:log:query")
    public Result<LoginInfo> getLatestLogin(@PathVariable Long userId) {
        LoginInfo loginInfo = loginInfoService.findLatestByUserId(userId);
        return Result.success(loginInfo);
    }

    /**
     * 统计用户登录次数
     */
    @GetMapping("/count/{userId}")
    @RequirePermission("sys:log:query")
    public Result<Integer> countLoginTimes(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) Integer loginStatus) {
        
        int count = loginInfoService.countLoginTimes(userId, startTime, endTime, loginStatus);
        return Result.success(count);
    }

    /**
     * 清理过期日志
     */
    @DeleteMapping("/clean")
    @RequirePermission("sys:log:delete")
    public Result<Void> cleanExpiredLogs(@RequestParam(defaultValue = "30") Integer days) {
        loginInfoService.cleanExpiredLogs(days);
        return Result.success();
    }
} 