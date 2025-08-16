<script setup lang="ts">
import { computed } from 'vue'
import { ElCard, ElSkeleton, ElIcon, ElTag, ElDivider } from 'element-plus'
import { 
  LocationFilled, 
  Timer, 
  Calendar,
  Position
} from '@element-plus/icons-vue'

// 定义属性
interface Order {
  id: string
  trainNumber: string
  trainType: string
  departure: string
  arrival: string
  departureTime: string
  arrivalTime: string
  duration: string
  seatType: string
  carNumber: string
  seatNumbers: string[]
  distance: string
  departureStation: string
  arrivalStation: string
}

interface Props {
  order: Order
  loading: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

// 格式化时间
const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化日期
const formatDate = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    weekday: 'short'
  })
}

// 获取列车类型颜色
const getTrainTypeColor = (trainType: string) => {
  const colorMap: { [key: string]: string } = {
    'G': '#1890ff', // 高铁
    'D': '#52c41a', // 动车
    'C': '#722ed1', // 城际
    'K': '#fa8c16', // 快速
    'T': '#eb2f96', // 特快
    'Z': '#f5222d'  // 直达
  }
  return colorMap[trainType] || '#8c8c8c'
}

// 获取座位类型显示名称
const getSeatTypeName = (seatType: string) => {
  const nameMap: { [key: string]: string } = {
    'business': '商务座',
    'first': '一等座',
    'second': '二等座',
    'hard_seat': '硬座',
    'soft_seat': '软座',
    'hard_sleeper': '硬卧',
    'soft_sleeper': '软卧'
  }
  return nameMap[seatType] || seatType
}

// 计算行程时长
const getTripDuration = computed(() => {
  if (!props.order.departureTime || !props.order.arrivalTime) return ''
  
  const departure = new Date(props.order.departureTime)
  const arrival = new Date(props.order.arrivalTime)
  const diffMs = arrival.getTime() - departure.getTime()
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
  const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60))
  
  return `${diffHours}小时${diffMinutes}分钟`
})

// 判断是否跨天
const isCrossDay = computed(() => {
  if (!props.order.departureTime || !props.order.arrivalTime) return false
  
  const departure = new Date(props.order.departureTime)
  const arrival = new Date(props.order.arrivalTime)
  
  return departure.getDate() !== arrival.getDate()
})
</script>

<template>
  <ElCard class="trip-details" shadow="never">
    <template #header>
      <div class="card-header">
        <ElIcon :size="20" color="#1890ff">
          <Train />
        </ElIcon>
        <span class="header-title">行程详情</span>
      </div>
    </template>
    
    <!-- 加载状态 -->
    <ElSkeleton v-if="loading" animated>
      <template #template>
        <div class="skeleton-content">
          <el-skeleton-item variant="rect" style="width: 100%; height: 80px; margin-bottom: 16px;" />
          <el-skeleton-item variant="rect" style="width: 100%; height: 120px; margin-bottom: 16px;" />
          <el-skeleton-item variant="rect" style="width: 100%; height: 60px;" />
        </div>
      </template>
    </ElSkeleton>
    
    <!-- 行程内容 -->
    <div v-else class="trip-content">
      <!-- 列车信息 -->
      <div class="train-info">
        <div class="train-number">
          <ElTag
            :color="getTrainTypeColor(order.trainType)"
            class="train-tag"
            size="large"
          >
            {{ order.trainNumber }}
          </ElTag>
          <span class="train-type">{{ order.trainType }}次列车</span>
        </div>
        
        <div class="trip-date">
          <ElIcon :size="16" color="#666">
            <Calendar />
          </ElIcon>
          <span>{{ formatDate(order.departureTime) }}</span>
        </div>
      </div>
      
      <ElDivider class="section-divider" />
      
      <!-- 行程路线 -->
      <div class="trip-route">
        <!-- 出发信息 -->
        <div class="station-info departure">
          <div class="station-time">
            <span class="time">{{ formatTime(order.departureTime) }}</span>
            <span class="date">出发</span>
          </div>
          <div class="station-details">
            <div class="station-name">
              <ElIcon :size="18" color="#52c41a">
                <LocationFilled />
              </ElIcon>
              <span>{{ order.departure }}</span>
            </div>
            <div class="station-full-name">{{ order.departureStation }}</div>
          </div>
        </div>
        
        <!-- 行程线 -->
        <div class="trip-line">
          <div class="line-container">
            <div class="line"></div>
            <div class="duration-info">
              <ElIcon :size="14" color="#1890ff">
                <Timer />
              </ElIcon>
              <span>{{ getTripDuration }}</span>
              <div class="distance">{{ order.distance }}</div>
            </div>
          </div>
        </div>
        
        <!-- 到达信息 -->
        <div class="station-info arrival">
          <div class="station-time">
            <span class="time">{{ formatTime(order.arrivalTime) }}</span>
            <span class="date">
              到达
              <ElTag v-if="isCrossDay" size="small" type="warning">次日</ElTag>
            </span>
          </div>
          <div class="station-details">
            <div class="station-name">
              <ElIcon :size="18" color="#ff4d4f">
                <LocationFilled />
              </ElIcon>
              <span>{{ order.arrival }}</span>
            </div>
            <div class="station-full-name">{{ order.arrivalStation }}</div>
          </div>
        </div>
      </div>
      
      <ElDivider class="section-divider" />
      
      <!-- 座位信息 -->
      <div class="seat-info">
        <div class="info-row">
          <div class="info-item">
            <ElIcon :size="16" color="#666">
              <Position />
            </ElIcon>
            <span class="label">座位类型：</span>
            <span class="value">{{ getSeatTypeName(order.seatType) }}</span>
          </div>
          
          <div class="info-item">
            <span class="label">车厢号：</span>
            <span class="value highlight">{{ order.carNumber }}车</span>
          </div>
        </div>
        
        <div class="seat-numbers">
          <span class="label">座位号：</span>
          <div class="seats">
            <ElTag
              v-for="seat in order.seatNumbers"
              :key="seat"
              class="seat-tag"
              type="info"
            >
              {{ seat }}
            </ElTag>
          </div>
        </div>
      </div>
    </div>
  </ElCard>
</template>

<style scoped>
.trip-details {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1a1a1a;
}

.header-title {
  font-size: 16px;
}

.skeleton-content {
  padding: 0;
}

.trip-content {
  padding: 0;
}

.train-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.train-number {
  display: flex;
  align-items: center;
  gap: 12px;
}

.train-tag {
  font-size: 18px;
  font-weight: 700;
  padding: 8px 16px;
  border-radius: 8px;
  color: white !important;
  border: none;
}

.train-type {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.trip-date {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.section-divider {
  margin: 20px 0;
  border-color: #f0f0f0;
}

.trip-route {
  display: flex;
  align-items: center;
  gap: 0;
  margin-bottom: 20px;
}

.station-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.station-info.departure {
  align-items: flex-start;
}

.station-info.arrival {
  align-items: flex-end;
  text-align: right;
}

.station-time {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.time {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  font-family: 'Arial', sans-serif;
}

.date {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.station-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.station-name {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.station-full-name {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
}

.trip-line {
  flex: 0 0 120px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 20px;
}

.line-container {
  position: relative;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.line {
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #52c41a 0%, #1890ff 50%, #ff4d4f 100%);
  border-radius: 1px;
  position: relative;
}

.line::before,
.line::after {
  content: '';
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  top: 50%;
  transform: translateY(-50%);
}

.line::before {
  left: -4px;
  background: #52c41a;
}

.line::after {
  right: -4px;
  background: #ff4d4f;
}

.duration-info {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  color: #1890ff;
  font-weight: 600;
  white-space: nowrap;
}

.distance {
  font-size: 10px;
  color: #8c8c8c;
  font-weight: 500;
}

.seat-info {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.label {
  color: #666;
  font-weight: 500;
}

.value {
  color: #1a1a1a;
  font-weight: 600;
}

.value.highlight {
  color: #1890ff;
  font-weight: 700;
}

.seat-numbers {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.seats {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.seat-tag {
  font-weight: 600;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .train-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .trip-route {
    flex-direction: column;
    gap: 20px;
  }
  
  .station-info.arrival {
    align-items: flex-start;
    text-align: left;
  }
  
  .trip-line {
    flex: none;
    width: 100%;
    margin: 0;
  }
  
  .line {
    transform: rotate(90deg);
    width: 60px;
  }
  
  .duration-info {
    position: static;
    transform: none;
    margin-top: 10px;
  }
  
  .info-row {
    flex-direction: column;
    gap: 8px;
  }
  
  .seat-numbers {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>