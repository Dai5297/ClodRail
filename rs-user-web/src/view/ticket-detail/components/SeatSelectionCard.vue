<template>
  <div class="seat-selection-card">
    <h3 class="card-title">
      <el-icon>
        <Grid/>
      </el-icon>
      选择座位类型
    </h3>

    <div class="seat-types">
      <div
          v-for="seatType in seatTypes"
          :key="seatType.type"
          class="seat-type-item"
          :class="{
            'selected': selectedSeatType?.type === seatType.type,
            'sold-out': seatType.remainingSeats === 0 
          }"
          @click="handleSelectSeatType(seatType)"
      >
        <div class="seat-type-header">
          <div class="seat-name">{{ seatType.name }}</div>
          <div class="seat-price">￥{{ seatType.price }}</div>
        </div>
        <div class="seat-info">
          <div class="remaining">
            {{ seatType.remainingSeats > 0 ? `余票${seatType.remainingSeats}张` : '无票' }}
          </div>
          <div class="features">{{ seatType.features }}</div>
        </div>

        <!-- 座位位置选择下拉菜单 -->
        <div
            v-if="selectedSeatType?.type === seatType.type && seatPositions[seatType.type] && seatPositions[seatType.type].length > 0"
            class="seat-position-dropdown"
            @click.stop
        >
          <div class="position-title">选择座位位置：</div>
          <div class="position-options">
            <div
                v-for="position in seatPositions[seatType.type]"
                :key="position.code"
                class="position-option"
                :class="{
                  'selected': selectedPositions.includes(position.code),
                  'disabled': position.remainingSeats === 0 
                }"
                @click="handleTogglePosition(position.code)"
            >
              <div class="position-header">
                <span class="position-code">{{ position.code }}</span>
                <span class="position-name">{{ position.name }}</span>
              </div>
              <div class="position-remaining">余{{ position.remainingSeats }}张</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Grid } from '@element-plus/icons-vue'

const props = defineProps({
  seatTypes: {
    type: Array,
    required: true
  },
  selectedSeatType: {
    type: Object,
    default: null
  },
  seatPositions: {
    type: Object,
    default: () => ({})
  },
  selectedPositions: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['select-seat-type', 'toggle-position'])

const handleSelectSeatType = (seatType) => {
  emit('select-seat-type', seatType)
}

const handleTogglePosition = (positionCode) => {
  emit('toggle-position', positionCode)
}
</script>

<style scoped>
.seat-selection-card {
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

.seat-types {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.seat-type-item {
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
}

.seat-type-item:hover {
  border-color: #1890ff;
}

.seat-type-item.selected {
  border-color: #1890ff;
  background-color: #f0f9ff;
}

.seat-type-item.sold-out {
  background-color: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.6;
}

.seat-type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.seat-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.seat-price {
  font-size: 20px;
  font-weight: 600;
  color: #ff6b35;
}

.seat-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.remaining {
  font-size: 14px;
  color: #52c41a;
}

.features {
  font-size: 12px;
  color: #999;
}

.seat-position-dropdown {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.position-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 12px;
  font-weight: 500;
}

.position-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 8px;
}

.position-option {
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
  text-align: center;
}

.position-option:hover {
  border-color: #1890ff;
  background-color: #f0f9ff;
}

.position-option.selected {
  border-color: #1890ff;
  background-color: #e6f7ff;
}

.position-option.disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.6;
}

.position-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 8px;
}

.position-code {
  font-size: 18px;
  font-weight: 700;
  color: #1890ff;
  background-color: #f0f9ff;
  border: 1px solid #d4edda;
  border-radius: 4px;
  padding: 2px 8px;
  min-width: 24px;
  text-align: center;
}

.position-name {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.position-remaining {
  font-size: 12px;
  color: #52c41a;
  font-weight: 500;
}

.position-option.disabled .position-remaining {
  color: #999;
}
</style>

