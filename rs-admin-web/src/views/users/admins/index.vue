<template>
  <div class="admins-container">
    <div class="page-header">
      <h2>管理员管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增管理员</el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="roleName" label="角色" width="150">
          <template #default="scope">
            <el-tag :type="getRoleTagType(scope.row.role)">{{ scope.row.roleName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="warning" @click="handleResetPwd(scope.row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    username: 'admin',
    realName: '超级管理员',
    role: 104,
    roleName: '超级管理员',
    status: 1,
    createTime: '2024-01-01 12:00:00'
  }
])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(1)

const getRoleTagType = (role) => {
  const map = {
    104: 'danger',
    103: 'warning',
    102: 'success',
    101: 'info',
    100: ''
  }
  return map[role] || ''
}

const handleAdd = () => {
  ElMessage.info('功能开发中...')
}

const handleEdit = (row) => {
  ElMessage.info('功能开发中...')
}

const handleResetPwd = (row) => {
  ElMessageBox.confirm(`确认重置管理员 ${row.username} 的密码吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('重置成功，默认密码: 123456')
  }).catch(() => {})
}

const handleStatusChange = (row) => {
  ElMessage.success(`状态已${row.status === 1 ? '启用' : '禁用'}`)
}

const handleSizeChange = (val) => {
  pageSize.value = val
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}
</script>

<style scoped>
.admins-container {
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

.table-card {
  border-radius: 8px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
