# 前端命名规范指南 v1.0.0 (2024-02-20)

## 1. 基本原则

### 1.1 通用原则
- 见名知意：命名应当能够清晰表达其用途
- 简洁明了：避免过长或难以理解的命名
- 一致性：在整个项目中保持统一的命名风格
- 英文命名：统一使用英文，避免拼音或中英混合

### 1.2 命名风格
- 小驼峰命名法(camelCase)：`firstName`
- 大驼峰命名法(PascalCase)：`UserInfo`
- 短横线命名法(kebab-case)：`user-info`
- 下划线命名法(snake_case)：`user_info`
- 常量命名法(CONSTANT_CASE)：`MAX_COUNT`

## 2. 具体规范

### 2.1 文件和目录命名
```bash
src/
  ├── api/                 # API接口目录
  │   ├── system/         # 系统管理相关接口
  │   │   ├── user.ts
  │   │   └── role.ts
  ├── assets/             # 静态资源目录
  │   ├── images/
  │   └── styles/
  ├── components/         # 公共组件目录
  │   ├── SvgIcon/
  │   └── SearchForm/
  ├── views/              # 页面组件目录
  │   └── system/
  │       ├── user/
  │       └── role/
  └── utils/              # 工具类目录
      ├── request.ts
      └── storage.ts
```

### 2.2 Vue组件命名
```vue
<!-- 组件文件使用大驼峰命名 -->
<!-- UserForm.vue -->
<template>
  <div class="user-form">
    <!-- 模板内使用短横线命名 -->
    <el-form class="search-form">
      <el-form-item label="用户名">
        <el-input v-model="formData.username" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
// 变量使用小驼峰命名
const formData = reactive({
  username: '',
  phoneNumber: ''
})

// 方法使用小驼峰命名
const handleSubmit = () => {
  // ...
}
</script>

<style lang="scss" scoped>
// 样式类名使用短横线命名
.user-form {
  .search-form {
    .form-item {
      // ...
    }
  }
}
</style>
```

### 2.3 TypeScript类型命名
```typescript
// 接口使用大驼峰，并以I开头（可选）
interface IUserInfo {
  id: number
  userName: string
}

// 类型别名使用大驼峰，并以T开头（可选）
type TResponse<T> = {
  code: number
  data: T
  message: string
}

// 枚举使用大驼峰，并以E开头（可选）
enum EUserStatus {
  ACTIVE = 1,
  INACTIVE = 0
}
```

### 2.4 变量命名
```typescript
// 普通变量：小驼峰
const userName = 'admin'
const phoneNumber = '13800138000'

// 私有变量：下划线开头
const _privateVar = 'private'

// 常量：大写下划线
const MAX_COUNT = 100
const API_BASE_URL = '/api'

// 布尔值：以is、has、can等开头
const isVisible = true
const hasPermission = false
const canEdit = true
```

### 2.5 函数命名
```typescript
// 普通函数：小驼峰，动词开头
function getUserInfo() {}
function handleSubmit() {}
function validateForm() {}

// 事件处理函数：handle开头
function handleClick() {}
function handleChange() {}
function handleSelect() {}

// 异步函数：可以加上async前缀
async function fetchUserList() {}
async function updateUserInfo() {}
```

### 2.6 CSS命名
```scss
/* 使用短横线命名 */
.user-container {
  .header-wrapper {
    .title-text {
      font-size: 16px;
    }
  }
  
  .content-wrapper {
    .search-form {
      .form-item {
        margin-bottom: 16px;
      }
    }
    
    .table-wrapper {
      .operation-column {
        .edit-button {
          margin-right: 8px;
        }
      }
    }
  }
}
```

## 3. 最佳实践

### 3.1 组件命名
- 组件名应该是多个单词的组合
- 使用完整的单词而不是缩写
```typescript
// 好的命名
UserProfileCard.vue
SearchFilterForm.vue

// 避免的命名
User.vue
UPCard.vue
```

### 3.2 事件命名
- 使用kebab-case
- 建议使用动词或动词短语
```vue
<!-- 好的命名 -->
<template>
  <button @click="handleClick">
    <div @user-select="handleUserSelect">
</template>

<!-- 避免的命名 -->
<template>
  <button @CLICK="onClick">
    <div @USER_SELECT="userSelect">
</template>
```

### 3.3 Props命名
- 在声明时使用camelCase
- 在模板中使用kebab-case
```vue
<!-- 父组件 -->
<template>
  <user-form
    :user-name="userName"
    :phone-number="phoneNumber"
  />
</template>

<!-- 子组件 -->
<script setup lang="ts">
defineProps<{
  userName: string
  phoneNumber: string
}>()
</script>
```

## 4. ESLint配置建议

```javascript
// .eslintrc.js
module.exports = {
  rules: {
    // 强制使用驼峰命名
    'camelcase': ['error', { properties: 'never' }],
    
    // Vue组件名使用大驼峰
    'vue/component-name-in-template-casing': ['error', 'PascalCase'],
    
    // Props名使用驼峰
    'vue/prop-name-casing': ['error', 'camelCase'],
    
    // 事件名使用短横线
    'vue/custom-event-name-casing': ['error', 'kebab-case']
  }
}
```

## 5. 更新记录

### 2024-02-20
- 创建命名规范文档
- 添加基本原则
- 补充具体规范
- 添加最佳实践示例 