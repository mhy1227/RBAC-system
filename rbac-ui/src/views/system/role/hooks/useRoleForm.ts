import { ref } from 'vue'
import type { RoleInfo } from '@/types/user'

export default function useRoleForm() {
  // 表单可见性
  const formVisible = ref(false)
  // 当前编辑的角色数据
  const currentRole = ref<RoleInfo | undefined>()

  // 打开新增表单
  const handleAdd = () => {
    currentRole.value = undefined
    formVisible.value = true
  }

  // 打开编辑表单
  const handleEdit = (role: RoleInfo) => {
    currentRole.value = { ...role } as RoleInfo 
    formVisible.value = true
  }

  return {
    formVisible,
    currentRole,
    handleAdd,
    handleEdit
  }
} 