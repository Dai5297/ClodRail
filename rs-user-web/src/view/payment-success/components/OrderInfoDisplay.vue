<template>
  <div class="order-info">
    <div class="info-item">
      <span class="label">订单号：</span>
      <span class="value">{{ orderInfo.out_trade_no || orderInfo.orderId }}</span>
    </div>
    <div class="info-item" v-if="orderInfo.trade_no">
      <span class="label">支付宝交易号：</span>
      <span class="value">{{ orderInfo.trade_no }}</span>
    </div>
    <div class="info-item" v-if="orderInfo.total_amount">
      <span class="label">支付金额：</span>
      <span class="value amount">￥{{ orderInfo.total_amount }}</span>
    </div>
    <div class="info-item" v-if="orderInfo.timestamp">
      <span class="label">支付时间：</span>
      <span class="value">{{ formatTime(orderInfo.timestamp) }}</span>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  orderInfo: {
    type: Object,
    required: true
  }
})

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}
</script>

<style scoped>
.order-info {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 32px;
  text-align: left;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e9ecef;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 14px;
  color: #666;
}

.value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  word-break: break-all;
}

.value.amount {
  color: #52c41a;
  font-size: 18px;
  font-weight: 600;
}
</style>



