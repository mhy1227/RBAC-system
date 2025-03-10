package com.czj.rbac.controller;

import com.czj.rbac.annotation.RequirePermission;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.common.Result;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import com.czj.rbac.model.dto.RoleDTO;
import com.czj.rbac.model.query.RoleQuery;
import com.czj.rbac.model.vo.RoleVO;
import com.czj.rbac.service.SysRoleService;
import com.czj.rbac.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @GetMapping("/{id}")
    @RequirePermission("sys:role:query")
    public Result<RoleVO> getById(@PathVariable Long id) {
        log.info("查询角色信息, id: {}", id);
        if (id == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "角色ID不能为空");
        }
        return Result.success(roleService.findById(id));
    }

    @GetMapping("/page")
    @RequirePermission("sys:role:query")
    public Result<PageResult<RoleVO>> page(RoleQuery query) {
        log.info("分页查询角色列表, query: {}", query);
        if (query == null) {
            query = new RoleQuery();
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
        
        return Result.success(roleService.findPage(query));
    }

    @GetMapping("/list")
    @RequirePermission("sys:role:query")
    public Result<List<RoleVO>> list(Integer status) {
        log.info("查询角色列表, status: {}", status);
        if (status != null && status != 0 && status != 1) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "无效的状态值");
        }
        return Result.success(roleService.findList(status));
    }

    @PostMapping
    @RequirePermission("sys:role:add")
    public Result<Void> add(@RequestBody RoleDTO roleDTO) {
        log.info("新增角色: {}", roleDTO);
        if (roleDTO == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "角色信息不能为空");
        }
        if (StringUtils.isEmpty(roleDTO.getRoleName()) || StringUtils.isEmpty(roleDTO.getRoleCode())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "角色名称和编码不能为空");
        }
        roleService.add(roleDTO);
        return Result.success();
    }

    @PutMapping
    @RequirePermission("sys:role:update")
    public Result<Void> update(@RequestBody RoleDTO roleDTO) {
        log.info("更新角色: {}", roleDTO);
        if (roleDTO == null || roleDTO.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "角色信息不能为空");
        }
        if (StringUtils.isEmpty(roleDTO.getRoleName()) || StringUtils.isEmpty(roleDTO.getRoleCode())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "角色名称和编码不能为空");
        }
        roleService.update(roleDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("sys:role:delete")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除角色: {}", id);
        if (id == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "角色ID不能为空");
        }
        roleService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status/{status}")
    @RequirePermission("sys:role:update")
    public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        log.info("更新角色状态: id={}, status={}", id, status);
        if (id == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "角色ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "无效的状态值");
        }
        roleService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/{roleId}/permission")
    @RequirePermission("sys:role:assign")
    public Result<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        log.info("分配角色权限: roleId={}, permissionIds={}", roleId, permissionIds);
        if (roleId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "角色ID不能为空");
        }
        if (permissionIds == null) {
            permissionIds = Collections.emptyList();
        } else {
            permissionIds = permissionIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .collect(Collectors.toList());
        }
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success();
    }

    @GetMapping("/page/memory")
    @RequirePermission("sys:role:query")
    public Result<PageResult<RoleVO>> pageInMemory(RoleQuery query) {
        log.info("内存分页查询角色列表, query: {}", query);
        if (query == null) {
            query = new RoleQuery();
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
        
        return Result.success(roleService.findPageInMemory(query));
    }
} 