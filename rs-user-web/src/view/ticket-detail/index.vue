<template>
  <div class="ticket-detail-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <el-button
            type="text"
            @click="goBack"
            class="back-btn"
        >
          <el-icon>
            <ArrowLeft/>
          </el-icon>
          返回车票列表
        </el-button>
        <h2 class="page-title">车票详情</h2>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated/>
    </div>

    <!-- 主要内容 -->
    <div v-else class="main-content">
      <div class="container">
        <!-- 车次信息卡片 -->
        <TrainInfoCard :ticket-detail="ticketDetail" />

        <!-- 座位选择区域 -->
        <SeatSelectionCard
          :seat-types="ticketDetail.seatTypes"
          :selected-seat-type="selectedSeatType"
          :seat-positions="seatPositions"
          :selected-positions="selectedPositions"
          @select-seat-type="selectSeatType"
          @toggle-position="togglePosition"
        />

        <!-- 乘客信息区域 -->
        <PassengerInfoCard
          :selected-passengers="selectedPassengers"
          :selected-seat-type="selectedSeatType"
          @add-passenger="showContactModal = true"
          @remove-passenger="removePassenger"
        />

        <!-- 订单摘要 -->
        <OrderSummaryCard
          :train-code="ticketDetail.trainCode"
          :departure-date="formatDate(ticketDetail.departureDate)"
          :origin-station="ticketDetail.originStation.name"
          :destination-station="ticketDetail.destinationStation.name"
          :seat-type-name="selectedSeatType?.name || '未选择'"
          :passenger-count="selectedPassengers.length"
          :total-amount="totalAmount"
        />

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button size="large" @click="goBack">取消</el-button>
          <el-button
              type="primary"
              size="large"
              @click="proceedToPayment"
              :disabled="!canProceed"
              :loading="submitting"
          >
            立即预订
          </el-button>
        </div>
      </div>
    </div>

    <!-- 联系人选择弹窗 -->
    <ContactModal
      v-model="showContactModal"
      :contacts="contacts"
      :selected-seat-type="selectedSeatType"
      @confirm="confirmPassengerSelection"
    />
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {ArrowLeft} from '@element-plus/icons-vue'
import request from '@/utils/request'
import {getContactList} from '@/api/contact'
import {getAvailableSeats} from '@/api/ticket'
import {createOrder} from '@/api/order'
import TrainInfoCard from './components/TrainInfoCard.vue'
import SeatSelectionCard from './components/SeatSelectionCard.vue'
import PassengerInfoCard from './components/PassengerInfoCard.vue'
import OrderSummaryCard from './components/OrderSummaryCard.vue'
import ContactModal from './components/ContactModal.vue'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(true)
const submitting = ref(false)
const showContactModal = ref(false)

// 搜索参数
const searchParams = reactive({
  ticketId: route.query.ticketId,
  originStationId: route.query.originStationId ? parseInt(route.query.originStationId) : null,
  destinationStationId: route.query.destinationStationId ? parseInt(route.query.destinationStationId) : null,
  date: route.query.date || ''
})

// 车票详情数据
const ticketDetail = ref({
  trainCode: '',
  trainType: '',
  startTime: '',
  endTime: '',
  duration: '',
  stopCount: 0,
  departureDate: '',
  originStation: {name: '', platform: ''},
  destinationStation: {name: '', platform: ''},
  seatTypes: []
})

// 选择的座位类型和乘客
const selectedSeatType = ref(null)
const selectedPassengers = ref([])
const selectedPositions = ref([])
const contacts = ref([])

// 座位位置数据
const seatPositions = ref({})
const createdOrders = ref([])

// 座位位置配置
const positionConfig = {
  1: [ // 一等座
    {code: 'A', name: '靠窗', remainingSeats: 0},
    {code: 'B', name: '过道', remainingSeats: 0},
    {code: 'E', name: '过道', remainingSeats: 0},
    {code: 'F', name: '靠窗', remainingSeats: 0}
  ],
  2: [ // 二等座
    {code: 'A', name: '靠窗', remainingSeats: 0},
    {code: 'B', name: '中间', remainingSeats: 0},
    {code: 'C', name: '过道', remainingSeats: 0},
    {code: 'E', name: '过道', remainingSeats: 0},
    {code: 'F', name: '靠窗', remainingSeats: 0}
  ]
}

// 计算属性
const totalAmount = computed(() => {
  if (!selectedSeatType.value || selectedPassengers.value.length === 0) {
    return 0
  }
  
  const unitPrice = selectedSeatType.value.price
  let total = 0
  
  selectedPassengers.value.forEach(passenger => {
    if (passenger.passengerType === 1) {
      // 成人原价
      total += unitPrice
    } else {
      // 其他类型8折
      total += unitPrice * 0.8
    }
  })
  
  return total
})

const canProceed = computed(() => {
  return selectedSeatType.value && selectedPassengers.value.length > 0
})

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

// 返回上一页
const goBack = () => {
  router.back()
}

// 获取车票详情
const loadTicketDetail = async () => {
  try {
    loading.value = true
    const response = await request.get('/tickets/detail', {
      params: {ticketId: searchParams.ticketId}
    })

    if (response.code === 200) {
      ticketDetail.value = response.data
    } else {
      ElMessage.error(response.message || '获取车票详情失败')
    }
  } catch (error) {
    console.error('获取车票详情失败:', error)
    ElMessage.error('获取车票详情失败')
  } finally {
    loading.value = false
  }
}

// 获取联系人列表
const loadContacts = async () => {
  try {
    const response = await getContactList()
    if (response.code === 200) {
      // 根据分页响应结构适配数据
      contacts.value = response.data.records || response.data || []
    } else {
      ElMessage.error(response.message || '获取联系人列表失败')
    }
  } catch (error) {
    console.error('获取联系人列表失败:', error)
    ElMessage.error('获取联系人列表失败')
  }
}

// 选择座位类型
const selectSeatType = async (seatType) => {
  if (seatType.remainingSeats === 0) {
    ElMessage.warning('该座位类型已售完')
    return
  }

  selectedSeatType.value = seatType
  selectedPositions.value = []
  // 清空已选乘客，因为座位类型变了
  selectedPassengers.value = []

  // 清空所有座位位置数据，避免其他座位类型显示错误数据
  seatPositions.value = {}

  // 加载座位位置信息
  await loadSeatPositions(seatType.type)
}

// 加载座位位置信息
const loadSeatPositions = async (seatType) => {
  try {
    // 使用getAvailableSeats方法调用API获取座位位置余票信息
    const response = await getAvailableSeats({
      ticketId: searchParams.ticketId,
      seatType: seatType
    })
    console.log(response)
    if (response.data && response.code === 200) {
      // 处理后端返回的数据格式：{ data: [{ site: "A", count: 10 }, ...], code: 200, message: null }
      const seatData = response.data; // ← 关键修复点！
      console.log('seatData:', seatData)
      // 根据座位类型生成位置信息
      seatPositions.value[seatType] = positionConfig[seatType].map(pos => {
        // 从后端数据中查找对应位置的余票信息
        const seatInfo = seatData.find(seat => seat.site === pos.code);
        const remainingCount = seatInfo ? seatInfo.count : 0;

        return {
          ...pos,
          remainingSeats: remainingCount
        };
      });
    } else {
      throw new Error('API响应格式错误')
    }
  } catch (error) {
    console.error('获取座位位置信息失败:', error)
    ElMessage.warning('获取座位位置信息失败，使用模拟数据')

    // 使用模拟数据
    seatPositions.value[seatType] = positionConfig[seatType].map(pos => ({
      ...pos,
      remainingSeats: Math.floor(Math.random() * 20) + 1 // 模拟余票数 1-20
    }))
  }
}

// 切换位置选择
const togglePosition = (positionCode) => {
  const position = seatPositions.value[selectedSeatType.value.type]?.find(p => p.code === positionCode)

  // 检查座位是否可用
  if (position && position.remainingSeats === 0) {
    ElMessage.warning('该位置已售完')
    return
  }

  const index = selectedPositions.value.indexOf(positionCode)
  if (index > -1) {
    // 取消选择
    selectedPositions.value.splice(index, 1)
  } else {
    // 选择位置
    // 如果还没有选择乘客，允许选择位置
    if (selectedPassengers.value.length === 0) {
      selectedPositions.value.push(positionCode)
    } else {
      // 如果已有乘客，检查位置数量不能超过乘客数量
      if (selectedPositions.value.length >= selectedPassengers.value.length) {
        ElMessage.warning(`最多只能选择${selectedPassengers.value.length}个座位位置`)
        return
      }
      selectedPositions.value.push(positionCode)
    }
  }
}

// 更新乘客选择逻辑
const confirmPassengerSelection = (selectedContacts) => {
  // 如果选择的位置数量超过乘客数量，截取对应数量的位置
  if (selectedPositions.value.length > selectedContacts.length) {
    selectedPositions.value = selectedPositions.value.slice(0, selectedContacts.length)
  }

  selectedPassengers.value = [...selectedContacts]
  showContactModal.value = false
}

// 移除乘客
const removePassenger = (index) => {
  selectedPassengers.value.splice(index, 1)
  // 同时移除对应的位置选择
  if (selectedPositions.value.length > selectedPassengers.value.length) {
    selectedPositions.value = selectedPositions.value.slice(0, selectedPassengers.value.length)
  }
}

// 前往支付页面
const proceedToPayment = async () => {
  try {
    submitting.value = true

    // 验证位置选择
    if (selectedPositions.value.length > 0 && selectedPositions.value.length !== selectedPassengers.value.length) {
      ElMessage.warning('请为每位乘客选择座位位置')
      submitting.value = false
      return
    }

    // 计算单个乘客应付金额（成人原价，其他类型8折）
    const unitPrice = selectedSeatType.value?.price || 0
    const calcPassengerAmount = (type) => (type === 1 ? unitPrice : unitPrice * 0.8)

    // 逐个乘客创建独立订单，确保每个订单的amount正确
    const createdOrderIds = []
    for (let i = 0; i < selectedPassengers.value.length; i++) {
      const p = selectedPassengers.value[i]

      const singleOrderData = {
        ticketId: searchParams.ticketId,
        seatType: selectedSeatType.value.type,
        amount: calcPassengerAmount(p.passengerType),
        startTime: ticketDetail.value.startTime,
        endTime: ticketDetail.value.endTime,
        passengers: [
          {
            passengerId: p.id, // 添加乘客ID
            name: p.name,
            passengerType: p.passengerType,
            seatPosition: selectedPositions.value[i] || null
          }
        ]
      }

      const resp = await createOrder(singleOrderData)
      
      // 详细日志：查看完整响应结构
      console.log('=== 创建订单响应 ===')
      console.log('完整响应对象:', resp)
      console.log('响应码:', resp?.code)
      console.log('响应数据:', resp?.data)
      console.log('订单ID (resp.data.id):', resp?.data?.id)
      console.log('订单ID (resp.data.orderId):', resp?.data?.orderId)
      console.log('==================')
      
      if (resp?.code !== 200) {
        ElMessage.error(resp?.message || '创建订单失败')
        throw new Error(resp?.message || '创建订单失败')
      }

      // 验证返回数据
      if (!resp.data) {
        ElMessage.error('创建订单失败：返回数据为空')
        throw new Error('返回数据为空')
      }

      // 兼容不同的字段名：id 或 orderId
      const orderId = resp.data.id || resp.data.orderId
      if (!orderId) {
        console.error('订单ID获取失败，完整数据:', resp)
        ElMessage.error('创建订单失败：无法获取订单ID')
        throw new Error('无法获取订单ID')
      }

      // 保存订单数据用于跳转
      const orderData = {
        ...resp.data,
        id: orderId, // 确保有 id 字段
        orderId: orderId, // 同时保留 orderId 字段
        trainCode: ticketDetail.value.trainCode,
        originStation: ticketDetail.value.originStation,
        destinationStation: ticketDetail.value.destinationStation,
        startTime: ticketDetail.value.startTime,
        endTime: ticketDetail.value.endTime,
        seatType: selectedSeatType.value
      }
      
      createdOrderIds.push(orderId)
      createdOrders.value.push(orderData)
    }

    // 跳转到支付页面（若仅支持单订单支付，则使用第一笔订单）
    if (createdOrderIds.length > 0) {
      router.push({
        path: '/ticketPayment',
        query: {
          orderId: createdOrderIds[0]
        }
      })
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 获取乘客类型文本
const getPassengerTypeText = (type) => {
  const typeMap = {
    1: '成人',
    2: '儿童', 
    3: '学生',
    4: '老人'
  }
  return typeMap[type] || '未知'
}

// 组件挂载时初始化
onMounted(async () => {
  // 验证必要参数
  if (!searchParams.ticketId) {
    ElMessage.error('缺少必要参数')
    router.back()
    return
  }

  await Promise.all([
    loadTicketDetail(),
    loadContacts()
  ])
})
</script>

<style scoped>
.ticket-detail-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.page-header {
  background: white;
  padding: 20px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
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

.loading-container {
  padding: 40px 20px;
}

.main-content {
  padding: 0 20px 40px;
}


/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 30px;
}

.action-buttons .el-button {
  min-width: 120px;
  height: 48px;
  font-size: 16px;
}


/* 响应式设计 */
@media (max-width: 768px) {
  .route-info {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .station-info.departure,
  .station-info.arrival {
    text-align: center;
  }

  .seat-types {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>