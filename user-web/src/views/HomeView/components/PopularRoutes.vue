<script setup lang="ts">
import { ref, computed, withDefaults } from 'vue'
import { 
  ElButton, 
  ElSkeleton, 
  ElIcon,
  ElTag,
  ElTooltip
} from 'element-plus'
import { 
  ArrowRight, 
  TrendCharts, 
  Timer,
  Star
} from '@element-plus/icons-vue'

// 定义热门线路接口
interface PopularRoute {
  name: string
  price: string
  duration?: string
  distance?: string
  popularity?: number
  discount?: string
}

// 定义属性
interface Props {
  routes: PopularRoute[]
  loading?: boolean
}

// 定义事件
interface Emits {
  'view-route': [route: PopularRoute]
}

const props = withDefaults(defineProps<Props>(), {
  routes: () => [],
  loading: false
})

const emit = defineEmits<Emits>()

// 扩展路线数据（添加更多信息）
const enhancedRoutes = computed(() => {
  const routeDetails = {
    '北京 → 上海': { duration: '4.5小时', distance: '1318km', popularity: 95, discount: '9折' },
    '广州 → 深圳': { duration: '1小时', distance: '140km', popularity: 88, discount: '8.5折' },
    '成都 → 重庆': { duration: '2小时', distance: '308km', popularity: 82, discount: '9.2折' },
    '杭州 → 南京': { duration: '3小时', distance: '270km', popularity: 76, discount: '9折' }
  }
  
  return props.routes.map(route => ({
    ...route,
    ...routeDetails[route.name as keyof typeof routeDetails]
  }))
})

// 获取热度等级
const getPopularityLevel = (popularity: number) => {
  if (popularity >= 90) return { text: '超热门', color: '#ff4d4f' }
  if (popularity >= 80) return { text: '热门', color: '#faad14' }
  if (popularity >= 70) return { text: '推荐', color: '#52c41a' }
  return { text: '普通', color: '#8c8c8c' }
}

// 处理路线查看
const handleViewRoute = (route: PopularRoute) => {
  emit('view-route', route)
}

// 获取价格数字
const getPriceNumber = (price: string) => {
  return price.replace(/[^\d]/g, '')
}

// 获取路线图标
const getRouteIcon = (routeName: string) => {
  if (routeName.includes('北京') || routeName.includes('上海')) return '🚄'
  if (routeName.includes('广州') || routeName.includes('深圳')) return '🚅'
  if (routeName.includes('成都') || routeName.includes('重庆')) return '🚆'
  return '🚈'
}
</script>

<template>
  <section class="popular-routes">
    <div class="container">
      <!-- 标题区域 -->
      <div class="section-header">
        <div class="title-wrapper">
          <ElIcon :size="24" color="#1890ff">
            <TrendCharts />
          </ElIcon>
          <h2 class="section-title">热门线路推荐</h2>
        </div>
        <p class="section-subtitle">精选热门出行路线，优惠价格抢先订</p>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-section">
        <ElSkeleton animated>
          <template #template>
            <div class="skeleton-grid">
              <div v-for="i in 4" :key="i" class="skeleton-card">
                <el-skeleton-item variant="rect" style="height: 120px; border-radius: 8px;" />
              </div>
            </div>
          </template>
        </ElSkeleton>
      </div>
      
      <!-- 路线网格 -->
      <div v-else class="routes-grid">
        <div 
          v-for="(route, index) in enhancedRoutes" 
          :key="route.name" 
          class="route-card"
          :class="`route-card-${index % 4}`"
          @click="handleViewRoute(route)"
        >
          <!-- 卡片头部 -->
          <div class="card-header">
            <div class="route-icon">{{ getRouteIcon(route.name) }}</div>
            <div class="popularity-badge" v-if="route.popularity">
              <ElTag 
                :color="getPopularityLevel(route.popularity).color"
                size="small"
                class="popularity-tag"
              >
                <ElIcon :size="12"><Star /></ElIcon>
                {{ getPopularityLevel(route.popularity).text }}
              </ElTag>
            </div>
          </div>
          
          <!-- 路线信息 -->
          <div class="route-info">
            <h3 class="route-name">{{ route.name }}</h3>
            
            <!-- 详细信息 -->
            <div class="route-details">
              <div v-if="route.duration" class="detail-item">
                <ElIcon :size="12" color="#8c8c8c">
                  <Timer />
                </ElIcon>
                <span>{{ route.duration }}</span>
              </div>
              <div v-if="route.distance" class="detail-item">
                <span class="distance-text">{{ route.distance }}</span>
              </div>
            </div>
            
            <!-- 价格信息 -->
            <div class="price-info">
              <div class="price-main">
                <span class="price-value">{{ route.price }}</span>
                <span v-if="route.discount" class="discount-badge">{{ route.discount }}</span>
              </div>
              <div class="price-desc">最低价格</div>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="card-action">
            <ElButton 
              type="primary" 
              plain 
              size="small"
              class="view-btn"
            >
              查看详情
              <ElIcon><ArrowRight /></ElIcon>
            </ElButton>
          </div>
          
          <!-- 悬浮效果装饰 -->
          <div class="card-decoration"></div>
        </div>
      </div>
      
      <!-- 更多路线提示 -->
      <div class="more-routes">
        <p class="more-text">更多热门路线，搜索发现</p>
        <div class="route-tags">
          <ElTag v-for="tag in ['京沪高铁', '广深城际', '成渝高铁', '沪杭高铁']" :key="tag" class="route-tag">
            {{ tag }}
          </ElTag>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* 热门线路推荐 */
.popular-routes {
  padding: 48px 0;
  background: #fff;
  position: relative;
}

.container {
  width: 100%;
  margin: 0;
  padding: 0 24px;
}

/* 标题区域 */
.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.title-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #262626;
  margin: 0;
}

.section-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

/* 加载状态 */
.loading-section {
  padding: 20px 0;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.skeleton-card {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
}

/* 路线网格 */
.routes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.route-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  padding: 20px;
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.route-card:hover {
  border-color: #1890FF;
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.15);
  transform: translateY(-4px);
}

.route-card:hover .card-decoration {
  opacity: 1;
  transform: scale(1);
}

/* 卡片装饰 */
.card-decoration {
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100px;
  height: 100px;
  background: linear-gradient(45deg, rgba(24, 144, 255, 0.1), rgba(24, 144, 255, 0.05));
  border-radius: 50%;
  opacity: 0;
  transform: scale(0.5);
  transition: all 0.3s ease;
  pointer-events: none;
}

/* 不同卡片的主题色 */
.route-card-0:hover { border-color: #1890ff; }
.route-card-1:hover { border-color: #52c41a; }
.route-card-2:hover { border-color: #faad14; }
.route-card-3:hover { border-color: #722ed1; }

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.route-icon {
  font-size: 32px;
  line-height: 1;
}

.popularity-badge {
  position: relative;
}

.popularity-tag {
  border: none;
  color: white;
  font-size: 10px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 2px;
}

/* 路线信息 */
.route-info {
  flex: 1;
  margin-bottom: 16px;
}

.route-name {
  font-size: 18px;
  font-weight: bold;
  color: #262626;
  margin: 0 0 12px 0;
  line-height: 1.3;
}

.route-details {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #8c8c8c;
}

.distance-text {
  font-size: 12px;
  color: #8c8c8c;
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 4px;
}

/* 价格信息 */
.price-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.price-main {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-value {
  font-size: 20px;
  font-weight: bold;
  color: #ff4d4f;
}

.discount-badge {
  background: #fff2f0;
  color: #ff4d4f;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}

.price-desc {
  font-size: 11px;
  color: #8c8c8c;
}

/* 操作按钮 */
.card-action {
  display: flex;
  justify-content: flex-end;
}

.view-btn {
  border-radius: 6px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s ease;
}

.view-btn:hover {
  transform: translateX(2px);
}

/* 更多路线 */
.more-routes {
  text-align: center;
  padding: 24px;
  background: #fafafa;
  border-radius: 8px;
}

.more-text {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0 0 16px 0;
}

.route-tags {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.route-tag {
  background: #f0f9ff;
  color: #1890ff;
  border: 1px solid #d6f7ff;
  cursor: pointer;
  transition: all 0.3s ease;
}

.route-tag:hover {
  background: #1890ff;
  color: white;
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .container {
    padding: 0 16px;
  }
  
  .routes-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .popular-routes {
    padding: 32px 0;
  }
  
  .routes-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .route-card {
    padding: 16px;
  }
  
  .section-title {
    font-size: 20px;
  }
  
  .title-wrapper {
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 12px;
  }
  
  .route-card {
    padding: 12px;
  }
  
  .route-details {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .route-tags {
    gap: 6px;
  }
  
  .route-tag {
    font-size: 11px;
    padding: 4px 8px;
  }
}
</style>