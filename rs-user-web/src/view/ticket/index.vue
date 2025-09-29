<template>
  <div class="ticket-page">
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-container">
        <h2 class="search-title">
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
                搜索车次
              </el-button>
            </div>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 热门路线区域 -->
    <div v-if="!hasSearched" class="hot-routes-section">
      <div class="container">
        <h3 class="section-title">
          <el-icon size="20" color="#ff6b35">
            <Star />
          </el-icon>
          热门路线
        </h3>
        
        <!-- 热门路线列表 -->
        <div v-if="hotRoutes.length > 0" class="hot-routes-grid">
          <div 
            v-for="route in hotRoutes" 
            :key="route.id"
            class="route-card"
            @click="selectHotRoute(route)"
          >
            <div class="route-info">
              <div class="route-stations">
                <span class="origin">{{ route.originStation?.name || '未知站点' }}</span>
                <el-icon class="arrow-icon"><ArrowRight /></el-icon>
                <span class="destination">{{ route.destinationStation?.name || '未知站点' }}</span>
              </div>
              <div class="route-details">
                <span class="route-name">{{ route.routeName }}</span>
                <span class="min-price">￥{{ route.minPrice }}起</span>
              </div>
              <div class="route-meta">
                <span class="duration">{{ route.avgDuration }}</span>
                <span class="popularity">热度 {{ route.popularity }}%</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 热门路线较少时的补充内容 -->
        <div v-if="hotRoutes.length < 4" class="additional-content">
          <!-- 快速搜索建议 -->
          <div class="quick-search-section">
            <h4 class="subsection-title">
              <el-icon size="18" color="#1890ff">
                <Lightning />
              </el-icon>
              快速搜索
            </h4>
            <div class="quick-search-tips">
              <div class="tip-card" @click="fillQuickSearch('北京', '上海')">
                <div class="tip-route">北京 → 上海</div>
                <div class="tip-desc">京沪高铁主干线</div>
              </div>
              <div class="tip-card" @click="fillQuickSearch('北京', '广州')">
                <div class="tip-route">北京 → 广州</div>
                <div class="tip-desc">京广高铁主干线</div>
              </div>
              <div class="tip-card" @click="fillQuickSearch('上海', '深圳')">
                <div class="tip-route">上海 → 深圳</div>
                <div class="tip-desc">沪深高速通道</div>
              </div>
              <div class="tip-card" @click="fillQuickSearch('杭州', '北京')">
                <div class="tip-route">杭州 → 北京</div>
                <div class="tip-desc">京杭快速直达</div>
              </div>
            </div>
          </div>

          <!-- 服务特色 -->
          <div class="features-section">
            <h4 class="subsection-title">
              <el-icon size="18" color="#52c41a">
                <Trophy />
              </el-icon>
              服务特色
            </h4>
            <div class="features-grid">
              <div class="feature-item">
                <el-icon size="24" color="#1890ff"><Clock /></el-icon>
                <div class="feature-content">
                  <div class="feature-title">准点率高</div>
                  <div class="feature-desc">高铁准点率达98%以上</div>
                </div>
              </div>
              <div class="feature-item">
                <el-icon size="24" color="#52c41a"><ChromeFilled /></el-icon>
                <div class="feature-content">
                  <div class="feature-title">安全保障</div>
                  <div class="feature-desc">全程安全监控保障</div>
                </div>
              </div>
              <div class="feature-item">
                <el-icon size="24" color="#ff6b35"><Service /></el-icon>
                <div class="feature-content">
                  <div class="feature-title">贴心服务</div>
                  <div class="feature-desc">24小时客服支持</div>
                </div>
              </div>
              <div class="feature-item">
                <el-icon size="24" color="#722ed1"><Cellphone /></el-icon>
                <div class="feature-content">
                  <div class="feature-title">便捷购票</div>
                  <div class="feature-desc">手机APP随时购票</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 购票须知 -->
          <div class="notice-section">
            <h4 class="subsection-title">
              <el-icon size="18" color="#fa8c16">
                <InfoFilled />
              </el-icon>
              购票须知
            </h4>
            <div class="notice-content">
              <div class="notice-item">
                <el-icon size="16" color="#1890ff"><Check /></el-icon>
                <span>请提前30分钟到达车站，预留充足时间进站</span>
              </div>
              <div class="notice-item">
                <el-icon size="16" color="#1890ff"><Check /></el-icon>
                <span>购票时请确认身份证信息准确无误</span>
              </div>
              <div class="notice-item">
                <el-icon size="16" color="#1890ff"><Check /></el-icon>
                <span>儿童票需要在成人陪同下购买和使用</span>
              </div>
              <div class="notice-item">
                <el-icon size="16" color="#1890ff"><Check /></el-icon>
                <span>退改签请按照铁路部门相关规定执行</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索结果区域 -->
    <div v-if="hasSearched" class="results-section">
      <div class="container">
        <div class="results-header">
          <h3 class="results-title">
            搜索结果 
            <span v-if="searchResults.total > 0" class="results-count">
              (共{{ searchResults.total }}个车次)
            </span>
          </h3>
          <div class="sort-options">
            <el-radio-group v-model="sortType" size="small" @change="handleSort">
              <el-radio-button label="time">出发时间</el-radio-button>
              <el-radio-button label="duration">历时最短</el-radio-button>
              <el-radio-button label="price">价格最低</el-radio-button>
            </el-radio-group>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="searchLoading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>

        <!-- 无结果 -->
        <div v-else-if="searchResults.tickets.length === 0 && hasSearched" class="no-results">
          <el-empty description="暂无符合条件的车次" />
        </div>

        <!-- 车票列表 -->
        <div v-else class="ticket-list">
          <div 
            v-for="ticket in searchResults.tickets" 
            :key="ticket.trainId"
            class="ticket-card"
          >
            <div class="train-info">
              <div class="train-number">{{ ticket.trainCode }}</div>
              <div class="train-type">{{ ticket.trainType }}</div>
            </div>

            <div class="time-info">
              <div class="departure">
                <div class="time">{{ ticket.startTime }}</div>
                <div class="station">{{ ticket.originStation?.name || '未知站点' }}</div>
              </div>
              <div class="duration">
                <div class="route-line">
                  <div class="start-dot"></div>
                  <div class="dashed-line"></div>
                  <div class="end-dot"></div>
                </div>
                <div class="duration-text">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatDuration(ticket.duration) }}</span>
                </div>
              </div>
              <div class="arrival">
                <div class="time">{{ ticket.endTime }}</div>
                <div class="station">{{ ticket.destinationStation?.name || '未知站点' }}</div>
              </div>
            </div>

            <div class="seat-info">
              <div 
                v-for="seatType in ticket.seatTypes" 
                :key="seatType.type"
                class="seat-type"
                :class="{ 'sold-out': seatType.remainingSeats === 0 }"
              >
                <div class="seat-name">{{ seatType.name }}</div>
                <div class="seat-price">￥{{ seatType.price }}</div>
                <div class="seat-count">
                  {{ seatType.remainingSeats > 0 ? `余${seatType.remainingSeats}` : '无票' }}
                </div>
              </div>
            </div>

            <div class="actions">
              <el-button 
                type="primary" 
                @click="viewTicketDetail(ticket)"
                :disabled="!hasAvailableSeats(ticket)"
              >
                {{ hasAvailableSeats(ticket) ? '预订' : '无票' }}
              </el-button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="searchResults.total > 0" class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="searchResults.total"
            layout="prev, pager, next, jumper"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Tickets, Switch, Search, Star, ArrowRight, Clock, Lightning, Trophy, ChromeFilled, Service, Cellphone, InfoFilled, Check } from '@element-plus/icons-vue'
import { getStations } from '@/api/station'
import { searchTickets, getHotRoutes } from '@/api/ticket'

const router = useRouter()
const route = useRoute()

// 表单引用
const searchFormRef = ref()
const searchLoading = ref(false)
const hasSearched = ref(false)

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
const hotRoutes = ref([])
const searchResults = reactive({
  tickets: [],
  total: 0,
  page: 1,
  pageSize: 10
})

// 分页和排序
const currentPage = ref(1)
const pageSize = ref(10)
const sortType = ref('time')

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

// 获取热门路线
const loadHotRoutes = async () => {
  try {
    const response = await getHotRoutes()
    if (response.code === 200) {
      hotRoutes.value = response.data
    } else {
      console.error('获取热门路线失败:', response.message)
    }
  } catch (error) {
    console.error('获取热门路线失败:', error)
  }
}

// 选择热门路线
const selectHotRoute = (route) => {
  searchForm.originStationId = route.originStation.id
  searchForm.destinationStationId = route.destinationStation.id
  handleSearch()
}

// 格式化ISO 8601持续时间为友好显示
const formatDuration = (isoDuration) => {
  if (!isoDuration) return '计算中'
    
  // 解析ISO 8601格式，支持负数时间，如 PT-24H 或 PT2H30M 或 PT1H30M
  const match = isoDuration.match(/^PT(-?\d+(?:\.\d+)?H)?(-?\d+(?:\.\d+)?M)?(-?\d+(?:\.\d+)?S)?$/)
  if (!match) {
    console.log('无法匹配ISO 8601格式:', isoDuration)
    return isoDuration
  }
  
  // 提取数值，去掉单位字母
  const hoursStr = match[1] ? match[1].replace('H', '') : '0'
  const minutesStr = match[2] ? match[2].replace('M', '') : '0'
  const secondsStr = match[3] ? match[3].replace('S', '') : '0'
  
  const hours = Math.abs(parseInt(hoursStr))
  const minutes = Math.abs(parseInt(minutesStr))
  const seconds = Math.abs(parseFloat(secondsStr))
    
  if (hours > 0 && minutes > 0) {
    return `${hours}小时${minutes}分钟`
  } else if (hours > 0) {
    return `${hours}小时`
  } else if (minutes > 0) {
    return `${minutes}分钟`
  } else if (seconds > 0) {
    return `${Math.round(seconds)}秒`
  } else {
    return '计算中'
  }
}

// 快速搜索填充
const fillQuickSearch = (origin, destination) => {
  const originStation = stations.value.find(station => station.name === origin)
  const destinationStation = stations.value.find(station => station.name === destination)
  
  if (originStation && destinationStation) {
    searchForm.originStationId = originStation.id
    searchForm.destinationStationId = destinationStation.id
  }
  // 可以选择是否立即搜索
  // handleSearch()
}

// 搜索车票
const handleSearch = async () => {
  try {
    const valid = await searchFormRef.value.validate()
    if (valid) {
      searchLoading.value = true
      hasSearched.value = true
      
      const response = await searchTickets({
        originStationId: searchForm.originStationId,
        destinationStationId: searchForm.destinationStationId,
        date: searchForm.date,
        pageNum: currentPage.value,
        pageSize: pageSize.value
      })
      
      if (response.code === 200) {
        // 根据新的分页响应结构适配数据
        const data = response.data
        searchResults.tickets = data.records.map(ticket => ({
          trainId: ticket.trainId,
          trainCode: ticket.trainName, // 使用trainName作为车次号
          trainType: ticket.trainNumber, // 使用trainNumber作为车型
          trainName: ticket.trainName,
          originStation: ticket.originStation,
          destinationStation: ticket.destinationStation,
          startTime: new Date(ticket.startTime).toLocaleTimeString('zh-CN', { 
            hour: '2-digit', 
            minute: '2-digit' 
          }),
          endTime: new Date(ticket.endTime).toLocaleTimeString('zh-CN', { 
            hour: '2-digit', 
            minute: '2-digit' 
          }),
          duration: ticket.duration || '计算中',
          seatTypes: ticket.seatTypes.map(seat => ({
            type: seat.type,
            name: seat.name,
            price: seat.price,
            remainingSeats: seat.remainingSeats
          }))
        }))
        // 使用新的分页结构字段
        searchResults.total = data.total
        searchResults.page = data.current
        searchResults.pageSize = data.size
        currentPage.value = data.current
      } else {
        ElMessage.error(response.message || '搜索失败')
      }
    }
  } catch (error) {
    console.error('搜索车票失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    searchLoading.value = false
  }
}

// 排序处理
const handleSort = () => {
  // 这里可以实现客户端排序或重新请求服务器排序
  console.log('排序类型:', sortType.value)
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  // 重新搜索当前页数据
  handleSearch()
}

// 检查是否有可用座位
const hasAvailableSeats = (ticket) => {
  return ticket.seatTypes.some(seat => seat.remainingSeats > 0)
}

// 查看车票详情
const viewTicketDetail = (ticket) => {
  router.push({
    path: '/ticket-detail',
    query: {
      trainId: ticket.trainId,
      date: searchForm.date,
      originStationId: searchForm.originStationId,
      destinationStationId: searchForm.destinationStationId
    }
  })
}

// 从路由参数初始化搜索条件
const initFromQuery = () => {
  const query = route.query
  if (query.originStationId) {
    searchForm.originStationId = parseInt(query.originStationId)
  }
  if (query.destinationStationId) {
    searchForm.destinationStationId = parseInt(query.destinationStationId)
  }
  if (query.date) {
    searchForm.date = query.date
  }
  
  // 如果有搜索参数，自动执行搜索
  if (query.originStationId && query.destinationStationId && query.date) {
    setTimeout(() => {
      handleSearch()
    }, 500)
  }
}

// 组件挂载时初始化
onMounted(async () => {
  initDefaultDate()
  await loadStations()
  await loadHotRoutes()
  initFromQuery()
})
</script>

<style scoped>
.ticket-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

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

.hot-routes-section {
  padding: 40px 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.hot-routes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.route-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.route-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.route-stations {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 18px;
  font-weight: 600;
}

.origin, .destination {
  color: #333;
}

.arrow-icon {
  color: #1890ff;
}

.route-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.route-name {
  color: #666;
  font-size: 14px;
}

.min-price {
  color: #ff6b35;
  font-weight: 600;
  font-size: 16px;
}

.route-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

.results-section {
  padding: 20px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.results-title {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.results-count {
  color: #666;
  font-weight: normal;
}

.loading-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.no-results {
  background: white;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
}

/* 补充内容样式 */
.additional-content {
  margin-top: 40px;
}

.subsection-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

/* 快速搜索样式 */
.quick-search-section {
  margin-bottom: 40px;
}

.quick-search-tips {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.tip-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.tip-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.tip-route {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.tip-desc {
  font-size: 14px;
  opacity: 0.9;
}

/* 服务特色样式 */
.features-section {
  margin-bottom: 40px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.feature-content {
  flex: 1;
}

.feature-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.feature-desc {
  font-size: 14px;
  color: #666;
}

/* 购票须知样式 */
.notice-section {
  margin-bottom: 20px;
}

.notice-content {
  background: #f8f9fa;
  padding: 24px;
  border-radius: 12px;
  border-left: 4px solid #1890ff;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #555;
}

.notice-item:last-child {
  margin-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .quick-search-tips {
    grid-template-columns: 1fr;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .tip-card {
    padding: 16px;
  }
  
  .feature-item {
    padding: 20px;
  }
}

.no-results {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.no-results .el-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.no-results-text {
  font-size: 16px;
  margin-bottom: 8px;
}

.no-results-tip {
  font-size: 14px;
  color: #ccc;
}

.ticket-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ticket-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: grid;
  grid-template-columns: 120px 1fr 300px 100px;
  gap: 20px;
  align-items: center;
}

.train-info {
  text-align: center;
}

.train-number {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 4px;
}

.train-type {
  font-size: 12px;
  color: #666;
}

.time-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.departure, .arrival {
  text-align: center;
}

.time {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.station {
  font-size: 14px;
  color: #666;
}

.duration {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  position: relative;
}

.route-line {
  display: flex;
  align-items: center;
  width: 120px;
  height: 20px;
  position: relative;
}

.start-dot, .end-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #1890ff;
  z-index: 2;
}

.dashed-line {
  flex: 1;
  height: 2px;
  background-image: linear-gradient(to right, #1890ff 50%, transparent 50%);
  background-size: 8px 2px;
  background-repeat: repeat-x;
  margin: 0 4px;
  position: relative;
}

.dashed-line::after {
  content: '→';
  position: absolute;
  right: -2px;
  top: -8px;
  color: #1890ff;
  font-size: 12px;
  font-weight: bold;
}

.duration-text {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 12px;
}

.seat-info {
  display: flex;
  gap: 12px;
}

.seat-type {
  text-align: center;
  padding: 8px 12px;
  border-radius: 6px;
  background-color: #f8f9fa;
  min-width: 80px;
}

.seat-type.sold-out {
  background-color: #f5f5f5;
  color: #ccc;
}

.seat-name {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.seat-price {
  font-size: 16px;
  font-weight: 600;
  color: #ff6b35;
  margin-bottom: 2px;
}

.seat-count {
  font-size: 12px;
  color: #999;
}

.actions {
  text-align: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
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
  
  .ticket-card {
    grid-template-columns: 1fr;
    gap: 16px;
    text-align: center;
  }
  
  .time-info {
    flex-direction: column;
    gap: 10px;
  }
  
  .seat-info {
    justify-content: center;
  }
}
</style>