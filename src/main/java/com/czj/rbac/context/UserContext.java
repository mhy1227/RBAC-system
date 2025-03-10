package com.czj.rbac.context;

import com.czj.rbac.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserContext {
    private static final ThreadLocal<UserVO> userHolder = new ThreadLocal<>();

    public static void setUser(UserVO user) {
        userHolder.set(user);
    }

    public static UserVO getCurrentUser() {
        return userHolder.get();
    }

    public static Long getCurrentUserId() {
        UserVO user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    public static void clear() {
        userHolder.remove();
    }
} 