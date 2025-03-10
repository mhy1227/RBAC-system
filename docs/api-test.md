# RBAC系统前端接口测试文档

##1.Postman导入步骤：
打开Postman
点击左上角的"Import"按钮
选择"File" -> "Upload Files"
选择刚才生成的docs/rbac-api.json文件
点击"Import"确认导入
导入后设置环境变量：
点击右上角的"Environment"
创建新环境（如"RBAC-Local"）
添加变量baseUrl，值为http://localhost:3000
##2.Apifox导入步骤：
打开Apifox
点击左侧"项目"
点击"导入项目"
选择"Postman Collection"
选择刚才生成的docs/rbac-api.json文件
点击"导入"确认
导入后设置环境变量：
点击左侧"环境配置"
创建新环境（如"本地环境"）
添加变量baseUrl，值为http://localhost:3000

导入后，你可以看到所有接口都按模块分类组织好了，包括：
用户模块（6个接口）
角色模块（6个接口）
权限模块（5个接口）
每个接口都包含了：
请求方法
请求URL
请求头
请求参数
请求体（如果需要）

## 测试步骤

### 1. 环境准备
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

### 2. 测试顺序
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

### 3. 测试方法
1. 使用Postman测试：
   - 创建新的Collection
   - 按模块创建文件夹
   - 导入测试接口
   - 设置环境变量（baseUrl等）
   - 按顺序执行测试用例

2. 使用curl命令测试：
   - 打开终端
   - 复制本文档中的curl命令
   - 替换相应的参数
   - 执行命令查看结果

3. 使用浏览器测试：
   - 打开浏览器开发者工具
   - 切换到Network标签页
   - 操作页面触发请求
   - 查看请求和响应数据

### 4. 测试要点
1. 数据完整性测试：
   - 必填字段是否校验
   - 字段类型是否正确
   - 数据格式是否符合要求

2. 业务逻辑测试：
   - 权限树结构是否正确
   - 角色-权限关联是否生效
   - 用户-角色关联是否生效
   - 状态变更是否正确

3. 异常情况测试：
   - 参数错误的处理
   - 数据不存在的处理
   - 权限不足的处理
   - 并发操作的处理

### 5. 测试记录
建议记录以下内容：
- 测试时间
- 测试环境
- 测试用例
- 测试结果
- 发现的问题
- 解决方案

## 环境准备
1. 确保项目已启动：`npm run dev`
2. 默认服务地址：`http://localhost:3000`

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

### 5. 删除用户
```bash
curl -X DELETE "http://localhost:3000/api/user/1"
```

### 6. 更新用户状态
```bash
curl -X PUT "http://localhost:3000/api/user/1/status/0"
```

## 角色模块测试

### 1. 分页查询角色列表
```bash
curl -X GET "http://localhost:3000/api/role/page?pageNum=1&pageSize=10"
```

### 2. 获取角色详情
```bash
curl -X GET "http://localhost:3000/api/role/1"
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

### 5. 删除角色
```bash
curl -X DELETE "http://localhost:3000/api/role/1"
```

### 6. 分配角色权限
```bash
curl -X POST "http://localhost:3000/api/role/1/permission" \
  -H "Content-Type: application/json" \
  -d '{
    "permissionIds": [1, 2, 3, 4, 5]
  }'
```

## 权限模块测试

### 1. 获取权限树
```bash
curl -X GET "http://localhost:3000/api/permission/tree"
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

### 4. 删除权限
```bash
curl -X DELETE "http://localhost:3000/api/permission/1"
```

### 5. 获取角色权限
```bash
curl -X GET "http://localhost:3000/api/permission/role/1"
```

## 测试注意事项

1. 所有接口返回格式统一为：
```typescript
interface ApiResult<T> {
  code: number;    // 状态码，200表示成功
  message: string; // 提示信息
  data: T;        // 响应数据
}
```

2. 分页查询返回格式：
```typescript
interface PageResult<T> {
  list: T[];      // 数据列表
  total: number;  // 总记录数
  pageNum: number;// 当前页码
  pageSize: number;// 每页大小
}
```

3. 测试工具建议：
   - 使用Postman或类似工具进行接口测试
   - 使用浏览器开发者工具查看网络请求
   - 使用curl命令行工具测试（如上述示例）

4. 常见问题排查：
   - 确认服务是否正常启动
   - 检查请求URL是否正确
   - 检查请求方法（GET/POST/PUT/DELETE）是否正确
   - 检查请求参数格式是否正确
   - 检查响应状态码和数据格式 