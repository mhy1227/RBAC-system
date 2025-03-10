-- 更新所有用户的密码为123456的加密格式
UPDATE sys_user 
SET password = 'YWJjZGVmZ2hpamtsbW5vcCQ1ZWJlMmNmZjk3NTBjOTU4YWE1ZDVmN2ExOGYwZjZiZA=='
WHERE username IN ('admin', 'admin2', 'test_admin1', 'test_admin2', 'test_admin3', 'user1', 'user2', 'user3', 'user4', 'user5');

-- 重置登录失败次数和锁定时间
UPDATE sys_user 
SET login_fail_count = 0,
    lock_time = NULL; 