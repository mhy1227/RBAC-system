<template>
  <div class="login-container">
    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      class="login-form"
      autocomplete="on"
      label-position="left"
    >
      <div class="title-container">
        <h3 class="title">RBAC权限管理系统</h3>
      </div>

      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          placeholder="用户名"
          type="text"
          tabindex="1"
          :prefix-icon="User"
          autocomplete="on"
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          placeholder="密码"
          :type="passwordVisible ? 'text' : 'password'"
          tabindex="2"
          :prefix-icon="Lock"
          :suffix-icon="passwordVisible ? View : Hide"
          autocomplete="on"
          @keyup.enter="handleLogin"
          @click-suffix-icon="passwordVisible = !passwordVisible"
        />
      </el-form-item>

      <el-button
        :loading="loading"
        type="primary"
        style="width: 100%; margin-bottom: 30px"
        @click="handleLogin"
      >
        登录
      </el-button>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User, View, Hide } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import type { FormInstance } from 'element-plus'
import type { LoginParams } from '@/types/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const passwordVisible = ref(false)

const loginForm = reactive<LoginParams>({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    loading.value = true
    await loginFormRef.value.validate()
    
    // 确保登录参数格式正确
    const loginParams = {
      username: loginForm.username.trim(),
      password: loginForm.password
    }
    
    // 调用登录接口
    await userStore.login(loginParams)
    
    // 登录成功后跳转
    const redirect = router.currentRoute.value.query.redirect as string
    router.push(redirect || '/')
    ElMessage.success('登录成功')
  } catch (error: any) {
    console.error('登录错误:', error)
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  background-color: #2d3a4b;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  .login-form {
    width: 400px;
    max-width: 100%;
    padding: 35px;
    margin: 0 auto;
    overflow: hidden;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }

  .title-container {
    position: relative;
    .title {
      font-size: 26px;
      color: #333;
      margin: 0 auto 30px auto;
      text-align: center;
      font-weight: bold;
    }
  }
}
</style>
