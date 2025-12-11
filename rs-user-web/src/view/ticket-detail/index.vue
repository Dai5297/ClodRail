<template>
  <div class="min-h-screen bg-gray-50 pb-10">
    <!-- 页面头部 -->
    <div class="bg-white shadow-sm mb-5 sticky top-0 z-30">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <button
          @click="goBack"
          class="flex items-center text-blue-600 hover:text-blue-800 transition-colors mb-2 text-sm"
        >
          <svg
            class="w-4 h-4 mr-1"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M10 19l-7-7m0 0l7-7m-7 7h18"
            ></path>
          </svg>
          返回车票列表
        </button>
        <h2 class="text-2xl font-bold text-gray-900">车票详情</h2>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div class="animate-pulse space-y-4">
        <div class="h-4 bg-gray-200 rounded w-1/4"></div>
        <div class="h-32 bg-gray-200 rounded"></div>
        <div class="h-32 bg-gray-200 rounded"></div>
        <div class="h-32 bg-gray-200 rounded"></div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div v-else class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- 车次信息卡片 -->
      <TrainInfoCard :ticket-detail="ticketDetail" />

      <!-- 座位选择区域 -->
      <SeatSelectionCard
        :seat-types="ticketDetail.seatTypes"
        :selected-seat-type="selectedSeatType"
        :seat-positions="seatPositions"
        :selected-positions="selectedPositions"
        @select-seat-type="selectSeatType"
        @update-position-count="updatePositionCount"
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
      <div class="mt-8 flex justify-center gap-4">
        <button
          @click="goBack"
          class="px-8 py-3 bg-white border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
        >
          取消
        </button>
        <button
          @click="proceedToPayment"
          :disabled="!canProceed || submitting"
          class="px-8 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center"
        >
          <svg
            v-if="submitting"
            class="animate-spin -ml-1 mr-2 h-5 w-5 text-white"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              class="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              stroke-width="4"
            ></circle>
            <path
              class="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            ></path>
          </svg>
          立即预订
        </button>
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
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import toast from '@/components/Toast'
import request from '@/utils/request'
import { getContactList } from '@/api/contact'
import { getAvailableSeats, getTicketDetail } from '@/api/ticket'
import { createOrder } from '@/api/order'
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
  destinationStationId: route.query.destinationStationId
    ? parseInt(route.query.destinationStationId)
    : null,
  date: route.query.date || '',
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
  originStation: { name: '', platform: '' },
  destinationStation: { name: '', platform: '' },
  seatTypes: [],
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
  1: [
    // 一等座
    { code: 'A', name: '靠窗', remainingSeats: 0 },
    { code: 'B', name: '过道', remainingSeats: 0 },
    { code: 'E', name: '过道', remainingSeats: 0 },
    { code: 'F', name: '靠窗', remainingSeats: 0 },
  ],
  2: [
    // 二等座
    { code: 'A', name: '靠窗', remainingSeats: 0 },
    { code: 'B', name: '中间', remainingSeats: 0 },
    { code: 'C', name: '过道', remainingSeats: 0 },
    { code: 'E', name: '过道', remainingSeats: 0 },
    { code: 'F', name: '靠窗', remainingSeats: 0 },
  ],
}

// 计算属性
const totalAmount = computed(() => {
  if (!selectedSeatType.value || selectedPassengers.value.length === 0) {
    return 0
  }

  const unitPrice = selectedSeatType.value.price
  let total = 0

  selectedPassengers.value.forEach((passenger) => {
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
    const response = await getTicketDetail(searchParams.ticketId)

    if (response.code === 200) {
      ticketDetail.value = response.data
    } else {
      toast.error(response.message || '获取车票详情失败')
    }
  } catch (error) {
    toast.error('获取车票详情失败')
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
      toast.error(response.message || '获取联系人列表失败')
    }
  } catch (error) {
    toast.error('获取联系人列表失败')
  }
}

// 选择座位类型
const selectSeatType = async (seatType) => {
  if (seatType.remainingSeats === 0) {
    toast.warning('该座位类型已售完')
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
      seatType: seatType,
    })
    if (response.data && response.code === 200) {
      // 处理后端返回的数据格式：{ data: [{ site: "A", count: 10 }, ...], code: 200, message: null }
      const seatData = response.data // ← 关键修复点！
      // 根据座位类型生成位置信息
      seatPositions.value[seatType] = positionConfig[seatType].map((pos) => {
        // 从后端数据中查找对应位置的余票信息
        const seatInfo = seatData.find((seat) => seat.site === pos.code)
        const remainingCount = seatInfo ? seatInfo.count : 0

        return {
          ...pos,
          remainingSeats: remainingCount,
        }
      })
    } else {
      throw new Error('API响应格式错误')
    }
  } catch (error) {
    toast.error('获取座位位置信息失败')
  }
}

// 更新位置选择数量
const updatePositionCount = (positionCode, delta) => {
  const position = seatPositions.value[selectedSeatType.value.type]?.find(
    (p) => p.code === positionCode,
  )
  
  if (!position) return

  // 获取当前位置已选择数量
  const currentCount = selectedPositions.value.filter(p => p === positionCode).length
  
  // 计算新数量
  let newCount = currentCount + delta
  if (newCount < 0) newCount = 0
  
  // 增加数量时的校验
  if (delta > 0) {
      if (position.remainingSeats === 0) {
        toast.warning('该位置已售完')
        return
      }
      
      const MAX_TICKETS = 5
      const totalSelected = selectedPositions.value.length
      
      if (totalSelected >= MAX_TICKETS) {
          toast.warning(`最多只能选择${MAX_TICKETS}张车票`)
          return
      }
      
      if (currentCount >= position.remainingSeats) {
          toast.warning('该位置余票不足')
          return
      }
  }

  // 更新 selectedPositions
  // 重构数组: 移除当前位置的所有记录，然后添加 newCount 个当前位置
  const otherPositions = selectedPositions.value.filter(p => p !== positionCode)
  const newPositions = [...otherPositions]
  for (let i = 0; i < newCount; i++) {
      newPositions.push(positionCode)
  }
  selectedPositions.value = newPositions
}

// 更新乘客选择逻辑
const confirmPassengerSelection = (selectedContacts) => {
  selectedPassengers.value = [...selectedContacts]
  showContactModal.value = false
}

// 移除乘客
const removePassenger = (index) => {
  selectedPassengers.value.splice(index, 1)
}

// 前往支付页面
const proceedToPayment = async () => {
  try {
    submitting.value = true

    // 验证位置选择
    if (
      selectedPositions.value.length > 0 &&
      selectedPositions.value.length !== selectedPassengers.value.length
    ) {
      toast.warning('请为每位乘客选择座位位置')
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
            seatPosition: selectedPositions.value[i] || null,
          },
        ],
      }

      const resp = await createOrder(singleOrderData)

      // 详细日志：查看完整响应结构

      if (resp?.code !== 200) {
        toast.error(resp?.message || '创建订单失败')
        throw new Error(resp?.message || '创建订单失败')
      }

      // 验证返回数据
      if (!resp.data) {
        toast.error('创建订单失败：返回数据为空')
        throw new Error('返回数据为空')
      }

      // 兼容不同的字段名：id 或 orderId
      const orderId = resp.data.id || resp.data.orderId
      if (!orderId) {
        toast.error('创建订单失败：无法获取订单ID')
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
        seatType: selectedSeatType.value,
      }

      createdOrderIds.push(orderId)
      createdOrders.value.push(orderData)
    }

    // 跳转到支付页面（若仅支持单订单支付，则使用第一笔订单）
    if (createdOrderIds.length > 0) {
      router.push({
        path: '/ticket-payment',
        query: {
          orderId: createdOrderIds[0],
        },
      })
    }
  } catch (error) {
    toast.error('创建订单失败，请重试')
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
    4: '老人',
  }
  return typeMap[type] || '未知'
}

// 组件挂载时初始化
onMounted(async () => {
  // 验证必要参数
  if (!searchParams.ticketId) {
    toast.error('缺少必要参数')
    router.back()
    return
  }

  await Promise.all([loadTicketDetail(), loadContacts()])
})
</script>


