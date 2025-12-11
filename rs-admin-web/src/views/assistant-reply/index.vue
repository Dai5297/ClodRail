<template>
  <div class="assistant-reply-page">
    <div class="page-header">
      <h1 class="page-title">客服回复</h1>
      <p class="page-description">在线客服回复管理</p>
    </div>

    <el-card>
      <div class="empty-placeholder">
        <el-icon :size="80" color="#d9d9d9"><ChatDotSquare /></el-icon>
        <p>客服回复功能开发中...</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.assistant-reply-page {
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

