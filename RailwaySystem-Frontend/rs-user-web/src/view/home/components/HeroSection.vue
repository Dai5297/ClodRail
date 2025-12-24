<template>
  <div class="relative pt-20 pb-32 overflow-hidden bg-background">
    <!-- Background Patterns -->
    <div class="absolute inset-0 hero-pattern z-0"></div>
    <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] bg-blue-100/50 rounded-full blur-3xl z-0"></div>
    <div class="absolute top-0 right-0 w-[400px] h-[400px] bg-indigo-100/40 rounded-full blur-3xl z-0"></div>

    <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col items-center text-center z-10">
      <span class="inline-flex items-center px-3 py-1 rounded-full bg-blue-50 text-primary text-xs font-semibold tracking-wide uppercase mb-6 border border-blue-100">
        CloudRail 2.0 Is Live
      </span>
      <h1 class="text-5xl md:text-6xl font-extrabold tracking-tight text-slate-900 mb-6 leading-tight">
        探索世界的<br/>
        <span class="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600">每一段旅程</span>
      </h1>
      <p class="mt-4 max-w-2xl text-xl text-slate-500 mb-12 font-light">
        下一代智能铁路票务系统。极速预订，智能规划，让每一次出行都成为享受。
      </p>

      <!-- Search Card -->
      <div class="w-full max-w-5xl glass-card rounded-3xl p-2 animate-slide-up relative z-20 text-left">
        <div class="bg-white rounded-2xl p-6 sm:p-8 shadow-sm">
          <div class="grid grid-cols-1 md:grid-cols-12 gap-6 items-end">
            <!-- Departure -->
            <div class="md:col-span-3 group">
              <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2 ml-1">出发地</label>
              <div class="relative">
                <i class="ri-map-pin-line absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 text-lg group-hover:text-primary transition-colors z-10"></i>
                <a-select
                  v-model:value="searchForm.departure"
                  show-search
                  placeholder="出发城市"
                  class="w-full !h-14 custom-select"
                  :options="stationOptions"
                  :filter-option="filterOption"
                  @change="handleDepartureChange"
                  :bordered="false"
                >
                  <template #option="{ value, label, code }">
                     <div class="flex justify-between items-center w-full">
                       <span>{{ label }}</span>
                       <span class="text-xs text-gray-400">{{ code }}</span>
                     </div>
                  </template>
                </a-select>
              </div>
            </div>
            
            <!-- Swap Button -->
            <div class="md:col-span-1 flex justify-center items-center h-full pb-3">
              <button @click="exchangeCities" class="w-10 h-10 rounded-full bg-slate-50 hover:bg-blue-50 text-slate-400 hover:text-primary transition-all border border-slate-100 hover:border-blue-200 flex items-center justify-center transform hover:rotate-180 duration-300">
                <i class="ri-arrow-left-right-line text-lg"></i>
              </button>
            </div>

            <!-- Destination -->
            <div class="md:col-span-3 group">
              <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2 ml-1">目的地</label>
              <div class="relative">
                <i class="ri-map-pin-time-line absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 text-lg group-hover:text-primary transition-colors z-10"></i>
                <a-select
                  v-model:value="searchForm.destination"
                  show-search
                  placeholder="到达城市"
                  class="w-full !h-14 custom-select"
                  :options="stationOptions"
                  :filter-option="filterOption"
                  @change="handleDestinationChange"
                  :bordered="false"
                >
                   <template #option="{ value, label, code }">
                     <div class="flex justify-between items-center w-full">
                       <span>{{ label }}</span>
                       <span class="text-xs text-gray-400">{{ code }}</span>
                     </div>
                  </template>
                </a-select>
              </div>
            </div>

            <!-- Date -->
            <div class="md:col-span-3 group">
              <label class="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2 ml-1">出发日期</label>
              <div class="relative">
                <i class="ri-calendar-line absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 text-lg group-hover:text-primary transition-colors z-10"></i>
                <a-date-picker 
                  v-model:value="searchForm.departureDate" 
                  class="w-full !h-14 custom-datepicker" 
                  :bordered="false" 
                  placeholder="选择日期"
                  :disabled-date="disabledDate"
                  value-format="YYYY-MM-DD"
                  :show-suffix-icon="false"
                />
              </div>
            </div>

            <!-- Search Button -->
            <div class="md:col-span-2">
              <button @click="handleSearch" class="w-full h-14 bg-primary hover:bg-blue-700 text-white rounded-2xl font-semibold shadow-lg shadow-blue-500/30 hover:shadow-blue-500/50 transition-all duration-300 flex items-center justify-center gap-2 group">
                <span v-if="!searchLoading">查询车次</span>
                <span v-else>查询中...</span>
                <i v-if="!searchLoading" class="ri-arrow-right-line group-hover:translate-x-1 transition-transform"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getHotStations } from '@/api/station'
import { message } from 'ant-design-vue'
import { pinyin } from 'pinyin-pro'
import dayjs from 'dayjs'

const router = useRouter()
const searchLoading = ref(false)

const searchForm = reactive({
  departure: undefined, // Stores station name or ID depending on a-select behavior
  destination: undefined,
  departureId: null,
  destinationId: null,
  departureDate: undefined
})

const hotStations = ref([])

const stationOptions = computed(() => {
  return hotStations.value.map(s => ({
    value: s.name, // Use name as value for display, but we need ID
    label: s.name,
    code: s.code,
    id: s.id
  }))
})

// Filter function for search
const filterOption = (input, option) => {
  const query = input.toLowerCase()
  const name = option.label.toLowerCase()
  const code = option.code ? option.code.toLowerCase() : ''
  const pinyinFirst = pinyin(option.label, { pattern: 'first', toneType: 'none' }).toLowerCase()
  const pinyinFull = pinyin(option.label, { toneType: 'none' }).toLowerCase()
  
  return name.includes(query) || code.includes(query) || pinyinFirst.includes(query) || pinyinFull.includes(query)
}

const handleDepartureChange = (value, option) => {
  searchForm.departureId = option.id
}

const handleDestinationChange = (value, option) => {
  searchForm.destinationId = option.id
}

const loadHotStations = async () => {
  try {
    const response = await getHotStations()
    if (response.code === 200 && response.data) {
      hotStations.value = response.data
      if (hotStations.value.length >= 2) {
        // Set default values
        const start = hotStations.value[0]
        const end = hotStations.value[1]
        
        searchForm.departure = start.name
        searchForm.departureId = start.id
        searchForm.destination = end.name
        searchForm.destinationId = end.id
      }
    }
  } catch (error) {
    message.error('加载车站数据失败')
  }
}

const exchangeCities = () => {
  const tempName = searchForm.departure
  const tempId = searchForm.departureId
  
  searchForm.departure = searchForm.destination
  searchForm.departureId = searchForm.destinationId
  
  searchForm.destination = tempName
  searchForm.destinationId = tempId
}

const disabledDate = (current) => {
  return current && current < dayjs().startOf('day')
}

const handleSearch = () => {
  if (!searchForm.departureId) {
    message.error('请选择出发地')
    return
  }
  if (!searchForm.destinationId) {
    message.error('请选择目的地')
    return
  }
  if (!searchForm.departureDate) {
    message.error('请选择出发日期')
    return
  }

  searchLoading.value = true
  
  router.push({
    path: '/ticket',
    query: {
      originStationId: searchForm.departureId,
      destinationStationId: searchForm.destinationId,
      date: searchForm.departureDate
    }
  })
  
  searchLoading.value = false
}

onMounted(async () => {
  searchForm.departureDate = dayjs().format('YYYY-MM-DD')
  await loadHotStations()
})
</script>

<style>
.custom-select .ant-select-selector {
  background-color: transparent !important;
  border: none !important;
  box-shadow: none !important;
  height: 100% !important;
  display: flex;
  align-items: center;
  padding-left: 40px !important;
}

.custom-select .ant-select-selection-search {
  padding-left: 30px !important;
}

.custom-select .ant-select-selection-placeholder {
  padding-left: 30px !important;
  font-size: 1rem;
  color: #94a3b8 !important;
}

.custom-select .ant-select-selection-item {
  padding-left: 30px !important;
  font-size: 1.125rem;
  font-weight: 500;
  color: #1e293b !important;
}

.custom-datepicker .ant-picker {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  height: 100% !important;
  padding-left: 50px !important;
}

.custom-datepicker .ant-picker-input > input {
  font-size: 1.125rem !important;
  font-weight: 500;
  color: #1e293b !important;
  padding-left: 25px !important;
}

/* Hide default calendar icon */
.custom-datepicker .ant-picker-suffix {
  display: none !important;
}

.hero-pattern {
  background-color: #f8fafc;
  background-image: radial-gradient(#e2e8f0 1px, transparent 1px);
  background-size: 24px 24px;
}

.animate-slide-up {
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
