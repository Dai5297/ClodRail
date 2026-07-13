<template>
  <section
    class="rounded-xl border p-5"
    :class="isUrgent ? 'border-red-200 bg-red-50' : 'border-orange-200 bg-orange-50'"
    aria-live="polite"
    aria-label="支付剩余时间"
  >
    <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <div class="flex items-center gap-3">
        <div
          class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full"
          :class="isUrgent ? 'bg-red-100 text-red-600' : 'bg-orange-100 text-orange-600'"
        >
          <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <div>
          <div class="mb-1 text-sm font-semibold text-gray-900">请在倒计时结束前完成支付</div>
          <div class="text-xs text-gray-600">超时后订单将自动取消，席位会重新释放</div>
        </div>
      </div>
      <div
        class="font-mono text-3xl font-bold tabular-nums sm:text-4xl"
        :class="isUrgent ? 'text-red-600' : 'text-orange-600'"
      >
        {{ formattedTime }}
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  remainingTime: {
    type: Number,
    required: true
  }
})

const formattedTime = computed(() => {
  const minutes = Math.floor(props.remainingTime / 60)
  const seconds = props.remainingTime % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})

const isUrgent = computed(() => props.remainingTime <= 5 * 60)
</script>
