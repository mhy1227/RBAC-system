<template>
  <div class="user-container">
    <!-- 搜索区域 -->
    <el-card class="search-wrapper">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true">
        <el-form-item>
          <el-input
            v-model="queryParams.username"
            placeholder="用户名"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="queryParams.nickname"
            placeholder="昵称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item style="margin-right: 0">
          <el-radio v-model="queryParams.status" :label="-1">全部</el-radio>
          <el-radio v-model="queryParams.status" :label="1">启用</el-radio>
          <el-radio v-model="queryParams.status" :label="0">禁用</el-radio>
          <el-button type="primary" :icon="Search" @click="handleQuery" style="margin-left: 20px">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 -->
    <div class="toolbar-wrapper">
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
        <el-button type="danger" :icon="Delete" :disabled="!selectedIds.length" @click="handleBatchDelete">
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-card class="table-wrapper">
      <el-table
        v-loading="loading"
        :data="userList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="用户名" prop="username" align="center" min-width="100" />
        <el-table-column label="昵称" prop="nickname" align="center" min-width="100" />
        <el-table-column label="邮箱" prop="email" align="center" min-width="180" />
        <el-table-column label="手机号" prop="phone" align="center" width="100" />
        <el-table-column label="状态" align="center" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              :disabled="row.id === 1"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" align="center" width="160" />
        <el-table-column label="操作" align="center" width="220">
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
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户表单组件 -->
    <user-form
      v-model:visible="dialogVisible"
      :user-data="currentUser"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { Search, Refresh, Plus, Edit, Delete, View } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import useUserList from './hooks/useUserList'
import useUserForm from './hooks/useUserForm'
import UserForm from './components/UserForm.vue'
import type { UserInfo } from '@/types/user'

const router = useRouter()

const {
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
} = useUserList()

const {
  dialogVisible,
  currentUser,
  handleAdd,
  handleUpdate
} = useUserForm()

// 表单提交成功
const handleSuccess = () => {
  getList()
}

// 查看用户详情
const handleView = (row: UserInfo) => {
  router.push({
    name: 'UserDetail',
    params: { id: row.id }
  })
}

// 编辑用户
const handleEdit = (row: UserInfo) => {
  router.push({
    name: 'UserEdit',
    params: { id: row.id }
  })
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.user-container {
  .search-wrapper {
    margin-bottom: 20px;

    :deep(.el-form--inline) {
      display: flex;
      align-items: center;
      
      .el-form-item {
        margin-right: 20px;
        margin-bottom: 0;
      }

      .el-radio {
        margin-right: 15px;
        
        &:last-child {
          margin-right: 0;
        }
      }
    }
  }

  .toolbar-wrapper {
    margin-bottom: 20px;
    display: flex;
    justify-content: center;

    .toolbar {
      display: flex;
      gap: 10px;
    }
  }

  .table-wrapper {
    .toolbar {
      display: flex;
      justify-content: flex-start;
      margin-bottom: 20px;
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>