<script setup lang="ts">
import { computed, watch } from 'vue'
import { 
  ElDialog, 
  ElButton, 
  ElTag, 
  ElDivider, 
  ElIcon,
  ElDescriptions,
  ElDescriptionsItem
} from 'element-plus'
import { 
  Close,
  CreditCard,
  RefreshLeft,
  Clock,
  Location,
  User,
  Tickets
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
  refundReason?: string
  refundAmount?: number
}

interface Props {
  visible: boolean
  order: Order | null
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  order: null
})

// 定义事件
const emit = defineEmits<{
  'update:visible': [visible: boolean]
  payOrder: [order: Order]
  cancelOrder: [order: Order]
  refundOrder: [order: Order]
}>()

// 状态配置
const statusConfig: Record<string, { label: string; color: string; bgColor: string; icon: string }> = {
  pending: { label: '待支付', color: '#faad14', bgColor: '#fff7e6', icon: '⏰' },
  paid: { label: '已支付', color: '#52c41a', bgColor: '#f6ffed', icon: '✅' },
  completed: { label: '已完成', color: '#1890ff', bgColor: '#e6f4ff', icon: '🎯' },
  cancelled: { label: '已取消', color: '#8c8c8c', bgColor: '#f5f5f5', icon: '❌' },
  refunded: { label: '已退款', color: '#722ed1', bgColor: '#f9f0ff', icon: '💰' },
  refund_pending: { label: '退票中', color: '#fa8c16', bgColor: '#fff2e8', icon: '🔄' }
}

// 获取状态配置
const getStatusConfig = (status: string) => {
  return statusConfig[status] || { label: status, color: '#8c8c8c', bgColor: '#f5f5f5', icon: '❓' }
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
    minute: '2-digit',
    second: '2-digit'
  })
}

// 格式化日期
const formatDate = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 格式化时间（仅显示时间）
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
const getAvailableActions = computed(() => {
  if (!props.order) return []
  
  const actions = []
  
  switch (props.order.status) {
    case 'pending':
      actions.push({ key: 'pay', label: '立即支付', icon: CreditCard, type: 'primary' as const })
      actions.push({ key: 'cancel', label: '取消订单', icon: Close, type: 'danger' as const })
      break
    case 'paid':
      actions.push({ key: 'refund', label: '申请退票', icon: RefreshLeft, type: 'warning' as const })
      break
  }
  
  return actions
})

// 关闭模态框
const handleClose = () => {
  emit('update:visible', false)
}

// 处理操作点击
const handleAction = (action: string) => {
  if (!props.order) return
  
  switch (action) {
    case 'pay':
      emit('payOrder', props.order)
      handleClose()
      break
    case 'cancel':
      emit('cancelOrder', props.order)
      handleClose()
      break
    case 'refund':
      emit('refundOrder', props.order)
      handleClose()
      break
  }
}

// 监听visible变化
watch(() => props.visible, (newVal) => {
  if (!newVal) {
    // 模态框关闭时的清理工作
  }
})
</script>

<template>
  <ElDialog
    :model-value="visible"
    title="订单详情"
    width="800px"
    :before-close="handleClose"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div v-if="order" class="order-detail">
      <!-- 订单状态 -->
      <div class="status-section">
        <div class="status-card">
          <div class="status-icon">{{ getStatusConfig(order.status).icon }}</div>
          <div class="status-info">
            <div class="status-title">订单状态</div>
            <ElTag
              :color="getStatusConfig(order.status).bgColor"
              :style="{ color: getStatusConfig(order.status).color }"
              size="large"
              class="status-tag"
            >
              {{ getStatusConfig(order.status).label }}
            </ElTag>
          </div>
        </div>
      </div>
      
      <ElDivider />
      
      <!-- 行程信息 -->
      <div class="trip-section">
        <h3 class="section-title">
          <ElIcon><Location /></ElIcon>
          行程信息
        </h3>
        
        <div class="trip-card">
          <div class="trip-header">
            <div class="train-info">
              <span class="train-number">{{ order.trainNumber }}</span>
              <span class="travel-date">{{ formatDate(order.departureTime) }}</span>
            </div>
            <div class="duration">{{ getTravelDuration(order.departureTime, order.arrivalTime) }}</div>
          </div>
          
          <div class="trip-route">
            <div class="station-info">
              <div class="station-name">{{ order.departure }}</div>
              <div class="station-time">{{ formatTimeOnly(order.departureTime) }}</div>
            </div>
            
            <div class="route-line">
              <div class="line"></div>
              <ElIcon class="arrow"><Location /></ElIcon>
            </div>
            
            <div class="station-info">
              <div class="station-name">{{ order.arrival }}</div>
              <div class="station-time">{{ formatTimeOnly(order.arrivalTime) }}</div>
            </div>
          </div>
          
          <div class="seat-info">
            <ElIcon><Tickets /></ElIcon>
            <span>{{ order.seatType }} {{ order.seatNumber }}</span>
          </div>
        </div>
      </div>
      
      <ElDivider />
      
      <!-- 乘客信息 -->
      <div class="passenger-section">
        <h3 class="section-title">
          <ElIcon><User /></ElIcon>
          乘客信息
        </h3>
        
        <div class="passenger-list">
          <div
            v-for="(passenger, index) in order.passengers"
            :key="index"
            class="passenger-card"
          >
            <div class="passenger-info">
              <div class="passenger-name">{{ passenger.name }}</div>
              <div class="passenger-id">{{ passenger.idCard }}</div>
            </div>
          </div>
        </div>
      </div>
      
      <ElDivider />
      
      <!-- 订单信息 -->
      <div class="order-info-section">
        <h3 class="section-title">
          <ElIcon><Clock /></ElIcon>
          订单信息
        </h3>
        
        <ElDescriptions :column="2" border>
          <ElDescriptionsItem label="订单号">{{ order.orderNumber }}</ElDescriptionsItem>
          <ElDescriptionsItem label="订单金额">
            <span class="price-amount">¥{{ order.totalAmount.toFixed(2) }}</span>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="创建时间">{{ formatTime(order.createTime) }}</ElDescriptionsItem>
          <ElDescriptionsItem v-if="order.payTime" label="支付时间">{{ formatTime(order.payTime) }}</ElDescriptionsItem>
          <ElDescriptionsItem v-if="order.cancelTime" label="取消时间">{{ formatTime(order.cancelTime) }}</ElDescriptionsItem>
          <ElDescriptionsItem v-if="order.refundReason" label="退票原因">{{ order.refundReason }}</ElDescriptionsItem>
          <ElDescriptionsItem v-if="order.refundAmount" label="退款金额">
            <span class="refund-amount">¥{{ order.refundAmount.toFixed(2) }}</span>
          </ElDescriptionsItem>
        </ElDescriptions>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="handleClose">关闭</ElButton>
        <ElButton
          v-for="action in getAvailableActions"
          :key="action.key"
          :type="action.type"
          :icon="action.icon"
          @click="handleAction(action.key)"
        >
          {{ action.label }}
        </ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<style scoped>
.order-detail {
  padding: 0;
}

.status-section {
  margin-bottom: 20px;
}

.status-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.status-icon {
  font-size: 32px;
}

.status-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.status-title {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.status-tag {
  border: none;
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 16px 0;
}

.trip-section {
  margin-bottom: 20px;
}

.trip-card {
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #f0f0f0;
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.train-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.train-number {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  background: #e6f4ff;
  padding: 4px 12px;
  border-radius: 16px;
}

.travel-date {
  font-size: 14px;
  color: #666;
}

.duration {
  font-size: 14px;
  color: #8c8c8c;
  background: white;
  padding: 4px 8px;
  border-radius: 12px;
}

.trip-route {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
}

.station-info {
  flex: 1;
  text-align: center;
}

.station-name {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.station-time {
  font-size: 16px;
  color: #666;
}

.route-line {
  flex: 2;
  display: flex;
  align-items: center;
  gap: 8px;
}

.line {
  flex: 1;
  height: 2px;
  background: linear-gradient(to right, #1890ff, #52c41a);
  border-radius: 1px;
}

.arrow {
  color: #52c41a;
  font-size: 18px;
}

.seat-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #666;
  background: white;
  padding: 8px 12px;
  border-radius: 8px;
  width: fit-content;
}

.passenger-section {
  margin-bottom: 20px;
}

.passenger-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.passenger-card {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #f0f0f0;
}

.passenger-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.passenger-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.passenger-id {
  font-size: 14px;
  color: #8c8c8c;
  font-family: 'Courier New', monospace;
}

.order-info-section {
  margin-bottom: 20px;
}

.price-amount {
  font-size: 16px;
  font-weight: 600;
  color: #ff4d4f;
}

.refund-amount {
  font-size: 16px;
  font-weight: 600;
  color: #52c41a;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
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
  
  .trip-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .passenger-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>