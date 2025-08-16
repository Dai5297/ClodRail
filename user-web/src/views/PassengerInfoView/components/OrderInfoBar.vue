<template>
  <div class="order-info-bar">
    <div class="container">
      <div class="order-summary">
        <div class="train-info">
          <span class="train-number">{{ orderInfo.trainNumber }}</span>
          <span class="route">{{ orderInfo.departure }} → {{ orderInfo.destination }}</span>
          <span class="date">{{ orderInfo.date }}</span>
        </div>
        <div class="seat-info">
          <span class="seat-type">{{ orderInfo.seatType }}</span>
          <span class="seats">{{ orderInfo.seats }}</span>
          <span class="total-price">总价：¥{{ orderInfo.totalPrice }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 定义props
const props = defineProps<{
  orderInfo: {
    trainNumber: string
    departure: string
    destination: string
    date: string
    seatType: string
    seats: string
    totalPrice: number
  }
}>()
</script>

<style scoped>
/* 订单信息栏 */
.order-info-bar {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  padding: 16px 0;
}

.order-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.train-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.train-number {
  font-size: 18px;
  font-weight: bold;
  color: #1890FF;
}

.route {
  font-size: 16px;
  font-weight: 500;
}

.date {
  color: #8c8c8c;
}

.seat-info {
  display: flex;
  align-items: center;
  gap: 16px;
  text-align: right;
}

.seat-type {
  font-weight: 500;
}

.seats {
  color: #8c8c8c;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-summary {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .train-info,
  .seat-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .container {
    padding: 0 16px;
  }
}
</style>