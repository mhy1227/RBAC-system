-- 角色表添加等级字段
ALTER TABLE sys_role
ADD COLUMN role_level int DEFAULT 0 COMMENT '角色等级'; 