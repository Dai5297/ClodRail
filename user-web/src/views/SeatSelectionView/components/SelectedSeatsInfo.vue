<template>
  <div class="selected-seats-info">
    <div class="info-header">
      <span class="title">已选座位</span>
      <span class="total-price">总计：¥{{ totalPrice }}</span>
    </div>
    
    <div class="seats-list">
      <div 
        v-for="seat in selectedSeats" 
        :key="seat"
        class="seat-item"
      >
        <div class="seat-info">
          <span class="carriage">{{ currentCarriage }}车厢</span>
          <span class="seat-number">{{ seat }}</span>
        </div>
        <el-button 
          type="text" 
          size="small" 
          class="remove-btn"
          @click="removeSeat(seat)"
        >
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Close } from '@element-plus/icons-vue'

defineProps<{
  selectedSeats: string[]
  currentCarriage: string
  totalPrice: number
}>()

const emit = defineEmits<{
  'remove-seat': [seatNumber: string]
}>()

const removeSeat = (seatNumber: string) => {
  emit('remove-seat', seatNumber)
}
</script>

<style scoped>
.selected-seats-info {
  background: white;
  margin-bottom: 12px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f0f8ff;
  border-bottom: 1px solid #e8e8e8;
}

.title {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.total-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff4d4f;
}

.seats-list {
  padding: 12px 16px;
}

.seat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.seat-item:last-child {
  border-bottom: none;
}

.seat-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.carriage {
  font-size: 12px;
  color: #666;
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
}

.seat-number {
  font-size: 14px;
  font-weight: bold;
  color: #1890ff;
}

.remove-btn {
  color: #ff4d4f;
  padding: 4px;
}

.remove-btn:hover {
  background: #fff2f0;
}

@media (max-width: 768px) {
  .info-header {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
}
</style>