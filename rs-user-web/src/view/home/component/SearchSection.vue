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
              @select="handleDepartureSelect"
            >
              <template #default="{ item }">
                <div class="city-item">
                  <span class="city-name">{{ item.name }}</span>
                  <span class="city-code">{{ item.code }}</span>
                </div>
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
              placeholder="请输入到达城市"
              class="form-input"
              clearable
              @select="handleDestinationSelect"
            >
              <template #default="{ item }">
                <div class="city-item">
                  <span class="city-name">{{ item.name }}</span>
                  <span class="city-code">{{ item.code }}</span>
                </div>
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

          <el-form-item label="返程日期" class="form-group">
            <el-date-picker
              v-model="searchForm.returnDate"
              type="date"
              placeholder="可选"
              class="form-input"
              :disabled-date="disabledReturnDate"
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

        <!-- 快捷选项 -->
        <div class="quick-options">
          <div class="passenger-count">
            <span class="option-label">乘车人：</span>
            <el-input-number 
              v-model="searchForm.passengerCount" 
              :min="1" 
              :max="6" 
              size="small"
            />
          </div>
          
          <div class="seat-type">
            <span class="option-label">座位类型：</span>
            <el-radio-group v-model="searchForm.seatType" size="small">
              <el-radio-button label="all">不限</el-radio-button>
              <el-radio-button label="business">商务座</el-radio-button>
              <el-radio-button label="first">一等座</el-radio-button>
              <el-radio-button label="second">二等座</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </el-form>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Tickets, Switch, Search } from '@element-plus/icons-vue'

const router = useRouter()

// 表单引用
const searchFormRef = ref()
const searchLoading = ref(false)

// 搜索表单数据
const searchForm = reactive({
  departure: '北京',
  destination: '上海',
  departureDate: '',
  returnDate: '',
  passengerCount: 1,
  seatType: 'all'
})

// 表单验证规则
const searchRules = {
  departure: [
    { required: true, message: '请输入出发城市', trigger: 'blur' }
  ],
  destination: [
    { required: true, message: '请输入到达城市', trigger: 'blur' }
  ],
  departureDate: [
    { required: true, message: '请选择出发日期', trigger: 'change' }
  ]
}

// 城市数据（模拟数据）
const cities = [
  { name: '北京', code: 'BJP' },
  { name: '上海', code: 'SHH' },
  { name: '广州', code: 'GZQ' },
  { name: '深圳', code: 'SZN' },
  { name: '杭州', code: 'HZH' },
  { name: '南京', code: 'NJH' },
  { name: '成都', code: 'CDW' },
  { name: '重庆', code: 'CQW' },
  { name: '西安', code: 'XAY' },
  { name: '武汉', code: 'WHN' },
  { name: '长沙', code: 'CSQ' },
  { name: '郑州', code: 'ZZF' }
]

// 出发地搜索
const queryDepartureSearch = (queryString, cb) => {
  const results = queryString
    ? cities.filter(city => city.name.toLowerCase().includes(queryString.toLowerCase()))
    : cities
  cb(results)
}

// 目的地搜索
const queryDestinationSearch = (queryString, cb) => {
  const results = queryString
    ? cities.filter(city => city.name.toLowerCase().includes(queryString.toLowerCase()))
    : cities
  cb(results)
}

// 选择出发地
const handleDepartureSelect = (item) => {
  searchForm.departure = item.name
}

// 选择目的地
const handleDestinationSelect = (item) => {
  searchForm.destination = item.name
}

// 交换出发地和目的地
const exchangeCities = () => {
  const temp = searchForm.departure
  searchForm.departure = searchForm.destination
  searchForm.destination = temp
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 禁用返程日期（不能早于出发日期）
const disabledReturnDate = (time) => {
  if (!searchForm.departureDate) return false
  return time.getTime() < new Date(searchForm.departureDate).getTime()
}

// 搜索车次
const handleSearch = async () => {
  try {
    const valid = await searchFormRef.value.validate()
    if (valid) {
      searchLoading.value = true
      
      // 直接跳转到搜索结果页面
      router.push({
        path: '/tickets',
        query: {
          from: searchForm.departure,
          to: searchForm.destination,
          date: searchForm.departureDate,
          returnDate: searchForm.returnDate,
          passengers: searchForm.passengerCount,
          seatType: searchForm.seatType
        }
      })
      
      searchLoading.value = false
    }
  } catch (error) {
    console.error('表单验证失败:', error)
    searchLoading.value = false
  }
}

// 初始化默认日期
const initDefaultDate = () => {
  const today = new Date()
  searchForm.departureDate = today.toISOString().split('T')[0]
}

// 组件挂载时初始化
initDefaultDate()
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
}

.form-row {
  display: grid;
  grid-template-columns: 1fr auto 1fr 1fr 1fr auto;
  gap: 20px;
  align-items: end;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 0;
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
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-input :deep(.el-input__inner:focus) {
  border-color: #1890ff;
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
}

.exchange-btn:hover {
  border-color: #1890ff;
  background-color: #f0f8ff;
  transform: rotate(180deg);
}

.search-btn-group {
  display: flex;
  align-items: end;
}

.search-btn {
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 8px;
  height: 48px;
  min-width: 120px;
}

.city-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.city-name {
  font-weight: 500;
}

.city-code {
  color: #999;
  font-size: 12px;
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .form-row {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .exchange-btn {
    display: none;
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
  
  .quick-options {
    align-items: flex-start;
  }
  
  .seat-type :deep(.el-radio-group) {
    flex-wrap: wrap;
  }
}
</style>