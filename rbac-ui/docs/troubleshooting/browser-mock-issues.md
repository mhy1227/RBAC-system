# 浏览器与Apifox测试差异问题分析与解决方案

## 一、问题描述

在使用Mock服务进行测试时，发现通过Apifox和浏览器测试的结果存在差异：
- Apifox测试：所有接口均正常响应
- 浏览器测试：部分接口出现异常

## 二、差异原因分析

### 1. 请求路径处理差异
- Apifox：直接发送完整URL请求（如：`http://localhost:3000/api/xxx`）
- 浏览器：通过axios发送请求，经过baseURL和请求路径拼接处理

### 2. 请求头处理差异
- Apifox：手动设置完整的请求头
- 浏览器：自动携带默认请求头，token通过拦截器注入

### 3. Mock服务工作机制
- Apifox：直接HTTP请求
- 浏览器：通过Vite开发服务器的中间件处理

## 三、解决方案

### 1. 统一请求配置
```typescript
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '/api',
  timeout: 10000
})
```

### 2. 规范化请求路径
```typescript
const request = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    // 确保url以/开头
    url = url.startsWith('/') ? url : `/${url}`
    return service.get(url, config).then(res => res.data)
  }
  // ...其他方法类似
}
```

### 3. 统一响应处理
```typescript
service.interceptors.response.use(
  (response: AxiosResponse<BaseResponse>) => {
    const res = response.data
    if (res.code !== 200) {
      // 统一错误处理
      return Promise.reject(new Error(res.message))
    }
    return res
  }
)
```

## 四、测试验证步骤

### 1. 接口测试
1. 登录接口
```bash
# 浏览器端
POST /api/auth/login
{
  "username": "admin",
  "password": "123456"
}

# 预期响应
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "mock-token-xxx",
    "user": {/*用户信息*/}
  }
}
```

2. 用户列表接口
```bash
# 浏览器端
GET /api/user/page?pageNum=1&pageSize=10

# 预期响应
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 0
  }
}
```

### 2. 调试方法
1. 开启开发者工具，查看Network面板
2. 检查请求URL是否正确
3. 验证请求头信息
4. 查看响应数据结构

## 五、常见问题处理

### 1. 接口404错误
- 检查请求路径是否正确
- 验证baseURL配置
- 确认Mock服务是否正常运行

### 2. 响应格式错误
- 检查响应拦截器处理逻辑
- 验证Mock数据格式
- 确保返回数据结构一致

### 3. 权限验证失败
- 检查token格式
- 验证token注入逻辑
- 确认Mock服务的token验证规则

## 六、最佳实践建议

### 1. 开发阶段
- 使用环境变量控制Mock服务
- 统一请求和响应处理
- 添加详细的调试日志

### 2. 测试阶段
- 先进行Apifox测试
- 再进行浏览器测试
- 对比请求和响应差异

### 3. 部署阶段
- 确保生产环境关闭Mock
- 验证实际后端接口
- 清理调试代码

## 七、注意事项

1. **环境变量配置**
   - 开发环境：`VITE_USE_MOCK=true`
   - 生产环境：`VITE_USE_MOCK=false`

2. **请求路径规范**
   - 统一使用相对路径
   - 保持与API文档一致
   - 避免硬编码完整URL

3. **错误处理**
   - 统一错误提示
   - 详细错误日志
   - 优雅降级处理 