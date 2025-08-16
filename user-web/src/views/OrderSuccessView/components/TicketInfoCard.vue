<template>
  <div class="ticket-info-card">
    <div class="card-header">
      <h3>车票信息</h3>
    </div>
    
    <div class="ticket-details">
      <div class="train-info">
        <div class="train-number">{{ ticketInfo.trainNumber }}</div>
        <div class="train-type">{{ getTrainType(ticketInfo.trainNumber) }}</div>
      </div>
      
      <div class="route-info">
        <div class="station departure">
          <div class="station-name">{{ ticketInfo.departure }}</div>
          <div class="time">{{ ticketInfo.departureTime }}</div>
          <div class="date">{{ ticketInfo.date }}</div>
        </div>
        
        <div class="route-arrow">
          <el-icon size="24"><ArrowRight /></el-icon>
          <div class="duration">{{ ticketInfo.duration }}</div>
        </div>
        
        <div class="station arrival">
          <div class="station-name">{{ ticketInfo.destination }}</div>
          <div class="time">{{ ticketInfo.arrivalTime }}</div>
          <div class="date">{{ ticketInfo.date }}</div>
        </div>
      </div>
      
      <div class="passenger-info">
        <div class="passenger-list">
          <div 
            v-for="(passenger, index) in ticketInfo.passengers" 
            :key="index"
            class="passenger-item"
          >
            <div class="passenger-details">
              <div class="passenger-name">{{ passenger.name }}</div>
              <div class="passenger-type">{{ passenger.type }}</div>
            </div>
            <div class="seat-info">
              <div class="seat-type">{{ ticketInfo.seatType }}</div>
              <div class="seat-number">{{ passenger.seatNumber }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowRight } from '@element-plus/icons-vue'

// 定义props
const props = defineProps<{
  ticketInfo: {
    trainNumber: string
    departure: string
    destination: string
    date: string
    departureTime: string
    arrivalTime: string
    duration: string
    seatType: string
    passengers: Array<{
      name: string
      type: string
      seatNumber: string
    }>
  }
}>()

// 获取车次类型
const getTrainType = (trainNumber: string) => {
  const type = trainNumber.charAt(0)
  const typeMap: Record<string, string> = {
    'G': '高速动车',
    'D': '动车组',
    'C': '城际列车',
    'K': '快速列车',
    'T': '特快列车',
    'Z': '直达列车'
  }
  return typeMap[type] || '普通列车'
}
</script>

<style scoped>
.ticket-info-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #262626;
}

.ticket-details {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.train-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.train-number {
  font-size: 24px;
  font-weight: bold;
  color: #1890FF;
}

.train-type {
  background: #e6f7ff;
  color: #1890FF;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.route-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
}

.station {
  text-align: center;
  flex: 1;
}

.station-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.time {
  font-size: 20px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 4px;
}

.date {
  color: #8c8c8c;
  font-size: 14px;
}

.route-arrow {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #8c8c8c;
}

.duration {
  font-size: 12px;
  white-space: nowrap;
}

.passenger-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.passenger-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f6f8fa;
  border-radius: 6px;
}

.passenger-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.passenger-name {
  font-weight: 500;
}

.passenger-type {
  color: #8c8c8c;
  font-size: 14px;
}

.seat-info {
  text-align: right;
}

.seat-type {
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 4px;
}

.seat-number {
  font-weight: bold;
  color: #1890FF;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .route-info {
    flex-direction: column;
    gap: 16px;
  }
  
  .station {
    flex: none;
  }
  
  .route-arrow {
    transform: rotate(90deg);
  }
  
  .passenger-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .seat-info {
    text-align: left;
  }
}
</style>