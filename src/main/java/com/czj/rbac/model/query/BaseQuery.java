package com.czj.rbac.model.query;

import lombok.Data;
import org.springframework.util.StringUtils;
import java.util.*;

@Data
public class BaseQuery {
    /**
     * 当前页码（从1开始）
     */
    private int page = 1;
    
    /**
     * 每页大小
     */
    private int size = 10;
    
    /**
     * 排序字段
     */
    private String orderBy;
    
    /**
     * 排序方向（ASC/DESC）
     */
    private String orderDirection = "DESC";
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
    
    /**
     * 搜索关键字
     */
    private String keyword;
    
    /**
     * 高级搜索条件
     */
    private List<SearchCondition> conditions;
    
    /**
     * 是否需要导出
     */
    private boolean needExport = false;
    
    /**
     * 导出的字段列表
     */
    private List<String> exportFields;
    
    /**
     * 允许的排序字段集合
     */
    private static final Set<String> ALLOWED_ORDER_FIELDS = new HashSet<>();
    
    /**
     * 允许导出的字段集合
     */
    private static final Set<String> ALLOWED_EXPORT_FIELDS = new HashSet<>();
    
    static {
        // 添加允许排序的字段
        ALLOWED_ORDER_FIELDS.add("id");
        ALLOWED_ORDER_FIELDS.add("create_time");
        ALLOWED_ORDER_FIELDS.add("update_time");
        ALLOWED_ORDER_FIELDS.add("status");
        
        // 添加允许导出的字段
        ALLOWED_EXPORT_FIELDS.add("id");
        ALLOWED_EXPORT_FIELDS.add("create_time");
        ALLOWED_EXPORT_FIELDS.add("update_time");
        ALLOWED_EXPORT_FIELDS.add("status");
    }
    
    /**
     * 搜索条件类
     */
    @Data
    public static class SearchCondition {
        /**
         * 字段名
         */
        private String field;
        
        /**
         * 操作符（eq, ne, gt, lt, like, in等）
         */
        private String operator;
        
        /**
         * 值
         */
        private Object value;
        
        /**
         * 连接符（AND/OR）
         */
        private String connector = "AND";
    }
    
    /**
     * 获取偏移量
     */
    public int getOffset() {
        return (page - 1) * size;
    }
    
    /**
     * 校验并修正分页参数
     */
    public void validate() {
        // 页码最小为1
        this.page = Math.max(1, this.page);
        // 每页大小限制在1-100之间
        this.size = Math.min(100, Math.max(1, this.size));
        // 排序方向只能是ASC或DESC
        if (!StringUtils.hasText(this.orderDirection) || 
            (!this.orderDirection.equalsIgnoreCase("ASC") && 
             !this.orderDirection.equalsIgnoreCase("DESC"))) {
            this.orderDirection = "DESC";
        }
        
        // 验证排序字段是否在允许的范围内
        if (StringUtils.hasText(this.orderBy) && !ALLOWED_ORDER_FIELDS.contains(this.orderBy.toLowerCase())) {
            this.orderBy = "create_time"; // 默认按创建时间排序
        }
        
        // 验证时间格式
        if (StringUtils.hasText(this.startTime) && !isValidDateFormat(this.startTime)) {
            this.startTime = null;
        }
        if (StringUtils.hasText(this.endTime) && !isValidDateFormat(this.endTime)) {
            this.endTime = null;
        }
        
        // 验证导出字段
        if (needExport && exportFields != null) {
            exportFields.removeIf(field -> !ALLOWED_EXPORT_FIELDS.contains(field.toLowerCase()));
        }
        
        // 验证高级搜索条件
        validateSearchConditions();
    }
    
    /**
     * 验证高级搜索条件
     */
    private void validateSearchConditions() {
        if (conditions == null) {
            return;
        }
        
        // 移除无效的搜索条件
        conditions.removeIf(condition -> 
            !StringUtils.hasText(condition.getField()) ||
            !StringUtils.hasText(condition.getOperator()) ||
            condition.getValue() == null ||
            !ALLOWED_ORDER_FIELDS.contains(condition.getField().toLowerCase()) ||
            !isValidOperator(condition.getOperator())
        );
    }
    
    /**
     * 验证操作符是否有效
     */
    private boolean isValidOperator(String operator) {
        Set<String> validOperators = new HashSet<>(Arrays.asList(
            "eq", "ne", "gt", "lt", "ge", "le", "like", "in", "not in"
        ));
        return validOperators.contains(operator.toLowerCase());
    }
    
    /**
     * 获取排序SQL片段
     */
    public String getOrderBySql() {
        if (!StringUtils.hasText(orderBy) || !ALLOWED_ORDER_FIELDS.contains(orderBy.toLowerCase())) {
            return "ORDER BY create_time DESC";
        }
        return String.format("ORDER BY %s %s", orderBy, orderDirection);
    }
    
    /**
     * 获取查询条件SQL片段
     */
    public String getWhereSql() {
        StringBuilder whereSql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        
        // 添加时间范围条件
        if (StringUtils.hasText(startTime)) {
            whereSql.append(" AND create_time >= ?");
            params.add(startTime);
        }
        if (StringUtils.hasText(endTime)) {
            whereSql.append(" AND create_time <= ?");
            params.add(endTime);
        }
        
        // 添加关键字搜索条件
        if (StringUtils.hasText(keyword)) {
            whereSql.append(" AND (").append(getKeywordSql()).append(")");
        }
        
        // 添加高级搜索条件
        if (conditions != null && !conditions.isEmpty()) {
            whereSql.append(" AND (");
            for (int i = 0; i < conditions.size(); i++) {
                SearchCondition condition = conditions.get(i);
                if (i > 0) {
                    whereSql.append(" ").append(condition.getConnector()).append(" ");
                }
                whereSql.append(buildConditionSql(condition));
            }
            whereSql.append(")");
        }
        
        return whereSql.toString();
    }
    
    /**
     * 构建单个条件的SQL片段
     */
    private String buildConditionSql(SearchCondition condition) {
        String field = condition.getField();
        String operator = condition.getOperator().toLowerCase();
        Object value = condition.getValue();
        
        switch (operator) {
            case "eq":
                return field + " = " + formatValue(value);
            case "ne":
                return field + " != " + formatValue(value);
            case "gt":
                return field + " > " + formatValue(value);
            case "lt":
                return field + " < " + formatValue(value);
            case "ge":
                return field + " >= " + formatValue(value);
            case "le":
                return field + " <= " + formatValue(value);
            case "like":
                return field + " LIKE '%" + value + "%'";
            case "in":
                return field + " IN (" + formatInValues(value) + ")";
            case "not in":
                return field + " NOT IN (" + formatInValues(value) + ")";
            default:
                return "1=1";
        }
    }
    
    /**
     * 格式化SQL值
     */
    private String formatValue(Object value) {
        if (value instanceof String) {
            return "'" + ((String) value).replace("'", "''") + "'";
        }
        return String.valueOf(value);
    }
    
    /**
     * 格式化IN查询的值
     */
    private String formatInValues(Object value) {
        if (value instanceof Collection) {
            return String.join(",", 
                ((Collection<?>) value).stream()
                    .map(this::formatValue)
                    .toArray(String[]::new)
            );
        }
        return formatValue(value);
    }
    
    /**
     * 获取关键字搜索SQL片段，子类需要重写此方法
     */
    protected String getKeywordSql() {
        return "1=1";
    }
    
    /**
     * 添加允许排序的字段
     */
    protected void addAllowedOrderField(String field) {
        if (StringUtils.hasText(field)) {
            ALLOWED_ORDER_FIELDS.add(field.toLowerCase());
        }
    }
    
    /**
     * 添加允许导出的字段
     */
    protected void addAllowedExportField(String field) {
        if (StringUtils.hasText(field)) {
            ALLOWED_EXPORT_FIELDS.add(field.toLowerCase());
        }
    }
    
    /**
     * 验证日期格式
     */
    private boolean isValidDateFormat(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2}:\\d{2})?");
    }
} 