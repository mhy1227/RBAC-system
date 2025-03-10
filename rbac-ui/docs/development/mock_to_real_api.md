# Mock 数据迁移到真实后端 API 指南

## 需求背景
当前 RBAC 管理系统使用 Mock 数据进行前端开发和测试。为了与后端服务进行集成，需要将所有 Mock 数据实现替换为真实的后端 API 调用。

## 影响范围
1. 用户管理模块
   - 用户列表
   - 用户详情
   - 用户编辑
   - 用户删除
   
2. 角色管理模块
   - 角色列表
   - 角色详情
   - 角色编辑
   - 角色删除
   
3. 权限管理模块
   - 权限分配
   - 权限查询

## 迁移步骤

### 1. 准备工作
1. **接口文档确认**
   - 获取完整的后端 API 文档
   - 确认所有接口的 URL、请求方法、参数格式
   - 确认返回数据的结构和格式
   - 确认错误处理机制和状态码

2. **环境准备**
   - 配置开发环境的后端服务地址
   - 配置测试环境的后端服务地址
   - 确认网络连接和跨域设置

### 2. 代码修改

#### 2.1 修改 API 调用
1. **移除 Mock 实现**
   ```typescript
   // 修改前
   export function getUserInfo(id: number): Promise<UserInfo> {
     if (IS_MOCK) {
       const user = mockUserListData.find(item => item.id === id)
       if (!user) {
         return Promise.reject(new Error('用户不存在'))
       }
       return Promise.resolve(user)
     }
     return request.get<UserInfo>(`/user/${id}`)
   }

   // 修改后
   export function getUserInfo(id: number): Promise<UserInfo> {
     return request.get<UserInfo>(`/user/${id}`)
   }
   ```

2. **统一错误处理**
   ```typescript
   // 在 request.ts 中添加统一的错误处理
   request.interceptors.response.use(
     response => response.data,
     error => {
       if (error.response) {
         switch (error.response.status) {
           case 404:
             // 处理资源不存在
             break
           case 401:
             // 处理未授权
             break
           case 403:
             // 处理禁止访问
             break
           default:
             // 处理其他错误
         }
       }
       return Promise.reject(error)
     }
   )
   ```

#### 2.2 数据格式适配
1. **类型声明更新**
   ```typescript
   // 根据后端返回的数据结构更新类型声明
   export interface ApiResponse<T> {
     code: number
     data: T
     message: string
   }

   export interface UserInfo {
     // 更新字段定义以匹配后端
   }
   ```

2. **数据转换处理**
   ```typescript
   // 如果后端返回的数据格式与前端不一致，添加数据转换层
   function transformUserData(apiUser: ApiUser): UserInfo {
     return {
       id: apiUser.userId,
       username: apiUser.userName,
       // ... 其他字段转换
     }
   }
   ```

### 3. 测试验证

#### 3.1 接口测试
1. **单接口测试**
   - 验证每个接口的请求和响应
   - 确认数据格式的正确性
   - 测试错误处理机制

2. **功能测试**
   - 测试完整的业务流程
   - 验证数据的连续性和一致性
   - 确认界面展示的正确性

#### 3.2 异常测试
1. **网络异常**
   - 测试网络断开情况
   - 测试请求超时情况
   - 验证错误提示的友好性

2. **数据异常**
   - 测试数据不存在的情况
   - 测试数据格式错误的情况
   - 测试数据权限受限的情况

### 4. 环境配置

#### 4.1 配置文件更新
1. **环境变量**
   ```typescript
   // .env.development
   VITE_API_BASE_URL=http://dev-api.example.com
   VITE_USE_MOCK=false

   // .env.production
   VITE_API_BASE_URL=http://api.example.com
   VITE_USE_MOCK=false
   ```

2. **请求配置**
   ```typescript
   // request.ts
   const request = axios.create({
     baseURL: import.meta.env.VITE_API_BASE_URL,
     timeout: 10000,
     headers: {
       'Content-Type': 'application/json'
     }
   })
   ```

## 注意事项

### 1. 数据一致性
- 确保前端展示字段与后端返回字段完全匹配
- 注意数据类型的转换（如日期格式、数字类型等）
- 处理可能的 null 或 undefined 值

### 2. 错误处理
- 实现统一的错误处理机制
- 提供友好的错误提示
- 考虑网络异常、超时等特殊情况

### 3. 性能优化
- 合理设置请求超时时间
- 实现请求缓存机制（如果需要）
- 考虑数据预加载策略

### 4. 安全性
- 实现统一的认证机制
- 处理敏感数据的传输
- 防止 XSS 和 CSRF 攻击

## 回滚方案

### 1. 保留 Mock 实现
- 保留原有的 Mock 数据实现
- 通过环境变量控制是否使用 Mock 数据
- 在出现问题时可快速回滚

### 2. 版本控制
- 在 Git 中创建迁移分支
- 完整测试通过后再合并到主分支
- 保留回滚点

## 进度管理

### 1. 迁移顺序
1. 用户管理模块
2. 角色管理模块
3. 权限管理模块
4. 其他功能模块

### 2. 测试计划
1. 单元测试
2. 集成测试
3. 端到端测试
4. 性能测试

## 总结
通过以上步骤，可以确保 Mock 数据到真实后端 API 的平滑迁移。在迁移过程中，需要特别注意数据一致性、错误处理、性能优化和安全性等方面的问题。同时，保留回滚方案也是必要的，以应对可能出现的问题。 