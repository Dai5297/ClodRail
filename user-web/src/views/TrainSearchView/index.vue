<script setup lang="ts">
import { ref, computed, onMounted, withDefaults } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import SearchBar from './components/SearchBar.vue'
import FilterBar from './components/FilterBar.vue'
import TrainList from './components/TrainList.vue'
import { searchTrains } from '@/api/train'
import type { TrainSearchRequest, TrainSearchResponse, TrainInfo } from '@/api/train'

const router = useRouter()
const route = useRoute()

// 页面状态
const loading = ref(false)
const searched = ref(false)

// 搜索表单
const searchForm = ref({
  departure: '',
  destination: '',
  date: '',
  passengers: 1
})

// 筛选条件
const filters = ref({
  trainTypes: [] as string[],
  departureTime: [] as string[],
  seatTypes: [] as string[]
})

// 车次列表
const trainList = ref<TrainInfo[]>([])

// 筛选后的车次列表
const filteredTrains = computed(() => {
  let result = trainList.value

  // 按车次类型筛选
  if (filters.value.trainTypes.length > 0) {
    result = result.filter(train => {
      const trainType = train.number.charAt(0)
      return filters.value.trainTypes.includes(trainType)
    })
  }

  // 按出发时间筛选
  if (filters.value.departureTime.length > 0) {
    result = result.filter(train => {
      const hour = parseInt(train.departure.time.split(':')[0])
      return filters.value.departureTime.some(timeRange => {
        switch (timeRange) {
          case 'morning': return hour >= 6 && hour < 12
          case 'afternoon': return hour >= 12 && hour < 18
          case 'evening': return hour >= 18 && hour < 24
          case 'night': return hour >= 0 && hour < 6
          default: return true
        }
      })
    })
  }

  // 按座位类型筛选
  if (filters.value.seatTypes.length > 0) {
    result = result.filter(train => {
      return train.seats.some(seat =>
        filters.value.seatTypes.includes(seat.type) && seat.available > 0
      )
    })
  }

  return result
})

// 搜索车次
const handleSearch = async (searchParams: typeof searchForm.value) => {
  if (!searchParams.departure || !searchParams.destination || !searchParams.date) {
    ElMessage.warning('请填写完整的搜索条件')
    return
  }

  if (searchParams.departure === searchParams.destination) {
    ElMessage.warning('出发地和目的地不能相同')
    return
  }

  loading.value = true
  searched.value = true

  try {
    const request: TrainSearchRequest = {
      departure: searchParams.departure,
      arrival: searchParams.destination,
      date: searchParams.date
    }

    const response = await searchTrains(request)
    trainList.value = response.trains || []

    if (trainList.value.length === 0) {
      ElMessage.info('未找到符合条件的车次')
    }
  } catch (error) {
    console.error('搜索车次失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
    trainList.value = []
  } finally {
    loading.value = false
  }
}

// 更新筛选条件
const handleFilterChange = (newFilters: typeof filters.value) => {
  filters.value = { ...newFilters }
}

// 选择车次
const handleTrainSelect = (train: TrainInfo) => {
  // 将选中的车次信息存储到路由参数中
  router.push({
    name: 'ticket-search',
    query: {
      trainId: train.id.toString(),
      trainNumber: train.number,
      departure: train.departure.station,
      destination: train.arrival.station,
      departureTime: train.departure.time,
      arrivalTime: train.arrival.time,
      date: searchForm.value.date,
      passengers: searchForm.value.passengers.toString()
    }
  })
}

// 重置搜索
const handleReset = () => {
  trainList.value = []
  searched.value = false
  filters.value = {
    trainTypes: [],
    departureTime: [],
    seatTypes: []
  }
}

// 初始化
onMounted(() => {
  // 从路由参数中获取搜索条件
  const query = route.query
  if (query.departure && query.destination && query.date) {
    searchForm.value = {
      departure: query.departure as string,
      destination: query.destination as string,
      date: query.date as string,
      passengers: parseInt(query.passengers as string) || 1
    }

    // 自动执行搜索
    handleSearch(searchForm.value)
  }
})
</script>

<template>
  <div class="train-search-page">
    <!-- 搜索条件栏 -->
    <SearchBar
      v-model="searchForm"
      :loading="loading"
      @search="handleSearch"
    />

    <!-- 筛选条件 -->
    <FilterBar
      v-if="trainList.length > 0"
      v-model="filters"
      @change="handleFilterChange"
    />

    <!-- 车次列表 -->
    <TrainList
      :trains="filteredTrains"
      :loading="loading"
      :searched="searched"
      @select="handleTrainSelect"
      @reset="handleReset"
    />
  </div>
</template>

<style scoped>
.train-search-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .train-search-page {
    padding: 0;
  }
}
</style>
