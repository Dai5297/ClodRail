<template>
  <div class="bg-white rounded-xl p-6 mb-5 shadow-sm border border-gray-100">
    <div class="flex justify-between items-center mb-5">
      <h3 class="flex items-center gap-2 text-lg font-medium text-gray-900">
        <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
        乘客信息
      </h3>
      <button
          @click="handleAddPassenger"
          :disabled="!selectedSeatType"
          class="inline-flex items-center px-3 py-1.5 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
      >
        <svg class="w-4 h-4 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
        添加乘客
      </button>
    </div>

    <div v-if="selectedPassengers.length === 0" class="py-12 flex flex-col items-center justify-center text-center">
      <div class="bg-gray-50 rounded-full p-4 mb-3">
        <svg class="w-12 h-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path></svg>
      </div>
      <p class="text-gray-500 text-sm">请先选择座位类型，然后添加乘客信息</p>
    </div>

    <div v-else class="flex flex-col gap-3">
      <div
          v-for="(passenger, index) in selectedPassengers"
          :key="index"
          class="flex justify-between items-center p-4 bg-gray-50 rounded-lg border border-gray-100 hover:border-blue-200 transition-colors duration-200"
      >
        <div>
          <div class="text-base font-medium text-gray-900 mb-1">{{ passenger.name }}</div>
          <div class="flex items-center gap-3 text-sm text-gray-500">
            <span class="bg-gray-200 px-2 py-0.5 rounded text-xs text-gray-600">{{ passenger.passengerType === 1 ? '身份证' : '护照' }}</span>
            <span class="font-mono">{{ passenger.idCard }}</span>
          </div>
        </div>
        <button
            @click="handleRemovePassenger(index)"
            class="text-gray-400 hover:text-red-500 p-1 rounded-full hover:bg-red-50 transition-colors duration-200"
            title="移除"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  selectedPassengers: {
    type: Array,
    required: true
  },
  selectedSeatType: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['add-passenger', 'remove-passenger'])

const handleAddPassenger = () => {
  emit('add-passenger')
}

const handleRemovePassenger = (index) => {
  emit('remove-passenger', index)
}
</script>

