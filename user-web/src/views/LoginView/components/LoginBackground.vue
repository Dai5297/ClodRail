<template>
  <div class="login-bg">
    <div class="bg-content">
      <h1>欢迎使用铁路订票系统</h1>
      <p>安全、便捷、高效的出行体验</p>
      
      <div class="feature-list">
        <div class="feature-item">
          <el-icon><Tickets /></el-icon>
          <span>在线购票</span>
        </div>
        <div class="feature-item">
          <el-icon><Clock /></el-icon>
          <span>实时查询</span>
        </div>
        <div class="feature-item">
          <el-icon><Star /></el-icon>
          <span>优质服务</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Tickets, Clock, Star } from '@element-plus/icons-vue'
</script>

<style scoped>
.login-bg {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.login-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('/images/train-bg.svg') no-repeat center;
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

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
}

.feature-item .el-icon {
  font-size: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-bg {
    height: 200px;
  }
  
  .bg-content h1 {
    font-size: 24px;
  }
  
  .bg-content p {
    font-size: 16px;
  }
  
  .feature-list {
    flex-direction: row;
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .feature-item {
    font-size: 14px;
  }
}
</style>