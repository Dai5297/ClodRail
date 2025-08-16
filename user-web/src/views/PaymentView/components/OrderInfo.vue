<script setup lang="ts">
import { computed } from 'vue'

// 定义Props
interface Passenger {
  name: string
  idCard: string
  ticketType: string
  seatType: string
  seatNumber: string
  price: number
}

interface Order {
  trainNumber: string
  departure: string
  arrival: string
  departureTime: string
  arrivalTime: string
  date: string
  duration: string
  passengers: Passenger[]
  insurance: number
  serviceFee: number
  totalAmount: number
}

interface Props {
  order: Order
}

const props = defineProps<Props>()

// 格式化身份证号（隐藏中间部分）
const formatIdCard = (idCard: string) => {
  if (idCard.length < 8) return idCard
  return idCard.slice(0, 4) + '****' + idCard.slice(-4)
}

// 计算车票总价
const ticketTotal = computed(() => {
  return props.order.passengers.reduce((sum, passenger) => sum + passenger.price, 0)
})
</script>

<template>
  <div class="order-info">
    <!-- 车次信息 -->
    <div class="info-section">
      <h3 class="section-title">
        <span class="title-icon">🚄</span>
        车次信息
      </h3>
      <div class="train-info">
        <div class="train-header">
          <div class="train-number">{{ order.trainNumber }}</div>
          <div class="train-date">{{ order.date }}</div>
        </div>
        
        <div class="route-info">
          <div class="station departure">
            <div class="station-name">{{ order.departure }}</div>
            <div class="station-time">{{ order.departureTime }}</div>
          </div>
          
          <div class="route-middle">
            <div class="route-line"></div>
            <div class="duration">{{ order.duration }}</div>
          </div>
          
          <div class="station arrival">
            <div class="station-name">{{ order.arrival }}</div>
            <div class="station-time">{{ order.arrivalTime }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 乘客信息 -->
    <div class="info-section">
      <h3 class="section-title">
        <span class="title-icon">👥</span>
        乘客信息
      </h3>
      <div class="passengers-list">
        <div 
          v-for="(passenger, index) in order.passengers" 
          :key="index"
          class="passenger-item"
        >
          <div class="passenger-header">
            <span class="passenger-name">{{ passenger.name }}</span>
            <span class="passenger-type">{{ passenger.ticketType }}</span>
          </div>
          
          <div class="passenger-details">
            <div class="detail-item">
              <span class="detail-label">身份证号</span>
              <span class="detail-value">{{ formatIdCard(passenger.idCard) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">座位类型</span>
              <span class="detail-value">{{ passenger.seatType }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">座位号</span>
              <span class="detail-value">{{ passenger.seatNumber }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">票价</span>
              <span class="detail-value price">¥{{ passenger.price }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 费用明细 -->
    <div class="info-section">
      <h3 class="section-title">
        <span class="title-icon">💰</span>
        费用明细
      </h3>
      <div class="cost-breakdown">
        <div class="cost-item">
          <span class="cost-label">车票费用</span>
          <span class="cost-value">¥{{ ticketTotal }}</span>
        </div>
        <div class="cost-item">
          <span class="cost-label">保险费用</span>
          <span class="cost-value">¥{{ order.insurance }}</span>
        </div>
        <div class="cost-item">
          <span class="cost-label">服务费</span>
          <span class="cost-value">¥{{ order.serviceFee }}</span>
        </div>
        <div class="cost-divider"></div>
        <div class="cost-item total">
          <span class="cost-label">总计</span>
          <span class="cost-value">¥{{ order.totalAmount }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-info {
  padding: 0;
}

.info-section {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.info-section:last-child {
  border-bottom: none;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.title-icon {
  font-size: 18px;
}

/* 车次信息样式 */
.train-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.train-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.train-number {
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
}

.train-date {
  font-size: 14px;
  color: #8c8c8c;
}

.route-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.station {
  flex: 1;
  text-align: center;
}

.station.departure {
  text-align: left;
}

.station.arrival {
  text-align: right;
}

.station-name {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 4px;
}

.station-time {
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
}

.route-middle {
  flex: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.route-line {
  width: 100%;
  height: 2px;
  background: linear-gradient(to right, #1890ff, #52c41a);
  border-radius: 1px;
  position: relative;
}

.route-line::before,
.route-line::after {
  content: '';
  position: absolute;
  top: -3px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #1890ff;
}

.route-line::before {
  left: -4px;
}

.route-line::after {
  right: -4px;
  background: #52c41a;
}

.duration {
  font-size: 12px;
  color: #8c8c8c;
  background: white;
  padding: 2px 8px;
  border-radius: 4px;
  border: 1px solid #f0f0f0;
}

/* 乘客信息样式 */
.passengers-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.passenger-item {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #f0f0f0;
}

.passenger-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.passenger-name {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.passenger-type {
  font-size: 12px;
  background: #e6f7ff;
  color: #1890ff;
  padding: 2px 8px;
  border-radius: 4px;
}

.passenger-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-label {
  font-size: 13px;
  color: #8c8c8c;
}

.detail-value {
  font-size: 13px;
  color: #262626;
  font-weight: 500;
}

.detail-value.price {
  color: #f5222d;
  font-weight: 600;
}

/* 费用明细样式 */
.cost-breakdown {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
}

.cost-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.cost-label {
  font-size: 14px;
  color: #595959;
}

.cost-value {
  font-size: 14px;
  color: #262626;
  font-weight: 500;
}

.cost-divider {
  height: 1px;
  background: #e8e8e8;
  margin: 8px 0;
}

.cost-item.total {
  padding-top: 12px;
}

.cost-item.total .cost-label {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.cost-item.total .cost-value {
  font-size: 18px;
  font-weight: bold;
  color: #f5222d;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .info-section {
    padding: 16px;
  }
  
  .train-info {
    padding: 16px;
  }
  
  .route-info {
    flex-direction: column;
    gap: 12px;
  }
  
  .station {
    text-align: center;
  }
  
  .route-middle {
    order: -1;
    flex-direction: row;
    width: 100%;
  }
  
  .route-line {
    height: 1px;
  }
  
  .passenger-details {
    grid-template-columns: 1fr;
  }
  
  .detail-item {
    padding: 4px 0;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .detail-item:last-child {
    border-bottom: none;
  }
}
</style>