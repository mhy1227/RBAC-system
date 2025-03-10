package com.czj.rbac.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BaseModel {
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 