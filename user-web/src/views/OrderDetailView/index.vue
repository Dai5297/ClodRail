<script setup lang="ts">
// OrderDetailView - 订单详情页面主文件
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { ref, onMounted, computed, withDefaults } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElButton } from 'element-plus'
import { ArrowLeft, Download, Share } from '@element-plus/icons-vue'
import OrderHeader from './components/OrderHeader.vue'
import TripDetails from './components/TripDetails.vue'
import PassengerDetails from './components/PassengerDetails.vue'
import PaymentDetails from './components/PaymentDetails.vue'
import OrderTimeline from './components/OrderTimeline.vue'
import ActionButtons from './components/ActionButtons.vue'

const route = useRoute()
const router = useRouter()

// 页面状态
const loading = ref(false)
const orderId = computed(() => route.params.id as string)

// 订单数据
const orderDetail = ref({
  id: '20241201001',
  orderNumber: 'TK20241201001',
  status: 'paid',
  trainNumber: 'G1234',
  trainType: 'G',
  departure: '北京南',
  arrival: '上海虹桥',
  departureTime: '2024-12-01 08:00:00',
  arrivalTime: '2024-12-01 12:30:00',
  departureDate: '2024-12-01',
  duration: '4小时30分',
  seatType: '二等座',
  carNumber: '05',
  seatNumber: '05车06A',
  seatNumbers: ['05车06A'],
  distance: '1318公里',
  departureStation: '北京南',
  arrivalStation: '上海虹桥',
  totalAmount: 553.0,
  passengers: [
    {
      id: '1',
      name: '张三',
      idType: '身份证',
      idNumber: '110101199001011234',
      phone: '138****5678',
      passengerType: 'adult',
      ticketType: '成人票',
      seatNumber: '05车06A',
      ticketPrice: 553.0
    }
  ],
  ticketPrice: 553.0,
  serviceFee: 0,
  insuranceFee: 0,
  createTime: '2024-12-01 14:30:00',
  payTime: '2024-12-01 14:32:15',
  payMethod: '支付宝',
  contactPhone: '138****5678',
  contactEmail: 'user@example.com',
  paymentInfo: {
    paymentMethod: 'alipay',
    paymentChannel: 'alipay_app',
    transactionId: '2024120114321512345',
    paymentTime: '2024-12-01 14:32:15',
    paymentAmount: 553.0,
    discountAmount: 0,
    actualAmount: 553.0,
    paymentStatus: 'success'
  },
  timeline: [
    {
      time: '2024-12-01 14:30:00',
      title: '订单创建',
      description: '订单创建成功，等待支付',
      status: 'completed'
    },
    {
      time: '2024-12-01 14:32:15',
      title: '支付完成',
      description: '支付宝支付成功，¥553.00',
      status: 'completed'
    },
    {
      time: '2024-12-01 14:32:30',
      title: '出票成功',
      description: '电子客票已生成',
      status: 'completed'
    },
    {
      time: '2024-12-15 08:00:00',
      title: '检票进站',
      description: '预计检票时间',
      status: 'pending'
    }
  ]
})

// 返回上一页
const goBack = () => {
  router.back()
}

// 下载电子票
const downloadTicket = () => {
  ElMessage.success('电子票下载中...')
  // 实际项目中这里会调用下载API
}

// 分享订单
const shareOrder = () => {
  if (navigator.share) {
    navigator.share({
      title: '我的火车票订单',
      text: `${orderDetail.value.trainNumber} ${orderDetail.value.departure} → ${orderDetail.value.arrival}`,
      url: window.location.href
    })
  } else {
    // 复制链接到剪贴板
    navigator.clipboard.writeText(window.location.href)
    ElMessage.success('订单链接已复制到剪贴板')
  }
}

// 支付订单
const handlePayOrder = () => {
  router.push({
    name: 'payment',
    query: { orderId: orderDetail.value.id }
  })
}

// 取消订单
const handleCancelOrder = async () => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    orderDetail.value.status = 'cancelled'
    orderDetail.value.timeline.push({
      time: new Date().toISOString().replace('T', ' ').slice(0, 19),
      title: '订单取消',
      description: '订单已取消',
      status: 'completed'
    })

    ElMessage.success('订单已取消')
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (error) {
    ElMessage.error('取消订单失败，请重试')
  }
}

// 申请退票
const handleRefundOrder = () => {
  router.push({
    name: 'my-orders',
    query: { action: 'refund', orderId: orderDetail.value.id }
  })
}

// 加载订单详情
const loadOrderDetail = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 实际项目中这里会根据orderId从API获取数据
    console.log('Loading order detail for:', orderId.value)

  } catch (error) {
    ElMessage.error('加载订单详情失败')
    console.error('Load order detail error:', error)
  } finally {
    loading.value = false
  }
}

// 页面初始化
onMounted(() => {
  loadOrderDetail()
})
</script>

<template>
  <div class="order-detail-page">
    <div class="container">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-left">
          <ElButton
            :icon="ArrowLeft"
            circle
            @click="goBack"
            class="back-button"
          />
          <div class="header-info">
            <h1 class="page-title">订单详情</h1>
            <p class="order-number">订单号：{{ orderDetail.orderNumber }}</p>
          </div>
        </div>

        <div class="header-actions">
          <ElButton
            :icon="Download"
            @click="downloadTicket"
            class="action-button"
          >
            下载电子票
          </ElButton>
          <ElButton
            :icon="Share"
            @click="shareOrder"
            class="action-button"
          >
            分享
          </ElButton>
        </div>
      </div>

      <!-- 订单内容 -->
      <div class="order-content">
        <div class="main-content">
          <!-- 订单状态头部 -->
          <OrderHeader
            :order="orderDetail"
            :loading="loading"
          />

          <!-- 行程详情 -->
          <TripDetails
            :order="orderDetail"
            :loading="loading"
          />

          <!-- 乘客详情 -->
          <PassengerDetails
            :passengers="orderDetail.passengers"
            :loading="loading"
          />

          <!-- 支付详情 -->
          <PaymentDetails
            :paymentInfo="orderDetail.paymentInfo"
            :loading="loading"
          />
        </div>

        <div class="sidebar-content">
          <!-- 订单时间线 -->
          <OrderTimeline
            :order="orderDetail"
            :loading="loading"
          />

          <!-- 操作按钮 -->
          <ActionButtons
            :order="orderDetail"
            :loading="loading"
            @pay-order="handlePayOrder"
            @cancel-order="handleCancelOrder"
            @refund-order="handleRefundOrder"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-detail-page {
  min-height: calc(100vh - 60px);
  background: #f5f7fa;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 20px 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-button {
  --el-button-bg-color: #f5f7fa;
  --el-button-border-color: #e4e7ed;
  --el-button-hover-bg-color: #ecf5ff;
  --el-button-hover-border-color: #b3d8ff;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.order-number {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
  font-family: 'Courier New', monospace;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.action-button {
  --el-button-bg-color: #f8f9fa;
  --el-button-border-color: #e9ecef;
  --el-button-text-color: #495057;
  --el-button-hover-bg-color: #e9ecef;
  --el-button-hover-border-color: #dee2e6;
}

.order-content {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
  align-items: start;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: sticky;
  top: 20px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .order-content {
    grid-template-columns: 1fr;
  }

  .sidebar-content {
    position: static;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-left {
    width: 100%;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .action-button {
    flex: 1;
  }
}
</style>
