<template>
  <div class="bg-white rounded-xl p-6 mb-5 shadow-sm border border-gray-100">
    <h3 class="flex items-center gap-2 text-lg font-medium text-gray-900 mb-6">
      <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
      订单信息
    </h3>
    
    <div class="flex flex-col gap-6">
      <!-- Train Info -->
      <div class="bg-blue-50/50 rounded-lg p-5 border border-blue-100">
        <div class="flex items-center gap-3 mb-4">
          <span class="text-xl font-bold text-blue-600">{{ orderInfo.trainCode }}</span>
          <span class="text-sm text-gray-500">{{ formatDate(orderInfo.date) }}</span>
        </div>
        <div class="flex items-center justify-between relative">
          <div class="flex flex-col">
            <div class="flex items-baseline gap-2 mb-1">
              <span class="text-2xl font-bold text-gray-900">{{ formatTime(orderInfo.startTime) }}</span>
              <span class="text-xs text-gray-400">{{ formatDate(orderInfo.startTime) }}</span>
            </div>
            <span class="text-base text-gray-700 font-medium">{{ orderInfo.originStation?.name || '出发站' }}</span>
          </div>
          
          <div class="flex flex-col items-center px-4 flex-1">
            <svg class="w-16 h-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"></path></svg>
          </div>
          
          <div class="flex flex-col items-end">
            <div class="flex items-baseline gap-2 mb-1">
              <span class="text-2xl font-bold text-gray-900">{{ formatTime(orderInfo.endTime) }}</span>
              <span class="text-xs text-gray-400">{{ formatDate(orderInfo.endTime) }}</span>
            </div>
            <span class="text-base text-gray-700 font-medium">{{ orderInfo.destinationStation?.name || '到达站' }}</span>
          </div>
        </div>
      </div>

      <!-- Passenger Info -->
      <div>
        <h4 class="text-sm font-semibold text-gray-900 mb-3 pl-2 border-l-4 border-blue-500">乘客信息</h4>
        <div class="grid gap-3">
          <div 
            v-for="(passenger, index) in orderInfo.passengers" 
            :key="index"
            class="flex justify-between items-center p-4 bg-gray-50 rounded-lg border border-gray-100"
          >
            <div class="flex flex-col gap-1">
              <div class="flex items-center gap-2">
                <span class="font-medium text-gray-900">{{ passenger.name }}</span>
                <span class="text-xs font-medium text-blue-600 bg-blue-50 border border-blue-100 rounded px-1.5 py-0.5">
                  {{ getPassengerTypeText(passenger.passengerType) }}
                </span>
              </div>
              <span class="text-sm text-gray-500">{{ orderInfo.seatType?.name || orderInfo.seatTypeName || '二等座' }} {{ passenger.seatPosition }}座</span>
            </div>
            <div class="text-right">
              <div class="flex flex-col items-end gap-0.5">
                <span v-if="passenger.discount > 0" class="text-xs text-gray-400 line-through">
                  原价：￥{{ passenger.basePrice?.toFixed(2) || '0.00' }}
                </span>
                <span v-if="passenger.discount > 0" class="text-xs text-red-500">
                  优惠：-￥{{ passenger.discount?.toFixed(2) || '0.00' }}
                </span>
                <span class="text-lg font-bold text-orange-500">
                  ￥{{ passenger.actualPrice?.toFixed(2) || orderInfo.ticketPrice?.toFixed(2) || '0.00' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Order Summary -->
      <div class="border-t border-gray-100 pt-4 mt-2">
        <div class="grid gap-2 text-sm">
          <div class="flex justify-between text-gray-500">
            <span>订单号</span>
            <span class="text-gray-900 font-mono">{{ orderInfo.orderNumber }}</span>
          </div>
          <div class="flex justify-between text-gray-500">
            <span>订单状态</span>
            <span class="text-gray-900">{{ getOrderStatusText(orderInfo.status) }}</span>
          </div>
          <div class="flex justify-between text-gray-500">
            <span>座位类型</span>
            <span class="text-gray-900">{{ orderInfo.seatTypeName }}</span>
          </div>
          <div class="flex justify-between text-gray-500">
            <span>乘客数量</span>
            <span class="text-gray-900">{{ orderInfo.passengers.length }}人</span>
          </div>
          <div class="flex justify-between text-gray-500">
            <span>创建时间</span>
            <span class="text-gray-900">{{ formatDateTime(orderInfo.createTime) }}</span>
          </div>
           <div class="flex justify-between text-gray-500" v-if="orderInfo.baseAmount && orderInfo.baseAmount !== orderInfo.totalAmount">
            <span>原价金额</span>
            <span class="text-gray-900">￥{{ orderInfo.baseAmount?.toFixed(2) }}</span>
          </div>
           <div class="flex justify-between text-gray-500" v-if="orderInfo.discountAmount > 0">
            <span>优惠金额</span>
            <span class="text-red-500">-￥{{ orderInfo.discountAmount?.toFixed(2) }}</span>
          </div>
          
          <div class="flex justify-between items-center pt-2 mt-2 border-t border-dashed border-gray-200">
            <span class="text-base font-semibold text-gray-900">应付金额</span>
            <span class="text-2xl font-bold text-orange-500">￥{{ orderInfo.totalAmount?.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  orderInfo: {
    type: Object,
    required: true
  }
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

// 格式化时间显示
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款'
  }
  return statusMap[status] || '未知状态'
}

// 获取乘客类型文本
const getPassengerTypeText = (type) => {
  const typeMap = {
    1: '成人',
    2: '儿童',
    3: '学生',
    4: '老人'
  }
  return typeMap[type] || '成人'
}
</script>
