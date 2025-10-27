<template>
  <div class="route-card" @click="handleSelect">
    <div class="route-info">
      <div class="route-stations">
        <span class="origin">{{ route.originStation?.name || '未知站点' }}</span>
        <el-icon class="arrow-icon"><ArrowRight /></el-icon>
        <span class="destination">{{ route.destinationStation?.name || '未知站点' }}</span>
      </div>
      <div class="route-details">
        <span class="route-name">{{ route.routeName }}</span>
        <span class="min-price">￥{{ route.minPrice }}起</span>
      </div>
      <div class="route-meta">
        <span class="duration">{{ formatDuration(route.avgDuration) }}</span>
        <span class="popularity">热度 {{ route.popularity }}%</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ArrowRight } from '@element-plus/icons-vue'

const props = defineProps({
  route: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['select'])

const handleSelect = () => {
  emit('select', props.route)
}

// 格式化ISO 8601持续时间为友好显示
const formatDuration = (isoDuration) => {
  if (!isoDuration) return '计算中'
    
  const match = isoDuration.match(/^PT(-?\d+(?:\.\d+)?H)?(-?\d+(?:\.\d+)?M)?(-?\d+(?:\.\d+)?S)?$/)
  if (!match) {
    return isoDuration
  }
  
  const hoursStr = match[1] ? match[1].replace('H', '') : '0'
  const minutesStr = match[2] ? match[2].replace('M', '') : '0'
  const secondsStr = match[3] ? match[3].replace('S', '') : '0'
  
  const hours = Math.abs(parseInt(hoursStr))
  const minutes = Math.abs(parseInt(minutesStr))
  const seconds = Math.abs(parseFloat(secondsStr))
    
  if (hours > 0 && minutes > 0) {
    return `${hours}小时${minutes}分钟`
  } else if (hours > 0) {
    return `${hours}小时`
  } else if (minutes > 0) {
    return `${minutes}分钟`
  } else if (seconds > 0) {
    return `${Math.round(seconds)}秒`
  } else {
    return '计算中'
  }
}
</script>

<style scoped>
.route-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.route-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.route-stations {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 18px;
  font-weight: 600;
}

.origin, .destination {
  color: #333;
}

.arrow-icon {
  color: #1890ff;
}

.route-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.route-name {
  color: #666;
  font-size: 14px;
}

.min-price {
  color: #ff6b35;
  font-weight: 600;
  font-size: 16px;
}

.route-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}
</style>

