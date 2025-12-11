<template>
  <div class="stations-container">
    <div class="page-header">
      <h2>车站管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增车站</el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="车站名称" width="150" />
        <el-table-column prop="city" label="所在城市" width="120" />
        <el-table-column prop="code" label="车站代码" width="120" />
        <el-table-column prop="address" label="详细地址" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
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
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const handleAdd = () => {
  ElMessage.info('功能开发中...')
}

const handleEdit = (row) => {
  ElMessage.info('功能开发中...')
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该车站吗？', '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {})
}

const handleSizeChange = (val) => {
  pageSize.value = val
  // loadData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  // loadData()
}
</script>

<style scoped>
.stations-container {
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
