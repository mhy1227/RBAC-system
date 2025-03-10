-- V1.0.15__Update_Permission_Structure.sql

-- 1. 修改权限表结构
ALTER TABLE sys_permission 
ADD COLUMN pid BIGINT COMMENT '父权限ID' AFTER id,
ADD COLUMN sort_order INT DEFAULT 0 COMMENT '排序号',
ADD INDEX idx_pid (pid);

-- 2. 清空现有权限数据
TRUNCATE TABLE sys_role_permission;
TRUNCATE TABLE sys_permission;

-- 3. 插入新的层级权限数据
-- 系统管理
INSERT INTO sys_permission (id, pid, permission_name, permission_code, description, type, sort_order, status, create_time) VALUES
(1, 0, '系统管理', 'sys', '系统管理模块', 1, 1, 1, NOW());

-- 用户管理
INSERT INTO sys_permission (id, pid, permission_name, permission_code, description, type, sort_order, status, create_time) VALUES
(2, 1, '用户管理', 'sys:user', '用户管理模块', 1, 1, 1, NOW()),
(3, 2, '查看用户', 'query', '查看用户列表及详情', 2, 1, 1, NOW()),
(4, 2, '新增用户', 'add', '新增用户', 2, 2, 1, NOW()),
(5, 2, '编辑用户', 'update', '修改用户信息', 2, 3, 1, NOW()),
(6, 2, '删除用户', 'delete', '删除用户', 2, 4, 1, NOW());

-- 角色管理
INSERT INTO sys_permission (id, pid, permission_name, permission_code, description, type, sort_order, status, create_time) VALUES
(7, 1, '角色管理', 'sys:role', '角色管理模块', 1, 2, 1, NOW()),
(8, 7, '查看角色', 'query', '查看角色列表及详情', 2, 1, 1, NOW()),
(9, 7, '新增角色', 'add', '新增角色', 2, 2, 1, NOW()),
(10, 7, '编辑角色', 'update', '修改角色信息', 2, 3, 1, NOW()),
(11, 7, '删除角色', 'delete', '删除角色', 2, 4, 1, NOW());

-- 权限管理
INSERT INTO sys_permission (id, pid, permission_name, permission_code, description, type, sort_order, status, create_time) VALUES
(12, 1, '权限管理', 'sys:permission', '权限管理模块', 1, 3, 1, NOW()),
(13, 12, '查看权限', 'query', '查看权限列表及详情', 2, 1, 1, NOW()),
(14, 12, '新增权限', 'add', '新增权限', 2, 2, 1, NOW()),
(15, 12, '编辑权限', 'update', '修改权限信息', 2, 3, 1, NOW()),
(16, 12, '删除权限', 'delete', '删除权限', 2, 4, 1, NOW());

-- 4. 重新分配角色权限
-- 超级管理员拥有所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 测试管理员拥有查看权限和部分操作权限
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES 
(2, 2), -- 用户管理模块
(2, 3), -- 查看用户
(2, 5), -- 编辑用户
(2, 7), -- 角色管理模块
(2, 8), -- 查看角色
(2, 12), -- 权限管理模块
(2, 13); -- 查看权限

-- 普通用户只有查看权限
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES 
(3, 2), -- 用户管理模块
(3, 3); -- 查看用户