<script setup lang="ts">
import { computed, withDefaults } from 'vue'

// 定义商品接口
interface Product {
  id: string
  name: string
  description: string
  points: number
  originalPrice?: number
  image: string
  category: string
  stock: number
  isHot?: boolean
  isNew?: boolean
  discount?: number
  rating?: number
  reviewCount?: number
}

// 定义Props
interface Props {
  products: Product[]
  loading?: boolean
  activeCategory: string
}

// 定义Emits
interface Emits {
  (e: 'productClick', product: Product): void
  (e: 'exchangeClick', product: Product): void
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

const emit = defineEmits<Emits>()

// 过滤商品
const filteredProducts = computed(() => {
  if (props.activeCategory === 'all') {
    return props.products
  }
  return props.products.filter(product => product.category === props.activeCategory)
})

// 格式化积分显示
const formatPoints = (points: number) => {
  return points.toLocaleString()
}

// 格式化价格显示
const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}

// 处理商品点击
const handleProductClick = (product: Product) => {
  emit('productClick', product)
}

// 处理兑换点击
const handleExchangeClick = (product: Product, event: Event) => {
  event.stopPropagation()
  emit('exchangeClick', product)
}

// 获取库存状态
const getStockStatus = (stock: number) => {
  if (stock === 0) return { text: '缺货', class: 'out-of-stock' }
  if (stock <= 5) return { text: `仅剩${stock}件`, class: 'low-stock' }
  return { text: '现货', class: 'in-stock' }
}

// 生成星级评分
const generateStars = (rating: number) => {
  const fullStars = Math.floor(rating)
  const hasHalfStar = rating % 1 >= 0.5
  const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0)
  
  return {
    full: '★'.repeat(fullStars),
    half: hasHalfStar ? '☆' : '',
    empty: '☆'.repeat(emptyStars)
  }
}
</script>

<template>
  <div class="product-grid">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-grid">
        <div 
          v-for="i in 8" 
          :key="i" 
          class="loading-card"
        >
          <div class="loading-image"></div>
          <div class="loading-content">
            <div class="loading-line long"></div>
            <div class="loading-line medium"></div>
            <div class="loading-line short"></div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 商品列表 -->
    <div v-else-if="filteredProducts.length > 0" class="products-container">
      <div class="products-header">
        <div class="products-count">
          共找到 <span class="count-number">{{ filteredProducts.length }}</span> 件商品
        </div>
        
        <div class="sort-options">
          <select class="sort-select">
            <option value="default">默认排序</option>
            <option value="points-asc">积分从低到高</option>
            <option value="points-desc">积分从高到低</option>
            <option value="rating">评分最高</option>
            <option value="new">最新上架</option>
          </select>
        </div>
      </div>
      
      <div class="products-grid">
        <div
          v-for="product in filteredProducts"
          :key="product.id"
          class="product-card"
          @click="handleProductClick(product)"
        >
          <!-- 商品图片 -->
          <div class="product-image">
            <div class="image-placeholder">
              {{ product.image }}
            </div>
            
            <!-- 标签 -->
            <div class="product-badges">
              <span v-if="product.isHot" class="badge hot">🔥 热门</span>
              <span v-if="product.isNew" class="badge new">🆕 新品</span>
              <span v-if="product.discount" class="badge discount">
                {{ product.discount }}折
              </span>
            </div>
            
            <!-- 库存状态 -->
            <div 
              class="stock-status"
              :class="getStockStatus(product.stock).class"
            >
              {{ getStockStatus(product.stock).text }}
            </div>
          </div>
          
          <!-- 商品信息 -->
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-description">{{ product.description }}</p>
            
            <!-- 评分 -->
            <div v-if="product.rating" class="product-rating">
              <div class="stars">
                <span class="stars-full">{{ generateStars(product.rating).full }}</span>
                <span class="stars-half">{{ generateStars(product.rating).half }}</span>
                <span class="stars-empty">{{ generateStars(product.rating).empty }}</span>
              </div>
              <span class="rating-text">
                {{ product.rating }} ({{ product.reviewCount }}评价)
              </span>
            </div>
            
            <!-- 价格信息 -->
            <div class="product-pricing">
              <div class="points-price">
                <span class="points-icon">💎</span>
                <span class="points-value">{{ formatPoints(product.points) }}</span>
                <span class="points-unit">积分</span>
              </div>
              
              <div v-if="product.originalPrice" class="original-price">
                市场价：<span class="price-value">{{ formatPrice(product.originalPrice) }}</span>
              </div>
            </div>
            
            <!-- 兑换按钮 -->
            <button 
              class="exchange-btn"
              :class="{ disabled: product.stock === 0 }"
              :disabled="product.stock === 0"
              @click="handleExchangeClick(product, $event)"
            >
              <span class="btn-icon">🎁</span>
              <span class="btn-text">
                {{ product.stock === 0 ? '暂时缺货' : '立即兑换' }}
              </span>
            </button>
          </div>
        </div>
      </div>
      
      <!-- 加载更多 -->
      <div class="load-more">
        <button class="load-more-btn">
          <span class="btn-icon">⬇️</span>
          加载更多商品
        </button>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">📦</div>
      <div class="empty-title">暂无商品</div>
      <div class="empty-description">该分类下暂时没有商品，请选择其他分类查看</div>
      <button class="empty-action" @click="() => {}">
        查看全部商品
      </button>
    </div>
  </div>
</template>

<style scoped>
.product-grid {
  flex: 1;
}

/* 加载状态 */
.loading-container {
  padding: 20px;
}

.loading-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.loading-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.loading-image {
  height: 200px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

.loading-content {
  padding: 16px;
}

.loading-line {
  height: 12px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  border-radius: 6px;
  margin-bottom: 8px;
  animation: loading 1.5s infinite;
}

.loading-line.long {
  width: 80%;
}

.loading-line.medium {
  width: 60%;
}

.loading-line.short {
  width: 40%;
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* 商品容器 */
.products-container {
  padding: 20px;
}

.products-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8ecf4;
}

.products-count {
  font-size: 14px;
  color: #666;
}

.count-number {
  font-weight: 600;
  color: #667eea;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-select {
  padding: 6px 12px;
  border: 1px solid #e8ecf4;
  border-radius: 6px;
  font-size: 13px;
  background: white;
  cursor: pointer;
}

.sort-select:focus {
  outline: none;
  border-color: #667eea;
}

/* 商品网格 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* 商品图片 */
.product-image {
  position: relative;
  height: 200px;
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f2ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.image-placeholder {
  font-size: 48px;
  opacity: 0.8;
}

.product-badges {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.badge {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 500;
  color: white;
}

.badge.hot {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
}

.badge.new {
  background: linear-gradient(135deg, #00d2d3 0%, #54a0ff 100%);
}

.badge.discount {
  background: linear-gradient(135deg, #feca57 0%, #ff9ff3 100%);
  color: #2c3e50;
}

.stock-status {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 500;
}

.stock-status.in-stock {
  background: rgba(39, 174, 96, 0.1);
  color: #27ae60;
}

.stock-status.low-stock {
  background: rgba(243, 156, 18, 0.1);
  color: #f39c12;
}

.stock-status.out-of-stock {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

/* 商品信息 */
.product-info {
  padding: 16px;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-description {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #666;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.stars {
  display: flex;
  align-items: center;
}

.stars-full {
  color: #feca57;
}

.stars-half {
  color: #feca57;
}

.stars-empty {
  color: #ddd;
}

.rating-text {
  font-size: 11px;
  color: #999;
}

.product-pricing {
  margin-bottom: 16px;
}

.points-price {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.points-icon {
  font-size: 16px;
}

.points-value {
  font-size: 20px;
  font-weight: 700;
  color: #667eea;
}

.points-unit {
  font-size: 12px;
  color: #667eea;
  font-weight: 500;
}

.original-price {
  font-size: 11px;
  color: #999;
}

.price-value {
  text-decoration: line-through;
}

.exchange-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.exchange-btn:hover:not(.disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.exchange-btn.disabled {
  background: #ddd;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 14px;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 20px 0;
}

.load-more-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: white;
  border: 2px solid #667eea;
  color: #667eea;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.load-more-btn:hover {
  background: #667eea;
  color: white;
  transform: translateY(-1px);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.empty-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 24px;
  line-height: 1.5;
}

.empty-action {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.empty-action:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .products-container {
    padding: 16px;
  }
  
  .products-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 12px;
  }
  
  .product-image {
    height: 160px;
  }
  
  .image-placeholder {
    font-size: 36px;
  }
  
  .product-info {
    padding: 12px;
  }
  
  .product-name {
    font-size: 14px;
  }
  
  .points-value {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .product-card {
    display: flex;
    height: 120px;
  }
  
  .product-image {
    width: 120px;
    height: 120px;
    flex-shrink: 0;
  }
  
  .image-placeholder {
    font-size: 24px;
  }
  
  .product-info {
    flex: 1;
    padding: 8px 12px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  
  .product-name {
    font-size: 13px;
    margin-bottom: 4px;
    -webkit-line-clamp: 1;
  }
  
  .product-description {
    font-size: 11px;
    margin-bottom: 8px;
    -webkit-line-clamp: 1;
  }
  
  .product-rating {
    margin-bottom: 8px;
  }
  
  .product-pricing {
    margin-bottom: 8px;
  }
  
  .points-value {
    font-size: 16px;
  }
  
  .exchange-btn {
    padding: 6px 8px;
    font-size: 12px;
  }
}
</style>