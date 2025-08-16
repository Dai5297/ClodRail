<template>
  <div class="success-header">
    <div class="success-icon">
      <el-icon size="80" color="#52c41a"><CircleCheck /></el-icon>
    </div>
    <h1 class="success-title">支付成功</h1>
    <p class="success-subtitle">恭喜您，订单支付成功！</p>
  </div>
</template>

<script setup lang="ts">
import { CircleCheck } from '@element-plus/icons-vue'
</script>

<style scoped>
.success-header {
  text-align: center;
  margin-bottom: 40px;
  color: #fff;
}

.success-icon {
  background: #fff;
  border-radius: 50%;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.success-title {
  font-size: 32px;
  font-weight: bold;
  margin: 0 0 8px 0;
}

.success-subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .success-title {
    font-size: 24px;
  }
  
  .success-subtitle {
    font-size: 16px;
  }
}
</style>