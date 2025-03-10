package com.czj.rbac.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class RoleDTO {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    private List<Long> permissionIds; // 权限ID列表
} 