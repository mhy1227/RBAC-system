-- 插入测试用户数据

INSERT INTO sys_user (username, password, nickname, email, phone, status, create_time)
VALUES 

-- 超级管理员
('admin1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '超级管理员1', 'admin1@example.com', '13800138001', 1, NOW()),
('admin2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '超级管理员2', 'admin2@example.com', '13800138002', 1, NOW()),

-- 测试管理员
('test_admin1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理员1', 'test_admin1@example.com', '13800138003', 1, NOW()),
('test_admin2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理员2', 'test_admin2@example.com', '13800138004', 1, NOW()),
('test_admin3', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '测试管理员3', 'test_admin3@example.com', '13800138005', 1, NOW()),

-- 普通用户
('user1', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '普通用户1', 'user1@example.com', '13800138006', 1, NOW()),
('user2', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '普通用户2', 'user2@example.com', '13800138007', 1, NOW()),
('user3', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '普通用户3', 'user3@example.com', '13800138008', 1, NOW()),
('user4', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '普通用户4', 'user4@example.com', '13800138009', 1, NOW()),
('user5', 'WHBvc0FhMG9BYlBmWTdRWQ==$431xh7sl0o6XzMX1n2lAYg==', '普通用户5', 'user5@example.com', '13800138010', 1, NOW()); 