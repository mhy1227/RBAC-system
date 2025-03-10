# RBAC系统数据库设计分析

## 1. 数据库版本变更历史

### 1.1 初始架构 (V1.0.0)
- 创建基础表结构：用户表、角色表、权限表
- 建立基本的RBAC模型

### 1.2 用户认证增强 (V1.0.1 - V1.0.2)
- 添加用户登录相关字段
- 增加角色层级概念

### 1.3 日志体系建设 (V1.0.3, V1.0.17, V1.0.18-V1.0.19)
- 添加系统日志表
- 完善登录信息记录
- 优化日志结构设计

### 1.4 权限模型优化 (V1.0.14 - V1.0.16)
- 更新权限结构
- 优化权限关系
- 完善权限数据

### 1.5 安全特性增强 (V1.0.20)
- 添加安全问题表
- 增强用户安全机制

## 2. 核心表结构

### 2.1 用户表 (sys_user)
```sql
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '用户头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    login_fail_count INT DEFAULT 0 COMMENT '登录失败次数，达到5次将锁定账号',
    lock_time DATETIME DEFAULT NULL COMMENT '锁定时间，默认锁定30分钟'
);
```

### 2.2 角色表 (sys_role)
```sql
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

### 2.3 权限表 (sys_permission)
```sql
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(50) NOT NULL UNIQUE COMMENT '权限编码',
    description VARCHAR(200) DEFAULT NULL COMMENT '权限描述',
    pid BIGINT COMMENT '父权限ID',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    type VARCHAR(20) NOT NULL COMMENT '类型(menu:菜单,button:按钮)',
    path VARCHAR(200) DEFAULT NULL COMMENT '路径',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

### 2.4 用户角色关联表 (sys_user_role)
```sql
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id) COMMENT '用户角色唯一索引',
    KEY idx_role_id (role_id) COMMENT '角色ID索引'
);
```

### 2.5 角色权限关联表 (sys_role_permission)
```sql
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id) COMMENT '角色权限唯一索引',
    KEY idx_permission_id (permission_id) COMMENT '权限ID索引'
);
```

### 2.6 系统日志表 (sys_log)
```sql
CREATE TABLE sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    username VARCHAR(50),
    operation VARCHAR(50),
    method VARCHAR(200),
    params TEXT,
    time BIGINT,
    ip VARCHAR(64),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    status TINYINT,
    error_message TEXT
);
```

### 2.7 登录信息表 (login_info)
```sql
CREATE TABLE login_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    login_id VARCHAR(50) COMMENT '登录标识ID',
    login_ip VARCHAR(50) COMMENT '登录IP地址',
    login_time DATETIME COMMENT '登录时间',
    logout_time DATETIME COMMENT '登出时间',
    login_status TINYINT COMMENT '登录状态：0-失败，1-成功',
    fail_reason VARCHAR(200) COMMENT '失败原因：密码错误、账号锁定等'
);
```

### 2.8 安全问题表 (security_question)
```sql
CREATE TABLE security_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer VARCHAR(200) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 3. 设计特点

### 3.1 安全性设计
1. 密码存储：使用加密存储
2. 登录安全：
   - 密码错误处理：连续5次密码错误将锁定账号
   - 账号锁定：锁定时间为30分钟
   - 登录失败记录：记录失败原因、IP地址等信息
   - 安全审计：完整的登录失败记录追踪
3. 操作审计：完整的日志记录

### 3.2 性能考虑
1. 合理的索引设计：包含主键、唯一索引和普通索引
2. 关联表优化：添加了必要的索引如idx_role_id和idx_permission_id
3. 适当的字段类型选择：使用合适的数据类型和长度

### 3.3 扩展性设计
1. 完整的注释：每个表和字段都有清晰的中文注释
2. 模块化的表结构：清晰的表关系
3. 版本化的迁移脚本：支持数据库版本控制

## 4. 版本控制建议

### 4.1 迁移脚本管理
1. 严格遵循版本号递增规则
2. 每个变更使用独立的迁移文件
3. 避免修改已发布的迁移脚本

### 4.2 变更流程
1. 创建新的迁移脚本
2. 在测试环境验证
3. 备份生产数据
4. 应用迁移脚本

### 4.3 最佳实践
1. 记录详细的变更说明
2. 保持向后兼容
3. 考虑数据迁移方案
4. 制定回滚策略

## 5. 待优化点

### 5.1 表结构优化
1. 考虑分表策略
2. 优化字段设计
3. 完善索引结构

### 5.2 性能优化
1. 大表分区
2. 历史数据归档
3. 索引优化

### 5.3 功能优化
1. 增加数据版本控制
2. 添加软删除支持
3. 完善审计功能 