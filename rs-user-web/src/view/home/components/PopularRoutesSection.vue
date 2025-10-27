<template>
  <section class="routes-section">
    <div class="routes-container">
      <h2 class="routes-title">
        <el-icon size="32" color="#ff4d4f">
          <Promotion />
        </el-icon>
        热门路线
      </h2>
      
      <!-- 路线筛选 -->
      <div class="route-filters">
        <el-radio-group v-model="activeFilter" @change="handleFilterChange">
          <el-radio-button label="all">全部路线</el-radio-button>
          <el-radio-button label="high-speed">高铁专线</el-radio-button>
          <el-radio-button label="economic">经济实惠</el-radio-button>
          <el-radio-button label="frequent">班次密集</el-radio-button>
        </el-radio-group>
      </div>

      <div class="routes-grid">
        <div 
          v-for="(route, index) in filteredRoutes" 
          :key="index"
          class="route-card"
          @click="handleRouteClick(route)"
        >
          <div class="route-header">
            <div class="route-cities">
              <span class="departure-city">{{ route.departure }}</span>
              <div class="route-arrow">
                <el-icon size="20" color="#1890ff">
                  <Right />
                </el-icon>
              </div>
              <span class="arrival-city">{{ route.arrival }}</span>
            </div>
            <div class="route-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ route.minPrice }}</span>
              <span class="price-unit">起</span>
            </div>
          </div>

          <div class="route-info">
            <div class="info-item">
              <el-icon size="16" color="#52c41a">
                <Clock />
              </el-icon>
              <span>{{ route.duration }}</span>
            </div>
            <div class="info-item">
              <el-icon size="16" color="#1890ff">
                <Calendar />
              </el-icon>
              <span>每日{{ route.frequency }}班次</span>
            </div>
            <div class="info-item">
              <el-icon size="16" color="#faad14">
                <Star />
              </el-icon>
              <span>{{ route.rating }}分</span>
            </div>
          </div>

          <div class="route-details">
            <div class="train-types">
              <el-tag 
                v-for="trainType in route.trainTypes" 
                :key="trainType"
                size="small"
                :type="getTrainTypeColor(trainType)"
              >
                {{ trainType }}
              </el-tag>
            </div>
            <div class="route-features">
              <span 
                v-for="feature in route.features" 
                :key="feature"
                class="feature-tag"
              >
                {{ feature }}
              </span>
            </div>
          </div>

          <div class="route-actions">
            <el-button 
              type="primary" 
              size="small" 
              @click.stop="handleBookNow(route)"
            >
              立即预订
            </el-button>
            <el-button 
              type="text" 
              size="small" 
              @click.stop="handleViewDetails(route)"
            >
              查看详情
            </el-button>
          </div>

          <!-- 热门标识 -->
          <div v-if="route.isHot" class="hot-badge">
            <el-icon size="12">
              <Star />
            </el-icon>
            热门
          </div>
        </div>
      </div>

      <!-- 查看更多 -->
      <div class="more-routes">
        <el-button type="primary" plain size="large" @click="handleViewMore">
          查看更多路线
          <el-icon class="ml-2">
            <ArrowRight />
          </el-icon>
        </el-button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Promotion, 
  Right, 
  Clock, 
  Calendar, 
  Star,
  ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()

// 筛选状态
const activeFilter = ref('all')

// 路线数据
const routeList = ref([
  {
    id: 1,
    departure: '北京',
    arrival: '上海',
    minPrice: 553,
    duration: '4小时28分',
    frequency: '100+',
    rating: 4.8,
    trainTypes: ['G高铁', 'D动车'],
    features: ['WiFi', '充电插座', '餐车'],
    isHot: true,
    category: ['high-speed', 'frequent']
  },
  {
    id: 2,
    departure: '广州',
    arrival: '深圳',
    minPrice: 79,
    duration: '1小时20分',
    frequency: '200+',
    rating: 4.9,
    trainTypes: ['G高铁', 'C城际'],
    features: ['WiFi', '充电插座'],
    isHot: true,
    category: ['high-speed', 'frequent', 'economic']
  },
  {
    id: 3,
    departure: '成都',
    arrival: '重庆',
    minPrice: 154,
    duration: '1小时13分',
    frequency: '80+',
    rating: 4.7,
    trainTypes: ['G高铁', 'D动车'],
    features: ['WiFi', '充电插座', '餐车'],
    isHot: false,
    category: ['high-speed']
  },
  {
    id: 4,
    departure: '杭州',
    arrival: '南京',
    minPrice: 119,
    duration: '1小时17分',
    frequency: '60+',
    rating: 4.6,
    trainTypes: ['G高铁', 'D动车'],
    features: ['WiFi', '充电插座'],
    isHot: false,
    category: ['high-speed', 'economic']
  },
  {
    id: 5,
    departure: '西安',
    arrival: '郑州',
    minPrice: 174,
    duration: '1小时30分',
    frequency: '40+',
    rating: 4.5,
    trainTypes: ['G高铁', 'D动车'],
    features: ['WiFi', '餐车'],
    isHot: false,
    category: ['high-speed']
  },
  {
    id: 6,
    departure: '武汉',
    arrival: '长沙',
    minPrice: 152,
    duration: '1小时20分',
    frequency: '50+',
    rating: 4.7,
    trainTypes: ['G高铁', 'D动车'],
    features: ['WiFi', '充电插座'],
    isHot: false,
    category: ['high-speed']
  },
  {
    id: 7,
    departure: '天津',
    arrival: '济南',
    minPrice: 98,
    duration: '2小时15分',
    frequency: '30+',
    rating: 4.4,
    trainTypes: ['G高铁', 'K快速'],
    features: ['WiFi'],
    isHot: false,
    category: ['economic']
  },
  {
    id: 8,
    departure: '南京',
    arrival: '合肥',
    minPrice: 89,
    duration: '1小时45分',
    frequency: '35+',
    rating: 4.3,
    trainTypes: ['G高铁', 'D动车'],
    features: ['WiFi', '充电插座'],
    isHot: false,
    category: ['economic']
  }
])

// 筛选后的路线
const filteredRoutes = computed(() => {
  if (activeFilter.value === 'all') {
    return routeList.value
  }
  return routeList.value.filter(route => 
    route.category.includes(activeFilter.value)
  )
})

// 获取列车类型颜色
const getTrainTypeColor = (trainType) => {
  const colorMap = {
    'G高铁': 'success',
    'D动车': 'primary',
    'C城际': 'warning',
    'K快速': 'info'
  }
  return colorMap[trainType] || 'default'
}

// 处理筛选变化
const handleFilterChange = (value) => {
  activeFilter.value = value
}

// 处理路线点击
const handleRouteClick = (route) => {
  // 跳转到车票搜索页面，预填出发地和目的地
  router.push({
    path: '/tickets',
    query: {
      from: route.departure,
      to: route.arrival
    }
  })
}

// 处理立即预订
const handleBookNow = (route) => {
  // 跳转到预订页面
  router.push({
    path: '/booking',
    query: {
      from: route.departure,
      to: route.arrival,
      routeId: route.id
    }
  })
}

// 处理查看详情
const handleViewDetails = (route) => {
  // 跳转到路线详情页面
  router.push(`/ticket/${route.id}`)
}

// 处理查看更多
const handleViewMore = () => {
  // 跳转到所有路线页面
  router.push('/ticket')
}
</script>

<style scoped>
.routes-section {
  padding: 80px 20px;
  background-color: white;
  position: relative;
}

.routes-container {
  max-width: 1200px;
  margin: 0 auto;
}

.routes-title {
  text-align: center;
  font-size: 36px;
  margin-bottom: 40px;
  color: #2c3e50;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  font-weight: 700;
}

.route-filters {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}

.route-filters :deep(.el-radio-group) {
  background-color: #f8f9fa;
  border-radius: 25px;
  padding: 4px;
}

.route-filters :deep(.el-radio-button__inner) {
  border: none;
  background-color: transparent;
  border-radius: 20px;
  padding: 8px 20px;
  transition: all 0.3s ease;
}

.route-filters :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: #1890ff;
  color: white;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.routes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 25px;
  margin-bottom: 50px;
}

.route-card {
  border: 2px solid #f0f0f0;
  border-radius: 16px;
  padding: 25px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  background: white;
  overflow: hidden;
}

.route-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(24, 144, 255, 0.1), transparent);
  transition: left 0.5s;
}

.route-card:hover::before {
  left: 100%;
}

.route-card:hover {
  border-color: #1890ff;
  box-shadow: 0 8px 25px rgba(24, 144, 255, 0.15);
  transform: translateY(-2px);
}

.route-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.route-cities {
  display: flex;
  align-items: center;
  gap: 15px;
}

.departure-city,
.arrival-city {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
}

.route-arrow {
  display: flex;
  align-items: center;
}

.route-price {
  display: flex;
  align-items: baseline;
  color: #ff4d4f;
  font-weight: bold;
}

.price-symbol {
  font-size: 16px;
}

.price-value {
  font-size: 24px;
  margin: 0 2px;
}

.price-unit {
  font-size: 14px;
}

.route-info {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
  font-size: 14px;
}

.route-details {
  margin-bottom: 20px;
}

.train-types {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.route-features {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.feature-tag {
  background-color: #f0f8ff;
  color: #1890ff;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  border: 1px solid #d6e4ff;
}

.route-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.hot-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: linear-gradient(45deg, #ff6b6b, #ff8e8e);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 3px;
  font-weight: 500;
}

.more-routes {
  text-align: center;
}

.more-routes .el-button {
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .routes-section {
    padding: 60px 15px;
  }
  
  .routes-title {
    font-size: 28px;
    margin-bottom: 30px;
  }
  
  .route-filters {
    margin-bottom: 30px;
  }
  
  .route-filters :deep(.el-radio-group) {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .routes-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .route-card {
    padding: 20px;
  }
  
  .route-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .route-cities {
    gap: 10px;
  }
  
  .departure-city,
  .arrival-city {
    font-size: 18px;
  }
  
  .route-info {
    gap: 15px;
  }
  
  .route-actions {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .routes-title {
    font-size: 24px;
    flex-direction: column;
    gap: 10px;
  }
  
  .route-card {
    padding: 15px;
  }
  
  .route-cities {
    flex-direction: column;
    gap: 5px;
    text-align: center;
  }
  
  .route-arrow {
    transform: rotate(90deg);
  }
  
  .route-actions {
    flex-direction: column;
  }
}
</style>