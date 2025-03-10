package com.czj.rbac.service.impl;

import com.czj.rbac.common.PageResult;
import org.springframework.beans.BeanUtils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseServiceImpl {

    /**
     * 对象转换
     */
    protected <T> T convert(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T target = BeanUtils.instantiateClass(targetClass);
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 列表转换
     */
    protected <T> List<T> convertList(List<?> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        return sourceList.stream()
            .map(source -> convert(source, targetClass))
            .collect(Collectors.toList());
    }

    /**
     * 分页处理
     */
    protected <T> PageResult<T> handlePage(List<T> list, Integer pageNum, Integer pageSize) {
        if (list == null || list.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }
        
        int total = list.size();
        int fromIndex = (pageNum - 1) * pageSize;
        if (fromIndex >= total) {
            return new PageResult<>(Collections.emptyList(), (long)total, pageNum, pageSize);
        }
        
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<T> pageList = list.subList(fromIndex, toIndex);
        
        return new PageResult<>(pageList, (long)total, pageNum, pageSize);
    }
} 