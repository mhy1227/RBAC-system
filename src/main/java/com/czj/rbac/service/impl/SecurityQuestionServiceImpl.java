package com.czj.rbac.service.impl;

import com.czj.rbac.mapper.SecurityQuestionMapper;
import com.czj.rbac.mapper.SysUserMapper;
import com.czj.rbac.model.SecurityQuestionTemplate;
import com.czj.rbac.model.UserSecurity;
import com.czj.rbac.service.SecurityQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service    
@RequiredArgsConstructor
public class SecurityQuestionServiceImpl implements SecurityQuestionService {

    private final SecurityQuestionMapper securityQuestionMapper;
    private final SysUserMapper userMapper;
    
    // 最大错误次数
    private static final int MAX_ERROR_COUNT = 5;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setSecurityQuestions(UserSecurity userSecurity) {
        if (userSecurity == null || userSecurity.getUserId() == null) {
            return false;
        }
        
        // 检查是否已设置密保
        UserSecurity existingSecurity = securityQuestionMapper.findByUserId(userSecurity.getUserId());
        boolean result;
        if (existingSecurity == null) {
            // 新增密保信息
            result = securityQuestionMapper.insertUserSecurity(userSecurity) > 0;
        } else {
            // 更新密保信息
            result = securityQuestionMapper.updateUserSecurity(userSecurity) > 0;
        }
        
        // 更新用户表密保状态
        if (result) {
            userMapper.updateSecurityStatus(userSecurity.getUserId(), 1);
        }
        
        return result;
    }

    @Override
    public boolean verifyAnswers(Long userId, List<String> answers) {
        if (userId == null || CollectionUtils.isEmpty(answers) || answers.size() != 3) {
            return false;
        }
        
        // 获取用户密保信息
        UserSecurity security = securityQuestionMapper.findByUserId(userId);
        if (security == null) {
            return false;
        }
        
        // 检查是否超过最大错误次数
        if (security.getErrorCount() != null && security.getErrorCount() >= MAX_ERROR_COUNT) {
            log.warn("用户{}密保验证次数超限", userId);
            return false;
        }
        
        // 验证答案
        boolean isCorrect = Objects.equals(answers.get(0), security.getAnswer1()) &&
                          Objects.equals(answers.get(1), security.getAnswer2()) &&
                          Objects.equals(answers.get(2), security.getAnswer3());
                          
        if (isCorrect) {
            // 重置错误次数
            securityQuestionMapper.resetErrorCount(userId);
        } else {
            // 更新错误次数
            int errorCount = security.getErrorCount() == null ? 1 : security.getErrorCount() + 1;
            securityQuestionMapper.updateErrorInfo(userId, errorCount);
        }
        
        return isCorrect;
    }

    @Override
    public UserSecurity getUserSecurity(Long userId) {
        return userId == null ? null : securityQuestionMapper.findByUserId(userId);
    }

    @Override
    public void resetErrorCount(Long userId) {
        if (userId != null) {
            securityQuestionMapper.resetErrorCount(userId);
        }
    }

    @Override
    public List<SecurityQuestionTemplate> getTemplateList(Integer type, Integer status) {
        return securityQuestionMapper.findTemplateList(type, status);
    }

    @Override
    public boolean addTemplate(SecurityQuestionTemplate template) {
        if (template == null || template.getQuestion() == null) {
            return false;
        }
        
        // 检查问题是否已存在
        if (securityQuestionMapper.checkQuestionExists(template.getQuestion()) > 0) {
            log.warn("密保问题已存在: {}", template.getQuestion());
            return false;
        }
        
        // 设置默认值
        template.setStatus(template.getStatus() == null ? 1 : template.getStatus());
        template.setType(template.getType() == null ? 1 : template.getType());
        
        return securityQuestionMapper.insertTemplate(template) > 0;
    }

    @Override
    public boolean updateTemplate(SecurityQuestionTemplate template) {
        if (template == null || template.getId() == null) {
            return false;
        }
        return securityQuestionMapper.updateTemplate(template) > 0;
    }

    @Override
    public boolean updateTemplateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            return false;
        }
        return securityQuestionMapper.updateTemplateStatus(id, status) > 0;
    }
} 