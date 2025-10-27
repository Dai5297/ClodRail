<template>
  <div class="user-order-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">我的订单</h2>
      <p class="page-desc">查看和管理您的订单信息</p>
    </div>

    <!-- 订单筛选 -->
    <div class="order-filter">
      <el-form :model="filterForm" inline>
        <el-form-item label="订单状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable @change="handleFilter">
            <el-option label="全部" value="" />
            <el-option label="待支付" value="待支付" />
            <el-option label="已支付" value="已支付" />
            <el-option label="已出票" value="已出票" />
            <el-option label="已取消" value="已取消" />
            <el-option label="已退票" value="已退票" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 订单列表 -->
    <div class="order-list" v-loading="loading">
      <div v-if="orderList.length === 0" class="empty-state">
        <el-empty description="暂无订单数据" />
      </div>
      <div v-else>
        <div
          v-for="order in orderList"
          :key="order.orderId"
          class="order-item"
          @click="handleViewDetail(order.orderId)"
        >
          <div class="order-header">
            <div class="order-info">
              <span class="order-id">订单号：{{ order.orderId }}</span>
              <span class="order-time">{{ order.createTime }}</span>
            </div>
            <div class="order-status" :class="getStatusClass(order.status)">
              {{ order.status }}
            </div>
          </div>
          
          <div class="order-content">
            <div class="train-info">
              <div class="train-number">{{ order.trainNumber }}</div>
              <div class="route">
                <span class="departure">{{ order.departure }}</span>
                <el-icon class="arrow-icon"><ArrowRight /></el-icon>
                <span class="arrival">{{ order.arrival }}</span>
              </div>
              <div class="time-info">
                <span class="departure-time">{{ order.departureTime }}</span>
                <span class="arrival-time">{{ order.arrivalTime }}</span>
              </div>
            </div>
            
            <div class="seat-info">
              <div class="seat-type">{{ order.seatType }}</div>
              <div class="price">¥{{ order.price }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="600px"
      :before-close="handleCloseDetail"
    >
      <div v-if="orderDetail" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ orderDetail.orderId }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusTagType(orderDetail.status)">{{ orderDetail.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="车次">{{ orderDetail.trainNumber }}</el-descriptions-item>
          <el-descriptions-item label="座位类型">{{ orderDetail.seatType }}</el-descriptions-item>
          <el-descriptions-item label="出发站">{{ orderDetail.departure }}</el-descriptions-item>
          <el-descriptions-item label="到达站">{{ orderDetail.arrival }}</el-descriptions-item>
          <el-descriptions-item label="出发时间">{{ orderDetail.departureTime }}</el-descriptions-item>
          <el-descriptions-item label="到达时间">{{ orderDetail.arrivalTime }}</el-descriptions-item>
          <el-descriptions-item label="座位号" v-if="orderDetail.seatNumber">{{ orderDetail.seatNumber }}</el-descriptions-item>
          <el-descriptions-item label="票价">¥{{ orderDetail.price }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ orderDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间" v-if="orderDetail.payTime">{{ orderDetail.payTime }}</el-descriptions-item>
        </el-descriptions>
        
        <div v-if="orderDetail.passengers && orderDetail.passengers.length > 0" class="passengers-info">
          <h4>乘车人信息</h4>
          <el-table :data="orderDetail.passengers" style="width: 100%">
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="idCard" label="身份证号" />
            <el-table-column prop="seatPosition" label="座位号" />
            <el-table-column prop="actualPrice" label="实际票价" />
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'
import { getOrderList, getOrderDetail } from '@/api/order.js'
import { getTicketDetail } from '@/api/ticket.js'

export default {
  name: 'UserOrder',
  components: {
    ArrowRight
  },
  setup() {
    const loading = ref(false)
    const orderList = ref([])
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const detailVisible = ref(false)
    const orderDetail = ref(null)

    const filterForm = reactive({
      status: ''
    })

    const fetchOrderList = async () => {
      try {
        loading.value = true
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          status: filterForm.status || undefined
        }
        
        const data = await getOrderList(params)
        orderList.value = data.records || []
        total.value = data.total || 0
      } catch (error) {
        ElMessage.error('获取订单列表失败')
      } finally {
        loading.value = false
      }
    }

    const handleFilter = () => {
      currentPage.value = 1
      fetchOrderList()
    }

    const handleReset = () => {
      filterForm.status = ''
      currentPage.value = 1
      fetchOrderList()
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      fetchOrderList()
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      fetchOrderList()
    }

    const handleViewDetail = async (orderId) => {
      try {
        const response = await getOrderDetail(orderId)
        if (response.code !== 200) {
          ElMessage.error(response.message || '获取订单详情失败')
          return
        }
        
        const orderData = response.data
        
        // 获取车票详情
        let ticketInfo = null
        if (orderData.ticketId) {
          try {
            const ticketResponse = await getTicketDetail(orderData.ticketId)
            if (ticketResponse.code === 200) {
              ticketInfo = ticketResponse.data
            }
          } catch (error) {
            console.error('获取车票信息失败:', error)
            ElMessage.warning('获取车票信息失败，部分信息可能不完整')
          }
        }
        
        // 处理订单详情数据，适配新的响应结构
        const processedOrderDetail = {
          orderId: orderData.orderId,
          status: getStatusText(orderData.status),
          trainNumber: ticketInfo?.trainCode || 'G1234',
          seatType: ticketInfo?.seatType === 1 ? '一等座' : ticketInfo?.seatType === 2 ? '二等座' : '商务座',
          departure: ticketInfo?.originStation || '出发站',
          arrival: ticketInfo?.destinationStation || '到达站',
          departureTime: ticketInfo?.departureTime ? formatDateTime(ticketInfo.departureTime) : '08:00',
          arrivalTime: ticketInfo?.arrivalTime ? formatDateTime(ticketInfo.arrivalTime) : '14:20',
          seatNumber: orderData.passengers && orderData.passengers.length > 0 ? orderData.passengers[0].seatPosition : '',
          price: orderData.totalAmount,
          createTime: orderData.createTime ? formatDateTime(orderData.createTime) : '',
          payTime: orderData.payTime ? formatDateTime(orderData.payTime) : '',
          passengers: orderData.passengers || []
        }
        
        orderDetail.value = processedOrderDetail
        detailVisible.value = true
      } catch (error) {
        ElMessage.error('获取订单详情失败')
      }
    }

    // 格式化日期时间
    const formatDateTime = (dateTimeStr) => {
      if (!dateTimeStr) return ''
      const date = new Date(dateTimeStr)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // 根据状态码获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        0: '待支付',
        1: '已支付',
        2: '已出票',
        3: '已取消',
        4: '已退票'
      }
      return statusMap[status] || '未知状态'
    }

    const handleCloseDetail = () => {
      detailVisible.value = false
      orderDetail.value = null
    }

    const getStatusClass = (status) => {
      const statusMap = {
        '待支付': 'pending',
        '已支付': 'paid',
        '已出票': 'issued',
        '已取消': 'cancelled',
        '已退票': 'refunded'
      }
      return statusMap[status] || 'default'
    }

    const getStatusTagType = (status) => {
      const typeMap = {
        '待支付': 'warning',
        '已支付': 'success',
        '已出票': 'success',
        '已取消': 'info',
        '已退票': 'info'
      }
      return typeMap[status] || 'default'
    }

    onMounted(() => {
      fetchOrderList()
    })

    return {
      loading,
      orderList,
      total,
      currentPage,
      pageSize,
      filterForm,
      detailVisible,
      orderDetail,
      fetchOrderList,
      handleFilter,
      handleReset,
      handleSizeChange,
      handleCurrentChange,
      handleViewDetail,
      handleCloseDetail,
      getStatusClass,
      getStatusTagType
    }
  }
}
</script>

<style scoped>
.user-order-page {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 8px 0;
}

.page-desc {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.order-filter {
  background: #fafafa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.order-list {
  min-height: 400px;
}

.order-item {
  background: white;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.order-item:hover {
  border-color: #1890FF;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-info {
  display: flex;
  gap: 16px;
}

.order-id {
  font-weight: 500;
  color: #262626;
}

.order-time {
  color: #8c8c8c;
  font-size: 14px;
}

.order-status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.order-status.pending {
  background: #fff7e6;
  color: #fa8c16;
}

.order-status.paid {
  background: #f6ffed;
  color: #52c41a;
}

.order-status.issued {
  background: #f6ffed;
  color: #52c41a;
}

.order-status.cancelled {
  background: #f5f5f5;
  color: #8c8c8c;
}

.order-status.refunded {
  background: #f5f5f5;
  color: #8c8c8c;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.train-info {
  flex: 1;
}

.train-number {
  font-size: 18px;
  font-weight: 600;
  color: #1890FF;
  margin-bottom: 8px;
}

.route {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.departure, .arrival {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
}

.arrow-icon {
  color: #8c8c8c;
}

.time-info {
  display: flex;
  gap: 16px;
  color: #8c8c8c;
  font-size: 14px;
}

.seat-info {
  text-align: right;
}

.seat-type {
  color: #8c8c8c;
  margin-bottom: 8px;
}

.price {
  font-size: 18px;
  font-weight: 600;
  color: #ff4d4f;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.order-detail {
  padding: 16px 0;
}

.passengers-info {
  margin-top: 24px;
}

.passengers-info h4 {
  margin-bottom: 16px;
  color: #262626;
}
</style>