# -- 初始化测试数据
# -- 1. 初始化角色
# INSERT INTO sys_role (role_name, role_code, description, status) VALUES
#                                                                      ('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1),
#                                                                      ('测试管理员', 'TEST_ADMIN', '测试管理员，拥有部分管理权限', 1),
#                                                                      ('普通用户', 'NORMAL_USER', '普通用户，仅有基本操作权限', 1);
#
# -- 2. 初始化用户
# INSERT INTO sys_user (username, password, nickname, email, phone, status) VALUES
# -- 超级管理员
# ('admin1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '超管一号', 'admin1@test.com', '13800000001', 1),
# ('admin2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '超管二号', 'admin2@test.com', '13800000002', 1),
# -- 测试管理员
# ('testadmin1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理A', 'test1@test.com', '13800000003', 1),
# ('testadmin2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理B', 'test2@test.com', '13800000004', 1),
# ('testadmin3', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理C', 'test3@test.com', '13800000005', 1),
# -- 普通用户
# ('user1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '用户一号', 'user1@test.com', '13800000006', 1),
# ('user2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '用户二号', 'user2@test.com', '13800000007', 1),
# ('user3', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '用户三号', 'user3@test.com', '13800000008', 1),
# ('user4', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '用户四号', 'user4@test.com', '13800000009', 1),
# ('user5', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '用户五号', 'user5@test.com', '13800000010', 1);
#
# -- 3. 用户角色关联
# INSERT INTO sys_user_role (user_id, role_id)
# SELECT u.id, r.id
# FROM sys_user u, sys_role r
# WHERE u.username IN ('admin1', 'admin2') AND r.role_code = 'SUPER_ADMIN'
# UNION
# SELECT u.id, r.id
# FROM sys_user u, sys_role r
# WHERE u.username IN ('testadmin1', 'testadmin2', 'testadmin3') AND r.role_code = 'TEST_ADMIN'
# UNION
# SELECT u.id, r.id
# FROM sys_user u, sys_role r
# WHERE u.username IN ('user1', 'user2', 'user3', 'user4', 'user5') AND r.role_code = 'NORMAL_USER';
-- 1. 初始化角色
INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1),
('测试管理员', 'TEST_ADMIN', '测试管理员，拥有部分管理权限', 1),
('普通用户', 'NORMAL_USER', '普通用户，仅有基本操作权限', 1),
('访客', 'VISITOR', '访客角色，只有查看权限', 1);

-- 2. 初始化权限
INSERT INTO sys_permission (permission_name, permission_code, description, pid, type, path, status, sort_order) VALUES
-- 系统管理
(N'系统管理', 'system:manage', N'系统管理目录', NULL, 'menu', '/system', 1, 1),
-- 用户管理
(N'用户管理', 'user:manage', N'用户管理目录', 1, 'menu', '/system/user', 1, 1),
(N'查看用户', 'user:view', N'查看用户信息', 2, 'button', NULL, 1, 1),
(N'新增用户', 'user:add', N'新增用户', 2, 'button', NULL, 1, 2),
(N'编辑用户', 'user:edit', N'编辑用户', 2, 'button', NULL, 1, 3),
(N'删除用户', 'user:delete', N'删除用户', 2, 'button', NULL, 1, 4),
(N'重置密码', 'user:reset:password', N'重置用户密码', 2, 'button', NULL, 1, 5),
(N'分配角色', 'user:assign:role', N'为用户分配角色', 2, 'button', NULL, 1, 6),
-- 角色管理
(N'角色管理', 'role:manage', N'角色管理目录', 1, 'menu', '/system/role', 1, 2),
(N'查看角色', 'role:view', N'查看角色信息', 9, 'button', NULL, 1, 1),
(N'新增角色', 'role:add', N'新增角色', 9, 'button', NULL, 1, 2),
(N'编辑角色', 'role:edit', N'编辑角色', 9, 'button', NULL, 1, 3),
(N'删除角色', 'role:delete', N'删除角色', 9, 'button', NULL, 1, 4),
(N'分配权限', 'role:assign:permission', N'为角色分配权限', 9, 'button', NULL, 1, 5),
-- 权限管理
(N'权限管理', 'permission:manage', N'权限管理目录', 1, 'menu', '/system/permission', 1, 3),
(N'查看权限', 'permission:view', N'查看权限信息', 15, 'button', NULL, 1, 1),
(N'新增权限', 'permission:add', N'新增权限', 15, 'button', NULL, 1, 2),
(N'编辑权限', 'permission:edit', N'编辑权限', 15, 'button', NULL, 1, 3),
(N'删除权限', 'permission:delete', N'删除权限', 15, 'button', NULL, 1, 4),
-- 日志管理
(N'日志管理', 'log:manage', N'日志管理目录', 1, 'menu', '/system/log', 1, 4),
(N'操作日志', 'log:operation', N'操作日志管理', 20, 'menu', '/system/log/operation', 1, 1),
(N'登录日志', 'log:login', N'登录日志管理', 20, 'menu', '/system/log/login', 1, 2),
(N'查看日志', 'log:view', N'查看日志信息', 20, 'button', NULL, 1, 3),
(N'删除日志', 'log:delete', N'删除日志', 20, 'button', NULL, 1, 4);

-- 3. 初始化用户
INSERT INTO sys_user (username, password, nickname, email, phone, status) VALUES
-- 超级管理员
('admin1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '超管一号', 'admin1@test.com', '13800000001', 1),
('admin2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '超管二号', 'admin2@test.com', '13800000002', 1),
-- 测试管理员
('testadmin1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理A', 'test1@test.com', '13800000003', 1),
('testadmin2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理B', 'test2@test.com', '13800000004', 1),
('testadmin3', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理C', 'test3@test.com', '13800000005', 1),
-- 普通用户
('user1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '用户一号', 'user1@test.com', '13800000006', 1),
('user2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '用户二号', 'user2@test.com', '13800000007', 1),
('user3', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '用户三号', 'user3@test.com', '13800000008', 1),
('user4', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '用户四号', 'user4@test.com', '13800000009', 1),
('user5', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '用户五号', 'user5@test.com', '13800000010', 1),
-- 访客用户
('visitor1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '访客一号', 'visitor1@test.com', '13800000011', 1),
('visitor2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '访客二号', 'visitor2@test.com', '13800000012', 1);

-- 4. 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) 
SELECT u.id, r.id 
FROM sys_user u, sys_role r 
WHERE u.username IN ('admin1', 'admin2') AND r.role_code = 'SUPER_ADMIN'
UNION
SELECT u.id, r.id 
FROM sys_user u, sys_role r 
WHERE u.username IN ('testadmin1', 'testadmin2', 'testadmin3') AND r.role_code = 'TEST_ADMIN'
UNION
SELECT u.id, r.id 
FROM sys_user u, sys_role r 
WHERE u.username IN ('user1', 'user2', 'user3', 'user4', 'user5') AND r.role_code = 'NORMAL_USER'
UNION
SELECT u.id, r.id 
FROM sys_user u, sys_role r 
WHERE u.username IN ('visitor1', 'visitor2') AND r.role_code = 'VISITOR';

-- 5. 角色权限关联
-- 超级管理员拥有所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'SUPER_ADMIN';

-- 测试管理员拥有查看权限和部分操作权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'TEST_ADMIN'
AND p.permission_code IN (
    'system:manage',
    'user:manage', 'user:view', 'user:add', 'user:edit', 'user:reset:password',
    'role:manage', 'role:view', 'role:add',
    'permission:manage', 'permission:view',
    'log:manage', 'log:view', 'log:operation', 'log:login'
);

-- 普通用户拥有基本操作权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'NORMAL_USER'
AND p.permission_code IN (
    'system:manage',
    'user:manage', 'user:view',
    'role:manage', 'role:view',
    'permission:manage', 'permission:view',
    'log:manage', 'log:view', 'log:operation', 'log:login'
);

-- 访客只有查看权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'VISITOR'
AND p.permission_code IN (
    'system:manage',
    'user:manage', 'user:view',
    'role:manage', 'role:view',
    'permission:manage', 'permission:view',
    'log:manage', 'log:view'
);