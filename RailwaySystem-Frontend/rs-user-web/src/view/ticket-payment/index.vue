<template>
  <div class="min-h-screen bg-gray-50 pb-10">
    <!-- 页面头部 -->
    <div class="bg-white shadow-sm mb-5 sticky top-0 z-30">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <button 
          @click="goBack" 
          class="flex items-center text-blue-600 hover:text-blue-800 transition-colors mb-2 text-sm"
        >
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path></svg>
          返回订单详情
        </button>
        <h2 class="text-2xl font-bold text-gray-900">订单支付</h2>
      </div>
    </div>

    <!-- 支付进度条 -->
    <div class="bg-white border-b border-gray-200 py-8 mb-6">
      <div class="max-w-3xl mx-auto px-4">
        <div class="flex items-center justify-between relative">
          <!-- 进度条背景线 -->
          <div class="absolute left-0 top-1/2 transform -translate-y-1/2 w-full h-1 bg-gray-200 -z-10"></div>
          <!-- 进度条激活线 -->
          <div class="absolute left-0 top-1/2 transform -translate-y-1/2 h-1 bg-blue-600 -z-10 transition-all duration-500" :style="{ width: `${(currentStep - 1) / 3 * 100}%` }"></div>
          
          <!-- 步骤项 -->
          <div v-for="(step, index) in steps" :key="index" class="flex flex-col items-center bg-white px-2">
            <div 
              class="w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold border-2 transition-colors duration-300"
              :class="[
                currentStep > index + 1 ? 'bg-blue-600 border-blue-600 text-white' : 
                currentStep === index + 1 ? 'bg-blue-600 border-blue-600 text-white' : 
                'bg-white border-gray-300 text-gray-400'
              ]"
            >
              <span v-if="currentStep > index + 1">✓</span>
              <span v-else>{{ index + 1 }}</span>
            </div>
            <span 
              class="mt-2 text-xs font-medium transition-colors duration-300"
              :class="currentStep >= index + 1 ? 'text-blue-600' : 'text-gray-400'"
            >
              {{ step.title }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div class="animate-pulse space-y-4">
        <div class="h-4 bg-gray-200 rounded w-1/4"></div>
        <div class="h-32 bg-gray-200 rounded"></div>
        <div class="h-32 bg-gray-200 rounded"></div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div v-else class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- 左侧：订单信息 -->
        <div class="lg:col-span-2 space-y-6">
          <!-- 订单详情卡片 -->
          <OrderInfoCard :order-info="orderInfo" />

          <!-- 支付倒计时 -->
          <CountdownCard :remaining-time="remainingTime" />
        </div>

        <!-- 右侧：支付方式 -->
        <div class="space-y-6">
          <PaymentMethodsCard
            :payment-methods="paymentMethods"
            :selected-method="selectedPaymentMethod"
            :total-amount="orderInfo.totalAmount"
            :processing="processing"
            @select-method="selectPaymentMethod"
            @pay="processPayment"
          />

          <!-- 支付说明 -->
          <PaymentNoticeCard :remaining-time="remainingTime" />
        </div>
      </div>
    </div>

    <!-- 支付成功弹窗 -->
    <Modal
      v-model="showSuccessModal"
      title="支付成功"
      :show-close="false"
      :close-on-click-modal="false"
    >
      <div class="text-center py-6">
        <div class="mb-5 inline-flex items-center justify-center w-16 h-16 rounded-full bg-green-100 text-green-500">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
        </div>
        <h3 class="text-xl font-bold text-gray-900 mb-2">支付成功！</h3>
        <p class="text-gray-500 mb-6">
          您的订单已支付成功，车票信息已发送至您的手机和邮箱
        </p>
        <div class="bg-gray-50 rounded-lg p-4 text-left mb-6 space-y-2">
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">订单号：</span>
            <span class="font-medium text-gray-900">{{ orderInfo.orderNumber }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">支付金额：</span>
            <span class="font-bold text-orange-600">￥{{ orderInfo.totalAmount }}</span>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="flex justify-center gap-3">
          <button 
            @click="goToOrders" 
            class="px-5 py-2.5 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
          >
            查看订单
          </button>
          <button 
            @click="goToHome" 
            class="px-5 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            返回首页
          </button>
        </div>
      </template>
    </Modal>

    <!-- 订单超时弹窗 -->
    <Modal
      v-model="showTimeoutModal"
      title="订单超时"
      :show-close="false"
      :close-on-click-modal="false"
    >
      <div class="text-center py-6">
        <div class="mb-5 inline-flex items-center justify-center w-16 h-16 rounded-full bg-red-100 text-red-500">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
        </div>
        <h3 class="text-xl font-bold text-gray-900 mb-2">订单已超时</h3>
        <p class="text-gray-500">
          很抱歉，您的订单因超时未支付已自动取消，请重新下单。
        </p>
      </div>
      <template #footer>
        <div class="flex justify-center">
          <button 
            @click="handleTimeoutConfirm" 
            class="px-6 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            确定
          </button>
        </div>
      </template>
    </Modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import toast from '@/components/Toast'
import Modal from '@/components/Modal/Modal.vue'
import request from '@/utils/request'
import { payOrder, getOrderDetail } from '@/api/order'
import { getTicketDetail } from '@/api/ticket'
import OrderInfoCard from './components/OrderInfoCard.vue'
import CountdownCard from './components/CountdownCard.vue'
import PaymentMethodsCard from './components/PaymentMethodsCard.vue'
import PaymentNoticeCard from './components/PaymentNoticeCard.vue'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(true)
const processing = ref(false)
const showSuccessModal = ref(false)
const showTimeoutModal = ref(false)
const currentStep = ref(3) // 1:选择车票, 2:填写信息, 3:确认支付, 4:支付完成
const remainingTime = ref(15 * 60) // 15分钟倒计时（秒）
const timer = ref(null)

const steps = [
  { title: '选择车票' },
  { title: '填写信息' },
  { title: '确认支付' },
  { title: '支付完成' }
]

// 订单信息
const orderInfo = ref({
  orderId: null,
  orderNumber: '',
  amount: 0,
  totalAmount: 0,
  status: 0,
  expireTime: '',
  createTime: '',
  trainCode: '',
  date: '',
  startTime: '',
  endTime: '',
  originStation: '',
  destinationStation: '',
  seatTypeName: '',
  ticketPrice: 0,
  passengers: []
})

// 支付方式
const paymentMethods = ref([
  {
    id: 'alipay',
    name: '支付宝',
    description: '推荐使用，支付快捷安全',
    available: true
  },
  {
    id: 'wechat',
    name: '微信支付',
    description: '功能待开发',
    available: false
  }
])

const selectedPaymentMethod = ref(null)

// 返回上一页
const goBack = () => {
  router.back()
}

// 获取订单信息
const loadOrderInfo = async () => {
  try {
    loading.value = true
    
    // 从路由参数获取orderId
    const orderId = route.query.orderId
    if (!orderId) {
      toast.error('订单ID缺失')
      router.back()
      return
    }

    // 调用API获取订单详情
    const response = await getOrderDetail(orderId)
    if (response.code !== 200) {
      toast.error(response.message || '获取订单信息失败')
      router.back()
      return
    }

    const orderData = response.data
    
    // 验证订单数据完整性
    if (!orderData.orderId || !orderData.totalAmount) {
      toast.error('订单数据不完整')
      router.back()
      return
    }

    // 获取车票详情
    let ticketInfo = null
    if (orderData.ticketId) {
      try {
        const ticketResponse = await getTicketDetail(orderData.ticketId)
        if (ticketResponse.code === 200) {
          ticketInfo = ticketResponse.data
        }
      } catch (error) {
        toast.warning('获取车票信息失败，部分信息可能不完整')
      }
    }

    // 处理 priceDetail 数据
    const priceDetail = orderData.priceDetail || {}
    const baseAmount = priceDetail.baseAmount || orderData.totalAmount
    const discountAmount = priceDetail.discountAmount || 0
    const totalAmount = priceDetail.totalAmount || orderData.totalAmount
    
    // 处理乘客信息
    const passengers = orderData.passengers ? orderData.passengers.map((passenger, index) => {
      // 从 priceDetail.breakdown 中获取价格信息
      const breakdown = priceDetail.breakdown?.[index] || {}
      return {
        name: passenger.name,
        passengerType: passenger.passengerType,
        seatPosition: passenger.seatPosition,
        idType: '身份证',
        idNumber: '***********',
        basePrice: breakdown.basePrice || 0,
        discount: breakdown.discount || 0,
        actualPrice: breakdown.actualPrice || breakdown.basePrice || 0
      }
    }) : []

    // 获取座位类型
    const getSeatTypeName = (seatType) => {
      const seatTypeMap = {
        1: '一等座',
        2: '二等座',
        3: '商务座',
        4: '特等座'
      }
      return seatTypeMap[seatType] || '二等座'
    }

    // 处理车站信息：兼容对象和字符串
    const getStationInfo = (stationData, defaultName) => {
      if (!stationData) return { name: defaultName }
      
      // 如果是字符串，直接返回
      if (typeof stationData === 'string') {
        return { name: stationData }
      }
      
      // 如果是对象，提取 name 字段
      if (typeof stationData === 'object') {
        return {
          name: stationData.name || defaultName,
          code: stationData.code || '',
          id: stationData.id || null
        }
      }
      
      return { name: defaultName }
    }

    // 设置订单信息 - 根据后端实际返回的数据结构
    const processedOrderData = {
      orderId: orderData.orderId,
      orderNumber: orderData.orderId,
      amount: totalAmount,
      totalAmount: totalAmount,
      baseAmount: baseAmount,
      discountAmount: discountAmount,
      status: orderData.status ?? 0,
      expireTime: orderData.expireTime,
      createTime: orderData.createTime,
      payTime: orderData.payTime,
      passengers: passengers,
      // 从车票信息获取车次信息
      trainCode: ticketInfo?.trainCode || 'G1234',
      date: ticketInfo?.departureTime || ticketInfo?.departureDate || new Date().toISOString(),
      startTime: ticketInfo?.departureTime || new Date().toISOString(),
      endTime: ticketInfo?.arrivalTime || new Date().toISOString(),
      originStation: getStationInfo(ticketInfo?.originStation, '出发站'),
      destinationStation: getStationInfo(ticketInfo?.destinationStation, '到达站'),
      seatType: { name: getSeatTypeName(ticketInfo?.seatType) },
      seatTypeName: getSeatTypeName(ticketInfo?.seatType),
      ticketPrice: passengers.length > 0 ? passengers[0].actualPrice : totalAmount
    }
    
    Object.assign(orderInfo.value, processedOrderData)
    
    // 计算剩余支付时间
    if (orderData.expireTime) {
      const expireTime = new Date(orderData.expireTime).getTime()
      const currentTime = new Date().getTime()
      const timeDiff = expireTime - currentTime
      
      if (timeDiff > 0) {
        remainingTime.value = Math.floor(timeDiff / 1000)
      } else {
        toast.error('订单已过期')
        router.back()
        return
      }
    }
    
  } catch (error) {
    toast.error('获取订单信息失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 选择支付方式
const selectPaymentMethod = (method) => {
  if (!method.available) {
    toast.warning('该支付方式暂不可用')
    return
  }
  selectedPaymentMethod.value = method
}

// 处理支付
const processPayment = async () => {
  if (!selectedPaymentMethod.value) {
    toast.warning('请选择支付方式')
    return
  }

  if (!selectedPaymentMethod.value.available) {
    toast.warning('该支付方式暂不可用')
    return
  }

  if (selectedPaymentMethod.value.id === 'wechat') {
    toast.info('微信支付功能待开发，敬请期待')
    return
  }

  try {
    processing.value = true
    
    // 调用支付宝支付API
    if (selectedPaymentMethod.value.id === 'alipay') {
      try {
        toast.info('正在跳转支付宝支付...')
        
        // 调用后端支付接口，传入订单ID
        const response = await payOrder(orderInfo.value.orderId)
        
        
        // 后端返回的是支付宝表单HTML
        if (response.code === 200 && response.data) {
          // response.data 是支付宝表单HTML字符串
          const alipayForm = response.data
          
          // 创建一个临时div来承载表单
          const div = document.createElement('div')
          div.innerHTML = alipayForm
          document.body.appendChild(div)
          
          // 查找表单并提交
          const form = div.querySelector('form')
          if (form) {
            // 提交表单，会自动跳转到支付宝页面
            form.submit()
            
            // 停止倒计时
            if (timer.value) {
              clearInterval(timer.value)
              timer.value = null
            }
            
            // 提示用户
            toast.success('正在跳转到支付宝支付页面...')
            
            // 注意：实际支付成功后，支付宝会通过回调通知后端
            // 前端可以通过轮询或websocket来获取支付状态更新
            // 这里先不显示成功弹窗，等用户从支付宝返回后再处理
          } else {
            toast.error('支付表单加载失败')
          }
        } else {
          toast.error(response.message || '获取支付信息失败')
        }
      } catch (apiError) {
        
        // 检查是否是404或接口不存在的错误
        if (apiError.response?.status === 404 || apiError.message?.includes('404')) {
          toast.error('支付接口不存在，请联系管理员')
        } else {
          toast.error('支付失败：' + (apiError.message || '未知错误'))
        }
      }
    }
  } catch (error) {
    toast.error('支付失败，请重试')
  } finally {
    processing.value = false
  }
}

// 前往订单页面
const goToOrders = () => {
  router.push('/user/order')
}

// 返回首页
const goToHome = () => {
  router.push('/')
}

// 订单超时确认
const handleTimeoutConfirm = () => {
  showTimeoutModal.value = false
  router.push('/ticket')
}

// 启动倒计时
const startCountdown = () => {
  timer.value = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      // 倒计时结束，订单超时
      clearInterval(timer.value)
      showTimeoutModal.value = true
    }
  }, 1000)
}

// 组件挂载时初始化
onMounted(async () => {
  // 从路由参数获取orderId
  const orderId = route.query.orderId
  
  if (!orderId) {
    toast.error('缺少订单ID，请重新下单')
    router.back()
    return
  }
  
  await loadOrderInfo()
  
  // 默认选择第一个可用的支付方式
  const availableMethod = paymentMethods.value.find(method => method.available)
  if (availableMethod) {
    selectedPaymentMethod.value = availableMethod
  }
  
  // 启动倒计时
  startCountdown()
})

// 组件卸载时清理定时器
onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})
</script>