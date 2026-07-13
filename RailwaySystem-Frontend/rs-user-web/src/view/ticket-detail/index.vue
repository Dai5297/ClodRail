<template>
  <main class="min-h-screen bg-background pb-12 text-ink">
    <header class="border-b border-line bg-white">
      <div class="mx-auto max-w-7xl px-4 py-5 sm:px-6 lg:px-8">
        <button
          type="button"
          class="mb-3 inline-flex items-center gap-1.5 rounded-lg text-sm font-medium text-secondary transition-colors hover:text-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2"
          @click="goBack"
        >
          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          返回车票列表
        </button>
        <div class="flex flex-col gap-1 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <p class="text-xs font-semibold uppercase tracking-[0.2em] text-primary">Ticket booking</p>
            <h1 class="mt-1 text-2xl font-bold text-navy sm:text-3xl">确认您的出行信息</h1>
          </div>
          <p class="text-sm text-secondary">按顺序完成席别与乘车人选择后提交订单</p>
        </div>
      </div>
    </header>

    <section class="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8" aria-label="订票进度">
      <ol class="grid grid-cols-4 overflow-hidden rounded-xl border border-line bg-white shadow-soft">
        <li
          v-for="(step, index) in ['车次', '席别', '乘车人', '订单确认']"
          :key="step"
          class="relative flex min-h-16 items-center justify-center gap-2 border-r border-line px-2 last:border-r-0 sm:justify-start sm:px-5"
          :class="index === 0 || (index === 1 && selectedSeatType) || (index >= 2 && selectedPassengers.length) ? 'text-navy' : 'text-secondary/80'"
        >
          <span
            class="data-number flex h-7 w-7 shrink-0 items-center justify-center rounded-full border text-xs font-bold"
            :class="index === 0 || (index === 1 && selectedSeatType) || (index >= 2 && selectedPassengers.length) ? 'border-primary bg-primary text-white' : 'border-line bg-background'"
          >{{ index + 1 }}</span>
          <span class="hidden text-sm font-semibold sm:inline">{{ step }}</span>
          <span v-if="index < 3" class="absolute -right-1.5 top-1/2 z-10 h-3 w-3 -translate-y-1/2 rotate-45 border-r border-t border-line bg-white" aria-hidden="true" />
        </li>
      </ol>
    </section>

    <section v-if="loading" class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8" aria-live="polite" aria-busy="true">
      <span class="sr-only">正在加载车票详情</span>
      <div class="animate-pulse space-y-5">
        <div class="h-48 rounded-xl border border-line bg-white" />
        <div class="grid gap-5 lg:grid-cols-[minmax(0,1fr)_360px]">
          <div class="space-y-5">
            <div class="h-64 rounded-xl border border-line bg-white" />
            <div class="h-48 rounded-xl border border-line bg-white" />
          </div>
          <div class="h-80 rounded-xl border border-line bg-white" />
        </div>
      </div>
    </section>

    <section v-else class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <TrainInfoCard :ticket-detail="ticketDetail" />

      <div class="grid items-start gap-5 lg:grid-cols-[minmax(0,1fr)_360px]">
        <div class="min-w-0 space-y-5">
          <SeatSelectionCard
            :seat-types="ticketDetail.seatTypes"
            :selected-seat-type="selectedSeatType"
            :seat-positions="seatPositions"
            :selected-positions="selectedPositions"
            @select-seat-type="selectSeatType"
            @update-position-count="updatePositionCount"
          />

          <PassengerInfoCard
            :selected-passengers="selectedPassengers"
            :selected-seat-type="selectedSeatType"
            @add-passenger="showContactModal = true"
            @remove-passenger="removePassenger"
          />
        </div>

        <aside class="lg:sticky lg:top-6">
          <OrderSummaryCard
            :train-code="ticketDetail.trainCode"
            :departure-date="formatDate(ticketDetail.departureDate)"
            :origin-station="ticketDetail.originStation.name"
            :destination-station="ticketDetail.destinationStation.name"
            :seat-type-name="selectedSeatType?.name || '未选择'"
            :passenger-count="selectedPassengers.length"
            :total-amount="totalAmount"
          />

          <div class="mt-4 grid grid-cols-2 gap-3">
            <button
              type="button"
              class="rounded-lg border border-line bg-white px-4 py-3 text-sm font-semibold text-secondary transition-colors hover:border-primary hover:text-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2"
              @click="goBack"
            >
              取消
            </button>
            <button
              type="button"
              class="inline-flex items-center justify-center rounded-lg bg-action px-4 py-3 text-sm font-bold text-white shadow-[0_6px_16px_color-mix(in_srgb,var(--rail-action)_20%,transparent)] transition-colors hover:bg-action-hover focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-action focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:bg-secondary/40 disabled:shadow-none"
              :disabled="!canProceed || submitting"
              @click="proceedToPayment"
            >
              <svg v-if="submitting" class="mr-2 h-4 w-4 animate-spin" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" aria-hidden="true">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
              </svg>
              {{ submitting ? '正在提交' : '提交订单' }}
            </button>
          </div>
          <p v-if="!canProceed" class="mt-3 text-center text-xs leading-5 text-secondary/80">选择席别并添加乘车人后即可提交</p>
        </aside>
      </div>
    </section>

    <ContactModal
      v-model="showContactModal"
      :contacts="contacts"
      :selected-seat-type="selectedSeatType"
      @confirm="confirmPassengerSelection"
    />
  </main>
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

<style scoped>
.data-number {
  font-family: "DIN Alternate", "Arial Narrow", Arial, sans-serif;
  font-variant-numeric: tabular-nums;
}
</style>
