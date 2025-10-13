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
                <span class="duration">{{ formatDuration(route.avgDuration) }}</span>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, ArrowRight, Clock, Lightning, Trophy, ChromeFilled, Service, Cellphone, InfoFilled, Check } from '@element-plus/icons-vue'
import { getHotRoutes } from '@/api/ticket'
import TicketSearch from '@/components/TicketSearch.vue'

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
  // 这里需要根据站点名称查找ID，但由于我们已经将站点逻辑移到组件内部
  // 可以通过事件或其他方式来处理
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
</style>