<template>
  <dl class="order-info" aria-label="支付订单摘要">
    <div class="info-item">
      <dt>订单号</dt>
      <dd class="data-value">{{ orderInfo.out_trade_no || orderInfo.orderId }}</dd>
    </div>
    <div v-if="orderInfo.trade_no && orderInfo.trade_no !== '---'" class="info-item">
      <dt>支付宝交易号</dt>
      <dd class="data-value">{{ orderInfo.trade_no }}</dd>
    </div>
    <div v-if="orderInfo.total_amount" class="info-item">
      <dt>支付金额</dt>
      <dd class="amount">¥{{ formatAmount(orderInfo.total_amount) }}</dd>
    </div>
    <div v-if="orderInfo.timestamp" class="info-item">
      <dt>支付时间</dt>
      <dd class="data-value">{{ formatTime(orderInfo.timestamp) }}</dd>
    </div>
  </dl>
</template>

<script setup>
defineProps({
  orderInfo: {
    type: Object,
    required: true
  }
})

const formatAmount = (amount) => Number(amount || 0).toFixed(2)

const formatTime = (timestamp) => {
  if (!timestamp) return '--'
  const date = new Date(timestamp)
  if (Number.isNaN(date.getTime())) return '--'
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}
</script>

<style scoped>
.order-info {
  margin: 0;
  padding: 8px 20px;
  border: 1px solid var(--rail-line);
  border-radius: 10px;
  background: var(--rail-background);
  text-align: left;
}
.info-item { display: flex; align-items: center; justify-content: space-between; gap: 20px; padding: 13px 0; border-bottom: 1px dashed color-mix(in srgb, var(--rail-line) 82%, var(--rail-navy)); }
.info-item:last-child { border-bottom: 0; }
dt { color: var(--rail-muted); font-size: 13px; }
dd { margin: 0; color: var(--rail-ink); font-size: 14px; font-weight: 600; text-align: right; word-break: break-all; }
.data-value { font-family: "Arial Narrow", "DIN Alternate", Arial, sans-serif; font-variant-numeric: tabular-nums; }
.amount { color: var(--rail-action); font-family: "Arial Narrow", "DIN Alternate", Arial, sans-serif; font-size: 20px; font-variant-numeric: tabular-nums; }
@media (max-width: 480px) { .info-item { align-items: flex-start; flex-direction: column; gap: 4px; } dd { text-align: left; } }
</style>
