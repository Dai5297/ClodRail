<template>
  <div class="order-success-page">
    <div class="container">
      <!-- 成功状态 -->
      <SuccessHeader />

      <!-- 订单信息 -->
      <OrderInfoCard :order-info="orderInfo" />

      <!-- 车票信息 -->
      <TicketInfoCard :ticket-info="ticketInfo" />

      <!-- 温馨提示 -->
      <TipsCard />

      <!-- 操作按钮 -->
      <ActionButtons :order-id="orderInfo.orderId" />

      <!-- 推荐服务 -->
      <RecommendServices />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import SuccessHeader from './components/SuccessHeader.vue'
import OrderInfoCard from './components/OrderInfoCard.vue'
import TicketInfoCard from './components/TicketInfoCard.vue'
import TipsCard from './components/TipsCard.vue'
import ActionButtons from './components/ActionButtons.vue'
import RecommendServices from './components/RecommendServices.vue'

const route = useRoute()

// 订单信息
const orderInfo = ref({
  orderId: '',
  orderNumber: '',
  amount: 0,
  paymentTime: ''
})

// 车票信息
const ticketInfo = ref({
  trainNumber: '',
  departure: '',
  destination: '',
  date: '',
  departureTime: '',
  arrivalTime: '',
  duration: '',
  seatType: '',
  passengers: [] as Array<{
    name: string
    type: string
    seatNumber: string
  }>
})

// 初始化
onMounted(() => {
  const query = route.query
  
  orderInfo.value = {
    orderId: query.orderId as string,
    orderNumber: query.orderNumber as string,
    amount: Number(query.amount) || 0,
    paymentTime: new Date().toLocaleString('zh-CN')
  }
  
  // 模拟车票信息
  ticketInfo.value = {
    trainNumber: 'G1234',
    departure: '北京南',
    destination: '上海虹桥',
    date: '2024-01-15',
    departureTime: '08:00',
    arrivalTime: '12:30',
    duration: '4小时30分',
    seatType: '二等座',
    passengers: [
      {
        name: '张三',
        type: '成人',
        seatNumber: '01A'
      }
    ]
  }
})
</script>

<style scoped>
.order-success-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }
}
</style>
