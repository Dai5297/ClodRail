<template>
  <div class="search-section">
    <div class="search-container">
      <h2 v-if="showTitle" class="search-title">
        <el-icon size="28" color="#1890ff">
          <Tickets />
        </el-icon>
        车票查询
      </h2>
      
      <el-form 
        :model="searchForm" 
        class="search-form"
        :rules="searchRules"
        ref="searchFormRef"
      >
        <div class="form-row">
          <el-form-item label="出发地" prop="originStationId" class="form-group">
            <el-select
              v-model="searchForm.originStationId"
              placeholder="请选择出发站"
              class="form-input"
              filterable
              clearable
            >
              <el-option
                v-for="station in stations"
                :key="station.id"
                :label="`${station.name} (${station.code})`"
                :value="station.id"
              />
            </el-select>
          </el-form-item>

          <div class="exchange-btn" @click="exchangeStations">
            <el-icon size="20" color="#1890ff">
              <Switch />
            </el-icon>
          </div>

          <el-form-item label="目的地" prop="destinationStationId" class="form-group">
            <el-select
              v-model="searchForm.destinationStationId"
              placeholder="请选择到达站"
              class="form-input"
              filterable
              clearable
            >
              <el-option
                v-for="station in stations"
                :key="station.id"
                :label="`${station.name} (${station.code})`"
                :value="station.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="出发日期" prop="date" class="form-group">
            <el-date-picker
              v-model="searchForm.date"
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
              {{ searchButtonText }}
            </el-button>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Tickets, Switch, Search } from '@element-plus/icons-vue'
import { getStations } from '@/api/station'

// 定义 props
const props = defineProps({
  // 初始搜索参数
  initialParams: {
    type: Object,
    default: () => ({})
  },
  // 是否显示标题
  showTitle: {
    type: Boolean,
    default: true
  },
  // 搜索按钮文本
  searchButtonText: {
    type: String,
    default: '搜索车次'
  }
})

// 定义 emits
const emit = defineEmits(['search'])

// 表单引用
const searchFormRef = ref()
const searchLoading = ref(false)

// 搜索表单数据
const searchForm = reactive({
  originStationId: null,
  destinationStationId: null,
  date: ''
})

// 表单验证规则
const searchRules = {
  originStationId: [
    { required: true, message: '请选择出发站', trigger: 'change' }
  ],
  destinationStationId: [
    { required: true, message: '请选择到达站', trigger: 'change' }
  ],
  date: [
    { required: true, message: '请选择出发日期', trigger: 'change' }
  ]
}

// 数据
const stations = ref([])

// 初始化默认日期
const initDefaultDate = () => {
  const today = new Date()
  searchForm.date = today.toISOString().split('T')[0]
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 交换出发站和到达站
const exchangeStations = () => {
  const temp = searchForm.originStationId
  searchForm.originStationId = searchForm.destinationStationId
  searchForm.destinationStationId = temp
}

// 获取站点列表
const loadStations = async () => {
  try {
    const response = await getStations()
    if (response.code === 200) {
      stations.value = response.data
    } else {
      ElMessage.error(response.message || '获取站点列表失败')
    }
  } catch (error) {
    console.error('获取站点列表失败:', error)
    ElMessage.error('获取站点列表失败')
  }
}

// 搜索车票
const handleSearch = async () => {
  try {
    const valid = await searchFormRef.value.validate()
    if (valid) {
      searchLoading.value = true
      // 触发搜索事件，将搜索参数传递给父组件
      emit('search', {
        originStationId: searchForm.originStationId,
        destinationStationId: searchForm.destinationStationId,
        date: searchForm.date
      })
      searchLoading.value = false
    }
  } catch (error) {
    console.error('搜索参数验证失败:', error)
    searchLoading.value = false
  }
}

// 初始化搜索参数
const initSearchParams = () => {
  if (props.initialParams.originStationId) {
    searchForm.originStationId = parseInt(props.initialParams.originStationId)
  }
  if (props.initialParams.destinationStationId) {
    searchForm.destinationStationId = parseInt(props.initialParams.destinationStationId)
  }
  if (props.initialParams.date) {
    searchForm.date = props.initialParams.date
  }
}

// 监听初始参数变化
watch(() => props.initialParams, () => {
  initSearchParams()
}, { deep: true, immediate: true })

// 暴露方法给父组件
defineExpose({
  searchForm,
  handleSearch,
  exchangeStations
})

// 组件挂载时初始化
onMounted(async () => {
  initDefaultDate()
  await loadStations()
  initSearchParams()
})
</script>

<style scoped>
.search-section {
  background-color: white;
  padding: 30px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-container {
  max-width: 1200px;
  margin: 0 auto;
}

.search-title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
  color: #1890ff;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-weight: 600;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr auto 1fr 1fr auto;
  gap: 20px;
  align-items: end;
}

.form-group {
  margin-bottom: 0;
}

.form-input {
  width: 100%;
}

.exchange-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: #f0f9ff;
  border: 1px solid #1890ff;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
}

.exchange-btn:hover {
  background-color: #1890ff;
  color: white;
}

.search-btn {
  height: 40px;
  padding: 0 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .exchange-btn {
    display: none;
  }
}
</style>