import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import type { Permission, PermissionQuery } from '../types/permission'
import type { Result } from '@/types/result'
import {
  getPermissionTree,
  deletePermission,
  batchDeletePermission,
  updatePermissionStatus
} from '@/api/permission'

export default function usePermission() {
  // 加载状态
  const loading = ref<boolean>(false)
  // 权限树数据
  const permissionList = ref<Permission[]>([])
  // 选中的权限ID列表
  const selectedIds = ref<number[]>([])
  // 查询参数
  const queryParams = ref<PermissionQuery>({
    permissionName: '',
    permissionCode: '',
    type: '',
    status: -1
  })
  // 查询表单ref
  const queryFormRef = ref<FormInstance>()

  /**
   * 获取权限列表
   */
  const getList = async () => {
    try {
      loading.value = true
      const data = await getPermissionTree(queryParams.value)
      permissionList.value = data || []
    } catch (error) {
      console.error('获取权限列表失败:', error)
      ElMessage.error('获取权限列表失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 重置查询
   */
  const resetQuery = () => {
    queryFormRef.value?.resetFields()
    queryParams.value = {
      permissionName: '',
      permissionCode: '',
      type: '',
      status: -1
    }
    getList()
  }

  /**
   * 执行查询
   */
  const handleQuery = () => {
    getList()
  }

  /**
   * 表格选择改变
   */
  const handleSelectionChange = (selection: Permission[]) => {
    selectedIds.value = selection.map(item => item.id)
  }

  /**
   * 删除权限
   */
  const handleDelete = async (row: Permission) => {
    try {
      await ElMessageBox.confirm('确认要删除该权限吗？', '警告', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
      
      const res = await deletePermission(row.id) as Result<void>
      if (res.code === 200) {
        ElMessage.success('删除成功')
        await getList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除权限失败:', error)
        ElMessage.error('删除失败')
      }
    }
  }

  /**
   * 批量删除权限
   */
  const handleBatchDelete = async () => {
    if (!selectedIds.value.length) {
      ElMessage.warning('请选择要删除的权限')
      return
    }

    try {
      await ElMessageBox.confirm('确认要删除选中的权限吗？', '警告', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
      
      const res = await batchDeletePermission(selectedIds.value) as Result<void>
      if (res.code === 200) {
        ElMessage.success('批量删除成功')
        selectedIds.value = []
        await getList()
      } else {
        ElMessage.error(res.message || '批量删除失败')
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('批量删除权限失败:', error)
        ElMessage.error('批量删除失败')
      }
    }
  }

  /**
   * 更新权限状态
   */
  const handleStatusChange = async (row: Permission) => {
    const newStatus = row.status === 1 ? 0 : 1
    const oldStatus = row.status
    
    try {
      row.status = newStatus
      const res = await updatePermissionStatus(row.id, newStatus) as Result<void>
      if (res.code === 200) {
        ElMessage.success('状态更新成功')
        await getList()
      } else {
        row.status = oldStatus
        ElMessage.error(res.message || '更新状态失败')
      }
    } catch (error) {
      row.status = oldStatus
      console.error('更新权限状态失败:', error)
      ElMessage.error('更新状态失败')
    }
  }

  return {
    loading,
    permissionList,
    selectedIds,
    queryParams,
    queryFormRef,
    getList,
    resetQuery,
    handleQuery,
    handleSelectionChange,
    handleDelete,
    handleBatchDelete,
    handleStatusChange
  }
} 