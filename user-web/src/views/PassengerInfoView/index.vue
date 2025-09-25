<template>
  <div class="passenger-info-page">
    <!-- 订单信息栏 -->
    <OrderInfoBar :order-info="orderInfo" />

    <!-- 乘客信息表单 -->
    <PassengerForm
      ref="passengerFormRef"
      v-model="passengerForm"
      :order-info="orderInfo"
    />

    <!-- 联系人信息 -->
    <ContactForm
      ref="contactFormRef"
      v-model="contactForm"
    />

    <!-- 增值服务 -->
    <ServiceOptions v-model="selectedServices" :passenger-count="orderInfo.passengers" />

    <!-- 底部操作栏 -->
    <BottomBar
      :order-info="orderInfo"
      :service-price="servicePrice"
      :final-price="finalPrice"
      :submitting="submitting"
      @go-back="goBack"
      @submit-order="submitOrder"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { createOrder } from '@/api/order'
import type { CreateOrderRequest, PassengerInfo } from '@/api/order'

// 导入子组件
import OrderInfoBar from './components/OrderInfoBar.vue'
import PassengerForm from './components/PassengerForm.vue'
import ContactForm from './components/ContactForm.vue'
import ServiceOptions from './components/ServiceOptions.vue'
import BottomBar from './components/BottomBar.vue'

const router = useRouter()
const route = useRoute()

// 表单引用
const passengerFormRef = ref<FormInstance>()
const contactFormRef = ref<FormInstance>()

// 订单信息
const orderInfo = ref({
  trainId: '',
  trainNumber: '',
  departure: '',
  destination: '',
  date: '',
  seatType: '',
  seats: '',
  seatNumbers: [] as string[],
  totalPrice: 0,
  passengers: 1
})

// 乘客信息表单
const passengerForm = ref({
  passengers: [] as PassengerInfo[]
})

// 联系人信息表单
const contactForm = ref({
  name: '',
  phone: '',
  email: ''
})

// 增值服务
const selectedServices = ref<string[]>([])

// 状态
const submitting = ref(false)

// 初始化
onMounted(() => {
  const query = route.query

  orderInfo.value = {
    trainId: query.trainId as string,
    trainNumber: query.trainNumber as string,
    departure: query.departure as string,
    destination: query.destination as string,
    date: query.date as string,
    seatType: query.seatType as string,
    seats: query.seats as string,
    seatNumbers: (query.seats as string)?.split(',') || [],
    totalPrice: Number(query.totalPrice) || 0,
    passengers: Number(query.passengers) || 1
  }

  // 初始化乘客信息
  passengerForm.value.passengers = Array.from({ length: orderInfo.value.passengers }, () => ({
    name: '',
    idCard: '',
    phone: '',
    passengerType: 'adult'
  }))

  // 确保trainId是字符串类型
  if (orderInfo.value.trainId) {
    orderInfo.value.trainId = String(orderInfo.value.trainId)
  }
})

// 计算增值服务价格
const servicePrice = computed(() => {
  let price = 0
  const passengerCount = orderInfo.value.passengers

  if (selectedServices.value.includes('insurance')) {
    price += 20 * passengerCount
  }
  if (selectedServices.value.includes('meal')) {
    price += 30 * passengerCount
  }
  if (selectedServices.value.includes('priority')) {
    price += 10 * passengerCount
  }

  return price
})

// 计算最终价格
const finalPrice = computed(() => {
  return orderInfo.value.totalPrice + servicePrice.value
})

// 返回
const goBack = () => {
  router.back()
}

// 提交订单
const submitOrder = async () => {
  // 验证表单
  const passengerValid = await passengerFormRef.value?.validate().catch(() => false)
  const contactValid = await contactFormRef.value?.validate().catch(() => false)

  if (!passengerValid || !contactValid) {
    ElMessage.error('请完善必填信息')
    return
  }

  // 确认提交
  try {
    await ElMessageBox.confirm(
      `确认提交订单？总金额：¥${finalPrice.value}`,
      '确认订单',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
  } catch {
    return
  }

  submitting.value = true

  try {
    const orderData: CreateOrderRequest = {
      trainId: Number(orderInfo.value.trainId),
      date: orderInfo.value.date,
      passengers: passengerForm.value.passengers,
      seats: orderInfo.value.seatNumbers.map((seat, index) => ({
        carriageNo: 1,
        seatNo: seat,
        seatType: orderInfo.value.seatType,
        price: orderInfo.value.totalPrice / orderInfo.value.passengers
      }))
    }

    const response = await createOrder(orderData)

    ElMessage.success('订单创建成功')

    // 跳转到支付页面
    router.push({
      path: '/mall-payment',
      query: {
        orderId: response.orderId,
        orderNumber: response.orderNo,
        amount: finalPrice.value
      }
    })
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.passenger-info-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 100px;
}
</style>
