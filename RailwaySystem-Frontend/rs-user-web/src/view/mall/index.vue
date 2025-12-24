<template>
  <div class="min-h-screen bg-slate-50 pb-20">
    <!-- 横幅区 -->
    <MallBanner :user-points="userPoints" />

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-24 relative z-10">
      <div class="flex flex-col lg:flex-row gap-8">
        <!-- 侧边分类菜单 -->
        <aside class="w-full lg:w-64 flex-shrink-0">
          <CategorySidebar
            :selected-category="selectedCategory"
            @category-change="handleCategoryChange"
          />
        </aside>

        <!-- 主内容区域 -->
        <main class="flex-1">
          <!-- 搜索和过滤栏 -->
          <div class="bg-white rounded-2xl p-4 shadow-sm mb-6 flex flex-col sm:flex-row justify-between items-center gap-4 border border-slate-100">
             <div class="text-lg font-bold text-slate-800">
               {{ categoryTitle }}
               <span class="text-sm font-normal text-slate-500 ml-2">共 {{ total }} 件商品</span>
             </div>

             <div class="flex items-center gap-4 w-full sm:w-auto">
                <a-input-search
                  v-model:value="searchKeyword"
                  placeholder="搜索商品..."
                  class="w-full sm:w-64"
                  @search="handleSearch"
                  allow-clear
                />
                
                <a-select v-model:value="sortOption" placeholder="默认排序" style="width: 120px" @change="handleSortChange">
                  <a-select-option value="">综合排序</a-select-option>
                  <a-select-option value="price">价格升序</a-select-option>
                  <a-select-option value="price_desc">价格降序</a-select-option>
                </a-select>
             </div>
          </div>

          <!-- 商品网格 -->
          <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
             <a-skeleton active :paragraph="{ rows: 6 }" class="bg-white p-4 rounded-xl" v-for="i in 8" :key="i" />
          </div>

          <div v-else-if="products.length === 0" class="bg-white rounded-2xl py-20 text-center shadow-sm border border-slate-100">
            <a-empty description="暂无相关商品" />
          </div>

          <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-8">
            <ProductCard
              v-for="product in products"
              :key="product.id"
              :product="product"
              @product-click="handleProductClick"
            />
          </div>

          <!-- 加载更多 -->
          <div v-if="hasMore && products.length > 0" class="text-center py-8">
            <a-button @click="loadMore" :loading="loadingMore" size="large" shape="round">
              加载更多商品
            </a-button>
          </div>
        </main>
      </div>
    </div>

    <!-- 商品详情模态框 -->
    <ProductDetailModal
      v-model="showDetailModal"
      :product="selectedProduct"
      :user-points="userPoints"
      @exchange-click="handleExchangeClick"
    />

    <!-- 兑换确认模态框 -->
    <ExchangeModal
      v-model="showExchangeModal"
      :exchange-data="exchangeData"
      :user-points="userPoints"
      @confirm-exchange="handleConfirmExchange"
    />

    <!-- 成功反馈模态框 -->
    <SuccessModal
      v-model="showSuccessModal"
      :exchange-order-number="exchangeOrderNumber"
      @continue-shopping="handleContinueShopping"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { getProductList, getPointsInfo, exchangeProduct, searchProducts } from '@/api/points'
import MallBanner from './components/MallBanner.vue'
import CategorySidebar from './components/CategorySidebar.vue'
import ProductCard from './components/ProductCard.vue'
import ProductDetailModal from './components/ProductDetailModal.vue'
import ExchangeModal from './components/ExchangeModal.vue'
import SuccessModal from './components/SuccessModal.vue'

// 响应式数据
const userPoints = ref(0)
const selectedCategory = ref('all')
const sortOption = ref('')
const searchKeyword = ref('')
const isSearchMode = ref(false)
const products = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const loading = ref(false)
const loadingMore = ref(false)

// 模态框相关状态
const showDetailModal = ref(false)
const showExchangeModal = ref(false)
const showSuccessModal = ref(false)
const selectedProduct = ref(null)
const exchangeData = ref(null)
const exchangeOrderNumber = ref('')

// 计算属性
const categoryTitle = computed(() => {
  if (searchKeyword.value) {
    return `搜索: "${searchKeyword.value}"`
  }
  if (selectedCategory.value === 'all') {
    return '全部商品'
  }
  return selectedCategory.value
})

const hasMore = computed(() => {
  return products.value.length < total.value
})

// 获取用户积分
const fetchUserPoints = async () => {
  try {
    const data = await getPointsInfo()
    userPoints.value = data.currentPoints || 0
  } catch (error) {
    // message.error('获取积分信息失败')
  }
}

// 获取商品列表
const fetchProducts = async (loadMore = false) => {
  try {
    if (loadMore) {
      loadingMore.value = true
    } else {
      loading.value = true
      currentPage.value = 1
      isSearchMode.value = false
    }

    const params = {
      category: selectedCategory.value !== 'all' ? selectedCategory.value : undefined,
      page: currentPage.value,
      size: pageSize.value,
    }

    if (sortOption.value) {
      if (sortOption.value === 'price_desc') {
        params.sortBy = 'price'
        params.isAsc = false
      } else if (sortOption.value === 'price') {
        params.sortBy = 'price'
        params.isAsc = true
      } else {
        params.sortBy = sortOption.value
        params.isAsc = false
      }
    }

    try {
      const response = await getProductList(params)
      const processedProducts = processProducts(response.data.records)

      if (loadMore) {
        products.value = [...products.value, ...processedProducts]
      } else {
        products.value = processedProducts
      }

      total.value = response.data.total || 0
    } catch (apiError) {
      message.error('获取商品列表失败')
    }

    if (loadMore) {
      currentPage.value++
    }
  } catch (error) {
    message.error('获取商品列表失败')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

// 搜索商品
const fetchSearchResults = async (loadMore = false) => {
  if (!searchKeyword.value.trim()) {
    fetchProducts()
    return
  }

  try {
    if (loadMore) {
      loadingMore.value = true
    } else {
      loading.value = true
      currentPage.value = 1
      isSearchMode.value = true
    }

    const params = {
      name: searchKeyword.value,
      page: currentPage.value,
      size: pageSize.value,
    }

    if (sortOption.value) {
      if (sortOption.value === 'price_desc') {
        params.sortBy = 'price'
        params.isAsc = false
      } else if (sortOption.value === 'price') {
        params.sortBy = 'price'
        params.isAsc = true
      } else {
        params.sortBy = sortOption.value
        params.isAsc = false
      }
    }

    try {
      const response = await searchProducts(params)
      const processedProducts = processProducts(response.data.records)

      if (loadMore) {
        products.value = [...products.value, ...processedProducts]
      } else {
        products.value = processedProducts
      }
      total.value = response.data.total || 0
    } catch (apiError) {
      message.error('搜索失败，请稍后重试')
      products.value = []
      total.value = 0
    }

    if (loadMore) {
      currentPage.value++
    }
  } catch (error) {
    message.error('搜索失败')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const processProducts = (records) => {
  return records.map((item) => {
    let specInfo = {}
    if (item.spec) {
      try {
        specInfo = typeof item.spec === 'string' ? JSON.parse(item.spec) : item.spec
      } catch (e) {
        specInfo = {}
      }
    }

    return {
      id: item.id,
      name: item.name,
      price: item.price,
      stock: item.stock,
      image: item.image,
      images: item.images,
      category: item.category,
      brand: item.brand,
      spec: specInfo,
      description: '',
      sold: item.sold || 0,
      commentCount: item.commentCount || 0,
      isAd: item.isAd || false,
      tags: [],
      status: item.status,
      createTime: item.createTime,
      updateTime: item.updateTime,
    }
  })
}

const handleCategoryChange = (category) => {
  selectedCategory.value = category
  searchKeyword.value = ''
  fetchProducts()
}

const handleSortChange = () => {
  if (isSearchMode.value) {
    fetchSearchResults()
  } else {
    fetchProducts()
  }
}

const handleProductClick = (product) => {
  selectedProduct.value = product
  showDetailModal.value = true
}

const handleExchangeClick = (data) => {
  exchangeData.value = data
  showDetailModal.value = false
  showExchangeModal.value = true
}

const handleConfirmExchange = async (data) => {
  try {
    const response = await exchangeProduct({
      itemId: data.product.id,
      quantity: data.quantity,
      recipientName: data.recipientName,
      recipientPhone: data.recipientPhone,
      recipientAddress: data.recipientAddress,
    })

    exchangeOrderNumber.value = response.data.orderNumber || `EX${Date.now()}`
    showExchangeModal.value = false
    showSuccessModal.value = true

    userPoints.value = response.data.remainingPoints || userPoints.value - data.totalPoints
    message.success('兑换成功！')
  } catch (error) {
    message.error(error.response?.data?.message || '兑换失败，请稍后重试')
  }
}

const handleContinueShopping = () => {
  if (isSearchMode.value) {
    fetchSearchResults()
  } else {
    fetchProducts()
  }
}

const loadMore = () => {
  if (isSearchMode.value) {
    fetchSearchResults(true)
  } else {
    fetchProducts(true)
  }
}

const handleSearch = () => {
  selectedCategory.value = 'all'
  fetchSearchResults()
}

onMounted(() => {
  fetchUserPoints()
  fetchProducts()
})
</script>
