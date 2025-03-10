-- 删除已存在的日志表(如果存在)
DROP TABLE IF EXISTS sys_log;

-- 创建系统日志表
CREATE TABLE sys_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    module VARCHAR(50) NOT NULL COMMENT '模块名称',
    operation VARCHAR(50) NOT NULL COMMENT '操作类型',
    content VARCHAR(500) DEFAULT NULL COMMENT '日志内容',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) DEFAULT NULL COMMENT '操作人名称',
    success TINYINT(1) DEFAULT 1 COMMENT '是否成功(1-成功 0-失败)',
    error_msg VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT '操作IP地址',
    log_level VARCHAR(10) DEFAULT 'INFO' COMMENT '日志级别(INFO/WARN/ERROR)',
    log_type VARCHAR(20) NOT NULL COMMENT '日志类型(OPERATION/SECURITY/ERROR)',
    status TINYINT(1) DEFAULT 1 COMMENT '状态(1-正常 0-删除)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_create_time (create_time),
    KEY idx_operator_id (operator_id),
    KEY idx_module_operation (module, operation),
    KEY idx_log_type_level (log_type, log_level),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- 添加测试数据
INSERT INTO sys_log 
(module, operation, content, operator_id, operator_name, log_type, log_level, status, create_time) 
VALUES 
('用户管理', '用户登录', '用户admin1登录成功', 1, 'admin1', 'SECURITY', 'INFO', 1, NOW()),
('角色管理', '新增角色', '管理员新增角色: 测试角色', 1, 'admin1', 'OPERATION', 'INFO', 1, NOW()),
('权限管理', '权限分配', '为角色[测试角色]分配权限', 1, 'admin1', 'OPERATION', 'INFO', 1, NOW()),
('系统管理', '系统异常', '数据库连接异常: Connection refused', NULL, 'system', 'ERROR', 'ERROR', 1, NOW()); 