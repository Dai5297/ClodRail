<template>
  <div class="bg-white rounded-xl p-6 mb-5 shadow-sm border border-gray-100">
    <h3 class="flex items-center gap-2 text-lg font-medium text-gray-900 mb-5">
      <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>
      选择座位类型
    </h3>

    <div class="flex flex-col gap-4">
      <div
          v-for="seatType in seatTypes"
          :key="seatType.type"
          class="border-2 rounded-lg p-5 cursor-pointer transition-all duration-300 w-full"
          :class="{
            'border-blue-500 bg-blue-50/50': selectedSeatType?.type === seatType.type,
            'border-gray-100 hover:border-blue-300': selectedSeatType?.type !== seatType.type,
            'bg-gray-50 opacity-60 cursor-not-allowed border-gray-100': seatType.remainingSeats === 0
          }"
          @click="handleSelectSeatType(seatType)"
      >
        <div class="flex justify-between items-center mb-3">
          <div class="text-base font-semibold text-gray-900">{{ seatType.name }}</div>
          <div class="text-xl font-semibold text-orange-500">￥{{ seatType.price }}</div>
        </div>
        <div class="flex justify-between items-center">
          <div class="text-sm" :class="seatType.remainingSeats > 0 ? 'text-green-500' : 'text-gray-400'">
            {{ seatType.remainingSeats > 0 ? `余票${seatType.remainingSeats}张` : '无票' }}
          </div>
          <div class="text-xs text-gray-400">{{ seatType.features }}</div>
        </div>

        <!-- 座位位置选择下拉菜单 -->
        <div
            v-if="selectedSeatType?.type === seatType.type && seatPositions[seatType.type] && seatPositions[seatType.type].length > 0"
            class="mt-4 pt-4 border-t border-blue-100"
            @click.stop
        >
          <div class="text-sm text-gray-700 mb-3 font-medium">选择座位位置：</div>
          <div class="grid grid-cols-[repeat(auto-fit,minmax(120px,1fr))] gap-2">
            <div
                v-for="position in seatPositions[seatType.type]"
                :key="position.code"
                :class="{
                  'border-blue-500 bg-blue-50': getPositionCount(position.code) > 0,
                  'border-gray-200 hover:border-blue-300 hover:bg-blue-50/30': getPositionCount(position.code) === 0,
                  'bg-gray-50 opacity-60 cursor-not-allowed': position.remainingSeats === 0
                }"
                class="relative border rounded-md p-3 cursor-pointer transition-all duration-300 bg-white text-center"
                @click="handleMainClick(position.code)"
            >
              <div v-if="getPositionCount(position.code) > 0" 
                   class="absolute -top-3 -right-3 flex items-center bg-white rounded-full shadow-md border border-gray-200 overflow-hidden z-20"
                   @click.stop
              >
                <button 
                  class="w-7 h-7 flex items-center justify-center hover:bg-gray-100 text-gray-600 font-bold transition-colors"
                  @click="handleUpdateCount(position.code, -1)"
                >−</button>
                <span class="w-6 text-center text-sm font-bold text-blue-600 select-none bg-blue-50 h-7 flex items-center justify-center">{{ getPositionCount(position.code) }}</span>
                <button 
                  class="w-7 h-7 flex items-center justify-center hover:bg-gray-100 text-blue-600 font-bold transition-colors"
                  @click="handleUpdateCount(position.code, 1)"
                >+</button>
              </div>
              <div class="flex items-center justify-center gap-2 mb-2">
                <span class="text-lg font-bold text-blue-500 bg-blue-50 border border-blue-100 rounded px-2 min-w-[24px] text-center">{{ position.code }}</span>
                <span class="text-sm text-gray-500 font-medium">{{ position.name }}</span>
              </div>
              <div class="text-xs font-medium" :class="position.remainingSeats > 0 ? 'text-green-500' : 'text-gray-400'">余{{ position.remainingSeats }}张</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  seatTypes: {
    type: Array,
    required: true
  },
  selectedSeatType: {
    type: Object,
    default: null
  },
  seatPositions: {
    type: Object,
    default: () => ({})
  },
  selectedPositions: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['select-seat-type', 'update-position-count'])

const handleSelectSeatType = (seatType) => {
  emit('select-seat-type', seatType)
}

const handleUpdateCount = (positionCode, delta) => {
  emit('update-position-count', positionCode, delta)
}

const handleMainClick = (positionCode) => {
  // 只有当数量为0时，点击卡片主体才增加
  if (getPositionCount(positionCode) === 0) {
    handleUpdateCount(positionCode, 1)
  }
}

const getPositionCount = (positionCode) => {
  return props.selectedPositions.filter(p => p === positionCode).length
}
</script>

