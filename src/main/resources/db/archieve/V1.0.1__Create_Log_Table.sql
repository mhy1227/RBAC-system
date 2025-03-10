CREATE TABLE IF NOT EXISTS `sys_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `module` varchar(50) NOT NULL COMMENT '模块名称',
    `operation` varchar(50) NOT NULL COMMENT '操作类型',
    `content` varchar(500) NOT NULL COMMENT '操作内容',
    `success` tinyint(1) DEFAULT '1' COMMENT '操作结果(1:成功,0:失败)',
    `error_msg` varchar(500) DEFAULT NULL COMMENT '错误信息',
    `operator_id` bigint(20) DEFAULT NULL COMMENT '操作用户ID',
    `operator_name` varchar(50) DEFAULT NULL COMMENT '操作用户名',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_module` (`module`),
    KEY `idx_operation` (`operation`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_operator_id` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表'; 