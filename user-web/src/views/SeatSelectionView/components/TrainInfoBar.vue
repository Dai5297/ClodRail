<template>
  <div class="train-info-bar">
    <div class="train-info">
      <div class="train-number">{{ trainInfo.trainNumber }}</div>
      <div class="route-info">
        <span class="station">{{ trainInfo.departure }}</span>
        <el-icon class="arrow-icon"><ArrowRight /></el-icon>
        <span class="station">{{ trainInfo.destination }}</span>
      </div>
      <div class="date-info">{{ trainInfo.date }}</div>
    </div>
    <div class="passenger-count">
      <span class="label">乘客数量：</span>
      <span class="count">{{ trainInfo.passengers }}人</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowRight } from '@element-plus/icons-vue'

interface TrainInfo {
  trainId: string
  trainNumber: string
  departure: string
  destination: string
  date: string
  passengers: number
}

defineProps<{
  trainInfo: TrainInfo
}>()
</script>

<style scoped>
.train-info-bar {
  background: white;
  padding: 16px;
  margin-bottom: 12px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.train-info {
  flex: 1;
}

.train-number {
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
  margin-bottom: 8px;
}

.route-info {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.station {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.arrow-icon {
  margin: 0 8px;
  color: #666;
}

.date-info {
  font-size: 14px;
  color: #666;
}

.passenger-count {
  text-align: right;
}

.label {
  font-size: 14px;
  color: #666;
}

.count {
  font-size: 16px;
  font-weight: bold;
  color: #1890ff;
}

@media (max-width: 768px) {
  .train-info-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .passenger-count {
    text-align: left;
    width: 100%;
  }
}
</style>