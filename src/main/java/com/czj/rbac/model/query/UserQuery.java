package com.czj.rbac.model.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserQuery extends BaseQuery {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 状态（0-禁用 1-启用）
     */
    private Integer status;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    public UserQuery() {
        // 添加用户表允许排序的字段
        addAllowedOrderField("username");
        addAllowedOrderField("nickname");
        addAllowedOrderField("last_login_time");
    }
    
    @Override
    protected String getKeywordSql() {
        if (!StringUtils.hasText(getKeyword())) {
            return "1=1";
        }
        return String.format(
            "username LIKE '%%%s%%' OR " +
            "nickname LIKE '%%%s%%' OR " +
            "email LIKE '%%%s%%' OR " +
            "phone LIKE '%%%s%%'",
            getKeyword(), getKeyword(), getKeyword(), getKeyword()
        );
    }
    
    @Override
    public String getWhereSql() {
        StringBuilder whereSql = new StringBuilder(super.getWhereSql());
        
        if (StringUtils.hasText(username)) {
            whereSql.append(" AND username LIKE '%").append(username).append("%'");
        }
        if (StringUtils.hasText(nickname)) {
            whereSql.append(" AND nickname LIKE '%").append(nickname).append("%'");
        }
        if (status != null) {
            whereSql.append(" AND status = ").append(status);
        }
        if (StringUtils.hasText(email)) {
            whereSql.append(" AND email LIKE '%").append(email).append("%'");
        }
        if (StringUtils.hasText(phone)) {
            whereSql.append(" AND phone LIKE '%").append(phone).append("%'");
        }
        if (roleId != null) {
            whereSql.append(" AND EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id AND ur.role_id = ").append(roleId).append(")");
        }
        
        return whereSql.toString();
    }
} 