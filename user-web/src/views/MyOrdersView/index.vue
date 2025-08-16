<script setup lang="ts">
// MyOrdersView - 我的订单页面主文件
import { ref, onMounted, computed, withDefaults } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import OrderTabs from './components/OrderTabs.vue'
import OrderList from './components/OrderList.vue'
import OrderDetailModal from './components/OrderDetailModal.vue'
import RefundModal from './components/RefundModal.vue'

// 定义订单接口
interface Order {
  id: string
  orderNumber: string
  status: string
  trainNumber: string
  departure: string
  arrival: string
  departureTime: string
  arrivalTime: string
  seatType: string
  seatNumber: string
  passengers: Array<{ name: string; idCard: string }>
  totalAmount: number
  createTime: string
  payTime?: string
  cancelTime?: string
  refundReason?: string
  refundAmount?: number
}

const route = useRoute()
const router = useRouter()

// 页面状态
const loading = ref(false)
const activeTab = ref('all')
const searchKeyword = ref('')
const selectedOrder = ref<Order | null>(null)
const showOrderDetail = ref(false)
const showRefundModal = ref(false)

// 订单数据
const orders = ref<Order[]>([
  {
    id: 'ORD20241201001',
    orderNumber: 'ORD20241201001',
    status: 'paid',
    trainNumber: 'G1234',
    departure: '北京南',
    arrival: '上海虹桥',
    departureTime: '2024-12-15 08:00',
    arrivalTime: '2024-12-15 12:30',
    seatType: '二等座',
    seatNumber: '05车06A',
    passengers: [{ name: '张三', idCard: '110101199001011234' }],
    totalAmount: 553.0,
    createTime: '2024-12-01 14:30:00',
    payTime: '2024-12-01 14:32:15'
  },
  {
    id: 'ORD20241130002',
    orderNumber: 'ORD20241130002',
    status: 'completed',
    trainNumber: 'D2468',
    departure: '上海虹桥',
    arrival: '杭州东',
    departureTime: '2024-11-30 16:20',
    arrivalTime: '2024-11-30 17:15',
    seatType: '一等座',
    seatNumber: '03车02B',
    passengers: [{ name: '李四', idCard: '310101199002021234' }],
    totalAmount: 73.0,
    createTime: '2024-11-29 10:15:00',
    payTime: '2024-11-29 10:16:30'
  },
  {
    id: 'ORD20241128003',
    orderNumber: 'ORD20241128003',
    status: 'cancelled',
    trainNumber: 'G5678',
    departure: '广州南',
    arrival: '深圳北',
    departureTime: '2024-11-28 19:30',
    arrivalTime: '2024-11-28 20:00',
    seatType: '二等座',
    seatNumber: '08车15C',
    passengers: [{ name: '王五', idCard: '440101199003031234' }],
    totalAmount: 74.5,
    createTime: '2024-11-27 16:45:00',
    cancelTime: '2024-11-27 18:20:00'
  }
])

// 过滤后的订单
const filteredOrders = computed(() => {
  let result = orders.value
  
  // 按状态过滤
  if (activeTab.value !== 'all') {
    result = result.filter(order => order.status === activeTab.value)
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(order => 
      order.orderNumber.toLowerCase().includes(keyword) ||
      order.trainNumber.toLowerCase().includes(keyword) ||
      order.departure.toLowerCase().includes(keyword) ||
      order.arrival.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

// 标签统计
const tabCounts = computed(() => {
  const counts = {
    all: orders.value.length,
    pending: orders.value.filter(o => o.status === 'pending').length,
    paid: orders.value.filter(o => o.status === 'paid').length,
    completed: orders.value.filter(o => o.status === 'completed').length,
    cancelled: orders.value.filter(o => o.status === 'cancelled').length,
    refunded: orders.value.filter(o => o.status === 'refunded').length
  }
  return counts
})

// 切换标签
const handleTabChange = (tab: string) => {
  activeTab.value = tab
}

// 搜索订单
const handleSearch = (keyword: string) => {
  searchKeyword.value = keyword
}

// 查看订单详情
const handleViewDetail = (order: any) => {
  selectedOrder.value = order
  showOrderDetail.value = true
}

// 支付订单
const handlePayOrder = (order: any) => {
  router.push({
    name: 'payment',
    query: { orderId: order.id }
  })
}

// 取消订单
const handleCancelOrder = async (order: any) => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 更新订单状态
    const index = orders.value.findIndex(o => o.id === order.id)
    if (index !== -1) {
      orders.value[index].status = 'cancelled'
      orders.value[index].cancelTime = new Date().toISOString().replace('T', ' ').slice(0, 19)
    }
    
    ElMessage.success('订单已取消')
  } catch (error) {
    ElMessage.error('取消订单失败，请重试')
  }
}

// 申请退票
const handleRefund = (order: any) => {
  selectedOrder.value = order
  showRefundModal.value = true
}

// 处理退票申请
const handleRefundSubmit = async (refundData: any) => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 更新订单状态
    const index = orders.value.findIndex(o => o.id === selectedOrder.value?.id)
    if (index !== -1) {
      orders.value[index].status = 'refund_pending'
      orders.value[index].refundReason = refundData.reason
      orders.value[index].refundAmount = refundData.amount
    }
    
    showRefundModal.value = false
    ElMessage.success('退票申请已提交，请等待审核')
  } catch (error) {
    ElMessage.error('退票申请失败，请重试')
  }
}

// 页面初始化
onMounted(() => {
  // 从路由参数获取初始状态
  if (route.query.status) {
    activeTab.value = route.query.status as string
  }
  
  console.log('MyOrdersView initialized')
})
</script>

<template>
  <div class="my-orders-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">我的订单</h1>
        <p class="page-subtitle">管理您的所有订单信息</p>
      </div>
      
      <!-- 订单标签 -->
      <OrderTabs
        :active-tab="activeTab"
        :tab-counts="tabCounts"
        @tab-change="handleTabChange"
        @search="handleSearch"
      />
      
      <!-- 订单列表 -->
      <OrderList
        :orders="filteredOrders"
        :loading="loading"
        @view-detail="handleViewDetail"
        @pay-order="handlePayOrder"
        @cancel-order="handleCancelOrder"
        @refund-order="handleRefund"
      />
      
      <!-- 订单详情模态框 -->
      <OrderDetailModal
        v-model:visible="showOrderDetail"
        :order="selectedOrder"
        @pay-order="handlePayOrder"
        @cancel-order="handleCancelOrder"
        @refund-order="handleRefund"
      />
      
      <!-- 退票申请模态框 -->
      <RefundModal
        v-model:visible="showRefundModal"
        :order="selectedOrder"
        @submit="handleRefundSubmit"
      />
    </div>
  </div>
</template>

<style scoped>
.my-orders-page {
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
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}
</style>
