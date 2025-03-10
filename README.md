# RBAC权限管理系统

基于Spring Boot 3.x的RBAC（基于角色的访问控制）权限管理系统，实现了完整的用户管理、角色管理、权限管理、操作日志等功能。系统采用现代化的技术栈和最佳实践，提供了完善的权限控制机制和丰富的功能特性。

## 技术栈

### 后端技术
- **核心框架：** Spring Boot 3.2.7
- **安全框架：** Spring Security
- **ORM框架：** MyBatis 3.0.3
- **数据库：** MySQL 8.0.33
- **缓存：** Redis
- **认证：** JWT 0.11.5
- **API文档：** SpringDoc (OpenAPI 3)
- **日志：** SLF4J + Logback

### 前端技术
- **开发语言：** TypeScript
- **构建工具：** Vite
- **状态管理：** Vue3 Composition API
- **UI框架：** Element Plus
- **HTTP客户端：** Axios
- **路由：** Vue Router

## 系统架构

### 1. 权限控制实现
- **注解驱动设计**
  ```java
  @RequirePermission(value = "sys:user:query", requireAll = false)
  ```
  - 自定义注解实现方法级权限控制
  - 支持多权限组合校验
  - 灵活的权限配置机制

- **AOP权限拦截**
  - 统一的权限校验流程
  - 性能监控和日志记录
  - 完善的异常处理

### 2. 核心功能模块

#### 2.1 用户管理
- 用户CRUD操作
- 用户状态管理
- 角色分配
- 密码管理
- 个人资料维护

#### 2.2 角色管理
- 角色CRUD操作
- 角色状态管理
- 权限分配
- 数据权限控制

#### 2.3 权限管理
- 权限CRUD操作
- 权限状态管理
- 权限树形结构
- 动态权限控制

#### 2.4 日志管理
- 操作日志记录
- 登录日志记录
- 异步日志处理
- 日志查询分析

## 技术特点

### 1. 安全特性
- JWT Token认证
- 密码加密存储
- 会话状态管理
- 登录保护机制
- 细粒度权限控制
- 数据安全防护

### 2. 系统性能
- Redis分布式缓存
- 多级缓存策略
- 异步日志处理
- 并发控制优化

### 3. 代码质量
- 统一API规范
- 标准异常处理
- 模块化设计
- 详细的注释文档

## 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+
- Node.js 16+

## 快速开始

### 1. 环境准备
```bash
# 创建数据库
mysql -uroot -p
create database rbac default charset utf8mb4;
```

### 2. 修改配置
修改 `application.yml` 中的数据库和Redis配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rbac?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
```

### 3. 启动服务
```bash
# 后端服务
mvn spring-boot:run

# 前端服务
cd rbac-ui
npm install
npm run dev
```

## API文档

启动应用后访问：http://localhost:8080/swagger-ui.html

### 主要接口
1. 认证接口
   - POST /auth/login - 用户登录
   - POST /auth/logout - 用户登出
   - GET /auth/info - 获取用户信息
   - POST /auth/refresh - 刷新token

2. 用户接口
   - GET /user/page - 分页查询
   - GET /user/{id} - 用户详情
   - POST /user - 创建用户
   - PUT /user - 更新用户
   - DELETE /user/{id} - 删除用户

3. 角色接口
   - GET /role/page - 分页查询
   - POST /role - 创建角色
   - PUT /role/{id}/permission - 分配权限

4. 权限接口
   - GET /permission/tree - 权限树
   - POST /permission - 创建权限
   - PUT /permission - 更新权限

## 开发计划

### 1. 短期计划（1-2周）
- API限流机制
- XSS/CSRF防护
- 数据库连接池优化
- 敏感数据加密

### 2. 中期计划（1个月）
- Redis集群部署
- 系统监控告警
- 缓存优化策略
- 性能指标收集

### 3. 长期计划（3个月）
- 分布式架构支持
- 容器化部署方案
- 自动化运维
- 微服务改造

## 贡献指南

1. Fork 本仓库
2. 创建分支 `git checkout -b feature/xxx`
3. 提交代码
4. 创建 Pull Request

## 版本历史

### v1.0.0 (2024-02-19)
- 完整的RBAC权限模型实现
- 基于Spring Boot 3.x的现代化架构
- 完善的权限控制机制
- 丰富的用户管理功能

## 许可证

[MIT License](LICENSE)

## 角色管理

### 1. 角色管理功能
- **获取角色详情**：`GET /role/{id}` - 查询角色信息。
- **分页查询角色**：`GET /role/page` - 分页查询角色列表。
- **查询角色列表**：`GET /role/list` - 查询角色列表。
- **添加角色**：`POST /role` - 新增角色。
- **更新角色**：`PUT /role` - 更新角色信息。
- **删除角色**：`DELETE /role/{id}` - 删除角色。
- **更新角色状态**：`PUT /role/{id}/status/{status}` - 更新角色状态。
- **分配权限**：`POST /role/{roleId}/permission` - 为角色分配权限。

## 权限管理

### 1. 权限管理功能
- **获取权限详情**：`GET /permission/{id}` - 查询权限信息。
- **分页查询权限**：`GET /permission/page` - 分页查询权限列表。
- **查询权限列表**：`GET /permission/list` - 查询权限列表。
- **查询权限树**：`GET /permission/tree` - 查询权限树。
- **添加权限**：`POST /permission` - 新增权限。
- **更新权限**：`PUT /permission` - 更新权限信息。
- **删除权限**：`DELETE /permission/{id}` - 删除权限。
- **更新权限状态**：`PUT /permission/{id}/status/{status}` - 更新权限状态. 