<template>
  <el-dialog
    :title="roleData ? '编辑角色' : '新增角色'"
    v-model="dialogVisible"
    width="500px"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
    >
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="form.roleName" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item label="角色编码" prop="roleCode">
        <el-input
          v-model="form.roleCode"
          placeholder="请输入角色编码"
          :disabled="!!roleData"
          :title="roleData ? '编辑时不可修改角色编码' : ''"
        />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          placeholder="请输入角色描述"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        确 定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import type { RoleInfo } from '@/types/user'
import { createRole, updateRole } from '@/api/system/role'

const props = defineProps<{
  visible: boolean
  roleData?: RoleInfo
}>()

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref<FormInstance>()
const dialogVisible = ref(props.visible)
const loading = ref(false)

const form = ref({
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const rules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' },
    { 
      pattern: /^[A-Z][A-Z0-9_]*$/,
      message: '角色编码必须以大写字母开头，只能包含大写字母、数字和下划线',
      trigger: 'blur'
    }
  ],
  description: [
    { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 监听visible变化
watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val && props.roleData) {
    // 编辑时填充表单数据，处理可能为null的字段
    form.value = {
      roleName: props.roleData.roleName,
      roleCode: props.roleData.roleCode,
      description: props.roleData.description || '',  // 处理null的情况
      status: props.roleData.status
    }
  }
})

// 监听dialogVisible变化
watch(() => dialogVisible.value, (val) => {
  emit('update:visible', val)
})

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  formRef.value?.resetFields()
  form.value = {
    roleName: '',
    roleCode: '',
    description: '',
    status: 1
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    if (props.roleData) {
      // 编辑角色
      await updateRole({
        ...form.value,
        id: props.roleData.id
      })
      ElMessage.success('编辑成功')
    } else {
      // 新增角色
      await createRole(form.value)
      ElMessage.success('新增成功')
    }
    
    emit('success')
    handleClose()
  } catch (error: any) {
    console.error('提交失败:', error)
    ElMessage.error(error.response?.data?.message || '提交失败')
  } finally {
    loading.value = false
  }
}
</script> 