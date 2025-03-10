import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { RoleInfo } from '@/types/user'
import { getRolePage, updateRoleStatus, deleteRole, batchDeleteRole } from '@/api/system/role'

export default function useRoleList() {
  // 加载状态
  const loading = ref(false)
  // 角色列表
  const roleList = ref<RoleInfo[]>([])
  // 总数
  const total = ref(0)
  // 当前页码
  const page = ref(1)
  // 每页条数
  const pageSize = ref(10)
  // 选中的角色ID
  const selectedIds = ref<number[]>([])
  // 搜索表单
  const searchForm = ref({
    roleName: '',
    roleCode: '',
    status: '' as string | number
  })

  // 获取角色列表
  const handleSearch = async () => {
    try {
      loading.value = true
      // 构建查询参数，过滤空值
      const params = {
        pageNum: page.value,
        pageSize: pageSize.value,
        ...(searchForm.value.roleName?.trim() && { roleName: searchForm.value.roleName.trim() }),
        ...(searchForm.value.roleCode?.trim() && { roleCode: searchForm.value.roleCode.trim() }),
        ...(searchForm.value.status !== '' && { status: Number(searchForm.value.status) })
      }
      const { list, total: totalCount } = await getRolePage(params)
      roleList.value = list
      total.value = totalCount
    } catch (error: any) {
      console.error('查询角色列表失败:', error)
      // 显示具体错误信息
      if (error.response?.data?.message) {
        ElMessage.error(error.response.data.message)
      } else {
        ElMessage.error(error.message || '获取角色列表失败')
      }
      // 清空数据
      roleList.value = []
      total.value = 0
    } finally {
      loading.value = false
    }
  }

  // 重置搜索
  const handleReset = () => {
    searchForm.value = {
      roleName: '',
      roleCode: '',
      status: ''
    }
    page.value = 1
    handleSearch()
  }

  // 表格选择
  const handleSelectionChange = (selection: RoleInfo[]) => {
    selectedIds.value = selection.map(item => item.id)
  }

  // 每页条数变化
  const handleSizeChange = (val: number) => {
    pageSize.value = val
    handleSearch()
  }

  // 页码变化
  const handleCurrentChange = (val: number) => {
    page.value = val
    handleSearch()
  }

  // 状态变化
  const handleStatusChange = async (row: RoleInfo, val: string | number | boolean) => {
    const status = Number(val)
    // 验证状态值
    if (![0, 1].includes(status)) {
      ElMessage.warning('无效的状态值')
      // 恢复原状态
      row.status = row.status === 1 ? 0 : 1
      return
    }

    try {
      await ElMessageBox.confirm(
        `确认${status === 1 ? '启用' : '禁用'}该角色吗？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          draggable: true,
          closeOnClickModal: false,
          beforeClose: async (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true
              try {
                await updateRoleStatus(row.id, status)
                done()
                ElMessage.success('状态更新成功')
                handleSearch()
              } catch (error: any) {
                // 恢复原状态
                row.status = row.status === 1 ? 0 : 1
                // 显示具体错误信息
                if (error.response?.data?.message) {
                  ElMessage.error(error.response.data.message)
                } else {
                  ElMessage.error(error.message || '状态更新失败')
                }
              } finally {
                instance.confirmButtonLoading = false
              }
            } else {
              // 恢复原状态
              row.status = row.status === 1 ? 0 : 1
              done()
            }
          }
        }
      )
    } catch (error) {
      // 用户取消操作，恢复原状态
      row.status = row.status === 1 ? 0 : 1
    }
  }

  // 删除角色
  const handleDelete = async (row: RoleInfo) => {
    try {
      await ElMessageBox.confirm(
        `确认删除角色"${row.roleName}"吗？删除后不可恢复！`,
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          draggable: true,
          closeOnClickModal: false,
          beforeClose: async (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true
              try {
                await deleteRole(row.id)
                done()
                ElMessage.success('删除成功')
                handleSearch()
              } catch (error: any) {
                ElMessage.error(error.message || '删除失败')
              } finally {
                instance.confirmButtonLoading = false
              }
            } else {
              done()
            }
          }
        }
      )
    } catch (error) {
      // 用户取消删除，不需要处理
    }
  }

  // 批量删除
  const handleBatchDelete = async () => {
    if (!selectedIds.value.length) {
      ElMessage.warning('请选择要删除的角色')
      return
    }

    try {
      await ElMessageBox.confirm(
        `确认删除选中的 ${selectedIds.value.length} 个角色吗？删除后不可恢复！`,
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          draggable: true,
          closeOnClickModal: false,
          beforeClose: async (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true
              try {
                await batchDeleteRole(selectedIds.value)
                done()
                ElMessage.success('批量删除成功')
                handleSearch()
              } catch (error: any) {
                ElMessage.error(error.message || '批量删除失败')
              } finally {
                instance.confirmButtonLoading = false
              }
            } else {
              done()
            }
          }
        }
      )
    } catch (error) {
      // 用户取消删除，不需要处理
    }
  }

  // 操作成功回调
  const handleSuccess = () => {
    handleSearch()
  }

  return {
    loading,
    roleList,
    total,
    page,
    pageSize,
    searchForm,
    selectedIds,
    handleSearch,
    handleReset,
    handleSelectionChange,
    handleSizeChange,
    handleCurrentChange,
    handleStatusChange,
    handleDelete,
    handleBatchDelete,
    handleSuccess
  }
}