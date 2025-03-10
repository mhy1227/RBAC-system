# RBAC系统前端接口测试文档

## 环境准备

1. 安装依赖：
```bash
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

3. 确认服务启动成功：
   - 访问 http://localhost:3000
   - 控制台无报错信息
   - 查看网络请求是否正常

## 测试工具导入

### 1. Postman导入步骤：
- 打开Postman
- 点击左上角的"Import"按钮
- 选择"File" -> "Upload Files"
- 选择刚才生成的docs/rbac-api.json文件
- 点击"Import"确认导入
- 导入后设置环境变量：
  - 点击右上角的"Environment"
  - 创建新环境（如"RBAC-Local"）
  - 添加变量baseUrl，值为http://localhost:3000

### 2. Apifox导入步骤：
- 打开Apifox
- 点击左侧"项目"
- 点击"导入项目"
- 选择"Postman Collection"
- 选择刚才生成的docs/rbac-api.json文件
- 点击"导入"确认
- 导入后设置环境变量：
  - 点击左侧"环境配置"
  - 创建新环境（如"本地环境"）
  - 添加变量baseUrl，值为http://localhost:3000

## 测试顺序

按照以下顺序进行测试，因为存在数据依赖关系：

1. 权限模块测试
   - 获取权限树
   - 新增权限（菜单和按钮）
   - 更新权限信息
   - 删除权限

2. 角色模块测试
   - 新增角色
   - 分页查询角色列表
   - 获取角色详情
   - 分配角色权限
   - 更新角色信息
   - 删除角色

3. 用户模块测试
   - 新增用户（关联角色）
   - 分页查询用户列表
   - 获取用户详情
   - 更新用户信息
   - 更新用户状态
   - 删除用户

## 用户模块测试

### 1. 分页查询用户列表
```bash
curl -X GET "http://localhost:3000/api/user/page?pageNum=1&pageSize=10"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 2. 获取用户详情
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
    "username": "...",
    "nickname": "...",
    "email": "...",
    "phone": "...",
    "avatar": "...",
    "status": 1,
    "createTime": "...",
    "updateTime": "...",
    "lastLoginTime": "...",
    "loginFailCount": 0,
    "lockTime": null,
    "roles": [...]
  }
}
```

### 3. 新增用户
```bash
curl -X POST "http://localhost:3000/api/user" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "nickname": "测试用户",
    "email": "test@example.com",
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

### 4. 更新用户
```bash
curl -X PUT "http://localhost:3000/api/user" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "nickname": "更新后的昵称",
    "email": "updated@example.com"
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

### 5. 删除用户
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

### 6. 更新用户状态
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

## 角色模块测试

### 1. 分页查询角色列表
```bash
curl -X GET "http://localhost:3000/api/role/page?pageNum=1&pageSize=10"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 50,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 2. 获取角色详情
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
    "roleName": "...",
    "roleCode": "...",
    "description": "...",
    "status": 1,
    "createTime": "...",
    "updateTime": "...",
    "permissions": [1, 2, 3, 4, 5]
  }
}
```

### 3. 新增角色
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

### 4. 更新角色
```bash
curl -X PUT "http://localhost:3000/api/role" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "roleName": "更新后的角色名",
    "description": "更新后的描述"
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

### 5. 删除角色
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

### 6. 分配角色权限
```bash
curl -X POST "http://localhost:3000/api/role/1/permission" \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3, 4, 5]'
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 权限模块测试

### 1. 获取权限树
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
      "parentId": 0,
      "type": "menu",
      "path": "/system",
      "status": 1,
      "createTime": "...",
      "updateTime": "...",
      "children": [...]
    }
  ]
}
```

### 2. 新增权限
```bash
curl -X POST "http://localhost:3000/api/permission" \
  -H "Content-Type: application/json" \
  -d '{
    "permissionName": "测试权限",
    "permissionCode": "test:permission",
    "description": "这是一个测试权限",
    "parentId": 0,
    "type": "menu",
    "path": "/test",
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

### 3. 更新权限
```bash
curl -X PUT "http://localhost:3000/api/permission" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "permissionName": "更新后的权限名",
    "description": "更新后的描述"
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

### 4. 删除权限
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

### 5. 获取角色权限
```bash
curl -X GET "http://localhost:3000/api/permission/role/1"
```
预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": [1, 2, 3, 4, 5]
}
```

## 错误处理测试

以下是一些错误情况的测试，用于验证系统的错误处理能力：

### 1. 请求不存在的用户
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

### 2. 请求格式错误
```bash
curl -X POST "http://localhost:3000/api/user" \
  -H "Content-Type: application/json" \
  -d '{
    "username": ""
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

### 3. 路径参数错误
```bash
curl -X GET "http://localhost:3000/api/user/abc"
```
预期响应：
```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null
}
```

## 测试注意事项

1. **响应格式**：所有接口返回格式统一为：
```typescript
interface ApiResult<T> {
  code: number;    // 状态码，200表示成功
  message: string; // 提示信息
  data: T;        // 响应数据
}
```

2. **分页格式**：分页查询返回格式：
```typescript
interface PageResult<T> {
  list: T[];      // 数据列表
  total: number;  // 总记录数
  pageNum: number;// 当前页码
  pageSize: number;// 每页大小
}
```

3. **错误处理**：
   - 400: 请求参数错误
   - 401: 未授权（未登录或登录已过期）
   - 403: 权限不足
   - 404: 资源不存在
   - 500: 服务器内部错误

4. **测试工具**：
   - 使用Postman或Apifox进行接口测试
   - 使用浏览器开发者工具查看网络请求
   - 使用curl命令行工具测试（如上述示例）

5. **常见问题排查**：
   - 确认服务是否正常启动（npm run dev）
   - 检查请求URL是否正确
   - 检查请求方法（GET/POST/PUT/DELETE）是否正确
   - 检查请求参数格式是否正确
   - 检查响应状态码和数据格式
   - 查看控制台日志是否有错误信息

6. **Mock数据说明**：
   - 本测试文档中的接口均由前端Mock服务提供
   - 数据为随机生成，每次重启服务数据会重置
   - 部分复杂业务逻辑可能未完全模拟

## 五、用户管理模块测试

### 1. 用户列表查询测试
...

### 2. 用户详情查询测试
...

## 六、角色管理模块测试
...

## 七、权限管理模块测试
... 