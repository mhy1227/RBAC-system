package com.czj.rbac.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleVO {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private List<PermissionVO> permissions; // 权限列表
} 