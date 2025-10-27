<template>
  <div class="passenger-info-card">
    <div class="card-header">
      <h3 class="card-title">
        <el-icon>
          <User/>
        </el-icon>
        乘客信息
      </h3>
      <el-button
          type="primary"
          size="small"
          @click="handleAddPassenger"
          :disabled="!selectedSeatType"
      >
        <el-icon>
          <Plus/>
        </el-icon>
        添加乘客
      </el-button>
    </div>

    <div v-if="selectedPassengers.length === 0" class="no-passengers">
      <el-empty description="请先选择座位类型，然后添加乘客信息"/>
    </div>

    <div v-else class="passenger-list">
      <div
          v-for="(passenger, index) in selectedPassengers"
          :key="index"
          class="passenger-item"
      >
        <div class="passenger-info">
          <div class="passenger-name">{{ passenger.name }}</div>
          <div class="passenger-details">
            <span class="id-type">{{ passenger.passengerType === 1 ? '身份证' : '护照' }}</span>
            <span class="id-number">{{ passenger.idCard }}</span>
          </div>
        </div>
        <div class="passenger-actions">
          <el-button
              type="text"
              size="small"
              @click="handleRemovePassenger(index)"
          >
            <el-icon>
              <Delete/>
            </el-icon>
            移除
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { User, Plus, Delete } from '@element-plus/icons-vue'

const props = defineProps({
  selectedPassengers: {
    type: Array,
    required: true
  },
  selectedSeatType: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['add-passenger', 'remove-passenger'])

const handleAddPassenger = () => {
  emit('add-passenger')
}

const handleRemovePassenger = (index) => {
  emit('remove-passenger', index)
}
</script>

<style scoped>
.passenger-info-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-title {
  font-size: 18px;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.no-passengers {
  padding: 20px 0;
}

.passenger-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.passenger-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.passenger-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.passenger-details {
  display: flex;
  gap: 12px;
  font-size: 14px;
  color: #666;
}
</style>

