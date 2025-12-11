<template>
  <div class="flex-1 w-full flex flex-col bg-white rounded-3xl shadow-sm border border-slate-100 p-6 h-full overflow-hidden">
    <!-- 页面标题 -->
    <div class="border-b border-gray-200 pb-4 mb-6 flex-shrink-0">
      <h2 class="text-2xl font-bold text-gray-900">我的订单</h2>
      <p class="mt-1 text-sm text-gray-500">查看和管理您的订单信息</p>
    </div>

    <!-- 订单类型切换与筛选 -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6 flex-shrink-0">
      <!-- 类型切换 -->
      <div class="flex p-1 bg-gray-100 rounded-lg">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          @click="handleTypeChange(tab.value)"
          :class="[
            'px-4 py-2 text-sm font-medium rounded-md transition-all duration-200',
            orderType === tab.value
              ? 'bg-white text-blue-600 shadow-sm'
              : 'text-gray-500 hover:text-gray-700'
          ]"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 筛选 -->
      <div class="flex items-center gap-3 w-full sm:w-auto">
        <div class="relative w-full sm:w-48">
          <select
            v-model="filterForm.status"
            @change="handleFilter"
            class="w-full appearance-none bg-white border border-gray-300 text-gray-700 py-2 px-3 pr-8 rounded-lg leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-shadow cursor-pointer"
          >
            <option value="">全部状态</option>
            <template v-if="orderType === 'train'">
              <option value="待支付">待支付</option>
              <option value="已支付">已支付</option>
              <option value="已出票">已出票</option>
              <option value="已取消">已取消</option>
              <option value="已退票">已退票</option>
            </template>
            <template v-else>
              <option :value="0">待发货</option>
              <option :value="1">已发货</option>
              <option :value="2">已完成</option>
              <option :value="3">已取消</option>
            </template>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-500">
            <svg class="fill-current h-4 w-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
              <path d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z"/>
            </svg>
          </div>
        </div>
        
        <button 
            @click="handleReset"
            class="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition-colors text-sm font-medium active:bg-gray-300"
        >
            重置
        </button>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="flex-1 overflow-y-auto min-h-0">
      <div v-if="loading" class="flex justify-center py-20">
        <svg class="animate-spin h-8 w-8 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      </div>

      <div v-else-if="orderList.length === 0" class="text-center py-20 bg-gray-50 rounded-xl border border-dashed border-gray-300">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-gray-100 mb-4">
          <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"></path>
          </svg>
        </div>
        <p class="text-gray-500">暂无订单数据</p>
      </div>

      <div v-else class="space-y-4">
        <!-- 车票订单 -->
        <template v-if="orderType === 'train'">
          <div
            v-for="order in orderList"
            :key="order.orderId"
            @click="handleViewDetail(order.orderId)"
            class="group bg-white border border-gray-200 rounded-xl overflow-hidden transition-all duration-300 hover:shadow-lg hover:border-blue-300 cursor-pointer relative"
          >
             <div class="p-5">
                <div class="flex flex-wrap justify-between items-center mb-4 pb-4 border-b border-gray-100">
                  <div class="flex items-center gap-4 text-sm">
                    <span class="font-medium text-gray-900">订单号：{{ order.orderId }}</span>
                    <span class="text-gray-500">{{ order.createTime }}</span>
                  </div>
                  <div :class="['px-3 py-1 rounded-full text-xs font-medium', getStatusClass(order.status)]">
                    {{ order.status }}
                  </div>
                </div>

                <div class="flex flex-col md:flex-row justify-between items-center gap-6">
                  <div class="flex-1 w-full">
                     <div class="text-lg font-bold text-blue-600 mb-2">{{ order.trainNumber }}</div>
                     <div class="flex items-center gap-3 text-gray-900 font-medium">
                        <span class="text-xl">{{ order.departure }}</span>
                        <svg class="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"></path>
                        </svg>
                        <span class="text-xl">{{ order.arrival }}</span>
                     </div>
                     <div class="flex items-center gap-8 mt-1 text-sm text-gray-500">
                        <span>{{ order.departureTime }}</span>
                        <span>{{ order.arrivalTime }}</span>
                     </div>
                  </div>

                  <div class="flex flex-row md:flex-col items-center md:items-end gap-2 md:gap-1 w-full md:w-auto justify-between md:justify-end border-t md:border-t-0 pt-4 md:pt-0 border-gray-100">
                     <div class="text-gray-600">{{ order.seatType }}</div>
                     <div class="text-2xl font-bold text-red-500">¥{{ order.price }}</div>
                  </div>
                </div>
             </div>
             <!-- Hover Indicator -->
             <div class="absolute top-0 left-0 w-1 h-full bg-blue-500 transform -translate-x-full group-hover:translate-x-0 transition-transform duration-300"></div>
          </div>
        </template>
        
        <!-- 商城订单 -->
        <template v-else>
          <div
            v-for="order in orderList"
            :key="order.orderNumber"
            class="bg-white border border-gray-200 rounded-xl overflow-hidden p-5"
          >
             <div class="flex flex-wrap justify-between items-center mb-4 pb-4 border-b border-gray-100">
                  <div class="flex items-center gap-4 text-sm">
                    <span class="font-medium text-gray-900">订单号：{{ order.orderNumber }}</span>
                    <span class="text-gray-500">{{ formatDateTime(order.createTime) }}</span>
                  </div>
                  <div :class="['px-3 py-1 rounded-full text-xs font-medium', getMallStatusClass(order.status)]">
                    {{ order.statusText }}
                  </div>
             </div>

             <div class="flex flex-col md:flex-row gap-6">
                <div class="flex gap-4 flex-1">
                   <img :src="order.itemImage" :alt="order.itemName" class="w-20 h-20 object-cover rounded-lg bg-gray-100 border border-gray-200" />
                   <div>
                      <div class="font-medium text-gray-900 text-lg">{{ order.itemName }}</div>
                      <div class="text-gray-500 mt-1">数量：{{ order.quantity }}</div>
                   </div>
                </div>

                <div class="flex-1 space-y-1 text-sm text-gray-600 bg-gray-50 p-3 rounded-lg">
                   <div class="flex gap-2"><span class="font-medium min-w-[60px]">收货人:</span> {{ order.recipientName }}</div>
                   <div class="flex gap-2"><span class="font-medium min-w-[60px]">电话:</span> {{ order.recipientPhone }}</div>
                   <div class="flex gap-2"><span class="font-medium min-w-[60px]">地址:</span> {{ order.recipientAddress }}</div>
                </div>

                <div class="flex flex-col justify-center items-end min-w-[100px]">
                   <span class="text-sm text-gray-500">总积分</span>
                   <span class="text-xl font-bold text-yellow-600">{{ order.totalPoints }}</span>
                </div>
             </div>
          </div>
        </template>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="flex justify-center items-center gap-2 pt-6">
       <button 
          @click="handleCurrentChange(currentPage - 1)" 
          :disabled="currentPage === 1"
          class="px-3 py-1 rounded border border-gray-300 text-gray-600 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          上一页
       </button>
       <span class="text-sm text-gray-600">第 {{ currentPage }} 页</span>
       <button 
          @click="handleCurrentChange(currentPage + 1)" 
          :disabled="orderList.length < pageSize"
          class="px-3 py-1 rounded border border-gray-300 text-gray-600 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          下一页
       </button>
    </div>

    <!-- 订单详情弹窗 -->
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="transform scale-95 opacity-0"
      enter-to-class="transform scale-100 opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="transform scale-100 opacity-100"
      leave-to-class="transform scale-95 opacity-0"
    >
      <div v-if="detailVisible" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6" role="dialog">
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-black/50 backdrop-blur-sm transition-opacity" @click="handleCloseDetail"></div>

        <!-- Modal Panel -->
        <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto flex flex-col">
          <!-- Header -->
          <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100 sticky top-0 bg-white z-10">
            <h3 class="text-lg font-bold text-gray-900">订单详情</h3>
            <button @click="handleCloseDetail" class="text-gray-400 hover:text-gray-600 transition-colors p-1 rounded-full hover:bg-gray-100">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>

          <!-- Content -->
          <div v-if="orderDetail" class="p-6 space-y-6">
             <!-- Basic Info Grid -->
             <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div class="p-3 bg-gray-50 rounded-lg">
                   <div class="text-xs text-gray-500 mb-1">订单号</div>
                   <div class="font-medium break-all">{{ orderDetail.orderId }}</div>
                </div>
                 <div class="p-3 bg-gray-50 rounded-lg">
                   <div class="text-xs text-gray-500 mb-1">订单状态</div>
                   <span :class="['px-2 py-0.5 rounded text-xs font-medium', getStatusClass(orderDetail.status)]">
                      {{ orderDetail.status }}
                   </span>
                </div>
                 <div class="p-3 bg-gray-50 rounded-lg">
                   <div class="text-xs text-gray-500 mb-1">车次</div>
                   <div class="font-medium text-blue-600">{{ orderDetail.trainNumber }}</div>
                </div>
                 <div class="p-3 bg-gray-50 rounded-lg">
                   <div class="text-xs text-gray-500 mb-1">座位类型</div>
                   <div class="font-medium">{{ orderDetail.seatType }}</div>
                </div>
                <div class="p-3 bg-gray-50 rounded-lg">
                   <div class="text-xs text-gray-500 mb-1">出发站</div>
                   <div class="font-medium">{{ orderDetail.departure }}</div>
                </div>
                <div class="p-3 bg-gray-50 rounded-lg">
                   <div class="text-xs text-gray-500 mb-1">到达站</div>
                   <div class="font-medium">{{ orderDetail.arrival }}</div>
                </div>
                 <div class="p-3 bg-gray-50 rounded-lg">
                   <div class="text-xs text-gray-500 mb-1">出发时间</div>
                   <div class="font-medium">{{ orderDetail.departureTime }}</div>
                </div>
                <div class="p-3 bg-gray-50 rounded-lg">
                   <div class="text-xs text-gray-500 mb-1">到达时间</div>
                   <div class="font-medium">{{ orderDetail.arrivalTime }}</div>
                </div>
                 <div class="p-3 bg-gray-50 rounded-lg">
                   <div class="text-xs text-gray-500 mb-1">票价</div>
                   <div class="font-bold text-red-500">¥{{ orderDetail.price }}</div>
                </div>
                 <div class="p-3 bg-gray-50 rounded-lg">
                   <div class="text-xs text-gray-500 mb-1">下单时间</div>
                   <div class="font-medium text-sm">{{ orderDetail.createTime }}</div>
                </div>
             </div>

             <!-- Passengers -->
             <div v-if="orderDetail.passengers && orderDetail.passengers.length > 0">
                <h4 class="font-bold text-gray-900 mb-3 flex items-center gap-2">
                   <svg class="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
                   </svg>
                   乘车人信息
                </h4>
                <div class="overflow-hidden border border-gray-200 rounded-lg">
                   <table class="min-w-full divide-y divide-gray-200">
                      <thead class="bg-gray-50">
                         <tr>
                            <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">姓名</th>
                            <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">身份证号</th>
                            <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">座位号</th>
                            <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">实际票价</th>
                         </tr>
                      </thead>
                      <tbody class="bg-white divide-y divide-gray-200">
                         <tr v-for="(passenger, idx) in orderDetail.passengers" :key="idx">
                            <td class="px-4 py-3 text-sm text-gray-900">{{ passenger.name }}</td>
                            <td class="px-4 py-3 text-sm text-gray-500 font-mono">{{ passenger.idCard }}</td>
                            <td class="px-4 py-3 text-sm text-gray-900">{{ passenger.seatPosition || '-' }}</td>
                            <td class="px-4 py-3 text-sm text-gray-900">¥{{ passenger.actualPrice }}</td>
                         </tr>
                      </tbody>
                   </table>
                </div>
             </div>
          </div>
          
          <!-- Footer -->
          <div class="p-6 border-t border-gray-100 bg-gray-50 flex justify-end gap-3 sticky bottom-0 z-10">
            <button 
              v-if="orderDetail && orderDetail.statusCode === 0" 
              @click="handlePay"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium shadow-sm hover:shadow-md"
            >
              去支付
            </button>
            <button 
              @click="handleCloseDetail"
              class="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors font-medium"
            >
              关闭
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Toast Notification -->
    <Transition
      enter-active-class="transition ease-out duration-300"
      enter-from-class="transform opacity-0 translate-y-2"
      enter-to-class="transform opacity-100 translate-y-0"
      leave-active-class="transition ease-in duration-200"
      leave-from-class="transform opacity-100 translate-y-0"
      leave-to-class="transform opacity-0 translate-y-2"
    >
      <div v-if="toast.show" :class="['fixed bottom-8 left-1/2 transform -translate-x-1/2 z-[100] px-6 py-3 rounded-full shadow-xl flex items-center gap-3', toast.type === 'success' ? 'bg-green-600 text-white' : 'bg-red-600 text-white']">
         <svg v-if="toast.type === 'success'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
         </svg>
         <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
         </svg>
         <span class="font-medium">{{ toast.message }}</span>
      </div>
    </Transition>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList, getOrderDetail, getOrderPage } from '@/api/order.js'
import { getTicketDetail } from '@/api/ticket.js'
import { getMallOrders } from '@/api/points.js'

export default {
  name: 'UserOrder',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const orderList = ref([])
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const detailVisible = ref(false)
    const orderDetail = ref(null)
    const orderType = ref('train') // 'train' 或 'mall'
    const toast = reactive({ show: false, message: '', type: 'success' })

    const tabs = [
      { label: '车票订单', value: 'train' },
      { label: '商城订单', value: 'mall' }
    ]

    // 车票详情缓存，避免重复请求
    const ticketCache = ref({})

    const filterForm = reactive({
      status: '',
    })

    const showToast = (message, type = 'success') => {
      toast.show = true
      toast.message = message
      toast.type = type
      setTimeout(() => {
        toast.show = false
      }, 3000)
    }

    // 获取车票详情（带缓存）
    const getTicketInfo = async (ticketId) => {
      if (!ticketId) return null

      // 如果缓存中已存在，直接返回
      if (ticketCache.value[ticketId]) {
        return ticketCache.value[ticketId]
      }

      // 请求车票详情
      try {
        const response = await getTicketDetail(ticketId)
        if (response.code === 200) {
          ticketCache.value[ticketId] = response.data
          return response.data
        }
      } catch (error) {
        console.error('获取车票详情失败', error)
      }

      return null
    }

    const fetchOrderList = async () => {
      if (orderType.value === 'train') {
        await fetchTrainOrders()
      } else {
        await fetchMallOrders()
      }
    }

    // 获取车票订单
    const fetchTrainOrders = async () => {
      try {
        loading.value = true
        const params = {
          pageNum: currentPage.value,
          pageSize: pageSize.value,
          status: filterForm.status ? getStatusCode(filterForm.status) : undefined,
        }

        const response = await getOrderPage(params)

        if (response.code === 200) {
          // 处理分页响应数据
          const pageData = response.data || {}
          const orders = pageData.records || []
          total.value = pageData.total || 0

          // 收集所有需要查询的 ticketId（去重）
          const ticketIds = [...new Set(orders.map((order) => order.ticketId).filter((id) => id))]

          // 批量获取车票详情（并行请求，但会使用缓存）
          await Promise.all(ticketIds.map((ticketId) => getTicketInfo(ticketId)))

          // 直接使用订单数据和缓存的车票详情
          const processedOrders = orders.map((order) => {
            // 获取第一个座位的类型（如果有多个座位，通常类型相同）
            const firstSeat = order.seat && order.seat.length > 0 ? order.seat[0] : null
            const seatType = firstSeat ? getSeatTypeText(firstSeat.seatType) : '-'

            // 从缓存获取车票信息
            const ticketInfo = ticketCache.value[order.ticketId]

            return {
              ...order,
              status: getStatusText(order.status),
              trainNumber: ticketInfo?.trainInfo?.name || order.trainNumber || '-',
              departure: order.startStation || '出发站',
              arrival: order.endStation || '到达站',
              departureTime: order.startTime ? formatDateTime(order.startTime) : '',
              arrivalTime: order.endTime ? formatDateTime(order.endTime) : '',
              seatType: seatType,
              price: order.amount || 0,
              createTime: order.createTime ? formatDateTime(order.createTime) : '',
            }
          })

          orderList.value = processedOrders
        } else {
          showToast(response.message || '获取订单列表失败', 'error')
        }
      } catch (error) {
        showToast('获取订单列表失败', 'error')
      } finally {
        loading.value = false
      }
    }

    // 获取商城订单
    const fetchMallOrders = async () => {
      try {
        loading.value = true
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          status: filterForm.status !== '' ? filterForm.status : undefined,
        }

        const response = await getMallOrders(params)
        const pageData = response.data || {}
        orderList.value = pageData.records || []
        total.value = pageData.total || 0
      } catch (error) {
        showToast('获取商城订单失败', 'error')
      } finally {
        loading.value = false
      }
    }

    const handleTypeChange = (type) => {
      orderType.value = type
      filterForm.status = ''
      currentPage.value = 1
      fetchOrderList()
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
          showToast(response.message || '获取订单详情失败', 'error')
          return
        }

        const orderData = response.data

        // 获取座位信息
        const firstSeat = orderData.seat && orderData.seat.length > 0 ? orderData.seat[0] : null
        const seatType = firstSeat ? getSeatTypeText(firstSeat.seatType) : '-'
        const seatNumber = firstSeat ? firstSeat.fullSeatCode : ''

        // 获取车票详情（使用缓存）
        const ticketInfo = await getTicketInfo(orderData.ticketId)

        // 处理订单详情数据，使用订单中的站点和座位信息
        const processedOrderDetail = {
          orderId: orderData.orderId,
          status: getStatusText(orderData.status),
          statusCode: orderData.status,
          trainNumber: ticketInfo?.trainInfo?.name || ticketInfo?.trainCode || '-',
          seatType: seatType,
          departure: orderData.startStation || '出发站',
          arrival: orderData.endStation || '到达站',
          departureTime: orderData.startTime ? formatDateTime(orderData.startTime) : '',
          arrivalTime: orderData.endTime ? formatDateTime(orderData.endTime) : '',
          seatNumber: seatNumber,
          price: orderData.amount || orderData.totalAmount || 0,
          createTime: orderData.createTime ? formatDateTime(orderData.createTime) : '',
          payTime: orderData.payTime ? formatDateTime(orderData.payTime) : '',
          passengers: orderData.passengers || [],
        }

        orderDetail.value = processedOrderDetail
        detailVisible.value = true
      } catch (error) {
        showToast('获取订单详情失败', 'error')
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
        minute: '2-digit',
      })
    }

    // 根据状态码获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        0: '待支付',
        1: '已支付',
        2: '已出票',
        3: '已取消',
        4: '已退票',
      }
      return statusMap[status] || '未知状态'
    }

    // 根据状态文本获取状态码
    const getStatusCode = (statusText) => {
      const statusMap = {
        待支付: 0,
        已支付: 1,
        已出票: 2,
        已取消: 3,
        已退票: 4,
      }
      return statusMap[statusText]
    }

    // 根据座位类型获取座位类型文本
    const getSeatTypeText = (seatType) => {
      const seatTypeMap = {
        1: '一等座',
        2: '二等座',
        3: '商务座',
        4: '硬座',
        5: '硬卧',
        6: '软卧',
      }
      return seatTypeMap[seatType] || '未知座位'
    }

    const handleCloseDetail = () => {
      detailVisible.value = false
      orderDetail.value = null
    }

    const getStatusClass = (status) => {
      const statusMap = {
        '待支付': 'bg-orange-50 text-orange-600',
        '已支付': 'bg-green-50 text-green-600',
        '已出票': 'bg-blue-50 text-blue-600',
        '已取消': 'bg-gray-100 text-gray-500',
        '已退票': 'bg-gray-100 text-gray-500',
      }
      return statusMap[status] || 'bg-gray-100 text-gray-500'
    }

    // 商城订单状态样式
    const getMallStatusClass = (status) => {
      const statusMap = {
        0: 'bg-blue-50 text-blue-600', // 待发货
        1: 'bg-indigo-50 text-indigo-600', // 已发货
        2: 'bg-green-50 text-green-600', // 已完成
        3: 'bg-gray-100 text-gray-500', // 已取消
      }
      return statusMap[status] || 'bg-gray-100 text-gray-500'
    }

    onMounted(() => {
      fetchOrderList()
    })

    // 跳转到支付页面
    const handlePay = () => {
      if (orderDetail.value && orderDetail.value.orderId) {
        router.push({
          path: '/ticket-payment',
          query: { orderId: orderDetail.value.orderId }
        })
      }
    }

    return {
      loading,
      orderList,
      total,
      currentPage,
      pageSize,
      filterForm,
      detailVisible,
      orderDetail,
      orderType,
      tabs,
      toast,
      fetchOrderList,
      handleTypeChange,
      handleFilter,
      handleReset,
      handleSizeChange,
      handleCurrentChange,
      handleViewDetail,
      handleCloseDetail,
      getStatusClass,
      getMallStatusClass,
      formatDateTime,
      handlePay,
    }
  },
}
</script>

<style scoped>
/* 移除 Element Plus 相关样式，使用 Tailwind */
</style>
