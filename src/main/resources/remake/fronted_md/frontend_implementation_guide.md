# RBAC系统前端实现指南

## 1. 项目初始化

### 1.1 创建项目
```bash
# 使用 Vue CLI 创建项目
npm create vue@latest rbac-admin

# 选择以下配置
✓ TypeScript
✓ JSX
✓ Vue Router
✓ Pinia
✓ ESLint
✓ Prettier

# 安装依赖
cd rbac-ui
npm install
```

### 1.2 添加必要依赖
```bash
# UI组件库
npm install element-plus

# HTTP请求
npm install axios

# 工具库
npm install lodash-es
npm install dayjs

# 类型定义
npm install @types/lodash-es -D
```

## 2. 项目结构设计

```bash
src/
├── api/                 # API接口
│   ├── auth.ts         # 认证相关
│   ├── user.ts         # 用户管理
│   ├── role.ts         # 角色管理
│   └── permission.ts   # 权限管理
├── assets/             # 静态资源
│   ├── images/         # 图片资源
│   └── styles/         # 样式文件
├── components/         # 公共组件
│   ├── layout/         # 布局组件
│   │   ├── Sidebar.vue    # 侧边栏
│   │   ├── Header.vue     # 顶部栏
│   │   └── Footer.vue     # 底部栏
│   ├── common/         # 通用组件
│   │   ├── Table.vue      # 表格组件
│   │   ├── Form.vue       # 表单组件
│   │   └── Dialog.vue     # 弹窗组件
│   └── business/       # 业务组件
├── router/             # 路由配置
│   ├── index.ts        # 路由入口
│   ├── routes.ts       # 路由定义
│   └── guard.ts        # 路由守卫
├── store/              # 状态管理
│   ├── index.ts        # store入口
│   ├── modules/        # 模块
│   │   ├── user.ts     # 用户模块
│   │   ├── permission.ts # 权限模块
│   │   └── app.ts      # 应用配置
│   └── types.ts        # 类型定义
├── utils/              # 工具函数
│   ├── request.ts      # axios封装
│   ├── auth.ts         # 认证工具
│   ├── validate.ts     # 验证工具
│   └── common.ts       # 通用工具
├── views/              # 页面组件
│   ├── login/          # 登录相关
│   ├── dashboard/      # 控制台
│   ├── system/         # 系统管理
│   │   ├── user/       # 用户管理
│   │   ├── role/       # 角色管理
│   │   └── permission/ # 权限管理
│   └── profile/        # 个人中心
└── types/              # 类型定义
    ├── api.d.ts        # API类型
    ├── store.d.ts      # Store类型
    └── common.d.ts     # 通用类型
```

## 3. 核心功能实现

### 3.1 网络请求封装
```typescript
// src/utils/request.ts
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/store/modules/user';

const service: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 15000
});

// 请求拦截器
service.interceptors.request.use(
    (config: AxiosRequestConfig) => {
        const userStore = useUserStore();
        if (userStore.token) {
            config.headers['Authorization'] = \`Bearer \${userStore.token}\`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    (response: AxiosResponse) => {
        const { code, message, data } = response.data;
        if (code === 200) {
            return data;
        } else {
            ElMessage.error(message);
            return Promise.reject(new Error(message));
        }
    },
    (error) => {
        if (error.response?.status === 401) {
            const userStore = useUserStore();
            userStore.logout();
        }
        ElMessage.error(error.message);
        return Promise.reject(error);
    }
);

export default service;
```

### 3.2 状态管理实现
```typescript
// src/store/modules/user.ts
import { defineStore } from 'pinia';
import { login, logout, getUserInfo } from '@/api/auth';
import { LoginParams, UserInfo } from '@/types/api';

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        userInfo: null as UserInfo | null,
        permissions: [] as string[]
    }),
    
    actions: {
        async login(params: LoginParams) {
            try {
                const { token, user } = await login(params);
                this.token = token;
                this.userInfo = user;
                localStorage.setItem('token', token);
                return true;
            } catch (error) {
                return false;
            }
        },
        
        async getUserInfo() {
            try {
                const data = await getUserInfo();
                this.userInfo = data;
                this.permissions = data.permissions;
                return true;
            } catch (error) {
                return false;
            }
        },
        
        async logout() {
            try {
                await logout();
                this.resetState();
                return true;
            } catch (error) {
                return false;
            }
        },
        
        resetState() {
            this.token = '';
            this.userInfo = null;
            this.permissions = [];
            localStorage.removeItem('token');
        }
    }
});
```

### 3.3 路由配置
```typescript
// src/router/routes.ts
import { RouteRecordRaw } from 'vue-router';

export const constantRoutes: RouteRecordRaw[] = [
    {
        path: '/login',
        component: () => import('@/views/login/index.vue'),
        meta: { title: '登录' }
    },
    {
        path: '/',
        component: () => import('@/components/layout/index.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                component: () => import('@/views/dashboard/index.vue'),
                meta: { title: '控制台', icon: 'dashboard' }
            }
        ]
    }
];

export const asyncRoutes: RouteRecordRaw[] = [
    {
        path: '/system',
        component: () => import('@/components/layout/index.vue'),
        meta: { title: '系统管理', icon: 'setting' },
        children: [
            {
                path: 'user',
                component: () => import('@/views/system/user/index.vue'),
                meta: { title: '用户管理', permission: 'sys:user:list' }
            },
            {
                path: 'role',
                component: () => import('@/views/system/role/index.vue'),
                meta: { title: '角色管理', permission: 'sys:role:list' }
            },
            {
                path: 'permission',
                component: () => import('@/views/system/permission/index.vue'),
                meta: { title: '权限管理', permission: 'sys:permission:list' }
            }
        ]
    }
];
```

### 3.4 权限控制实现
```typescript
// src/utils/permission.ts
import { useUserStore } from '@/store/modules/user';

export const checkPermission = (permission: string | string[]): boolean => {
    const userStore = useUserStore();
    const permissions = userStore.permissions;
    
    if (Array.isArray(permission)) {
        return permission.some(item => permissions.includes(item));
    }
    return permissions.includes(permission);
};

// 权限指令
export const permissionDirective = {
    mounted(el: HTMLElement, binding: any) {
        const permission = binding.value;
        if (!checkPermission(permission)) {
            el.parentNode?.removeChild(el);
        }
    }
};
```

## 4. 页面实现示例

### 4.1 登录页面
```vue
<!-- src/views/login/index.vue -->
<template>
    <div class="login-container">
        <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
        >
            <h3 class="title">RBAC权限管理系统</h3>
            <el-form-item prop="username">
                <el-input
                    v-model="loginForm.username"
                    placeholder="用户名"
                    type="text"
                />
            </el-form-item>
            <el-form-item prop="password">
                <el-input
                    v-model="loginForm.password"
                    placeholder="密码"
                    type="password"
                />
            </el-form-item>
            <el-form-item>
                <el-button
                    :loading="loading"
                    type="primary"
                    style="width: 100%"
                    @click="handleLogin"
                >
                    登录
                </el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/modules/user';
import type { FormInstance } from 'element-plus';

const router = useRouter();
const userStore = useUserStore();
const loginFormRef = ref<FormInstance>();
const loading = ref(false);

const loginForm = reactive({
    username: '',
    password: ''
});

const loginRules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

const handleLogin = async () => {
    if (!loginFormRef.value) return;
    
    await loginFormRef.value.validate();
    loading.value = true;
    
    try {
        const success = await userStore.login(loginForm);
        if (success) {
            await router.push('/');
        }
    } finally {
        loading.value = false;
    }
};
</script>

<style lang="scss" scoped>
.login-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f0f2f5;
    
    .login-form {
        width: 400px;
        padding: 40px;
        background: #fff;
        border-radius: 4px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        
        .title {
            text-align: center;
            margin-bottom: 30px;
        }
    }
}
</style>
```

### 4.2 用户管理页面
```vue
<!-- src/views/system/user/index.vue -->
<template>
    <div class="app-container">
        <!-- 搜索区域 -->
        <el-form :model="queryParams" ref="queryForm" :inline="true">
            <el-form-item label="用户名" prop="username">
                <el-input
                    v-model="queryParams.username"
                    placeholder="请输入用户名"
                    clearable
                />
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-select v-model="queryParams.status" clearable>
                    <el-option label="启用" :value="1" />
                    <el-option label="禁用" :value="0" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleQuery">查询</el-button>
                <el-button @click="resetQuery">重置</el-button>
            </el-form-item>
        </el-form>

        <!-- 操作按钮区域 -->
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button
                    type="primary"
                    v-permission="'sys:user:add'"
                    @click="handleAdd"
                >
                    新增
                </el-button>
            </el-col>
        </el-row>

        <!-- 表格区域 -->
        <el-table
            v-loading="loading"
            :data="userList"
            border
        >
            <el-table-column label="用户名" prop="username" />
            <el-table-column label="昵称" prop="nickname" />
            <el-table-column label="邮箱" prop="email" />
            <el-table-column label="手机号" prop="phone" />
            <el-table-column label="状态" prop="status">
                <template #default="{ row }">
                    <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                        {{ row.status === 1 ? '启用' : '禁用' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="创建时间" prop="createTime" />
            <el-table-column label="操作" width="180">
                <template #default="{ row }">
                    <el-button
                        v-permission="'sys:user:edit'"
                        type="primary"
                        link
                        @click="handleEdit(row)"
                    >
                        编辑
                    </el-button>
                    <el-button
                        v-permission="'sys:user:delete'"
                        type="danger"
                        link
                        @click="handleDelete(row)"
                    >
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页区域 -->
        <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.size"
            :total="total"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />

        <!-- 用户表单对话框 -->
        <el-dialog
            :title="dialog.title"
            v-model="dialog.visible"
            width="500px"
            append-to-body
        >
            <el-form
                ref="userFormRef"
                :model="userForm"
                :rules="userRules"
                label-width="80px"
            >
                <el-form-item label="用户名" prop="username">
                    <el-input
                        v-model="userForm.username"
                        placeholder="请输入用户名"
                        :disabled="userForm.id !== undefined"
                    />
                </el-form-item>
                <el-form-item
                    label="密码"
                    prop="password"
                    v-if="userForm.id === undefined"
                >
                    <el-input
                        v-model="userForm.password"
                        type="password"
                        placeholder="请输入密码"
                    />
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                    <el-input
                        v-model="userForm.nickname"
                        placeholder="请输入昵称"
                    />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input
                        v-model="userForm.email"
                        placeholder="请输入邮箱"
                    />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    <el-input
                        v-model="userForm.phone"
                        placeholder="请输入手机号"
                    />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="userForm.status">
                        <el-radio :label="1">启用</el-radio>
                        <el-radio :label="0">禁用</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="dialog.visible = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">确定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getUserList, addUser, updateUser, deleteUser } from '@/api/user';

// 查询参数
const queryParams = reactive({
    page: 1,
    size: 10,
    username: '',
    status: undefined
});

// 用户列表数据
const userList = ref([]);
const total = ref(0);
const loading = ref(false);

// 对话框数据
const dialog = reactive({
    visible: false,
    title: ''
});

// 表单数据
const userFormRef = ref();
const userForm = reactive({
    id: undefined,
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    status: 1
});

// 表单校验规则
const userRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
    ],
    email: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ]
};

// 获取用户列表
const getList = async () => {
    loading.value = true;
    try {
        const { records, total: totalCount } = await getUserList(queryParams);
        userList.value = records;
        total.value = totalCount;
    } finally {
        loading.value = false;
    }
};

// 查询按钮点击
const handleQuery = () => {
    queryParams.page = 1;
    getList();
};

// 重置按钮点击
const resetQuery = () => {
    queryParams.username = '';
    queryParams.status = undefined;
    handleQuery();
};

// 新增按钮点击
const handleAdd = () => {
    dialog.title = '新增用户';
    dialog.visible = true;
    userForm.id = undefined;
    userForm.username = '';
    userForm.password = '';
    userForm.nickname = '';
    userForm.email = '';
    userForm.phone = '';
    userForm.status = 1;
};

// 编辑按钮点击
const handleEdit = (row: any) => {
    dialog.title = '编辑用户';
    dialog.visible = true;
    Object.assign(userForm, row);
};

// 删除按钮点击
const handleDelete = async (row: any) => {
    try {
        await ElMessageBox.confirm('确认要删除该用户吗？', '提示', {
            type: 'warning'
        });
        await deleteUser(row.id);
        ElMessage.success('删除成功');
        getList();
    } catch (error) {
        // 用户取消删除
    }
};

// 提交表单
const submitForm = async () => {
    if (!userFormRef.value) return;
    
    await userFormRef.value.validate();
    
    try {
        if (userForm.id === undefined) {
            await addUser(userForm);
            ElMessage.success('新增成功');
        } else {
            await updateUser(userForm);
            ElMessage.success('修改成功');
        }
        dialog.visible = false;
        getList();
    } catch (error) {
        // 错误处理
    }
};

// 分页大小改变
const handleSizeChange = (val: number) => {
    queryParams.size = val;
    getList();
};

// 页码改变
const handleCurrentChange = (val: number) => {
    queryParams.page = val;
    getList();
};

// 初始化
onMounted(() => {
    getList();
});
</script>

<style lang="scss" scoped>
.app-container {
    padding: 20px;
}

.mb8 {
    margin-bottom: 8px;
}
</style>
```

## 5. 样式主题配置

### 5.1 Element Plus主题配置
```scss
// src/assets/styles/element-variables.scss
@forward 'element-plus/theme-chalk/src/common/var.scss' with (
  $colors: (
    'primary': (
      'base': #409eff,
    ),
    'success': (
      'base': #67c23a,
    ),
    'warning': (
      'base': #e6a23c,
    ),
    'danger': (
      'base': #f56c6c,
    ),
    'info': (
      'base': #909399,
    ),
  )
);
```

### 5.2 全局样式
```scss
// src/assets/styles/global.scss
html,
body {
    height: 100%;
    margin: 0;
    padding: 0;
    -moz-osx-font-smoothing: grayscale;
    -webkit-font-smoothing: antialiased;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
        'Helvetica Neue', Arial, 'Noto Sans', sans-serif, 'Apple Color Emoji',
        'Segoe UI Emoji', 'Segoe UI Symbol', 'Noto Color Emoji';
}

#app {
    height: 100%;
}

*,
*:before,
*:after {
    box-sizing: border-box;
}

a,
a:focus,
a:hover {
    color: inherit;
    outline: none;
    text-decoration: none;
}

div:focus {
    outline: none;
}

.clearfix {
    &:after {
        content: '';
        display: table;
        clear: both;
    }
}
```

## 6. 构建与部署

### 6.1 构建配置
```typescript
// vite.config.ts
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src')
        }
    },
    css: {
        preprocessorOptions: {
            scss: {
                additionalData: \`@use "@/assets/styles/element-variables.scss" as *;\`
            }
        }
    },
    server: {
        port: 3000,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    },
    build: {
        outDir: 'dist',
        assetsDir: 'assets',
        sourcemap: false,
        terserOptions: {
            compress: {
                drop_console: true,
                drop_debugger: true
            }
        }
    }
});
```

### 6.2 Docker部署
```dockerfile
# Dockerfile
FROM node:16 as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

```nginx
# nginx.conf
server {
    listen 80;
    server_name localhost;

    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://backend:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 7. 开发流程建议

1. **基础框架搭建**
   - 项目初始化
   - 依赖安装
   - 目录结构创建
   - 基础配置

2. **公共功能实现**
   - 网络请求封装
   - 路由配置
   - 状态管理
   - 权限控制

3. **布局开发**
   - 整体布局
   - 菜单实现
   - 头部组件
   - 面包屑导航

4. **功能模块开发**
   - 登录模块
   - 用户管理
   - 角色管理
   - 权限管理

5. **优化与测试**
   - 性能优化
   - 代码检查
   - 单元测试
   - 兼容性测试

## 8. 注意事项

1. **代码规范**
   - 使用TypeScript
   - 遵循ESLint规则
   - 编写注释
   - 代码格式化

2. **性能优化**
   - 路由懒加载
   - 组件按需加载
   - 合理使用缓存
   - 避免重复请求

3. **安全考虑**
   - Token管理
   - 敏感信息加密
   - XSS防护
   - CSRF防护

4. **用户体验**
   - 加载状态
   - 错误提示
   - 操作反馈
   - 响应式适配 