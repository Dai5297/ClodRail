<script setup lang="ts">
import { computed } from 'vue'
import { ElButton, ElIcon, ElPopconfirm } from 'element-plus'
import { 
  CreditCard,
  Download,
  Share,
  Delete,
  RefreshLeft,
  Phone,
  QuestionFilled
} from '@element-plus/icons-vue'

// 定义订单接口
interface Order {
  id: string
  status: string
  orderNumber: string
  trainNumber: string
  departure: string
  arrival: string
  departureTime: string
  totalAmount: number
}

// 定义操作按钮接口
interface ActionButton {
  key: string
  label: string
  type: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  icon: any
  loading: boolean
  disabled: boolean
  description: string
  needConfirm?: boolean
  confirmTitle?: string
  confirmContent?: string
}

// 定义属性
interface Props {
  order: Order
  loading: boolean
}

// 定义事件
interface Emits {
  pay: [orderId: string]
  cancel: [orderId: string]
  refund: [orderId: string]
  downloadTicket: [orderId: string]
  share: [orderId: string]
  contactService: []
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

const emit = defineEmits<Emits>()

// 获取可用操作
const availableActions = computed(() => {
  const actions: ActionButton[] = []
  const status = props.order.status
  
  switch (status) {
    case 'pending':
      actions.push(
        {
          key: 'pay',
          label: '立即支付',
          type: 'primary',
          icon: CreditCard,
          loading: false,
          disabled: false,
          description: '完成订单支付'
        },
        {
          key: 'cancel',
          label: '取消订单',
          type: 'default',
          icon: Delete,
          loading: false,
          disabled: false,
          description: '取消当前订单',
          needConfirm: true,
          confirmTitle: '确认取消订单？',
          confirmContent: '取消后订单无法恢复，确定要取消吗？'
        }
      )
      break
      
    case 'paid':
      // 检查是否可以退票（发车前2小时）
      const canRefund = checkCanRefund(props.order.departureTime)
      
      actions.push(
        {
          key: 'downloadTicket',
          label: '下载电子票',
          type: 'primary',
          icon: Download,
          loading: false,
          disabled: false,
          description: '下载电子客票'
        },
        {
          key: 'share',
          label: '分享订单',
          type: 'default',
          icon: Share,
          loading: false,
          disabled: false,
          description: '分享订单信息'
        }
      )
      
      if (canRefund) {
        actions.push({
          key: 'refund',
          label: '申请退票',
          type: 'default',
          icon: RefreshLeft,
          loading: false,
          disabled: false,
          description: '申请退票退款',
          needConfirm: true,
          confirmTitle: '确认申请退票？',
          confirmContent: '退票可能产生手续费，确定要申请退票吗？'
        })
      }
      break
      
    case 'completed':
      actions.push(
        {
          key: 'downloadTicket',
          label: '下载电子票',
          type: 'default',
          icon: Download,
          loading: false,
          disabled: false,
          description: '下载电子客票'
        },
        {
          key: 'share',
          label: '分享订单',
          type: 'default',
          icon: Share,
          loading: false,
          disabled: false,
          description: '分享订单信息'
        }
      )
      break
      
    case 'cancelled':
    case 'refunded':
      actions.push({
        key: 'share',
        label: '分享订单',
        type: 'default',
        icon: Share,
        loading: false,
        disabled: false,
        description: '分享订单信息'
      })
      break
      
    case 'refund_pending':
      actions.push({
        key: 'share',
        label: '分享订单',
        type: 'default',
        icon: Share,
        loading: false,
        disabled: false,
        description: '分享订单信息'
      })
      break
  }
  
  // 所有状态都可以联系客服
  actions.push({
    key: 'contactService',
    label: '联系客服',
    type: 'default',
    icon: Phone,
    loading: false,
    disabled: false,
    description: '联系在线客服'
  })
  
  return actions
})

// 检查是否可以退票
const checkCanRefund = (departureTime: string) => {
  if (!departureTime) return false
  
  const now = new Date()
  const departure = new Date(departureTime)
  const timeDiff = departure.getTime() - now.getTime()
  const hoursDiff = timeDiff / (1000 * 60 * 60)
  
  // 发车前2小时可以退票
  return hoursDiff > 2
}

// 处理按钮点击
const handleAction = (actionKey: string) => {
  switch (actionKey) {
    case 'pay':
      emit('pay', props.order.id)
      break
    case 'cancel':
      emit('cancel', props.order.id)
      break
    case 'refund':
      emit('refund', props.order.id)
      break
    case 'downloadTicket':
      emit('downloadTicket', props.order.id)
      break
    case 'share':
      emit('share', props.order.id)
      break
    case 'contactService':
      emit('contactService')
      break
  }
}

// 获取按钮样式类
const getButtonClass = (action: any) => {
  const classes = ['action-button']
  
  if (action.type === 'primary') {
    classes.push('action-button-primary')
  }
  
  if (action.key === 'cancel' || action.key === 'refund') {
    classes.push('action-button-danger')
  }
  
  return classes.join(' ')
}
</script>

<template>
  <div class="action-buttons">
    <!-- 主要操作区域 -->
    <div class="primary-actions">
      <template v-for="action in availableActions" :key="action.key">
        <!-- 需要确认的操作 -->
        <ElPopconfirm
          v-if="action.needConfirm"
          :title="action.confirmTitle"
          :width="280"
          confirm-button-text="确认"
          cancel-button-text="取消"
          confirm-button-type="danger"
          :icon="QuestionFilled"
          icon-color="#faad14"
          @confirm="handleAction(action.key)"
        >
          <template #reference>
            <ElButton
              :type="action.type"
              :loading="action.loading || loading"
              :disabled="action.disabled"
              :class="getButtonClass(action)"
              size="large"
            >
              <ElIcon class="button-icon">
                <component :is="action.icon" />
              </ElIcon>
              <span class="button-text">{{ action.label }}</span>
            </ElButton>
          </template>
        </ElPopconfirm>
        
        <!-- 普通操作 -->
        <ElButton
          v-else
          :type="action.type"
          :loading="action.loading || loading"
          :disabled="action.disabled"
          :class="getButtonClass(action)"
          size="large"
          @click="handleAction(action.key)"
        >
          <ElIcon class="button-icon">
            <component :is="action.icon" />
          </ElIcon>
          <span class="button-text">{{ action.label }}</span>
        </ElButton>
      </template>
    </div>
    
    <!-- 操作提示 -->
    <div class="action-tips">
      <div v-if="order.status === 'pending'" class="tip-item pending">
        <ElIcon :size="14" color="#faad14">
          <QuestionFilled />
        </ElIcon>
        <span class="tip-text">订单将在30分钟后自动取消，请尽快完成支付</span>
      </div>
      
      <div v-else-if="order.status === 'paid'" class="tip-item paid">
        <ElIcon :size="14" color="#52c41a">
          <QuestionFilled />
        </ElIcon>
        <span class="tip-text">电子客票已生成，可凭身份证或电子票检票进站</span>
      </div>
      
      <div v-else-if="order.status === 'refund_pending'" class="tip-item refunding">
        <ElIcon :size="14" color="#1890ff">
          <QuestionFilled />
        </ElIcon>
        <span class="tip-text">退票申请处理中，退款将在1-3个工作日内到账</span>
      </div>
      
      <div v-else-if="order.status === 'completed'" class="tip-item completed">
        <ElIcon :size="14" color="#52c41a">
          <QuestionFilled />
        </ElIcon>
        <span class="tip-text">行程已完成，感谢您的使用！如需报销凭证请联系客服</span>
      </div>
    </div>
    
    <!-- 紧急联系信息 -->
    <div class="emergency-contact">
      <div class="contact-header">
        <ElIcon :size="16" color="#1890ff">
          <Phone />
        </ElIcon>
        <span class="contact-title">需要帮助？</span>
      </div>
      
      <div class="contact-info">
        <div class="contact-item">
          <span class="contact-label">客服热线：</span>
          <span class="contact-value">400-123-4567</span>
        </div>
        <div class="contact-item">
          <span class="contact-label">服务时间：</span>
          <span class="contact-value">7×24小时</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.primary-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.action-button {
  min-width: 140px;
  height: 48px;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-button-primary {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border: none;
  color: white;
}

.action-button-primary:hover {
  background: linear-gradient(135deg, #096dd9 0%, #0050b3 100%);
}

.action-button-danger {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.action-button-danger:hover {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

.button-icon {
  font-size: 16px;
}

.button-text {
  font-size: 14px;
}

.action-tips {
  display: flex;
  justify-content: center;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 8px;
  max-width: 500px;
  text-align: center;
}

.tip-item.pending {
  background: #fff7e6;
  border: 1px solid #ffd666;
}

.tip-item.paid {
  background: #f6ffed;
  border: 1px solid #d9f7be;
}

.tip-item.refunding {
  background: #e6f4ff;
  border: 1px solid #69c0ff;
}

.tip-item.completed {
  background: #f6ffed;
  border: 1px solid #d9f7be;
}

.tip-text {
  font-size: 13px;
  font-weight: 500;
  line-height: 1.4;
}

.tip-item.pending .tip-text {
  color: #d48806;
}

.tip-item.paid .tip-text {
  color: #389e0d;
}

.tip-item.refunding .tip-text {
  color: #0958d9;
}

.tip-item.completed .tip-text {
  color: #389e0d;
}

.emergency-contact {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #f0f0f0;
}

.contact-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.contact-title {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
}

.contact-info {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.contact-label {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
}

.contact-value {
  font-size: 12px;
  color: #1a1a1a;
  font-weight: 600;
  font-family: 'Courier New', monospace;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .primary-actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .action-button {
    min-width: auto;
    width: 100%;
    height: 44px;
  }
  
  .button-text {
    font-size: 15px;
  }
  
  .tip-item {
    padding: 10px 12px;
  }
  
  .tip-text {
    font-size: 12px;
  }
  
  .contact-info {
    flex-direction: column;
    gap: 8px;
  }
  
  .contact-item {
    justify-content: space-between;
  }
}

/* 按钮加载状态优化 */
:deep(.el-button.is-loading) {
  pointer-events: none;
}

:deep(.el-button.is-loading .el-icon) {
  margin-right: 8px;
}

/* Popconfirm 样式优化 */
:deep(.el-popconfirm__main) {
  margin-bottom: 12px;
}

:deep(.el-popconfirm__action) {
  text-align: center;
}
</style>