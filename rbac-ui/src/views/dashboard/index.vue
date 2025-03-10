<template>
  <div class="dashboard">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <span>欢迎回来</span>
        </div>
      </template>
      <div class="welcome-info">
        <div class="user-avatar">
          <el-avatar :size="64" :src="userStore.userInfo?.avatar || undefined" />
        </div>
        <div class="user-info">
          <h3>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h3>
          <p>角色：{{ userStore.userInfo?.roles?.map(role => role.roleName).join(', ') || '无' }}</p>
          <p>上次登录时间：{{ userStore.userInfo?.lastLoginTime || '首次登录' }}</p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
</script>

<style lang="scss" scoped>
.dashboard-container {
  .info-content {
    .info-item {
      margin-bottom: 15px;
      
      .label {
        font-weight: bold;
        margin-right: 10px;
      }
    }
  }
}
</style>