<template>
  <section class="search-section">
    <div class="search-container">
      <h2 class="search-title">
        <el-icon size="28" color="#1890ff">
          <Tickets />
        </el-icon>
        车票预订
      </h2>
      
      <el-form 
        :model="searchForm" 
        class="search-form"
        :rules="searchRules"
        ref="searchFormRef"
      >
        <div class="form-row">
          <el-form-item label="出发地" prop="departure" class="form-group">
            <el-autocomplete
              v-model="searchForm.departure"
              :fetch-suggestions="queryDepartureSearch"
              placeholder="请输入出发城市"
              class="form-input"
              clearable
              :loading="departureLoading"
              @select="handleDepartureSelect"
              @clear="handleDepartureClear"
              @input="handleDepartureInput"
            >
              <template #default="{ item }">
                <div class="city-item" :class="{ 'placeholder-item': item.isPlaceholder }">
                  <span class="city-name">{{ item.name }}</span>
                  <span class="city-code" v-if="!item.isPlaceholder">{{ item.code }}</span>
                </div>
              </template>
              <template #empty>
                <div class="no-data">没有相关车站</div>
              </template>
            </el-autocomplete>
          </el-form-item>

          <div class="exchange-btn" @click="exchangeCities">
            <el-icon size="20" color="#1890ff">
              <Switch />
            </el-icon>
          </div>

          <el-form-item label="目的地" prop="destination" class="form-group">
            <el-autocomplete
              v-model="searchForm.destination"
              :fetch-suggestions="queryDestinationSearch"
              placeholder="请输入目的城市"
              class="form-input"
              clearable
              :loading="destinationLoading"
              @select="handleDestinationSelect"
              @clear="handleDestinationClear"
              @input="handleDestinationInput"
            >
              <template #default="{ item }">
                <div class="city-item" :class="{ 'placeholder-item': item.isPlaceholder }">
                  <span class="city-name">{{ item.name }}</span>
                  <span class="city-code" v-if="!item.isPlaceholder">{{ item.code }}</span>
                </div>
              </template>
              <template #empty>
                <div class="no-data">没有相关车站</div>
              </template>
            </el-autocomplete>
          </el-form-item>

          <el-form-item label="出发日期" prop="departureDate" class="form-group">
            <el-date-picker
              v-model="searchForm.departureDate"
              type="date"
              placeholder="请选择出发日期"
              class="form-input"
              :disabled-date="disabledDate"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>

          <div class="search-btn-group">
            <el-button 
              type="primary" 
              size="large" 
              @click="handleSearch"
              :loading="searchLoading"
              class="search-btn"
            >
              <el-icon><Search /></el-icon>
              搜索车次
            </el-button>
          </div>
        </div>
      </el-form>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Tickets, Switch, Search } from '@element-plus/icons-vue'
import { getHotStations } from '@/api/station'
import { ElMessage } from 'element-plus'
import { pinyin } from 'pinyin-pro'

const router = useRouter()

// 表单引用
const searchFormRef = ref()
const searchLoading = ref(false)

// 搜索表单数据
const searchForm = reactive({
  departure: '',
  destination: '',
  departureId: null,
  destinationId: null,
  departureDate: '',
  passengerCount: 1,
  seatType: 'all'
})

// 标记是否从下拉列表中选择
const departureSelected = ref(false)
const destinationSelected = ref(false)

// 加载状态
const departureLoading = ref(false)
const destinationLoading = ref(false)

// 表单验证规则
const searchRules = {
  departure: [
    { required: true, message: '请输入出发城市', trigger: 'blur' }
  ],
  destination: [
    { required: true, message: '请输入目的城市', trigger: 'blur' }
  ],
  departureDate: [
    { required: true, message: '请选择出发日期', trigger: 'change' }
  ]
}

// 热门车站数据
const hotStations = ref([])

// 按首字母分组的车站数据
const groupedStations = computed(() => {
  const groups = {}
  hotStations.value.forEach(station => {
    // 获取车站名称的首字母
    const firstLetter = station.name.charAt(0).toUpperCase()
    if (!groups[firstLetter]) {
      groups[firstLetter] = []
    }
    groups[firstLetter].push(station)
  })
  
  // 转换为数组并排序
  return Object.keys(groups)
    .sort()
    .map(letter => ({
      letter,
      stations: groups[letter].sort((a, b) => a.name.localeCompare(b.name))
    }))
})

// 加载热门车站数据
const loadHotStations = async () => {
  try {
    const response = await getHotStations()
    if (response.code === 200 && response.data) {
      hotStations.value = response.data
      // 设置默认的出发地和目的地为前两个热门车站
      if (hotStations.value.length >= 2) {
        searchForm.departure = hotStations.value[0].name
        searchForm.destination = hotStations.value[1].name
        searchForm.departureId = hotStations.value[0].id
        searchForm.destinationId = hotStations.value[1].id
      }
    }
  } catch (error) {
    ElMessage.error('加载热门车站数据失败')
    // 使用备用数据
    hotStations.value = []
    searchForm.departure = ''
    searchForm.destination = ''
    searchForm.departureId = null
    searchForm.destinationId = null
  }
}

// 出发地搜索
const queryDepartureSearch = (queryString, cb) => {
  departureLoading.value = true
  
  // 模拟异步搜索延迟
  setTimeout(() => {
    let results = []
    
    if (queryString) {
      results = hotStations.value.filter(station => {
        const query = queryString.toLowerCase()
        const stationName = station.name.toLowerCase()
        const stationCode = station.code.toLowerCase()
        const pinyinFirst = pinyin(station.name, { pattern: 'first', toneType: 'none' }).toLowerCase()
        const pinyinFull = pinyin(station.name, { toneType: 'none' }).toLowerCase()
        
        return stationName.includes(query) ||
               stationCode.includes(query) ||
               pinyinFirst.includes(query) ||
               pinyinFull.includes(query)
      })
      
      // 如果没有搜索结果，添加一个不可选择的提示项
      if (results.length === 0) {
        results = [{ name: '没有相关车站', code: '', id: null, disabled: true, isPlaceholder: true }]
      }
    } else {
      results = hotStations.value
    }
    
    departureLoading.value = false
    cb(results)
  }, 300)
}

// 目的地搜索
const queryDestinationSearch = (queryString, cb) => {
  destinationLoading.value = true
  
  // 模拟异步搜索延迟
  setTimeout(() => {
    let results = []
    
    if (queryString) {
      results = hotStations.value.filter(station => {
        const query = queryString.toLowerCase()
        const stationName = station.name.toLowerCase()
        const stationCode = station.code.toLowerCase()
        const pinyinFirst = pinyin(station.name, { pattern: 'first', toneType: 'none' }).toLowerCase()
        const pinyinFull = pinyin(station.name, { toneType: 'none' }).toLowerCase()
        
        return stationName.includes(query) ||
               stationCode.includes(query) ||
               pinyinFirst.includes(query) ||
               pinyinFull.includes(query)
      })
      
      // 如果没有搜索结果，添加一个不可选择的提示项
      if (results.length === 0) {
        results = [{ name: '没有相关车站', code: '', id: null, disabled: true, isPlaceholder: true }]
      }
    } else {
      results = hotStations.value
    }
    
    destinationLoading.value = false
    cb(results)
  }, 300)
}

// 选择出发地
const handleDepartureSelect = (item) => {
  if (item.disabled || item.isPlaceholder) return
  
  searchForm.departure = item.name
  searchForm.departureId = item.id
  departureSelected.value = true
}

// 选择目的地
const handleDestinationSelect = (item) => {
  if (item.disabled || item.isPlaceholder) return
  
  searchForm.destination = item.name
  searchForm.destinationId = item.id
  destinationSelected.value = true
}

// 出发地清空处理
const handleDepartureClear = () => {
  searchForm.departureId = null
  departureSelected.value = false
}

// 目的地清空处理
const handleDestinationClear = () => {
  searchForm.destinationId = null
  destinationSelected.value = false
}

// 出发地输入处理
const handleDepartureInput = (value) => {
  if (!departureSelected.value) {
    searchForm.departureId = null
  }
  departureSelected.value = false
}

// 目的地输入处理
const handleDestinationInput = (value) => {
  if (!destinationSelected.value) {
    searchForm.destinationId = null
  }
  destinationSelected.value = false
}

// 交换出发地和目的地
const exchangeCities = () => {
  const tempName = searchForm.departure
  const tempId = searchForm.departureId
  const tempSelected = departureSelected.value
  
  searchForm.departure = searchForm.destination
  searchForm.departureId = searchForm.destinationId
  departureSelected.value = destinationSelected.value
  
  searchForm.destination = tempName
  searchForm.destinationId = tempId
  destinationSelected.value = tempSelected
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 搜索车次
const handleSearch = async () => {
  try {
    const valid = await searchFormRef.value.validate()
    if (valid) {
      // 验证出发地和目的地必须从下拉列表中选择
      if (!searchForm.departureId) {
        ElMessage.error('请从下拉列表中选择出发地')
        return
      }
      
      if (!searchForm.destinationId) {
        ElMessage.error('请从下拉列表中选择目的地')
        return
      }
      
      searchLoading.value = true
      
      // 验证必要参数
      if (!searchForm.departureDate) {
        ElMessage.error('请选择出发日期')
        searchLoading.value = false
        return
      }
      
      // 直接跳转到搜索结果页面
      router.push({
        path: '/ticket-search',
        query: {
          originStationId: searchForm.departureId,
          destinationStationId: searchForm.destinationId,
          date: searchForm.departureDate
        }
      })
      
      searchLoading.value = false
    }
  } catch (error) {
    searchLoading.value = false
  }
}

// 初始化默认日期
const initDefaultDate = () => {
  const today = new Date()
  searchForm.departureDate = today.toISOString().split('T')[0]
}

// 组件挂载时初始化
onMounted(async () => {
  initDefaultDate()
  await loadHotStations()
})
</script>

<style scoped>
.search-section {
  background-color: white;
  padding: 40px 20px;
  box-shadow: 0 -5px 15px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
  margin: -50px 20px 0;
  border-radius: 12px;
}

.search-container {
  max-width: 1200px;
  margin: 0 auto;
}

.search-title {
  text-align: center;
  font-size: 28px;
  margin-bottom: 30px;
  color: #1890ff;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-weight: 600;
}

.search-form {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
}

.form-row {
  display: flex;
  justify-content: center;
  align-items: end;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  padding: 0 10px;
}

.form-group {
  margin-bottom: 0;
  flex: 1;
  min-width: 220px;
  max-width: 280px;
}

.form-group :deep(.el-form-item__label) {
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
}

.form-input :deep(.el-input__inner) {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  background-color: #fafafa;
}

.form-input :deep(.el-input__inner:focus) {
  border-color: #1890ff;
  background-color: white;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.exchange-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: 2px solid #e0e0e0;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
  background-color: white;
  margin-bottom: 5px;
  flex-shrink: 0;
}

.exchange-btn:hover {
  border-color: #1890ff;
  background-color: #f0f8ff;
  transform: rotate(180deg);
}

.search-btn-group {
  display: flex;
  align-items: end;
  flex-shrink: 0;
  margin-left: 15px;
}

.search-btn {
  padding: 8px 24px;
  font-size: 14px;
  border-radius: 6px;
  height: 40px;
  min-width: 100px;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.25);
  transition: all 0.3s ease;
}

.search-btn:hover {
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4);
  transform: translateY(-1px);
}

.city-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.city-name {
  font-weight: 500;
  flex: 1;
}

.city-code {
  color: #999;
  font-size: 12px;
  margin-left: 10px;
}

/* 选择框选项样式 */
:deep(.el-select-dropdown__item) {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-select-group__title) {
  font-weight: 600;
  color: #1890ff;
  background-color: #f0f8ff;
  padding: 8px 12px;
  font-size: 14px;
}

.quick-options {
  display: flex;
  gap: 40px;
  align-items: center;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.option-label {
  font-weight: 500;
  color: #666;
  margin-right: 10px;
}

.passenger-count {
  display: flex;
  align-items: center;
}

.seat-type {
  display: flex;
  align-items: center;
}

/* 空数据提示样式 */
.no-data {
  padding: 10px;
  text-align: center;
  color: #999;
  font-size: 14px;
}

/* 覆盖Element Plus自动完成组件的默认选中样式 */
:deep(.el-autocomplete-suggestion__item.disabled-item),
:deep(.el-autocomplete-suggestion__item.disabled-item:hover),
:deep(.el-autocomplete-suggestion__item.disabled-item.highlighted) {
  background-color: #f8f8f8 !important;
  color: #999 !important;
  cursor: not-allowed !important;
  pointer-events: none !important;
}

/* 禁用项样式 */
:deep(.el-autocomplete-suggestion__list .disabled-item),
:deep(.el-autocomplete-suggestion__list .placeholder-item) {
  color: #999 !important;
  cursor: not-allowed !important;
  background-color: #f8f8f8 !important;
  padding: 12px 16px !important;
  border-radius: 4px !important;
  margin: 2px 4px !important;
  pointer-events: none !important;
}

/* 完全禁用所有交互状态 */
:deep(.el-autocomplete-suggestion__list .disabled-item:hover),
:deep(.el-autocomplete-suggestion__list .disabled-item:focus),
:deep(.el-autocomplete-suggestion__list .disabled-item:active),
:deep(.el-autocomplete-suggestion__list .disabled-item.is-highlighted),
:deep(.el-autocomplete-suggestion__list .disabled-item.hover),
:deep(.el-autocomplete-suggestion__list .disabled-item.selected),
:deep(.el-autocomplete-suggestion__list .disabled-item.el-autocomplete-suggestion__item--highlighted),
:deep(.el-autocomplete-suggestion__list .placeholder-item:hover),
:deep(.el-autocomplete-suggestion__list .placeholder-item:focus),
:deep(.el-autocomplete-suggestion__list .placeholder-item:active),
:deep(.el-autocomplete-suggestion__list .placeholder-item.is-highlighted),
:deep(.el-autocomplete-suggestion__list .placeholder-item.hover),
:deep(.el-autocomplete-suggestion__list .placeholder-item.selected),
:deep(.el-autocomplete-suggestion__list .placeholder-item.el-autocomplete-suggestion__item--highlighted) {
  background-color: #f8f8f8 !important;
  color: #999 !important;
  cursor: not-allowed !important;
  pointer-events: none !important;
  box-shadow: none !important;
  border: none !important;
  outline: none !important;
}

.disabled-item .city-name,
.placeholder-item .city-name {
  color: #999 !important;
  font-weight: 400 !important;
  text-align: center !important;
  width: 100% !important;
}

.disabled-item .city-code,
.placeholder-item .city-code {
  display: none !important;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .form-row {
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }
  
  .form-group {
    max-width: 300px;
    width: 100%;
  }
  
  .exchange-btn {
    order: 2;
  }
  
  .quick-options {
    flex-direction: column;
    gap: 15px;
  }
}

@media (max-width: 768px) {
  .search-section {
    margin: -30px 10px 0;
    padding: 30px 15px;
  }
  
  .search-title {
    font-size: 24px;
  }
  
  .form-group {
    min-width: auto;
  }
  
  .quick-options {
    align-items: flex-start;
  }
  
  .seat-type :deep(.el-radio-group) {
    flex-wrap: wrap;
  }
}
</style>