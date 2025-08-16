<script setup lang="ts">
// HomeView - 首页主文件
import { ref, onMounted, withDefaults } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserInfo } from '@/api/auth'

// 导入子组件
import SearchSection from './components/SearchSection.vue'
import PopularRoutes from './components/PopularRoutes.vue'
import FeaturesSection from './components/FeaturesSection.vue'

// 定义搜索表单接口
interface SearchForm {
  departure: string
  destination: string
  date: string
  passengers: number
}

// 定义热门线路接口
interface PopularRoute {
  name: string
  price: string
}

// 定义用户信息接口
interface UserInfo {
  name: string
  points: number
}

const router = useRouter()

// 页面状态
const loading = ref(false)

// 表单数据
const searchForm = ref<SearchForm>({
  departure: '北京',
  destination: '上海',
  date: '',
  passengers: 1
})

// 热门线路
const popularRoutes = ref<PopularRoute[]>([
  { name: '北京 → 上海', price: '¥553起' },
  { name: '广州 → 深圳', price: '¥75起' },
  { name: '成都 → 重庆', price: '¥154起' },
  { name: '杭州 → 南京', price: '¥267起' }
])

// 用户信息
const userInfo = ref<UserInfo>({
  name: '张三',
  points: 1250
})

// 初始化页面
onMounted(() => {
  initializePage()
})

// 初始化页面数据
const initializePage = () => {
  // 设置默认日期为今天
  const today = new Date()
  searchForm.value.date = today.toISOString().split('T')[0]
  
  // 加载用户信息
  loadUserInfo()
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    loading.value = true
    const response = await getUserInfo()
    userInfo.value = response.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = (form: SearchForm) => {
  if (!form.departure || !form.destination) {
    ElMessage.warning('请选择出发地和目的地')
    return
  }
  if (!form.date) {
    ElMessage.warning('请选择出发日期')
    return
  }
  if (form.departure === form.destination) {
    ElMessage.warning('出发地和目的地不能相同')
    return
  }
  
  // 跳转到搜索页面
  router.push({
    name: 'train-search',
    query: {
      departure: form.departure,
      arrival: form.destination,
      date: form.date,
      passengers: form.passengers.toString()
    }
  })
}

// 查看热门线路
const handleRouteView = (route: PopularRoute) => {
  const [departure, arrival] = route.name.split(' → ')
  router.push({
    name: 'train-search',
    query: {
      departure,
      arrival,
      date: searchForm.value.date
    }
  })
}

// 更新搜索表单
const updateSearchForm = (form: SearchForm) => {
  searchForm.value = { ...form }
}
</script>

<template>
  <div class="home-page">
    <!-- 主要内容 -->
    <main class="main-content">
      <!-- 搜索区域 -->
      <SearchSection
        v-model="searchForm"
        :loading="loading"
        @search="handleSearch"
        @update="updateSearchForm"
      />
      
      <!-- 热门线路推荐 -->
      <PopularRoutes
        :routes="popularRoutes"
        :loading="loading"
        @view-route="handleRouteView"
      />
      
      <!-- 服务特色 -->
      <FeaturesSection />
    </main>
  </div>
</template>

<style scoped>
/* 首页样式 */
.home-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  width: 100%;
}

/* 主要内容区域 */
.main-content {
  min-height: calc(100vh - 120px);
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home-page {
    padding: 0;
  }
}
</style>