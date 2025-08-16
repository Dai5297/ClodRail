<script setup lang="ts">
import { ref, computed, withDefaults } from 'vue'
import { 
  ElForm, 
  ElFormItem, 
  ElSelect, 
  ElOption, 
  ElDatePicker, 
  ElButton, 
  ElIcon,
  ElMessage
} from 'element-plus'
import { Switch } from '@element-plus/icons-vue'

// 定义搜索表单接口
interface SearchForm {
  departure: string
  destination: string
  date: string
  passengers: number
}

// 定义属性
interface Props {
  modelValue: SearchForm
  loading: boolean
}

// 定义事件
interface Emits {
  'update:modelValue': [value: SearchForm]
  search: [searchParams: SearchForm]
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

const emit = defineEmits<Emits>()

// 城市列表
const cities = [
  '北京', '上海', '广州', '深圳', '杭州', '南京', '成都', '重庆',
  '西安', '武汉', '天津', '苏州', '长沙', '郑州', '青岛', '大连',
  '宁波', '厦门', '福州', '无锡', '合肥', '昆明', '哈尔滨', '济南',
  '佛山', '长春', '温州', '石家庄', '南宁', '常州', '泉州', '南昌',
  '贵阳', '太原', '烟台', '嘉兴', '南通', '金华', '珠海', '惠州'
]

// 本地表单数据
const localForm = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 禁用日期（不能选择过去的日期）
const disabledDate = (time: Date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time.getTime() < today.getTime()
}

// 交换出发地和目的地
const swapCities = () => {
  const temp = localForm.value.departure
  localForm.value = {
    ...localForm.value,
    departure: localForm.value.destination,
    destination: temp
  }
}

// 搜索车次
const handleSearch = () => {
  if (!localForm.value.departure) {
    ElMessage.warning('请选择出发地')
    return
  }
  
  if (!localForm.value.destination) {
    ElMessage.warning('请选择目的地')
    return
  }
  
  if (!localForm.value.date) {
    ElMessage.warning('请选择出发日期')
    return
  }
  
  if (localForm.value.departure === localForm.value.destination) {
    ElMessage.warning('出发地和目的地不能相同')
    return
  }
  
  emit('search', localForm.value)
}

// 快速选择日期
const selectDate = (days: number) => {
  const date = new Date()
  date.setDate(date.getDate() + days)
  localForm.value = {
    ...localForm.value,
    date: date.toISOString().split('T')[0]
  }
}

// 获取日期显示文本
const getDateText = (days: number) => {
  const date = new Date()
  date.setDate(date.getDate() + days)
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const weekday = weekdays[date.getDay()]
  
  if (days === 0) return `今天 ${weekday}`
  if (days === 1) return `明天 ${weekday}`
  if (days === 2) return `后天 ${weekday}`
  return `${date.getMonth() + 1}/${date.getDate()} ${weekday}`
}
</script>

<template>
  <div class="search-bar">
    <div class="container">
      <div class="search-content">
        <!-- 搜索表单 -->
        <ElForm :model="localForm" class="search-form" inline>
          <div class="form-row">
            <ElFormItem label="出发地" class="city-item">
              <ElSelect 
                v-model="localForm.departure" 
                placeholder="请选择出发地" 
                filterable
                class="city-select"
              >
                <ElOption
                  v-for="city in cities"
                  :key="city"
                  :label="city"
                  :value="city"
                />
              </ElSelect>
            </ElFormItem>
            
            <div class="swap-container">
              <ElButton @click="swapCities" type="text" class="swap-btn">
                <ElIcon :size="20">
                  <Switch />
                </ElIcon>
              </ElButton>
            </div>
            
            <ElFormItem label="目的地" class="city-item">
              <ElSelect 
                v-model="localForm.destination" 
                placeholder="请选择目的地" 
                filterable
                class="city-select"
              >
                <ElOption
                  v-for="city in cities"
                  :key="city"
                  :label="city"
                  :value="city"
                />
              </ElSelect>
            </ElFormItem>
            
            <ElFormItem label="出发日期" class="date-item">
              <ElDatePicker
                v-model="localForm.date"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledDate"
                class="date-picker"
              />
            </ElFormItem>
            
            <ElFormItem label="乘客" class="passenger-item">
              <ElSelect v-model="localForm.passengers" class="passenger-select">
                <ElOption
                  v-for="num in 6"
                  :key="num"
                  :label="`${num}人`"
                  :value="num"
                />
              </ElSelect>
            </ElFormItem>
            
            <ElFormItem class="search-btn-item">
              <ElButton 
                type="primary" 
                size="large"
                @click="handleSearch" 
                :loading="loading"
                class="search-button"
              >
                搜索车次
              </ElButton>
            </ElFormItem>
          </div>
        </ElForm>
        
        <!-- 快速日期选择 -->
        <div class="quick-dates">
          <span class="quick-label">快速选择：</span>
          <div class="date-buttons">
            <button 
              v-for="days in [0, 1, 2, 3, 4, 5, 6]"
              :key="days"
              class="date-btn"
              :class="{ active: localForm.date === new Date(Date.now() + days * 24 * 60 * 60 * 1000).toISOString().split('T')[0] }"
              @click="selectDate(days)"
            >
              {{ getDateText(days) }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.search-bar {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  padding: 24px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.search-form {
  width: 100%;
}

.form-row {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  flex-wrap: wrap;
}

.city-item {
  flex: 1;
  min-width: 150px;
}

.swap-container {
  display: flex;
  align-items: center;
  height: 40px;
}

.swap-btn {
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
  color: #1890ff;
}

.swap-btn:hover {
  background: #f0f8ff;
  transform: rotate(180deg);
}

.date-item {
  flex: 1;
  min-width: 160px;
}

.passenger-item {
  min-width: 100px;
}

.search-btn-item {
  margin-left: auto;
}

.city-select,
.date-picker {
  width: 100%;
}

.passenger-select {
  width: 100px;
}

.search-button {
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(24, 144, 255, 0.3);
}

.quick-dates {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.quick-label {
  font-size: 14px;
  color: #8c8c8c;
  white-space: nowrap;
}

.date-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.date-btn {
  padding: 8px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  background: white;
  color: #595959;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.date-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.date-btn.active {
  background: #1890ff;
  border-color: #1890ff;
  color: white;
}

/* 表单项样式优化 */
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #262626;
  font-size: 14px;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .form-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .city-item,
  .date-item {
    min-width: auto;
  }
  
  .swap-container {
    order: 3;
    justify-content: center;
    height: auto;
    padding: 8px 0;
  }
  
  .search-btn-item {
    margin-left: 0;
    order: 6;
  }
  
  .search-button {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .search-bar {
    padding: 16px 0;
  }
  
  .container {
    padding: 0 16px;
  }
  
  .search-content {
    padding: 20px 16px;
  }
  
  .quick-dates {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .date-buttons {
    width: 100%;
    justify-content: space-between;
  }
  
  .date-btn {
    flex: 1;
    min-width: 0;
    padding: 6px 8px;
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .search-content {
    padding: 16px 12px;
  }
  
  .date-buttons {
    flex-direction: column;
  }
  
  .date-btn {
    width: 100%;
  }
}
</style>