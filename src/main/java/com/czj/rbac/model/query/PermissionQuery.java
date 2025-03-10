package com.czj.rbac.model.query;

import lombok.Data;

@Data
public class PermissionQuery extends PageQuery {
    private Integer page = 1;
    private Integer size = 10;
    private String permissionName;
    private String permissionCode;
    private String type;
    private Integer status;
    private Long userId;  // 用于权限控制，查询用户拥有的权限
} 