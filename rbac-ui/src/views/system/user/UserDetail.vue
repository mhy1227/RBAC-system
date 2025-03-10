<template>
  <div class="user-detail">
    <el-card class="detail-card">
      <template #header>
        <div class="card-header">
          <h2>用户详情</h2>
          <div class="button-group">
            <el-button type="primary" @click="handleEdit">编辑</el-button>
            <el-button type="warning" @click="handleChangePassword">修改密码</el-button>
            <el-button type="danger" @click="handleDelete">删除用户</el-button>
          </div>
        </div>
      </template>
      
      <div class="user-info" v-if="userInfo">
        <div class="avatar-section">
          <el-image
            :src="userInfo.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'"
            fit="cover"
            class="avatar"
          >
            <template #error>
              <div class="avatar-error">
                <el-icon><Avatar /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
        <div class="info-section">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ userInfo.nickname || '-' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userInfo.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userInfo.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
                {{ userInfo.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag
                v-for="role in userInfo.roles"
                :key="role.id"
                class="role-tag"
                type="info"
              >
                {{ role.roleName }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ userInfo.createTime }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ userInfo.updateTime }}</el-descriptions-item>
            <el-descriptions-item label="最后登录时间" :span="2">{{ userInfo.lastLoginTime || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>

    <!-- 修改密码对话框 -->
    <password-dialog ref="passwordDialogRef" @success="handlePasswordSuccess" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo, deleteUser } from '@/api/system/user'
import type { UserInfo } from '@/types/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Avatar } from '@element-plus/icons-vue'
import PasswordDialog from './components/PasswordDialog.vue'

const route = useRoute()
const router = useRouter()
const userInfo = ref<UserInfo | null>(null)
const passwordDialogRef = ref()

onMounted(async () => {
  const id = Number(route.params.id)
  try {
    const res = await getUserInfo(id)
    userInfo.value = res.data
  } catch (error: any) {
    ElMessage.error(error.message || '获取用户信息失败')
  }
})

const handleEdit = () => {
  router.push({
    name: 'UserEdit',
    params: { id: userInfo.value?.id }
  })
}

const handleChangePassword = () => {
  passwordDialogRef.value?.show()
}

const handlePasswordSuccess = () => {
  ElMessage.success('密码修改成功')
}

const handleDelete = () => {
  ElMessageBox.confirm('确定删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    if (userInfo.value?.id) {
      await deleteUser(userInfo.value.id)
      ElMessage.success('用户删除成功')
      router.push({ name: 'User' })
    } else {
      ElMessage.error('用户ID不存在')
    }
  })
}
</script>

<style lang="scss" scoped>
.user-detail {
  padding: 20px;

  .detail-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      h2 {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
      }

      .button-group {
        display: flex;
        gap: 12px;
      }
    }
  }

  .user-info {
    display: flex;
    gap: 40px;
    margin: 20px 0;

    .avatar-section {
      flex-shrink: 0;
      width: 120px;
      height: 120px;
      border-radius: 8px;
      overflow: hidden;
      border: 2px solid var(--el-border-color-light);

      .avatar {
        width: 100%;
        height: 100%;
      }

      .avatar-error {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: var(--el-fill-color-light);
        color: var(--el-text-color-secondary);
        font-size: 32px;
      }
    }

    .info-section {
      flex-grow: 1;
      
      :deep(.el-descriptions) {
        padding: 0;
        
        .el-descriptions__label {
          width: 120px;
          justify-content: flex-end;
          padding: 12px 20px;
          font-weight: 600;
          background-color: var(--el-fill-color-light);
        }
        
        .el-descriptions__content {
          padding: 12px 20px;
        }

        .role-tag {
          margin-right: 8px;
          margin-bottom: 4px;
        }
      }
    }
  }
}
</style>