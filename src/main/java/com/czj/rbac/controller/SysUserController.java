package com.czj.rbac.controller;

import com.czj.rbac.annotation.RequirePermission;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.common.Result;
import com.czj.rbac.model.dto.UserDTO;
import com.czj.rbac.model.query.UserQuery;
import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.czj.rbac.util.SecurityUtils;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Value("${rbac.upload.avatar.path:/upload/avatar/}")
    private String avatarPath;

    @Autowired
    private SysUserService userService;

    @GetMapping("/page")
    @RequirePermission("sys:user:query")
    public Result<PageResult<UserVO>> page(UserQuery query) {
        return Result.success(userService.findPage(query));
    }

    @GetMapping("/{id}")
    @RequirePermission("sys:user:query")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.findById(id));
    }

    @PostMapping
    @RequirePermission("sys:user:add")
    public Result<Void> add(@RequestBody UserDTO userDTO) {
        userService.add(userDTO);
        return Result.success();
    }

    @PutMapping
    @RequirePermission("sys:user:update")
    public Result<Void> update(@RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("sys:user:delete")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status/{status}")
    @RequirePermission("sys:user:update")
    public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/{id}/role")
    @RequirePermission("sys:user:assign:role")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success();
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserDTO userDTO) {
        Long currentUserId = SecurityUtils.getLoginUserId();
        if (!currentUserId.equals(userDTO.getId())) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "只能修改自己的信息");
        }
        userService.updateProfile(userDTO);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(
            @RequestParam Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        Long currentUserId = SecurityUtils.getLoginUserId();
        if (!currentUserId.equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "只能修改自己的密码");
        }
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @PutMapping("/{id}/password/reset")
    @RequirePermission("sys:user:reset:password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "请选择头像文件");
        }
        
        // 2. 验证文件类型
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "只能上传图片文件");
        }
        
        // 3. 生成文件名
        String fileName = UUID.randomUUID().toString() + 
            file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        
        // 4. 保存文件
        try {
            String filePath = avatarPath + fileName;
            File dest = new File(filePath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
            
            // 5. 更新用户头像
            Long userId = SecurityUtils.getLoginUserId();
            UserDTO userDTO = new UserDTO();
            userDTO.setId(userId);
            userDTO.setAvatar(filePath);
            userService.updateProfile(userDTO);
            
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("上传头像失败", e);
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "上传头像失败");
        }
    }

    @GetMapping("/page/memory")
    @RequirePermission("sys:user:query")
    public Result<PageResult<UserVO>> pageInMemory(UserQuery query) {
        log.info("内存分页查询用户列表, query: {}", query);
        if (query == null) {
            query = new UserQuery();
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
        
        return Result.success(userService.findPageInMemory(query));
    }
} 