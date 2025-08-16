<template>
  <div class="bottom-bar">
    <div class="price-info">
      <div class="selected-count">已选 {{ selectedSeats.length }}/{{ passengers }} 个座位</div>
      <div class="total-price">总计：¥{{ totalPrice }}</div>
    </div>
    
    <div class="actions">
      <el-button 
        size="large" 
        @click="goBack"
      >
        返回
      </el-button>
      <el-button 
        type="primary" 
        size="large"
        :disabled="selectedSeats.length !== passengers"
        @click="confirmSelection"
      >
        确认选座
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  selectedSeats: string[]
  totalPrice: number
  passengers: number
}>()

const emit = defineEmits<{
  'go-back': []
  'confirm-selection': []
}>()

const goBack = () => {
  emit('go-back')
}

const confirmSelection = () => {
  emit('confirm-selection')
}
</script>

<style scoped>
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 12px 16px;
  border-top: 1px solid #e8e8e8;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.price-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.selected-count {
  font-size: 14px;
  color: #666;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

.actions {
  display: flex;
  gap: 12px;
}

.actions .el-button {
  flex: 1;
  height: 44px;
}

@media (max-width: 768px) {
  .bottom-bar {
    padding: 16px;
  }
  
  .price-info {
    flex-direction: column;
    gap: 4px;
    text-align: center;
  }
  
  .actions {
    margin-top: 16px;
  }
  
  .actions .el-button {
    height: 48px;
  }
}
</style>