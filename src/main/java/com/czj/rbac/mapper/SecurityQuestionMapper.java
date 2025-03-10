package com.czj.rbac.mapper;

import com.czj.rbac.model.SecurityQuestionTemplate;
import com.czj.rbac.model.UserSecurity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SecurityQuestionMapper {
    /**
     * 查询用户密保信息
     */
    UserSecurity findByUserId(@Param("userId") Long userId);
    
    /**
     * 保存用户密保信息
     */
    int insertUserSecurity(UserSecurity userSecurity);
    
    /**
     * 更新用户密保信息
     */
    int updateUserSecurity(UserSecurity userSecurity);
    
    /**
     * 更新错误次数和时间
     */
    int updateErrorInfo(@Param("userId") Long userId, 
                       @Param("errorCount") Integer errorCount);
    
    /**
     * 重置错误次数
     */
    int resetErrorCount(@Param("userId") Long userId);
    
    /**
     * 查询密保问题模板列表
     */
    List<SecurityQuestionTemplate> findTemplateList(@Param("type") Integer type,
                                                  @Param("status") Integer status);
    
    /**
     * 新增密保问题模板
     */
    int insertTemplate(SecurityQuestionTemplate template);
    
    /**
     * 更新密保问题模板
     */
    int updateTemplate(SecurityQuestionTemplate template);
    
    /**
     * 更新模板状态
     */
    int updateTemplateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 检查问题是否存在
     */
    int checkQuestionExists(@Param("question") String question);
} 