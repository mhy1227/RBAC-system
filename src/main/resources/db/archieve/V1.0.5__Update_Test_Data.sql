-- 清理旧数据
DELETE FROM sys_user_role;
DELETE FROM sys_role_permission;
DELETE FROM sys_user;
DELETE FROM sys_role;
DELETE FROM sys_permission;

-- 初始化权限数据
INSERT INTO sys_permission (permission_name, permission_code, description, type, status) VALUES
('系统管理', 'sys:admin', '系统管理权限', 'menu', 1),
('用户查询', 'sys:user:query', '用户查询权限', 'button', 1),
('用户新增', 'sys:user:add', '用户新增权限', 'button', 1),
('用户修改', 'sys:user:update', '用户修改权限', 'button', 1),
('用户删除', 'sys:user:delete', '用户删除权限', 'button', 1),
('角色查询', 'sys:role:query', '角色查询权限', 'button', 1),
('角色新增', 'sys:role:add', '角色新增权限', 'button', 1),
('角色修改', 'sys:role:update', '角色修改权限', 'button', 1),
('角色删除', 'sys:role:delete', '角色删除权限', 'button', 1),
('权限查询', 'sys:permission:query', '权限查询权限', 'button', 1),
('权限新增', 'sys:permission:add', '权限新增权限', 'button', 1),
('权限修改', 'sys:permission:update', '权限修改权限', 'button', 1),
('权限删除', 'sys:permission:delete', '权限删除权限', 'button', 1),
('日志查询', 'sys:log:query', '日志查询权限', 'button', 1);

-- 初始化角色数据
INSERT INTO sys_role (role_name, role_code, description, status, role_level) VALUES
('超级管理员', 'ROLE_SUPER_ADMIN', '超级管理员，拥有所有权限', 1, 100),
('管理员', 'ROLE_ADMIN', '管理员，拥有大部分权限', 1, 80),
('普通用户', 'ROLE_USER', '普通用户，拥有基本权限', 1, 10);

-- 初始化用户数据 (密码: 123456)
INSERT INTO sys_user (username, password, nickname, email, phone, status, login_fail_count) VALUES
-- 超级管理员
('admin', 'dGVzdHNhbHQkMTIzNDU2Nzg5MGFiY2RlZiQ1ZWJlMmNmZjk3NTBjOTU4YWE1ZDVmN2ExOGYwZjZiZA==', '超级管理员1', 'admin@example.com', '13800138000', 1, 0),
('admin2', 'dGVzdHNhbHQkMTIzNDU2Nzg5MGFiY2RlZiQ1ZWJlMmNmZjk3NTBjOTU4YWE1ZDVmN2ExOGYwZjZiZA==', '超级管理员2', 'admin2@example.com', '13800138001', 1, 0),

-- 测试用户(管理员)
('test_admin1', 'dGVzdHNhbHQkMTIzNDU2Nzg5MGFiY2RlZiQ1ZWJlMmNmZjk3NTBjOTU4YWE1ZDVmN2ExOGYwZjZiZA==', '测试管理员1', 'test_admin1@example.com', '13800138002', 1, 0),
('test_admin2', 'dGVzdHNhbHQkMTIzNDU2Nzg5MGFiY2RlZiQ1ZWJlMmNmZjk3NTBjOTU4YWE1ZDVmN2ExOGYwZjZiZA==', '测试管理员2', 'test_admin2@example.com', '13800138003', 1, 0),
('test_admin3', 'dGVzdHNhbHQkMTIzNDU2Nzg5MGFiY2RlZiQ1ZWJlMmNmZjk3NTBjOTU4YWE1ZDVmN2ExOGYwZjZiZA==', '测试管理员3', 'test_admin3@example.com', '13800138004', 1, 0),

-- 普通用户
('user1', 'dGVzdHNhbHQkMTIzNDU2Nzg5MGFiY2RlZiQ1ZWJlMmNmZjk3NTBjOTU4YWE1ZDVmN2ExOGYwZjZiZA==', '普通用户1', 'user1@example.com', '13800138005', 1, 0),
('user2', 'dGVzdHNhbHQkMTIzNDU2Nzg5MGFiY2RlZiQ1ZWJlMmNmZjk3NTBjOTU4YWE1ZDVmN2ExOGYwZjZiZA==', '普通用户2', 'user2@example.com', '13800138006', 1, 0),
('user3', 'dGVzdHNhbHQkMTIzNDU2Nzg5MGFiY2RlZiQ1ZWJlMmNmZjk3NTBjOTU4YWE1ZDVmN2ExOGYwZjZiZA==', '普通用户3', 'user3@example.com', '13800138007', 1, 0),
('user4', 'dGVzdHNhbHQkMTIzNDU2Nzg5MGFiY2RlZiQ1ZWJlMmNmZjk3NTBjOTU4YWE1ZDVmN2ExOGYwZjZiZA==', '普通用户4', 'user4@example.com', '13800138008', 1, 0),
('user5', 'dGVzdHNhbHQkMTIzNDU2Nzg5MGFiY2RlZiQ1ZWJlMmNmZjk3NTBjOTU4YWE1ZDVmN2ExOGYwZjZiZA==', '普通用户5', 'user5@example.com', '13800138009', 1, 0);

-- 分配角色权限关系
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_SUPER_ADMIN';

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_ADMIN'
AND p.permission_code != 'sys:admin';

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_USER'
AND p.permission_code IN ('sys:user:query', 'sys:role:query', 'sys:permission:query');

-- 分配用户角色关系
-- 超级管理员
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u, sys_role r
WHERE u.username IN ('admin', 'admin2') 
AND r.role_code = 'ROLE_SUPER_ADMIN';

-- 测试管理员
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u, sys_role r
WHERE u.username LIKE 'test_admin%'
AND r.role_code = 'ROLE_ADMIN';

-- 普通用户
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u, sys_role r
WHERE u.username LIKE 'user%'
AND r.role_code = 'ROLE_USER'; 