# RBAC系统前端项目初始化步骤

## 1. 环境检查
```bash
# 检查Node.js版本（需要v16+）
node -v  # 当前版本 v20.17.0
```

## 2. 创建项目
```bash
# 进入已有的rbac-ui目录
cd rbac-ui

# 在当前目录下创建Vue3 + TypeScript项目
npm create vite@latest . -- --template vue-ts

# 初始化项目依赖
npm install
```

## 3. 安装依赖

### 3.1 安装核心依赖
```bash
# 安装UI框架
npm install element-plus

# 安装状态管理
npm install pinia
npm install pinia-plugin-persistedstate

# 安装路由
npm install vue-router

# 安装HTTP客户端
npm install axios

# 安装工具库
npm install @vueuse/core
npm install nprogress @types/nprogress
```

### 3.2 安装开发依赖
```bash
# 安装TypeScript相关
npm install -D @types/node
npm install -D @vue/tsconfig

# 安装样式相关
npm install -D sass

# 安装自动导入插件
npm install -D unplugin-auto-import
npm install -D unplugin-vue-components

# 安装代码规范相关
npm install -D eslint
npm install -D @typescript-eslint/parser
npm install -D @typescript-eslint/eslint-plugin
npm install -D eslint-plugin-vue
npm install -D prettier
npm install -D eslint-config-prettier
npm install -D eslint-plugin-prettier
```

## 4. 项目配置

### 4.1 代码规范配置

创建 `.eslintrc.js`：
```javascript
module.exports = {
  root: true,
  env: {
    browser: true,
    node: true,
    es2021: true,
  },
  parser: 'vue-eslint-parser',
  parserOptions: {
    parser: '@typescript-eslint/parser',
    ecmaVersion: 2021,
    sourceType: 'module',
    ecmaFeatures: {
      jsx: true,
    },
  },
  extends: [
    'plugin:vue/vue3-recommended',
    'plugin:@typescript-eslint/recommended',
    'prettier',
    'plugin:prettier/recommended',
  ],
  rules: {
    'vue/multi-word-component-names': 'off',
    '@typescript-eslint/no-explicit-any': 'off',
    '@typescript-eslint/no-unused-vars': 'warn',
  },
}
```

创建 `.prettierrc`：
```json
{
  "semi": false,
  "singleQuote": true,
  "printWidth": 100,
  "trailingComma": "none",
  "arrowParens": "avoid"
}
```

### 4.2 Vite配置
修改 `vite.config.ts`：
```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia'],
      dts: 'src/types/auto-imports.d.ts',
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/types/components.d.ts',
    }),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    host: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: '@use "@/assets/styles/variables.scss" as *;'
      }
    }
  }
})
```

### 4.3 TypeScript配置
修改 `tsconfig.json`：
```json
{
  "extends": "@vue/tsconfig/tsconfig.dom.json",
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    },
    "types": ["element-plus/global", "vite/client"]
  },
  "include": ["src/**/*.ts", "src/**/*.d.ts", "src/**/*.tsx", "src/**/*.vue"],
  "references": [
    {
      "path": "./tsconfig.node.json"
    }
  ]
}
```

### 4.4 环境配置
创建 `.env.development`：
```
VITE_API_URL=http://localhost:8080
VITE_APP_TITLE=RBAC管理系统
VITE_APP_BASE_API=/api
```

创建 `.env.production`：
```
VITE_API_URL=http://api.example.com
VITE_APP_TITLE=RBAC管理系统
VITE_APP_BASE_API=/api
```

## 5. 基础设施搭建
### 5.0 创建目录结构

项目完整目录结构如下：
```
rbac-ui/
├── src/
│   ├── api/
│   │   └── modules/          # API模块目录
│   ├── assets/
│   │   ├── icons/           # 图标资源
│   │   └── styles/          # 样式文件
│   │       ├── index.scss
│   │       ├── reset.scss
│   │       └── variables.scss
│   ├── components/          # 公共组件
│   ├── composables/         # 组合式函数
│   ├── config/             # 配置文件
│   ├── directives/         # 自定义指令
│   ├── layout/             # 布局组件
│   ├── router/             # 路由配置
│   │   ├── index.ts
│   │   └── permission.ts
│   ├── store/              # 状态管理
│   │   ├── modules/
│   │   │   ├── user.ts
│   │   │   └── permission.ts
│   │   └── index.ts
│   ├── types/              # TypeScript类型定义
│   │   ├── api.d.ts
│   │   ├── user.d.ts
│   │   └── route.d.ts
│   ├── utils/              # 工具函数
│   │   ├── request.ts
│   │   └── storage.ts
│   └── views/              # 页面组件
│       ├── login/
│       ├── dashboard/
│       ├── system/
│       └── error/
```

在Windows系统中，建议手动创建以上目录结构。
### 5.1 创建目录结构
```bash
# 创建基础目录结构
mkdir -p src/{api/modules,assets/{icons,styles},components,composables,config,directives,layout,router,store/modules,types,utils,views/{login,dashboard,system,error}}

# 创建必要的文件
touch src/assets/styles/{index,reset,variables}.scss
touch src/types/{api,user,route}.d.ts
touch src/utils/{request,storage}.ts
touch src/store/index.ts
touch src/store/modules/{user,permission}.ts
touch src/router/{index,permission}.ts
```

### 5.2 样式设置
创建 `src/assets/styles/variables.scss`：
```scss
// 主题色变量
$primary-color: #409eff;
$success-color: #67c23a;
$warning-color: #e6a23c;
$danger-color: #f56c6c;
$info-color: #909399;

// 文字颜色
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$text-placeholder: #c0c4cc;

// 边框颜色
$border-color-base: #dcdfe6;
$border-color-light: #e4e7ed;
$border-color-lighter: #ebeef5;
$border-color-extra-light: #f2f6fc;

// 背景颜色
$background-color-base: #f5f7fa;

// 布局相关
$header-height: 60px;
$sidebar-width: 210px;
$sidebar-collapsed-width: 64px;
```

创建 `src/assets/styles/reset.scss`：
```scss
/* 重置样式 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  height: 100%;
}

* {
  box-sizing: border-box;
}
```

创建 `src/assets/styles/index.scss`：
```scss
@import './reset.scss';
@import './variables.scss';

// 全局样式
.app-container {
  padding: 20px;
}
```

### 5.3 工具类配置

创建 `src/utils/request.ts`：
```typescript
import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 50000,
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

// 请求拦截器
service.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers = {
        ...config.headers,
        Authorization: `Bearer ${userStore.token}`
      }
    }
    return config
  },
  (error: any) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code, message, data } = response.data
    if (code === 200) {
      return data
    } else {
      ElMessage.error(message)
      return Promise.reject(new Error(message))
    }
  },
  (error: any) => {
    if (error.response?.status === 401) {
      ElMessageBox.confirm('登录状态已过期，请重新登录', '系统提示', {
        confirmButtonText: '重新登录',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const userStore = useUserStore()
        userStore.resetToken()
        window.location.reload()
      })
    } else {
      ElMessage.error(error.message)
    }
    return Promise.reject(error)
  }
)

export default service
```

创建 `src/utils/storage.ts`：
```typescript
export const getItem = <T>(key: string): T | null => {
  const data = window.localStorage.getItem(key)
  if (!data) return null
  try {
    return JSON.parse(data) as T
  } catch (err) {
    return null
  }
}

export const setItem = (key: string, value: unknown): void => {
  if (typeof value === 'string') {
    window.localStorage.setItem(key, value)
  } else {
    window.localStorage.setItem(key, JSON.stringify(value))
  }
}

export const removeItem = (key: string): void => {
  window.localStorage.removeItem(key)
}

export const clearItems = (): void => {
  window.localStorage.clear()
}
```

### 5.4 类型定义

创建 `src/types/api.d.ts`：
```typescript
// 通用响应类型
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 分页请求参数
export interface PageQuery {
  pageNum: number
  pageSize: number
  [key: string]: unknown
}

// 分页响应数据
export interface PageResult<T> {
  total: number
  records: T[]
}
```

创建 `src/types/user.d.ts`：
```typescript
// 用户信息
export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  roles: string[]
  permissions: string[]
}

// 登录参数
export interface LoginParams {
  username: string
  password: string
  code?: string
  uuid?: string
}

// 登录响应
export interface LoginResult {
  token: string
  userInfo: UserInfo
}
```

创建 `src/types/route.d.ts`：
```typescript
import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    hidden?: boolean
    roles?: string[]
    permissions?: string[]
    keepAlive?: boolean
    affix?: boolean
  }
}
```

### 5.5 状态管理配置

创建 `src/store/index.ts`：
```typescript
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

export default pinia
```

创建 `src/store/modules/user.ts`：
```typescript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '@/types/user'
import { getItem, setItem, removeItem } from '@/utils/storage'

export const useUserStore = defineStore(
  'user',
  () => {
    const token = ref<string>(getItem('token') || '')
    const userInfo = ref<UserInfo | null>(null)

    const setToken = (value: string) => {
      token.value = value
      setItem('token', value)
    }

    const setUserInfo = (value: UserInfo) => {
      userInfo.value = value
    }

    const resetToken = () => {
      token.value = ''
      userInfo.value = null
      removeItem('token')
    }

    return {
      token,
      userInfo,
      setToken,
      setUserInfo,
      resetToken
    }
  },
  {
    persist: {
      key: 'user',
      storage: localStorage,
      paths: ['token']
    }
  }
)
```

### 5.6 路由配置

创建 `src/router/index.ts`：
```typescript
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

// 公共路由
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', hidden: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

// 白名单路由
const whiteList = ['/login', '/404']

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const hasToken = userStore.token

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      if (!userStore.userInfo) {
        try {
          // 获取用户信息
          await userStore.getUserInfo()
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败，重置token
          await userStore.resetToken()
          next(`/login?redirect=${to.path}`)
        }
      } else {
        next()
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

export default router
```

## 6. 初始化检查清单

### 6.1 环境检查
- [ ] Node.js版本 >= 16
- [ ] npm可用
- [ ] 网络连接正常（能访问npm仓库）

### 6.2 项目检查
- [ ] 依赖安装完成
- [ ] 基础配置文件创建完成
- [ ] 工具类配置完成
- [ ] 目录结构创建完成

### 6.3 配置检查
- [ ] ESLint和Prettier配置正确
- [ ] TypeScript配置正确
- [ ] 环境变量配置正确
- [ ] 路由配置正确
- [ ] 状态管理配置正确

### 6.4 开发准备检查
- [ ] IDE配置完成
- [ ] 代码规范配置完成
- [ ] API请求工具配置完成
- [ ] 权限控制配置完成

## 7. 启动项目

```bash
# 启动开发服务器
npm run dev
```

访问 http://localhost:3000 验证项目是否正常运行。 