<template>
  <el-dialog
    title="分配权限"
    v-model="dialogVisible"
    width="600px"
    @close="handleClose"
  >
    <el-tree
      ref="treeRef"
      :data="permissionList"
      :props="defaultProps"
      show-checkbox
      node-key="id"
      :default-checked-keys="checkedKeys"
      :check-strictly="checkStrictly"
    />
    <template #footer>
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { ElTree } from 'element-plus'
import { ElMessage } from 'element-plus'
import type { RoleInfo } from '@/types/user'
import type { PermissionInfo } from '@/types/permission'
import { getPermissionTree, getRolePermissions, updateRolePermissions } from '@/api/system/permission'

const props = defineProps<{
  visible: boolean
  roleData?: Partial<RoleInfo>
}>()

const emit = defineEmits(['update:visible', 'success'])

// 权限树引用
const treeRef = ref<InstanceType<typeof ElTree>>()
// 对话框可见性
const dialogVisible = ref(props.visible)
// 加载状态
const loading = ref(false)
// 权限列表
const permissionList = ref<PermissionInfo[]>([])
// 选中的权限ID
const checkedKeys = ref<number[]>([])
// 是否严格的父子节点不互相关联
const checkStrictly = ref(true)

// 树的配置
const defaultProps = {
  children: 'children',
  label: 'permissionName'
}

// 监听visible变化
watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val && props.roleData?.id) {
    loadPermissionData()
  }
})

// 监听dialogVisible变化
watch(() => dialogVisible.value, (val) => {
  emit('update:visible', val)
})

// 加载权限数据
const loadPermissionData = async () => {
  if (!props.roleData?.id) {
    ElMessage.warning('角色ID不能为空')
    return
  }
  
  try {
    loading.value = true
    // 分开调用，便于定位错误
    const treeData = await getPermissionTree()
    permissionList.value = treeData

    const rolePerms = await getRolePermissions(props.roleData.id)
    checkedKeys.value = rolePerms.map(item => item.id)
  } catch (error: any) {
    console.error('获取权限数据失败:', error)
    ElMessage.error(error.response?.data?.message || '获取权限数据失败')
    dialogVisible.value = false
  } finally {
    loading.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  permissionList.value = []
  checkedKeys.value = []
}

// 提交表单
const handleSubmit = async () => {
  if (!props.roleData?.id || !treeRef.value) {
    ElMessage.warning('角色ID不能为空')
    return
  }
  
  try {
    loading.value = true
    const checkedNodes = treeRef.value.getCheckedKeys(false) as number[]
    const halfCheckedNodes = treeRef.value.getHalfCheckedKeys() as number[]
    const permissionIds = [...checkedNodes, ...halfCheckedNodes]
    
    await updateRolePermissions(props.roleData.id, permissionIds)
    ElMessage.success('权限分配成功')
    emit('success')
    handleClose()
  } catch (error: any) {
    console.error('权限分配失败:', error)
    ElMessage.error(error.response?.data?.message || '权限分配失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.el-tree {
  max-height: 400px;
  overflow-y: auto;
  margin: 10px 0;
  padding: 10px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
}
</style> 