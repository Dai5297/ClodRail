<template>
  <div class="payment-success-page">
    <div class="container">
      <!-- 支付结果卡片 -->
      <PaymentResultCard
        :payment-success="paymentSuccess"
        :result-message="resultMessage"
        :order-info="orderInfo"
        :loading="loading"
        @go-to-orders="goToOrders"
        @go-to-home="goToHome"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import PaymentResultCard from './components/PaymentResultCard.vue'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(true)
const paymentSuccess = ref(false)
const resultMessage = ref('正在确认支付结果...')
const orderInfo = ref(null)

// 前往订单页面
const goToOrders = () => {
  router.push('/orders')
}

// 返回首页
const goToHome = () => {
  router.push('/')
}

// 处理支付宝返回的参数
const processAlipayReturn = () => {
  try {
    loading.value = true

    // 获取URL中的所有查询参数
    const queryParams = route.query
    
    console.log('=== 支付宝回调参数 ===')
    console.log('所有参数:', queryParams)
    console.log('==================')

    // 支付宝返回的关键参数
    const {
      out_trade_no,  // 商户订单号
      trade_no,      // 支付宝交易号
      total_amount,  // 交易金额
      timestamp,     // 时间戳
      trade_status,  // 交易状态
      sign,          // 签名
    } = queryParams

    // 验证必要参数
    if (!out_trade_no) {
      console.error('缺少订单号参数')
      paymentSuccess.value = false
      resultMessage.value = '支付结果获取失败，请查看订单状态'
      loading.value = false
      return
    }

    // 设置订单信息
    orderInfo.value = {
      out_trade_no,
      trade_no,
      total_amount,
      timestamp,
      trade_status
    }

    // 判断支付状态
    // TRADE_SUCCESS 或 TRADE_FINISHED 表示支付成功
    if (trade_status === 'TRADE_SUCCESS' || trade_status === 'TRADE_FINISHED') {
      paymentSuccess.value = true
      resultMessage.value = '您的订单已支付成功，感谢您的购买！'
      ElMessage.success('支付成功！')
    } else {
      paymentSuccess.value = false
      resultMessage.value = `支付状态：${trade_status || '未知'}`
      ElMessage.warning('支付状态异常，请查看订单详情')
    }

    loading.value = false
  } catch (error) {
    console.error('处理支付结果失败:', error)
    paymentSuccess.value = false
    resultMessage.value = '支付结果处理失败，请查看订单状态'
    loading.value = false
  }
}

// 组件挂载时初始化
onMounted(() => {
  // 延迟一下，让加载动画显示得更自然
  setTimeout(() => {
    processAlipayReturn()
  }, 500)
})
</script>

<style scoped>
.payment-success-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.container {
  max-width: 600px;
  width: 100%;
}
</style>

