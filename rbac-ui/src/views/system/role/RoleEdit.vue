<template>
  <div class="role-edit">
    <h2>编辑角色</h2>
    <el-card>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRoleInfo, updateRole } from '@/api/system/role'
import type { FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import type { RoleInfo } from '@/types/user'

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()

interface RoleForm {
  id: number
  roleName: string
  roleCode: string
  description: string
  status: number
}

const form = ref<RoleForm>({
  id: 0,
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const rules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ]
}

onMounted(async () => {
  const id = Number(route.params.id)
  const roleInfo = await getRoleInfo(id)
  console.log("获取的角色信息",roleInfo)
  form.value = {
    id: roleInfo.id,
    roleName: roleInfo.roleName || '',
    roleCode: roleInfo.roleCode || '',
    description: roleInfo.description || '',
    status: roleInfo.status
  }
})

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate()
  await updateRole(form.value)
  ElMessage.success('保存成功')
  router.push({ name: 'RoleDetail', params: { id: form.value.id } })
}

const handleCancel = () => {
  router.back()
}
</script>

<style scoped>
.role-edit {
  padding: 20px;
}
</style> 