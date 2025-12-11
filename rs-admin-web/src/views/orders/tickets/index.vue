<template>
  <div class="orders-page">
    <div class="page-header">
      <h1 class="page-title">订单管理</h1>
      <p class="page-description">管理系统订单信息</p>
    </div>

    <el-card>
      <div class="empty-placeholder">
        <el-icon :size="80" color="#d9d9d9"><Tickets /></el-icon>
        <p>订单管理功能开发中...</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.orders-page {
  width: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.page-description {
  color: #666;
  font-size: 14px;
}

.empty-placeholder {
  height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}

.empty-placeholder p {
  margin-top: 20px;
  font-size: 16px;
}
</style>

