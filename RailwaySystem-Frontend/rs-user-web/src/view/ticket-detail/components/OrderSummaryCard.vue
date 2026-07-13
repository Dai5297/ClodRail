<template>
  <section class="overflow-hidden rounded-xl border border-line bg-white shadow-soft" aria-labelledby="order-summary-title">
    <div class="border-b border-primary/25 bg-navy px-5 py-5 text-white">
      <span class="inline-flex rounded bg-white/10 px-2.5 py-1 text-xs font-bold text-primary-soft">第 4 站 · 订单确认</span>
      <h2 id="order-summary-title" class="mt-2 text-xl font-bold">核对订单</h2>
      <p class="mt-1 text-xs text-primary-soft/75">提交前请确认车次、席别和人数</p>
    </div>

    <dl class="divide-y divide-line/70 px-5">
      <div class="flex items-start justify-between gap-4 py-4 text-sm">
        <dt class="shrink-0 text-secondary">车次日期</dt>
        <dd class="data-number text-right font-semibold text-ink">{{ trainCode }}<br /><span class="text-xs font-normal text-secondary">{{ departureDate }}</span></dd>
      </div>
      <div class="flex items-start justify-between gap-4 py-4 text-sm">
        <dt class="shrink-0 text-secondary">出发到达</dt>
        <dd class="text-right font-semibold text-ink">{{ originStation }} <span class="mx-1 text-primary">→</span> {{ destinationStation }}</dd>
      </div>
      <div class="flex items-center justify-between gap-4 py-4 text-sm">
        <dt class="text-secondary">席别</dt>
        <dd class="font-semibold" :class="seatTypeName === '未选择' ? 'text-secondary/60' : 'text-ink'">{{ seatTypeName }}</dd>
      </div>
      <div class="flex items-center justify-between gap-4 py-4 text-sm">
        <dt class="text-secondary">乘车人</dt>
        <dd class="data-number font-semibold" :class="passengerCount ? 'text-ink' : 'text-secondary/60'">{{ passengerCount }} 人</dd>
      </div>
    </dl>

    <div class="border-t border-line bg-action-soft px-5 py-5">
      <div class="flex items-end justify-between gap-4">
        <div>
          <p class="text-sm font-semibold text-secondary">订单总额</p>
          <p class="mt-1 text-xs text-secondary/80">优惠票按实际规则计价</p>
        </div>
        <p class="data-number text-3xl font-bold leading-none text-action"><span class="mr-0.5 text-base">￥</span>{{ Number(totalAmount).toFixed(2) }}</p>
      </div>
    </div>
  </section>
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

<style scoped>
.data-number {
  font-family: "DIN Alternate", "Arial Narrow", Arial, sans-serif;
  font-variant-numeric: tabular-nums;
}
</style>
