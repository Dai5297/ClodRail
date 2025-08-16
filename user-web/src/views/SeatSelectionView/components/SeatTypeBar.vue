<template>
  <div class="seat-type-bar">
    <div class="seat-type-title">选择座位类型</div>
    <div class="seat-type-options">
      <div 
        v-for="seatType in availableSeatTypes" 
        :key="seatType.type"
        class="seat-type-option"
        :class="{ active: selectedSeatType === seatType.type }"
        @click="selectSeatType(seatType.type)"
      >
        <div class="type-name">{{ seatType.type }}</div>
        <div class="type-price">¥{{ seatType.price }}</div>
        <div class="type-available">余{{ seatType.available }}张</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface SeatInfo {
  type: string
  price: number
  available: number
}

defineProps<{
  availableSeatTypes: SeatInfo[]
  selectedSeatType: string
}>()

const emit = defineEmits<{
  'select-seat-type': [type: string]
}>()

const selectSeatType = (type: string) => {
  emit('select-seat-type', type)
}
</script>

<style scoped>
.seat-type-bar {
  background: white;
  padding: 16px;
  margin-bottom: 12px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.seat-type-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.seat-type-options {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.seat-type-option {
  flex: 1;
  min-width: 120px;
  padding: 12px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.seat-type-option:hover {
  border-color: #1890ff;
  background: #f0f8ff;
}

.seat-type-option.active {
  border-color: #1890ff;
  background: #e6f7ff;
}

.type-name {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.type-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff4d4f;
  margin-bottom: 4px;
}

.type-available {
  font-size: 12px;
  color: #666;
}

.seat-type-option.active .type-name {
  color: #1890ff;
}

@media (max-width: 768px) {
  .seat-type-options {
    flex-direction: column;
  }
  
  .seat-type-option {
    min-width: auto;
  }
}
</style>