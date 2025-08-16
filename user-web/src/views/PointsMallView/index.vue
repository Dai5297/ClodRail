<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import MallBanner from './components/MallBanner.vue'
import CategorySidebar from './components/CategorySidebar.vue'
import ProductGrid from './components/ProductGrid.vue'
import ExchangeModal from './components/ExchangeModal.vue'

const route = useRoute()
const router = useRouter()

// 页面状态
const loading = ref(false)
const showExchangeModal = ref(false)
const selectedProduct = ref<any>(null)

// 用户积分
const userPoints = ref(12580)

// 当前选中的分类
const selectedCategory = ref('all')

// 模拟商品数据
const products = ref([
  {
    id: '1',
    name: '高铁优惠券',
    description: '全国高铁通用优惠券，满100减20',
    points: 500,
    originalPrice: 20,
    image: 'https://via.placeholder.com/200x200/2196F3/white?text=Coupon',
    category: 'coupon',
    stock: 100,
    sales: 1234
  },
  {
    id: '2',
    name: '星巴克咖啡券',
    description: '星巴克中杯咖啡兑换券，全国门店通用',
    points: 1200,
    originalPrice: 35,
    image: 'https://via.placeholder.com/200x200/4CAF50/white?text=Coffee',
    category: 'coupon',
    stock: 50,
    sales: 567
  },
  {
    id: '3',
    name: '旅行收纳包',
    description: '多功能旅行收纳包，出差旅游必备',
    points: 800,
    originalPrice: 58,
    image: 'https://via.placeholder.com/200x200/FF9800/white?text=Bag',
    category: 'travel',
    stock: 80,
    sales: 890
  },
  {
    id: '4',
    name: '蓝牙耳机',
    description: '无线蓝牙耳机，高音质立体声',
    points: 1500,
    originalPrice: 128,
    image: 'https://via.placeholder.com/200x200/9C27B0/white?text=Earphone',
    category: 'digital',
    stock: 30,
    sales: 345
  },
  {
    id: '5',
    name: '德芙巧克力礼盒',
    description: '德芙精装巧克力礼盒，节日送礼首选',
    points: 600,
    originalPrice: 45,
    image: 'https://via.placeholder.com/200x200/8BC34A/white?text=Chocolate',
    category: 'food',
    stock: 120,
    sales: 678
  },
  {
    id: '6',
    name: '保温杯',
    description: '304不锈钢保温杯，24小时保温',
    points: 900,
    originalPrice: 68,
    image: 'https://via.placeholder.com/200x200/607D8B/white?text=Cup',
    category: 'life',
    stock: 60,
    sales: 432
  }
])

// 分类配置
const categories = [
  { id: 'all', name: '全部商品', icon: '🛍️' },
  { id: 'coupon', name: '优惠券', icon: '🎫' },
  { id: 'travel', name: '旅行用品', icon: '🧳' },
  { id: 'digital', name: '数码产品', icon: '📱' },
  { id: 'life', name: '生活用品', icon: '🏠' },
  { id: 'food', name: '美食特产', icon: '🍫' }
]

// 过滤后的商品
const filteredProducts = computed(() => {
  if (selectedCategory.value === 'all') {
    return products.value
  }
  return products.value.filter(product => product.category === selectedCategory.value)
})

// 处理分类选择
const handleCategorySelect = (categoryId: string) => {
  selectedCategory.value = categoryId
}

// 处理商品兑换
const handleProductExchange = (product: any) => {
  if (userPoints.value < product.points) {
    ElMessage.warning('积分不足，无法兑换此商品')
    return
  }
  
  selectedProduct.value = product
  showExchangeModal.value = true
}

// 确认兑换
const confirmExchange = () => {
  if (selectedProduct.value) {
    userPoints.value -= selectedProduct.value.points
    ElMessage.success('兑换成功！商品将在3-5个工作日内发货')
    showExchangeModal.value = false
    selectedProduct.value = null
  }
}

// 页面初始化
onMounted(() => {
  // 初始化页面数据
  console.log('PointsMallView initialized')
})
</script>

<template>
  <div class="points-mall-page">
    <div class="container">
      <!-- 积分商城横幅 -->
      <MallBanner :user-points="userPoints" />
      
      <div class="mall-content">
        <!-- 侧边分类菜单 -->
        <div class="sidebar">
          <CategorySidebar 
            :categories="categories"
            :active-category="selectedCategory"
            @category-change="handleCategorySelect"
          />
        </div>
        
        <!-- 商品网格 -->
        <div class="main-content">
          <ProductGrid 
            :products="filteredProducts"
            :active-category="selectedCategory"
            :loading="loading"
            @product-click="handleProductExchange"
            @exchange-click="handleProductExchange"
          />
        </div>
      </div>
    </div>
    
    <!-- 兑换确认模态框 -->
    <ExchangeModal 
      v-model:visible="showExchangeModal"
      :product="selectedProduct"
      :user-points="userPoints"
      @confirm="confirmExchange"
    />
  </div>
</template>

<style scoped>
.points-mall-page {
  min-height: calc(100vh - 60px);
  background: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.mall-content {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 20px;
  margin-top: 20px;
}

.sidebar {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  height: fit-content;
  position: sticky;
  top: 20px;
}

.main-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

@media (max-width: 1024px) {
  .mall-content {
    grid-template-columns: 200px 1fr;
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }
  
  .mall-content {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .sidebar {
    position: static;
  }
}
</style>
