# RBAC认证模块测试文档

## 一、登录功能测试

### 1. 正常登录场景

#### 1.1 超级管理员登录
- 账号：`admin`
- 密码：`123456`
- 预期结果：
  - 登录成功
  - 获取完整的用户信息
  - 权限标识为 `['*:*:*']`
  - 角色为"超级管理员"

#### 1.2 测试管理员登录
- 账号：`test_admin`
- 密码：`123456`
- 预期结果：
  - 登录成功
  - 获取完整的用户信息
  - 权限标识为 `['sys:test:*']`
  - 角色为"测试管理员"

### 2. 异常登录场景

#### 2.1 密码错误
- 账号：`admin`
- 密码：`wrong_password`
- 预期结果：
  - 登录失败
  - 提示"用户名或密码错误"
  - 状态码：401

#### 2.2 用户被禁用
- 账号：`disabled_user`（status = 0的用户）
- 密码：`123456`
- 预期结果：
  - 登录失败
  - 提示"用户已被禁用"
  - 状态码：401

#### 2.3 账号被锁定
- 账号：`locked_user`（lockTime不为null的用户）
- 密码：`123456`
- 预期结果：
  - 登录失败
  - 提示"用户已被锁定"
  - 状态码：401

## 二、获取用户信息测试

### 1. 正常获取场景
- 前置条件：已登录成功
- 操作步骤：调用获取用户信息接口
- 预期结果：
  - 返回当前登录用户的完整信息
  - 包含：用户基本信息、角色信息、权限信息
  - 状态码：200

### 2. 异常获取场景
- 前置条件：未登录或token已失效
- 操作步骤：调用获取用户信息接口
- 预期结果：
  - 获取失败
  - 提示"未认证"
  - 状态码：401

## 三、退出登录测试

### 1. 正常退出场景
- 前置条件：已登录成功
- 操作步骤：点击退出登录
- 预期结果：
  - 退出成功
  - 清除本地token
  - 跳转到登录页
  - 状态码：200

### 2. 异常退出场景
- 前置条件：未登录或token已失效
- 操作步骤：调用退出接口
- 预期结果：
  - 退出成功
  - 状态码：200

## 四、测试数据说明

### 1. 测试账号列表

| 用户名 | 密码 | 角色 | 权限 | 状态 |
|-------|------|------|------|------|
| admin | 123456 | 超级管理员 | *:*:* | 正常 |
| test_admin | 123456 | 测试管理员 | sys:test:* | 正常 |
| dept_admin | 123456 | 部门管理员 | sys:dept:* | 正常 |
| project_manager | 123456 | 项目经理 | sys:project:* | 正常 |
| ops_user | 123456 | 运维人员 | sys:ops:* | 正常 |
| auditor | 123456 | 审计人员 | sys:audit:* | 正常 |

### 2. 测试步骤建议

1. 先测试正常登录场景
2. 再测试获取用户信息
3. 然后测试异常场景
4. 最后测试退出登录

### 3. 注意事项

1. 所有测试账号的默认密码都是：`123456`
2. 登录成功后会返回 mock-token
3. 用户信息接口会返回当前登录用户的完整信息
4. 测试时注意观察浏览器控制台的请求和响应信息
5. 建议使用 Chrome 浏览器的开发者工具进行测试

## 五、Mock测试补充说明

### 1. Mock测试Token列表

为了便于测试不同场景，Mock服务提供了以下固定的测试token：

| Token值 | 用途 | 对应用户 |
|---------|------|----------|
| mock-token-admin-12345 | 管理员token | admin用户 |
| mock-token-test-12345 | 测试管理员token | test_admin用户 |
| mock-token-expired-12345 | 模拟过期token | 任意用户 |
| mock-token-disabled-12345 | 模拟禁用用户token | 禁用用户 |

### 2. Mock测试场景示例

#### 2.1 测试无权限访问
```bash
# 使用测试管理员token访问
curl -X GET "http://localhost:3000/api/user/page" \
-H "Authorization: Bearer mock-token-test-12345"

# 预期响应
{
  "code": 403,
  "message": "无权限访问",
  "data": null
}
```

#### 2.2 测试token过期
```bash
# 使用过期token访问
curl -X GET "http://localhost:3000/api/user/page" \
-H "Authorization: Bearer mock-token-expired-12345"

# 预期响应
{
  "code": 401,
  "message": "token已过期",
  "data": null
}
```

#### 2.3 测试参数验证
```bash
# 测试空用户名
curl -X POST "http://localhost:3000/api/user" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer mock-token-admin-12345" \
-d '{
  "username": "",
  "password": "123456"
}'

# 预期响应
{
  "code": 400,
  "message": "用户名不能为空",
  "data": null
}

# 测试非法参数类型
curl -X POST "http://localhost:3000/api/user" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer mock-token-admin-12345" \
-d '{
  "username": 123,
  "password": true
}'

# 预期响应
{
  "code": 400,
  "message": "参数类型错误",
  "data": null
}
```

### 3. 注意事项

1. 这些测试token仅在Mock环境中有效
2. 每个token代表不同的用户角色和状态
3. token格式必须是 `Bearer ${token}`
4. 实际生产环境中token是动态生成的
5. Mock测试主要用于前端功能验证

### 4. 建议的测试顺序

1. 先使用正确的账号密码登录，获取token
2. 使用获取的token测试正常业务流程
3. 使用固定的测试token测试各种异常场景
4. 最后测试登出功能

### 5. 常见问题处理

1. 如果遇到"Invalid character in header"错误，检查：
   - token中是否包含中文字符
   - Authorization header格式是否正确
   - token是否完整复制

2. 如果遇到权限相关错误，检查：
   - 是否使用了正确的token
   - 当前用户是否具有对应的权限
   - 请求的API路径是否正确

3. 如果遇到参数验证错误，检查：
   - 请求参数的类型是否正确
   - 必填字段是否已填写
   - Content-Type是否设置正确 