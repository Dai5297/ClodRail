<template>
  <div class="bg-white rounded-3xl shadow-soft p-2 mb-8 border border-slate-100/50 max-w-6xl mx-auto">
    <div class="bg-slate-50/50 rounded-2xl p-6">
      <div class="grid grid-cols-1 md:grid-cols-12 gap-4 items-end">
        <!-- Origin -->
        <div class="md:col-span-3 group">
          <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2 ml-1">出发地</label>
          <div class="relative">
            <i class="ri-map-pin-line absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 group-hover:text-primary transition-colors z-10"></i>
            <a-select
              v-model:value="searchForm.originStationId"
              show-search
              placeholder="出发站"
              class="w-full !h-12 custom-search-select"
              :options="stationOptions"
              :filter-option="filterOption"
              :bordered="false"
            />
          </div>
        </div>

        <!-- Swap -->
        <div class="md:col-span-1 flex justify-center pb-2">
          <button @click="exchangeStations" class="w-10 h-10 rounded-full bg-white hover:bg-blue-50 text-slate-400 hover:text-primary shadow-sm hover:shadow transition-all border border-slate-200 hover:border-blue-200 flex items-center justify-center transform hover:rotate-180 duration-300">
            <i class="ri-arrow-left-right-line text-lg"></i>
          </button>
        </div>

        <!-- Destination -->
        <div class="md:col-span-3 group">
          <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2 ml-1">目的地</label>
          <div class="relative">
            <i class="ri-map-pin-time-line absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 group-hover:text-primary transition-colors z-10"></i>
            <a-select
              v-model:value="searchForm.destinationStationId"
              show-search
              placeholder="到达站"
              class="w-full !h-12 custom-search-select"
              :options="stationOptions"
              :filter-option="filterOption"
              :bordered="false"
            />
          </div>
        </div>

        <!-- Date -->
        <div class="md:col-span-3 group">
          <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2 ml-1">出发日期</label>
          <div class="relative">
            <i class="ri-calendar-line absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 group-hover:text-primary transition-colors z-10"></i>
            <a-date-picker
              v-model:value="searchForm.date"
              class="w-full !h-12 custom-search-picker"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledDate"
              :bordered="false"
              :show-suffix-icon="false"
            >
            </a-date-picker>
          </div>
        </div>

        <!-- Button -->
        <div class="md:col-span-2">
          <button 
            @click="handleSearch" 
            class="w-full h-12 bg-primary hover:bg-blue-600 text-white rounded-xl font-bold shadow-lg shadow-blue-500/20 hover:shadow-blue-500/40 transition-all duration-300 flex items-center justify-center gap-2"
          >
            <span v-if="!searchLoading">{{ searchButtonText }}</span>
            <i v-else class="ri-loader-4-line animate-spin text-xl"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { message } from 'ant-design-vue'
import { getStations } from '@/api/station'
import dayjs from 'dayjs'
import { pinyin } from 'pinyin-pro'

const props = defineProps({
  initialParams: {
    type: Object,
    default: () => ({})
  },
  showTitle: {
    type: Boolean,
    default: true
  },
  searchButtonText: {
    type: String,
    default: '搜索车次'
  }
})

const emit = defineEmits(['search'])

const searchLoading = ref(false)
const searchForm = reactive({
  originStationId: undefined,
  destinationStationId: undefined,
  date: undefined
})

const stations = ref([])

const stationOptions = computed(() => {
  return stations.value.map(s => ({
    value: s.id,
    label: s.name,
    code: s.code
  }))
})

const filterOption = (input, option) => {
  const query = input.toLowerCase()
  const name = option.label.toLowerCase()
  const code = option.code ? option.code.toLowerCase() : ''
  const pinyinFirst = pinyin(option.label, { pattern: 'first', toneType: 'none' }).toLowerCase()
  const pinyinFull = pinyin(option.label, { toneType: 'none' }).toLowerCase()
  
  return name.includes(query) || code.includes(query) || pinyinFirst.includes(query) || pinyinFull.includes(query)
}

const disabledDate = (current) => {
  return current && current < dayjs().startOf('day')
}

const exchangeStations = () => {
  const temp = searchForm.originStationId
  searchForm.originStationId = searchForm.destinationStationId
  searchForm.destinationStationId = temp
}

const loadStations = async () => {
  try {
    const response = await getStations()
    if (response.code === 200) {
      stations.value = response.data
    } else {
      message.error(response.message || '获取站点列表失败')
    }
  } catch (error) {
    message.error('获取站点列表失败')
  }
}

const handleSearch = async () => {
  if (!searchForm.originStationId || !searchForm.destinationStationId || !searchForm.date) {
    message.warning('请完善搜索条件')
    return
  }
  
  searchLoading.value = true
  emit('search', {
    originStationId: searchForm.originStationId,
    destinationStationId: searchForm.destinationStationId,
    date: searchForm.date
  })
  searchLoading.value = false
}

const initSearchParams = () => {
  if (props.initialParams.originStationId) {
    searchForm.originStationId = parseInt(props.initialParams.originStationId)
  }
  if (props.initialParams.destinationStationId) {
    searchForm.destinationStationId = parseInt(props.initialParams.destinationStationId)
  }
  if (props.initialParams.date) {
    searchForm.date = props.initialParams.date
  } else {
    searchForm.date = dayjs().format('YYYY-MM-DD')
  }
}

watch(() => props.initialParams, () => {
  initSearchParams()
}, { deep: true, immediate: true })

onMounted(async () => {
  await loadStations()
  initSearchParams()
})
</script>

<style>
.custom-search-select .ant-select-selector {
  background-color: white !important;
  border: 1px solid #e2e8f0 !important;
  border-radius: 0.75rem !important; /* rounded-xl */
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05) !important;
  height: 100% !important;
  display: flex !important;
  align-items: center !important;
  padding-left: 36px !important;
  transition: all 0.2s !important;
}
.custom-search-select:hover .ant-select-selector {
  border-color: #cbd5e1 !important;
}
.custom-search-select.ant-select-focused .ant-select-selector {
  border-color: #2563eb !important;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1) !important;
}
.custom-search-select .ant-select-selection-item {
  font-weight: 500;
  color: #1e293b;
}
.custom-search-select .ant-select-selection-placeholder {
  color: #94a3b8;
}

.custom-search-picker .ant-picker {
  background-color: white !important;
  border: 1px solid #e2e8f0 !important;
  border-radius: 0.75rem !important;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05) !important;
  height: 100% !important;
  padding-left: 46px !important;
  transition: all 0.2s !important;
}
.custom-search-picker:hover .ant-picker {
  border-color: #cbd5e1 !important;
}
.custom-search-picker.ant-picker-focused {
  border-color: #2563eb !important;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1) !important;
}
.custom-search-picker input {
  font-weight: 500;
  color: #1e293b !important;
  padding-left: 25px !important;
}
.custom-search-picker input::placeholder {
  color: #94a3b8 !important;
}
/* Hide default calendar icon */
.custom-search-picker .ant-picker-suffix {
  display: none !important;
}
</style>
