<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="header-left">
        <h2>RBAC管理系统</h2>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            {{ userStore.userInfo?.nickname }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="210px" class="layout-aside">
        <el-menu
          :default-active="$route.path"
          :key="$route.path"
          router
          unique-opened
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/dashboard">
            <el-icon><Menu /></el-icon>
            <template #title>首页</template>
          </el-menu-item>

          <el-sub-menu index="/system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/user">
              <el-icon><User /></el-icon>
              <template #title>用户管理</template>
            </el-menu-item>
            <el-menu-item index="/system/role">
              <el-icon><UserFilled /></el-icon>
              <template #title>角色管理</template>
            </el-menu-item>
            <el-menu-item index="/system/menu">
              <el-icon><Grid /></el-icon>
              <template #title>菜单管理</template>
            </el-menu-item>
            <el-menu-item index="/system/permission">
              <el-icon><Lock /></el-icon>
              <template #title>权限管理</template>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" :key="$route.path" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useUserStore } from '@/store/modules/user'
import { useRouter } from 'vue-router'
import { 
  ArrowDown,
  Menu,
  Setting,
  User,
  UserFilled,
  Grid,
  Lock
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

const handleLogout = async () => {
  try {
    await userStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('登出失败:', error)
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;

  .layout-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #fff;
    border-bottom: 1px solid #dcdfe6;
    padding: 0 20px;

    .header-left {
      h2 {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
      }
    }

    .header-right {
      .user-info {
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }
  }

  .layout-aside {
    background-color: #304156;
    border-right: 1px solid #dcdfe6;

    :deep(.el-menu) {
      border-right: none;
    }
  }

  .layout-main {
    background-color: #f0f2f5;
    padding: 20px;
  }
}
</style>