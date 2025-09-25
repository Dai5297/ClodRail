<script setup lang="ts">
import { ref, onMounted, computed, watch, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElButton } from 'element-plus'
import ProgressIndicator from './components/ProgressIndicator.vue'
import OrderInfo from './components/OrderInfo.vue'
import PaymentMethods from './components/PaymentMethods.vue'
import PaymentConfirmModal from './components/PaymentConfirmModal.vue'
import PaymentSuccessModal from './components/PaymentSuccessModal.vue'

const route = useRoute()
const router = useRouter()

// 页面状态
const loading = ref(false)
const showConfirmModal = ref(false)
const showSuccessModal = ref(false)
const paymentCountdown = ref(900) // 15分钟倒计时
const countdownInterval = ref<number | null>(null)

// 订单信息
const orderInfo = ref({
  trainNumber: 'G1234',
  departure: '北京南',
  arrival: '上海虹桥',
  departureTime: '08:00',
  arrivalTime: '12:30',
  date: '2024-01-15',
  duration: '4小时30分',
  passengers: [
    {
      name: '张三',
      idCard: '110101199001011234',
      ticketType: '成人票',
      seatType: '二等座',
      seatNumber: '05车06A',
      price: 553
    }
  ],
  insurance: 30,
  serviceFee: 5,
  totalAmount: 588
})

// 支付方式
const selectedPaymentMethod = ref('alipay')
const availableCoupons = ref([
  { id: 1, name: '新用户优惠券', discount: 20, minAmount: 500 },
  { id: 2, name: '春节特惠券', discount: 50, minAmount: 800 }
])
const selectedCoupon = ref<number | null>(null)

// 计算最终金额
const finalAmount = computed(() => {
  let amount = orderInfo.value.totalAmount
  if (selectedCoupon.value) {
    const coupon = availableCoupons.value.find(c => c.id === selectedCoupon.value)
    if (coupon) {
      amount -= coupon.discount
    }
  }
  return Math.max(amount, 0)
})

// 格式化倒计时
const formattedCountdown = computed(() => {
  const minutes = Math.floor(paymentCountdown.value / 60)
  const seconds = paymentCountdown.value % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

// 开始倒计时
const startCountdown = () => {
  if (countdownInterval.value) {
    clearInterval(countdownInterval.value)
  }

  countdownInterval.value = setInterval(() => {
    paymentCountdown.value--
    if (paymentCountdown.value <= 0) {
      clearInterval(countdownInterval.value!)
      ElMessage.error('支付超时，请重新下单')
      router.push('/ticket')
    }
  }, 1000)
}

// 处理支付方式选择
const handlePaymentMethodChange = (method: string) => {
  selectedPaymentMethod.value = method
}

// 处理优惠券选择
const handleCouponSelect = (couponId: number | null) => {
  selectedCoupon.value = couponId
}

// 处理支付
const handlePayment = () => {
  if (!selectedPaymentMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }

  showConfirmModal.value = true
}

// 确认支付
const confirmPayment = () => {
  showConfirmModal.value = false
  loading.value = true

  // 模拟支付处理
  setTimeout(() => {
    loading.value = false
    showSuccessModal.value = true

    // 清除倒计时
    if (countdownInterval.value) {
      clearInterval(countdownInterval.value)
    }
  }, 3000)
}

// 支付成功后处理
const handlePaymentSuccess = (action: 'home' | 'orders') => {
  showSuccessModal.value = false
  if (action === 'home') {
    router.push('/')
  } else {
    router.push('/my-orders')
  }
}

// 页面初始化
onMounted(() => {
  startCountdown()
})

// 组件卸载时清除定时器
onBeforeUnmount(() => {
  if (countdownInterval.value) {
    clearInterval(countdownInterval.value)
  }
})
</script>

<template>
  <div class="payment-page">
    <div class="container">
      <!-- 进度指示器 -->
      <ProgressIndicator :current-step="2" />

      <!-- 支付倒计时 -->
      <div class="countdown-bar">
        <div class="countdown-content">
          <span class="countdown-text">请在</span>
          <span class="countdown-time">{{ formattedCountdown }}</span>
          <span class="countdown-text">内完成支付</span>
        </div>
      </div>

      <div class="payment-content">
        <!-- 订单信息 -->
        <div class="left-section">
          <OrderInfo :order="orderInfo" />
        </div>

        <!-- 支付区域 -->
        <div class="right-section">
          <PaymentMethods
            :selected-method="selectedPaymentMethod"
            :available-coupons="availableCoupons"
            :selected-coupon="selectedCoupon"
            :final-amount="finalAmount"
            @method-change="handlePaymentMethodChange"
            @coupon-select="handleCouponSelect"
            @payment="handlePayment"
          />
        </div>
      </div>
    </div>

    <!-- 支付确认模态框 -->
    <PaymentConfirmModal
      :visible="showConfirmModal"
      :payment-method="selectedPaymentMethod"
      :amount="finalAmount"
      :loading="loading"
      @confirm="confirmPayment"
    />

    <!-- 支付成功模态框 -->
    <PaymentSuccessModal
      :visible="showSuccessModal"
      :order-number="'202401150001'"
      @action="handlePaymentSuccess"
    />
  </div>
</template>

<style scoped>
.payment-page {
  min-height: calc(100vh - 60px);
  background: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.countdown-bar {
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 8px;
  padding: 12px 20px;
  margin-bottom: 20px;
  text-align: center;
}

.countdown-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.countdown-text {
  font-size: 14px;
  color: #856404;
}

.countdown-time {
  font-size: 18px;
  font-weight: bold;
  color: #dc3545;
  font-family: 'Courier New', monospace;
}

.payment-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 20px;
  align-items: start;
}

.left-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.right-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: sticky;
  top: 20px;
}

@media (max-width: 1024px) {
  .payment-content {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .right-section {
    position: static;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }

  .countdown-content {
    flex-direction: column;
    gap: 4px;
  }

  .countdown-time {
    font-size: 20px;
  }
}
</style>
