<template>
  <div class="bg-white rounded-xl p-6 mb-5 shadow-sm border border-gray-100">
    <div class="flex items-center gap-4">
      <div class="text-[#ff6b35]">
        <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
      </div>
      <div>
        <div class="text-sm text-gray-500 mb-2">请在以下时间内完成支付</div>
        <div class="text-2xl font-bold text-[#ff6b35] mb-1 leading-none">
          <span class="font-mono">{{ formattedTime }}</span>
        </div>
        <div class="text-xs text-gray-400">超时订单将自动取消</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  remainingTime: {
    type: Number,
    required: true
  }
})

// 格式化倒计时
const formattedTime = computed(() => {
  const minutes = Math.floor(props.remainingTime / 60)
  const seconds = props.remainingTime % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})
</script>
