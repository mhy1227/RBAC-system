package com.czj.rbac.controller;

import com.czj.rbac.annotation.RequirePermission;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.common.Result;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import com.czj.rbac.model.SysLog;
import com.czj.rbac.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/log")
public class SysLogController {

    @Autowired
    private SysLogService logService;

    @GetMapping("/{id}")
    @RequirePermission("sys:log:query")
    public Result<SysLog> getById(@PathVariable Long id) {
        if (id == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "日志ID不能为空");
        }
        return Result.success(logService.findById(id));
    }

    @GetMapping("/page")
    @RequirePermission("sys:log:query")
    public Result<PageResult<SysLog>> page(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        // 设置默认分页参数
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        
        PageResult<SysLog> pageResult = logService.findPage(page, size, module, operation, startTime, endTime);
        return Result.success(pageResult);
    }
} 