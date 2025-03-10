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
                                          `login_fail_count` int DEFAULT '0' COMMENT '登录失败次数',
                                          `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
                                          `lock_time` datetime DEFAULT NULL COMMENT '账号锁定时间',
                                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `uk_username` (`username`),
                                          INDEX `idx_email` (`email`),
                                          INDEX `idx_phone` (`phone`),
                                          KEY `idx_status` (`status`),
                                          KEY `idx_create_time` (`create_time`)
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
                                                UNIQUE KEY `uk_permission_code` (`permission_code`),
                                                INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
                                               `user_id` bigint NOT NULL COMMENT '用户ID',
                                               `role_id` bigint NOT NULL COMMENT '角色ID',
                                               PRIMARY KEY (`user_id`,`role_id`),
                                               CONSTRAINT `fk_user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
                                               CONSTRAINT `fk_user_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
                                                     `role_id` bigint NOT NULL COMMENT '角色ID',
                                                     `permission_id` bigint NOT NULL COMMENT '权限ID',
                                                     PRIMARY KEY (`role_id`,`permission_id`),
                                                     CONSTRAINT `fk_role_perm_role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
                                                     CONSTRAINT `fk_role_perm_perm_id` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 系统日志表
CREATE TABLE IF NOT EXISTS `sys_log` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `module` varchar(50) NOT NULL COMMENT '模块名称',
                                         `operation` varchar(50) NOT NULL COMMENT '操作类型',
                                         `content` TEXT NOT NULL COMMENT '操作内容',
                                         `success` tinyint(1) DEFAULT '1' COMMENT '操作结果(1:成功,0:失败)',
                                         `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
                                         `operator_id` bigint DEFAULT NULL COMMENT '操作用户ID',
                                         `operator_name` varchar(50) DEFAULT NULL COMMENT '操作用户名',
                                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         PRIMARY KEY (`id`),
                                         KEY `idx_module` (`module`),
                                         KEY `idx_operation` (`operation`),
                                         KEY `idx_create_time` (`create_time`),
                                         KEY `idx_operator_id` (`operator_id`),
                                         CONSTRAINT `fk_log_operator_id` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

-- 初始化用户数据
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `phone`, `status`) VALUES
                                                                                            ('admin', 'MTIzNDU2JDEyMzQ1Njc4OTAxMjM0NQ==$MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=', '超级管理员', 'admin@example.com', '13800000000', 1),
                                                                                            ('test', 'MTIzNDU2JDEyMzQ1Njc4OTAxMjM0NQ==$MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=', '测试用户', 'test@example.com', '13800000001', 1),
                                                                                            ('user1', 'MTIzNDU2JDEyMzQ1Njc4OTAxMjM0NQ==$MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=', '普通用户1', 'user1@example.com', '13800000002', 1),
                                                                                            ('user2', 'MTIzNDU2JDEyMzQ1Njc4OTAxMjM0NQ==$MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=', '普通用户2', 'user2@example.com', '13800000003', 1)
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 初始化角色数据
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `status`) VALUES
                                                                               ('超级管理员', 'ROLE_SUPER_ADMIN', '系统超级管理员', 1),
                                                                               ('系统管理员', 'ROLE_ADMIN', '系统管理员', 1),
                                                                               ('测试人员', 'ROLE_TEST', '测试人员', 1),
                                                                               ('普通用户', 'ROLE_USER', '普通用户', 1)
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 初始化菜单权限
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `type`, `path`, `status`) VALUES
                                                                                                  ('系统管理', 'sys:admin', 'menu', '/system', 1),
                                                                                                  ('用户管理', 'sys:user', 'menu', '/system/user', 1),
                                                                                                  ('角色管理', 'sys:role', 'menu', '/system/role', 1),
                                                                                                  ('权限管理', 'sys:permission', 'menu', '/system/permission', 1),
                                                                                                  ('日志管理', 'sys:log', 'menu', '/system/log', 1)
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 初始化按钮权限
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '查询用户', 'sys:user:query', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:user'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '新增用户', 'sys:user:add', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:user'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '修改用户', 'sys:user:update', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:user'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '删除用户', 'sys:user:delete', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:user'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '分配角色', 'sys:user:assign:role', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:user'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '重置密码', 'sys:user:reset:password', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:user'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '查询角色', 'sys:role:query', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:role'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '新增角色', 'sys:role:add', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:role'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '修改角色', 'sys:role:update', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:role'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '删除角色', 'sys:role:delete', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:role'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '分配权限', 'sys:role:assign', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:role'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '查询权限', 'sys:permission:query', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:permission'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '新增权限', 'sys:permission:add', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:permission'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '修改权限', 'sys:permission:update', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:permission'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '删除权限', 'sys:permission:delete', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:permission'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `parent_id`, `type`, `status`)
SELECT '查询日志', 'sys:log:query', id, 'button', 1 FROM `sys_permission` WHERE `permission_code` = 'sys:log'
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 初始化用户角色关联关系
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `sys_user` u, `sys_role` r
WHERE u.username = 'admin' AND r.role_code = 'ROLE_SUPER_ADMIN'
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`);

INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `sys_user` u, `sys_role` r
WHERE u.username = 'test' AND r.role_code = 'ROLE_TEST'
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`);

INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `sys_user` u, `sys_role` r
WHERE u.username IN ('user1', 'user2') AND r.role_code = 'ROLE_USER'
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`);

-- 为超级管理员角色分配所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `sys_role` r, `sys_permission` p
WHERE r.role_code = 'ROLE_SUPER_ADMIN'
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 为系统管理员分配除权限管理外的所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `sys_role` r, `sys_permission` p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.permission_code NOT LIKE 'sys:permission%'
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 为测试人员分配查询权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `sys_role` r, `sys_permission` p
WHERE r.role_code = 'ROLE_TEST'
  AND p.permission_code LIKE '%:query'
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 为普通用户分配基本查询权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `sys_role` r, `sys_permission` p
WHERE r.role_code = 'ROLE_USER'
  AND p.permission_code IN ('sys:user:query')
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);





-- 清理数据
DELETE FROM sys_role_permission;
DELETE FROM sys_user_role;
DELETE FROM sys_permission;
DELETE FROM sys_role;
DELETE FROM sys_user;

-- 重新添加完整数据
INSERT INTO sys_user (id, username, password, nickname, email, phone, status, create_time) VALUES
                                                                                               (1, 'admin', '123456', '管理员', 'admin@example.com', '13800138000', 1, NOW()),
                                                                                               (2, 'test', '123456', '测试用户', 'test@example.com', '13800138001', 1, NOW());

-- 初始化角色数据
INSERT INTO sys_role (id, role_name, role_code, description, status, create_time) VALUES
                                                                                      (1, '超级管理员', 'ROLE_ADMIN', '系统超级管理员', 1, NOW()),
                                                                                      (2, '普通用户', 'ROLE_USER', '普通用户', 1, NOW());

-- 初始化权限数据
INSERT INTO sys_permission (id, permission_name, permission_code, description, parent_id, type, path, status, create_time) VALUES
-- 系统管理
(1, '系统管理', 'sys:manage', '系统管理', 0, 'menu', '/system', 1, NOW()),

-- 用户管理
(2, '用户管理', 'sys:user:manage', '用户管理', 1, 'menu', '/system/user', 1, NOW()),
(3, '用户查询', 'sys:user:query', '用户查询', 2, 'button', '', 1, NOW()),
(4, '用户添加', 'sys:user:add', '用户添加', 2, 'button', '', 1, NOW()),
(5, '用户修改', 'sys:user:update', '用户修改', 2, 'button', '', 1, NOW()),
(6, '用户删除', 'sys:user:delete', '用户删除', 2, 'button', '', 1, NOW()),

-- 角色管理
(7, '角色管理', 'sys:role:manage', '角色管理', 1, 'menu', '/system/role', 1, NOW()),
(8, '角色查询', 'sys:role:query', '角色查询', 7, 'button', '', 1, NOW()),
(9, '角色添加', 'sys:role:add', '角色添加', 7, 'button', '', 1, NOW()),
(10, '角色修改', 'sys:role:update', '角色修改', 7, 'button', '', 1, NOW()),
(11, '角色删除', 'sys:role:delete', '角色删除', 7, 'button', '', 1, NOW()),

-- 权限管理
(12, '权限管理', 'sys:permission:manage', '权限管理', 1, 'menu', '/system/permission', 1, NOW()),
(13, '权限查询', 'sys:permission:query', '权限查询', 12, 'button', '', 1, NOW()),
(14, '权限添加', 'sys:permission:add', '权限添加', 12, 'button', '', 1, NOW()),
(15, '权限修改', 'sys:permission:update', '权限修改', 12, 'button', '', 1, NOW()),
(16, '权限删除', 'sys:permission:delete', '权限删除', 12, 'button', '', 1, NOW());

-- 初始化用户角色关联数据
INSERT INTO sys_user_role (user_id, role_id) VALUES
                                                 (1, 1),  -- admin -> 超级管理员
                                                 (2, 2);  -- test -> 普通用户

-- 初始化角色权限关联数据
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
-- 超级管理员拥有所有权限
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),  -- 系统管理+用户管理及按钮
(1, 7), (1, 8), (1, 9), (1, 10), (1, 11),        -- 角色管理及按钮
(1, 12), (1, 13), (1, 14), (1, 15), (1, 16),     -- 权限管理及按钮

-- 普通用户只有查询权限
(2, 1),  -- 系统管理
(2, 2), (2, 3),  -- 用户管理+查询
(2, 7), (2, 8),  -- 角色管理+查询
(2, 12), (2, 13);  -- 权限管理+查询

-- 初始化角色数据
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `status`, `create_time`, `update_time`) VALUES
                                                                                                             ('超级管理员', 'ROLE_SUPER_ADMIN', '系统超级管理员', 1, NOW(), NOW()),
                                                                                                             ('系统管理员', 'ROLE_ADMIN', '系统管理员', 1, NOW(), NOW()),
                                                                                                             ('测试人员', 'ROLE_TEST', '测试人员', 1, NOW(), NOW()),
                                                                                                             ('普通用户', 'ROLE_USER', '普通用户', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `update_time` = VALUES(`update_time`);

-- 初始化用户数据
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `phone`, `status`, `create_time`, `update_time`) VALUES
-- 管理员用户
('admin', '$2a$10$VQ.Rj7bc8D.UH7p.kCyLUOJj7jKs2jTYGyrTcL9q8kOZgR8aUa6yG', '超级管理员', 'admin@example.com', '13800000001', 1, NOW(), NOW()),
('admin2', '$2a$10$VQ.Rj7bc8D.UH7p.kCyLUOJj7jKs2jTYGyrTcL9q8kOZgR8aUa6yG', '系统管理员2', 'admin2@example.com', '13800000002', 1, NOW(), NOW()),
('admin3', '$2a$10$VQ.Rj7bc8D.UH7p.kCyLUOJj7jKs2jTYGyrTcL9q8kOZgR8aUa6yG', '系统管理员3', 'admin3@example.com', '13800000003', 1, NOW(), NOW()),

-- 测试用户
('test1', '$2a$10$VQ.Rj7bc8D.UH7p.kCyLUOJj7jKs2jTYGyrTcL9q8kOZgR8aUa6yG', '测试用户1', 'test1@example.com', '13800000004', 1, NOW(), NOW()),
('test2', '$2a$10$VQ.Rj7bc8D.UH7p.kCyLUOJj7jKs2jTYGyrTcL9q8kOZgR8aUa6yG', '测试用户2', 'test2@example.com', '13800000005', 1, NOW(), NOW()),
('test3', '$2a$10$VQ.Rj7bc8D.UH7p.kCyLUOJj7jKs2jTYGyrTcL9q8kOZgR8aUa6yG', '测试用户3', 'test3@example.com', '13800000006', 1, NOW(), NOW()),

-- 普通用户
('user1', '$2a$10$VQ.Rj7bc8D.UH7p.kCyLUOJj7jKs2jTYGyrTcL9q8kOZgR8aUa6yG', '普通用户1', 'user1@example.com', '13800000007', 1, NOW(), NOW()),
('user2', '$2a$10$VQ.Rj7bc8D.UH7p.kCyLUOJj7jKs2jTYGyrTcL9q8kOZgR8aUa6yG', '普通用户2', 'user2@example.com', '13800000008', 1, NOW(), NOW()),
('user3', '$2a$10$VQ.Rj7bc8D.UH7p.kCyLUOJj7jKs2jTYGyrTcL9q8kOZgR8aUa6yG', '普通用户3', 'user3@example.com', '13800000009', 1, NOW(), NOW()),
('user4', '$2a$10$VQ.Rj7bc8D.UH7p.kCyLUOJj7jKs2jTYGyrTcL9q8kOZgR8aUa6yG', '普通用户4', 'user4@example.com', '13800000010', 1, NOW(), NOW()),
('user5', '$2a$10$VQ.Rj7bc8D.UH7p.kCyLUOJj7jKs2jTYGyrTcL9q8kOZgR8aUa6yG', '普通用户5', 'user5@example.com', '13800000011', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `update_time` = VALUES(`update_time`);

-- 初始化用户角色关系
-- 为admin分配超级管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `sys_user` u, `sys_role` r
WHERE u.username = 'admin' AND r.role_code = 'ROLE_SUPER_ADMIN'
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`);

-- 为admin2和admin3分配系统管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `sys_user` u, `sys_role` r
WHERE u.username IN ('admin2', 'admin3') AND r.role_code = 'ROLE_ADMIN'
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`);

-- 为test1、test2、test3分配测试角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `sys_user` u, `sys_role` r
WHERE u.username IN ('test1', 'test2', 'test3') AND r.role_code = 'ROLE_TEST'
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`);

-- 为user1到user5分配普通用户角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `sys_user` u, `sys_role` r
WHERE u.username IN ('user1', 'user2', 'user3', 'user4', 'user5') AND r.role_code = 'ROLE_USER'
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`);

-- 初始化权限数据
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `description`, `type`, `status`, `create_time`, `update_time`) VALUES
-- 用户管理权限
('用户查询', 'sys:user:query', '查询用户列表、用户详情', 'MENU', 1, NOW(), NOW()),
('用户新增', 'sys:user:add', '新增用户', 'BUTTON', 1, NOW(), NOW()),
('用户编辑', 'sys:user:update', '修改用户信息', 'BUTTON', 1, NOW(), NOW()),
('用户删除', 'sys:user:delete', '删除用户', 'BUTTON', 1, NOW(), NOW()),
('分配角色', 'sys:user:assign:role', '为用户分配角色', 'BUTTON', 1, NOW(), NOW()),

-- 角色管理权限
('角色查询', 'sys:role:query', '查询角色列表、角色详情', 'MENU', 1, NOW(), NOW()),
('角色新增', 'sys:role:add', '新增角色', 'BUTTON', 1, NOW(), NOW()),
('角色编辑', 'sys:role:update', '修改角色信息', 'BUTTON', 1, NOW(), NOW()),
('角色删除', 'sys:role:delete', '删除角色', 'BUTTON', 1, NOW(), NOW()),
('角色授权', 'sys:role:assign', '为角色分配权限', 'BUTTON', 1, NOW(), NOW()),

-- 权限管理权限
('权限查询', 'sys:permission:query', '查询权限列表、权限详情', 'MENU', 1, NOW(), NOW()),
('权限新增', 'sys:permission:add', '新增权限', 'BUTTON', 1, NOW(), NOW()),
('权限编辑', 'sys:permission:update', '修改权限信息', 'BUTTON', 1, NOW(), NOW()),
('权限删除', 'sys:permission:delete', '删除权限', 'BUTTON', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `update_time` = VALUES(`update_time`);

-- 为超级管理员角色分配所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `sys_role` r, `sys_permission` p
WHERE r.role_code = 'ROLE_SUPER_ADMIN'
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 为系统管理员分配除权限管理外的所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `sys_role` r, `sys_permission` p
WHERE r.role_code = 'ROLE_ADMIN'
  AND p.permission_code NOT LIKE 'sys:permission%'
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 为测试人员分配查询权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `sys_role` r, `sys_permission` p
WHERE r.role_code = 'ROLE_TEST'
  AND p.permission_code LIKE '%:query'
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 为普通用户分配基本查询权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `sys_role` r, `sys_permission` p
WHERE r.role_code = 'ROLE_USER'
  AND p.permission_code IN ('sys:user:query')
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);


