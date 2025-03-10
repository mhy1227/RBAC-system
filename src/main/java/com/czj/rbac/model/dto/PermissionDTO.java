package com.czj.rbac.model.dto;

import lombok.Data;

@Data
public class PermissionDTO {
    private Long id;
    private String permissionName;
    private String permissionCode;
    private String description;
    private Long parentId;
    private String type;
    private String path;
    private Integer status;
} 