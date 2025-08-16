<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElButton } from 'element-plus'

// 定义事件
const emit = defineEmits<{
  openRating: []
}>()

// 当前时间
const currentTime = ref('')

// 常见问题列表
const faqList = [
  {
    icon: '❓',
    text: '如何购买火车票？',
    question: '如何购买火车票？'
  },
  {
    icon: '🔄',
    text: '可以改签几次？',
    question: '可以改签几次？'
  },
  {
    icon: '💰',
    text: '退票手续费是多少？',
    question: '退票手续费是多少？'
  },
  {
    icon: '👶',
    text: '儿童票怎么购买？',
    question: '儿童票怎么购买？'
  },
  {
    icon: '🐕',
    text: '可以带宠物上车吗？',
    question: '可以带宠物上车吗？'
  }
]

// 实时信息
const realtimeInfo = [
  {
    label: '在线用户',
    value: '1,234'
  },
  {
    label: '今日咨询',
    value: '5,678'
  }
]

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 定时器
let timeInterval: number

// 处理FAQ点击
const handleFaqClick = (question: string) => {
  // 这里可以触发发送消息事件，但需要通过父组件传递
  console.log('FAQ clicked:', question)
}

// 处理评价按钮点击
const handleRatingClick = () => {
  emit('openRating')
}

// 组件挂载
onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)
})

// 组件卸载
onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<template>
  <div class="sidebar-info">
    <!-- 常见问题 -->
    <div class="info-card">
      <h3 class="card-title">常见问题</h3>
      <div class="faq-list">
        <div 
          v-for="faq in faqList" 
          :key="faq.question"
          class="faq-item"
          @click="handleFaqClick(faq.question)"
        >
          <span class="faq-icon">{{ faq.icon }}</span>
          <span class="faq-text">{{ faq.text }}</span>
        </div>
      </div>
    </div>

    <!-- 实时信息 -->
    <div class="info-card">
      <h3 class="card-title">实时信息</h3>
      <div class="real-time-info">
        <div class="info-item">
          <span class="info-label">当前时间</span>
          <span class="info-value">{{ currentTime }}</span>
        </div>
        <div 
          v-for="info in realtimeInfo" 
          :key="info.label"
          class="info-item"
        >
          <span class="info-label">{{ info.label }}</span>
          <span class="info-value">{{ info.value }}</span>
        </div>
      </div>
    </div>

    <!-- 服务评价 -->
    <div class="info-card">
      <h3 class="card-title">服务评价</h3>
      <div class="rating-container">
        <div class="rating-stars">
          <span class="star active">★</span>
          <span class="star active">★</span>
          <span class="star active">★</span>
          <span class="star active">★</span>
          <span class="star active">★</span>
        </div>
        <p class="rating-text">4.9分 (基于1,234个评价)</p>
        <el-button 
          type="primary" 
          size="small" 
          class="rating-btn"
          @click="handleRatingClick"
        >
          评价服务
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.sidebar-info {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-title {
  margin: 0;
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.faq-list {
  padding: 8px 0;
}

.faq-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.faq-item:hover {
  background: #f5f5f5;
}

.faq-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.faq-text {
  flex: 1;
  font-size: 14px;
  color: #595959;
  line-height: 1.4;
}

.real-time-info {
  padding: 16px 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 14px;
  color: #8c8c8c;
}

.info-value {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
}

.rating-container {
  padding: 16px 20px;
  text-align: center;
}

.rating-stars {
  display: flex;
  justify-content: center;
  gap: 4px;
  margin-bottom: 8px;
}

.star {
  font-size: 18px;
  color: #d9d9d9;
  transition: color 0.3s ease;
}

.star.active {
  color: #faad14;
}

.rating-text {
  margin: 0 0 16px 0;
  font-size: 13px;
  color: #8c8c8c;
}

.rating-btn {
  width: 100%;
}

@media (max-width: 1024px) {
  .sidebar-info {
    width: 100%;
    flex-direction: row;
    overflow-x: auto;
  }
  
  .info-card {
    flex-shrink: 0;
    width: 280px;
  }
}

@media (max-width: 768px) {
  .sidebar-info {
    flex-direction: column;
  }
  
  .info-card {
    width: 100%;
  }
}
</style>