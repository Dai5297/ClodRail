<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElDialog, ElButton, ElLoading } from 'element-plus'

// 定义Props
interface Props {
  visible: boolean
  paymentMethod: string
  amount: number
  loading: boolean
}

const props = defineProps<Props>()

// 定义事件
const emit = defineEmits<{
  'update:visible': [value: boolean]
  confirm: []
}>()

// 支付方式配置
const paymentMethodConfig = {
  alipay: {
    name: '支付宝',
    icon: '💙',
    qrCode: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iIzAwNzlmZiIvPjx0ZXh0IHg9IjUwIiB5PSI1NSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE0IiBmaWxsPSJ3aGl0ZSIgdGV4dC1hbmNob3I9Im1pZGRsZSI+QWxpcGF5PC90ZXh0Pjwvc3ZnPg=='
  },
  wechat: {
    name: '微信支付',
    icon: '💚',
    qrCode: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iIzA5YmIwNyIvPjx0ZXh0IHg9IjUwIiB5PSI1NSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE0IiBmaWxsPSJ3aGl0ZSIgdGV4dC1hbmNob3I9Im1pZGRsZSI+V2VDaGF0PC90ZXh0Pjwvc3ZnPg=='
  },
  unionpay: {
    name: '银联支付',
    icon: '🔵',
    qrCode: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iIzAwNTFhNSIvPjx0ZXh0IHg9IjUwIiB5PSI1NSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE0IiBmaWxsPSJ3aGl0ZSIgdGV4dC1hbmNob3I9Im1pZGRsZSI+VW5pb25QYXk8L3RleHQ+PC9zdmc+'
  },
  points: {
    name: '积分支付',
    icon: '⭐',
    qrCode: ''
  }
}

// 支付状态
const paymentStatus = ref<'waiting' | 'processing' | 'success' | 'failed'>('waiting')

// 获取当前支付方式配置
const currentMethod = computed(() => {
  return paymentMethodConfig[props.paymentMethod as keyof typeof paymentMethodConfig] || paymentMethodConfig.alipay
})

// 处理关闭
const handleClose = () => {
  if (!props.loading) {
    emit('update:visible', false)
    paymentStatus.value = 'waiting'
  }
}

// 处理确认支付
const handleConfirm = () => {
  paymentStatus.value = 'processing'
  emit('confirm')
}

// 监听loading状态变化
watch(() => props.loading, (newLoading) => {
  if (newLoading) {
    paymentStatus.value = 'processing'
  }
})
</script>

<template>
  <el-dialog
    :model-value="visible"
    title="确认支付"
    width="450px"
    :before-close="handleClose"
    :close-on-click-modal="!loading"
    :close-on-press-escape="!loading"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="payment-confirm">
      <!-- 支付信息 -->
      <div class="payment-info">
        <div class="payment-header">
          <span class="payment-icon">{{ currentMethod.icon }}</span>
          <span class="payment-name">{{ currentMethod.name }}</span>
        </div>
        
        <div class="payment-amount">
          <span class="amount-label">支付金额</span>
          <span class="amount-value">¥{{ amount }}</span>
        </div>
      </div>
      
      <!-- 二维码支付 -->
      <div v-if="currentMethod.qrCode && !loading" class="qr-payment">
        <div class="qr-container">
          <img :src="currentMethod.qrCode" alt="支付二维码" class="qr-code" />
        </div>
        <p class="qr-tips">
          请使用{{ currentMethod.name }}扫描二维码完成支付
        </p>
      </div>
      
      <!-- 积分支付 -->
      <div v-else-if="paymentMethod === 'points' && !loading" class="points-payment">
        <div class="points-info">
          <div class="points-item">
            <span class="points-label">当前积分</span>
            <span class="points-value">12,580</span>
          </div>
          <div class="points-item">
            <span class="points-label">需要积分</span>
            <span class="points-value">{{ amount * 100 }}</span>
          </div>
          <div class="points-item">
            <span class="points-label">剩余积分</span>
            <span class="points-value">{{ 12580 - amount * 100 }}</span>
          </div>
        </div>
        <p class="points-tips">
          1元 = 100积分，积分不足部分将使用其他支付方式
        </p>
      </div>
      
      <!-- 支付处理中 -->
      <div v-if="loading" class="payment-processing">
        <div class="processing-animation">
          <div class="spinner"></div>
        </div>
        <p class="processing-text">正在处理支付请求...</p>
        <p class="processing-tips">请勿关闭页面或重复提交</p>
      </div>
      
      <!-- 安全提示 -->
      <div v-if="!loading" class="security-tips">
        <div class="tip-item">
          <span class="tip-icon">🔒</span>
          <span class="tip-text">256位SSL加密传输</span>
        </div>
        <div class="tip-item">
          <span class="tip-icon">🛡️</span>
          <span class="tip-text">银行级安全保障</span>
        </div>
        <div class="tip-item">
          <span class="tip-icon">⚡</span>
          <span class="tip-text">支付成功立即出票</span>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button 
          v-if="!loading" 
          @click="handleClose"
        >
          取消
        </el-button>
        <el-button 
          v-if="!loading && paymentMethod !== 'points'" 
          type="primary" 
          @click="handleConfirm"
        >
          确认支付
        </el-button>
        <el-button 
          v-if="!loading && paymentMethod === 'points'" 
          type="primary" 
          @click="handleConfirm"
        >
          确认使用积分
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.payment-confirm {
  padding: 8px 0;
}

.payment-info {
  text-align: center;
  margin-bottom: 24px;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
}

.payment-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 16px;
}

.payment-icon {
  font-size: 24px;
}

.payment-name {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}

.payment-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.amount-label {
  font-size: 16px;
  color: #595959;
}

.amount-value {
  font-size: 24px;
  font-weight: bold;
  color: #f5222d;
}

/* 二维码支付样式 */
.qr-payment {
  text-align: center;
  margin-bottom: 24px;
}

.qr-container {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
  padding: 20px;
  background: white;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
}

.qr-code {
  width: 160px;
  height: 160px;
  border-radius: 4px;
}

.qr-tips {
  margin: 0;
  font-size: 14px;
  color: #8c8c8c;
}

/* 积分支付样式 */
.points-payment {
  margin-bottom: 24px;
}

.points-info {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.points-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.points-item:last-child {
  margin-bottom: 0;
  padding-top: 8px;
  border-top: 1px solid #d9f7be;
  font-weight: 600;
}

.points-label {
  font-size: 14px;
  color: #595959;
}

.points-value {
  font-size: 14px;
  color: #262626;
  font-weight: 500;
}

.points-tips {
  margin: 0;
  font-size: 12px;
  color: #8c8c8c;
  text-align: center;
}

/* 支付处理中样式 */
.payment-processing {
  text-align: center;
  margin-bottom: 24px;
  padding: 32px 20px;
}

.processing-animation {
  margin-bottom: 16px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f0f0f0;
  border-top: 4px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.processing-text {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  color: #262626;
}

.processing-tips {
  margin: 0;
  font-size: 13px;
  color: #8c8c8c;
}

/* 安全提示样式 */
.security-tips {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 16px;
  background: #f6ffed;
  border-radius: 8px;
  border: 1px solid #b7eb8f;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.tip-icon {
  font-size: 12px;
}

.tip-text {
  font-size: 11px;
  color: #52c41a;
  font-weight: 500;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .qr-code {
    width: 120px;
    height: 120px;
  }
  
  .security-tips {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
  
  .payment-amount {
    flex-direction: column;
    gap: 8px;
  }
  
  .amount-value {
    font-size: 20px;
  }
}
</style>