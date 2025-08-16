<script setup lang="ts">
import { ref, computed, withDefaults } from 'vue'
import { 
  ElCheckboxGroup, 
  ElCheckbox, 
  ElButton, 
  ElIcon,
  ElCollapse,
  ElCollapseItem
} from 'element-plus'
import { Filter, Refresh } from '@element-plus/icons-vue'

// 定义筛选条件接口
interface FilterOptions {
  trainTypes: string[]
  departureTime: string[]
  seatTypes: string[]
}

// 定义属性
interface Props {
  modelValue: FilterOptions
}

// 定义事件
interface Emits {
  'update:modelValue': [value: FilterOptions]
  change: [filters: FilterOptions]
}

const props = withDefaults(defineProps<Props>(), {})
const emit = defineEmits<Emits>()

// 是否显示筛选面板（移动端）
const showFilters = ref(false)

// 本地筛选数据
const localFilters = computed({
  get: () => props.modelValue,
  set: (value) => {
    emit('update:modelValue', value)
    emit('change', value)
  }
})

// 车次类型选项
const trainTypeOptions = [
  { label: 'G', name: '高速动车', color: '#1890ff' },
  { label: 'D', name: '动车', color: '#52c41a' },
  { label: 'C', name: '城际', color: '#faad14' },
  { label: 'K', name: '快速', color: '#722ed1' },
  { label: 'T', name: '特快', color: '#eb2f96' },
  { label: 'Z', name: '直达', color: '#f5222d' }
]

// 出发时间选项
const departureTimeOptions = [
  { label: 'morning', name: '06:00-12:00', icon: '🌅' },
  { label: 'afternoon', name: '12:00-18:00', icon: '☀️' },
  { label: 'evening', name: '18:00-24:00', icon: '🌆' },
  { label: 'night', name: '00:00-06:00', icon: '🌙' }
]

// 座位类型选项
const seatTypeOptions = [
  { label: '商务座', name: '商务座', price: '高' },
  { label: '一等座', name: '一等座', price: '中' },
  { label: '二等座', name: '二等座', price: '低' },
  { label: '硬卧', name: '硬卧', price: '中' },
  { label: '软卧', name: '软卧', price: '高' },
  { label: '硬座', name: '硬座', price: '低' }
]

// 重置筛选条件
const resetFilters = () => {
  localFilters.value = {
    trainTypes: [],
    departureTime: [],
    seatTypes: []
  }
}

// 获取已选择的筛选条件数量
const getFilterCount = computed(() => {
  return localFilters.value.trainTypes.length + 
         localFilters.value.departureTime.length + 
         localFilters.value.seatTypes.length
})

// 切换筛选面板显示
const toggleFilters = () => {
  showFilters.value = !showFilters.value
}
</script>

<template>
  <div class="filter-bar">
    <div class="container">
      <!-- 桌面端筛选 -->
      <div class="desktop-filters">
        <div class="filter-section">
          <div class="filter-header">
            <ElIcon :size="16" color="#1890ff">
              <Filter />
            </ElIcon>
            <span class="filter-title">筛选条件</span>
            <ElButton 
              v-if="getFilterCount > 0"
              type="text" 
              size="small" 
              @click="resetFilters"
              class="reset-btn"
            >
              <ElIcon :size="14">
                <Refresh />
              </ElIcon>
              重置
            </ElButton>
          </div>
          
          <div class="filter-content">
            <!-- 车次类型筛选 -->
            <div class="filter-group">
              <span class="filter-label">车次类型：</span>
              <ElCheckboxGroup v-model="localFilters.trainTypes" class="checkbox-group">
                <ElCheckbox 
                  v-for="option in trainTypeOptions" 
                  :key="option.label"
                  :label="option.label"
                  class="train-type-checkbox"
                >
                  <div class="train-type-item">
                    <span class="train-type-label" :style="{ color: option.color }">{{ option.label }}</span>
                    <span class="train-type-name">{{ option.name }}</span>
                  </div>
                </ElCheckbox>
              </ElCheckboxGroup>
            </div>
            
            <!-- 出发时间筛选 -->
            <div class="filter-group">
              <span class="filter-label">出发时间：</span>
              <ElCheckboxGroup v-model="localFilters.departureTime" class="checkbox-group">
                <ElCheckbox 
                  v-for="option in departureTimeOptions" 
                  :key="option.label"
                  :label="option.label"
                  class="time-checkbox"
                >
                  <div class="time-item">
                    <span class="time-icon">{{ option.icon }}</span>
                    <span class="time-name">{{ option.name }}</span>
                  </div>
                </ElCheckbox>
              </ElCheckboxGroup>
            </div>
            
            <!-- 座位类型筛选 -->
            <div class="filter-group">
              <span class="filter-label">座位类型：</span>
              <ElCheckboxGroup v-model="localFilters.seatTypes" class="checkbox-group">
                <ElCheckbox 
                  v-for="option in seatTypeOptions" 
                  :key="option.label"
                  :label="option.label"
                  class="seat-type-checkbox"
                >
                  <div class="seat-type-item">
                    <span class="seat-type-name">{{ option.name }}</span>
                    <span class="seat-type-price" :class="`price-${option.price}`">{{ option.price }}</span>
                  </div>
                </ElCheckbox>
              </ElCheckboxGroup>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 移动端筛选按钮 -->
      <div class="mobile-filter-btn">
        <ElButton 
          @click="toggleFilters" 
          class="filter-toggle-btn"
          :class="{ active: showFilters }"
        >
          <ElIcon :size="16">
            <Filter />
          </ElIcon>
          筛选
          <span v-if="getFilterCount > 0" class="filter-count">{{ getFilterCount }}</span>
        </ElButton>
        
        <ElButton 
          v-if="getFilterCount > 0"
          type="text" 
          size="small" 
          @click="resetFilters"
          class="mobile-reset-btn"
        >
          重置
        </ElButton>
      </div>
      
      <!-- 移动端筛选面板 -->
      <div class="mobile-filters" :class="{ show: showFilters }">
        <ElCollapse>
          <ElCollapseItem title="车次类型" name="trainTypes">
            <ElCheckboxGroup v-model="localFilters.trainTypes" class="mobile-checkbox-group">
              <ElCheckbox 
                v-for="option in trainTypeOptions" 
                :key="option.label"
                :label="option.label"
                class="mobile-checkbox"
              >
                <div class="mobile-train-type-item">
                  <span class="train-type-label" :style="{ color: option.color }">{{ option.label }}</span>
                  <span class="train-type-name">{{ option.name }}</span>
                </div>
              </ElCheckbox>
            </ElCheckboxGroup>
          </ElCollapseItem>
          
          <ElCollapseItem title="出发时间" name="departureTime">
            <ElCheckboxGroup v-model="localFilters.departureTime" class="mobile-checkbox-group">
              <ElCheckbox 
                v-for="option in departureTimeOptions" 
                :key="option.label"
                :label="option.label"
                class="mobile-checkbox"
              >
                <div class="mobile-time-item">
                  <span class="time-icon">{{ option.icon }}</span>
                  <span class="time-name">{{ option.name }}</span>
                </div>
              </ElCheckbox>
            </ElCheckboxGroup>
          </ElCollapseItem>
          
          <ElCollapseItem title="座位类型" name="seatTypes">
            <ElCheckboxGroup v-model="localFilters.seatTypes" class="mobile-checkbox-group">
              <ElCheckbox 
                v-for="option in seatTypeOptions" 
                :key="option.label"
                :label="option.label"
                class="mobile-checkbox"
              >
                <div class="mobile-seat-type-item">
                  <span class="seat-type-name">{{ option.name }}</span>
                  <span class="seat-type-price" :class="`price-${option.price}`">{{ option.price }}</span>
                </div>
              </ElCheckbox>
            </ElCheckboxGroup>
          </ElCollapseItem>
        </ElCollapse>
      </div>
    </div>
  </div>
</template>

<style scoped>
.filter-bar {
  background: white;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 桌面端筛选 */
.desktop-filters {
  display: block;
  padding: 16px 0;
}

.filter-section {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
}

.filter-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.filter-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.reset-btn {
  margin-left: auto;
  color: #8c8c8c;
}

.filter-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-group {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.filter-label {
  font-size: 14px;
  font-weight: 500;
  color: #595959;
  white-space: nowrap;
  min-width: 80px;
  line-height: 32px;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

/* 车次类型样式 */
.train-type-checkbox {
  margin-right: 0;
}

.train-type-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.train-type-label {
  font-size: 16px;
  font-weight: bold;
  min-width: 20px;
}

.train-type-name {
  font-size: 12px;
  color: #8c8c8c;
}

/* 时间筛选样式 */
.time-checkbox {
  margin-right: 0;
}

.time-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.time-icon {
  font-size: 16px;
}

.time-name {
  font-size: 13px;
  color: #595959;
}

/* 座位类型样式 */
.seat-type-checkbox {
  margin-right: 0;
}

.seat-type-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.seat-type-name {
  font-size: 13px;
  color: #595959;
}

.seat-type-price {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}

.price-高 {
  background: #fff2e8;
  color: #fa8c16;
}

.price-中 {
  background: #f6ffed;
  color: #52c41a;
}

.price-低 {
  background: #e6f7ff;
  color: #1890ff;
}

/* 移动端筛选按钮 */
.mobile-filter-btn {
  display: none;
  padding: 12px 0;
  justify-content: space-between;
  align-items: center;
}

.filter-toggle-btn {
  position: relative;
  padding: 8px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  background: white;
  display: flex;
  align-items: center;
  gap: 6px;
}

.filter-toggle-btn.active {
  border-color: #1890ff;
  color: #1890ff;
}

.filter-count {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #ff4d4f;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mobile-reset-btn {
  color: #8c8c8c;
}

/* 移动端筛选面板 */
.mobile-filters {
  display: none;
  background: white;
  border-top: 1px solid #f0f0f0;
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
}

.mobile-filters.show {
  max-height: 500px;
  padding: 16px 0;
}

.mobile-checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.mobile-checkbox {
  margin-right: 0;
  width: 100%;
}

.mobile-train-type-item,
.mobile-time-item,
.mobile-seat-type-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 8px 0;
}

/* 复选框样式优化 */
:deep(.el-checkbox) {
  margin-right: 0;
}

:deep(.el-checkbox__label) {
  padding-left: 8px;
  font-size: 14px;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #1890ff;
  border-color: #1890ff;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #1890ff;
}

/* 折叠面板样式优化 */
:deep(.el-collapse-item__header) {
  font-weight: 600;
  color: #262626;
  padding: 12px 0;
}

:deep(.el-collapse-item__content) {
  padding: 12px 0 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .desktop-filters {
    display: none;
  }
  
  .mobile-filter-btn {
    display: flex;
  }
  
  .mobile-filters {
    display: block;
  }
  
  .container {
    padding: 0 16px;
  }
}

@media (max-width: 480px) {
  .filter-group {
    flex-direction: column;
    gap: 8px;
  }
  
  .filter-label {
    min-width: auto;
    line-height: 1.5;
  }
  
  .checkbox-group {
    gap: 8px;
  }
}
</style>