<template>
  <div class="countdown-card">
    <div class="countdown-content">
      <el-icon class="countdown-icon"><Clock /></el-icon>
      <div class="countdown-text">
        <div class="countdown-title">请在以下时间内完成支付</div>
        <div class="countdown-timer">
          <span class="time-number">{{ formattedTime }}</span>
        </div>
        <div class="countdown-tip">超时订单将自动取消</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Clock } from '@element-plus/icons-vue'

const props = defineProps({
  remainingTime: {
    type: Number,
    required: true
  }
})

// 格式化倒计时
const formattedTime = computed(() => {
  const minutes = Math.floor(props.remainingTime / 60)
  const seconds = props.remainingTime % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})
</script>

<style scoped>
.countdown-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.countdown-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.countdown-icon {
  font-size: 32px;
  color: #ff6b35;
}

.countdown-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.countdown-timer {
  font-size: 24px;
  font-weight: 600;
  color: #ff6b35;
  margin-bottom: 4px;
}

.countdown-tip {
  font-size: 12px;
  color: #999;
}
</style>

