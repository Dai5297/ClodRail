<script setup lang="ts">
import { computed } from 'vue'
import { ElTag, ElSkeleton, ElIcon } from 'element-plus'
import { 
  SuccessFilled, 
  Clock, 
  WarningFilled, 
  CircleCloseFilled,
  RefreshLeft
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
  totalAmount: number
  createTime: string
  payTime?: string
  cancelTime?: string
}

interface Props {
  order: Order
  loading: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

// 状态配置
const statusConfig: { [key: string]: any } = {
  pending: {
    label: '待支付',
    color: '#faad14',
    bgColor: '#fff7e6',
    borderColor: '#ffd666',
    icon: Clock,
    description: '请尽快完成支付，订单将在30分钟后自动取消'
  },
  paid: {
    label: '已支付',
    color: '#52c41a',
    bgColor: '#f6ffed',
    borderColor: '#95de64',
    icon: SuccessFilled,
    description: '支付成功，电子客票已生成，请按时乘车'
  },
  completed: {
    label: '已完成',
    color: '#1890ff',
    bgColor: '#e6f4ff',
    borderColor: '#69c0ff',
    icon: SuccessFilled,
    description: '行程已完成，感谢您的使用'
  },
  cancelled: {
    label: '已取消',
    color: '#8c8c8c',
    bgColor: '#f5f5f5',
    borderColor: '#d9d9d9',
    icon: CircleCloseFilled,
    description: '订单已取消，如有疑问请联系客服'
  },
  refunded: {
    label: '已退款',
    color: '#722ed1',
    bgColor: '#f9f0ff',
    borderColor: '#d3adf7',
    icon: RefreshLeft,
    description: '退款已处理，款项将在3-5个工作日内到账'
  },
  refund_pending: {
    label: '退票中',
    color: '#fa8c16',
    bgColor: '#fff2e8',
    borderColor: '#ffbb96',
    icon: RefreshLeft,
    description: '退票申请已提交，我们将在1-3个工作日内处理'
  }
}

// 获取状态配置
const currentStatus = computed(() => {
  return statusConfig[props.order.status] || {
    label: props.order.status,
    color: '#8c8c8c',
    bgColor: '#f5f5f5',
    borderColor: '#d9d9d9',
    icon: WarningFilled,
    description: '未知状态'
  }
})

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

// 获取关键时间信息
const getTimeInfo = computed(() => {
  const order = props.order
  
  switch (order.status) {
    case 'pending':
      return {
        label: '创建时间',
        time: formatTime(order.createTime)
      }
    case 'paid':
    case 'completed':
      return {
        label: '支付时间',
        time: formatTime(order.payTime || '')
      }
    case 'cancelled':
      return {
        label: '取消时间',
        time: formatTime(order.cancelTime || '')
      }
    default:
      return {
        label: '创建时间',
        time: formatTime(order.createTime)
      }
  }
})
</script>

<template>
  <div class="order-header">
    <!-- 加载状态 -->
    <ElSkeleton v-if="loading" animated>
      <template #template>
        <div class="header-skeleton">
          <el-skeleton-item variant="rect" style="width: 100%; height: 120px; border-radius: 12px;" />
        </div>
      </template>
    </ElSkeleton>
    
    <!-- 订单状态卡片 -->
    <div 
      v-else
      class="status-card"
      :style="{
        backgroundColor: currentStatus.bgColor,
        borderColor: currentStatus.borderColor
      }"
    >
      <div class="status-main">
        <div class="status-icon-wrapper">
          <ElIcon 
            :size="32" 
            :color="currentStatus.color"
            class="status-icon"
          >
            <component :is="currentStatus.icon" />
          </ElIcon>
        </div>
        
        <div class="status-content">
          <div class="status-header">
            <ElTag
              :color="currentStatus.bgColor"
              :style="{ 
                color: currentStatus.color,
                borderColor: currentStatus.borderColor
              }"
              size="large"
              class="status-tag"
            >
              {{ currentStatus.label }}
            </ElTag>
            
            <div class="order-amount">
              <span class="amount-label">订单金额</span>
              <span class="amount-value">¥{{ order.totalAmount.toFixed(2) }}</span>
            </div>
          </div>
          
          <div class="status-description">
            {{ currentStatus.description }}
          </div>
          
          <div class="time-info">
            <span class="time-label">{{ getTimeInfo.label }}：</span>
            <span class="time-value">{{ getTimeInfo.time }}</span>
          </div>
        </div>
      </div>
      
      <!-- 进度指示器（仅对特定状态显示） -->
      <div 
        v-if="order.status === 'paid' || order.status === 'refund_pending'"
        class="progress-indicator"
      >
        <div class="progress-bar">
          <div 
            class="progress-fill"
            :style="{
              backgroundColor: currentStatus.color,
              width: order.status === 'paid' ? '75%' : '50%'
            }"
          ></div>
        </div>
        <div class="progress-text">
          <span v-if="order.status === 'paid'">等待出行</span>
          <span v-else-if="order.status === 'refund_pending'">处理中</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-header {
  margin-bottom: 0;
}

.header-skeleton {
  padding: 0;
}

.status-card {
  background: white;
  border-radius: 12px;
  border: 2px solid;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.status-main {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 16px;
}

.status-icon-wrapper {
  flex-shrink: 0;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.status-tag {
  border: 1px solid;
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 16px;
}

.order-amount {
  text-align: right;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.amount-label {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
}

.amount-value {
  font-size: 24px;
  font-weight: 700;
  color: #ff4d4f;
  font-family: 'Arial', sans-serif;
}

.status-description {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  background: rgba(255, 255, 255, 0.6);
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.time-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}

.time-label {
  color: #8c8c8c;
  font-weight: 500;
}

.time-value {
  color: #1a1a1a;
  font-weight: 600;
  font-family: 'Courier New', monospace;
}

.progress-indicator {
  border-top: 1px solid rgba(255, 255, 255, 0.8);
  padding-top: 16px;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.3),
    transparent
  );
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.progress-text {
  text-align: center;
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .status-card {
    padding: 20px;
  }
  
  .status-main {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 16px;
  }
  
  .status-header {
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }
  
  .order-amount {
    text-align: center;
  }
  
  .amount-value {
    font-size: 20px;
  }
}
</style>