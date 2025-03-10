package com.czj.rbac.model.enums;

/**
 * 数据权限范围枚举
 * TODO: 目前仅使用ALL和SELF两种权限范围，其他类型预留
 */
public enum DataScope {
    ALL(1, "所有数据"),
    SELF(2, "仅本人数据");

    // TODO: 预留的数据权限范围，后续可能会用到
        /*
        DEPT_AND_CHILD(3, "本部门及以下数据"),
        DEPT(4, "本部门数据"),
        CUSTOM(5, "自定义数据");
        */

    private final int code;
    private final String desc;

    DataScope(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DataScope getByCode(int code) {
        for (DataScope scope : values()) {
            if (scope.getCode() == code) {
                return scope;
            }
        }
        return SELF; // 默认返回仅本人数据
    }
}