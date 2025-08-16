<script setup lang="ts">
import { ref, computed, withDefaults } from 'vue'
import { 
  ElRow, 
  ElCol, 
  ElSelect, 
  ElOption, 
  ElDatePicker, 
  ElButton, 
  ElIcon,
  ElMessage
} from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'

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
  loading?: boolean
}

// 定义事件
interface Emits {
  'update:modelValue': [value: SearchForm]
  search: [form: SearchForm]
  update: [form: SearchForm]
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

const emit = defineEmits<Emits>()

// 本地表单数据
const localForm = ref<SearchForm>({ ...props.modelValue })

// 城市选项
const cities = [
  '北京', '上海', '广州', '深圳', '成都', '重庆', '杭州', '南京',
  '西安', '武汉', '天津', '苏州', '长沙', '郑州', '青岛', '大连',
  '宁波', '厦门', '福州', '沈阳', '石家庄', '长春', '哈尔滨', '济南',
  '合肥', '南昌', '贵阳', '昆明', '兰州', '银川', '西宁', '乌鲁木齐'
]

// 乘客选项
const passengerOptions = [
  { label: '成人1', value: 1 },
  { label: '成人2', value: 2 },
  { label: '成人3', value: 3 },
  { label: '成人4', value: 4 },
  { label: '成人5', value: 5 }
]

// 计算属性 - 最小日期（今天）
const minDate = computed(() => {
  return new Date()
})

// 计算属性 - 最大日期（60天后）
const maxDate = computed(() => {
  const date = new Date()
  date.setDate(date.getDate() + 60)
  return date
})

// 监听表单变化
const updateForm = <K extends keyof SearchForm>(field: K, value: SearchForm[K]) => {
  (localForm.value as any)[field] = value
  emit('update:modelValue', { ...localForm.value })
  emit('update', { ...localForm.value })
}

// 交换出发地和目的地
const swapCities = () => {
  const temp = localForm.value.departure
  updateForm('departure', localForm.value.destination)
  updateForm('destination', temp)
  ElMessage.success('已交换出发地和目的地')
}

// 重置表单
const resetForm = () => {
  const today = new Date().toISOString().split('T')[0]
  const resetData: SearchForm = {
    departure: '',
    destination: '',
    date: today,
    passengers: 1
  }
  localForm.value = resetData
  emit('update:modelValue', resetData)
  emit('update', resetData)
  ElMessage.success('已重置搜索条件')
}

// 快速选择日期
const quickSelectDate = (days: number) => {
  const date = new Date()
  date.setDate(date.getDate() + days)
  const dateStr = date.toISOString().split('T')[0]
  updateForm('date', dateStr)
}

// 处理搜索
const handleSearch = () => {
  emit('search', { ...localForm.value })
}

// 获取日期显示文本
const getDateDisplayText = (dateStr: string) => {
  if (!dateStr) return ''
  
  const date = new Date(dateStr)
  const today = new Date()
  const tomorrow = new Date(today)
  tomorrow.setDate(today.getDate() + 1)
  
  if (date.toDateString() === today.toDateString()) {
    return '今天'
  } else if (date.toDateString() === tomorrow.toDateString()) {
    return '明天'
  }
  
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return weekdays[date.getDay()]
}
</script>

<template>
  <section class="search-section">
    <div class="search-container">
      <h1 class="search-title">火车票预订</h1>
      
      <div class="search-form">
        <!-- 快速日期选择 -->
        <div class="quick-date-bar">
          <span class="quick-date-label">快速选择：</span>
          <div class="quick-date-buttons">
            <ElButton 
              size="small" 
              type="primary" 
              plain
              @click="quickSelectDate(0)"
            >
              今天
            </ElButton>
            <ElButton 
              size="small" 
              type="primary" 
              plain
              @click="quickSelectDate(1)"
            >
              明天
            </ElButton>
            <ElButton 
              size="small" 
              type="primary" 
              plain
              @click="quickSelectDate(2)"
            >
              后天
            </ElButton>
          </div>
        </div>
        
        <!-- 搜索表单 -->
        <ElRow :gutter="20" class="form-row">
          <ElCol :span="5">
            <div class="form-group">
              <label>出发地</label>
              <ElSelect 
                :model-value="localForm.departure"
                @update:model-value="(val) => updateForm('departure', val)"
                placeholder="请选择出发地" 
                class="w-full"
                filterable
                clearable
              >
                <ElOption
                  v-for="city in cities"
                  :key="city"
                  :label="city"
                  :value="city"
                />
              </ElSelect>
            </div>
          </ElCol>
          
          <!-- 交换按钮 -->
          <ElCol :span="1">
            <div class="swap-button-container">
              <ElButton 
                type="primary" 
                :icon="Refresh" 
                circle 
                size="small"
                @click="swapCities"
                class="swap-btn"
                title="交换出发地和目的地"
              />
            </div>
          </ElCol>
          
          <ElCol :span="5">
            <div class="form-group">
              <label>目的地</label>
              <ElSelect 
                :model-value="localForm.destination"
                @update:model-value="(val) => updateForm('destination', val)"
                placeholder="请选择目的地" 
                class="w-full"
                filterable
                clearable
              >
                <ElOption
                  v-for="city in cities"
                  :key="city"
                  :label="city"
                  :value="city"
                />
              </ElSelect>
            </div>
          </ElCol>
          
          <ElCol :span="5">
            <div class="form-group">
              <label>出发日期</label>
              <ElDatePicker
                :model-value="localForm.date"
                @update:model-value="(val) => updateForm('date', val)"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="(date: Date) => date < minDate || date > maxDate"
                class="w-full"
              >
                <template #default="{ parsedValue }">
                  <div class="date-cell">
                    <span class="date-text">{{ parsedValue?.getDate() }}</span>
                    <span class="date-week">{{ getDateDisplayText(localForm.date) }}</span>
                  </div>
                </template>
              </ElDatePicker>
            </div>
          </ElCol>
          
          <ElCol :span="4">
            <div class="form-group">
              <label>乘客</label>
              <ElSelect 
                :model-value="localForm.passengers"
                @update:model-value="(val) => updateForm('passengers', val)"
                class="w-full"
              >
                <ElOption 
                  v-for="option in passengerOptions"
                  :key="option.value"
                  :label="option.label" 
                  :value="option.value" 
                />
              </ElSelect>
            </div>
          </ElCol>
          
          <ElCol :span="4">
            <div class="form-group">
              <label>&nbsp;</label>
              <div class="button-group">
                <ElButton 
                  type="primary" 
                  size="large" 
                  class="search-btn" 
                  @click="handleSearch"
                  :loading="loading"
                >
                  <ElIcon><Search /></ElIcon>
                  搜索车票
                </ElButton>
                <ElButton 
                  size="large" 
                  class="reset-btn" 
                  @click="resetForm"
                >
                  重置
                </ElButton>
              </div>
            </div>
          </ElCol>
        </ElRow>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* 搜索区域 */
.search-section {
  background: linear-gradient(135deg, #1890FF 0%, #096dd9 100%);
  padding: 48px 0;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.search-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="%23ffffff" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>') repeat;
  pointer-events: none;
}

.search-container {
  width: 100%;
  margin: 0;
  padding: 0 24px;
  position: relative;
  z-index: 1;
}

.search-title {
  font-size: 32px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 32px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.search-form {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

/* 快速日期选择 */
.quick-date-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.quick-date-label {
  font-size: 14px;
  color: #8c8c8c;
  font-weight: 500;
}

.quick-date-buttons {
  display: flex;
  gap: 8px;
}

/* 表单行 */
.form-row {
  align-items: end;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #8c8c8c;
  font-size: 12px;
  font-weight: 500;
}

.w-full {
  width: 100%;
}

/* 交换按钮 */
.swap-button-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding-top: 20px;
}

.swap-btn {
  background: #f0f9ff;
  border-color: #1890ff;
  color: #1890ff;
  transition: all 0.3s ease;
}

.swap-btn:hover {
  background: #1890ff;
  color: white;
  transform: rotate(180deg);
}

/* 按钮组 */
.button-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.search-btn {
  width: 100%;
  height: 40px;
  font-weight: bold;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.4);
}

.reset-btn {
  width: 100%;
  height: 32px;
  font-size: 12px;
  color: #8c8c8c;
  border-color: #d9d9d9;
}

/* 日期单元格 */
.date-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.date-text {
  font-size: 14px;
  font-weight: 600;
}

.date-week {
  font-size: 10px;
  color: #8c8c8c;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .search-container {
    padding: 0 16px;
  }
  
  .search-form {
    padding: 24px;
  }
  
  .form-row {
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .search-title {
    font-size: 24px;
    margin-bottom: 24px;
  }
  
  .search-form {
    padding: 16px;
  }
  
  .quick-date-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 16px;
  }
  
  .form-row .el-col {
    width: 100%;
  }
  
  .swap-button-container {
    padding-top: 0;
    order: -1;
  }
  
  .button-group {
    flex-direction: row;
  }
  
  .search-btn,
  .reset-btn {
    flex: 1;
    height: 44px;
  }
}

@media (max-width: 480px) {
  .search-container {
    padding: 0 12px;
  }
  
  .search-form {
    padding: 12px;
  }
  
  .quick-date-buttons {
    flex-wrap: wrap;
  }
  
  .button-group {
    flex-direction: column;
  }
}
</style>