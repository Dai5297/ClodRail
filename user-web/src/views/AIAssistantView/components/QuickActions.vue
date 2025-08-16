<script setup lang="ts">
import { ElButton } from 'element-plus'

// 定义事件
const emit = defineEmits(['quickAction'])

// 快捷功能配置
const quickButtons = [
  {
    action: 'search-train',
    icon: '🚄',
    text: '查询车次'
  },
  {
    action: 'ticket-info',
    icon: '🎫',
    text: '购票咨询'
  },
  {
    action: 'route-plan',
    icon: '🗺️',
    text: '路线规划'
  },
  {
    action: 'weather-info',
    icon: '🌤️',
    text: '天气查询'
  },
  {
    action: 'station-info',
    icon: '🏢',
    text: '车站信息'
  },
  {
    action: 'travel-guide',
    icon: '📖',
    text: '出行指南'
  }
]

// 处理快捷功能点击
const handleQuickAction = (action: string) => {
  emit('quickAction', action)
}
</script>

<template>
  <div class="quick-actions">
    <h3 class="quick-title">快捷服务</h3>
    <div class="quick-buttons">
      <button 
        v-for="button in quickButtons" 
        :key="button.action"
        class="quick-btn"
        @click="handleQuickAction(button.action)"
      >
        <span class="quick-icon">{{ button.icon }}</span>
        <span class="quick-text">{{ button.text }}</span>
      </button>
    </div>
  </div>
</template>

<style scoped>
.quick-actions {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.quick-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.quick-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
}

.quick-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 12px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  color: inherit;
}

.quick-btn:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
  transform: translateY(-2px);
}

.quick-icon {
  font-size: 24px;
  line-height: 1;
}

.quick-text {
  font-size: 13px;
  color: #595959;
  text-align: center;
  line-height: 1.2;
}

.quick-btn:hover .quick-text {
  color: #1890ff;
}

@media (max-width: 768px) {
  .quick-actions {
    padding: 16px;
  }
  
  .quick-buttons {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }
  
  .quick-btn {
    padding: 12px 8px;
  }
  
  .quick-icon {
    font-size: 20px;
  }
  
  .quick-text {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .quick-buttons {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>