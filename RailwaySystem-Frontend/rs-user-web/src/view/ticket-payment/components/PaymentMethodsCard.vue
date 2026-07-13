<template>
  <section class="rounded-xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6" aria-labelledby="payment-method-title">
    <div class="mb-5 flex items-start justify-between gap-4">
      <div>
        <p class="mb-1 text-xs font-semibold uppercase tracking-wider text-primary">支付方式</p>
        <h3 id="payment-method-title" class="text-lg font-bold text-ink">选择支付渠道</h3>
      </div>
      <svg class="h-6 w-6 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
      </svg>
    </div>

    <div class="mb-6 grid gap-3" role="radiogroup" aria-label="支付渠道">
      <button
        v-for="method in paymentMethods"
        :key="method.id"
        type="button"
        role="radio"
        :aria-checked="selectedMethod?.id === method.id"
        :aria-disabled="!method.available"
        :disabled="!method.available"
        class="flex w-full items-center justify-between rounded-lg border-2 p-4 text-left focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60"
        :class="selectedMethod?.id === method.id
          ? 'border-primary bg-primary-soft'
          : 'border-slate-200 bg-white hover:border-blue-300 hover:bg-slate-50'"
        @click="handleSelectMethod(method)"
      >
        <span class="flex min-w-0 items-center gap-3">
          <span
            class="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg text-lg font-bold text-white"
            :class="method.id === 'alipay' ? 'bg-[#1677FF]' : method.id === 'wechat' ? 'bg-[#07C160]' : 'bg-slate-500'"
            aria-hidden="true"
          >
            {{ method.id === 'alipay' ? '支' : method.id === 'wechat' ? '微' : '付' }}
          </span>
          <span class="min-w-0">
            <span class="block font-semibold text-ink">{{ method.name }}</span>
            <span class="mt-0.5 block text-xs text-slate-500">{{ method.description }}</span>
          </span>
        </span>
        <span
          class="ml-3 flex h-5 w-5 shrink-0 items-center justify-center rounded-full border-2"
          :class="selectedMethod?.id === method.id ? 'border-primary bg-primary' : 'border-slate-300 bg-white'"
          aria-hidden="true"
        >
          <span v-if="selectedMethod?.id === method.id" class="h-2 w-2 rounded-full bg-white" />
        </span>
      </button>
    </div>

    <div class="border-t border-dashed border-slate-200 pt-5">
      <div class="mb-4 flex items-end justify-between gap-4">
        <span class="text-sm text-slate-600">订单应付</span>
        <span class="font-mono text-3xl font-bold tabular-nums text-action">¥{{ Number(totalAmount || 0).toFixed(2) }}</span>
      </div>
      <button
        type="button"
        :disabled="!selectedMethod || processing"
        class="flex h-12 w-full items-center justify-center gap-2 rounded-lg bg-action px-5 text-base font-bold text-white shadow-sm hover:bg-action-hover focus:outline-none focus:ring-2 focus:ring-action focus:ring-offset-2 disabled:cursor-not-allowed disabled:bg-slate-300"
        @click="handlePay"
      >
        <svg v-if="processing" class="h-5 w-5 animate-spin" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" aria-hidden="true">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
        </svg>
        <span>{{ processing ? '正在前往支付平台…' : '确认支付' }}</span>
      </button>
      <p class="mt-3 flex items-center justify-center gap-1.5 text-xs text-slate-500">
        <svg class="h-4 w-4 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
        </svg>
        支付信息已加密保护
      </p>
    </div>
  </section>
</template>

<script setup>
defineProps({
  paymentMethods: {
    type: Array,
    required: true
  },
  selectedMethod: {
    type: Object,
    default: null
  },
  totalAmount: {
    type: Number,
    required: true
  },
  processing: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['select-method', 'pay'])

const handleSelectMethod = (method) => {
  if (method.available) emit('select-method', method)
}

const handlePay = () => emit('pay')
</script>
