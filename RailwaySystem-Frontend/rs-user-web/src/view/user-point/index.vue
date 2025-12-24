<template>
  <div class="h-full flex flex-col overflow-hidden space-y-4">
    <!-- 页面标题 -->
    <div class="border-b border-slate-200 pb-3 flex-shrink-0">
      <h2 class="text-xl font-bold text-slate-800">积分管理</h2>
      <p class="mt-1 text-xs text-slate-500">查看您的积分信息和明细记录</p>
    </div>

    <!-- 积分概览 -->
    <div class="relative flex-shrink-0 overflow-hidden rounded-xl bg-gradient-to-br from-indigo-600 to-purple-700 p-4 sm:p-6 text-white shadow-lg">
      <div v-if="overviewLoading" class="absolute inset-0 flex items-center justify-center bg-indigo-600/50 backdrop-blur-sm z-10">
        <svg class="animate-spin h-8 w-8 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      </div>
      
      <div class="relative z-0 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <div class="flex items-center gap-2 mb-1">
            <h3 class="text-base font-medium text-indigo-100">我的积分</h3>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-yellow-300" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 2a1 1 0 011 1v1.323l3.954 1.582 1.699-3.181a1 1 0 011.827 1.035L17.474 6.88A9.956 9.956 0 0120 12C20 17.523 15.523 22 10 22S0 17.523 0 12a9.956 9.956 0 012.526-5.12l-1.003-3.042a1 1 0 011.827-1.035l1.699 3.181L9 4.323V3a1 1 0 011-1zm-5 8.274l-.818 2.455a7.957 7.957 0 00-1.044 3.27A7.962 7.962 0 0010 20a7.962 7.962 0 006.862-3.999 7.957 7.957 0 00-1.044-3.27L15 10.274V12a5 5 0 11-10 0v-1.726z" clip-rule="evenodd" />
            </svg>
          </div>
          <div class="flex items-baseline gap-2">
            <span class="text-3xl font-bold tracking-tight">{{ pointsInfo.currentPoints || 0 }}</span>
            <span class="text-sm text-indigo-100">积分</span>
          </div>
        </div>
        
        <div class="grid grid-cols-3 gap-6 md:gap-8 border-t border-white/10 md:border-t-0 md:border-l md:pl-8 pt-4 md:pt-0">
          <div>
            <p class="text-xs text-indigo-200 mb-1">累计获得</p>
            <p class="text-lg font-semibold">{{ pointsInfo.totalEarned || 0 }}</p>
          </div>
          <div>
            <p class="text-xs text-indigo-200 mb-1">累计消费</p>
            <p class="text-lg font-semibold">{{ pointsInfo.totalSpent || 0 }}</p>
          </div>
          <div>
            <p class="text-xs text-indigo-200 mb-1">即将过期</p>
            <p class="text-lg font-semibold text-yellow-300">{{ pointsInfo.expiringSoon || 0 }}</p>
          </div>
        </div>
      </div>
      
      <!-- Decorative background elements -->
      <div class="absolute top-0 right-0 -mt-10 -mr-10 w-32 h-32 bg-white/10 rounded-full blur-2xl pointer-events-none"></div>
      <div class="absolute bottom-0 left-0 -mb-10 -ml-10 w-32 h-32 bg-indigo-500/20 rounded-full blur-2xl pointer-events-none"></div>
    </div>

    <!-- 积分明细 -->
    <div class="flex-1 flex flex-col min-h-0 bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
      <div class="flex-shrink-0 px-6 py-3 border-b border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <h3 class="text-lg font-semibold text-slate-800">积分明细</h3>
        
        <div class="flex flex-wrap items-center gap-3">
          <!-- 类型筛选 -->
          <div class="relative">
            <select
              v-model="filterForm.type"
              @change="handleFilter"
              class="block w-32 pl-3 pr-8 py-2 text-sm border border-slate-300 rounded-lg bg-white focus:outline-none focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 appearance-none cursor-pointer"
            >
              <option value="">全部类型</option>
              <option value="earn">获得</option>
              <option value="spend">消费</option>
              <option value="expire">过期</option>
            </select>
            <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none text-slate-500">
              <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </div>
          </div>
          
          <!-- 日期筛选 (Simplified) -->
          <div class="flex items-center gap-2">
            <input 
              type="date" 
              v-model="startDate"
              class="block w-36 px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="开始日期"
              @change="handleFilter"
            />
            <span class="text-slate-400">-</span>
            <input 
              type="date" 
              v-model="endDate"
              class="block w-36 px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="结束日期"
              @change="handleFilter"
            />
          </div>
          
          <button 
            @click="handleReset"
            class="px-3 py-2 text-sm font-medium text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-lg transition-colors"
          >
            重置
          </button>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto">
        <div v-if="historyLoading" class="flex justify-center items-center py-20">
          <svg class="animate-spin h-8 w-8 text-indigo-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </div>
        
        <div v-else-if="historyList.length === 0" class="flex flex-col items-center justify-center py-16 text-center">
          <div class="bg-slate-50 p-4 rounded-full mb-3">
            <svg class="h-8 w-8 text-slate-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <h3 class="text-sm font-medium text-slate-900">暂无积分记录</h3>
          <p class="mt-1 text-sm text-slate-500">您的积分变动记录将显示在这里。</p>
        </div>
        
        <div v-else class="divide-y divide-slate-100">
          <div
            v-for="record in historyList"
            :key="record.id"
            class="flex items-center justify-between px-6 py-4 hover:bg-slate-50 transition-colors"
          >
            <div class="flex items-center gap-4">
              <div 
                class="flex-shrink-0 h-10 w-10 rounded-full flex items-center justify-center"
                :class="{
                  'bg-green-100 text-green-600': record.type === 'earn',
                  'bg-orange-100 text-orange-600': record.type === 'spend',
                  'bg-slate-100 text-slate-600': record.type === 'expire' || !['earn', 'spend'].includes(record.type)
                }"
              >
                <svg v-if="record.type === 'earn'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
                </svg>
                <svg v-else-if="record.type === 'spend'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M3 10a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z" clip-rule="evenodd" />
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd" />
                </svg>
              </div>
              <div>
                <p class="text-sm font-medium text-slate-900">{{ record.description }}</p>
                <p class="text-xs text-slate-500 mt-0.5">{{ formatDateTime(record.createTime) }}</p>
              </div>
            </div>
            <div 
              class="text-sm font-bold font-mono"
              :class="{
                'text-green-600': record.type === 'earn',
                'text-orange-600': record.type === 'spend',
                'text-slate-500': record.type === 'expire'
              }"
            >
              {{ record.type === 'earn' ? '+' : record.type === 'spend' ? '-' : '' }}{{ record.points }}
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="flex-shrink-0 flex items-center justify-between border-t border-slate-200 bg-slate-50 px-4 py-3 sm:px-6 rounded-b-xl" v-if="total > 0">
        <div class="flex flex-1 justify-between sm:hidden">
          <button 
            @click="handleCurrentChange(currentPage - 1)"
            :disabled="currentPage === 1"
            class="relative inline-flex items-center rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            上一页
          </button>
          <button 
            @click="handleCurrentChange(currentPage + 1)"
            :disabled="currentPage >= Math.ceil(total / pageSize)"
            class="relative ml-3 inline-flex items-center rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            下一页
          </button>
        </div>
        <div class="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
          <div>
            <p class="text-sm text-slate-700">
              显示第
              <span class="font-medium">{{ (currentPage - 1) * pageSize + 1 }}</span>
              到
              <span class="font-medium">{{ Math.min(currentPage * pageSize, total) }}</span>
              条，共
              <span class="font-medium">{{ total }}</span>
              条记录
            </p>
          </div>
          <div>
            <nav class="isolate inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
              <button 
                @click="handleCurrentChange(currentPage - 1)"
                :disabled="currentPage === 1"
                class="relative inline-flex items-center rounded-l-md px-2 py-2 text-slate-400 ring-1 ring-inset ring-slate-300 hover:bg-slate-50 focus:z-20 focus:outline-offset-0 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <span class="sr-only">Previous</span>
                <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M12.79 5.23a.75.75 0 01-.02 1.06L8.832 10l3.938 3.71a.75.75 0 11-1.04 1.08l-4.5-4.25a.75.75 0 010-1.08l4.5-4.25a.75.75 0 011.06.02z" clip-rule="evenodd" />
                </svg>
              </button>
              
              <template v-for="page in displayedPages" :key="page">
                <span v-if="page === '...'" class="relative inline-flex items-center px-4 py-2 text-sm font-semibold text-slate-700 ring-1 ring-inset ring-slate-300 focus:outline-offset-0">...</span>
                <button 
                  v-else
                  @click="handleCurrentChange(page)"
                  :class="[
                    page === currentPage ? 'z-10 bg-indigo-600 text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600' : 'text-slate-900 ring-1 ring-inset ring-slate-300 hover:bg-slate-50 focus:outline-offset-0',
                    'relative inline-flex items-center px-4 py-2 text-sm font-semibold focus:z-20'
                  ]"
                >
                  {{ page }}
                </button>
              </template>

              <button 
                @click="handleCurrentChange(currentPage + 1)"
                :disabled="currentPage >= Math.ceil(total / pageSize)"
                class="relative inline-flex items-center rounded-r-md px-2 py-2 text-slate-400 ring-1 ring-inset ring-slate-300 hover:bg-slate-50 focus:z-20 focus:outline-offset-0 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <span class="sr-only">Next</span>
                <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M7.21 14.77a.75.75 0 01.02-1.06L11.168 10 7.23 6.29a.75.75 0 111.04-1.08l4.5 4.25a.75.75 0 010 1.08l-4.5 4.25a.75.75 0 01-1.06-.02z" clip-rule="evenodd" />
                </svg>
              </button>
            </nav>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getPointsInfo, getPointsHistory } from '@/api/points.js'
import toast from '@/components/Toast'

const overviewLoading = ref(false)
const historyLoading = ref(false)
const pointsInfo = ref({})
const historyList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const filterForm = reactive({
  type: '',
  dateRange: null
})

const startDate = ref('')
const endDate = ref('')

// 计算显示的页码
const displayedPages = computed(() => {
  const pages = []
  const totalPages = Math.ceil(total.value / pageSize.value) || 0
  const current = currentPage.value
  
  if (totalPages <= 7) {
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(totalPages)
    } else if (current >= totalPages - 3) {
      pages.push(1)
      pages.push('...')
      for (let i = totalPages - 4; i <= totalPages; i++) {
        pages.push(i)
      }
    } else {
      pages.push(1)
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(totalPages)
    }
  }
  return pages
})

const fetchPointsInfo = async () => {
  try {
    overviewLoading.value = true
    const response = await getPointsInfo()
    pointsInfo.value = response.data || {}
  } catch (error) {
    toast.error('获取积分信息失败')
  } finally {
    overviewLoading.value = false
  }
}

const fetchPointsHistory = async () => {
  try {
    historyLoading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      type: filterForm.type || undefined,
      startDate: startDate.value || undefined,
      endDate: endDate.value || undefined
    }

    const response = await getPointsHistory(params)
    if (response.data) {
      historyList.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      historyList.value = []
      total.value = 0
    }
  } catch (error) {
    toast.error('获取积分明细失败')
    historyList.value = []
    total.value = 0
  } finally {
    historyLoading.value = false
  }
}

const handleFilter = () => {
  currentPage.value = 1
  fetchPointsHistory()
}

const handleReset = () => {
  filterForm.type = ''
  startDate.value = ''
  endDate.value = ''
  currentPage.value = 1
  fetchPointsHistory()
}

const handleCurrentChange = (page) => {
  if (page < 1 || page > Math.ceil(total.value / pageSize.value)) return
  currentPage.value = page
  fetchPointsHistory()
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  // 如果已经是格式化的字符串，直接返回
  if (typeof dateTime === 'string' && dateTime.includes('-')) {
    return dateTime
  }
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

onMounted(() => {
  fetchPointsInfo()
  fetchPointsHistory()
})
</script>