<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { 
  ElCard, 
  ElTable, 
  ElTableColumn, 
  ElPagination, 
  ElButton, 
  ElTag, 
  ElDatePicker, 
  ElSelect, 
  ElOption, 
  ElInput, 
  ElSkeleton,
  ElIcon,
  ElEmpty,
  ElTooltip
} from 'element-plus'
import { 
  Document,
  Search,
  Refresh,
  View,
  Download,
  Calendar
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

// 定义订单接口
interface Order {
  id: string
  orderNumber: string
  trainNumber: string
  departure: string
  arrival: string
  departureTime: string
  arrivalTime: string
  seatType: string
  seatNumber: string
  passengerCount: number
  totalAmount: number
  status: 'pending' | 'paid' | 'completed' | 'cancelled' | 'refunded'
  createTime: string
  payTime?: string
  completeTime?: string
}

// 定义属性
interface Props {
  orders: Order[]
  total: number
  loading: boolean
}

// 定义事件
interface Emits {
  loadOrders: [params: {
    page: number
    size: number
    status?: string
    trainNumber?: string
    dateRange?: [string, string]
  }]
  viewOrder: [orderId: string]
  downloadTicket: [orderId: string]
}

const props = withDefaults(defineProps<Props>(), {
  orders: () => [],
  total: 0,
  loading: false
})

const emit = defineEmits<Emits>()
const router = useRouter()

// 搜索表单
const searchForm = reactive({
  status: '',
  trainNumber: '',
  dateRange: null as [string, string] | null
})

// 分页参数
const pagination = reactive({
  current: 1,
  size: 10
})

// 订单状态配置
const statusConfig: Record<string, { text: string; color: string; bgColor: string }> = {
  pending: { text: '待支付', color: '#faad14', bgColor: '#fff7e6' },
  paid: { text: '已支付', color: '#1890ff', bgColor: '#e6f4ff' },
  completed: { text: '已完成', color: '#52c41a', bgColor: '#f6ffed' },
  cancelled: { text: '已取消', color: '#8c8c8c', bgColor: '#f5f5f5' },
  refunded: { text: '已退款', color: '#ff4d4f', bgColor: '#fff2f0' }
}

// 状态选项
const statusOptions = [
  { label: '全部状态', value: '' },
  { label: '待支付', value: 'pending' },
  { label: '已支付', value: 'paid' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' },
  { label: '已退款', value: 'refunded' }
]

// 计算属性
const displayOrders = computed(() => props.orders || [])

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 格式化日期
const formatDate = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleDateString('zh-CN')
}

// 格式化时间（仅时分）
const formatTimeOnly = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 计算行程时长
const calculateDuration = (departureTime: string, arrivalTime: string) => {
  if (!departureTime || !arrivalTime) return '-'
  
  const departure = new Date(departureTime)
  const arrival = new Date(arrivalTime)
  const duration = arrival.getTime() - departure.getTime()
  
  const hours = Math.floor(duration / (1000 * 60 * 60))
  const minutes = Math.floor((duration % (1000 * 60 * 60)) / (1000 * 60))
  
  return `${hours}小时${minutes}分钟`
}

// 获取订单状态配置
const getStatusConfig = (status: string) => {
  return statusConfig[status] || statusConfig.pending
}

// 获取座位类型名称
const getSeatTypeName = (seatType: string) => {
  const seatTypeMap: Record<string, string> = {
    'business': '商务座',
    'first': '一等座',
    'second': '二等座',
    'hard_seat': '硬座',
    'soft_seat': '软座',
    'hard_sleeper': '硬卧',
    'soft_sleeper': '软卧'
  }
  return seatTypeMap[seatType] || seatType
}

// 搜索订单
const searchOrders = () => {
  pagination.current = 1
  loadOrders()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    status: '',
    trainNumber: '',
    dateRange: null
  })
  pagination.current = 1
  loadOrders()
}

// 加载订单
const loadOrders = () => {
  const params = {
    page: pagination.current,
    size: pagination.size,
    ...(searchForm.status && { status: searchForm.status }),
    ...(searchForm.trainNumber && { trainNumber: searchForm.trainNumber }),
    ...(searchForm.dateRange && { dateRange: searchForm.dateRange })
  }
  emit('loadOrders', params)
}

// 分页变化
const handlePageChange = (page: number) => {
  pagination.current = page
  loadOrders()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadOrders()
}

// 查看订单详情
const viewOrderDetail = (order: Order) => {
  emit('viewOrder', order.id)
  router.push(`/order-detail/${order.id}`)
}

// 下载电子票
const downloadTicket = (order: Order) => {
  emit('downloadTicket', order.id)
}

// 判断是否可以下载电子票
const canDownloadTicket = (status: string) => {
  return ['paid', 'completed'].includes(status)
}

// 初始化加载
loadOrders()
</script>

<template>
  <div class="order-history">
    <ElCard class="history-card" shadow="hover">
      <!-- 卡片头部 -->
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <ElIcon :size="20" color="#1890ff">
              <Document />
            </ElIcon>
            <span class="header-title">订单记录</span>
            <span class="order-count">（共 {{ total }} 条）</span>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-form">
          <div class="search-item">
            <label class="search-label">订单状态：</label>
            <ElSelect v-model="searchForm.status" placeholder="全部状态" style="width: 120px;">
              <ElOption 
                v-for="option in statusOptions" 
                :key="option.value" 
                :label="option.label" 
                :value="option.value" 
              />
            </ElSelect>
          </div>
          
          <div class="search-item">
            <label class="search-label">车次号：</label>
            <ElInput 
              v-model="searchForm.trainNumber" 
              placeholder="请输入车次号" 
              style="width: 150px;"
              clearable
            />
          </div>
          
          <div class="search-item">
            <label class="search-label">订单日期：</label>
            <ElDatePicker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 240px;"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </div>
          
          <div class="search-actions">
            <ElButton type="primary" :icon="Search" @click="searchOrders" :loading="loading">
              搜索
            </ElButton>
            <ElButton :icon="Refresh" @click="resetSearch">
              重置
            </ElButton>
          </div>
        </div>
      </div>
      
      <!-- 订单表格 -->
      <div class="table-section">
        <!-- 加载状态 -->
        <ElSkeleton v-if="loading" animated>
          <template #template>
            <div class="table-skeleton">
              <el-skeleton-item v-for="i in 5" :key="i" variant="rect" style="height: 60px; margin-bottom: 16px;" />
            </div>
          </template>
        </ElSkeleton>
        
        <!-- 订单表格 -->
        <ElTable 
          v-else
          :data="displayOrders" 
          stripe 
          class="order-table"
          empty-text="暂无订单记录"
        >
          <ElTableColumn prop="orderNumber" label="订单号" width="180" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="order-number">
                <span class="number-text">{{ row.orderNumber }}</span>
                <ElButton 
                  type="primary" 
                  link 
                  size="small" 
                  @click="viewOrderDetail(row)"
                >
                  查看详情
                </ElButton>
              </div>
            </template>
          </ElTableColumn>
          
          <ElTableColumn label="行程信息" width="300">
            <template #default="{ row }">
              <div class="trip-info">
                <div class="trip-route">
                  <span class="train-number">{{ row.trainNumber }}</span>
                  <span class="route">{{ row.departure }} → {{ row.arrival }}</span>
                </div>
                <div class="trip-time">
                  <span class="time">{{ formatTimeOnly(row.departureTime) }}</span>
                  <span class="separator">-</span>
                  <span class="time">{{ formatTimeOnly(row.arrivalTime) }}</span>
                  <span class="duration">{{ calculateDuration(row.departureTime, row.arrivalTime) }}</span>
                </div>
                <div class="trip-date">
                  {{ formatDate(row.departureTime) }}
                </div>
              </div>
            </template>
          </ElTableColumn>
          
          <ElTableColumn label="座位信息" width="120">
            <template #default="{ row }">
              <div class="seat-info">
                <div class="seat-type">{{ getSeatTypeName(row.seatType) }}</div>
                <div class="seat-number">{{ row.seatNumber }}</div>
                <div class="passenger-count">{{ row.passengerCount }}人</div>
              </div>
            </template>
          </ElTableColumn>
          
          <ElTableColumn prop="totalAmount" label="订单金额" width="100" align="right">
            <template #default="{ row }">
              <div class="amount">
                <span class="currency">¥</span>
                <span class="value">{{ row.totalAmount.toFixed(2) }}</span>
              </div>
            </template>
          </ElTableColumn>
          
          <ElTableColumn label="订单状态" width="100" align="center">
            <template #default="{ row }">
              <ElTag 
                :color="getStatusConfig(row.status).bgColor" 
                :style="{ color: getStatusConfig(row.status).color, border: `1px solid ${getStatusConfig(row.status).color}` }"
                size="small"
              >
                {{ getStatusConfig(row.status).text }}
              </ElTag>
            </template>
          </ElTableColumn>
          
          <ElTableColumn label="创建时间" width="160">
            <template #default="{ row }">
              <div class="create-time">
                <ElIcon :size="14" color="#8c8c8c">
                  <Calendar />
                </ElIcon>
                <span>{{ formatTime(row.createTime) }}</span>
              </div>
            </template>
          </ElTableColumn>
          
          <ElTableColumn label="操作" width="120" align="center" fixed="right">
            <template #default="{ row }">
              <div class="table-actions">
                <ElTooltip content="查看详情" placement="top">
                  <ElButton 
                    type="primary" 
                    :icon="View" 
                    size="small" 
                    circle 
                    @click="viewOrderDetail(row)"
                  />
                </ElTooltip>
                
                <ElTooltip 
                  v-if="canDownloadTicket(row.status)"
                  content="下载电子票" 
                  placement="top"
                >
                  <ElButton 
                    type="success" 
                    :icon="Download" 
                    size="small" 
                    circle 
                    @click="downloadTicket(row)"
                  />
                </ElTooltip>
              </div>
            </template>
          </ElTableColumn>
        </ElTable>
        
        <!-- 空状态 -->
        <div v-if="!loading && displayOrders.length === 0" class="empty-state">
          <ElEmpty description="暂无订单记录" />
        </div>
      </div>
      
      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-section">
        <ElPagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </ElCard>
  </div>
</template>

<style scoped>
.order-history {
  max-width: 1200px;
}

.history-card {
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.order-count {
  font-size: 14px;
  color: #8c8c8c;
}

.search-section {
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.search-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.search-label {
  font-size: 14px;
  color: #595959;
  font-weight: 500;
}

.search-actions {
  display: flex;
  gap: 8px;
}

.table-section {
  margin-bottom: 20px;
}

.table-skeleton {
  padding: 20px 0;
}

.order-table {
  width: 100%;
}

.order-number {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.number-text {
  font-family: 'Courier New', monospace;
  font-size: 13px;
  color: #262626;
}

.trip-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.trip-route {
  display: flex;
  align-items: center;
  gap: 8px;
}

.train-number {
  background: #e6f4ff;
  color: #1890ff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.route {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.trip-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #595959;
}

.time {
  font-weight: 500;
}

.separator {
  color: #d9d9d9;
}

.duration {
  color: #8c8c8c;
  font-size: 12px;
}

.trip-date {
  font-size: 12px;
  color: #8c8c8c;
}

.seat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  text-align: center;
}

.seat-type {
  font-size: 13px;
  font-weight: 500;
  color: #262626;
}

.seat-number {
  font-size: 12px;
  color: #1890ff;
  font-weight: 500;
}

.passenger-count {
  font-size: 12px;
  color: #8c8c8c;
}

.amount {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
  gap: 2px;
}

.currency {
  font-size: 12px;
  color: #8c8c8c;
}

.value {
  font-size: 16px;
  font-weight: 600;
  color: #ff4d4f;
}

.create-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #8c8c8c;
}

.table-actions {
  display: flex;
  gap: 4px;
  justify-content: center;
}

.empty-state {
  padding: 40px 0;
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .search-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-item {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
  }
  
  .search-label {
    min-width: 80px;
  }
  
  .search-actions {
    justify-content: center;
    margin-top: 16px;
  }
}

@media (max-width: 768px) {
  .order-table {
    font-size: 12px;
  }
  
  .trip-info {
    gap: 2px;
  }
  
  .trip-route {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .amount .value {
    font-size: 14px;
  }
  
  .table-actions {
    flex-direction: column;
    gap: 2px;
  }
}

@media (max-width: 480px) {
  .search-item {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-label {
    min-width: auto;
  }
  
  :deep(.el-table__body-wrapper) {
    overflow-x: auto;
  }
  
  .pagination-section {
    :deep(.el-pagination) {
      justify-content: center;
      flex-wrap: wrap;
    }
  }
}

/* 表格样式优化 */
:deep(.el-table) {
  --el-table-border-color: #f0f0f0;
  --el-table-bg-color: #ffffff;
  --el-table-tr-bg-color: #fafafa;
  --el-table-header-bg-color: #f8f9fa;
  --el-table-header-text-color: #262626;
}

:deep(.el-table th) {
  font-weight: 600;
  background-color: #f8f9fa;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-table .el-table__empty-block) {
  min-height: 200px;
}

/* 分页样式优化 */
:deep(.el-pagination) {
  --el-pagination-button-color: #595959;
  --el-pagination-hover-color: #1890ff;
}
</style>