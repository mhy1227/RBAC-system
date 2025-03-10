# Mock数据开发指南 v1.0.0 (2024-02-20)

## 一、概述

### 1.1 目的
提供一个独立于后端的前端开发和测试环境，通过模拟API响应数据，使前端开发可以独立进行，提高开发效率。

### 1.2 技术栈版本
- Vue: 3.4.15
- TypeScript: 5.3.3
- Vite: 5.0.12
- Pinia: 2.1.7
- Element Plus: 2.5.3

## 二、Mock数据结构

### 2.1 基础响应格式
```typescript
interface ApiResponse<T> {
  code: number      // 状态码
  message: string   // 消息
  data: T          // 数据
}
```

### 2.2 核心数据模型

#### 2.2.1 用户认证相关
```typescript
// 登录响应
const mockLoginResponse = {
  code: 200,
  data: {
    token: 'mock_token_2024',
    user: {
      id: 1,
      username: 'admin',
      nickname: '管理员',
      avatar: 'https://example.com/avatar.jpg',
      roles: ['admin'],
      permissions: ['*:*:*']
    }
  },
  message: 'success'
}

// 用户信息响应
const mockUserInfoResponse = {
  code: 200,
  data: {
    id: 1,
    username: 'admin',
    nickname: '管理员',
    email: 'admin@example.com',
    phone: '13800138000',
    avatar: 'https://example.com/avatar.jpg',
    status: 1,
    roles: ['admin'],
    permissions: ['*:*:*'],
    createTime: '2024-02-20 10:00:00'
  },
  message: 'success'
}
```

#### 2.2.2 权限相关
```typescript
// 权限树响应
const mockPermissionTreeResponse = {
  code: 200,
  data: [
    {
      id: 1,
      name: '系统管理',
      code: 'system',
      type: 'menu',
      children: [
        {
          id: 2,
          name: '用户管理',
          code: 'system:user',
          type: 'menu',
          children: [
            {
              id: 3,
              name: '查询用户',
              code: 'system:user:query',
              type: 'button'
            }
          ]
        }
      ]
    }
  ],
  message: 'success'
}
```

## 三、实现方案

### 3.1 Mock数据实现
```typescript
// src/mock/index.ts
import type { LoginParams, LoginResult, UserInfo } from '@/types/user'

export class MockService {
  // 模拟延迟
  private static async delay(ms: number = 1000) {
    return new Promise(resolve => setTimeout(resolve, ms))
  }

  // 登录接口
  static async login(params: LoginParams): Promise<LoginResult> {
    await this.delay()
    
    if (params.username === 'admin' && params.password === '123456') {
      return mockLoginResponse.data
    }
    throw new Error('用户名或密码错误')
  }

  // 获取用户信息
  static async getUserInfo(): Promise<UserInfo> {
    await this.delay()
    return mockUserInfoResponse.data
  }
}
```

### 3.2 集成到API层
```typescript
// src/api/auth.ts
import { MockService } from '@/mock'
import type { LoginParams, LoginResult, UserInfo } from '@/types/user'

const IS_MOCK = true // 控制是否使用mock数据

export function login(data: LoginParams) {
  if (IS_MOCK) {
    return MockService.login(data)
  }
  return request.post<LoginResult>('/auth/login', data)
}

export function getUserInfo() {
  if (IS_MOCK) {
    return MockService.getUserInfo()
  }
  return request.get<UserInfo>('/auth/info')
}
```

## 四、使用指南

### 4.1 开启Mock模式
1. 在 `.env.development` 中添加配置：
```bash
VITE_USE_MOCK=true
```

2. 在API调用层判断：
```typescript
const IS_MOCK = import.meta.env.VITE_USE_MOCK === 'true'
```

### 4.2 Mock数据测试
```typescript
// 测试登录功能
const testLogin = async () => {
  try {
    // 成功场景
    const successResult = await MockService.login({
      username: 'admin',
      password: '123456'
    })
    console.log('登录成功:', successResult)

    // 失败场景
    await MockService.login({
      username: 'wrong',
      password: 'wrong'
    })
  } catch (error) {
    console.error('登录失败:', error.message)
  }
}
```

### 4.3 测试用例
```typescript
// 测试数据集
const mockTestCases = {
  login: [
    {
      name: '正确凭据',
      input: { username: 'admin', password: '123456' },
      expectSuccess: true
    },
    {
      name: '错误凭据',
      input: { username: 'wrong', password: 'wrong' },
      expectSuccess: false
    }
  ]
}
```

## 五、最佳实践

### 5.1 Mock数据原则
1. 数据真实性
   - 模拟真实的数据结构
   - 保持与后端API一致的响应格式
   - 包含各种边界情况

2. 延迟模拟
   - 添加适当的响应延迟
   - 模拟网络请求的真实情况
   - 便于测试加载状态

3. 错误处理
   - 模拟各种错误情况
   - 测试错误提示
   - 验证异常处理逻辑

### 5.2 开发建议
1. 分离关注点
   - Mock数据与业务逻辑分离
   - 便于切换真实/模拟数据
   - 保持代码整洁

2. 类型安全
   - 使用TypeScript类型
   - 保持与实际API类型一致
   - 提早发现类型错误

3. 注释完善
   - 标注数据来源
   - 说明特殊处理
   - 记录更新历史

## 六、注意事项

### 6.1 安全考虑
- 不要在mock数据中包含敏感信息
- 生产环境必须关闭mock模式
- 注意数据脱敏处理

### 6.2 维护建议
- 定期与后端同步API变更
- 及时更新mock数据结构
- 保持测试用例的有效性

## 七、更新记录

### v1.0.0 (2024-02-20)
- 创建初始文档
- 添加基础Mock数据结构
- 实现用户认证相关mock
- 添加使用指南和最佳实践 