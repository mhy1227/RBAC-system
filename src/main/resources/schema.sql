-- 用户表
ALTER TABLE sys_user 
ADD COLUMN login_fail_count INT DEFAULT 0 COMMENT '登录失败次数',
ADD COLUMN last_login_time datetime COMMENT '最后登录时间',
ADD COLUMN lock_time datetime COMMENT '账号锁定时间';

-- 更新现有记录
UPDATE sys_user SET login_fail_count = 0; 


