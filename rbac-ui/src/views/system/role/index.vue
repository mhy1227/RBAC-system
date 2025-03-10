<template>
  <div class="role-container">
    <!-- 搜索表单 -->
    <el-form :model="searchForm" ref="searchFormRef" class="search-form">
      <el-form-item prop="roleName">
        <el-input
          v-model="searchForm.roleName"
          placeholder="角色名称"
          clearable
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item prop="roleCode">
        <el-input
          v-model="searchForm.roleCode"
          placeholder="角色编码"
          clearable
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item prop="status">
        <!-- 
          状态选择 - 下拉框实现（预留扩展）
          适用于状态类型较多的场景
        -->
        <!--
        <el-select v-model="searchForm.status" placeholder="状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        -->
        
        <!-- 
          状态选择 - 单选按钮组实现
          适用于简单的二元状态场景
        -->
        <el-radio-group v-model="searchForm.status" class="status-filter">
          <el-radio :label="''">全部</el-radio>
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      <div class="search-buttons">
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </el-form>

    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
      <el-button
        type="danger"
        :icon="Delete"
        :disabled="selectedIds.length === 0"
        @click="handleBatchDelete"
      >
        批量删除
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="roleList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="roleName" label="角色名称" />
      <el-table-column prop="roleCode" label="角色编码" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="(val) => handleStatusChange(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" align="center" width="120">
        <template #default="{ row }">
          <el-dropdown trigger="click">
            <el-button type="primary" link>
              操作<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleView(row)">
                  <el-icon><View /></el-icon>查看
                </el-dropdown-item>
                <el-dropdown-item @click="handleEdit(row)">
                  <el-icon><edit /></el-icon>编辑
                </el-dropdown-item>
                <el-dropdown-item @click="handlePermission(row)">
                  <el-icon><setting /></el-icon>权限
                </el-dropdown-item>
                <el-dropdown-item divided type="danger" @click="handleDelete(row)">
                  <el-icon><delete /></el-icon>删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 角色表单 -->
    <role-form
      v-model:visible="formVisible"
      :role-data="currentRole"  
      @success="handleSuccess"
    />

    <!-- 权限树 -->
    <perm-tree
      v-model:visible="permVisible"
      :role-data="currentRole"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Search, Refresh, Plus, Edit, Delete, Setting, ArrowDown, View } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import type { RoleInfo } from '@/types/user'
import useRoleList from './hooks/useRoleList'
import useRoleForm from './hooks/useRoleForm'
import RoleForm from './components/RoleForm.vue'
import PermTree from './components/PermTree.vue'

const router = useRouter()

const {
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
} = useRoleList()

const {
  formVisible,
  currentRole,
  handleAdd,
  handleEdit
} = useRoleForm()

// 权限树对话框可见性
const permVisible = ref(false)

// 打开权限分配对话框
const handlePermission = (row: RoleInfo) => {
  currentRole.value = { ...row }
  permVisible.value = true
}

// 查看角色详情
const handleView = (row: RoleInfo) => {
  router.push({
    name: 'RoleDetail',
    params: { id: row.id }
  })
}

onMounted(() => {
  handleSearch()
})
</script>

<style lang="scss" scoped>
.role-container {
  padding: 20px;

  .search-form {
    display: flex;
    flex-wrap: nowrap;
    align-items: flex-start;
    gap: 16px;
    margin-bottom: 20px;

    .el-form-item {
      margin-right: 0;
      margin-bottom: 0;
      flex: 1;
      min-width: 200px;
      max-width: 300px;
    }

    .search-buttons {
      flex: none;
      min-width: auto;
    }
  }

  .toolbar {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .status-filter {
    display: inline-flex !important;
    gap: 8px !important;

    :deep(.el-radio) {
      margin-right: 0;
      height: 32px;
      line-height: 32px;
      padding: 0 12px;
      border-radius: 16px;
      
      &.is-checked {
        color: var(--el-color-primary);
        background-color: var(--el-color-primary-light-9);
      }

      .el-radio__label {
        padding-left: 4px;
      }
    }
  }
}
</style>
