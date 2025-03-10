-- 创建登录信息表
CREATE TABLE sys_login_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    login_id VARCHAR(50) NOT NULL COMMENT '登录标识',
    device_type VARCHAR(20) COMMENT '设备类型',
    device_info VARCHAR(200) COMMENT '设备信息',
    browser VARCHAR(50) COMMENT '浏览器',
    os VARCHAR(50) COMMENT '操作系统',
    login_ip VARCHAR(50) COMMENT '登录IP',
    login_location VARCHAR(100) COMMENT '登录地点',
    login_time DATETIME NOT NULL COMMENT '登录时间',
    logout_time DATETIME COMMENT '登出时间',
    login_status TINYINT COMMENT '登录状态(1-成功 0-失败)',
    fail_reason VARCHAR(200) COMMENT '失败原因',
    status TINYINT DEFAULT 1 COMMENT '状态(1-正常 0-删除)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_login_time (login_time),
    INDEX idx_login_ip (login_ip),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录信息记录表';

-- 添加测试数据
INSERT INTO sys_login_info 
(user_id, username, login_id, login_ip, browser, os, login_status, login_time) 
VALUES 
(1, 'admin1', UUID(), '127.0.0.1', 'Chrome', 'Windows', 1, NOW()),
(2, 'admin2', UUID(), '127.0.0.1', 'Firefox', 'MacOS', 1, NOW()),
(3, 'test_admin1', UUID(), '127.0.0.1', 'Edge', 'Windows', 0, NOW()); 