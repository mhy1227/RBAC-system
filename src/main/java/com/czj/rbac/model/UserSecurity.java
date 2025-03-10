package com.czj.rbac.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户密保信息实体
 */
@Data
public class UserSecurity {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 密保问题1
     */
    private String question1;
    
    /**
     * 密保答案1
     */
    private String answer1;
    
    /**
     * 密保问题2
     */
    private String question2;
    
    /**
     * 密保答案2
     */
    private String answer2;
    
    /**
     * 密保问题3
     */
    private String question3;
    
    /**
     * 密保答案3
     */
    private String answer3;
    
    /**
     * 答错次数
     */
    private Integer errorCount;
    
    /**
     * 最后一次答错时间
     */
    private LocalDateTime lastErrorTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 