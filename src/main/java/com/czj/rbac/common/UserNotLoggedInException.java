package com.czj.rbac.common;

public class UserNotLoggedInException extends RuntimeException {
    public UserNotLoggedInException() {
        super("当前用户未登录");
    }
}
