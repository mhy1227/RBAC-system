package com.czj.rbac.controller;

import com.czj.rbac.common.Result;
import com.czj.rbac.model.dto.LoginDTO;
import com.czj.rbac.model.vo.LoginVO;
import com.czj.rbac.service.AuthService;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import com.czj.rbac.util.SensitiveInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        log.info("用户登录：{}", loginDTO.getUsername());
        LoginVO loginVO = authService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return Result.success(loginVO);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/info")
    public Result<LoginVO> getCurrentUser() {
        return Result.success(authService.getCurrentUser());
    }
    
    @PostMapping("/refresh")
    public Result<LoginVO> refreshToken(@RequestParam String refreshToken) {
        return Result.success(authService.refreshToken(refreshToken));
    }
} 