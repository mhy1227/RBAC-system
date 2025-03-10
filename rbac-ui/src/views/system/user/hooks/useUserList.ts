import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getUserPage, deleteUser, batchDeleteUser, updateUserStatus } from '@/api/system/user'
import type { UserInfo } from '@/types/user'

// 最大批量操作数量
const MAX_BATCH_SIZE = 100

export default function useUserList() {
  // 查询参数
  const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    username: '',
    nickname: '',
    status: -1 as number // 修改这里，默认值为-1表示全部
  })

  // 数据相关
  const loading = ref(false)
  const userList = ref<UserInfo[]>([])
  const total = ref(0)
  const selectedIds = ref<number[]>([])
  const queryFormRef = ref<FormInstance>()

  // 查询用户列表
  const getList = async () => {
    try {
      loading.value = true
      // 构建查询参数，过滤空值
      const params = {
        pageNum: queryParams.pageNum,
        pageSize: queryParams.pageSize,
        ...(queryParams.username?.trim() && { username: queryParams.username.trim() }),
        ...(queryParams.nickname?.trim() && { nickname: queryParams.nickname.trim() }),
        ...(queryParams.status !== -1 && { status: queryParams.status })
      }
      const { list, total: totalCount } = await getUserPage(params)
      userList.value = list
      total.value = totalCount
    } catch (error: any) {
      console.error('查询用户列表失败:', error)
      ElMessage.error(error.message || '查询失败')
      // 查询失败时清空数据
      userList.value = []
      total.value = 0
    } finally {
      loading.value = false
    }
  }

  // 重置查询
  const resetQuery = () => {
    if (!queryFormRef.value) return
    queryFormRef.value.resetFields()
    queryParams.pageNum = 1
    queryParams.status = -1 // 重置时将状态设为全部
    getList()
  }

  // 处理查询
  const handleQuery = () => {
    queryParams.pageNum = 1
    getList()
  }

  // 处理表格选择
  const handleSelectionChange = (selection: UserInfo[]) => {
    selectedIds.value = selection.map(item => item.id)
  }

  // 处理分页大小变化
  const handleSizeChange = (val: number) => {
    if (val > 0 && val <= 100) {
      queryParams.pageSize = val
      getList()
    } else {
      ElMessage.warning('每页显示数量必须在1-100之间')
    }
  }

  // 处理页码变化
  const handleCurrentChange = (val: number) => {
    if (val > 0) {
      queryParams.pageNum = val
      getList()
    } else {
      ElMessage.warning('页码必须大于0')
    }
  }

  // 检查是否包含超级管理员
  const checkContainsAdmin = (ids: number[]) => {
    return ids.includes(1)
  }

  // 处理删除
  const handleDelete = async (row: UserInfo) => {
    // 检查是否为超级管理员
    if (row.id === 1) {
      ElMessage.warning('不能删除超级管理员账号')
      return
    }

    try {
      await ElMessageBox.confirm('确认要删除该用户吗？', '警告', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error.message || '删除失败')
      }
    }
  }

  // 处理批量删除
  const handleBatchDelete = async () => {
    if (!selectedIds.value.length) {
      ElMessage.warning('请选择要删除的用户')
      return
    }

    // 检查选中数量
    if (selectedIds.value.length > MAX_BATCH_SIZE) {
      ElMessage.warning(`每次最多只能删除${MAX_BATCH_SIZE}个用户`)
      return
    }

    // 检查是否包含超级管理员
    if (checkContainsAdmin(selectedIds.value)) {
      ElMessage.warning('选中的用户中包含超级管理员，无法删除')
      return
    }

    try {
      await ElMessageBox.confirm(
        `确认要删除选中的 ${selectedIds.value.length} 个用户吗？`, 
        '警告', 
        {
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }
      )
      
      loading.value = true
      await batchDeleteUser(selectedIds.value)
      ElMessage.success('删除成功')
      // 刷新列表
      getList()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error.message || '删除失败')
      }
    } finally {
      loading.value = false
    }
  }

  // 处理状态变更
  const handleStatusChange = async (row: UserInfo) => {
    // 检查是否为超级管理员
    if (row.id === 1 && row.status === 0) {
      ElMessage.warning('不能禁用超级管理员账号')
      row.status = 1
      return
    }

    try {
      await ElMessageBox.confirm(
        `确认要${row.status === 1 ? '启用' : '禁用'}该用户吗？`,
        '提示',
        {
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }
      )
      
      await updateUserStatus(row.id, row.status)
      ElMessage.success('状态更新成功')
      getList()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error.message || '状态更新失败')
        // 恢复原状态
        row.status = row.status === 1 ? 0 : 1
      } else {
        // 取消操作，恢复原状态
        row.status = row.status === 1 ? 0 : 1
      }
    }
  }

  return {
    queryParams,
    loading,
    userList,
    total,
    selectedIds,
    queryFormRef,
    getList,
    resetQuery,
    handleQuery,
    handleSelectionChange,
    handleSizeChange,
    handleCurrentChange,
    handleDelete,
    handleBatchDelete,
    handleStatusChange
  }
}