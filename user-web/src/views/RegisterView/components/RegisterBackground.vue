<template>
  <div class="register-bg">
    <div class="bg-content">
      <h1>加入我们</h1>
      <p>开启便捷的出行体验</p>
      <div class="benefit-list">
        <div class="benefit-item">
          <el-icon><Present /></el-icon>
          <span>新用户专享优惠</span>
        </div>
        <div class="benefit-item">
          <el-icon><Star /></el-icon>
          <span>积分奖励计划</span>
        </div>
        <div class="benefit-item">
          <el-icon><Service /></el-icon>
          <span>24小时客服服务</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Present, Star, Service } from '@element-plus/icons-vue'
</script>

<style scoped>
.register-bg {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.register-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('/images/register-bg.svg') no-repeat center;
  background-size: cover;
  opacity: 0.1;
}

.bg-content {
  text-align: center;
  z-index: 1;
}

.bg-content h1 {
  font-size: 32px;
  margin-bottom: 16px;
  font-weight: bold;
}

.bg-content p {
  font-size: 18px;
  margin-bottom: 32px;
  opacity: 0.9;
}

.benefit-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
}

.benefit-item .el-icon {
  font-size: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-bg {
    height: 200px;
  }
  
  .bg-content h1 {
    font-size: 24px;
  }
  
  .bg-content p {
    font-size: 16px;
  }
  
  .benefit-list {
    flex-direction: row;
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .benefit-item {
    font-size: 14px;
  }
}
</style>