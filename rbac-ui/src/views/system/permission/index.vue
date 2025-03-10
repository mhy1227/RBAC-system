<template>
  <div class="permission-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
      <el-form-item label="权限名称" prop="permissionName">
        <el-input
          v-model="queryParams.permissionName"
          placeholder="请输入权限名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
      <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </div>

    <!-- 权限表格 -->
    <el-table
      v-loading="loading"
      :data="permissionList"
      row-key="id"
      border
      :tree-props="{ children: 'children' }"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="权限名称" prop="permissionName" show-overflow-tooltip />
      <el-table-column label="权限标识" prop="permissionCode" show-overflow-tooltip />
      <el-table-column label="权限类型" prop="type" width="100">
        <template #default="{ row }">
          <el-tag :type="row.type === 'MENU' ? 'success' : row.type === 'BUTTON' ? 'warning' : 'info'">
            {{ row.type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sortOrder" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="(val) => handleStatusChange(row, Number(val))"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 权限表单对话框 -->
    <el-dialog
      :title="formTitle"
      v-model="dialogVisible"
      width="500px"
      append-to-body
      @close="handleClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="上级权限">
          <el-tree-select
            v-model="form.pid"
            :data="permissionList"
            :props="{ label: 'permissionName', value: 'id' }"
            placeholder="请选择上级权限"
            clearable
          />
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择权限类型">
            <el-option label="菜单权限" value="MENU" />
            <el-option label="按钮权限" value="BUTTON" />
            <el-option label="接口权限" value="API" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Search, Plus, Delete, Refresh } from '@element-plus/icons-vue'
import type { Permission, PermissionQuery } from './types/permission'
import {
  getPermissionTree,
  createPermission,
  updatePermission,
  deletePermission,
  batchDeletePermission,
  updatePermissionStatus
} from '@/api/permission'

// 加载状态
const loading = ref(false)
// 权限列表数据
const permissionList = ref<Permission[]>([])
// 选中的权限ID列表
const selectedIds = ref<number[]>([])
// 查询参数
const queryParams = ref<PermissionQuery>({
  permissionName: '',
  status: undefined
})
// 查询表单ref
const queryFormRef = ref<FormInstance>()

// 对话框相关
const dialogVisible = ref(false)
const formTitle = computed(() => form.id ? '编辑权限' : '新增权限')
const formRef = ref<FormInstance>()
const form = reactive<Partial<Permission>>({
  id: undefined,
  pid: 0,
  permissionName: '',
  permissionCode: '',
  type: 'MENU',
  sortOrder: 0,
  status: 1,
  description: ''
})

// 表单校验规则
const rules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限标识', trigger: 'blur' },
    { pattern: /^[a-z]+:[a-z]+:[a-z]+$/, message: '格式为 模块:子模块:操作', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ]
}

// 获取权限列表
const getList = async () => {
  try {
    loading.value = true
    const data = await getPermissionTree(queryParams.value)
    permissionList.value = data || []
  } catch (error) {
    console.error('获取权限列表失败:', error)
    ElMessage.error('获取权限列表失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

// 查询
const handleQuery = () => {
  getList()
}

// 表格选择变更
const handleSelectionChange = (selection: Permission[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增权限
const handleAdd = () => {
  form.id = undefined
  form.pid = 0
  form.permissionName = ''
  form.permissionCode = ''
  form.type = 'MENU'
  form.sortOrder = 0
  form.status = 1
  form.description = ''
  dialogVisible.value = true
}

// 编辑权限
const handleEdit = (row: Permission) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除权限
const handleDelete = async (row: Permission) => {
  try {
    await ElMessageBox.confirm('确认要删除该权限吗？', '提示', {
      type: 'warning'
    })
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除权限失败:', error)
      ElMessage.error('删除权限失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的权限')
    return
  }
  try {
    await ElMessageBox.confirm('确认要删除选中的权限吗？', '提示', {
      type: 'warning'
    })
    await batchDeletePermission(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 更新状态
const handleStatusChange = async (row: Permission, status: number) => {
  try {
    await updatePermissionStatus(row.id, status)
    ElMessage.success('状态更新成功')
    getList()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新状态失败')
    row.status = status === 1 ? 0 : 1
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    if (form.id) {
      await updatePermission(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createPermission(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('保存权限失败:', error)
    ElMessage.error('保存失败')
  }
}

// 关闭对话框
const handleClose = () => {
  formRef.value?.resetFields()
  dialogVisible.value = false
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.permission-container {
  padding: 20px;

  .search-form {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #fff;
    border-radius: 4px;
  }

  .toolbar {
    margin-bottom: 20px;
  }

  :deep(.el-table) {
    margin-top: 20px;
  }
}
</style> 