package com.czj.rbac.controller;

import com.czj.rbac.annotation.RequirePermission;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.common.Result;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import com.czj.rbac.model.dto.PermissionDTO;
import com.czj.rbac.model.query.PermissionQuery;
import com.czj.rbac.model.vo.PermissionVO;
import com.czj.rbac.service.SysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.czj.rbac.util.SecurityUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/permission")
public class SysPermissionController {

    @Autowired
    private SysPermissionService permissionService;

    @GetMapping("/{id}")
    @RequirePermission("sys:permission:query")
    public Result<PermissionVO> getById(@PathVariable Long id) {
        log.info("查询权限信息, id: {}", id);
        if (id == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "权限ID不能为空");
        }
        return Result.success(permissionService.findById(id));
    }

    @GetMapping("/page")
    @RequirePermission("sys:permission:query")
    public Result<PageResult<PermissionVO>> page(PermissionQuery query) {
        log.info("分页查询权限列表, query: {}", query);
        if (query == null) {
            query = new PermissionQuery();
        }
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10);
        }
        
        Long userId = SecurityUtils.getLoginUserId();
        
        if (!SecurityUtils.hasAdminPermission()) {
            query.setUserId(userId);
        }
        
        return Result.success(permissionService.findPage(query));
    }

    @GetMapping("/page/memory")
    @RequirePermission("sys:permission:query")
    public Result<PageResult<PermissionVO>> pageInMemory(PermissionQuery query) {
        log.info("内存分页查询权限列表, query: {}", query);
        if (query == null) {
            query = new PermissionQuery();
        }
        if (query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getSize() < 1) {
            query.setSize(10);
        }
        
        Long userId = SecurityUtils.getLoginUserId();
        
        if (!SecurityUtils.hasAdminPermission()) {
            query.setUserId(userId);
        }
        
        return Result.success(permissionService.findPageInMemory(query));
    }

    @GetMapping("/list")
    @RequirePermission("sys:permission:query")
    public Result<List<PermissionVO>> list(Integer status, String type) {
        log.info("查询权限列表, status: {}, type: {}", status, type);
        if (status != null && status != 0 && status != 1) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "无效的状态值");
        }
        return Result.success(permissionService.findList(status, type));
    }

    @GetMapping("/tree")
    @RequirePermission("sys:permission:query")
    public Result<List<PermissionVO>> tree(String type) {
        log.info("查询权限树, type: {}", type);
        return Result.success(permissionService.findPermissionTree(type));
    }

    @PostMapping
    @RequirePermission("sys:permission:add")
    public Result<Void> add(@RequestBody PermissionDTO permissionDTO) {
        log.info("新增权限: {}", permissionDTO);
        if (permissionDTO == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "权限信息不能为空");
        }
        if (StringUtils.isEmpty(permissionDTO.getPermissionName()) 
            || StringUtils.isEmpty(permissionDTO.getPermissionCode())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "权限名称和编码不能为空");
        }
        permissionService.add(permissionDTO);
        return Result.success();
    }

    @PutMapping
    @RequirePermission("sys:permission:update")
    public Result<Void> update(@RequestBody PermissionDTO permissionDTO) {
        log.info("更新权限: {}", permissionDTO);
        if (permissionDTO == null || permissionDTO.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "权限信息不能为空");
        }
        if (StringUtils.isEmpty(permissionDTO.getPermissionName()) 
            || StringUtils.isEmpty(permissionDTO.getPermissionCode())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "权限名称和编码不能为空");
        }
        permissionService.update(permissionDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("sys:permission:delete")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除权限: {}", id);
        if (id == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "权限ID不能为空");
        }
        permissionService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status/{status}")
    @RequirePermission("sys:permission:update")
    public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        log.info("更新权限状态: id={}, status={}", id, status);
        if (id == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "权限ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "无效的状态值");
        }
        permissionService.updateStatus(id, status);
        return Result.success();
    }
} 