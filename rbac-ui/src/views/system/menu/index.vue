<template>
  <div class="menu-container">
    <el-card class="search-wrapper">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="菜单名称">
          <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search">搜索</el-button>
          <el-button :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-wrapper">
      <template #header>
        <div class="toolbar">
          <el-button type="primary" :icon="Plus">新增</el-button>
        </div>
      </template>

      <el-table :data="[]" v-loading="false" row-key="id">
        <el-table-column label="菜单名称" prop="menuName" />
        <el-table-column label="图标" align="center" width="60">
          <template #default="{ row }">
            <el-icon v-if="row.icon">
              <component :is="row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="orderNum" align="center" width="60" />
        <el-table-column label="权限标识" prop="perms" align="center" />
        <el-table-column label="路由地址" prop="path" align="center" />
        <el-table-column label="组件路径" prop="component" align="center" />
        <el-table-column label="状态" align="center" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" align="center" width="180" />
        <el-table-column label="操作" align="center" width="200">
          <template #default>
            <el-button link type="primary" :icon="Plus">新增</el-button>
            <el-button link type="primary" :icon="Edit">编辑</el-button>
            <el-button link type="danger" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'

const queryParams = reactive({
  menuName: '',
  status: undefined as number | undefined
})
</script>

<style lang="scss" scoped>
.menu-container {
  .search-wrapper {
    margin-bottom: 20px;
  }

  .table-wrapper {
    .toolbar {
      display: flex;
      justify-content: flex-start;
      margin-bottom: 20px;
    }
  }
}
</style>