<script setup lang="ts">
import { ref, computed, watch } from 'vue'

// 定义商品接口
interface Product {
  id: string
  name: string
  description: string
  points: number
  originalPrice?: number
  image: string
  category: string
  stock: number
  isHot?: boolean
  isNew?: boolean
  discount?: number
  rating?: number
  reviewCount?: number
}

// 定义Props
interface Props {
  visible: boolean
  product: Product | null
  userPoints: number
}

// 定义Emits
interface Emits {
  (e: 'update:visible', visible: boolean): void
  (e: 'confirm', data: { product: Product; quantity: number; deliveryInfo: any }): void
  (e: 'cancel'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const quantity = ref(1)
const exchangeStep = ref(1) // 1: 确认商品, 2: 填写信息, 3: 兑换成功
const isExchanging = ref(false)

// 收货信息
const deliveryInfo = ref({
  name: '',
  phone: '',
  address: '',
  remark: ''
})

// 计算属性
const totalPoints = computed(() => {
  return props.product ? props.product.points * quantity.value : 0
})

const canExchange = computed(() => {
  return props.userPoints >= totalPoints.value
})

const remainingPoints = computed(() => {
  return props.userPoints - totalPoints.value
})

const isDeliveryInfoValid = computed(() => {
  return deliveryInfo.value.name && 
         deliveryInfo.value.phone && 
         deliveryInfo.value.address
})

// 监听弹窗显示状态
watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetModal()
  }
})

// 重置弹窗状态
const resetModal = () => {
  quantity.value = 1
  exchangeStep.value = 1
  isExchanging.value = false
  deliveryInfo.value = {
    name: '',
    phone: '',
    address: '',
    remark: ''
  }
}

// 关闭弹窗
const closeModal = () => {
  emit('update:visible', false)
  emit('cancel')
}

// 数量调整
const adjustQuantity = (delta: number) => {
  const newQuantity = quantity.value + delta
  if (newQuantity >= 1 && newQuantity <= (props.product?.stock || 0)) {
    quantity.value = newQuantity
  }
}

// 下一步
const nextStep = () => {
  if (exchangeStep.value === 1) {
    exchangeStep.value = 2
  } else if (exchangeStep.value === 2 && isDeliveryInfoValid.value) {
    confirmExchange()
  }
}

// 上一步
const prevStep = () => {
  if (exchangeStep.value === 2) {
    exchangeStep.value = 1
  }
}

// 确认兑换
const confirmExchange = async () => {
  if (!props.product || !canExchange.value) return
  
  isExchanging.value = true
  
  try {
    // 模拟兑换过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    exchangeStep.value = 3
    
    // 发送确认事件
    emit('confirm', {
      product: props.product,
      quantity: quantity.value,
      deliveryInfo: deliveryInfo.value
    })
  } catch (error) {
    console.error('兑换失败:', error)
  } finally {
    isExchanging.value = false
  }
}

// 格式化积分显示
const formatPoints = (points: number) => {
  return points.toLocaleString()
}

// 格式化价格显示
const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}
</script>

<template>
  <div v-if="visible" class="exchange-modal-overlay" @click="closeModal">
    <div class="exchange-modal" @click.stop>
      <!-- 关闭按钮 -->
      <button class="close-btn" @click="closeModal">
        <span class="close-icon">✕</span>
      </button>
      
      <!-- 步骤指示器 -->
      <div class="step-indicator">
        <div class="step" :class="{ active: exchangeStep >= 1, completed: exchangeStep > 1 }">
          <div class="step-number">1</div>
          <div class="step-label">确认商品</div>
        </div>
        <div class="step-line" :class="{ active: exchangeStep > 1 }"></div>
        <div class="step" :class="{ active: exchangeStep >= 2, completed: exchangeStep > 2 }">
          <div class="step-number">2</div>
          <div class="step-label">填写信息</div>
        </div>
        <div class="step-line" :class="{ active: exchangeStep > 2 }"></div>
        <div class="step" :class="{ active: exchangeStep >= 3 }">
          <div class="step-number">3</div>
          <div class="step-label">兑换成功</div>
        </div>
      </div>
      
      <!-- 步骤1: 确认商品 -->
      <div v-if="exchangeStep === 1" class="modal-content">
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="title-icon">🎁</span>
            确认兑换商品
          </h2>
        </div>
        
        <div class="product-details" v-if="product">
          <div class="product-image">
            <div class="image-placeholder">
              {{ product.image }}
            </div>
          </div>
          
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-description">{{ product.description }}</p>
            
            <div class="product-pricing">
              <div class="points-price">
                <span class="points-icon">💎</span>
                <span class="points-value">{{ formatPoints(product.points) }}</span>
                <span class="points-unit">积分/件</span>
              </div>
              
              <div v-if="product.originalPrice" class="original-price">
                市场价：<span class="price-value">{{ formatPrice(product.originalPrice) }}</span>
              </div>
            </div>
            
            <div class="stock-info">
              <span class="stock-label">库存：</span>
              <span class="stock-value">{{ product.stock }}件</span>
            </div>
          </div>
        </div>
        
        <!-- 数量选择 -->
        <div class="quantity-section">
          <div class="quantity-label">兑换数量</div>
          <div class="quantity-controls">
            <button 
              class="quantity-btn"
              :disabled="quantity <= 1"
              @click="adjustQuantity(-1)"
            >
              -
            </button>
            <input 
              v-model.number="quantity" 
              class="quantity-input"
              type="number"
              min="1"
              :max="product?.stock || 0"
            >
            <button 
              class="quantity-btn"
              :disabled="quantity >= (product?.stock || 0)"
              @click="adjustQuantity(1)"
            >
              +
            </button>
          </div>
        </div>
        
        <!-- 积分计算 -->
        <div class="points-calculation">
          <div class="calculation-row">
            <span class="label">单价：</span>
            <span class="value">{{ formatPoints(product?.points || 0) }} 积分</span>
          </div>
          <div class="calculation-row">
            <span class="label">数量：</span>
            <span class="value">{{ quantity }} 件</span>
          </div>
          <div class="calculation-row total">
            <span class="label">总计：</span>
            <span class="value">{{ formatPoints(totalPoints) }} 积分</span>
          </div>
          <div class="calculation-row">
            <span class="label">我的积分：</span>
            <span class="value">{{ formatPoints(userPoints) }} 积分</span>
          </div>
          <div class="calculation-row" :class="{ insufficient: !canExchange }">
            <span class="label">兑换后余额：</span>
            <span class="value">{{ formatPoints(remainingPoints) }} 积分</span>
          </div>
        </div>
        
        <!-- 不足提示 -->
        <div v-if="!canExchange" class="insufficient-notice">
          <span class="notice-icon">⚠️</span>
          <span class="notice-text">积分不足，无法完成兑换</span>
        </div>
      </div>
      
      <!-- 步骤2: 填写信息 -->
      <div v-if="exchangeStep === 2" class="modal-content">
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="title-icon">📝</span>
            填写收货信息
          </h2>
        </div>
        
        <div class="delivery-form">
          <div class="form-group">
            <label class="form-label">收货人姓名 *</label>
            <input 
              v-model="deliveryInfo.name"
              class="form-input"
              type="text"
              placeholder="请输入收货人姓名"
            >
          </div>
          
          <div class="form-group">
            <label class="form-label">联系电话 *</label>
            <input 
              v-model="deliveryInfo.phone"
              class="form-input"
              type="tel"
              placeholder="请输入联系电话"
            >
          </div>
          
          <div class="form-group">
            <label class="form-label">收货地址 *</label>
            <textarea 
              v-model="deliveryInfo.address"
              class="form-textarea"
              placeholder="请输入详细的收货地址"
              rows="3"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">备注信息</label>
            <textarea 
              v-model="deliveryInfo.remark"
              class="form-textarea"
              placeholder="如有特殊要求请在此填写（选填）"
              rows="2"
            ></textarea>
          </div>
        </div>
        
        <!-- 兑换摘要 -->
        <div class="exchange-summary">
          <div class="summary-title">兑换摘要</div>
          <div class="summary-item">
            <span class="item-label">商品：</span>
            <span class="item-value">{{ product?.name }} × {{ quantity }}</span>
          </div>
          <div class="summary-item">
            <span class="item-label">消耗积分：</span>
            <span class="item-value">{{ formatPoints(totalPoints) }} 积分</span>
          </div>
        </div>
      </div>
      
      <!-- 步骤3: 兑换成功 -->
      <div v-if="exchangeStep === 3" class="modal-content success">
        <div class="success-animation">
          <div class="success-icon">✅</div>
          <div class="success-title">兑换成功！</div>
          <div class="success-description">您的商品正在准备中，我们会尽快为您发货</div>
        </div>
        
        <div class="success-details">
          <div class="detail-item">
            <span class="detail-label">兑换商品：</span>
            <span class="detail-value">{{ product?.name }} × {{ quantity }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">消耗积分：</span>
            <span class="detail-value">{{ formatPoints(totalPoints) }} 积分</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">剩余积分：</span>
            <span class="detail-value">{{ formatPoints(remainingPoints) }} 积分</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">预计发货：</span>
            <span class="detail-value">1-3个工作日</span>
          </div>
        </div>
        
        <div class="success-tips">
          <div class="tip-item">
            <span class="tip-icon">📱</span>
            <span class="tip-text">您可以在"我的订单"中查看兑换记录</span>
          </div>
          <div class="tip-item">
            <span class="tip-icon">🚚</span>
            <span class="tip-text">物流信息将通过短信通知您</span>
          </div>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="modal-actions">
        <template v-if="exchangeStep === 1">
          <button class="action-btn secondary" @click="closeModal">
            取消
          </button>
          <button 
            class="action-btn primary"
            :disabled="!canExchange"
            @click="nextStep"
          >
            下一步
          </button>
        </template>
        
        <template v-if="exchangeStep === 2">
          <button class="action-btn secondary" @click="prevStep">
            上一步
          </button>
          <button 
            class="action-btn primary"
            :disabled="!isDeliveryInfoValid || isExchanging"
            @click="nextStep"
          >
            <span v-if="isExchanging" class="loading-spinner"></span>
            {{ isExchanging ? '兑换中...' : '确认兑换' }}
          </button>
        </template>
        
        <template v-if="exchangeStep === 3">
          <button class="action-btn primary full" @click="closeModal">
            完成
          </button>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.exchange-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.exchange-modal {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.2);
}

.close-icon {
  font-size: 16px;
  color: #666;
}

/* 步骤指示器 */
.step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px 32px 0;
  margin-bottom: 24px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.step-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e8ecf4;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.step.active .step-number {
  background: #667eea;
  color: white;
}

.step.completed .step-number {
  background: #27ae60;
  color: white;
}

.step-label {
  font-size: 12px;
  color: #999;
  font-weight: 500;
}

.step.active .step-label {
  color: #667eea;
}

.step.completed .step-label {
  color: #27ae60;
}

.step-line {
  flex: 1;
  height: 2px;
  background: #e8ecf4;
  margin: 0 16px;
  transition: all 0.3s ease;
}

.step-line.active {
  background: #667eea;
}

/* 模态框内容 */
.modal-content {
  padding: 0 32px 24px;
}

.modal-header {
  margin-bottom: 24px;
}

.modal-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 24px;
}

/* 商品详情 */
.product-details {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9ff;
  border-radius: 12px;
}

.product-image {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #f0f2ff 0%, #e8ecf4 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.image-placeholder {
  font-size: 32px;
  opacity: 0.8;
}

.product-info {
  flex: 1;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.product-description {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.product-pricing {
  margin-bottom: 12px;
}

.points-price {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.points-icon {
  font-size: 16px;
}

.points-value {
  font-size: 20px;
  font-weight: 700;
  color: #667eea;
}

.points-unit {
  font-size: 12px;
  color: #667eea;
  font-weight: 500;
}

.original-price {
  font-size: 12px;
  color: #999;
}

.price-value {
  text-decoration: line-through;
}

.stock-info {
  font-size: 14px;
  color: #666;
}

.stock-label {
  font-weight: 500;
}

.stock-value {
  color: #27ae60;
  font-weight: 600;
}

/* 数量选择 */
.quantity-section {
  margin-bottom: 24px;
}

.quantity-label {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 8px;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #e8ecf4;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  color: #667eea;
  transition: all 0.3s ease;
}

.quantity-btn:hover:not(:disabled) {
  border-color: #667eea;
  background: #f8f9ff;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-input {
  width: 60px;
  height: 32px;
  border: 1px solid #e8ecf4;
  border-radius: 6px;
  text-align: center;
  font-size: 14px;
  font-weight: 500;
}

.quantity-input:focus {
  outline: none;
  border-color: #667eea;
}

/* 积分计算 */
.points-calculation {
  background: white;
  border: 1px solid #e8ecf4;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.calculation-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
  font-size: 14px;
}

.calculation-row.total {
  border-top: 1px solid #e8ecf4;
  margin-top: 8px;
  padding-top: 12px;
  font-weight: 600;
  font-size: 16px;
}

.calculation-row.insufficient {
  color: #e74c3c;
}

.label {
  color: #666;
}

.value {
  color: #2c3e50;
  font-weight: 500;
}

.calculation-row.total .value {
  color: #667eea;
}

/* 不足提示 */
.insufficient-notice {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(231, 76, 60, 0.1);
  border: 1px solid rgba(231, 76, 60, 0.2);
  border-radius: 8px;
  color: #e74c3c;
  font-size: 14px;
}

.notice-icon {
  font-size: 16px;
}

/* 收货信息表单 */
.delivery-form {
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 6px;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e8ecf4;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 60px;
}

/* 兑换摘要 */
.exchange-summary {
  background: #f8f9ff;
  border-radius: 8px;
  padding: 16px;
}

.summary-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
  font-size: 14px;
}

.item-label {
  color: #666;
}

.item-value {
  color: #2c3e50;
  font-weight: 500;
}

/* 成功页面 */
.modal-content.success {
  text-align: center;
}

.success-animation {
  margin-bottom: 32px;
}

.success-icon {
  font-size: 64px;
  margin-bottom: 16px;
  animation: bounce 0.6s ease-in-out;
}

@keyframes bounce {
  0%, 20%, 53%, 80%, 100% {
    transform: translate3d(0, 0, 0);
  }
  40%, 43% {
    transform: translate3d(0, -20px, 0);
  }
  70% {
    transform: translate3d(0, -10px, 0);
  }
  90% {
    transform: translate3d(0, -4px, 0);
  }
}

.success-title {
  font-size: 24px;
  font-weight: 600;
  color: #27ae60;
  margin-bottom: 8px;
}

.success-description {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.success-details {
  background: #f8f9ff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  text-align: left;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 14px;
  border-bottom: 1px solid #e8ecf4;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  color: #666;
}

.detail-value {
  color: #2c3e50;
  font-weight: 500;
}

.success-tips {
  text-align: left;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  font-size: 13px;
  color: #666;
}

.tip-icon {
  font-size: 16px;
}

/* 操作按钮 */
.modal-actions {
  display: flex;
  gap: 12px;
  padding: 24px 32px 32px;
  border-top: 1px solid #e8ecf4;
}

.action-btn {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.action-btn.secondary {
  background: #f8f9ff;
  color: #667eea;
  border: 1px solid #e8ecf4;
}

.action-btn.secondary:hover {
  background: #f0f2ff;
  border-color: #667eea;
}

.action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.action-btn.primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
}

.action-btn.full {
  flex: none;
  width: 100%;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exchange-modal {
    margin: 10px;
    max-width: none;
  }
  
  .step-indicator {
    padding: 20px 16px 0;
  }
  
  .step-line {
    margin: 0 8px;
  }
  
  .modal-content {
    padding: 0 20px 20px;
  }
  
  .product-details {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .product-image {
    width: 100px;
    height: 100px;
  }
  
  .modal-actions {
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .exchange-modal-overlay {
    padding: 10px;
  }
  
  .step-indicator {
    padding: 16px 12px 0;
  }
  
  .step-number {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }
  
  .step-label {
    font-size: 11px;
  }
  
  .modal-content {
    padding: 0 16px 16px;
  }
  
  .modal-title {
    font-size: 18px;
  }
  
  .product-image {
    width: 80px;
    height: 80px;
  }
  
  .image-placeholder {
    font-size: 24px;
  }
  
  .product-name {
    font-size: 16px;
  }
  
  .points-value {
    font-size: 18px;
  }
  
  .modal-actions {
    padding: 16px;
    flex-direction: column;
  }
  
  .action-btn {
    padding: 10px 20px;
  }
}
</style>