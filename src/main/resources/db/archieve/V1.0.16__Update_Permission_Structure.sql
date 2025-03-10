-- V1.0.16__Update_Permission_Structure.sql

-- 1. 修改权限表结构
ALTER TABLE sys_permission 
ADD COLUMN pid BIGINT COMMENT '父权限ID' AFTER id,
ADD COLUMN sort_order INT DEFAULT 0 COMMENT '排序号',
ADD INDEX idx_pid (pid);

-- 2. 清空现有权限数据
TRUNCATE TABLE sys_role_permission;
TRUNCATE TABLE sys_permission;

-- 3. 插入新的层级权限数据
-- 系统管理模块 (1-100)
INSERT INTO sys_permission (id, pid, permission_name, permission_code, description, type, sort_order, status, create_time) VALUES
(1, 0, '系统管理', 'sys', '系统管理模块', 1, 1, 1, NOW());

-- 用户管理模块 (101-200)
INSERT INTO sys_permission (id, pid, permission_name, permission_code, description, type, sort_order, status, create_time) VALUES
(101, 1, '用户管理', 'sys:user', '用户管理模块', 1, 1, 1, NOW()),
(102, 101, '查看用户', 'query', '查看用户列表及详情', 2, 1, 1, NOW()),
(103, 101, '新增用户', 'add', '新增用户', 2, 2, 1, NOW()),
(104, 101, '编辑用户', 'update', '修改用户信息', 2, 3, 1, NOW()),
(105, 101, '删除用户', 'delete', '删除用户', 2, 4, 1, NOW());

-- 角色管理模块 (201-300)
INSERT INTO sys_permission (id, pid, permission_name, permission_code, description, type, sort_order, status, create_time) VALUES
(201, 1, '角色管理', 'sys:role', '角色管理模块', 1, 2, 1, NOW()),
(202, 201, '查看角色', 'query', '查看角色列表及详情', 2, 1, 1, NOW()),
(203, 201, '新增角色', 'add', '新增角色', 2, 2, 1, NOW()),
(204, 201, '编辑角色', 'update', '修改角色信息', 2, 3, 1, NOW()),
(205, 201, '删除角色', 'delete', '删除角色', 2, 4, 1, NOW());

-- 权限管理模块 (301-400)
INSERT INTO sys_permission (id, pid, permission_name, permission_code, description, type, sort_order, status, create_time) VALUES
(301, 1, '权限管理', 'sys:permission', '权限管理模块', 1, 3, 1, NOW()),
(302, 301, '查看权限', 'query', '查看权限列表及详情', 2, 1, 1, NOW()),
(303, 301, '新增权限', 'add', '新增权限', 2, 2, 1, NOW()),
(304, 301, '编辑权限', 'update', '修改权限信息', 2, 3, 1, NOW()),
(305, 301, '删除权限', 'delete', '删除权限', 2, 4, 1, NOW());

-- 日志管理模块 (401-500)
INSERT INTO sys_permission (id, pid, permission_name, permission_code, description, type, sort_order, status, create_time) VALUES
(401, 1, '日志管理', 'sys:log', '日志管理模块', 1, 4, 1, NOW()),
(402, 401, '查看日志', 'query', '查看操作日志', 2, 1, 1, NOW()),
(403, 401, '删除日志', 'delete', '删除操作日志', 2, 2, 1, NOW());

-- 预留500-1000给系统模块的扩展
-- 预留1001以后给业务模块

-- 4. 重新分配角色权限
-- 超级管理员拥有所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 测试管理员拥有查看权限和部分操作权限
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES 
(2, 101), -- 用户管理模块
(2, 102), -- 查看用户
(2, 104), -- 编辑用户
(2, 201), -- 角色管理模块
(2, 202), -- 查看角色
(2, 301), -- 权限管理模块
(2, 302), -- 查看权限
(2, 401), -- 日志管理模块
(2, 402); -- 查看日志

-- 普通用户只有查看权限
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES 
(3, 101), -- 用户管理模块
(3, 102), -- 查看用户
(3, 401), -- 日志管理模块
(3, 402); -- 查看日志
