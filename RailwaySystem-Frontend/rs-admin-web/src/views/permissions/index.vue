<template>
  <div class="page-container">
    <div class="page-header">
      <h2>权限管理</h2>
      <el-button type="primary" :icon="Plus">新增权限</el-button>
    </div>
    <el-card>
      <el-table :data="tableData" style="width: 100%" row-key="id" default-expand-all>
        <el-table-column prop="name" label="权限名称" width="200" />
        <el-table-column prop="code" label="权限标识" width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'primary' : 'success'">
              {{ scope.row.type === 1 ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'

const tableData = ref([
  {
    id: 1,
    name: '用户管理',
    code: 'users',
    type: 1,
    description: '用户管理模块',
    children: [
      { id: 11, name: 'C端用户管理', code: 'users:customers', type: 1, description: 'C端用户列表' },
      { id: 12, name: 'B端用户管理', code: 'users:admins', type: 1, description: 'B端用户列表' }
    ]
  },
  {
    id: 2,
    name: '运营管理',
    code: 'operation',
    type: 1,
    description: '运营管理模块',
    children: [
      { id: 21, name: '仪表盘', code: 'operation:dashboard', type: 1, description: '数据仪表盘' },
      { id: 22, name: '列车管理', code: 'operation:trains', type: 1, description: '列车信息管理' }
    ]
  }
])
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}
</style>
