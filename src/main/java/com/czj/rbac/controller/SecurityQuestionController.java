package com.czj.rbac.controller;

import com.czj.rbac.annotation.RequirePermission;
import com.czj.rbac.common.Result;
import com.czj.rbac.model.SecurityQuestionTemplate;
import com.czj.rbac.model.UserSecurity;
import com.czj.rbac.service.SecurityQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/security/question")
@RequiredArgsConstructor
public class SecurityQuestionController {

    private final SecurityQuestionService securityQuestionService;

    /**
     * 设置密保问题
     */
    @PostMapping("/set")
    public Result<Boolean> setSecurityQuestions(@RequestBody UserSecurity userSecurity) {
        return Result.success(securityQuestionService.setSecurityQuestions(userSecurity));
    }

    /**
     * 验证密保答案
     */
    @PostMapping("/verify/{userId}")
    public Result<Boolean> verifyAnswers(
            @PathVariable Long userId,
            @RequestBody List<String> answers) {
        return Result.success(securityQuestionService.verifyAnswers(userId, answers));
    }

    /**
     * 获取用户密保信息
     */
    @GetMapping("/{userId}")
    public Result<UserSecurity> getUserSecurity(@PathVariable Long userId) {
        return Result.success(securityQuestionService.getUserSecurity(userId));
    }

    /**
     * 重置密保问题错误次数
     */
    @PostMapping("/reset/{userId}")
    @RequirePermission("sys:security:reset")
    public Result<Void> resetErrorCount(@PathVariable Long userId) {
        securityQuestionService.resetErrorCount(userId);
        return Result.success();
    }

    /**
     * 获取密保问题模板列表
     */
    @GetMapping("/template/list")
    public Result<List<SecurityQuestionTemplate>> getTemplateList(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return Result.success(securityQuestionService.getTemplateList(type, status));
    }

    /**
     * 添加密保问题模板
     */
    @PostMapping("/template")
    @RequirePermission("sys:security:template:add")
    public Result<Boolean> addTemplate(@RequestBody SecurityQuestionTemplate template) {
        boolean result = securityQuestionService.addTemplate(template);
        return Result.success(result);
    }

    /**
     * 更新密保问题模板
     */
    @PutMapping("/template")
    @RequirePermission("sys:security:template:update")
    public Result<Boolean> updateTemplate(@RequestBody SecurityQuestionTemplate template) {
        boolean result = securityQuestionService.updateTemplate(template);
        return Result.success(result);
    }

    /**
     * 更新模板状态
     */
    @PutMapping("/template/{id}/status/{status}")
    @RequirePermission("sys:security:template:update")
    public Result<Boolean> updateTemplateStatus(
            @PathVariable Long id,
            @PathVariable Integer status) {
        boolean result = securityQuestionService.updateTemplateStatus(id, status);
        return Result.success(result);
    }
} 