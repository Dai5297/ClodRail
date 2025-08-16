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
  ElSkeleton,
  ElIcon,
  ElEmpty,
  ElStatistic,
  ElDivider,
  ElProgress,
  ElTooltip
} from 'element-plus'
import { 
  Star,
  TrendCharts,
  Calendar,
  Plus,
  Minus,
  QuestionFilled
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

// 定义积分记录接口
interface PointsRecord {
  id: string
  type: 'earn' | 'spend' | 'expire'
  amount: number
  description: string
  source: string
  createTime: string
  orderId?: string
  expireTime?: string
}

// 定义积分信息接口
interface PointsInfo {
  totalPoints: number
  availablePoints: number
  usedPoints: number
  expiredPoints: number
  expiringPoints: number
  expiringDate: string
  level: string
  levelProgress: number
  nextLevelPoints: number
  thisYearEarned: number
  thisYearSpent: number
}

// 定义属性
interface Props {
  pointsInfo: PointsInfo | null
  pointsRecords: PointsRecord[]
  total: number
  loading: boolean
}

// 定义事件
interface Emits {
  loadRecords: [params: {
    page: number
    size: number
    type?: string
    dateRange?: [string, string]
  }]
  goToMall: []
}

const props = withDefaults(defineProps<Props>(), {
  pointsInfo: null,
  pointsRecords: () => [],
  total: 0,
  loading: false
})

const emit = defineEmits<Emits>()
const router = useRouter()

// 搜索表单
const searchForm = reactive({
  type: '',
  dateRange: null as [string, string] | null
})

// 分页参数
const pagination = reactive({
  current: 1,
  size: 10
})

// 积分类型配置
const typeConfig: Record<string, { text: string; color: string; bgColor: string; icon: any }> = {
  earn: { text: '获得', color: '#52c41a', bgColor: '#f6ffed', icon: Plus },
  spend: { text: '消费', color: '#ff4d4f', bgColor: '#fff2f0', icon: Minus },
  expire: { text: '过期', color: '#8c8c8c', bgColor: '#f5f5f5', icon: Calendar }
}

// 类型选项
const typeOptions = [
  { label: '全部类型', value: '' },
  { label: '积分获得', value: 'earn' },
  { label: '积分消费', value: 'spend' },
  { label: '积分过期', value: 'expire' }
]

// 会员等级配置
const levelConfig: Record<string, { text: string; color: string; nextLevel: string | null; requiredPoints: number }> = {
  'bronze': { text: '青铜会员', color: '#cd7f32', nextLevel: 'silver', requiredPoints: 1000 },
  'silver': { text: '白银会员', color: '#c0c0c0', nextLevel: 'gold', requiredPoints: 5000 },
  'gold': { text: '黄金会员', color: '#ffd700', nextLevel: 'platinum', requiredPoints: 10000 },
  'platinum': { text: '铂金会员', color: '#e5e4e2', nextLevel: 'diamond', requiredPoints: 20000 },
  'diamond': { text: '钻石会员', color: '#b9f2ff', nextLevel: null, requiredPoints: 0 }
}

// 计算属性
const displayPointsInfo = computed(() => props.pointsInfo || {
  totalPoints: 0,
  availablePoints: 0,
  usedPoints: 0,
  expiredPoints: 0,
  expiringPoints: 0,
  expiringDate: '',
  level: 'bronze',
  levelProgress: 0,
  nextLevelPoints: 1000,
  thisYearEarned: 0,
  thisYearSpent: 0
})

const displayRecords = computed(() => props.pointsRecords || [])

// 获取当前等级配置
const currentLevelConfig = computed(() => {
  return levelConfig[displayPointsInfo.value.level] || levelConfig.bronze
})

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

// 获取积分类型配置
const getTypeConfig = (type: string) => {
  return typeConfig[type] || typeConfig.earn
}

// 格式化积分数量
const formatPoints = (points: number, type: string) => {
  const prefix = type === 'earn' ? '+' : type === 'spend' ? '-' : ''
  return `${prefix}${Math.abs(points)}`
}

// 搜索记录
const searchRecords = () => {
  pagination.current = 1
  loadRecords()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    type: '',
    dateRange: null
  })
  pagination.current = 1
  loadRecords()
}

// 加载记录
const loadRecords = () => {
  const params = {
    page: pagination.current,
    size: pagination.size,
    ...(searchForm.type && { type: searchForm.type }),
    ...(searchForm.dateRange && { dateRange: searchForm.dateRange })
  }
  emit('loadRecords', params)
}

// 分页变化
const handlePageChange = (page: number) => {
  pagination.current = page
  loadRecords()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadRecords()
}

// 前往积分商城
const goToPointsMall = () => {
  emit('goToMall')
  router.push('/points-mall')
}

// 查看订单详情
const viewOrderDetail = (orderId: string) => {
  router.push(`/order-detail/${orderId}`)
}

// 初始化加载
loadRecords()
</script>

<template>
  <div class="points-info">
    <!-- 积分概览卡片 -->
    <ElCard class="overview-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <ElIcon :size="20" color="#1890ff">
              <Star />
            </ElIcon>
            <span class="header-title">积分概览</span>
          </div>
          <ElButton type="primary" @click="goToPointsMall">
            <ElIcon><Gift /></ElIcon>
            积分商城
          </ElButton>
        </div>
      </template>
      
      <!-- 加载状态 -->
      <ElSkeleton v-if="loading" animated>
        <template #template>
          <div class="overview-skeleton">
            <div class="stats-skeleton">
              <el-skeleton-item v-for="i in 4" :key="i" variant="rect" style="height: 80px; margin-right: 20px; display: inline-block; width: 150px;" />
            </div>
            <el-skeleton-item variant="rect" style="height: 100px; margin-top: 20px;" />
          </div>
        </template>
      </ElSkeleton>
      
      <!-- 积分统计 -->
      <div v-else class="overview-content">
        <div class="points-stats">
          <div class="stat-item main-stat">
            <ElStatistic 
              title="可用积分" 
              :value="displayPointsInfo.availablePoints" 
              :precision="0"
              class="points-statistic"
            >
              <template #suffix>
                <span class="points-unit">分</span>
              </template>
            </ElStatistic>
          </div>
          
          <div class="stat-item">
            <ElStatistic 
              title="累计获得" 
              :value="displayPointsInfo.totalPoints" 
              :precision="0"
            />
          </div>
          
          <div class="stat-item">
            <ElStatistic 
              title="已使用" 
              :value="displayPointsInfo.usedPoints" 
              :precision="0"
            />
          </div>
          
          <div class="stat-item">
            <ElStatistic 
              title="已过期" 
              :value="displayPointsInfo.expiredPoints" 
              :precision="0"
            />
          </div>
        </div>
        
        <ElDivider />
        
        <!-- 会员等级和即将过期提醒 -->
        <div class="additional-info">
          <div class="level-info">
            <div class="level-header">
              <h4 class="level-title">
                <ElIcon :size="16" :color="currentLevelConfig.color">
                  <Star />
                </ElIcon>
                {{ currentLevelConfig.text }}
              </h4>
              <ElTooltip content="会员等级说明" placement="top">
                <ElIcon :size="14" color="#8c8c8c">
                  <QuestionFilled />
                </ElIcon>
              </ElTooltip>
            </div>
            
            <div v-if="currentLevelConfig.nextLevel" class="level-progress">
              <div class="progress-info">
                <span class="progress-text">
                  距离{{ levelConfig[currentLevelConfig.nextLevel].text }}还需 
                  <strong>{{ displayPointsInfo.nextLevelPoints }}</strong> 积分
                </span>
              </div>
              <ElProgress 
                :percentage="displayPointsInfo.levelProgress" 
                :color="currentLevelConfig.color"
                :stroke-width="8"
                class="level-progress-bar"
              />
            </div>
            <div v-else class="max-level">
              <ElTag :color="currentLevelConfig.color" class="max-level-tag">
                已达到最高等级
              </ElTag>
            </div>
          </div>
          
          <div class="expiring-info">
            <div v-if="displayPointsInfo.expiringPoints > 0" class="expiring-alert">
              <ElIcon :size="16" color="#faad14">
                <Calendar />
              </ElIcon>
              <div class="expiring-text">
                <div class="expiring-amount">
                  <strong>{{ displayPointsInfo.expiringPoints }}</strong> 积分即将过期
                </div>
                <div class="expiring-date">
                  过期时间：{{ formatDate(displayPointsInfo.expiringDate) }}
                </div>
              </div>
            </div>
            
            <div class="year-stats">
              <div class="year-stat">
                <span class="stat-label">今年获得：</span>
                <span class="stat-value earn">+{{ displayPointsInfo.thisYearEarned }}</span>
              </div>
              <div class="year-stat">
                <span class="stat-label">今年消费：</span>
                <span class="stat-value spend">-{{ displayPointsInfo.thisYearSpent }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </ElCard>
    
    <!-- 积分记录卡片 -->
    <ElCard class="records-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <ElIcon :size="20" color="#1890ff">
              <TrendCharts />
            </ElIcon>
            <span class="header-title">积分记录</span>
            <span class="record-count">（共 {{ total }} 条）</span>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-form">
          <div class="search-item">
            <label class="search-label">记录类型：</label>
            <ElSelect v-model="searchForm.type" placeholder="全部类型" style="width: 120px;">
              <ElOption 
                v-for="option in typeOptions" 
                :key="option.value" 
                :label="option.label" 
                :value="option.value" 
              />
            </ElSelect>
          </div>
          
          <div class="search-item">
            <label class="search-label">记录日期：</label>
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
            <ElButton type="primary" @click="searchRecords" :loading="loading">
              搜索
            </ElButton>
            <ElButton @click="resetSearch">
              重置
            </ElButton>
          </div>
        </div>
      </div>
      
      <!-- 记录表格 -->
      <div class="table-section">
        <!-- 加载状态 -->
        <ElSkeleton v-if="loading" animated>
          <template #template>
            <div class="table-skeleton">
              <el-skeleton-item v-for="i in 5" :key="i" variant="rect" style="height: 60px; margin-bottom: 16px;" />
            </div>
          </template>
        </ElSkeleton>
        
        <!-- 记录表格 -->
        <ElTable 
          v-else
          :data="displayRecords" 
          stripe 
          class="records-table"
          empty-text="暂无积分记录"
        >
          <ElTableColumn label="类型" width="100" align="center">
            <template #default="{ row }">
              <ElTag 
                :color="getTypeConfig(row.type).bgColor" 
                :style="{ color: getTypeConfig(row.type).color, border: `1px solid ${getTypeConfig(row.type).color}` }"
                size="small"
              >
                <ElIcon :size="12" class="type-icon">
                  <component :is="getTypeConfig(row.type).icon" />
                </ElIcon>
                {{ getTypeConfig(row.type).text }}
              </ElTag>
            </template>
          </ElTableColumn>
          
          <ElTableColumn label="积分变动" width="120" align="right">
            <template #default="{ row }">
              <div class="points-change" :class="row.type">
                <span class="points-amount">{{ formatPoints(row.amount, row.type) }}</span>
                <span class="points-unit">分</span>
              </div>
            </template>
          </ElTableColumn>
          
          <ElTableColumn prop="description" label="说明" min-width="200" show-overflow-tooltip />
          
          <ElTableColumn prop="source" label="来源" width="120" show-overflow-tooltip />
          
          <ElTableColumn label="时间" width="160">
            <template #default="{ row }">
              <div class="record-time">
                <ElIcon :size="14" color="#8c8c8c">
                  <Calendar />
                </ElIcon>
                <span>{{ formatTime(row.createTime) }}</span>
              </div>
            </template>
          </ElTableColumn>
          
          <ElTableColumn label="操作" width="100" align="center">
            <template #default="{ row }">
              <ElButton 
                v-if="row.orderId"
                type="primary" 
                link 
                size="small"
                @click="viewOrderDetail(row.orderId)"
              >
                查看订单
              </ElButton>
              <span v-else class="no-action">-</span>
            </template>
          </ElTableColumn>
        </ElTable>
        
        <!-- 空状态 -->
        <div v-if="!loading && displayRecords.length === 0" class="empty-state">
          <ElEmpty description="暂无积分记录" />
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
.points-info {
  max-width: 1000px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.overview-card,
.records-card {
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

.record-count {
  font-size: 14px;
  color: #8c8c8c;
}

.overview-skeleton {
  padding: 20px 0;
}

.stats-skeleton {
  display: flex;
  justify-content: space-between;
}

.overview-content {
  padding: 8px 0;
}

.points-stats {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.main-stat {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  color: white;
  border: none;
}

.main-stat :deep(.el-statistic__content) {
  color: white;
}

.main-stat :deep(.el-statistic__head) {
  color: rgba(255, 255, 255, 0.8);
}

.points-statistic :deep(.el-statistic__number) {
  font-size: 32px;
  font-weight: 700;
}

.points-unit {
  font-size: 16px;
  font-weight: 500;
  margin-left: 4px;
}

.additional-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
  margin-top: 16px;
}

.level-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.level-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.level-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  display: flex;
  align-items: center;
  gap: 6px;
}

.level-progress {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-text {
  font-size: 14px;
  color: #595959;
}

.level-progress-bar {
  margin-top: 4px;
}

.max-level {
  display: flex;
  align-items: center;
}

.max-level-tag {
  border: none;
  color: white;
  font-weight: 500;
}

.expiring-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.expiring-alert {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: #fffbe6;
  border: 1px solid #ffe58f;
  border-radius: 6px;
}

.expiring-text {
  flex: 1;
}

.expiring-amount {
  font-size: 14px;
  color: #d48806;
  margin-bottom: 2px;
}

.expiring-date {
  font-size: 12px;
  color: #8c8c8c;
}

.year-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.year-stat {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 14px;
  color: #595959;
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
}

.stat-value.earn {
  color: #52c41a;
}

.stat-value.spend {
  color: #ff4d4f;
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

.records-table {
  width: 100%;
}

.type-icon {
  margin-right: 4px;
}

.points-change {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
  gap: 2px;
}

.points-amount {
  font-size: 16px;
  font-weight: 600;
}

.points-change.earn .points-amount {
  color: #52c41a;
}

.points-change.spend .points-amount {
  color: #ff4d4f;
}

.points-change.expire .points-amount {
  color: #8c8c8c;
}

.points-unit {
  font-size: 12px;
  color: #8c8c8c;
}

.record-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #8c8c8c;
}

.no-action {
  color: #d9d9d9;
  font-size: 12px;
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
  .points-stats {
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
  
  .additional-info {
    grid-template-columns: 1fr;
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .points-stats {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
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
  
  .records-table {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .search-item {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-label {
    min-width: auto;
  }
  
  .year-stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  :deep(.el-table__body-wrapper) {
    overflow-x: auto;
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

/* 统计组件样式优化 */
:deep(.el-statistic) {
  --el-statistic-content-font-size: 24px;
  --el-statistic-title-font-size: 14px;
}

:deep(.el-statistic__head) {
  color: #8c8c8c;
  font-weight: 500;
}

:deep(.el-statistic__content) {
  color: #262626;
}

/* 进度条样式优化 */
:deep(.el-progress-bar__outer) {
  background-color: #f5f5f5;
}

:deep(.el-progress-bar__inner) {
  border-radius: 4px;
}
</style>