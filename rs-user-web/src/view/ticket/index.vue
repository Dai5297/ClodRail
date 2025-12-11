<template>
  <div class="min-h-screen bg-slate-50 pb-20">
    <!-- 顶部背景图 -->
    <div class="h-48 bg-gradient-to-r from-blue-600 to-indigo-700 relative overflow-hidden">
      <div class="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1474487548417-781cb71495f3?auto=format&fit=crop&q=80')] bg-cover bg-center opacity-20"></div>
      <div class="absolute inset-0 bg-black/10"></div>
      <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-full flex flex-col justify-center">
        <h1 class="text-3xl font-bold text-white mb-2">车票查询</h1>
        <p class="text-blue-100">轻松预订，开启您的美好旅程</p>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-16 relative z-10">
      <!-- Search Bar -->
      <TicketSearch
        :initial-params="searchParams"
        :show-title="false"
        search-button-text="重新查询"
        @search="handleSearch"
      />

      <!-- Filter & Sort Bar -->
      <div class="flex flex-col lg:flex-row gap-6">
        <!-- Sidebar Filter -->
        <div class="w-full lg:w-64 flex-shrink-0 space-y-6">
          <div class="bg-white rounded-2xl p-6 shadow-sm sticky top-24 border border-slate-100">
            <h3 class="font-bold text-slate-900 mb-6 flex items-center gap-2">
              <i class="ri-filter-3-line text-primary"></i> 筛选条件
            </h3>
            
            <div class="space-y-6">
              <div>
                <div class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">车型</div>
                <a-checkbox-group v-model:value="filters.trainTypes" class="flex flex-col gap-3">
                  <a-checkbox value="G">G-高铁</a-checkbox>
                  <a-checkbox value="D">D-动车</a-checkbox>
                  <a-checkbox value="Z">Z-直达</a-checkbox>
                  <a-checkbox value="K">K-普快</a-checkbox>
                </a-checkbox-group>
              </div>
              
              <div class="h-px bg-slate-100"></div>
              
              <div>
                <div class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">出发时段</div>
                <a-checkbox-group v-model:value="filters.departureTimes" class="flex flex-col gap-3">
                  <a-checkbox value="0-6">凌晨 (00:00-06:00)</a-checkbox>
                  <a-checkbox value="6-12">上午 (06:00-12:00)</a-checkbox>
                  <a-checkbox value="12-18">下午 (12:00-18:00)</a-checkbox>
                  <a-checkbox value="18-24">晚上 (18:00-24:00)</a-checkbox>
                </a-checkbox-group>
              </div>

               <div class="h-px bg-slate-100"></div>

               <div>
                 <a-checkbox v-model:checked="filters.hasTicket">只看有票</a-checkbox>
               </div>
            </div>
          </div>
        </div>

        <!-- Results List -->
        <div class="flex-1">
          <!-- Sort Bar -->
          <div class="bg-white px-6 py-4 rounded-2xl shadow-sm flex items-center justify-between text-sm mb-6 border border-slate-100">
            <div class="flex gap-8">
              <span 
                class="cursor-pointer font-bold flex items-center gap-1 transition-colors"
                :class="sortBy === 'startTime' ? 'text-primary' : 'text-slate-500 hover:text-slate-900'"
                @click="handleSort('startTime')"
              >
                出发时间 <i class="ri-arrow-up-line" v-if="sortBy === 'startTime'"></i>
              </span>
              <span 
                class="cursor-pointer font-bold flex items-center gap-1 transition-colors"
                :class="sortBy === 'duration' ? 'text-primary' : 'text-slate-500 hover:text-slate-900'"
                @click="handleSort('duration')"
              >
                耗时最短 <i class="ri-arrow-up-line" v-if="sortBy === 'duration'"></i>
              </span>
              <span 
                class="cursor-pointer font-bold flex items-center gap-1 transition-colors"
                :class="sortBy === 'price' ? 'text-primary' : 'text-slate-500 hover:text-slate-900'"
                @click="handleSort('price')"
              >
                价格最低 <i class="ri-arrow-up-line" v-if="sortBy === 'price'"></i>
              </span>
            </div>
            <span class="text-slate-400">共找到 {{ filteredTickets.length }} 个车次</span>
          </div>

          <!-- Loading State -->
          <div v-if="loading" class="space-y-4">
             <a-skeleton active :paragraph="{ rows: 4 }" class="bg-white p-6 rounded-2xl" v-for="i in 3" :key="i" />
          </div>

          <!-- Empty State -->
          <div v-else-if="filteredTickets.length === 0" class="bg-white rounded-2xl py-16 text-center border border-slate-100 shadow-sm">
            <div class="w-24 h-24 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4 text-slate-300">
               <i class="ri-train-line text-4xl"></i>
            </div>
            <h3 class="text-lg font-bold text-slate-900">暂无符合条件的车次</h3>
            <p class="text-slate-500 mt-2">请尝试更改筛选条件或搜索日期</p>
          </div>

          <!-- Ticket List -->
          <div v-else class="space-y-4">
            <TicketCard
              v-for="ticket in filteredTickets"
              :key="ticket.trainId"
              :ticket="ticket"
              @view-detail="handleViewDetail"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { searchTickets } from '@/api/ticket'
import TicketSearch from '@/components/TicketSearch.vue'
import TicketCard from './components/TicketCard.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const tickets = ref([])
const searchParams = reactive({})

const filters = reactive({
  trainTypes: [],
  departureTimes: [],
  hasTicket: false
})

const sortBy = ref('startTime')

// Initialize search params from route query
const initParams = () => {
  const { originStationId, destinationStationId, date } = route.query
  if (originStationId && destinationStationId && date) {
    searchParams.originStationId = originStationId
    searchParams.destinationStationId = destinationStationId
    searchParams.date = date
    fetchTickets()
  }
}

const fetchTickets = async () => {
  loading.value = true
  try {
    // Construct API params
    const params = {
      departureDate: searchParams.date,
      originStationId: searchParams.originStationId,
      destinationStationId: searchParams.destinationStationId
    }
    
    const response = await searchTickets(params)
    if (response.code === 200) {
      tickets.value = response.data?.records || []
    } else {
      message.error(response.message || '查询失败')
      tickets.value = []
    }
  } catch (error) {
    message.error('查询出错')
    tickets.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = (params) => {
  // Update route query to allow sharing/refreshing
  router.push({
    path: '/ticket',
    query: {
      originStationId: params.originStationId,
      destinationStationId: params.destinationStationId,
      date: params.date
    }
  }).then(() => {
    // Update local params and fetch
    searchParams.originStationId = params.originStationId
    searchParams.destinationStationId = params.destinationStationId
    searchParams.date = params.date
    fetchTickets()
  })
}

const handleSort = (type) => {
  sortBy.value = type
}

const handleViewDetail = (ticket) => {
  // Navigate to booking page or show modal
  router.push({
    path: '/ticket-detail', 
    query: {
      ticketId: ticket.id,
      date: searchParams.date,
      originStationId: searchParams.originStationId,
      destinationStationId: searchParams.destinationStationId
    }
  })
}

// Client-side filtering and sorting
const filteredTickets = computed(() => {
  let result = [...tickets.value]

  // Filter by train type
  if (filters.trainTypes.length > 0) {
    result = result.filter(t => {
      const type = t.trainCode ? t.trainCode[0] : ''
      return filters.trainTypes.includes(type)
    })
  }

  // Filter by time
  if (filters.departureTimes.length > 0) {
    result = result.filter(t => {
      const hour = parseInt(t.startTime.split(':')[0])
      return filters.departureTimes.some(range => {
        const [start, end] = range.split('-').map(Number)
        return hour >= start && hour < end
      })
    })
  }

  // Filter by available tickets
  if (filters.hasTicket) {
    result = result.filter(t => t.seatTypes.some(s => s.remainingSeats > 0))
  }

  // Sort
  result.sort((a, b) => {
    if (sortBy.value === 'startTime') {
      return a.startTime.localeCompare(b.startTime)
    } else if (sortBy.value === 'duration') {
       return a.duration.localeCompare(b.duration) 
    } else if (sortBy.value === 'price') {
      const getMinPrice = (t) => {
          if (!t.seatTypes || t.seatTypes.length === 0) return 999999
          return Math.min(...t.seatTypes.map(s => s.price))
      }
      return getMinPrice(a) - getMinPrice(b)
    }
    return 0
  })

  return result
})

onMounted(() => {
  initParams()
})
</script>
