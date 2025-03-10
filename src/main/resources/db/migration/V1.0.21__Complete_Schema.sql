-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '用户头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    login_fail_count INT DEFAULT 0 COMMENT '登录失败次数',
    lock_time DATETIME COMMENT '锁定时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_email (email),
    KEY idx_phone (phone),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) COMMENT '用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) COMMENT '角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(50) NOT NULL COMMENT '权限编码',
    description VARCHAR(200) DEFAULT NULL COMMENT '权限描述',
    pid BIGINT COMMENT '父权限ID',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    type VARCHAR(20) NOT NULL COMMENT '类型(menu:菜单,button:按钮)',
    path VARCHAR(200) DEFAULT NULL COMMENT '路径',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_permission_code (permission_code),
    KEY idx_pid (pid),
    KEY idx_status (status),
    KEY idx_type (type),
    KEY idx_create_time (create_time)
) COMMENT '权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id) COMMENT '用户角色唯一索引',
    KEY idx_role_id (role_id) COMMENT '角色ID索引'
) COMMENT '用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_permission (role_id, permission_id) COMMENT '角色权限唯一索引',
    KEY idx_permission_id (permission_id) COMMENT '权限ID索引'
) COMMENT '角色权限关联表';

-- 系统日志表
CREATE TABLE IF NOT EXISTS sys_log (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(50) COMMENT '操作',
    method VARCHAR(200) COMMENT '方法名',
    params TEXT COMMENT '参数',
    time BIGINT COMMENT '执行时长(毫秒)',
    ip VARCHAR(64) COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status TINYINT COMMENT '状态',
    error_message TEXT COMMENT '错误信息',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time),
    KEY idx_status (status)
) COMMENT '系统日志表';

-- 登录信息表
CREATE TABLE IF NOT EXISTS login_info (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    login_id VARCHAR(50) COMMENT '登录标识',
    login_ip VARCHAR(50) COMMENT '登录IP',
    login_time DATETIME COMMENT '登录时间',
    logout_time DATETIME COMMENT '登出时间',
    login_status TINYINT COMMENT '登录状态：0-失败，1-成功',
    fail_reason VARCHAR(200) COMMENT '失败原因',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_login_time (login_time),
    KEY idx_login_status (login_status),
    KEY idx_username (username)
) COMMENT '登录信息表';

-- 安全问题表
CREATE TABLE IF NOT EXISTS security_question (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    question_id BIGINT NOT NULL COMMENT '问题ID',
    answer VARCHAR(200) NOT NULL COMMENT '答案',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time)
) COMMENT '安全问题表';

-- 添加必要的索引
ALTER TABLE sys_user ADD INDEX idx_status (status) COMMENT '状态索引';
ALTER TABLE sys_permission ADD INDEX idx_pid (pid) COMMENT '父权限ID索引';
ALTER TABLE sys_log ADD INDEX idx_create_time (create_time) COMMENT '创建时间索引';
ALTER TABLE login_info ADD INDEX idx_user_id (user_id) COMMENT '用户ID索引';
ALTER TABLE login_info ADD INDEX idx_login_time (login_time) COMMENT '登录时间索引';

-- 添加外键约束（如果需要的话，取消注释以下内容）
/*
ALTER TABLE sys_user_role ADD CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES sys_user (id);
ALTER TABLE sys_user_role ADD CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES sys_role (id);
ALTER TABLE sys_role_permission ADD CONSTRAINT fk_role_permission_role_id FOREIGN KEY (role_id) REFERENCES sys_role (id);
ALTER TABLE sys_role_permission ADD CONSTRAINT fk_role_permission_permission_id FOREIGN KEY (permission_id) REFERENCES sys_permission (id);
*/

