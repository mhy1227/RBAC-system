<template>
  <el-dialog
    :title="formData.id ? '编辑权限' : '新增权限'"
    v-model="dialogVisible"
    width="500px"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      class="permission-form"
    >
      <el-form-item label="权限名称" prop="permissionName">
        <el-input 
          v-model="formData.permissionName" 
          placeholder="请输入权限名称"
          @input="handleNameInput"
        />
      </el-form-item>

      <el-form-item label="权限标识" prop="permissionCode">
        <el-input 
          v-model="formData.permissionCode" 
          placeholder="请输入权限标识"
          :readonly="!formData.id"
        >
          <template #suffix>
            <el-tooltip
              content="权限标识将根据类型和名称自动生成，格式：模块:子模块:操作"
              placement="top"
            >
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="权限类型" prop="type">
        <el-select 
          v-model="formData.type" 
          placeholder="请选择权限类型"
          class="w-full"
          :disabled="!!formData.id"
        >
          <el-option label="菜单权限" value="MENU" />
          <el-option label="按钮权限" value="BUTTON" />
          <el-option label="接口权限" value="API" />
        </el-select>
      </el-form-item>

      <el-form-item label="上级权限" prop="parentId">
        <el-tree-select
          v-model="formData.parentId"
          :data="availableParents"
          :props="treeProps"
          placeholder="请选择上级权限"
          class="w-full"
          clearable
          :disabled="!!formData.id"
        />
      </el-form-item>

      <el-form-item label="路由路径" prop="path" v-if="formData.type === 'MENU'">
        <el-input 
          v-model="formData.path" 
          placeholder="请输入路由路径"
        >
          <template #suffix>
            <el-tooltip
              content="菜单权限的路由路径，例如：/system/user"
              placement="top"
            >
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="权限描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          placeholder="请输入权限描述"
          :rows="3"
        />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue';
import { QuestionFilled } from '@element-plus/icons-vue';
import type { FormInstance, FormRules } from 'element-plus';
import type { Permission } from '../types/permission';
import { ElMessage } from 'element-plus';

const props = defineProps<{
  treeData: Permission[];
}>();

const emit = defineEmits<{
  (e: 'submit', data: Partial<Permission>): void;
  (e: 'update:modelValue', value: boolean): void;
}>();

const dialogVisible = ref(false);
const loading = ref(false);
const formRef = ref<FormInstance>();

// 表单数据
const formData = reactive<Partial<Permission>>({
  id: undefined,
  permissionName: '',
  permissionCode: '',
  description: '',
  parentId: 0,
  type: undefined,
  path: '',
  status: 1
});

// 树形选择配置
const treeProps = {
  value: 'id',
  label: 'permissionName',
  children: 'children'
};

// 表单校验规则
const rules: FormRules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限标识', trigger: 'blur' },
    { pattern: /^[\w-]+:[\w-]+:[\w-]+$/, message: '格式：模块:子模块:操作', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ],
  parentId: [
    { required: true, message: '请选择上级权限', trigger: 'change' }
  ],
  path: [
    { required: true, message: '请输入路由路径', trigger: 'blur', if: (form) => form.type === 'MENU' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
};

// 打开弹窗
const open = (data?: Partial<Permission>) => {
  Object.assign(formData, {
    id: undefined,
    permissionName: '',
    permissionCode: '',
    description: '',
    parentId: 0,
    type: undefined,
    path: '',
    status: 1
  });
  
  if (data) {
    Object.assign(formData, data);
  }
  
  dialogVisible.value = true;
};

// 关闭弹窗
const handleClose = () => {
  formRef.value?.resetFields();
  dialogVisible.value = false;
  emit('update:modelValue', false);
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      loading.value = true;
      emit('submit', formData);
      loading.value = false;
      handleClose();
    }
  });
};

// 过滤可选的上级权限
const filterParentOptions = (data: Permission[]) => {
  // 当前编辑的权限ID
  const currentId = formData.id;
  
  // 递归过滤函数
  const filter = (items: Permission[]): Permission[] => {
    return items.filter(item => {
      // 如果是编辑状态，排除自己及其子节点
      if (currentId && (item.id === currentId || isChildNode(item, currentId))) {
        return false;
      }
      
      // 按钮权限不能作为父级
      if (item.type === 'BUTTON') {
        return false;
      }
      
      // 递归处理子节点
      if (item.children) {
        item.children = filter(item.children);
      }
      
      return true;
    });
  };
  
  return filter(data);
};

// 判断是否为某个节点的子节点
const isChildNode = (node: Permission, targetId: number): boolean => {
  if (!node.children) {
    return false;
  }
  
  return node.children.some(child => 
    child.id === targetId || isChildNode(child, targetId)
  );
};

// 监听权限类型变化
watch(() => formData.type, (newType) => {
  if (newType === 'BUTTON') {
    // 按钮权限必须有父级
    if (!formData.parentId) {
      ElMessage.warning('按钮权限必须选择父级菜单')
      formData.parentId = undefined
    }
  }
  
  // 自动生成权限标识
  if (formData.parentId && formData.permissionName) {
    formData.permissionCode = generatePermissionCode(newType, formData.parentId)
  }
});

// 监听权限名称变化
watch(() => formData.permissionName, (newName) => {
  // 自动生成权限标识
  if (formData.parentId && formData.type && newName) {
    formData.permissionCode = generatePermissionCode(formData.type, formData.parentId)
  }
})

// 监听父级权限变化
watch(() => formData.parentId, (newParentId) => {
  if (newParentId && formData.type && formData.permissionName) {
    formData.permissionCode = generatePermissionCode(formData.type, newParentId)
  }
})

// 获取可选的父级权限
const availableParents = computed(() => {
  return filterParentOptions(props.treeData)
})

// 生成权限标识
const generatePermissionCode = (type: string, parentId: number) => {
  // 获取父级权限
  const parent = props.treeData.find(item => findPermissionById(item, parentId))
  if (!parent) {
    return ''
  }

  // 根据类型生成权限标识
  const parentCode = parent.permissionCode || ''
  const baseName = formData.permissionName?.toLowerCase().replace(/\s+/g, '-') || ''
  
  switch (type) {
    case 'MENU':
      return `${parentCode}:${baseName}`
    case 'BUTTON':
      return `${parentCode}:${baseName}`
    case 'API':
      return `${parentCode}:${baseName}`
    default:
      return ''
  }
}

// 递归查找权限
const findPermissionById = (permission: Permission, id: number): Permission | null => {
  if (permission.id === id) {
    return permission
  }
  if (permission.children) {
    for (const child of permission.children) {
      const found = findPermissionById(child, id)
      if (found) {
        return found
      }
    }
  }
  return null
}

// 暴露方法给父组件
defineExpose({
  open
});
</script>

<style scoped>
.permission-form {
  padding: 20px;
}

.w-full {
  width: 100%;
}
</style> 