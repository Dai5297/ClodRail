<template>
  <div class="recommend-services">
    <div class="section-header">
      <h3>推荐服务</h3>
      <p>更多便民服务，让您的出行更加便捷</p>
    </div>
    
    <div class="services-grid">
      <div class="service-item" @click="goToAIAssistant">
        <div class="service-icon ai">
          <el-icon size="32"><ChatDotRound /></el-icon>
        </div>
        <div class="service-content">
          <h4>AI助手</h4>
          <p>智能客服，24小时为您服务</p>
        </div>
        <el-icon class="arrow-icon"><ArrowRight /></el-icon>
      </div>
      
      <div class="service-item" @click="goToPointsMall">
        <div class="service-icon points">
          <el-icon size="32"><Present /></el-icon>
        </div>
        <div class="service-content">
          <h4>积分商城</h4>
          <p>积分兑换好礼，惊喜等您来</p>
        </div>
        <el-icon class="arrow-icon"><ArrowRight /></el-icon>
      </div>
      
      <div class="service-item" @click="downloadApp">
        <div class="service-icon app">
          <el-icon size="32"><Download /></el-icon>
        </div>
        <div class="service-content">
          <h4>下载APP</h4>
          <p>手机购票更便捷，随时随地订车票</p>
        </div>
        <el-icon class="arrow-icon"><ArrowRight /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Present, Download, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()

// 跳转到AI助手
const goToAIAssistant = () => {
  router.push({ name: 'AIAssistant' })
}

// 跳转到积分商城
const goToPointsMall = () => {
  router.push({ name: 'PointsMall' })
}

// 下载APP
const downloadApp = () => {
  ElMessage.info('APP下载功能即将上线，敬请期待！')
}
</script>

<style scoped>
.recommend-services {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.section-header {
  text-align: center;
  margin-bottom: 24px;
}

.section-header h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #262626;
}

.section-header p {
  margin: 0;
  color: #8c8c8c;
  font-size: 14px;
}

.services-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.service-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.service-item:hover {
  border-color: #1890FF;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
}

.service-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.service-icon.ai {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.service-icon.points {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.service-icon.app {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #fff;
}

.service-content {
  flex: 1;
}

.service-content h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #262626;
}

.service-content p {
  margin: 0;
  color: #8c8c8c;
  font-size: 14px;
}

.arrow-icon {
  color: #bfbfbf;
  transition: color 0.3s ease;
}

.service-item:hover .arrow-icon {
  color: #1890FF;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .service-item {
    padding: 12px;
  }
  
  .service-icon {
    width: 48px;
    height: 48px;
    margin-right: 12px;
  }
  
  .service-content h4 {
    font-size: 15px;
  }
  
  .service-content p {
    font-size: 13px;
  }
}
</style>