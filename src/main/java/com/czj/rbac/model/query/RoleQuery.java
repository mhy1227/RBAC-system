package com.czj.rbac.model.query;

import lombok.Data;

@Data
public class RoleQuery extends PageQuery {
    private String roleName;
    private String roleCode;
    private Integer status;
    private Long userId;  // 用于权限控制，查询用户拥有的角色
} 