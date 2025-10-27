<template>
  <div class="payment-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <el-button 
          type="text" 
          @click="goBack" 
          class="back-btn"
        >
          <el-icon><ArrowLeft /></el-icon>
          返回订单详情
        </el-button>
        <h2 class="page-title">订单支付</h2>
      </div>
    </div>

    <!-- 支付进度条 -->
    <div class="progress-section">
      <div class="container">
        <el-steps :active="currentStep" align-center>
          <el-step title="选择车票" icon="Tickets" />
          <el-step title="填写信息" icon="User" />
          <el-step title="确认支付" icon="CreditCard" />
          <el-step title="支付完成" icon="Check" />
        </el-steps>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 主要内容 -->
    <div v-else class="main-content">
      <div class="container">
        <div class="payment-layout">
          <!-- 左侧：订单信息 -->
          <div class="order-section">
            <!-- 订单详情卡片 -->
            <OrderInfoCard :order-info="orderInfo" />

            <!-- 支付倒计时 -->
            <CountdownCard :remaining-time="remainingTime" />
          </div>

          <!-- 右侧：支付方式 -->
          <div class="payment-section">
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
    </div>

    <!-- 支付成功弹窗 -->
    <el-dialog
      v-model="showSuccessModal"
      title="支付成功"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <div class="success-modal">
        <div class="success-icon">
          <el-icon size="60" color="#52c41a"><CircleCheck /></el-icon>
        </div>
        <div class="success-content">
          <h3 class="success-title">支付成功！</h3>
          <p class="success-message">
            您的订单已支付成功，车票信息已发送至您的手机和邮箱
          </p>
          <div class="success-info">
            <div class="info-item">
              <span class="label">订单号：</span>
              <span class="value">{{ orderInfo.orderNumber }}</span>
            </div>
            <div class="info-item">
              <span class="label">支付金额：</span>
              <span class="value">￥{{ orderInfo.totalAmount }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="success-actions">
          <el-button @click="goToOrders">查看订单</el-button>
          <el-button type="primary" @click="goToHome">返回首页</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, CircleCheck, Tickets, User, Check
} from '@element-plus/icons-vue'
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
const currentStep = ref(2) // 当前在支付步骤
const remainingTime = ref(15 * 60) // 15分钟倒计时（秒）
const timer = ref(null)

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
    icon: '/images/alipay.png',
    available: true
  },
  {
    id: 'wechat',
    name: '微信支付',
    description: '功能待开发',
    icon: '/images/wechat-pay.png',
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
      ElMessage.error('订单ID缺失')
      router.back()
      return
    }

    // 调用API获取订单详情
    const response = await getOrderDetail(orderId)
    if (response.code !== 200) {
      ElMessage.error(response.message || '获取订单信息失败')
      router.back()
      return
    }

    const orderData = response.data
    
    // 验证订单数据完整性
    if (!orderData.orderId || !orderData.totalAmount) {
      ElMessage.error('订单数据不完整')
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
        console.error('获取车票信息失败:', error)
        ElMessage.warning('获取车票信息失败，部分信息可能不完整')
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
        ElMessage.error('订单已过期')
        router.back()
        return
      }
    }
    
  } catch (error) {
    console.error('获取订单信息失败:', error)
    ElMessage.error('获取订单信息失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 选择支付方式
const selectPaymentMethod = (method) => {
  if (!method.available) {
    ElMessage.warning('该支付方式暂不可用')
    return
  }
  selectedPaymentMethod.value = method
}

// 处理支付
const processPayment = async () => {
  if (!selectedPaymentMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }

  if (!selectedPaymentMethod.value.available) {
    ElMessage.warning('该支付方式暂不可用')
    return
  }

  if (selectedPaymentMethod.value.id === 'wechat') {
    ElMessage.info('微信支付功能待开发，敬请期待')
    return
  }

  try {
    processing.value = true
    
    // 调用支付宝支付API
    if (selectedPaymentMethod.value.id === 'alipay') {
      try {
        ElMessage.info('正在跳转支付宝支付...')
        
        // 调用后端支付接口，传入订单ID
        const response = await payOrder(orderInfo.value.orderId)
        
        console.log('=== 支付接口响应 ===')
        console.log('响应数据:', response)
        console.log('==================')
        
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
            ElMessage.success('正在跳转到支付宝支付页面...')
            
            // 注意：实际支付成功后，支付宝会通过回调通知后端
            // 前端可以通过轮询或websocket来获取支付状态更新
            // 这里先不显示成功弹窗，等用户从支付宝返回后再处理
          } else {
            console.error('未找到支付表单')
            ElMessage.error('支付表单加载失败')
          }
        } else {
          ElMessage.error(response.message || '获取支付信息失败')
        }
      } catch (apiError) {
        console.error('支付API调用失败:', apiError)
        
        // 检查是否是404或接口不存在的错误
        if (apiError.response?.status === 404 || apiError.message?.includes('404')) {
          ElMessageBox.confirm(
            '后端支付接口调用失败，是否模拟支付成功？',
            '提示',
            {
              confirmButtonText: '模拟支付',
              cancelButtonText: '取消',
              type: 'warning'
            }
          ).then(() => {
            // 模拟支付成功
            ElMessage.success('支付成功（模拟）！')
            orderInfo.value.status = 1
            if (timer.value) {
              clearInterval(timer.value)
              timer.value = null
            }
            showSuccessModal.value = true
            currentStep.value = 3
          }).catch(() => {
            ElMessage.info('已取消支付')
          })
        } else {
          ElMessage.error('支付失败：' + (apiError.message || '未知错误'))
        }
      }
    }
  } catch (error) {
    console.error('支付过程出错:', error)
    ElMessage.error('支付失败，请重试')
  } finally {
    processing.value = false
  }
}

// 前往订单页面
const goToOrders = () => {
  router.push('/orders')
}

// 返回首页
const goToHome = () => {
  router.push('/')
}

// 启动倒计时
const startCountdown = () => {
  timer.value = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      // 倒计时结束，订单超时
      clearInterval(timer.value)
      ElMessageBox.alert('订单已超时，请重新下单', '订单超时', {
        confirmButtonText: '确定',
        callback: () => {
          router.push('/tickets')
        }
      })
    }
  }, 1000)
}

// 组件挂载时初始化
onMounted(async () => {
  // 从路由参数获取orderId
  const orderId = route.query.orderId
  
  if (!orderId) {
    ElMessage.error('缺少订单ID，请重新下单')
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

<style scoped>
.payment-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.page-header {
  background: white;
  padding: 20px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

.progress-section {
  background: white;
  padding: 30px 0;
  border-bottom: 1px solid #f0f0f0;
}

.loading-container {
  padding: 40px 20px;
}

.main-content {
  padding: 30px 20px;
}

.payment-layout {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 30px;
}


/* 支付成功弹窗 */
.success-modal {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  margin-bottom: 20px;
}

.success-title {
  font-size: 20px;
  color: #333;
  margin: 0 0 12px 0;
}

.success-message {
  color: #666;
  margin: 0 0 20px 0;
  line-height: 1.5;
}

.success-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;
  background: #fafafa;
  padding: 16px;
  border-radius: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
}

.success-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .payment-layout {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}
</style>