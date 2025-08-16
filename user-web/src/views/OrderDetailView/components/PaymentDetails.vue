<script setup lang="ts">
import { computed } from 'vue'
import { ElCard, ElSkeleton, ElIcon, ElTag, ElDivider } from 'element-plus'
import { 
  CreditCard, 
  Wallet, 
  Money,
  Clock,
  SuccessFilled,
  WarningFilled
} from '@element-plus/icons-vue'

// 定义支付信息接口
interface PaymentInfo {
  paymentMethod: string
  paymentChannel: string
  transactionId: string
  paymentTime: string
  paymentAmount: number
  discountAmount: number
  actualAmount: number
  paymentStatus: string
  refundAmount?: number
  refundTime?: string
  refundStatus?: string
}

// 定义属性
interface Props {
  paymentInfo: PaymentInfo | null
  loading: boolean
}

const props = withDefaults(defineProps<Props>(), {
  paymentInfo: null,
  loading: false
})

// 支付方式映射
const paymentMethodMap: { [key: string]: string } = {
  'alipay': '支付宝',
  'wechat': '微信支付',
  'unionpay': '银联支付',
  'credit_card': '信用卡',
  'debit_card': '储蓄卡',
  'balance': '账户余额',
  'points': '积分支付'
}

// 支付渠道映射
const paymentChannelMap: { [key: string]: string } = {
  'alipay_app': '支付宝APP',
  'alipay_web': '支付宝网页',
  'wechat_app': '微信APP',
  'wechat_jsapi': '微信公众号',
  'wechat_native': '微信扫码',
  'unionpay_app': '银联APP',
  'unionpay_web': '银联网页',
  'bank_card': '银行卡',
  'balance': '余额',
  'points': '积分'
}

// 支付状态映射
const paymentStatusMap: { [key: string]: string } = {
  'pending': '待支付',
  'processing': '支付中',
  'success': '支付成功',
  'failed': '支付失败',
  'cancelled': '已取消',
  'refunded': '已退款',
  'partial_refunded': '部分退款'
}

// 退款状态映射
const refundStatusMap: { [key: string]: string } = {
  'pending': '退款中',
  'processing': '处理中',
  'success': '退款成功',
  'failed': '退款失败',
  'cancelled': '已取消'
}

// 获取支付方式显示名称
const getPaymentMethodName = (method: string) => {
  return paymentMethodMap[method] || method
}

// 获取支付渠道显示名称
const getPaymentChannelName = (channel: string) => {
  return paymentChannelMap[channel] || channel
}

// 获取支付状态显示名称
const getPaymentStatusName = (status: string) => {
  return paymentStatusMap[status] || status
}

// 获取退款状态显示名称
const getRefundStatusName = (status: string) => {
  return refundStatusMap[status] || status
}

// 获取支付状态颜色
const getPaymentStatusColor = (status: string) => {
  const colorMap: { [key: string]: string } = {
    'pending': '#faad14',
    'processing': '#1890ff',
    'success': '#52c41a',
    'failed': '#ff4d4f',
    'cancelled': '#8c8c8c',
    'refunded': '#722ed1',
    'partial_refunded': '#fa8c16'
  }
  return colorMap[status] || '#8c8c8c'
}

// 获取退款状态颜色
const getRefundStatusColor = (status: string) => {
  const colorMap: { [key: string]: string } = {
    'pending': '#faad14',
    'processing': '#1890ff',
    'success': '#52c41a',
    'failed': '#ff4d4f',
    'cancelled': '#8c8c8c'
  }
  return colorMap[status] || '#8c8c8c'
}

// 获取支付方式图标
const getPaymentIcon = (method: string) => {
  const iconMap: { [key: string]: any } = {
    'alipay': Wallet,
    'wechat': Wallet,
    'unionpay': CreditCard,
    'credit_card': CreditCard,
    'debit_card': CreditCard,
    'balance': Money,
    'points': Money
  }
  return iconMap[method] || CreditCard
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

// 脱敏处理交易号
const maskTransactionId = (transactionId: string) => {
  if (!transactionId) return ''
  if (transactionId.length <= 8) return transactionId
  
  const start = transactionId.substring(0, 4)
  const end = transactionId.substring(transactionId.length - 4)
  const middle = '*'.repeat(Math.min(transactionId.length - 8, 8))
  
  return `${start}${middle}${end}`
}

// 计算节省金额
const savedAmount = computed(() => {
  if (!props.paymentInfo) return 0
  return props.paymentInfo.paymentAmount - props.paymentInfo.actualAmount
})
</script>

<template>
  <ElCard class="payment-details" shadow="never">
    <template #header>
      <div class="card-header">
        <ElIcon :size="20" color="#1890ff">
          <Money />
        </ElIcon>
        <span class="header-title">支付详情</span>
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
    
    <!-- 支付信息 -->
    <div v-else-if="paymentInfo" class="payment-content">
      <!-- 支付状态 -->
      <div class="payment-status">
        <div class="status-info">
          <ElIcon :size="24" :color="getPaymentStatusColor(paymentInfo.paymentStatus)">
            <SuccessFilled v-if="paymentInfo.paymentStatus === 'success'" />
            <Clock v-else-if="paymentInfo.paymentStatus === 'pending'" />
            <WarningFilled v-else />
          </ElIcon>
          
          <div class="status-text">
            <ElTag
              :color="getPaymentStatusColor(paymentInfo.paymentStatus)"
              class="status-tag"
              size="large"
            >
              {{ getPaymentStatusName(paymentInfo.paymentStatus) }}
            </ElTag>
            
            <div class="status-time" v-if="paymentInfo.paymentTime">
              {{ formatTime(paymentInfo.paymentTime) }}
            </div>
          </div>
        </div>
        
        <div class="payment-amount">
          <span class="amount-label">实付金额</span>
          <span class="amount-value">¥{{ paymentInfo.actualAmount.toFixed(2) }}</span>
        </div>
      </div>
      
      <ElDivider class="section-divider" />
      
      <!-- 支付方式 -->
      <div class="payment-method">
        <div class="method-info">
          <ElIcon :size="20" :color="getPaymentStatusColor(paymentInfo.paymentStatus)">
            <component :is="getPaymentIcon(paymentInfo.paymentMethod)" />
          </ElIcon>
          
          <div class="method-details">
            <div class="method-name">
              {{ getPaymentMethodName(paymentInfo.paymentMethod) }}
            </div>
            <div class="method-channel">
              {{ getPaymentChannelName(paymentInfo.paymentChannel) }}
            </div>
          </div>
        </div>
        
        <div class="transaction-info">
          <div class="transaction-id">
            <span class="label">交易号：</span>
            <span class="value">{{ maskTransactionId(paymentInfo.transactionId) }}</span>
          </div>
        </div>
      </div>
      
      <ElDivider class="section-divider" />
      
      <!-- 费用明细 -->
      <div class="cost-breakdown">
        <div class="breakdown-title">
          <ElIcon :size="16" color="#1890ff">
            <Money />
          </ElIcon>
          <span>费用明细</span>
        </div>
        
        <div class="breakdown-content">
          <div class="breakdown-row">
            <span class="row-label">订单金额</span>
            <span class="row-value">¥{{ paymentInfo.paymentAmount.toFixed(2) }}</span>
          </div>
          
          <div class="breakdown-row discount" v-if="paymentInfo.discountAmount > 0">
            <span class="row-label">优惠减免</span>
            <span class="row-value discount-value">-¥{{ paymentInfo.discountAmount.toFixed(2) }}</span>
          </div>
          
          <div class="breakdown-row total">
            <span class="row-label">实付金额</span>
            <span class="row-value total-value">¥{{ paymentInfo.actualAmount.toFixed(2) }}</span>
          </div>
          
          <div class="saved-info" v-if="savedAmount > 0">
            <span class="saved-text">🎉 您节省了 ¥{{ savedAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>
      
      <!-- 退款信息 -->
      <div v-if="paymentInfo.refundAmount && paymentInfo.refundAmount > 0" class="refund-info">
        <ElDivider class="section-divider" />
        
        <div class="refund-header">
          <div class="refund-status">
            <ElIcon :size="20" :color="getRefundStatusColor(paymentInfo.refundStatus || 'pending')">
              <Money />
            </ElIcon>
            
            <div class="refund-text">
              <ElTag
                :color="getRefundStatusColor(paymentInfo.refundStatus || 'pending')"
                class="refund-tag"
                size="large"
              >
                {{ getRefundStatusName(paymentInfo.refundStatus || 'pending') }}
              </ElTag>
              
              <div class="refund-time" v-if="paymentInfo.refundTime">
                {{ formatTime(paymentInfo.refundTime) }}
              </div>
            </div>
          </div>
          
          <div class="refund-amount">
            <span class="amount-label">退款金额</span>
            <span class="amount-value refund-value">¥{{ paymentInfo.refundAmount.toFixed(2) }}</span>
          </div>
        </div>
        
        <div class="refund-tips">
          <div class="tip-item">
            <span class="tip-icon">💡</span>
            <span class="tip-text">退款将原路返回至您的支付账户，请注意查收</span>
          </div>
          <div class="tip-item">
            <span class="tip-icon">⏰</span>
            <span class="tip-text">退款到账时间：支付宝/微信1-3个工作日，银行卡3-7个工作日</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 无支付信息 -->
    <div v-else class="no-payment">
      <ElIcon :size="48" color="#d9d9d9">
        <Money />
      </ElIcon>
      <div class="no-payment-text">暂无支付信息</div>
    </div>
  </ElCard>
</template>

<style scoped>
.payment-details {
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

.payment-content {
  padding: 0;
}

.payment-status {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.status-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.status-text {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.status-tag {
  font-size: 16px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 6px;
  color: white !important;
  border: none;
}

.status-time {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
  font-family: 'Courier New', monospace;
}

.payment-amount {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
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

.section-divider {
  margin: 20px 0;
  border-color: #f0f0f0;
}

.payment-method {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.method-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.method-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.method-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.method-channel {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
}

.transaction-info {
  text-align: right;
}

.transaction-id {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.label {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
}

.value {
  font-size: 14px;
  color: #1a1a1a;
  font-weight: 600;
  font-family: 'Courier New', monospace;
}

.cost-breakdown {
  background: white;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  overflow: hidden;
}

.breakdown-title {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
}

.breakdown-content {
  padding: 16px;
}

.breakdown-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.breakdown-row:last-child {
  margin-bottom: 0;
}

.breakdown-row.total {
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  margin-top: 12px;
}

.row-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.row-value {
  font-size: 14px;
  color: #1a1a1a;
  font-weight: 600;
  font-family: 'Arial', sans-serif;
}

.discount-value {
  color: #52c41a;
}

.total-value {
  font-size: 18px;
  font-weight: 700;
  color: #ff4d4f;
}

.saved-info {
  text-align: center;
  margin-top: 12px;
  padding: 8px;
  background: #f6ffed;
  border-radius: 6px;
  border: 1px solid #d9f7be;
}

.saved-text {
  font-size: 12px;
  color: #52c41a;
  font-weight: 600;
}

.refund-info {
  margin-top: 20px;
}

.refund-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  background: #fff7e6;
  border-radius: 8px;
  border: 1px solid #ffd666;
  margin-bottom: 16px;
}

.refund-status {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.refund-text {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.refund-tag {
  font-size: 14px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 4px;
  color: white !important;
  border: none;
}

.refund-time {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
  font-family: 'Courier New', monospace;
}

.refund-amount {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.refund-value {
  color: #722ed1;
}

.refund-tips {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px 12px;
  background: #f6ffed;
  border-radius: 6px;
  border: 1px solid #d9f7be;
}

.tip-icon {
  font-size: 14px;
  flex-shrink: 0;
  margin-top: 1px;
}

.tip-text {
  font-size: 12px;
  color: #52c41a;
  font-weight: 500;
  line-height: 1.4;
}

.no-payment {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.no-payment-text {
  margin-top: 12px;
  font-size: 14px;
  color: #8c8c8c;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .payment-status {
    flex-direction: column;
    gap: 16px;
    padding: 16px;
  }
  
  .payment-amount {
    align-items: flex-start;
  }
  
  .amount-value {
    font-size: 20px;
  }
  
  .payment-method {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .transaction-info {
    text-align: left;
    width: 100%;
  }
  
  .refund-header {
    flex-direction: column;
    gap: 12px;
    padding: 12px;
  }
  
  .refund-amount {
    align-items: flex-start;
  }
  
  .total-value {
    font-size: 16px;
  }
}
</style>