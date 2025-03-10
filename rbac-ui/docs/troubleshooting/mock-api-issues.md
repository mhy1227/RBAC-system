# RBAC系统前端Mock服务错误分析与解决方案

## 一、问题描述

在使用Apifox测试RBAC系统前端API时，发现部分接口返回ECONNREFUSED错误，具体表现为：

### 1. 用户管理模块
- 批量删除用户接口 `/user/batch` 返回 ECONNREFUSED 错误
- 检查用户名是否存在的接口 `/user/check?username=admin` 返回 ECONNREFUSED 错误

### 2. 角色管理模块
- 批量删除角色接口 `/role/batch` 返回 ECONNREFUSED 错误
- 更新角色状态接口 `/role/1/status/0` 返回 ECONNREFUSED 错误

### 3. 权限管理模块
- 更新权限接口 `/permission/1` 返回 ECONNREFUSED 错误
- 批量删除权限接口 `/permission/batch` 返回 ECONNREFUSED 错误
- 更新权限状态接口 `/permission/1/status` 返回 ECONNREFUSED 错误

### 4. 认证登录模块
- 登录接口 `/auth/login` 返回 ECONNREFUSED 错误
- 获取用户信息接口 `/auth/info` 返回 ECONNREFUSED 错误

## 二、问题原因分析

通过分析控制台日志和源代码，发现以下几个关键问题：

### 1. 路径不一致问题

- **API调用路径**：在API调用文件中（如`src/api/system/role.ts`），请求路径格式为 `/role/batch`、`/role/${id}/status/${status}` 等
- **Mock服务配置路径**：在Mock服务配置文件中（如`src/mock/modules/role.ts`），接口路径格式为 `/api/role/page`、`/api/role/:id` 等
- **路径不匹配**：由于前缀不一致，导致Mock服务无法匹配到请求，请求被转发到代理服务器

### 2. 功能未实现问题

- 部分接口在Mock服务中完全没有实现，包括：
  - 批量删除接口（用户、角色、权限）
  - 状态更新接口（角色、权限）
  - 用户名检查接口
  - 认证相关接口（登录、获取用户信息）

### 3. 请求配置问题

- 在`src/utils/request.ts`中，`baseURL`设置为空字符串，意味着所有请求都是相对路径

## 三、业务代码分析

通过全面检查业务代码实现，发现：

### 1. 权限管理模块
- **完整实现**：`usePermission.ts` 包含所有核心功能
  - 获取权限树 (`getList`)
  - 删除权限 (`handleDelete`)
  - 批量删除 (`handleBatchDelete`)
  - 更新状态 (`handleStatusChange`)
- **表单功能**：`usePermissionForm.ts` 实现完整
  - 新增权限 (`handleAdd`)
  - 编辑权限 (`handleEdit`)
  - 表单提交 (`handleSubmit`)

### 2. 角色管理模块
- **列表功能**：`useRoleList.ts` 实现完整
  - 获取角色列表 (`handleSearch`)
  - 删除角色 (`handleDelete`)
  - 批量删除 (`handleBatchDelete`)
  - 更新状态 (`handleStatusChange`)
- **表单功能**：`useRoleForm.ts` 和 `RoleForm.vue` 实现完整
  - 新增角色
  - 编辑角色
  - 表单验证
- **其他功能**：
  - 详情页 (`RoleDetail.vue`)
  - 权限分配 (`PermTree.vue`)

### 3. 用户管理模块
- **表单功能**：`UserForm.vue` 实现完整
  - 新增用户
  - 编辑用户
  - 角色分配
  - 状态管理
- **验证功能**：`useUserValidation.ts` 实现完整
  - 用户名验证
  - 密码验证
  - 角色验证
  - 状态验证

### 4. API接口实现
- 所有模块的API调用已完整实现
- 包含完整的类型定义和错误处理
- 统一的请求/响应处理

## 四、解决方案

### 1. 统一请求路径

在API调用代码中统一添加 `/api` 前缀：

```typescript
// 修改前
export function batchDeleteRole(ids: number[]) {
  return request.delete('/role/batch', { data: ids })
}

// 修改后
export function batchDeleteRole(ids: number[]) {
  return request.delete('/api/role/batch', { data: ids })
}
```

### 2. 完善Mock服务实现

1. 添加缺失的Mock接口实现
2. 确保Mock接口返回格式与API文档一致
3. 添加适当的错误处理

### 3. 优化请求配置

在`src/utils/request.ts`中优化配置：

```typescript
const service = axios.create({
  baseURL: '',  // 保持为空，让Mock拦截器正常工作
  timeout: 10000
})
```

## 五、实施步骤

1. 修改所有API调用代码，统一添加 `/api` 前缀
2. 完善Mock服务接口实现
3. 测试所有功能点
4. 更新API文档

## 六、结论

1. 问题的根本原因是API路径不一致，而不是业务代码实现问题
2. 所有必要的业务功能都已经实现
3. 代码结构清晰，分层合理
4. 包含了完整的错误处理和状态管理
5. 表单验证规则完善

## 七、长期改进建议

1. **规范化API路径**：
   - 制定统一的API路径规范
   - 使用常量管理API路径
   - 添加路径前缀配置

2. **增强Mock服务**：
   - 添加数据持久化
   - 实现更复杂的业务逻辑
   - 增加延迟模拟

3. **改进错误处理**：
   - 统一错误码管理
   - 增加错误日志记录
   - 优化错误提示

4. **完善文档**：
   - 维护详细的API文档
   - 添加业务代码说明
   - 记录常见问题解决方案 