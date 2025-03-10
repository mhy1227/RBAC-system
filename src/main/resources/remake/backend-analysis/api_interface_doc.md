# RBAC系统接口文档

## 1. 基础说明

### 1.1 接口基础信息
- 基础路径：`http://localhost:8080`
- 接口认证：Bearer Token（除登录等公开接口外，其他接口都需要在请求头中携带token）
- 请求头格式：`Authorization: Bearer {token}`
- 响应格式：统一采用JSON格式
- 时间格式：`yyyy-MM-dd HH:mm:ss`

### 1.2 通用响应格式
```json
{
    "code": 200,       // 响应码
    "message": "success", // 响应消息
    "data": {}         // 响应数据
}
```

### 1.3 通用响应码
- 200：成功
- 401：未认证
- 403：无权限
- 404：资源不存在
- 500：系统错误
- 1001：参数错误
- 1002：业务错误

## 2. 认证管理

### 2.1 用户登录
- 请求路径：`POST /auth/login`
- 请求参数：
```json
{
    "username": "admin",  // 用户名
    "password": "123456"  // 密码
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1...",  // JWT令牌
        "user": {
            "id": 1,
            "username": "admin",
            "nickname": "管理员",
            "email": "admin@example.com",
            "phone": "13800138000",
            "avatar": "/upload/avatar/1.jpg",
            "status": 1,
            "createTime": "2024-01-20 10:00:00"
        }
    }
}
```

### 2.2 退出登录
- 请求路径：`POST /auth/logout`
- 请求头：需要携带token
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

### 2.3 获取当前用户信息
- 请求路径：`GET /auth/info`
- 请求头：需要携带token
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "admin",
        "nickname": "管理员",
        "email": "admin@example.com",
        "phone": "13800138000",
        "avatar": "/upload/avatar/1.jpg",
        "status": 1,
        "createTime": "2024-01-20 10:00:00",
        "permissions": ["sys:user:add", "sys:user:edit"]  // 权限列表
    }
}
```

## 3. 用户管理

### 3.1 分页查询用户列表
- 请求路径：`GET /user/page`
- 请求参数：
  - page：页码（从1开始）
  - size：每页大小
  - username：用户名（可选）
  - nickname：昵称（可选）
  - status：状态（可选）
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "list": [{
            "id": 1,
            "username": "admin",
            "nickname": "管理员",
            "email": "admin@example.com",
            "phone": "13800138000",
            "status": 1,
            "createTime": "2024-01-20 10:00:00"
        }]
    }
}
```

### 3.2 获取用户详情
- 请求路径：`GET /user/{id}`
- 路径参数：
  - id：用户ID
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "admin",
        "nickname": "管理员",
        "email": "admin@example.com",
        "phone": "13800138000",
        "status": 1,
        "createTime": "2024-01-20 10:00:00",
        "roles": [{
            "id": 1,
            "roleName": "超级管理员",
            "roleCode": "SUPER_ADMIN"
        }]
    }
}
```

### 3.3 新增用户
- 请求路径：`POST /user`
- 请求参数：
```json
{
    "username": "test",
    "password": "123456",
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "13800138001",
    "roleIds": [1, 2]  // 角色ID列表
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

### 3.4 更新用户
- 请求路径：`PUT /user`
- 请求参数：
```json
{
    "id": 1,
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "13800138001",
    "roleIds": [1, 2]  // 角色ID列表
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

### 3.5 删除用户
- 请求路径：`DELETE /user/{id}`
- 路径参数：
  - id：用户ID
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

### 3.6 更新用户状态
- 请求路径：`PUT /user/{id}/status/{status}`
- 路径参数：
  - id：用户ID
  - status：状态（0-禁用，1-启用）
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

## 4. 角色管理

### 4.1 分页查询角色列表
- 请求路径：`GET /role/page`
- 请求参数：
  - page：页码（从1开始）
  - size：每页大小
  - roleName：角色名称（可选）
  - status：状态（可选）
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "list": [{
            "id": 1,
            "roleName": "超级管理员",
            "roleCode": "SUPER_ADMIN",
            "description": "系统超级管理员",
            "status": 1,
            "createTime": "2024-01-20 10:00:00"
        }]
    }
}
```

### 4.2 获取角色详情
- 请求路径：`GET /role/{id}`
- 路径参数：
  - id：角色ID
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "roleName": "超级管理员",
        "roleCode": "SUPER_ADMIN",
        "description": "系统超级管理员",
        "status": 1,
        "createTime": "2024-01-20 10:00:00",
        "permissions": [1, 2, 3]  // 权限ID列表
    }
}
```

### 4.3 新增角色
- 请求路径：`POST /role`
- 请求参数：
```json
{
    "roleName": "测试角色",
    "roleCode": "TEST_ROLE",
    "description": "测试角色描述",
    "status": 1
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

### 4.4 更新角色
- 请求路径：`PUT /role`
- 请求参数：
```json
{
    "id": 1,
    "roleName": "测试角色",
    "roleCode": "TEST_ROLE",
    "description": "测试角色描述",
    "status": 1
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

### 4.5 删除角色
- 请求路径：`DELETE /role/{id}`
- 路径参数：
  - id：角色ID
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

### 4.6 分配角色权限
- 请求路径：`POST /role/{roleId}/permission`
- 路径参数：
  - roleId：角色ID
- 请求参数：
```json
[1, 2, 3]  // 权限ID列表
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

## 5. 权限管理

### 5.1 获取权限树
- 请求路径：`GET /permission/tree`
- 请求参数：
  - type：权限类型（可选，menu-菜单，button-按钮）
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": [{
        "id": 1,
        "permissionName": "系统管理",
        "permissionCode": "sys:manage",
        "type": "menu",
        "path": "/system",
        "children": [{
            "id": 2,
            "permissionName": "用户管理",
            "permissionCode": "sys:user:manage",
            "type": "menu",
            "path": "/system/user"
        }]
    }]
}
```

### 5.2 新增权限
- 请求路径：`POST /permission`
- 请求参数：
```json
{
    "permissionName": "新增用户",
    "permissionCode": "sys:user:add",
    "type": "button",
    "parentId": 2,
    "path": null,
    "status": 1
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

### 5.3 更新权限
- 请求路径：`PUT /permission`
- 请求参数：
```json
{
    "id": 1,
    "permissionName": "新增用户",
    "permissionCode": "sys:user:add",
    "type": "button",
    "parentId": 2,
    "path": null,
    "status": 1
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

### 5.4 删除权限
- 请求路径：`DELETE /permission/{id}`
- 路径参数：
  - id：权限ID
- 响应结果：
```json
{
    "code": 200,
    "message": "success"
}
```

## 6. 日志管理

### 6.1 分页查询操作日志
- 请求路径：`GET /log/page`
- 请求参数：
  - page：页码（从1开始）
  - size：每页大小
  - module：模块名称（可选）
  - operation：操作类型（可选）
  - startTime：开始时间（可选）
  - endTime：结束时间（可选）
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "list": [{
            "id": 1,
            "userId": 1,
            "username": "admin",
            "operation": "新增用户",
            "method": "com.czj.rbac.controller.UserController.add",
            "params": "{\"username\":\"test\"}",
            "time": 100,
            "ip": "127.0.0.1",
            "createTime": "2024-01-20 10:00:00",
            "status": 1
        }]
    }
}
```

### 6.2 分页查询登录日志
- 请求路径：`GET /login-info/page`
- 请求参数：
  - page：页码（从1开始）
  - size：每页大小
  - username：用户名（可选）
  - loginIp：登录IP（可选）
  - startTime：开始时间（可选）
  - endTime：结束时间（可选）
  - loginStatus：登录状态（可选，0-失败，1-成功）
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "list": [{
            "id": 1,
            "userId": 1,
            "username": "admin",
            "loginId": "xxx",
            "loginIp": "127.0.0.1",
            "loginTime": "2024-01-20 10:00:00",
            "logoutTime": "2024-01-20 11:00:00",
            "loginStatus": 1,
            "failReason": null
        }]
    }
}
``` 