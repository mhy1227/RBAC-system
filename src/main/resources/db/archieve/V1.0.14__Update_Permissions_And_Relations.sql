-- 清空权限相关数据
SET FOREIGN_KEY_CHECKS = 0;  -- 临时禁用外键检查
TRUNCATE TABLE sys_role_permission;
TRUNCATE TABLE sys_permission;
SET FOREIGN_KEY_CHECKS = 1;  -- 重新启用外键检查

-- 重置权限表自增ID
ALTER TABLE sys_permission AUTO_INCREMENT = 1;

-- 插入权限数据
INSERT INTO sys_permission (permission_name, permission_code, description, status, type, create_time)
VALUES 
-- 用户管理权限
('查看用户', 'sys:user:query', '查看用户列表、详情权限', 1, 1, NOW()),      -- 1表示菜单权限
('新增用户', 'sys:user:add', '新增用户权限', 1, 2, NOW()),                  -- 2表示按钮权限
('编辑用户', 'sys:user:update', '修改用户权限', 1, 2, NOW()),               -- 2表示按钮权限
('删除用户', 'sys:user:delete', '删除用户权限', 1, 2, NOW()),               -- 2表示按钮权限

-- 角色管理权限
('查看角色', 'sys:role:query', '查看角色列表、详情权限', 1, 1, NOW()),      -- 1表示菜单权限
('新增角色', 'sys:role:add', '新增角色权限', 1, 2, NOW()),                  -- 2表示按钮权限
('编辑角色', 'sys:role:update', '修改角色权限', 1, 2, NOW()),               -- 2表示按钮权限
('删除角色', 'sys:role:delete', '删除角色权限', 1, 2, NOW()),               -- 2表示按钮权限

-- 权限管理权限
('查看权限', 'sys:permission:query', '查看权限列表、详情权限', 1, 1, NOW()), -- 1表示菜单权限
('新增权限', 'sys:permission:add', '新增权限权限', 1, 2, NOW()),             -- 2表示按钮权限
('编辑权限', 'sys:permission:update', '修改权限权限', 1, 2, NOW()),          -- 2表示按钮权限
('删除权限', 'sys:permission:delete', '删除权限权限', 1, 2, NOW()),          -- 2表示按钮权限

-- 系统管理权限
('查看日志', 'sys:log:query', '查看系统日志权限', 1, 1, NOW()),             -- 1表示菜单权限
('删除日志', 'sys:log:delete', '删除系统日志权限', 1, 2, NOW());            -- 2表示按钮权限

-- 插入角色权限关联数据
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES 
-- 超级管理员拥有所有权限
(1, 1), (1, 2), (1, 3), (1, 4),     -- 用户管理权限
(1, 5), (1, 6), (1, 7), (1, 8),     -- 角色管理权限
(1, 9), (1, 10), (1, 11), (1, 12),  -- 权限管理权限
(1, 13), (1, 14),                    -- 系统管理权限

-- 测试管理员拥有查看权限和部分操作权限
(2, 1), (2, 3),                      -- 用户管理：查看、编辑
(2, 5), (2, 7),                      -- 角色管理：查看、编辑
(2, 9),                              -- 权限管理：仅查看
(2, 13),                             -- 系统管理：仅查看日志

-- 普通用户仅拥有查看权限
(3, 1),                              -- 仅查看用户
(3, 13);                             -- 查看日志

-- 更新角色描述，使其更准确
UPDATE sys_role 
SET description = CASE 
    WHEN role_code = 'ROLE_SUPER_ADMIN' THEN '拥有系统所有操作权限'
    WHEN role_code = 'ROLE_TEST_ADMIN' THEN '拥有用户和角色的查看及编辑权限'
    WHEN role_code = 'ROLE_USER' THEN '仅拥有基本的查看权限'
    ELSE description
END
WHERE role_code IN ('ROLE_SUPER_ADMIN', 'ROLE_TEST_ADMIN', 'ROLE_USER'); 