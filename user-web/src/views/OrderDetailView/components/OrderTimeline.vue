<script setup lang="ts">
import { computed } from 'vue'
import { ElCard, ElSkeleton, ElIcon, ElTimeline, ElTimelineItem } from 'element-plus'
import { 
  DocumentAdd,
  CreditCard,
  SuccessFilled,
  CircleCheckFilled,
  CircleCloseFilled,
  RefreshLeft,
  Clock,
  CaretRight
} from '@element-plus/icons-vue'

// 定义时间线项目接口
interface TimelineItem {
  id: string
  title: string
  description: string
  time: string
  status: 'success' | 'primary' | 'warning' | 'danger' | 'info'
  icon?: any
  type: string
}

// 定义订单接口
interface Order {
  id: string
  status: string
  createTime: string
  payTime?: string
  departureTime?: string
  arrivalTime?: string
  cancelTime?: string
  refundTime?: string
  refundApplyTime?: string
}

// 定义属性
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
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 生成时间线数据
const timelineItems = computed(() => {
  const items: TimelineItem[] = []
  const order = props.order
  
  // 订单创建
  items.push({
    id: 'create',
    title: '订单创建',
    description: '订单已成功创建，请尽快完成支付',
    time: formatTime(order.createTime),
    status: 'success',
    icon: DocumentAdd,
    type: 'create'
  })
  
  // 根据订单状态添加相应的时间线项目
  switch (order.status) {
    case 'pending':
      items.push({
        id: 'pending_payment',
        title: '等待支付',
        description: '订单等待支付中，请在30分钟内完成支付',
        time: '',
        status: 'warning',
        icon: Clock,
        type: 'pending'
      })
      break
      
    case 'paid':
      if (order.payTime) {
        items.push({
          id: 'payment',
          title: '支付成功',
          description: '订单支付成功，电子客票已生成',
          time: formatTime(order.payTime),
          status: 'success',
          icon: CreditCard,
          type: 'payment'
        })
      }
      
      // 添加等待出行项目
      if (order.departureTime) {
        const now = new Date()
        const departureDate = new Date(order.departureTime)
        
        if (now < departureDate) {
          items.push({
            id: 'waiting_departure',
            title: '等待出行',
            description: `请按时到达车站，检票时间为发车前30分钟`,
            time: formatTime(order.departureTime),
            status: 'primary',
            icon: CaretRight,
            type: 'waiting'
          })
        } else {
          items.push({
            id: 'departed',
            title: '已发车',
            description: '列车已发车，祝您旅途愉快',
            time: formatTime(order.departureTime),
            status: 'success',
            icon: CaretRight,
            type: 'departed'
          })
        }
      }
      break
      
    case 'completed':
      if (order.payTime) {
        items.push({
          id: 'payment',
          title: '支付成功',
          description: '订单支付成功，电子客票已生成',
          time: formatTime(order.payTime),
          status: 'success',
          icon: CreditCard,
          type: 'payment'
        })
      }
      
      if (order.departureTime) {
        items.push({
          id: 'departed',
          title: '已发车',
          description: '列车已发车',
          time: formatTime(order.departureTime),
          status: 'success',
          icon: CaretRight,
          type: 'departed'
        })
      }
      
      if (order.arrivalTime) {
        items.push({
          id: 'completed',
          title: '行程完成',
          description: '列车已到达目的地，行程圆满完成',
          time: formatTime(order.arrivalTime),
          status: 'success',
          icon: CircleCheckFilled,
          type: 'completed'
        })
      }
      break
      
    case 'cancelled':
      if (order.cancelTime) {
        items.push({
          id: 'cancelled',
          title: '订单取消',
          description: '订单已取消，如有疑问请联系客服',
          time: formatTime(order.cancelTime),
          status: 'danger',
          icon: CircleCloseFilled,
          type: 'cancelled'
        })
      }
      break
      
    case 'refund_pending':
      if (order.payTime) {
        items.push({
          id: 'payment',
          title: '支付成功',
          description: '订单支付成功',
          time: formatTime(order.payTime),
          status: 'success',
          icon: CreditCard,
          type: 'payment'
        })
      }
      
      if (order.refundApplyTime) {
        items.push({
          id: 'refund_apply',
          title: '申请退票',
          description: '退票申请已提交，我们将在1-3个工作日内处理',
          time: formatTime(order.refundApplyTime),
          status: 'warning',
          icon: RefreshLeft,
          type: 'refund_apply'
        })
      }
      break
      
    case 'refunded':
      if (order.payTime) {
        items.push({
          id: 'payment',
          title: '支付成功',
          description: '订单支付成功',
          time: formatTime(order.payTime),
          status: 'success',
          icon: CreditCard,
          type: 'payment'
        })
      }
      
      if (order.refundApplyTime) {
        items.push({
          id: 'refund_apply',
          title: '申请退票',
          description: '退票申请已提交',
          time: formatTime(order.refundApplyTime),
          status: 'success',
          icon: RefreshLeft,
          type: 'refund_apply'
        })
      }
      
      if (order.refundTime) {
        items.push({
          id: 'refunded',
          title: '退票完成',
          description: '退票已完成，款项将在3-5个工作日内到账',
          time: formatTime(order.refundTime),
          status: 'success',
          icon: SuccessFilled,
          type: 'refunded'
        })
      }
      break
  }
  
  return items.reverse() // 最新的在上面
})

// 获取时间线项目的颜色
const getTimelineColor = (status: string) => {
  const colorMap: { [key: string]: string } = {
    'success': '#52c41a',
    'primary': '#1890ff',
    'warning': '#faad14',
    'danger': '#ff4d4f',
    'info': '#8c8c8c'
  }
  return colorMap[status] || '#8c8c8c'
}

// 获取相对时间描述
const getRelativeTime = (timeStr: string) => {
  if (!timeStr) return ''
  
  const now = new Date()
  const time = new Date(timeStr)
  const diffMs = now.getTime() - time.getTime()
  const diffMinutes = Math.floor(diffMs / (1000 * 60))
  const diffHours = Math.floor(diffMinutes / 60)
  const diffDays = Math.floor(diffHours / 24)
  
  if (diffMinutes < 1) {
    return '刚刚'
  } else if (diffMinutes < 60) {
    return `${diffMinutes}分钟前`
  } else if (diffHours < 24) {
    return `${diffHours}小时前`
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return formatTime(timeStr)
  }
}
</script>

<template>
  <ElCard class="order-timeline" shadow="never">
    <template #header>
      <div class="card-header">
        <ElIcon :size="20" color="#1890ff">
          <Clock />
        </ElIcon>
        <span class="header-title">订单动态</span>
      </div>
    </template>
    
    <!-- 加载状态 -->
    <ElSkeleton v-if="loading" animated>
      <template #template>
        <div class="skeleton-content">
          <el-skeleton-item 
            v-for="i in 3" 
            :key="i"
            variant="rect" 
            style="width: 100%; height: 80px; margin-bottom: 16px;" 
          />
        </div>
      </template>
    </ElSkeleton>
    
    <!-- 时间线内容 -->
    <div v-else class="timeline-content">
      <ElTimeline class="custom-timeline">
        <ElTimelineItem
          v-for="(item, index) in timelineItems"
          :key="item.id"
          :color="getTimelineColor(item.status)"
          :size="index === 0 ? 'large' : 'normal'"
          class="timeline-item"
        >
          <template #dot>
            <div 
              class="timeline-dot"
              :class="{
                'timeline-dot-large': index === 0,
                [`timeline-dot-${item.status}`]: true
              }"
            >
              <ElIcon 
                :size="index === 0 ? 18 : 14" 
                :color="index === 0 ? 'white' : getTimelineColor(item.status)"
              >
                <component :is="item.icon" />
              </ElIcon>
            </div>
          </template>
          
          <div class="timeline-item-content">
            <div class="timeline-header">
              <h4 
                class="timeline-title"
                :class="{ 'timeline-title-current': index === 0 }"
              >
                {{ item.title }}
              </h4>
              
              <div class="timeline-time">
                <span v-if="item.time" class="absolute-time">{{ item.time }}</span>
                <span v-if="item.time" class="relative-time">{{ getRelativeTime(item.time) }}</span>
              </div>
            </div>
            
            <p 
              class="timeline-description"
              :class="{ 'timeline-description-current': index === 0 }"
            >
              {{ item.description }}
            </p>
            
            <!-- 特殊状态的额外信息 -->
            <div v-if="item.type === 'pending'" class="timeline-extra">
              <div class="pending-tips">
                <div class="tip-item">
                  <span class="tip-icon">⏰</span>
                  <span class="tip-text">订单将在30分钟后自动取消</span>
                </div>
                <div class="tip-item">
                  <span class="tip-icon">💡</span>
                  <span class="tip-text">支持支付宝、微信、银联等多种支付方式</span>
                </div>
              </div>
            </div>
            
            <div v-else-if="item.type === 'waiting'" class="timeline-extra">
              <div class="waiting-tips">
                <div class="tip-item">
                  <span class="tip-icon">🚄</span>
                  <span class="tip-text">请提前30分钟到达车站进行安检和检票</span>
                </div>
                <div class="tip-item">
                  <span class="tip-icon">📱</span>
                  <span class="tip-text">可使用电子客票或身份证直接检票进站</span>
                </div>
              </div>
            </div>
            
            <div v-else-if="item.type === 'refund_apply'" class="timeline-extra">
              <div class="refund-tips">
                <div class="tip-item">
                  <span class="tip-icon">📋</span>
                  <span class="tip-text">退票申请正在处理中，请耐心等待</span>
                </div>
                <div class="tip-item">
                  <span class="tip-icon">💰</span>
                  <span class="tip-text">退款将原路返回至您的支付账户</span>
                </div>
              </div>
            </div>
          </div>
        </ElTimelineItem>
      </ElTimeline>
      
      <!-- 底部提示 -->
      <div class="timeline-footer">
        <div class="footer-tips">
          <ElIcon :size="16" color="#1890ff">
            <SuccessFilled />
          </ElIcon>
          <span class="footer-text">订单状态实时更新，如有疑问请联系客服</span>
        </div>
      </div>
    </div>
  </ElCard>
</template>

<style scoped>
.order-timeline {
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

.timeline-content {
  padding: 0;
}

.custom-timeline {
  padding-left: 0;
}

.timeline-item {
  padding-bottom: 24px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-dot {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border: 2px solid;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.timeline-dot-large {
  width: 40px;
  height: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.timeline-dot-success {
  border-color: #52c41a;
  background: #52c41a;
}

.timeline-dot-primary {
  border-color: #1890ff;
  background: #1890ff;
}

.timeline-dot-warning {
  border-color: #faad14;
  background: #faad14;
}

.timeline-dot-danger {
  border-color: #ff4d4f;
  background: #ff4d4f;
}

.timeline-dot-info {
  border-color: #8c8c8c;
  background: #8c8c8c;
}

.timeline-item-content {
  margin-left: 16px;
  flex: 1;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
  gap: 16px;
}

.timeline-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
  line-height: 1.4;
}

.timeline-title-current {
  font-size: 18px;
  font-weight: 700;
  color: #1890ff;
}

.timeline-time {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
  flex-shrink: 0;
}

.absolute-time {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
  font-family: 'Courier New', monospace;
}

.relative-time {
  font-size: 11px;
  color: #bfbfbf;
  font-weight: 500;
}

.timeline-description {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin: 0 0 12px 0;
}

.timeline-description-current {
  font-size: 15px;
  font-weight: 500;
  color: #1a1a1a;
}

.timeline-extra {
  margin-top: 12px;
}

.pending-tips,
.waiting-tips,
.refund-tips {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  padding: 6px 10px;
  background: #f6ffed;
  border-radius: 4px;
  border: 1px solid #d9f7be;
}

.tip-icon {
  font-size: 12px;
  flex-shrink: 0;
  margin-top: 1px;
}

.tip-text {
  font-size: 12px;
  color: #52c41a;
  font-weight: 500;
  line-height: 1.3;
}

.timeline-footer {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.footer-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}

.footer-text {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .timeline-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .timeline-time {
    align-items: flex-start;
  }
  
  .timeline-title {
    font-size: 15px;
  }
  
  .timeline-title-current {
    font-size: 16px;
  }
  
  .timeline-description {
    font-size: 13px;
  }
  
  .timeline-description-current {
    font-size: 14px;
  }
  
  .timeline-dot {
    width: 28px;
    height: 28px;
  }
  
  .timeline-dot-large {
    width: 36px;
    height: 36px;
  }
  
  .timeline-item-content {
    margin-left: 12px;
  }
}

/* 时间线连接线样式优化 */
:deep(.el-timeline-item__tail) {
  border-left: 2px solid #e8e8e8;
  left: 15px;
}

:deep(.el-timeline-item__wrapper) {
  position: relative;
  padding-left: 0;
  top: 0;
}

:deep(.el-timeline-item__dot) {
  position: absolute;
  left: 0;
  top: 0;
}

:deep(.el-timeline-item__content) {
  margin-left: 48px;
}

@media (max-width: 768px) {
  :deep(.el-timeline-item__content) {
    margin-left: 44px;
  }
  
  :deep(.el-timeline-item__tail) {
    left: 13px;
  }
}
</style>