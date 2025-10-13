<template>
  <div class="ticket-search-page">
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="container">
        <TicketSearch 
          :initial-params="searchParams"
          @search="handleSearch"
          search-button-text="搜索车次"
        />
      </div>
    </div>

    <!-- 搜索结果区域 -->
    <div class="results-section">
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
        <div v-else-if="searchResults.tickets.length === 0" class="no-results">
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
import { Clock } from '@element-plus/icons-vue'
import { searchTickets } from '@/api/ticket'
import TicketSearch from '@/components/TicketSearch.vue'

const router = useRouter()
const route = useRoute()

// 搜索参数
const searchParams = reactive({
  originStationId: route.query.originStationId ? parseInt(route.query.originStationId) : null,
  destinationStationId: route.query.destinationStationId ? parseInt(route.query.destinationStationId) : null,
  date: route.query.date || ''
})

// 搜索状态
const searchLoading = ref(false)

// 搜索结果数据
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

// 搜索车票
const handleSearch = async (searchData) => {
  try {
    // 如果有新的搜索数据，更新路由参数
    if (searchData) {
      await router.push({
        path: '/ticketSearch',
        query: {
          originStationId: searchData.originStationId,
          destinationStationId: searchData.destinationStationId,
          date: searchData.date
        }
      })
      // 更新本地搜索参数
      Object.assign(searchParams, searchData)
    }

    const query = searchData || route.query
    if (!query.originStationId || !query.destinationStationId || !query.date) {
      ElMessage.error('搜索参数不完整')
      return
    }

    searchLoading.value = true
    
    const response = await searchTickets({
      originStationId: parseInt(query.originStationId),
      destinationStationId: parseInt(query.destinationStationId),
      date: query.date,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (response.code === 200) {
      // 根据新的分页响应结构适配数据
      const data = response.data
      searchResults.tickets = data.records.map(ticket => ({
        id: ticket.id,
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
    name: 'ticket-detail',
    query: {
      ticketId: ticket.id,
    }
  })
}

// 组件挂载时执行搜索
onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.ticket-search-page {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-section {
  margin-bottom: 20px;
}

.results-section {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.results-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.results-count {
  color: #666;
  font-weight: normal;
  font-size: 14px;
}

.sort-options {
  display: flex;
  align-items: center;
}

.loading-container {
  padding: 40px 20px;
}

.no-results {
  padding: 60px 20px;
  text-align: center;
}

.ticket-list {
  padding: 0;
}

.ticket-card {
  display: grid;
  grid-template-columns: 120px 1fr 300px 100px;
  gap: 20px;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.ticket-card:hover {
  background-color: #fafafa;
}

.ticket-card:last-child {
  border-bottom: none;
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
  background-color: #f0f0f0;
  padding: 2px 8px;
  border-radius: 12px;
  display: inline-block;
}

.time-info {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 20px;
  align-items: center;
}

.departure, .arrival {
  text-align: center;
}

.departure {
  text-align: left;
}

.arrival {
  text-align: right;
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
}

.route-line {
  display: flex;
  align-items: center;
  width: 100px;
}

.start-dot, .end-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #1890ff;
}

.dashed-line {
  flex: 1;
  height: 2px;
  background: linear-gradient(to right, #1890ff 50%, transparent 50%);
  background-size: 8px 2px;
  margin: 0 4px;
}

.duration-text {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.seat-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.seat-type {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
  font-size: 14px;
}

.seat-type.sold-out {
  background-color: #f5f5f5;
  color: #999;
}

.seat-name {
  font-weight: 500;
}

.seat-price {
  color: #ff4d4f;
  font-weight: 600;
}

.seat-count {
  font-size: 12px;
  color: #52c41a;
}

.seat-type.sold-out .seat-count {
  color: #999;
}

.actions {
  text-align: center;
}

.pagination-container {
  padding: 20px;
  text-align: center;
  border-top: 1px solid #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ticket-card {
    grid-template-columns: 1fr;
    gap: 15px;
    text-align: center;
  }
  
  .time-info {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .departure, .arrival {
    text-align: center;
  }
  
  .seat-info {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .seat-type {
    min-width: 120px;
  }
}
</style>