<template>
  <div class="payment-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <el-button 
          type="text" 
          @click="goBack" 
          class="back-btn"
        >
          <el-icon><ArrowLeft /></el-icon>
          返回订单详情
        </el-button>
        <h2 class="page-title">订单支付</h2>
      </div>
    </div>

    <!-- 支付进度条 -->
    <div class="progress-section">
      <div class="container">
        <el-steps :active="currentStep" align-center>
          <el-step title="选择车票" icon="Tickets" />
          <el-step title="填写信息" icon="User" />
          <el-step title="确认支付" icon="CreditCard" />
          <el-step title="支付完成" icon="Check" />
        </el-steps>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 主要内容 -->
    <div v-else class="main-content">
      <div class="container">
        <div class="payment-layout">
          <!-- 左侧：订单信息 -->
          <div class="order-section">
            <!-- 订单详情卡片 -->
            <div class="order-info-card">
              <h3 class="card-title">
                <el-icon><Document /></el-icon>
                订单信息
              </h3>
              
              <div class="order-details">
                <div class="train-info">
                  <div class="train-header">
                    <span class="train-number">{{ orderInfo.trainCode }}</span>
                    <span class="train-date">{{ formatDate(orderInfo.date) }}</span>
                  </div>
                  <div class="route-info">
                    <div class="route-item">
                      <div class="time-display">
                        <span class="time">{{ formatTime(orderInfo.startTime) }}</span>
                        <span class="date">{{ formatDate(orderInfo.startTime) }}</span>
                      </div>
                      <span class="station">{{ orderInfo.originStation }}</span>
                    </div>
                    <div class="route-arrow">
                      <el-icon><ArrowRight /></el-icon>
                    </div>
                    <div class="route-item">
                      <div class="time-display">
                        <span class="time">{{ formatTime(orderInfo.endTime) }}</span>
                        <span class="date">{{ formatDate(orderInfo.endTime) }}</span>
                      </div>
                      <span class="station">{{ orderInfo.destinationStation }}</span>
                    </div>
                  </div>
                </div>

                <div class="passenger-info">
                  <h4 class="section-title">乘客信息</h4>
                  <div class="passenger-list">
                    <div 
                      v-for="(passenger, index) in orderInfo.passengers" 
                      :key="index"
                      class="passenger-item"
                    >
                      <div class="passenger-details">
                        <span class="passenger-name">{{ passenger.name }}</span>
                        <span class="seat-info">{{ orderInfo.seatTypeName }}</span>
                      </div>
                      <div class="passenger-meta">
                        <span class="id-info">{{ passenger.idType }} {{ passenger.idNumber }}</span>
                        <span class="price">￥{{ orderInfo.ticketPrice }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="order-summary">
                  <div class="summary-item">
                    <span class="label">订单号：</span>
                    <span class="value">{{ orderInfo.orderNumber }}</span>
                  </div>
                  <div class="summary-item">
                    <span class="label">座位类型：</span>
                    <span class="value">{{ orderInfo.seatTypeName }}</span>
                  </div>
                  <div class="summary-item">
                    <span class="label">乘客数量：</span>
                    <span class="value">{{ orderInfo.passengers.length }}人</span>
                  </div>
                  <div class="summary-item total">
                    <span class="label">应付金额：</span>
                    <span class="value">￥{{ orderInfo.totalAmount }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 支付倒计时 -->
            <div class="countdown-card">
              <div class="countdown-content">
                <el-icon class="countdown-icon"><Clock /></el-icon>
                <div class="countdown-text">
                  <div class="countdown-title">请在以下时间内完成支付</div>
                  <div class="countdown-timer">
                    <span class="time-number">{{ formatCountdown(remainingTime) }}</span>
                  </div>
                  <div class="countdown-tip">超时订单将自动取消</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧：支付方式 -->
          <div class="payment-section">
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
                  :class="{ 'selected': selectedPaymentMethod?.id === method.id }"
                  @click="selectPaymentMethod(method)"
                >
                  <div class="method-icon">
                    <img :src="method.icon" :alt="method.name" />
                  </div>
                  <div class="method-info">
                    <div class="method-name">{{ method.name }}</div>
                    <div class="method-desc">{{ method.description }}</div>
                  </div>
                  <div class="method-radio">
                    <el-radio 
                      :model-value="selectedPaymentMethod?.id === method.id"
                      @change="selectPaymentMethod(method)"
                    />
                  </div>
                </div>
              </div>

              <!-- 支付按钮 -->
              <div class="payment-actions">
                <el-button 
                  type="primary" 
                  size="large" 
                  @click="processPayment"
                  :disabled="!selectedPaymentMethod || processing"
                  :loading="processing"
                  class="pay-button"
                >
                  <el-icon><CreditCard /></el-icon>
                  立即支付 ￥{{ orderInfo.totalAmount }}
                </el-button>
                
                <div class="payment-security">
                  <el-icon><Lock /></el-icon>
                  <span>支付环境安全，信息已加密</span>
                </div>
              </div>
            </div>

            <!-- 支付说明 -->
            <div class="payment-notice-card">
              <h4 class="notice-title">
                <el-icon><InfoFilled /></el-icon>
                支付说明
              </h4>
              <ul class="notice-list">
                <li>请在{{ Math.floor(remainingTime / 60) }}分钟内完成支付，超时订单将自动取消</li>
                <li>支付成功后，车票信息将发送至您的手机和邮箱</li>
                <li>如遇支付问题，请联系客服：400-123-4567</li>
                <li>退票改签请遵循铁路部门相关规定</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付成功弹窗 -->
    <el-dialog
      v-model="showSuccessModal"
      title="支付成功"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <div class="success-modal">
        <div class="success-icon">
          <el-icon size="60" color="#52c41a"><CircleCheck /></el-icon>
        </div>
        <div class="success-content">
          <h3 class="success-title">支付成功！</h3>
          <p class="success-message">
            您的订单已支付成功，车票信息已发送至您的手机和邮箱
          </p>
          <div class="success-info">
            <div class="info-item">
              <span class="label">订单号：</span>
              <span class="value">{{ orderInfo.orderNumber }}</span>
            </div>
            <div class="info-item">
              <span class="label">支付金额：</span>
              <span class="value">￥{{ orderInfo.totalAmount }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="success-actions">
          <el-button @click="goToOrders">查看订单</el-button>
          <el-button type="primary" @click="goToHome">返回首页</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Document, ArrowRight, Clock, CreditCard, Lock, 
  InfoFilled, CircleCheck, Tickets, User, Check
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(true)
const processing = ref(false)
const showSuccessModal = ref(false)
const currentStep = ref(2) // 当前在支付步骤
const remainingTime = ref(15 * 60) // 15分钟倒计时（秒）
const timer = ref(null)

// 订单信息
const orderInfo = ref({
  orderId: route.query.orderId,
  orderNumber: '',
  trainCode: '',
  date: '',
  startTime: '',
  endTime: '',
  originStation: '',
  destinationStation: '',
  seatTypeName: '',
  ticketPrice: 0,
  totalAmount: 0,
  passengers: []
})

// 支付方式
const paymentMethods = ref([
  {
    id: 'alipay',
    name: '支付宝',
    description: '推荐使用，支付快捷安全',
    icon: '/images/alipay.png'
  },
  {
    id: 'wechat',
    name: '微信支付',
    description: '微信用户首选支付方式',
    icon: '/images/wechat-pay.png'
  },
  {
    id: 'unionpay',
    name: '银联支付',
    description: '支持各大银行卡支付',
    icon: '/images/unionpay.png'
  },
  {
    id: 'credit-card',
    name: '信用卡支付',
    description: '支持Visa、MasterCard等',
    icon: '/images/credit-card.png'
  }
])

const selectedPaymentMethod = ref(null)

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  const weekday = weekdays[date.getDay()]
  return `${year}-${month}-${day} 周${weekday}`
}

// 格式化时间显示
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

// 格式化倒计时
const formatCountdown = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 获取订单信息
const loadOrderInfo = async () => {
  try {
    loading.value = true
    // 这里应该调用获取订单详情的API
    // const response = await request.get(`/orders/${orderInfo.value.orderId}`)
    
    // 模拟订单数据
    const mockOrderData = {
      orderNumber: 'TK' + Date.now(),
      trainCode: 'G1234',
      date: '2024-01-15',
      startTime: '08:30',
      endTime: '14:20',
      originStation: '北京南',
      destinationStation: '上海虹桥',
      seatTypeName: '二等座',
      ticketPrice: 553,
      totalAmount: 1106,
      passengers: [
        {
          name: '张三',
          idType: '身份证',
          idNumber: '110101199001011234'
        },
        {
          name: '李四',
          idType: '身份证',
          idNumber: '110101199002021234'
        }
      ]
    }
    
    Object.assign(orderInfo.value, mockOrderData)
    
  } catch (error) {
    console.error('获取订单信息失败:', error)
    ElMessage.error('获取订单信息失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 选择支付方式
const selectPaymentMethod = (method) => {
  selectedPaymentMethod.value = method
}

// 处理支付
const processPayment = async () => {
  if (!selectedPaymentMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }

  try {
    processing.value = true
    
    // 模拟支付处理
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 这里应该调用支付API
    // const response = await request.post('/payments', {
    //   orderId: orderInfo.value.orderId,
    //   paymentMethod: selectedPaymentMethod.value.id,
    //   amount: orderInfo.value.totalAmount
    // })
    
    // 模拟支付成功
    showSuccessModal.value = true
    currentStep.value = 3
    
    // 停止倒计时
    if (timer.value) {
      clearInterval(timer.value)
    }
    
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败，请重试')
  } finally {
    processing.value = false
  }
}

// 前往订单页面
const goToOrders = () => {
  router.push('/orders')
}

// 返回首页
const goToHome = () => {
  router.push('/')
}

// 启动倒计时
const startCountdown = () => {
  timer.value = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      // 倒计时结束，订单超时
      clearInterval(timer.value)
      ElMessageBox.alert('订单已超时，请重新下单', '订单超时', {
        confirmButtonText: '确定',
        callback: () => {
          router.push('/tickets')
        }
      })
    }
  }, 1000)
}

// 组件挂载时初始化
onMounted(async () => {
  // 验证订单ID
  if (!orderInfo.value.orderId) {
    ElMessage.error('缺少订单信息')
    router.back()
    return
  }
  
  await loadOrderInfo()
  
  // 默认选择第一个支付方式
  if (paymentMethods.value.length > 0) {
    selectedPaymentMethod.value = paymentMethods.value[0]
  }
  
  // 启动倒计时
  startCountdown()
})

// 组件卸载时清理定时器
onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})
</script>

<style scoped>
.payment-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.page-header {
  background: white;
  padding: 20px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-btn {
  color: #1890ff;
  font-size: 14px;
  margin-bottom: 10px;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.progress-section {
  background: white;
  padding: 30px 0;
  border-bottom: 1px solid #f0f0f0;
}

.loading-container {
  padding: 40px 20px;
}

.main-content {
  padding: 30px 20px;
}

.payment-layout {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 30px;
}

/* 订单信息区域 */
.order-info-card,
.countdown-card,
.payment-methods-card,
.payment-notice-card {
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

.train-info {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.train-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.train-number {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.train-date {
  color: #666;
  font-size: 14px;
}

.route-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.route-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.time-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 4px;
}

.time {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 1;
}

.date {
  font-size: 10px;
  color: #999;
  line-height: 1;
  margin-top: 2px;
}

.station {
  font-size: 14px;
  color: #666;
}

.route-arrow {
  color: #1890ff;
}

.section-title {
  font-size: 16px;
  color: #333;
  margin: 0 0 12px 0;
}

.passenger-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.passenger-item {
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.passenger-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.passenger-name {
  font-weight: 600;
  color: #333;
}

.seat-info {
  color: #1890ff;
  font-size: 14px;
}

.passenger-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
}

.price {
  color: #ff6b35;
  font-weight: 600;
}

.order-summary {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.summary-item.total {
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  margin-top: 8px;
  font-size: 16px;
  font-weight: 600;
}

.label {
  color: #666;
}

.value {
  color: #333;
}

.summary-item.total .value {
  color: #ff6b35;
  font-size: 20px;
}

/* 倒计时卡片 */
.countdown-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.countdown-icon {
  font-size: 32px;
  color: #ff6b35;
}

.countdown-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.countdown-timer {
  font-size: 24px;
  font-weight: 600;
  color: #ff6b35;
  margin-bottom: 4px;
}

.countdown-tip {
  font-size: 12px;
  color: #999;
}

/* 支付方式 */
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

/* 支付说明 */
.notice-title {
  font-size: 14px;
  color: #333;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.notice-list {
  margin: 0;
  padding-left: 16px;
  color: #666;
  font-size: 12px;
  line-height: 1.6;
}

.notice-list li {
  margin-bottom: 4px;
}

/* 支付成功弹窗 */
.success-modal {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  margin-bottom: 20px;
}

.success-title {
  font-size: 20px;
  color: #333;
  margin: 0 0 12px 0;
}

.success-message {
  color: #666;
  margin: 0 0 20px 0;
  line-height: 1.5;
}

.success-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;
  background: #fafafa;
  padding: 16px;
  border-radius: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
}

.success-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .payment-layout {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .route-info {
    flex-direction: column;
    gap: 12px;
  }
  
  .passenger-details,
  .passenger-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>