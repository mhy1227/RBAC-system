package com.czj.rbac.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;      // 后端返回的是 list
    private Long total;        // 总记录数
    private Integer pages;     // 总页数
    private Integer pageNum;   // 当前页码
    private Integer pageSize;  // 每页大小
    
    public PageResult(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }
} 