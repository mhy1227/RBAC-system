package com.czj.rbac.service;

import com.czj.rbac.model.SecurityQuestionTemplate;
import com.czj.rbac.model.UserSecurity;
import java.util.List;

/**
 * 密保问题服务接口
 */
public interface SecurityQuestionService {
    
    /**
     * 设置用户密保问题
     *
     * @param userSecurity 用户密保信息
     * @return 是否成功
     */
    boolean setSecurityQuestions(UserSecurity userSecurity);
    
    /**
     * 验证密保问题答案
     *
     * @param userId 用户ID
     * @param answers 答案列表(按顺序)
     * @return 是否验证通过
     */
    boolean verifyAnswers(Long userId, List<String> answers);
    
    /**
     * 获取用户密保信息
     *
     * @param userId 用户ID
     * @return 密保信息
     */
    UserSecurity getUserSecurity(Long userId);
    
    /**
     * 重置密保问题错误次数
     *
     * @param userId 用户ID
     */
    void resetErrorCount(Long userId);
    
    /**
     * 获取密保问题模板列表
     *
     * @param type 问题类型
     * @param status 状态
     * @return 模板列表
     */
    List<SecurityQuestionTemplate> getTemplateList(Integer type, Integer status);
    
    /**
     * 添加密保问题模板
     *
     * @param template 模板信息
     * @return 是否成功
     */
    boolean addTemplate(SecurityQuestionTemplate template);
    
    /**
     * 更新密保问题模板
     *
     * @param template 模板信息
     * @return 是否成功
     */
    boolean updateTemplate(SecurityQuestionTemplate template);
    
    /**
     * 更新模板状态
     *
     * @param id 模板ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateTemplateStatus(Long id, Integer status);
} 