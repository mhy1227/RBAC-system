package com.czj.rbac.model.query;

import lombok.Data;

@Data
public class PageQuery {
    private Integer page = 1;
    private Integer size = 10;
} 