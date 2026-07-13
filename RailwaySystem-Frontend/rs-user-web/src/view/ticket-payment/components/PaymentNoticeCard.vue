<template>
  <aside class="rounded-xl border border-blue-100 bg-blue-50 p-5" aria-labelledby="payment-notice-title">
    <h4 id="payment-notice-title" class="mb-3 flex items-center gap-2 text-sm font-semibold text-navy">
      <svg class="h-4 w-4 text-primary" fill="currentColor" viewBox="0 0 20 20" aria-hidden="true">
        <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
      </svg>
      支付安全提示
    </h4>
    <ul class="space-y-2 pl-5 text-xs leading-5 text-slate-600 marker:text-primary">
      <li>请在 {{ minutes }} 分钟内完成支付，超时订单将自动取消</li>
      <li>支付成功后，可在“我的订单”中查看车票信息</li>
      <li>请确认收款方与支付金额，切勿向他人泄露验证码</li>
      <li>如遇支付问题，请联系客服：400-123-4567</li>
    </ul>
  </aside>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  remainingTime: {
    type: Number,
    required: true
  }
})

const minutes = computed(() => Math.max(0, Math.ceil(props.remainingTime / 60)))
</script>
