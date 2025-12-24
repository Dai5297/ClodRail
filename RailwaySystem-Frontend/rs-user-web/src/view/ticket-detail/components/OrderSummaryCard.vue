<template>
  <div class="bg-white rounded-xl p-6 mb-5 shadow-sm border border-gray-100">
    <h3 class="flex items-center gap-2 text-lg font-medium text-gray-900 mb-5">
      <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
      订单摘要
    </h3>

    <div class="flex flex-col gap-3">
      <div class="flex justify-between items-center py-2 text-sm">
        <span class="text-gray-500">车次信息：</span>
        <span class="text-gray-900 font-medium">{{ trainCode }} {{ departureDate }}</span>
      </div>
      <div class="flex justify-between items-center py-2 text-sm">
        <span class="text-gray-500">出发到达：</span>
        <span class="text-gray-900 font-medium">
          {{ originStation }} → {{ destinationStation }}
        </span>
      </div>
      <div class="flex justify-between items-center py-2 text-sm">
        <span class="text-gray-500">座位类型：</span>
        <span class="text-gray-900 font-medium">{{ seatTypeName }}</span>
      </div>
      <div class="flex justify-between items-center py-2 text-sm">
        <span class="text-gray-500">乘客数量：</span>
        <span class="text-gray-900 font-medium">{{ passengerCount }}人</span>
      </div>
      <div class="flex justify-between items-center pt-4 mt-2 border-t border-gray-100">
        <span class="text-gray-900 font-semibold text-lg">总金额：</span>
        <span class="text-orange-500 font-bold text-2xl">￥{{ totalAmount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  trainCode: {
    type: String,
    required: true
  },
  departureDate: {
    type: String,
    required: true
  },
  originStation: {
    type: String,
    required: true
  },
  destinationStation: {
    type: String,
    required: true
  },
  seatTypeName: {
    type: String,
    default: '未选择'
  },
  passengerCount: {
    type: Number,
    default: 0
  },
  totalAmount: {
    type: Number,
    default: 0
  }
})
</script>

