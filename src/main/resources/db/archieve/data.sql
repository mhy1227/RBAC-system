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