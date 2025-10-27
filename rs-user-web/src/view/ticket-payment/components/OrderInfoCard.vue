<template>
  <div class="order-info-card">
    <h3 class="card-title">
      <el-icon><Document /></el-icon>
      订单信息
    </h3>
    
    <div class="order-details">
      <div class="train-info">
        <div class="train-header">
          <span class="train-number">{{ orderInfo.trainCode }}</span>
          <span class="train-date">{{ formatDate(orderInfo.date) }}</span>
        </div>
        <div class="route-info">
          <div class="route-item">
            <div class="time-display">
              <span class="time">{{ formatTime(orderInfo.startTime) }}</span>
              <span class="date">{{ formatDate(orderInfo.startTime) }}</span>
            </div>
            <span class="station">{{ orderInfo.originStation?.name || '出发站' }}</span>
          </div>
          <div class="route-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
          <div class="route-item">
            <div class="time-display">
              <span class="time">{{ formatTime(orderInfo.endTime) }}</span>
              <span class="date">{{ formatDate(orderInfo.endTime) }}</span>
            </div>
            <span class="station">{{ orderInfo.destinationStation?.name || '到达站' }}</span>
          </div>
        </div>
      </div>

      <div class="passenger-info">
        <h4 class="section-title">乘客信息</h4>
        <div class="passenger-list">
          <div 
            v-for="(passenger, index) in orderInfo.passengers" 
            :key="index"
            class="passenger-item"
          >
            <div class="passenger-details">
              <div class="passenger-main">
                <span class="passenger-name">{{ passenger.name }}</span>
                <span class="passenger-type-badge">{{ getPassengerTypeText(passenger.passengerType) }}</span>
              </div>
              <span class="seat-info">{{ orderInfo.seatType?.name || orderInfo.seatTypeName || '二等座' }} {{ passenger.seatPosition }}座</span>
            </div>
            <div class="passenger-meta">
              <div class="price-detail">
                <span class="base-price" v-if="passenger.discount > 0">
                  原价：￥{{ passenger.basePrice?.toFixed(2) || '0.00' }}
                </span>
                <span class="discount" v-if="passenger.discount > 0">
                  优惠：-￥{{ passenger.discount?.toFixed(2) || '0.00' }}
                </span>
                <span class="actual-price">
                  实付：￥{{ passenger.actualPrice?.toFixed(2) || orderInfo.ticketPrice?.toFixed(2) || '0.00' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="order-summary">
        <div class="summary-item">
          <span class="label">订单号：</span>
          <span class="value">{{ orderInfo.orderNumber }}</span>
        </div>
        <div class="summary-item">
          <span class="label">订单状态：</span>
          <span class="value">{{ getOrderStatusText(orderInfo.status) }}</span>
        </div>
        <div class="summary-item">
          <span class="label">座位类型：</span>
          <span class="value">{{ orderInfo.seatTypeName }}</span>
        </div>
        <div class="summary-item">
          <span class="label">乘客数量：</span>
          <span class="value">{{ orderInfo.passengers.length }}人</span>
        </div>
        <div class="summary-item">
          <span class="label">创建时间：</span>
          <span class="value">{{ formatDateTime(orderInfo.createTime) }}</span>
        </div>
        <div class="summary-item" v-if="orderInfo.baseAmount && orderInfo.baseAmount !== orderInfo.totalAmount">
          <span class="label">原价金额：</span>
          <span class="value">￥{{ orderInfo.baseAmount?.toFixed(2) }}</span>
        </div>
        <div class="summary-item" v-if="orderInfo.discountAmount > 0">
          <span class="label">优惠金额：</span>
          <span class="value discount-value">-￥{{ orderInfo.discountAmount?.toFixed(2) }}</span>
        </div>
        <div class="summary-item total">
          <span class="label">应付金额：</span>
          <span class="value">￥{{ orderInfo.totalAmount?.toFixed(2) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Document, ArrowRight } from '@element-plus/icons-vue'

const props = defineProps({
  orderInfo: {
    type: Object,
    required: true
  }
})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  const weekday = weekdays[date.getDay()]
  return `${year}-${month}-${day} 周${weekday}`
}

// 格式化时间显示
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款'
  }
  return statusMap[status] || '未知状态'
}

// 获取乘客类型文本
const getPassengerTypeText = (type) => {
  const typeMap = {
    1: '成人',
    2: '儿童',
    3: '学生',
    4: '老人'
  }
  return typeMap[type] || '成人'
}
</script>

<style scoped>
.order-info-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  color: #333;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.train-info {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.train-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.train-number {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.train-date {
  color: #666;
  font-size: 14px;
}

.route-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.route-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.time-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 4px;
}

.time {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 1;
}

.date {
  font-size: 10px;
  color: #999;
  line-height: 1;
  margin-top: 2px;
}

.station {
  font-size: 14px;
  color: #666;
}

.route-arrow {
  color: #1890ff;
}

.section-title {
  font-size: 16px;
  color: #333;
  margin: 0 0 12px 0;
}

.passenger-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.passenger-item {
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.passenger-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 8px;
}

.passenger-main {
  display: flex;
  align-items: center;
  gap: 8px;
}

.passenger-name {
  font-weight: 600;
  color: #333;
  font-size: 15px;
}

.passenger-type-badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
}

.seat-info {
  color: #1890ff;
  font-size: 14px;
}

.passenger-meta {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.price-detail {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  font-size: 12px;
}

.base-price {
  color: #999;
  text-decoration: line-through;
}

.discount {
  color: #52c41a;
  font-weight: 500;
}

.actual-price {
  color: #ff6b35;
  font-weight: 600;
  font-size: 14px;
}

.order-summary {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.summary-item.total {
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  margin-top: 8px;
  font-size: 16px;
  font-weight: 600;
}

.label {
  color: #666;
}

.value {
  color: #333;
}

.summary-item.total .value {
  color: #ff6b35;
  font-size: 20px;
}

.discount-value {
  color: #52c41a;
  font-weight: 500;
}

@media (max-width: 768px) {
  .route-info {
    flex-direction: column;
    gap: 12px;
  }
  
  .passenger-details,
  .passenger-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>

