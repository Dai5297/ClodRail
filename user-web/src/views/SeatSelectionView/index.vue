<template>
  <div class="seat-selection-page">
    <!-- 车次信息栏 -->
    <TrainInfoBar :train-info="trainInfo" />
    
    <!-- 座位类型选择 -->
    <SeatTypeBar 
      :available-seat-types="availableSeatTypes"
      :selected-seat-type="selectedSeatType"
      @select-seat-type="selectSeatType"
    />
    
    <!-- 座位图 -->
    <SeatMap 
      :seat-map="seatMap"
      :loading="loadingSeatMap"
      :current-carriage="currentCarriage"
      :selected-seats="selectedSeats"
      @toggle-seat="toggleSeat"
    />
    
    <!-- 已选座位信息 -->
    <SelectedSeatsInfo 
      v-if="selectedSeats.length > 0"
      :selected-seats="selectedSeats"
      :current-carriage="currentCarriage"
      :total-price="totalPrice"
      @remove-seat="removeSeat"
    />
    
    <!-- 底部操作栏 -->
    <BottomBar 
      :selected-seats="selectedSeats"
      :total-price="totalPrice"
      :passengers="trainInfo.passengers"
      @go-back="goBack"
      @confirm-selection="confirmSelection"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSeatMap } from '@/api/train'
import type { SeatInfo } from '@/api/train'

// 导入子组件
import TrainInfoBar from './components/TrainInfoBar.vue'
import SeatTypeBar from './components/SeatTypeBar.vue'
import SeatMap from './components/SeatMap.vue'
import SelectedSeatsInfo from './components/SelectedSeatsInfo.vue'
import BottomBar from './components/BottomBar.vue'

const router = useRouter()
const route = useRoute()

// 车次信息
const trainInfo = ref({
  trainId: '',
  trainNumber: '',
  departure: '',
  destination: '',
  date: '',
  passengers: 1
})

// 座位类型
const availableSeatTypes = ref<SeatInfo[]>([])
const selectedSeatType = ref('')

// 座位图
interface SeatMapRow {
  rowNumber: string
  seats: {
    seatNumber: string
    status: 'available' | 'occupied'
  }[]
}

const seatMap = ref<SeatMapRow[]>([])
const loadingSeatMap = ref(false)
const currentCarriage = ref('01')

// 已选座位
const selectedSeats = ref<string[]>([])

// 初始化
onMounted(() => {
  const query = route.query
  trainInfo.value = {
    trainId: query.trainId as string,
    trainNumber: query.trainNumber as string,
    departure: query.departure as string,
    destination: query.destination as string,
    date: query.date as string,
    passengers: Number(query.passengers) || 1
  }
  
  // 模拟座位类型数据
  availableSeatTypes.value = [
    { type: '二等座', price: 553, available: 99, status: 'available' },
    { type: '一等座', price: 888, available: 15, status: 'available' },
    { type: '商务座', price: 1748, available: 3, status: 'limited' }
  ]
  
  // 默认选择二等座
  if (availableSeatTypes.value.length > 0) {
    selectedSeatType.value = availableSeatTypes.value[0].type
    loadSeatMap()
  }
})

// 计算总价
const totalPrice = computed(() => {
  const seatType = availableSeatTypes.value.find(s => s.type === selectedSeatType.value)
  return seatType ? seatType.price * selectedSeats.value.length : 0
})

// 选择座位类型
const selectSeatType = (type: string) => {
  selectedSeatType.value = type
  selectedSeats.value = []
  loadSeatMap()
}

// 加载座位图
const loadSeatMap = async () => {
  loadingSeatMap.value = true
  
  try {
    // 这里应该调用真实的API
    // const response = await getSeatMap(trainInfo.value.trainId, selectedSeatType.value)
    
    // 模拟座位图数据
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const mockSeatMap: SeatMapRow[] = []
    const seatLabels = selectedSeatType.value === '商务座' 
      ? ['A', 'C', 'F'] 
      : selectedSeatType.value === '一等座'
      ? ['A', 'C', 'D', 'F']
      : ['A', 'B', 'C', 'D', 'F'] // 二等座
    
    for (let row = 1; row <= 16; row++) {
      const rowNumber = row.toString().padStart(2, '0')
      const seats = seatLabels.map(label => ({
        seatNumber: `${rowNumber}${label}`,
        status: Math.random() > 0.3 ? 'available' : 'occupied' as 'available' | 'occupied'
      }))
      
      mockSeatMap.push({ rowNumber, seats })
    }
    
    seatMap.value = mockSeatMap
  } catch (error) {
    console.error('加载座位图失败:', error)
    ElMessage.error('加载座位图失败，请稍后重试')
  } finally {
    loadingSeatMap.value = false
  }
}

// 切换座位选择
const toggleSeat = (seat: { seatNumber: string; status: 'available' | 'occupied' }) => {
  if (seat.status === 'occupied') {
    ElMessage.warning('该座位已被占用')
    return
  }
  
  const index = selectedSeats.value.indexOf(seat.seatNumber)
  if (index > -1) {
    selectedSeats.value.splice(index, 1)
  } else {
    if (selectedSeats.value.length >= trainInfo.value.passengers) {
      ElMessage.warning(`最多只能选择${trainInfo.value.passengers}个座位`)
      return
    }
    selectedSeats.value.push(seat.seatNumber)
  }
}

// 移除座位
const removeSeat = (seatNumber: string) => {
  const index = selectedSeats.value.indexOf(seatNumber)
  if (index > -1) {
    selectedSeats.value.splice(index, 1)
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 确认选座
const confirmSelection = () => {
  if (selectedSeats.value.length !== trainInfo.value.passengers) {
    ElMessage.warning(`请选择${trainInfo.value.passengers}个座位`)
    return
  }
  
  // 跳转到乘客信息页面
  router.push({
    path: '/passenger-info',
    query: {
      ...route.query,
      seatType: selectedSeatType.value,
      seats: selectedSeats.value.join(','),
      totalPrice: totalPrice.value
    }
  })
}
</script>

<style scoped>
.seat-selection-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}
</style>