<template>
  <el-dialog
    :title="formData.id ? '编辑用户' : '新增用户'"
    v-model="dialogVisible"
    width="500px"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="用户名" prop="username">
        <el-input v-model="formData.username" placeholder="请输入用户名" :disabled="!!formData.id" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="formData.nickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="头像" prop="avatar">
        <avatar-upload v-model="formData.avatar" @success="handleAvatarSuccess" />
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="!formData.id">
        <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="formData.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="角色" prop="roleIds">
        <el-select v-model="formData.roleIds" multiple placeholder="请选择角色">
          <el-option
            v-for="role in roleOptions"
            :key="role.id"
            :label="role.roleName"
            :value="role.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确 定</el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);

    &:hover {
      border-color: var(--el-color-primary);
    }
  }
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import type { UserInfo, RoleInfo } from '@/types/user'
import { createUser, updateUser } from '@/api/system/user'
import { getUserInfo } from '@/api/auth'
import useUserValidation from '../hooks/useUserValidation'
import { computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import AvatarUpload from './AvatarUpload.vue'

const props = defineProps<{
  visible: boolean
  userData?: Partial<UserInfo>
}>()

const emit = defineEmits(['update:visible', 'success'])

// 表单数据
const formData = reactive<Partial<UserInfo> & { password?: string; roleIds?: number[] }>({
  username: '',
  nickname: '',
  password: '',
  email: '',
  phone: '',
  status: 1,
  roleIds: [],
  avatar: '',
  loginFailCount: 0,
  lastLoginTime: undefined,
  lockTime: undefined
})

const formRef = ref<FormInstance>()
const loading = ref(false)
const dialogVisible = ref(props.visible)

// 是否为管理员
const isAdmin = ref(false)

// 角色选择的验证规则
const roleValidation = (rule: any, value: number[], callback: any) => {
  if (!value || value.length === 0) {
    callback(new Error('请至少选择一个角色'))
  } else if (value.includes(1) && !isAdmin.value) {
    callback(new Error('无权分配超级管理员角色'))
  } else if (value.length > 10) {
    callback(new Error('最多只能选择10个角色'))
  } else {
    callback()
  }
}

// 表单校验规则
const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '以字母开头，只能包含字母、数字和下划线', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value && value.trim() !== value) {
        callback(new Error('用户名不能包含首尾空格'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{6,20}$/, 
      message: '密码必须包含大小写字母和数字', 
      trigger: 'blur' 
    }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
    { max: 50, message: '邮箱长度不能超过50个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  roleIds: [
    { required: true, message: '请选择角色', trigger: 'change' },
    { type: 'array', min: 1, message: '至少选择一个角色', trigger: 'change' },
    { type: 'array', max: 10, message: '最多选择10个角色', trigger: 'change' },
    { validator: roleValidation, trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' },
    { validator: (rule, value, callback) => {
      if (formData.id === 1 && value === 0) {
        callback(new Error('不能禁用超级管理员账号'))
      } else {
        callback()
      }
    }, trigger: 'change' }
  ]
}

// 角色选项（这里使用模拟数据，实际应该从API获取）
const roleOptions = ref<RoleInfo[]>([
  { id: 1, roleName: '超级管理员', roleCode: 'ROLE_SUPER_ADMIN', description: '系统超级管理员', status: 1, createTime: '', updateTime: '' },
  { id: 2, roleName: '测试管理员', roleCode: 'ROLE_TEST_ADMIN', description: '测试管理员', status: 1, createTime: '', updateTime: '' },
  { id: 3, roleName: '普通用户', roleCode: 'ROLE_USER', description: '普通用户', status: 1, createTime: '', updateTime: '' },
  // 预留扩展角色
  { id: 4, roleName: '部门管理员', roleCode: 'ROLE_DEPT_ADMIN', description: '部门管理员', status: 1, createTime: '', updateTime: '' },
  { id: 5, roleName: '项目经理', roleCode: 'ROLE_PROJECT_MANAGER', description: '项目经理', status: 1, createTime: '', updateTime: '' },
  { id: 6, roleName: '运维人员', roleCode: 'ROLE_OPS', description: '运维人员', status: 1, createTime: '', updateTime: '' },
  { id: 7, roleName: '审计人员', roleCode: 'ROLE_AUDITOR', description: '审计人员', status: 1, createTime: '', updateTime: '' },
  { id: 8, roleName: '数据分析师', roleCode: 'ROLE_ANALYST', description: '数据分析师', status: 1, createTime: '', updateTime: '' }
])

// 监听visible变化
watch(() => props.visible, (val) => {
  dialogVisible.value = val
})

// 监听dialogVisible变化
watch(() => dialogVisible.value, (val) => {
  emit('update:visible', val)
})

// 监听userData变化
watch(() => props.userData, (val) => {
  if (val) {
    Object.assign(formData, val)
    // 设置角色ID
    formData.roleIds = val.roles?.map(role => role.id)
  }
}, { immediate: true })

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, {
    username: '',
    nickname: '',
    password: '',
    email: '',
    phone: '',
    status: 1,
    roleIds: [],
    avatar: '',
    loginFailCount: 0,
    lastLoginTime: undefined,
    lockTime: undefined
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    if (formData.id) {
      await updateUser(formData)
      ElMessage.success('修改成功')
    } else {
      await createUser(formData)
      ElMessage.success('新增成功')
    }
    
    emit('success')
    handleClose()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    loading.value = false
  }
}

// 获取当前用户信息
const getCurrentUser = async () => {
  try {
    const userInfo = await getUserInfo()
    isAdmin.value = userInfo.roles?.some((role: RoleInfo) => role.roleCode === 'admin') || false
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 在组件挂载时获取用户信息
onMounted(() => {
  getCurrentUser()
})

// 头像上传成功的回调
const handleAvatarSuccess = (url: string) => {
  formData.avatar = url
}
</script> 