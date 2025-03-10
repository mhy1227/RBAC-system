# RBAC系统API测试文档 V2

## 一、用户管理模块测试

### 1. 用户列表查询测试

#### 1.1 基本查询
```bash
curl -X GET "http://localhost:3000/api/user/page?pageNum=1&pageSize=10"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [用户列表数据],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

#### 1.2 条件查询
```bash
curl -X GET "http://localhost:3000/api/user/page?pageNum=1&pageSize=10&username=admin&status=1"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [符合条件的用户列表],
    "total": 少于100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 2. 用户详情查询测试

#### 2.1 查询存在的用户
```bash
curl -X GET "http://localhost:3000/api/user/1"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "用户名",
    "nickname": "昵称",
    "email": "邮箱",
    "phone": "手机号",
    "avatar": "头像URL",
    "status": 1,
    "createTime": "创建时间",
    "updateTime": "更新时间",
    "lastLoginTime": "最后登录时间",
    "loginFailCount": 0,
    "lockTime": null,
    "roles": [角色信息]
  }
}
```

### 3. 创建用户测试

#### 3.1 正常创建
```bash
curl -X POST "http://localhost:3000/api/user" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "123456",
    "nickname": "新用户",
    "email": "newuser@example.com",
    "phone": "13800138000",
    "status": 1,
    "roleIds": [1, 2]
  }'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 3.2 创建用户名已存在
```bash
curl -X POST "http://localhost:3000/api/user" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456",
    "nickname": "管理员",
    "status": 1
  }'
```
预期响应：
```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

### 4. 更新用户测试

#### 4.1 正常更新
```bash
curl -X PUT "http://localhost:3000/api/user" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "nickname": "更新后的昵称",
    "email": "updated@example.com",
    "phone": "13900139000",
    "status": 1,
    "roleIds": [1, 3]
  }'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 5. 删除用户测试

#### 5.1 单个删除
```bash
curl -X DELETE "http://localhost:3000/api/user/1"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.2 批量删除
```bash
curl -X DELETE "http://localhost:3000/api/user/batch" \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3]'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 6. 更新用户状态测试

```bash
curl -X PUT "http://localhost:3000/api/user/1/status/0"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 7. 检查用户名是否存在测试

```bash
curl -X GET "http://localhost:3000/api/user/check?username=admin"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "exists": true
  }
}
```

## 二、角色管理模块测试

### 1. 角色列表查询测试

#### 1.1 基本查询
```bash
curl -X GET "http://localhost:3000/api/role/page?pageNum=1&pageSize=10"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [角色列表数据],
    "total": 50,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

#### 1.2 条件查询
```bash
curl -X GET "http://localhost:3000/api/role/page?pageNum=1&pageSize=10&roleName=管理员&status=1"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [符合条件的角色列表],
    "total": 少于50,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 2. 角色详情查询测试

```bash
curl -X GET "http://localhost:3000/api/role/1"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "roleName": "角色名称",
    "roleCode": "角色编码",
    "description": "角色描述",
    "status": 1,
    "createTime": "创建时间",
    "updateTime": "更新时间",
    "permissions": [1, 2, 3, 4, 5]
  }
}
```

### 3. 创建角色测试

```bash
curl -X POST "http://localhost:3000/api/role" \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "测试角色",
    "roleCode": "TEST_ROLE",
    "description": "这是一个测试角色",
    "status": 1
  }'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 4. 更新角色测试

```bash
curl -X PUT "http://localhost:3000/api/role" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "roleName": "更新后的角色名",
    "description": "更新后的描述",
    "status": 1
  }'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 5. 删除角色测试

#### 5.1 单个删除
```bash
curl -X DELETE "http://localhost:3000/api/role/1"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.2 批量删除
```bash
curl -X DELETE "http://localhost:3000/api/role/batch" \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3]'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 6. 更新角色状态测试

```bash
curl -X PUT "http://localhost:3000/api/role/1/status/0"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 7. 获取角色权限测试

```bash
curl -X GET "http://localhost:3000/api/role/1/permissions"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": [1, 2, 3, 4, 5]
}
```

### 8. 更新角色权限测试

```bash
curl -X POST "http://localhost:3000/api/role/1/permissions" \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3, 4, 5, 6, 7]'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 三、权限管理模块测试

### 1. 获取权限树测试

```bash
curl -X GET "http://localhost:3000/api/permission/tree"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "permissionName": "系统管理",
      "permissionCode": "sys:manage",
      "description": "系统管理模块",
      "pid": 0,
      "type": "MENU",
      "path": "/system",
      "status": 1,
      "createTime": "创建时间",
      "updateTime": "更新时间",
      "children": [
        {
          "id": 2,
          "permissionName": "用户管理",
          "permissionCode": "sys:user:manage",
          "description": "用户管理模块",
          "pid": 1,
          "type": "MENU",
          "path": "/system/user",
          "status": 1,
          "createTime": "创建时间",
          "updateTime": "更新时间",
          "children": [...]
        }
      ]
    }
  ]
}
```

### 2. 条件查询权限树测试

```bash
curl -X GET "http://localhost:3000/api/permission/tree?permissionName=用户&type=MENU&status=1"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": [符合条件的权限树]
}
```

### 3. 创建权限测试

```bash
curl -X POST "http://localhost:3000/api/permission" \
  -H "Content-Type: application/json" \
  -d '{
    "permissionName": "测试权限",
    "permissionCode": "test:permission",
    "description": "这是一个测试权限",
    "pid": 1,
    "type": "BUTTON",
    "status": 1,
    "sortOrder": 1
  }'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 4. 更新权限测试

```bash
curl -X PUT "http://localhost:3000/api/permission/1" \
  -H "Content-Type: application/json" \
  -d '{
    "permissionName": "更新后的权限名",
    "description": "更新后的描述",
    "status": 1
  }'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 5. 删除权限测试

#### 5.1 单个删除
```bash
curl -X DELETE "http://localhost:3000/api/permission/1"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.2 批量删除
```bash
curl -X DELETE "http://localhost:3000/api/permission/batch" \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3]'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 6. 更新权限状态测试

```bash
curl -X PUT "http://localhost:3000/api/permission/1/status" \
  -H "Content-Type: application/json" \
  -d '{
    "status": 0
  }'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 四、认证模块测试

### 1. 用户登录测试

#### 1.1 正常登录
```bash
curl -X POST "http://localhost:3000/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "mock-token",
    "user": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "email": "admin@example.com",
      "phone": "13800138000",
      "avatar": "头像URL",
      "status": 1,
      "roles": [
        {
          "id": 1,
          "roleName": "超级管理员",
          "roleCode": "SUPER_ADMIN",
          "description": "系统超级管理员",
          "status": 1
        }
      ]
    }
  }
}
```

#### 1.2 用户名或密码错误
```bash
curl -X POST "http://localhost:3000/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "wrong_password"
  }'
```
预期响应：
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

#### 1.3 用户被禁用
```bash
curl -X POST "http://localhost:3000/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "disabled_user",
    "password": "123456"
  }'
```
预期响应：
```json
{
  "code": 401,
  "message": "用户已被禁用",
  "data": null
}
```

### 2. 获取用户信息测试

```bash
curl -X GET "http://localhost:3000/api/auth/info" \
  -H "Authorization: Bearer mock-token"
```
预期响应：
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
    "avatar": "头像URL",
    "status": 1,
    "createTime": "创建时间",
    "updateTime": "更新时间",
    "lastLoginTime": "最后登录时间",
    "loginFailCount": 0,
    "lockTime": null,
    "roles": [角色信息],
    "permissions": ["sys:user:view", "sys:user:add", ...]
  }
}
```

### 3. 退出登录测试

```bash
curl -X POST "http://localhost:3000/api/auth/logout" \
  -H "Authorization: Bearer mock-token"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 五、综合测试场景

### 1. 用户登录后获取权限测试

#### 1.1 登录
```bash
curl -X POST "http://localhost:3000/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

#### 1.2 获取用户信息
```bash
curl -X GET "http://localhost:3000/api/auth/info" \
  -H "Authorization: Bearer mock-token"
```

### 2. 角色权限分配测试

#### 2.1 创建角色
```bash
curl -X POST "http://localhost:3000/api/role" \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "测试管理员",
    "roleCode": "TEST_ADMIN",
    "description": "测试管理员角色",
    "status": 1
  }'
```

#### 2.2 分配权限
```bash
curl -X POST "http://localhost:3000/api/role/新角色ID/permissions" \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3, 4, 5]'
```

#### 2.3 创建用户并分配角色
```bash
curl -X POST "http://localhost:3000/api/user" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testadmin",
    "password": "123456",
    "nickname": "测试管理员",
    "email": "testadmin@example.com",
    "status": 1,
    "roleIds": [新角色ID]
  }'
```

#### 2.4 使用新用户登录
```bash
curl -X POST "http://localhost:3000/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testadmin",
    "password": "123456"
  }'
```

#### 2.5 验证新用户权限
```bash
curl -X GET "http://localhost:3000/api/auth/info" \
  -H "Authorization: Bearer 新用户token"
```

## 六、错误场景测试

### 1. 无权限访问测试

```bash
curl -X GET "http://localhost:3000/api/user/page" \
  -H "Authorization: Bearer 普通用户token"
```
预期响应：
```json
{
  "code": 403,
  "message": "权限不足",
  "data": null
}
```

### 2. Token过期测试

```bash
curl -X GET "http://localhost:3000/api/user/page" \
  -H "Authorization: Bearer 过期token"
```
预期响应：
```json
{
  "code": 401,
  "message": "登录已过期，请重新登录",
  "data": null
}
```

### 3. 参数验证失败测试

```bash
curl -X POST "http://localhost:3000/api/user" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "",
    "password": "123"
  }'
```
预期响应：
```json
{
  "code": 400,
  "message": "用户名不能为空，密码长度不能小于6位",
  "data": null
}
```

### 4. 请求不存在的资源

```bash
curl -X GET "http://localhost:3000/api/user/999"
```
预期响应：
```json
{
  "code": 404,
  "message": "用户不存在",
  "data": null
}
```

### 5. 请求格式错误

```bash
curl -X POST "http://localhost:3000/api/user" \
  -H "Content-Type: application/json" \
  -d '{
    "username": 123,
    "password": true
  }'
```
预期响应：
```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null
}
```

## 七、测试注意事项

### 1. 响应格式

所有接口返回格式统一为：
```typescript
interface ApiResult<T> {
  code: number;    // 状态码，200表示成功
  message: string; // 提示信息
  data: T;        // 响应数据
}
```

### 2. 分页格式

分页查询返回格式：
```typescript
interface PageResult<T> {
  list: T[];      // 数据列表
  total: number;  // 总记录数
  pageNum: number;// 当前页码
  pageSize: number;// 每页大小
}
```

### 3. 错误码说明

- 200: 请求成功
- 400: 请求参数错误
- 401: 未授权（未登录或登录已过期）
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误

### 4. 测试工具

- 使用Postman或Apifox进行接口测试
- 使用浏览器开发者工具查看网络请求
- 使用curl命令行工具测试（如上述示例）

### 5. 常见问题排查

- 确认服务是否正常启动（npm run dev）
- 检查请求URL是否正确
- 检查请求方法（GET/POST/PUT/DELETE）是否正确
- 检查请求参数格式是否正确
- 检查响应状态码和数据格式
- 查看控制台日志是否有错误信息

### 6. Mock数据说明

- 本测试文档中的接口均由前端Mock服务提供
- 数据为随机生成，每次重启服务数据会重置
- 部分复杂业务逻辑可能未完全模拟

## 八、测试结果记录模板

| 测试ID | 测试名称 | 测试步骤 | 预期结果 | 实际结果 | 是否通过 | 备注 |
|-------|---------|---------|---------|---------|---------|------|
| UT001 | 用户登录 | 1. 输入用户名密码<br>2. 点击登录 | 登录成功，跳转到首页 | | | |
| UT002 | 用户列表查询 | 1. 进入用户管理页面<br>2. 设置查询条件<br>3. 点击查询 | 显示符合条件的用户列表 | | | |

## 九、测试环境配置说明

### 1. 前端环境

- Node.js v16+
- npm v8+
- Vite v4+
- Vue 3
- TypeScript 5+

### 2. 启动测试环境

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 3. Mock服务配置

- 确保在`.env.development`中设置`VITE_USE_MOCK=true`
- 确保`vite.config.ts`中正确配置了`viteMockServe`插件

### 4. 测试工具

- Apifox/Postman：API测试
- Chrome DevTools：网络请求监控
- curl：命令行测试 