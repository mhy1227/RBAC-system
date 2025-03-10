package com.czj.rbac.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionVO {
    private Long id;
    private String permissionName;
    private String permissionCode;
    private String description;
    private Long parentId;
    private String type;
    private String path;
    private Integer status;
    private LocalDateTime createTime;
    private List<PermissionVO> children; // 子权限列表

    public PermissionVO(Long id, String permissionName, String permissionCode) {
        this.id = id;
        this.permissionName = permissionName;
        this.permissionCode = permissionCode;
    }
} 