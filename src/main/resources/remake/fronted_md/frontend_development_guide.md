# RBAC系统前端开发指南

## 1. API文档访问

### 1.1 Swagger文档
- 访问地址：`http://localhost:8080/swagger-ui.html`
- 接口分组：
  * 用户管理（/user/**）
  * 角色管理（/role/**）
  * 权限管理（/permission/**）
  * 认证管理（/auth/**）
  * 日志管理（/log/**）

### 1.2 认证说明
- 除登录接口外，所有接口都需要携带token
- Token格式：`Authorization: Bearer {token}`
- Token过期时间：12小时
- 支持Token自动续期

## 2. 接口对应关系

### 2.1 认证管理（AuthController）
```javascript
// 登录
POST /auth/login
Request: {
    username: string,
    password: string
}
Response: {
    token: string,
    user: {
        id: number,
        username: string,
        // ...其他用户信息
    }
}

// 登出
POST /auth/logout

// 获取当前用户信息
GET /auth/info

// 刷新token
POST /auth/refresh
Request: {
    refreshToken: string
}
```

### 2.2 用户管理（SysUserController）
```javascript
// 分页查询用户列表
GET /user/page
Params: {
    page: number,
    size: number,
    username?: string,
    status?: number
}

// 获取用户详情
GET /user/{id}

// 新增用户
POST /user
Request: {
    username: string,
    password: string,
    nickname?: string,
    email?: string,
    phone?: string
}

// 修改用户
PUT /user
Request: {
    id: number,
    nickname?: string,
    email?: string,
    phone?: string
}

// 删除用户
DELETE /user/{id}

// 修改用户状态
PUT /user/{id}/status/{status}
```

### 2.3 角色管理（SysRoleController）
```javascript
// 分页查询角色列表
GET /role/page
Params: {
    page: number,
    size: number,
    roleName?: string,
    status?: number
}

// 获取角色详情
GET /role/{id}

// 新增角色
POST /role
Request: {
    roleName: string,
    roleCode: string,
    description?: string
}

// 修改角色
PUT /role

// 删除角色
DELETE /role/{id}

// 分配角色权限
POST /role/{roleId}/permission
Request: number[]  // 权限ID列表
```

### 2.4 权限管理（SysPermissionController）
```javascript
// 获取权限树
GET /permission/tree

// 获取权限列表
GET /permission/list
Params: {
    type?: string,
    status?: number
}

// 新增权限
POST /permission

// 修改权限
PUT /permission

// 删除权限
DELETE /permission/{id}
```

## 3. 统一响应格式

```javascript
{
    code: number,       // 响应码：200成功，其他表示错误
    message: string,    // 响应消息
    data: any          // 响应数据
}
```

## 4. 前端开发建议

### 4.1 技术选型建议
1. **框架选择**
   - Vue3 + TypeScript
   - React + TypeScript
   - 建议使用TypeScript增加代码健壮性

2. **UI组件库**
   - Element Plus（Vue3）
   - Ant Design（React）
   - 建议选择生态完善的组件库

3. **状态管理**
   - Pinia（Vue3）
   - Redux/Mobx（React）
   - 用于管理用户信息、权限等全局状态

### 4.2 项目结构建议
```
src/
├── api/                # API接口封装
│   ├── auth.ts        # 认证相关接口
│   ├── user.ts        # 用户管理接口
│   ├── role.ts        # 角色管理接口
│   └── permission.ts  # 权限管理接口
├── components/        # 公共组件
├── views/            # 页面组件
├── store/            # 状态管理
├── utils/            # 工具函数
└── router/           # 路由配置
```

### 4.3 开发顺序建议
1. **基础框架搭建**（2-3天）
   - 项目初始化
   - 路由配置
   - 状态管理
   - 请求封装
   - 权限控制

2. **认证模块**（2-3天）
   - 登录页面
   - Token管理
   - 用户信息管理
   - 路由守卫

3. **用户管理模块**（3-4天）
   - 用户列表
   - 用户添加/编辑
   - 用户删除
   - 状态管理

4. **角色管理模块**（3-4天）
   - 角色列表
   - 角色添加/编辑
   - 角色删除
   - 权限分配

5. **权限管理模块**（3-4天）
   - 权限树
   - 权限添加/编辑
   - 权限删除
   - 按钮权限控制

6. **系统功能完善**（3-4天）
   - 日志管理
   - 个人中心
   - 系统设置

### 4.4 权限控制实现
```typescript
// 权限指令示例（Vue3）
const hasPermission = (permission: string) => {
    const store = useStore();
    return store.permissions.includes(permission);
};

// 按钮权限示例
<button v-if="hasPermission('sys:user:add')">
    添加用户
</button>

// 路由权限示例
const router = createRouter({
    routes: [
        {
            path: '/user',
            component: UserList,
            meta: {
                permission: 'sys:user:query'
            }
        }
    ]
});
```

### 4.5 注意事项
1. **接口调用**
   - 统一处理请求/响应拦截
   - 统一处理错误信息
   - 实现请求重试机制
   - 添加请求loading效果

2. **数据处理**
   - 统一的数据格式化
   - 统一的时间处理
   - 统一的数字处理
   - 做好数据缓存

3. **UI交互**
   - 统一的提示信息
   - 统一的确认框
   - 统一的表单验证
   - 友好的加载效果

4. **性能优化**
   - 路由懒加载
   - 组件按需加载
   - 图片懒加载
   - 合理的缓存策略

## 5. 开发规范

### 5.1 命名规范
- 文件名：小写字母，多个单词用-连接
- 组件名：大驼峰命名
- 变量名：小驼峰命名
- 常量名：大写字母，多个单词用_连接

### 5.2 代码规范
- 使用ESLint进行代码检查
- 使用Prettier进行代码格式化
- 遵循项目既定的代码风格
- 编写必要的注释

### 5.3 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试用例
- chore: 其他修改

## 6. 部署相关

### 6.1 构建配置
```javascript
// vite.config.js 示例
export default defineConfig({
    base: '/',
    build: {
        outDir: 'dist',
        assetsDir: 'assets',
        sourcemap: false
    },
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    }
});
```

### 6.2 环境配置
```javascript
// .env.development
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=RBAC系统(开发环境)

// .env.production
VITE_API_BASE_URL=http://api.example.com
VITE_APP_TITLE=RBAC系统
```

## 7. 测试建议

### 7.1 单元测试
- 编写组件测试
- 编写工具函数测试
- 编写状态管理测试
- 使用Jest或Vitest

### 7.2 E2E测试
- 关键功能流程测试
- 使用Cypress或Playwright
- 覆盖主要业务场景
- 自动化测试脚本 