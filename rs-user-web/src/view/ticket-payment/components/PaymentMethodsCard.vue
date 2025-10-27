<template>
  <div class="payment-methods-card">
    <h3 class="card-title">
      <el-icon><CreditCard /></el-icon>
      选择支付方式
    </h3>

    <div class="payment-methods">
      <div 
        v-for="method in paymentMethods" 
        :key="method.id"
        class="payment-method"
        :class="{ 
          'selected': selectedMethod?.id === method.id,
          'disabled': !method.available
        }"
        @click="handleSelectMethod(method)"
      >
        <div class="method-icon">
          <img :src="method.icon" :alt="method.name" />
        </div>
        <div class="method-info">
          <div class="method-name">{{ method.name }}</div>
          <div class="method-desc" :class="{ 'unavailable': !method.available }">
            {{ method.description }}
          </div>
        </div>
        <div class="method-radio">
          <el-radio 
            :model-value="selectedMethod?.id === method.id"
            :disabled="!method.available"
            @change="handleSelectMethod(method)"
          />
        </div>
      </div>
    </div>

    <!-- 支付按钮 -->
    <div class="payment-actions">
      <el-button 
        type="primary" 
        size="large" 
        @click="handlePay"
        :disabled="!selectedMethod || processing"
        :loading="processing"
        class="pay-button"
      >
        <el-icon><CreditCard /></el-icon>
        立即支付 ￥{{ totalAmount }}
      </el-button>
      
      <div class="payment-security">
        <el-icon><Lock /></el-icon>
        <span>支付环境安全，信息已加密</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { CreditCard, Lock } from '@element-plus/icons-vue'

const props = defineProps({
  paymentMethods: {
    type: Array,
    required: true
  },
  selectedMethod: {
    type: Object,
    default: null
  },
  totalAmount: {
    type: Number,
    required: true
  },
  processing: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['select-method', 'pay'])

const handleSelectMethod = (method) => {
  if (method.available) {
    emit('select-method', method)
  }
}

const handlePay = () => {
  emit('pay')
}
</script>

<style scoped>
.payment-methods-card {
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

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.payment-method {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-method:hover {
  border-color: #1890ff;
}

.payment-method.selected {
  border-color: #1890ff;
  background-color: #f0f9ff;
}

.payment-method.disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.method-icon img {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.method-info {
  flex: 1;
}

.method-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.method-desc {
  font-size: 12px;
  color: #666;
}

.method-desc.unavailable {
  color: #999;
}

.payment-actions {
  text-align: center;
}

.pay-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  margin-bottom: 16px;
}

.payment-security {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 12px;
  color: #52c41a;
}
</style>

