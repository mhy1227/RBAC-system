package com.czj.rbac.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SysPermission {
    private Long id;
    private String permissionName;
    private String permissionCode;
    private String description;
    private Long parentId;
    private String type;
    private String path;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Long pid;
    private Integer sortOrder;
    private List<SysPermission> children;
    private String parentName;
    private boolean hasChildren;
} 