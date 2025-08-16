<script setup lang="ts">
import { ElButton, ElIcon } from 'element-plus'
import { Delete, Setting } from '@element-plus/icons-vue'

// 定义事件
const emit = defineEmits(['clearChat', 'openSettings'])

// 处理清空对话
const handleClearChat = () => {
  emit('clearChat')
}

// 处理打开设置
const handleOpenSettings = () => {
  emit('openSettings')
}
</script>

<template>
  <div class="ai-header">
    <div class="ai-avatar">
      <div class="ai-avatar-placeholder">🤖</div>
      <div class="ai-status online"></div>
    </div>
    <div class="ai-info">
      <h1 class="ai-name">小铁助手</h1>
      <p class="ai-description">您的专属出行顾问，为您提供智能化的铁路出行服务</p>
    </div>
    <div class="ai-actions">
      <el-button 
        type="text" 
        class="action-btn"
        @click="handleClearChat"
        title="清空对话"
      >
        <el-icon><Delete /></el-icon>
      </el-button>
      <el-button 
        type="text" 
        class="action-btn"
        @click="handleOpenSettings"
        title="设置"
      >
        <el-icon><Setting /></el-icon>
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.ai-header {
  display: flex;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.ai-avatar {
  position: relative;
  margin-right: 16px;
}

.ai-avatar-placeholder {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  backdrop-filter: blur(10px);
}

.ai-status {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid white;
}

.ai-status.online {
  background: #52c41a;
}

.ai-info {
  flex: 1;
}

.ai-name {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: white;
}

.ai-description {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.4;
}

.ai-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: none;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.05);
}

@media (max-width: 768px) {
  .ai-header {
    padding: 16px;
  }
  
  .ai-avatar-placeholder {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }
  
  .ai-name {
    font-size: 20px;
  }
  
  .ai-description {
    font-size: 13px;
  }
}
</style>