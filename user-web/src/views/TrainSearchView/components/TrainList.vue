<script setup lang="ts">
import { ref, computed, withDefaults } from 'vue'
import { 
  ElButton, 
  ElEmpty, 
  ElSkeleton, 
  ElTag, 
  ElIcon,
  ElTooltip,
  ElDivider
} from 'element-plus'
import { 
  ArrowRight, 
  Clock, 
  Location, 
  Warning
} from '@element-plus/icons-vue'
import type { TrainInfo } from '@/api/train'

// 定义属性
interface Props {
  trains: TrainInfo[]
  loading: boolean
  searched: boolean
}

// 定义事件
interface Emits {
  select: [train: TrainInfo]
  reset: []
}

const props = withDefaults(defineProps<Props>(), {
  trains: () => [],
  loading: false,
  searched: false
})

const emit = defineEmits<Emits>()

// 获取车次类型
const getTrainType = (trainNumber: string) => {
  const type = trainNumber.charAt(0)
  const typeMap: { [key: string]: { name: string; color: string } } = {
    'G': { name: '高速动车', color: '#1890ff' },
    'D': { name: '动车', color: '#52c41a' },
    'C': { name: '城际', color: '#faad14' },
    'K': { name: '快速', color: '#722ed1' },
    'T': { name: '特快', color: '#eb2f96' },
    'Z': { name: '直达', color: '#f5222d' }
  }
  return typeMap[type] || { name: '普通', color: '#8c8c8c' }
}

// 检查是否有可用座位
const hasAvailableSeats = (train: TrainInfo) => {
  return train.seats.some(seat => seat.available > 0)
}

// 获取最低价格
const getMinPrice = (train: TrainInfo) => {
  const availableSeats = train.seats.filter(seat => seat.available > 0)
  if (availableSeats.length === 0) return 0
  return Math.min(...availableSeats.map(seat => seat.price))
}

// 格式化时间
const formatTime = (time: string) => {
  return time.substring(0, 5) // 只显示小时:分钟
}

// 计算跨天信息
const getDayInfo = (departureTime: string, arrivalTime: string, duration: string) => {
  const depHour = parseInt(departureTime.split(':')[0])
  const arrHour = parseInt(arrivalTime.split(':')[0])
  const durationHours = parseFloat(duration.replace('小时', '').replace('分', '').split('时')[0])
  
  if (durationHours > 12 || (depHour > arrHour && durationHours > 6)) {
    return '+1'
  }
  return ''
}

// 获取座位状态文本
const getSeatStatusText = (available: number) => {
  if (available === 0) return '无'
  if (available < 10) return '紧张'
  if (available < 50) return '有'
  return '充足'
}

// 获取座位状态颜色
const getSeatStatusColor = (available: number) => {
  if (available === 0) return '#d9d9d9'
  if (available < 10) return '#ff4d4f'
  if (available < 50) return '#faad14'
  return '#52c41a'
}

// 选择车次
const selectTrain = (train: TrainInfo) => {
  if (!hasAvailableSeats(train)) return
  emit('select', train)
}

// 重置搜索
const resetSearch = () => {
  emit('reset')
}
</script>

<template>
  <div class="train-list">
    <div class="container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-section">
        <ElSkeleton animated>
          <template #template>
            <div class="skeleton-list">
              <div v-for="i in 5" :key="i" class="skeleton-item">
                <el-skeleton-item variant="rect" style="height: 120px; border-radius: 8px;" />
              </div>
            </div>
          </template>
        </ElSkeleton>
      </div>
      
      <!-- 车次列表 -->
      <div v-else-if="trains.length > 0" class="trains-section">
        <!-- 列表头部 -->
        <div class="list-header">
          <div class="header-item train-info">车次信息</div>
          <div class="header-item time-info">时间信息</div>
          <div class="header-item duration">历时</div>
          <div class="header-item seats">座位信息</div>
          <div class="header-item price">票价</div>
          <div class="header-item action">操作</div>
        </div>
        
        <!-- 车次项目 -->
        <div class="train-items">
          <div 
            v-for="train in trains" 
            :key="train.id" 
            class="train-item"
            :class="{ 'no-seats': !hasAvailableSeats(train) }"
          >
            <!-- 车次信息 -->
            <div class="train-info">
              <div class="train-number">{{ train.number }}</div>
              <ElTag 
                :color="getTrainType(train.number).color" 
                size="small"
                class="train-type-tag"
              >
                {{ getTrainType(train.number).name }}
              </ElTag>
            </div>
            
            <!-- 时间信息 -->
            <div class="time-info">
              <div class="departure">
                <div class="time">{{ formatTime(train.departure.time) }}</div>
                <div class="station">{{ train.departure.station }}</div>
              </div>
              
              <div class="route-line">
                <ElIcon class="arrow-icon">
                  <ArrowRight />
                </ElIcon>
              </div>
              
              <div class="arrival">
                <div class="time">
                  {{ formatTime(train.arrival.time) }}
                  <span v-if="getDayInfo(train.departure.time, train.arrival.time, train.duration)" class="day-info">
                    {{ getDayInfo(train.departure.time, train.arrival.time, train.duration) }}
                  </span>
                </div>
                <div class="station">{{ train.arrival.station }}</div>
              </div>
            </div>
            
            <!-- 历时 -->
            <div class="duration">
              <ElIcon :size="14" color="#8c8c8c">
                <Clock />
              </ElIcon>
              <span>{{ train.duration }}</span>
            </div>
            
            <!-- 座位信息 -->
            <div class="seats">
              <div 
                v-for="seat in train.seats" 
                :key="seat.type" 
                class="seat-item"
                :class="{ 'no-seat': seat.available === 0 }"
              >
                <div class="seat-type">{{ seat.type }}</div>
                <div class="seat-status">
                  <span 
                    class="seat-count" 
                    :style="{ color: getSeatStatusColor(seat.available) }"
                  >
                    {{ getSeatStatusText(seat.available) }}
                  </span>
                  <div class="seat-price">¥{{ seat.price }}</div>
                </div>
              </div>
            </div>
            
            <!-- 最低价格 -->
            <div class="price">
              <div class="min-price">
                <span class="price-label">最低</span>
                <span class="price-value">¥{{ getMinPrice(train) }}</span>
              </div>
            </div>
            
            <!-- 操作按钮 -->
            <div class="action">
              <ElTooltip 
                v-if="!hasAvailableSeats(train)"
                content="该车次暂无余票"
                placement="top"
              >
                <ElButton 
                  disabled
                  class="book-btn"
                >
                  <ElIcon><Warning /></ElIcon>
                  无票
                </ElButton>
              </ElTooltip>
              
              <ElButton 
                v-else
                type="primary" 
                @click="selectTrain(train)"
                class="book-btn"
              >
                预订
              </ElButton>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 无结果提示 -->
      <div v-else-if="searched" class="no-result">
        <ElEmpty description="暂无符合条件的车次">
          <template #image>
            <div class="empty-image">
              <ElIcon :size="80" color="#d9d9d9">
                <Location />
              </ElIcon>
            </div>
          </template>
          <template #description>
            <div class="empty-description">
              <p>未找到符合条件的车次</p>
              <p class="empty-tips">请尝试调整搜索条件或选择其他日期</p>
            </div>
          </template>
          <ElButton type="primary" @click="resetSearch">
            重新搜索
          </ElButton>
        </ElEmpty>
      </div>
      
      <!-- 初始状态 -->
      <div v-else class="initial-state">
        <div class="initial-content">
          <ElIcon :size="100" color="#d9d9d9">
            <Location />
          </ElIcon>
          <h3>请输入搜索条件</h3>
          <p>选择出发地、目的地和出发日期开始搜索车次</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.train-list {
  flex: 1;
  background: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 加载状态 */
.loading-section {
  padding: 20px 0;
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skeleton-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
}

/* 车次列表 */
.trains-section {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.list-header {
  display: grid;
  grid-template-columns: 1.5fr 2fr 1fr 2fr 1fr 1fr;
  gap: 16px;
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  font-weight: 600;
  color: #262626;
  font-size: 14px;
}

.header-item {
  display: flex;
  align-items: center;
  justify-content: center;
}

.train-items {
  display: flex;
  flex-direction: column;
}

.train-item {
  display: grid;
  grid-template-columns: 1.5fr 2fr 1fr 2fr 1fr 1fr;
  gap: 16px;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  cursor: pointer;
}

.train-item:hover {
  background: #f8f9fa;
}

.train-item:last-child {
  border-bottom: none;
}

.train-item.no-seats {
  opacity: 0.6;
  cursor: not-allowed;
}

.train-item.no-seats:hover {
  background: white;
}

/* 车次信息 */
.train-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.train-number {
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
}

.train-type-tag {
  border: none;
  color: white;
  font-size: 11px;
  font-weight: 500;
}

/* 时间信息 */
.time-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.departure,
.arrival {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.time {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  position: relative;
}

.day-info {
  position: absolute;
  top: -2px;
  right: -16px;
  font-size: 10px;
  color: #ff4d4f;
  background: #fff2f0;
  padding: 1px 4px;
  border-radius: 2px;
}

.station {
  font-size: 13px;
  color: #8c8c8c;
  text-align: center;
}

.route-line {
  display: flex;
  align-items: center;
  flex: 1;
  justify-content: center;
}

.arrow-icon {
  color: #d9d9d9;
  font-size: 20px;
}

/* 历时 */
.duration {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 14px;
  color: #595959;
}

/* 座位信息 */
.seats {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.seat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 8px;
  border-radius: 4px;
  background: #fafafa;
  transition: all 0.3s ease;
}

.seat-item.no-seat {
  opacity: 0.5;
}

.seat-type {
  font-size: 12px;
  color: #595959;
  font-weight: 500;
}

.seat-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.seat-count {
  font-size: 12px;
  font-weight: 600;
}

.seat-price {
  font-size: 11px;
  color: #8c8c8c;
}

/* 价格 */
.price {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.min-price {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.price-label {
  font-size: 11px;
  color: #8c8c8c;
}

.price-value {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

/* 操作按钮 */
.action {
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-btn {
  padding: 8px 24px;
  border-radius: 6px;
  font-weight: 600;
}

/* 空状态 */
.no-result,
.initial-state {
  padding: 60px 20px;
  text-align: center;
}

.empty-image {
  margin-bottom: 16px;
}

.empty-description {
  margin-bottom: 24px;
}

.empty-description p {
  margin: 8px 0;
  color: #8c8c8c;
}

.empty-tips {
  font-size: 14px;
}

.initial-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  color: #8c8c8c;
}

.initial-content h3 {
  margin: 0;
  font-size: 18px;
  color: #595959;
}

.initial-content p {
  margin: 0;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .list-header,
  .train-item {
    grid-template-columns: 1fr 2fr 1fr 1.5fr 0.8fr 0.8fr;
    gap: 12px;
  }
  
  .container {
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .list-header {
    display: none;
  }
  
  .train-item {
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding: 16px;
  }
  
  .time-info {
    order: 1;
  }
  
  .train-info {
    order: 2;
    flex-direction: row;
    justify-content: space-between;
  }
  
  .duration {
    order: 3;
    flex-direction: row;
    justify-content: center;
  }
  
  .seats {
    order: 4;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .seat-item {
    flex: 1;
    min-width: calc(50% - 4px);
  }
  
  .price {
    order: 5;
  }
  
  .action {
    order: 6;
  }
  
  .book-btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 12px;
  }
  
  .train-item {
    padding: 12px;
  }
  
  .time-info {
    gap: 12px;
  }
  
  .time {
    font-size: 16px;
  }
  
  .train-number {
    font-size: 18px;
  }
  
  .seat-item {
    min-width: 100%;
  }
}
</style>