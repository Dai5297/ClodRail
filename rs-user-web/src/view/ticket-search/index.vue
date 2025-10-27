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
          <TicketCard
            v-for="ticket in searchResults.tickets"
            :key="ticket.trainId"
            :ticket="ticket"
            @view-detail="viewTicketDetail"
          />
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
import { searchTickets } from '@/api/ticket'
import TicketSearch from '@/components/TicketSearch.vue'
import TicketCard from './components/TicketCard.vue'

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

.pagination-container {
  padding: 20px;
  text-align: center;
  border-top: 1px solid #f0f0f0;
}


</style>