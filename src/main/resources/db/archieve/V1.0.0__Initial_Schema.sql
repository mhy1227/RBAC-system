-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(128) NOT NULL COMMENT '密码',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像URL',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态(1:启用,0:禁用)',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_name` varchar(50) NOT NULL COMMENT '角色名称',
    `role_code` varchar(50) NOT NULL COMMENT '角色编码',
    `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `permission_name` varchar(50) NOT NULL COMMENT '权限名称',
    `permission_code` varchar(50) NOT NULL COMMENT '权限编码',
    `description` varchar(200) DEFAULT NULL COMMENT '权限描述',
    `parent_id` bigint DEFAULT NULL COMMENT '父级ID',
    `type` varchar(20) NOT NULL COMMENT '类型(menu:菜单,button:按钮)',
    `path` varchar(200) DEFAULT NULL COMMENT '路径',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表'; 