<template>
  <div class="ticket-page">
    <!-- 搜索区域 -->
    <TicketSearch 
      :initial-params="searchParams"
      @search="handleSearch"
    />

    <!-- 热门路线区域 -->
    <div class="hot-routes-section">
      <div class="container">
        <h3 class="section-title">
          <el-icon size="20" color="#ff6b35">
            <Star />
          </el-icon>
          热门路线
        </h3>
        
        <!-- 热门路线列表 -->
        <div v-if="hotRoutes.length > 0" class="hot-routes-grid">
          <HotRouteCard
            v-for="route in hotRoutes"
            :key="route.id"
            :route="route"
            @select="selectHotRoute"
          />
        </div>
        
        <!-- 热门路线较少时的补充内容 -->
        <div v-if="hotRoutes.length < 4" class="additional-content">
          <!-- 快速搜索建议 -->
          <QuickSearchSection @quick-search="fillQuickSearch" />

          <!-- 服务特色 -->
          <FeaturesSection />

          <!-- 购票须知 -->
          <NoticeSection />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import { getHotRoutes } from '@/api/ticket'
import TicketSearch from '@/components/TicketSearch.vue'
import HotRouteCard from './components/HotRouteCard.vue'
import QuickSearchSection from './components/QuickSearchSection.vue'
import FeaturesSection from './components/FeaturesSection.vue'
import NoticeSection from './components/NoticeSection.vue'

const router = useRouter()
const route = useRoute()

// 搜索参数
const searchParams = reactive({
  originStationId: null,
  destinationStationId: null,
  date: ''
})

// 数据
const hotRoutes = ref([])

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
  searchParams.originStationId = route.originStation?.id
  searchParams.destinationStationId = route.destinationStation?.id
  // 触发搜索
  handleSearch({
    originStationId: route.originStation?.id,
    destinationStationId: route.destinationStation?.id,
    date: searchParams.date || new Date().toISOString().split('T')[0]
  })
}

// 快速搜索填充
const fillQuickSearch = ({ origin, destination }) => {
  console.log('快速搜索:', origin, destination)
}

// 搜索车票 - 处理来自搜索组件的搜索事件
const handleSearch = (searchData) => {
  // 跳转到搜索结果页面
  router.push({
    path: 'ticketSearch',
    query: {
      originStationId: searchData.originStationId,
      destinationStationId: searchData.destinationStationId,
      date: searchData.date
    }
  })
}

// 从路由参数初始化搜索条件
const initFromQuery = () => {
  const query = route.query
  if (query.originStationId) {
    searchParams.originStationId = parseInt(query.originStationId)
  }
  if (query.destinationStationId) {
    searchParams.destinationStationId = parseInt(query.destinationStationId)
  }
  if (query.date) {
    searchParams.date = query.date
  }
}

// 组件挂载时初始化
onMounted(async () => {
  await loadHotRoutes()
  initFromQuery()
})
</script>

<style scoped>
.ticket-page {
  min-height: 100vh;
  background-color: #f8f9fa;
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

/* 补充内容样式 */
.additional-content {
  margin-top: 40px;
}
</style>