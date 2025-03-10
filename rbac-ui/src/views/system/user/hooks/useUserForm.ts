import { ref } from 'vue'
import type { UserInfo } from '@/types/user'

export default function useUserForm() {
  // 弹窗控制
  const dialogVisible = ref(false)
  const currentUser = ref<Partial<UserInfo>>()

  // 新增用户
  const handleAdd = () => {
    currentUser.value = undefined
    dialogVisible.value = true
  }

  // 修改用户
  const handleUpdate = (row: UserInfo) => {
    currentUser.value = { ...row }
    dialogVisible.value = true
  }

  return {
    dialogVisible,
    currentUser,
    handleAdd,
    handleUpdate
  }
} 