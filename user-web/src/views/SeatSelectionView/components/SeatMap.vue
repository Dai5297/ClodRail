<template>
  <div class="seat-map-container">
    <!-- 座位图例 -->
    <div class="seat-legend">
      <div class="legend-item">
        <div class="legend-seat available"></div>
        <span>可选</span>
      </div>
      <div class="legend-item">
        <div class="legend-seat occupied"></div>
        <span>已占</span>
      </div>
      <div class="legend-item">
        <div class="legend-seat selected"></div>
        <span>已选</span>
      </div>
    </div>

    <!-- 车厢信息 -->
    <div class="carriage-info">
      <span class="carriage-number">{{ currentCarriage }}车厢</span>
      <span class="direction-indicator">← 列车前进方向</span>
    </div>

    <!-- 座位图 -->
    <div class="seat-map" v-loading="loading">
      <div v-if="!loading && seatMap.length > 0" class="seat-grid">
        <div 
          v-for="row in seatMap" 
          :key="row.rowNumber"
          class="seat-row"
        >
          <div class="row-number">{{ row.rowNumber }}</div>
          <div class="seats">
            <template v-for="(seat, index) in row.seats" :key="seat.seatNumber">
              <div 
                class="seat"
                :class="{
                  available: seat.status === 'available',
                  occupied: seat.status === 'occupied',
                  selected: selectedSeats.includes(seat.seatNumber)
                }"
                @click="toggleSeat(seat)"
              >
                <span class="seat-label">{{ seat.seatNumber.slice(-1) }}</span>
              </div>
              <!-- 过道分隔 -->
              <div 
                v-if="needAisle(index)" 
                class="aisle"
              ></div>
            </template>
          </div>
        </div>
      </div>
      
      <div v-else-if="!loading" class="empty-state">
        <el-empty description="暂无座位信息" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface SeatMapRow {
  rowNumber: string
  seats: {
    seatNumber: string
    status: 'available' | 'occupied'
  }[]
}

defineProps<{
  seatMap: SeatMapRow[]
  loading: boolean
  currentCarriage: string
  selectedSeats: string[]
}>()

const emit = defineEmits<{
  'toggle-seat': [seat: { seatNumber: string; status: 'available' | 'occupied' }]
}>()

const toggleSeat = (seat: { seatNumber: string; status: 'available' | 'occupied' }) => {
  emit('toggle-seat', seat)
}

// 判断是否需要过道（在C和D之间）
const needAisle = (index: number) => {
  // 这里简化处理，实际应该根据座位类型和布局来判断
  return index === 1 // 在B和C之间，或者C和D之间
}
</script>

<style scoped>
.seat-map-container {
  background: white;
  margin-bottom: 12px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.seat-legend {
  display: flex;
  justify-content: center;
  gap: 24px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e8e8e8;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #666;
}

.legend-seat {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.legend-seat.available {
  background: #52c41a;
  border-color: #52c41a;
}

.legend-seat.occupied {
  background: #d9d9d9;
  border-color: #d9d9d9;
}

.legend-seat.selected {
  background: #1890ff;
  border-color: #1890ff;
}

.carriage-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f0f8ff;
  border-bottom: 1px solid #e8e8e8;
}

.carriage-number {
  font-size: 14px;
  font-weight: bold;
  color: #1890ff;
}

.direction-indicator {
  font-size: 12px;
  color: #666;
}

.seat-map {
  padding: 16px;
  min-height: 400px;
}

.seat-grid {
  max-width: 400px;
  margin: 0 auto;
}

.seat-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.row-number {
  width: 30px;
  text-align: center;
  font-size: 12px;
  color: #666;
  margin-right: 12px;
}

.seats {
  display: flex;
  gap: 4px;
  flex: 1;
  justify-content: center;
}

.seat {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #f5f5f5;
}

.seat.available {
  background: #52c41a;
  border-color: #52c41a;
  color: white;
}

.seat.available:hover {
  background: #73d13d;
  transform: scale(1.1);
}

.seat.occupied {
  background: #d9d9d9;
  border-color: #d9d9d9;
  color: #999;
  cursor: not-allowed;
}

.seat.selected {
  background: #1890ff;
  border-color: #1890ff;
  color: white;
  transform: scale(1.1);
}

.seat-label {
  font-size: 10px;
  font-weight: bold;
}

.aisle {
  width: 16px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

@media (max-width: 768px) {
  .seat-legend {
    gap: 16px;
  }
  
  .carriage-info {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
  
  .seat {
    width: 28px;
    height: 28px;
  }
  
  .seat-label {
    font-size: 9px;
  }
}
</style>