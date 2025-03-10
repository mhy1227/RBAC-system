package com.czj.rbac.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseModel {
    /**
     * 最大登录失败次数
     */
    public static final int MAX_LOGIN_FAIL_COUNT = 5;

    /**
     * 锁定时间(分钟)
     */
    public static final int LOCK_TIME_MINUTES = 30;

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private Integer loginFailCount;
    private LocalDateTime lastLoginTime;
    private LocalDateTime lockTime;

    /**
     * 检查账号是否被锁定
     */
    public boolean isLocked() {
        if (lockTime == null) {
            return false;
        }
        return lockTime.isAfter(LocalDateTime.now());
    }

    /**
     * 增加登录失败次数
     */
    public void increaseLoginFailCount() {
        this.loginFailCount = (this.loginFailCount == null ? 0 : this.loginFailCount) + 1;
        if (this.loginFailCount >= MAX_LOGIN_FAIL_COUNT) {
            this.lockTime = LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES);
        }
    }

    /**
     * 重置登录失败次数
     */
    public void resetLoginFail() {
        this.loginFailCount = 0;
        this.lockTime = null;
    }

    /**
     * 更新最后登录时间
     */
    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
    }
} 