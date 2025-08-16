<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElButton, ElRadio, ElRadioGroup, ElCollapse, ElCollapseItem } from 'element-plus'

// 定义Props
interface Coupon {
  id: number
  name: string
  discount: number
  minAmount: number
}

interface Props {
  selectedMethod: string
  availableCoupons: Coupon[]
  selectedCoupon: number | null
  finalAmount: number
}

const props = defineProps<Props>()

// 定义事件
const emit = defineEmits<{
  'method-change': [method: string]
  'coupon-select': [couponId: number | null]
  payment: []
}>()

// 支付方式配置
const paymentMethods = [
  {
    id: 'alipay',
    name: '支付宝',
    icon: '💙',
    description: '推荐使用支付宝快捷支付',
    discount: '立减5元'
  },
  {
    id: 'wechat',
    name: '微信支付',
    icon: '💚',
    description: '微信安全支付',
    discount: ''
  },
  {
    id: 'unionpay',
    name: '银联支付',
    icon: '🔵',
    description: '银联在线支付',
    discount: ''
  },
  {
    id: 'points',
    name: '积分支付',
    icon: '⭐',
    description: '使用积分抵扣部分费用',
    discount: ''
  }
]

// 优惠券展开状态
const couponExpanded = ref(['coupons'])

// 处理支付方式选择
const handleMethodChange = (method: string | number | boolean | undefined) => {
  if (typeof method === 'string') {
    emit('method-change', method)
  }
}

// 处理优惠券选择
const handleCouponSelect = (couponId: number | null) => {
  emit('coupon-select', couponId)
}

// 处理支付
const handlePayment = () => {
  emit('payment')
}

// 检查优惠券是否可用
const isCouponAvailable = (coupon: Coupon) => {
  return props.finalAmount >= coupon.minAmount
}

// 获取选中的优惠券
const selectedCouponInfo = computed(() => {
  if (!props.selectedCoupon) return null
  return props.availableCoupons.find(c => c.id === props.selectedCoupon)
})
</script>

<template>
  <div class="payment-methods">
    <!-- 支付方式选择 -->
    <div class="payment-section">
      <h3 class="section-title">
        <span class="title-icon">💳</span>
        选择支付方式
      </h3>
      
      <el-radio-group 
        :model-value="selectedMethod" 
        @update:model-value="handleMethodChange"
        class="payment-options"
      >
        <div 
          v-for="method in paymentMethods" 
          :key="method.id"
          class="payment-option"
          :class="{ active: selectedMethod === method.id }"
        >
          <el-radio :label="method.id" class="payment-radio">
            <div class="method-content">
              <div class="method-info">
                <div class="method-header">
                  <span class="method-icon">{{ method.icon }}</span>
                  <span class="method-name">{{ method.name }}</span>
                  <span v-if="method.discount" class="method-discount">{{ method.discount }}</span>
                </div>
                <div class="method-description">{{ method.description }}</div>
              </div>
            </div>
          </el-radio>
        </div>
      </el-radio-group>
    </div>
    
    <!-- 优惠券选择 -->
    <div class="payment-section">
      <h3 class="section-title">
        <span class="title-icon">🎫</span>
        优惠券
      </h3>
      
      <el-collapse v-model="couponExpanded" class="coupon-collapse">
        <el-collapse-item name="coupons">
          <template #title>
            <div class="coupon-header">
              <span class="coupon-count">可用优惠券 {{ availableCoupons.length }} 张</span>
              <span v-if="selectedCouponInfo" class="selected-coupon">
                已选择：{{ selectedCouponInfo.name }}
              </span>
            </div>
          </template>
          
          <div class="coupon-list">
            <div 
              class="coupon-item"
              :class="{ 
                selected: selectedCoupon === null,
                disabled: false
              }"
              @click="handleCouponSelect(null)"
            >
              <div class="coupon-radio">
                <span class="radio-dot" :class="{ active: selectedCoupon === null }"></span>
              </div>
              <div class="coupon-content">
                <div class="coupon-name">不使用优惠券</div>
                <div class="coupon-desc">原价支付</div>
              </div>
            </div>
            
            <div 
              v-for="coupon in availableCoupons" 
              :key="coupon.id"
              class="coupon-item"
              :class="{ 
                selected: selectedCoupon === coupon.id,
                disabled: !isCouponAvailable(coupon)
              }"
              @click="isCouponAvailable(coupon) && handleCouponSelect(coupon.id)"
            >
              <div class="coupon-radio">
                <span class="radio-dot" :class="{ active: selectedCoupon === coupon.id }"></span>
              </div>
              <div class="coupon-content">
                <div class="coupon-name">{{ coupon.name }}</div>
                <div class="coupon-desc">
                  立减¥{{ coupon.discount }} (满¥{{ coupon.minAmount }}可用)
                </div>
              </div>
              <div class="coupon-amount">-¥{{ coupon.discount }}</div>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    
    <!-- 支付金额和按钮 -->
    <div class="payment-section">
      <div class="amount-summary">
        <div class="amount-row">
          <span class="amount-label">应付金额</span>
          <span class="amount-value final">¥{{ finalAmount }}</span>
        </div>
        <div v-if="selectedCouponInfo" class="discount-info">
          已优惠 ¥{{ selectedCouponInfo.discount }}
        </div>
      </div>
      
      <el-button 
        type="primary" 
        size="large" 
        class="payment-button"
        @click="handlePayment"
      >
        立即支付 ¥{{ finalAmount }}
      </el-button>
      
      <div class="payment-tips">
        <div class="tip-item">
          <span class="tip-icon">🔒</span>
          <span class="tip-text">支付环境安全加密</span>
        </div>
        <div class="tip-item">
          <span class="tip-icon">⚡</span>
          <span class="tip-text">支付成功后立即出票</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.payment-methods {
  padding: 0;
}

.payment-section {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.payment-section:last-child {
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

/* 支付方式样式 */
.payment-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-option {
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.payment-option:hover {
  border-color: #d9d9d9;
}

.payment-option.active {
  border-color: #1890ff;
  background: #f6ffed;
}

.payment-radio {
  width: 100%;
}

.payment-radio :deep(.el-radio__input) {
  display: none;
}

.payment-radio :deep(.el-radio__label) {
  padding: 0;
  width: 100%;
}

.method-content {
  width: 100%;
}

.method-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.method-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.method-icon {
  font-size: 20px;
}

.method-name {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  flex: 1;
}

.method-discount {
  font-size: 12px;
  background: #ff4d4f;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
}

.method-description {
  font-size: 13px;
  color: #8c8c8c;
  margin-left: 32px;
}

/* 优惠券样式 */
.coupon-collapse {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
}

.coupon-collapse :deep(.el-collapse-item__header) {
  padding: 16px;
  border: none;
  background: #fafafa;
}

.coupon-collapse :deep(.el-collapse-item__content) {
  padding: 0;
}

.coupon-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.coupon-count {
  font-size: 14px;
  color: #262626;
  font-weight: 500;
}

.selected-coupon {
  font-size: 12px;
  color: #1890ff;
}

.coupon-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.coupon-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.coupon-item:hover:not(.disabled) {
  border-color: #1890ff;
  background: #f6ffed;
}

.coupon-item.selected {
  border-color: #1890ff;
  background: #e6f7ff;
}

.coupon-item.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.coupon-radio {
  flex-shrink: 0;
}

.radio-dot {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid #d9d9d9;
  border-radius: 50%;
  transition: all 0.3s ease;
  position: relative;
}

.radio-dot.active {
  border-color: #1890ff;
}

.radio-dot.active::after {
  content: '';
  position: absolute;
  top: 2px;
  left: 2px;
  width: 8px;
  height: 8px;
  background: #1890ff;
  border-radius: 50%;
}

.coupon-content {
  flex: 1;
}

.coupon-name {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 2px;
}

.coupon-desc {
  font-size: 12px;
  color: #8c8c8c;
}

.coupon-amount {
  font-size: 16px;
  font-weight: bold;
  color: #f5222d;
}

/* 支付金额和按钮样式 */
.amount-summary {
  margin-bottom: 20px;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.amount-label {
  font-size: 16px;
  color: #595959;
}

.amount-value {
  font-size: 18px;
  font-weight: bold;
  color: #262626;
}

.amount-value.final {
  font-size: 24px;
  color: #f5222d;
}

.discount-info {
  text-align: right;
  font-size: 12px;
  color: #52c41a;
}

.payment-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.payment-tips {
  display: flex;
  justify-content: center;
  gap: 24px;
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
  color: #8c8c8c;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .payment-section {
    padding: 16px;
  }
  
  .payment-tips {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
  
  .method-header {
    gap: 8px;
  }
  
  .method-description {
    margin-left: 28px;
  }
}
</style>