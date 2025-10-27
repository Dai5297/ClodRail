<template>
  <div class="order-summary-card">
    <h3 class="card-title">
      <el-icon>
        <Document/>
      </el-icon>
      订单摘要
    </h3>

    <div class="summary-content">
      <div class="summary-item">
        <span class="label">车次信息：</span>
        <span class="value">{{ trainCode }} {{ departureDate }}</span>
      </div>
      <div class="summary-item">
        <span class="label">出发到达：</span>
        <span class="value">
          {{ originStation }} → {{ destinationStation }}
        </span>
      </div>
      <div class="summary-item">
        <span class="label">座位类型：</span>
        <span class="value">{{ seatTypeName }}</span>
      </div>
      <div class="summary-item">
        <span class="label">乘客数量：</span>
        <span class="value">{{ passengerCount }}人</span>
      </div>
      <div class="summary-item total">
        <span class="label">总金额：</span>
        <span class="value">￥{{ totalAmount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Document } from '@element-plus/icons-vue'

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
.order-summary-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-title {
  font-size: 18px;
  color: #333;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.summary-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.summary-item.total {
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
  margin-top: 8px;
  font-size: 18px;
  font-weight: 600;
}

.label {
  color: #666;
}

.value {
  color: #333;
  font-weight: 500;
}

.summary-item.total .value {
  color: #ff6b35;
  font-size: 24px;
}
</style>

