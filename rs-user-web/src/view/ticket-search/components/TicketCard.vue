<template>
  <div class="ticket-card">
    <div class="train-info">
      <div class="train-number">{{ ticket.trainCode }}</div>
      <div class="train-type">{{ ticket.trainType }}</div>
    </div>

    <div class="time-info">
      <div class="departure">
        <div class="time">{{ ticket.startTime }}</div>
        <div class="station">{{ ticket.originStation?.name || '未知站点' }}</div>
      </div>
      <div class="duration">
        <div class="route-line">
          <div class="start-dot"></div>
          <div class="dashed-line"></div>
          <div class="end-dot"></div>
        </div>
        <div class="duration-text">
          <el-icon><Clock /></el-icon>
          <span>{{ formatDuration(ticket.duration) }}</span>
        </div>
      </div>
      <div class="arrival">
        <div class="time">{{ ticket.endTime }}</div>
        <div class="station">{{ ticket.destinationStation?.name || '未知站点' }}</div>
      </div>
    </div>

    <div class="seat-info">
      <div 
        v-for="seatType in ticket.seatTypes" 
        :key="seatType.type"
        class="seat-type"
        :class="{ 'sold-out': seatType.remainingSeats === 0 }"
      >
        <div class="seat-name">{{ seatType.name }}</div>
        <div class="seat-price">￥{{ seatType.price }}</div>
        <div class="seat-count">
          {{ seatType.remainingSeats > 0 ? `余${seatType.remainingSeats}` : '无票' }}
        </div>
      </div>
    </div>

    <div class="actions">
      <el-button 
        type="primary" 
        @click="handleViewDetail"
        :disabled="!hasAvailableSeats"
      >
        {{ hasAvailableSeats ? '预订' : '无票' }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Clock } from '@element-plus/icons-vue'

const props = defineProps({
  ticket: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['view-detail'])

const hasAvailableSeats = computed(() => {
  return props.ticket.seatTypes.some(seat => seat.remainingSeats > 0)
})

const handleViewDetail = () => {
  emit('view-detail', props.ticket)
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
.ticket-card {
  display: grid;
  grid-template-columns: 120px 1fr 300px 100px;
  gap: 20px;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.ticket-card:hover {
  background-color: #fafafa;
}

.ticket-card:last-child {
  border-bottom: none;
}

.train-info {
  text-align: center;
}

.train-number {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 4px;
}

.train-type {
  font-size: 12px;
  color: #666;
  background-color: #f0f0f0;
  padding: 2px 8px;
  border-radius: 12px;
  display: inline-block;
}

.time-info {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 20px;
  align-items: center;
}

.departure, .arrival {
  text-align: center;
}

.departure {
  text-align: left;
}

.arrival {
  text-align: right;
}

.time {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.station {
  font-size: 14px;
  color: #666;
}

.duration {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.route-line {
  display: flex;
  align-items: center;
  width: 100px;
}

.start-dot, .end-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #1890ff;
}

.dashed-line {
  flex: 1;
  height: 2px;
  background: linear-gradient(to right, #1890ff 50%, transparent 50%);
  background-size: 8px 2px;
  margin: 0 4px;
}

.duration-text {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.seat-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.seat-type {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
  font-size: 14px;
}

.seat-type.sold-out {
  background-color: #f5f5f5;
  color: #999;
}

.seat-name {
  font-weight: 500;
}

.seat-price {
  color: #ff4d4f;
  font-weight: 600;
}

.seat-count {
  font-size: 12px;
  color: #52c41a;
}

.seat-type.sold-out .seat-count {
  color: #999;
}

.actions {
  text-align: center;
}

@media (max-width: 768px) {
  .ticket-card {
    grid-template-columns: 1fr;
    gap: 15px;
    text-align: center;
  }
  
  .time-info {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .departure, .arrival {
    text-align: center;
  }
  
  .seat-info {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .seat-type {
    min-width: 120px;
  }
}
</style>

