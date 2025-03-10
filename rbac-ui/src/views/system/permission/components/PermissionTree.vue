<template>
  <el-drawer
    v-model="visible"
    title="权限树预览"
    size="800px"
    :destroy-on-close="true"
  >
    <div class="permission-tree-viewer">
      <!-- 搜索区域 -->
      <div class="search-bar">
        <el-input
          v-model="searchText"
          placeholder="搜索权限名称或标识"
          clearable
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button-group>
          <el-button type="primary" link @click="handleExpandAll">
            <el-icon><ArrowDown /></el-icon>
            {{ isExpandAll ? '收起' : '展开' }}
          </el-button>
          <el-button type="primary" link @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </el-button-group>
      </div>

      <!-- 树形结构 -->
      <div class="tree-container">
        <el-tree
          ref="treeRef"
          :data="permissions"
          :props="defaultProps"
          :filter-node-method="filterNode"
          :default-expand-all="isExpandAll"
          node-key="id"
          highlight-current
        >
          <template #default="{ node, data }">
            <div class="custom-tree-node">
              <div class="node-content">
                <span class="node-icon">
                  <el-icon v-if="data.type === 'MENU'"><Menu /></el-icon>
                  <el-icon v-else-if="data.type === 'BUTTON'"><Mouse /></el-icon>
                  <el-icon v-else><Link /></el-icon>
                </span>
                <span class="node-name">{{ data.permissionName }}</span>
                <el-tag size="small" :type="getTypeTag(data.type)" class="node-type">
                  {{ data.type }}
                </el-tag>
                <span class="node-code">{{ data.permissionCode }}</span>
                <el-tag 
                  size="small" 
                  :type="data.status === 1 ? 'success' : 'danger'"
                  class="node-status"
                >
                  {{ data.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </div>
            </div>
          </template>
        </el-tree>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Search, Menu, Mouse, Link, ArrowDown, Refresh } from '@element-plus/icons-vue'
import type { Permission } from '../types/permission'

const props = defineProps<{
  modelValue: boolean
  permissions: Permission[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const treeRef = ref()
const searchText = ref('')
const isExpandAll = ref(true)

// 树的配置
const defaultProps = {
  children: 'children',
  label: 'permissionName'
}

// 获取权限类型对应的标签类型
const getTypeTag = (type: string) => {
  switch (type) {
    case 'MENU':
      return 'primary'
    case 'BUTTON':
      return 'warning'
    case 'API':
      return 'info'
    default:
      return ''
  }
}

// 过滤节点
const filterNode = (value: string, data: Permission) => {
  if (!value) return true
  const searchValue = value.toLowerCase()
  return data.permissionName.toLowerCase().includes(searchValue) ||
         data.permissionCode.toLowerCase().includes(searchValue)
}

// 展开/收起所有节点
const handleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value
  if (isExpandAll.value) {
    treeRef.value?.expandAll()
  } else {
    treeRef.value?.collapseAll()
  }
}

// 刷新
const handleRefresh = () => {
  // 可以触发父组件重新获取数据
}

// 监听搜索文本变化
watch(searchText, (val) => {
  treeRef.value?.filter(val)
})
</script>

<style scoped lang="scss">
.permission-tree-viewer {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 0 20px;

  .search-bar {
    padding: 16px 0;
    display: flex;
    gap: 16px;
    align-items: center;
    border-bottom: 1px solid var(--el-border-color-light);

    .search-input {
      width: 300px;
    }
  }

  .tree-container {
    flex: 1;
    overflow: auto;
    padding: 20px 0;

    .custom-tree-node {
      width: 100%;
      
      .node-content {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 4px 0;

        .node-icon {
          font-size: 16px;
          width: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .node-name {
          font-weight: 500;
          min-width: 120px;
        }

        .node-type {
          min-width: 60px;
          text-align: center;
        }

        .node-code {
          color: var(--el-text-color-secondary);
          font-size: 13px;
          font-family: monospace;
        }

        .node-status {
          margin-left: auto;
        }
      }
    }
  }
}

:deep(.el-tree-node__content) {
  height: 40px;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: var(--el-color-primary-light-9);
}
</style> 