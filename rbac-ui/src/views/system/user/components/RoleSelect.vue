<template>
  <el-form-item label="角色" prop="roleIds">
    <el-select
      v-model="selectedRoles"
      multiple
      collapse-tags
      collapse-tags-tooltip
      :loading="loading"
      placeholder="请选择角色"
      @change="handleChange"
    >
      <el-option
        v-for="role in roleList"
        :key="role.id"
        :label="role.roleName"
        :value="role.id"
        :disabled="isRoleDisabled(role)"
      >
        <span>{{ role.roleName }}</span>
        <span class="role-description">{{ role.description }}</span>
      </el-option>
    </el-select>
  </el-form-item>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRolePage } from '@/api/system/role'
import { useUserStore } from '@/store/modules/user'
import type { RoleInfo } from '@/types/user'

const props = defineProps<{
  modelValue: number[]
}>()

const emit = defineEmits(['update:modelValue', 'change'])

const userStore = useUserStore()
const loading = ref(false)
const roleList = ref<RoleInfo[]>([])
const selectedRoles = ref<number[]>(props.modelValue)

// 加载角色列表
const loadRoles = async () => {
  try {
    loading.value = true
    const { list } = await getRolePage({ pageSize: 100 })
    roleList.value = list
  } catch (error: any) {
    ElMessage.error(error.message || '获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 判断角色是否禁用
const isRoleDisabled = (role: RoleInfo) => {
  // 超级管理员角色只能由超级管理员分配
  if (role.id === 1) {
    return !userStore.userInfo?.roles?.some(r => r.id === 1)
  }
  // 禁用状态的角色不能选择
  return role.status === 0
}

// 处理选择变化
const handleChange = (value: number[]) => {
  emit('update:modelValue', value)
  emit('change', value)
}

// 监听props变化
watch(() => props.modelValue, (newVal) => {
  selectedRoles.value = newVal
})

onMounted(() => {
  loadRoles()
})
</script>

<style lang="scss" scoped>
.role-description {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

:deep(.el-select) {
  width: 100%;
}
</style> 