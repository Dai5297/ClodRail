<template>
  <div class="page-container">
    <div class="page-header">
      <h2>商城订单</h2>
    </div>
    <el-card>
      <el-table :data="[]" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button link type="primary">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已取消' }
  return map[status] || '未知'
}
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
