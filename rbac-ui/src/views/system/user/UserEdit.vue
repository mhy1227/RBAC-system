<template>
  <div class="user-edit">
    <el-card class="edit-card">
      <template #header>
        <div class="card-header">
          <h2>编辑用户</h2>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="edit-form"
      >
        <div class="form-content">
          <div class="avatar-section">
            <el-form-item label="头像">
              <avatar-upload v-model="form.avatar" />
            </el-form-item>
          </div>

          <div class="info-section">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" :disabled="true" />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-switch
                v-model="form.status"
                :active-value="1"
                :inactive-value="0"
                active-text="启用"
                inactive-text="禁用"
              />
            </el-form-item>
          </div>
        </div>

        <div class="form-footer">
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
.user-edit {
  padding: 20px;

  .edit-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      h2 {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
      }
    }

    .edit-form {
      margin-top: 20px;

      .form-content {
        display: flex;
        gap: 40px;

        .avatar-section {
          flex-shrink: 0;
          width: 200px;
        }

        .info-section {
          flex-grow: 1;
          max-width: 500px;

          :deep(.el-form-item) {
            margin-bottom: 22px;

            .el-input {
              width: 100%;
            }
          }
        }
      }

      .form-footer {
        margin-top: 30px;
        display: flex;
        justify-content: center;
        gap: 15px;
      }
    }
  }
}
</style>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo, updateUser } from '@/api/system/user'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import AvatarUpload from './components/AvatarUpload.vue'
import type { UserInfo } from '@/types/user'

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()

interface UserForm {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  status: number
  avatar: string
}

const form = ref<UserForm>({
  id: 0,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1,
  avatar: ''
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

onMounted(async () => {
  const id = Number(route.params.id)
  try {
    const res = await getUserInfo(id)
    if (res.data) {
      form.value = {
        id: res.data.id,
        username: res.data.username,
        nickname: res.data.nickname || '',
        email: res.data.email || '',
        phone: res.data.phone || '',
        status: res.data.status,
        avatar: res.data.avatar || ''
      }
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取用户信息失败')
    router.back()
  }
})

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate()
  await updateUser(form.value)
  ElMessage.success('保存成功')
  router.push({ name: 'UserDetail', params: { id: form.value.id } })
}

const handleCancel = () => {
  router.back()
}
</script> 