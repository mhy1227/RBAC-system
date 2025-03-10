-- 插入角色数据
INSERT INTO sys_role (role_name, role_code, description, status, create_time)
VALUES 
('超级管理员', 'ROLE_SUPER_ADMIN', '拥有所有权限', 1, NOW()),
('测试管理员', 'ROLE_TEST_ADMIN', '拥有大部分权限', 1, NOW()),
('普通用户', 'ROLE_USER', '拥有基本权限', 1, NOW());

-- 插入测试用户数据
INSERT INTO sys_user (username, password, nickname, email, phone, status, create_time)
VALUES 
('admin1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '超级管理员1', 'admin1@example.com', '13800138001', 1, NOW()),
('admin2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '超级管理员2', 'admin2@example.com', '13800138002', 1, NOW()),
('test_admin1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理员1', 'test_admin1@example.com', '13800138003', 1, NOW()),
('test_admin2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理员2', 'test_admin2@example.com', '13800138004', 1, NOW()),
('test_admin3', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理员3', 'test_admin3@example.com', '13800138005', 1, NOW()),
('user1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '普通用户1', 'user1@example.com', '13800138006', 1, NOW()),
('user2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '普通用户2', 'user2@example.com', '13800138007', 1, NOW()),
('user3', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '普通用户3', 'user3@example.com', '13800138008', 1, NOW()),
('user4', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '普通用户4', 'user4@example.com', '13800138009', 1, NOW()),
('user5', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '普通用户5', 'user5@example.com', '13800138010', 1, NOW());

-- 插入用户角色关联数据
INSERT INTO sys_user_role (user_id, role_id)
VALUES 
(1, 1),  -- admin1 -> 超级管理员
(2, 1),  -- admin2 -> 超级管理员
(3, 2),  -- test_admin1 -> 测试管理员
(4, 2),  -- test_admin2 -> 测试管理员
(5, 2),  -- test_admin3 -> 测试管理员
(6, 3),  -- user1 -> 普通用户
(7, 3),  -- user2 -> 普通用户
(8, 3),  -- user3 -> 普通用户
(9, 3),  -- user4 -> 普通用户
(10, 3); -- user5 -> 普通用户

-- 插入权限数据
INSERT INTO sys_permission (permission_name, permission_code, description, status, create_time)
VALUES 
('查看用户', 'VIEW_USER', '查看用户权限', 1, NOW()),
('编辑用户', 'EDIT_USER', '编辑用户权限', 1, NOW()),
('删除用户', 'DELETE_USER', '删除用户权限', 1, NOW()),
('查看角色', 'VIEW_ROLE', '查看角色权限', 1, NOW()),
('编辑角色', 'EDIT_ROLE', '编辑角色权限', 1, NOW()),
('查看权限', 'VIEW_PERMISSION', '查看权限权限', 1, NOW());

-- 插入角色权限关联数据
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES 
(1, 1),  -- 超级管理员拥有查看用户权限
(1, 2),  -- 超级管理员拥有编辑用户权限
(1, 3),  -- 超级管理员拥有删除用户权限
(1, 4),  -- 超级管理员拥有查看角色权限
(1, 5),  -- 超级管理员拥有编辑角色权限
(1, 6),  -- 超级管理员拥有查看权限
(2, 1),  -- 测试管理员拥有查看用户权限
(2, 2),  -- 测试管理员拥有编辑用户权限
(3, 1);  -- 普通用户仅拥有查看用户权限 