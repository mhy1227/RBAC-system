<template>
  <div class="permission-columns">
    <!-- 调试信息 -->
    <div v-if="!columns.length" class="debug-info">
      <p>当前权限数据：{{ permissions }}</p>
    </div>

    <!-- 面包屑导航 -->
    <div class="breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item v-for="item in breadcrumbs" :key="item?.id">
          <span class="breadcrumb-item" @click="handleBreadcrumbClick(item!)">
            {{ item?.permissionName }}
          </span>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 动态多列布局 -->
    <div class="columns-container">
      <div 
        v-for="(column, index) in columns" 
        :key="index"
        class="column"
      >
        <div class="column-header">
          <span class="title">{{ getColumnTitle(index, column) }}</span>
          <el-input
            v-model="filters[index]"
            placeholder="搜索"
            clearable
            class="column-search"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="column-content">
          <div
            v-for="item in getFilteredItems(column.permissions, index)"
            :key="item.id"
            class="column-item"
            :class="{ 
              active: selectedNodes[index]?.id === item.id,
              selected: selectedItems.includes(item),
              'is-button': item.type === 'BUTTON',
              'is-api': item.type === 'API'
            }"
            @click="handleItemClick(item, index)"
            @dblclick="handleItemDblClick(item)"
            @keydown="handleKeyDown($event, item, index)"
            tabindex="0"
          >
            <div class="item-main">
              <span class="item-name">
                <el-icon v-if="item.type === 'MENU'"><Menu /></el-icon>
                <el-icon v-else-if="item.type === 'BUTTON'"><Operation /></el-icon>
                <el-icon v-else><Link /></el-icon>
                {{ item.permissionName }}
              </span>
              <span class="item-code">{{ item.permissionCode }}</span>
            </div>
            <div class="item-extra">
              <el-tag size="small" :type="getTypeTagType(item.type)">
                {{ item.type }}
              </el-tag>
              <el-switch
                v-model="item.status"
                :active-value="1"
                :inactive-value="0"
                @change="(val) => handleStatusChange(item, Number(val))"
                @click.stop
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Search, Menu, Operation, Link } from '@element-plus/icons-vue'
import type { Permission } from '../types/permission'

const props = defineProps<{
  permissions: Permission[]
}>()

const emit = defineEmits<{
  (e: 'select', permission: Permission): void
  (e: 'status-change', permission: Permission, status: number): void
  (e: 'delete', permissions: Permission[]): void
}>()

// 选中的节点
const selectedNodes = ref<(Permission | null)[]>([])
// 选中的权限项
const selectedItems = ref<Permission[]>([])
// 搜索过滤器
const filters = ref<string[]>([])

// 计算面包屑导航
const breadcrumbs = computed(() => {
  return selectedNodes.value.filter(node => node !== null) as Permission[]
})

// 计算列数据
const columns = computed(() => {
  const result: { permissions: Permission[] }[] = []
  
  // 第一列显示所有顶级权限
  result.push({
    permissions: props.permissions.filter(p => !p.pid || p.pid === 0)
  })
  
  // 根据选中的节点添加子级权限列
  selectedNodes.value.forEach((node, index) => {
    if (node && node.children && node.children.length > 0) {
      result.push({
        permissions: node.children
      })
    }
  })
  
  return result
})

// 获取列标题
const getColumnTitle = (index: number, column: { permissions: Permission[] }) => {
  if (index === 0) return '顶级权限'
  const parent = selectedNodes.value[index - 1]
  return parent ? `${parent.permissionName}的子权限` : '子权限'
}

// 获取过滤后的权限列表
const getFilteredItems = (permissions: Permission[], columnIndex: number) => {
  const filter = filters.value[columnIndex]
  if (!filter) return permissions
  
  const searchText = filter.toLowerCase()
  return permissions.filter(item => 
    item.permissionName.toLowerCase().includes(searchText) ||
    item.permissionCode.toLowerCase().includes(searchText)
  )
}

// 获取权限类型对应的标签类型
const getTypeTagType = (type: string): 'success' | 'warning' | 'info' | 'primary' => {
  switch (type) {
    case 'MENU': return 'primary'
    case 'BUTTON': return 'warning'
    case 'API': return 'info'
    default: return 'info'
  }
}

// 处理权限项点击
const handleItemClick = (item: Permission, columnIndex: number) => {
  // 更新选中节点
  selectedNodes.value = selectedNodes.value.slice(0, columnIndex + 1)
  selectedNodes.value[columnIndex] = item
  
  // 清除后续列的过滤器
  filters.value = filters.value.slice(0, columnIndex + 1)
  
  // 触发选择事件
  emit('select', item)
}

// 处理权限项双击
const handleItemDblClick = (item: Permission) => {
  const index = selectedItems.value.findIndex(i => i.id === item.id)
  if (index === -1) {
    selectedItems.value.push(item)
  } else {
    selectedItems.value.splice(index, 1)
  }
}

// 处理面包屑点击
const handleBreadcrumbClick = (item: Permission) => {
  const index = selectedNodes.value.findIndex(node => node?.id === item.id)
  if (index !== -1) {
    selectedNodes.value = selectedNodes.value.slice(0, index + 1)
    filters.value = filters.value.slice(0, index + 1)
  }
}

// 处理键盘事件
const handleKeyDown = (event: KeyboardEvent, item: Permission, columnIndex: number) => {
  if (event.key === 'Enter') {
    handleItemDblClick(item)
  } else if (event.key === 'Delete') {
    emit('delete', [item])
  }
}

// 处理状态变更
const handleStatusChange = (item: Permission, status: number) => {
  emit('status-change', item, status)
}

// 监听权限数据变化
watch(() => props.permissions, () => {
  // 清空选中状态
  selectedNodes.value = []
  selectedItems.value = []
  filters.value = []
}, { deep: true })
</script>

<style scoped lang="scss">
.permission-columns {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color);
  
  .breadcrumb {
    padding: 16px;
    border-bottom: 1px solid var(--el-border-color-light);
    
    .breadcrumb-item {
      cursor: pointer;
      color: var(--el-text-color-primary);
      
      &:hover {
        color: var(--el-color-primary);
      }
    }
  }

  .columns-container {
    flex: 1;
    display: flex;
    gap: 1px;
    background-color: var(--el-border-color-light);
    overflow: hidden;

    .column {
      flex: 1;
      display: flex;
      flex-direction: column;
      background-color: var(--el-bg-color);
      min-width: 250px;
      
      .column-header {
        padding: 16px;
        border-bottom: 1px solid var(--el-border-color-light);
        display: flex;
        align-items: center;
        gap: 12px;
        
        .title {
          font-size: 14px;
          font-weight: 500;
          white-space: nowrap;
        }
        
        .column-search {
          flex: 1;
        }
      }
      
      .column-content {
        flex: 1;
        overflow-y: auto;
        padding: 8px;
        
        .column-item {
          padding: 12px;
          border-radius: 4px;
          cursor: pointer;
          transition: all 0.2s;
          outline: none;
          
          &:hover {
            background-color: var(--el-fill-color-light);
          }
          
          &.active {
            background-color: var(--el-color-primary-light-9);
          }
          
          &.selected {
            background-color: var(--el-color-primary-light-8);
            border: 1px solid var(--el-color-primary);
          }
          
          &:focus {
            outline: none;
            box-shadow: 0 0 0 2px var(--el-color-primary-light-5);
          }
          
          .item-main {
            display: flex;
            flex-direction: column;
            gap: 4px;
            
            .item-name {
              font-size: 14px;
              color: var(--el-text-color-primary);
              display: flex;
              align-items: center;
              gap: 8px;
              
              .el-icon {
                font-size: 16px;
              }
            }
            
            .item-code {
              font-size: 12px;
              color: var(--el-text-color-secondary);
            }
          }
          
          .item-extra {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-top: 8px;
          }
          
          &.is-button {
            background-color: var(--el-fill-color-lighter);
            
            .item-name {
              color: var(--el-text-color-regular);
            }
          }
          
          &.is-api {
            background-color: var(--el-fill-color-dark);
            
            .item-name {
              color: var(--el-text-color-secondary);
            }
          }
        }
      }
    }
  }
}

.debug-info {
  padding: 20px;
  background-color: #f5f7fa;
  margin-bottom: 16px;
  border-radius: 4px;
  
  p {
    margin: 0;
    font-family: monospace;
    white-space: pre-wrap;
  }
}
</style> 