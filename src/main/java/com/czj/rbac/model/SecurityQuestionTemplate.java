package com.czj.rbac.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 密保问题模板实体
 */
@Data
public class SecurityQuestionTemplate {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 问题内容
     */
    private String question;
    
    /**
     * 问题类型(1-常规 2-个人 3-工作)
     */
    private Integer type;
    
    /**
     * 状态(0-禁用 1-启用)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 