import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { Permission } from '../types/permission'
import { createPermission, updatePermission } from '@/api/permission'

export default function usePermissionForm() {
  // 对话框可见性
  const dialogVisible = ref(false)
  // 当前编辑的权限数据
  const currentPermission = ref<Partial<Permission>>({})
  // 表单ref
  const formRef = ref()
  // 表单规则
  const rules = {
    permissionName: [
      { required: true, message: '请输入权限名称', trigger: 'blur' },
      { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    permissionCode: [
      { required: true, message: '请输入权限标识', trigger: 'blur' },
      { pattern: /^[a-z]+:[a-z]+:[a-z]+$/, message: '格式为 模块:子模块:操作', trigger: 'blur' }
    ],
    type: [
      { required: true, message: '请选择权限类型', trigger: 'change' }
    ]
  }

  /**
   * 打开新增对话框
   */
  const handleAdd = (pid?: number) => {
    currentPermission.value = {
      pid: pid || 0,
      status: 1,
      sortOrder: 0
    }
    dialogVisible.value = true
  }

  /**
   * 打开编辑对话框
   */
  const handleEdit = (row: Permission) => {
    currentPermission.value = { ...row }
    dialogVisible.value = true
  }

  /**
   * 提交表单
   */
  const handleSubmit = async () => {
    await formRef.value?.validate()
    try {
      if (currentPermission.value.id) {
        const { id, ...data } = currentPermission.value
        await updatePermission(id, data)
        ElMessage.success('更新成功')
      } else {
        await createPermission(currentPermission.value)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      return true
    } catch (error) {
      console.error('保存权限失败:', error)
      return false
    }
  }

  /**
   * 关闭对话框
   */
  const handleClose = () => {
    formRef.value?.resetFields()
    currentPermission.value = {}
    dialogVisible.value = false
  }

  return {
    dialogVisible,
    currentPermission,
    formRef,
    rules,
    handleAdd,
    handleEdit,
    handleSubmit,
    handleClose
  }
} 