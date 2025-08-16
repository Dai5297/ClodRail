<script setup lang="ts">
import { computed } from 'vue'
import { ElButton, ElEmpty, ElSkeleton, ElTag, ElIcon } from 'element-plus'
import { 
  View, 
  CreditCard, 
  Close, 
  RefreshLeft,
  Clock,
  Location
} from '@element-plus/icons-vue'

// 定义属性
interface Order {
  id: string
  orderNumber: string
  status: string
  trainNumber: string
  departure: string
  arrival: string
  departureTime: string
  arrivalTime: string
  seatType: string
  seatNumber: string
  passengers: Array<{ name: string; idCard: string }>
  totalAmount: number
  createTime: string
  payTime?: string
  cancelTime?: string
}

interface Props {
  orders: Order[]
  loading: boolean
}

const props = withDefaults(defineProps<Props>(), {
  orders: () => [],
  loading: false
})

// 定义事件
const emit = defineEmits<{
  viewDetail: [order: Order]
  payOrder: [order: Order]
  cancelOrder: [order: Order]
  refundOrder: [order: Order]
}>()

// 状态配置
const statusConfig: Record<string, { label: string; color: string; bgColor: string }> = {
  pending: { label: '待支付', color: '#faad14', bgColor: '#fff7e6' },
  paid: { label: '已支付', color: '#52c41a', bgColor: '#f6ffed' },
  completed: { label: '已完成', color: '#1890ff', bgColor: '#e6f4ff' },
  cancelled: { label: '已取消', color: '#8c8c8c', bgColor: '#f5f5f5' },
  refunded: { label: '已退款', color: '#722ed1', bgColor: '#f9f0ff' },
  refund_pending: { label: '退票中', color: '#fa8c16', bgColor: '#fff2e8' }
}

// 获取状态配置
const getStatusConfig = (status: string) => {
  return statusConfig[status] || { label: status, color: '#8c8c8c', bgColor: '#f5f5f5' }
}

// 格式化时间
const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化日期时间（仅显示时间）
const formatTimeOnly = (timeStr: string) => {
  if (!timeStr) return ''
  return timeStr.split(' ')[1] || timeStr
}

// 计算行程时长
const getTravelDuration = (departureTime: string, arrivalTime: string) => {
  if (!departureTime || !arrivalTime) return ''
  
  const departure = new Date(departureTime)
  const arrival = new Date(arrivalTime)
  const diff = arrival.getTime() - departure.getTime()
  
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  
  return `${hours}小时${minutes}分钟`
}

// 获取可用操作
const getAvailableActions = (order: Order) => {
  const actions = []
  
  actions.push({ key: 'detail', label: '查看详情', icon: View, type: 'default' as const })
  
  switch (order.status) {
    case 'pending':
      actions.push({ key: 'pay', label: '立即支付', icon: CreditCard, type: 'primary' as const })
      actions.push({ key: 'cancel', label: '取消订单', icon: Close, type: 'danger' as const })
      break
    case 'paid':
      actions.push({ key: 'refund', label: '申请退票', icon: RefreshLeft, type: 'warning' as const })
      break
  }
  
  return actions
}

// 处理操作点击
const handleAction = (action: string, order: Order) => {
  switch (action) {
    case 'detail':
      emit('viewDetail', order)
      break
    case 'pay':
      emit('payOrder', order)
      break
    case 'cancel':
      emit('cancelOrder', order)
      break
    case 'refund':
      emit('refundOrder', order)
      break
  }
}
</script>

<template>
  <div class="order-list">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <ElSkeleton v-for="i in 3" :key="i" animated>
        <template #template>
          <div class="order-skeleton">
            <el-skeleton-item variant="rect" style="width: 100%; height: 200px; border-radius: 12px;" />
          </div>
        </template>
      </ElSkeleton>
    </div>
    
    <!-- 订单列表 -->
    <div v-else-if="orders.length > 0" class="orders-container">
      <div
        v-for="order in orders"
        :key="order.id"
        class="order-card"
      >
        <!-- 订单头部 -->
        <div class="order-header">
          <div class="order-info">
            <span class="order-number">订单号：{{ order.orderNumber }}</span>
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
          </div>
          <ElTag
            :color="getStatusConfig(order.status).bgColor"
            :style="{ color: getStatusConfig(order.status).color }"
            class="status-tag"
          >
            {{ getStatusConfig(order.status).label }}
          </ElTag>
        </div>
        
        <!-- 行程信息 -->
        <div class="trip-info">
          <div class="trip-route">
            <div class="station-info">
              <div class="station-name">{{ order.departure }}</div>
              <div class="station-time">{{ formatTimeOnly(order.departureTime) }}</div>
            </div>
            
            <div class="route-line">
              <div class="train-number">{{ order.trainNumber }}</div>
              <div class="route-arrow">
                <div class="arrow-line"></div>
                <ElIcon class="arrow-icon"><Location /></ElIcon>
              </div>
              <div class="duration">{{ getTravelDuration(order.departureTime, order.arrivalTime) }}</div>
            </div>
            
            <div class="station-info">
              <div class="station-name">{{ order.arrival }}</div>
              <div class="station-time">{{ formatTimeOnly(order.arrivalTime) }}</div>
            </div>
          </div>
        </div>
        
        <!-- 座位和乘客信息 -->
        <div class="seat-passenger-info">
          <div class="seat-info">
            <ElIcon><Clock /></ElIcon>
            <span>{{ order.seatType }} {{ order.seatNumber }}</span>
          </div>
          <div class="passenger-info">
            <span class="passenger-label">乘客：</span>
            <span class="passenger-names">
              {{ order.passengers.map(p => p.name).join('、') }}
            </span>
          </div>
        </div>
        
        <!-- 订单底部 -->
        <div class="order-footer">
          <div class="price-info">
            <span class="price-label">总金额：</span>
            <span class="price-amount">¥{{ order.totalAmount.toFixed(2) }}</span>
          </div>
          
          <div class="action-buttons">
            <ElButton
              v-for="action in getAvailableActions(order)"
              :key="action.key"
              :type="action.type"
              :icon="action.icon"
              size="small"
              @click="handleAction(action.key, order)"
            >
              {{ action.label }}
            </ElButton>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else class="empty-container">
      <ElEmpty
        description="暂无订单数据"
        :image-size="120"
      >
        <template #image>
          <div class="empty-icon">📋</div>
        </template>
        <template #description>
          <p class="empty-text">您还没有任何订单</p>
          <p class="empty-subtext">快去预订您的第一张车票吧！</p>
        </template>
      </ElEmpty>
    </div>
  </div>
</template>

<style scoped>
.order-list {
  min-height: 400px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-skeleton {
  padding: 0;
}

.orders-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.order-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-number {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
}

.order-time {
  font-size: 12px;
  color: #8c8c8c;
}

.status-tag {
  border: none;
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 16px;
}

.trip-info {
  margin-bottom: 16px;
}

.trip-route {
  display: flex;
  align-items: center;
  gap: 16px;
}

.station-info {
  flex: 1;
  text-align: center;
}

.station-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.station-time {
  font-size: 14px;
  color: #666;
}

.route-line {
  flex: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.train-number {
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
  background: #e6f4ff;
  padding: 2px 8px;
  border-radius: 12px;
}

.route-arrow {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  position: relative;
}

.arrow-line {
  flex: 1;
  height: 2px;
  background: linear-gradient(to right, #1890ff, #52c41a);
  border-radius: 1px;
}

.arrow-icon {
  color: #52c41a;
  font-size: 16px;
}

.duration {
  font-size: 12px;
  color: #8c8c8c;
}

.seat-passenger-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.seat-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.passenger-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.passenger-label {
  color: #8c8c8c;
}

.passenger-names {
  color: #1a1a1a;
  font-weight: 500;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-info {
  display: flex;
  align-items: center;
  gap: 4px;
}

.price-label {
  font-size: 14px;
  color: #8c8c8c;
}

.price-amount {
  font-size: 18px;
  font-weight: 600;
  color: #ff4d4f;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #8c8c8c;
  margin: 0 0 8px 0;
}

.empty-subtext {
  font-size: 14px;
  color: #bfbfbf;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-card {
    padding: 16px;
  }
  
  .trip-route {
    flex-direction: column;
    gap: 12px;
  }
  
  .route-line {
    order: -1;
    flex-direction: row;
    width: 100%;
  }
  
  .station-info {
    flex: none;
  }
  
  .seat-passenger-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .order-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .action-buttons {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>