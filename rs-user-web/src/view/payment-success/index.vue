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
import { getOrderDetail } from '@/api/order'

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

// 处理支付宝返回的参数并查询订单状态
const processAlipayReturn = async () => {
  try {
    loading.value = true

    // 获取URL中的所有查询参数
    const queryParams = route.query
    

    // 支付宝返回的关键参数
    const {
      out_trade_no,  // 商户订单号
      trade_no,      // 支付宝交易号
      total_amount,  // 交易金额
      timestamp,     // 时间戳
      trade_status,  // 交易状态（可能不存在）
    } = queryParams

    // 验证必要参数
    if (!out_trade_no) {
      paymentSuccess.value = false
      resultMessage.value = '支付结果获取失败，请查看订单状态'
      loading.value = false
      return
    }

    // 🔥 关键修复：调用后端接口查询订单实际状态
    
    // 等待一下，让后端异步回调有时间处理
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const response = await getOrderDetail(out_trade_no)
    
    // 响应拦截器返回的数据结构：{ success, data, code, message }
    const orderDetail = response.data

    // 设置订单信息（优先使用后端返回的数据）
    orderInfo.value = {
      out_trade_no: orderDetail.orderId || out_trade_no,
      trade_no: trade_no || '---',
      total_amount: orderDetail.totalAmount || total_amount,
      timestamp: timestamp || new Date().toISOString(),
      trade_status: orderDetail.status,
      createTime: orderDetail.createTime,
      payTime: orderDetail.payTime,
      passengers: orderDetail.passengers,
    }

    // 判断支付状态（根据后端返回的订单状态）
    // 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
    if (orderDetail.status === 1) {
      paymentSuccess.value = true
      resultMessage.value = '您的订单已支付成功，感谢您的购买！'
      ElMessage.success('支付成功！')
    } else if (orderDetail.status === 0) {
      paymentSuccess.value = false
      resultMessage.value = '订单尚未支付成功，请稍后查看订单状态'
      ElMessage.warning('支付处理中，请稍后查看订单状态')
    } else {
      paymentSuccess.value = false
      resultMessage.value = `订单状态异常：${getOrderStatusText(orderDetail.status)}`
      ElMessage.error('订单状态异常')
    }

    loading.value = false
  } catch (error) {
    paymentSuccess.value = false
    resultMessage.value = '支付结果处理失败，请查看订单状态'
    loading.value = false
    ElMessage.error('查询订单状态失败')
  }
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款'
  }
  return statusMap[status] || '未知状态'
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

