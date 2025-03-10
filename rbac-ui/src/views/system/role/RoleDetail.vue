<template>
  <div class="role-detail">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>角色详情</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleEdit">编辑</el-button>
            <el-button @click="$router.back()">返回</el-button>
          </div>
        </div>
      </template>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="角色名称">{{ roleInfo?.roleName }}</el-descriptions-item>
        <el-descriptions-item label="角色编码">
          <el-tag>{{ roleInfo?.roleCode }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="roleInfo?.status === 1 ? 'success' : 'danger'">
            {{ roleInfo?.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ roleInfo?.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ roleInfo?.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ roleInfo?.description }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="box-card permission-card">
      <template #header>
        <div class="card-header">
          <span>权限信息</span>
        </div>
      </template>
      
      <el-tree
        :data="permissionTree"
        :props="defaultProps"
        :default-checked-keys="rolePermissions"
        node-key="id"
        show-checkbox
        disabled
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRoleInfo, getRolePermissions } from '@/api/system/role'
import { getPermissionTree } from '@/api/system/permission'
import type { RoleInfo } from '@/types/user'
import type { PermissionInfo } from '@/types/permission'
import type { Result } from '@/types/result'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const roleInfo = ref<RoleInfo | null>(null)
const permissionTree = ref<PermissionInfo[]>([])
const rolePermissions = ref<number[]>([])

const defaultProps = {
  children: 'children',
  label: 'permissionName'
}

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) {
    ElMessage.error('角色ID不能为空')
    router.back()
    return
  }

  try {
    // 并行获取数据
    const [roleData, treeData, permData] = await Promise.all([
      getRoleInfo(id),
      getPermissionTree(),
      getRolePermissions(id)
    ])

    roleInfo.value = roleData
    permissionTree.value = treeData
    rolePermissions.value = permData
  } catch (error: any) {
    ElMessage.error(error.message || '获取数据失败')
    router.back()
  }
})

const handleEdit = () => {
  router.push({
    name: 'RoleEdit',
    params: { id: roleInfo.value?.id }
  })
}
</script>

<style lang="scss" scoped>
.role-detail {
  .box-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        gap: 10px;
      }
    }
  }

  .permission-card {
    margin-top: 20px;
  }

  :deep(.el-descriptions) {
    margin: 20px 0;
  }

  :deep(.el-tag) {
    text-transform: uppercase;
  }
}
</style>
