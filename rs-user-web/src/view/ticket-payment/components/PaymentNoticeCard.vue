<template>
  <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
    <h4 class="flex items-center gap-2 text-sm font-semibold text-gray-900 mb-3">
      <svg class="w-4 h-4 text-blue-500" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"></path></svg>
      支付说明
    </h4>
    <ul class="list-disc list-inside space-y-1 text-xs text-gray-500 pl-1">
      <li>请在{{ minutes }}分钟内完成支付，超时订单将自动取消</li>
      <li>支付成功后，车票信息将发送至您的手机和邮箱</li>
      <li>如遇支付问题，请联系客服：400-123-4567</li>
      <li>退票改签请遵循铁路部门相关规定</li>
    </ul>
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

const minutes = computed(() => Math.floor(props.remainingTime / 60))
</script>
