<script setup lang="ts">
// 定义Props
interface Props {
  activeCategory: string
}

// 定义Emits
interface Emits {
  (e: 'categoryChange', category: string): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 分类配置
const categories = [
  {
    id: 'all',
    name: '全部商品',
    icon: '🛍️',
    count: 128,
    color: '#667eea'
  },
  {
    id: 'coupons',
    name: '优惠券',
    icon: '🎫',
    count: 24,
    color: '#f093fb'
  },
  {
    id: 'travel',
    name: '旅行用品',
    icon: '🧳',
    count: 32,
    color: '#4facfe'
  },
  {
    id: 'digital',
    name: '数码产品',
    icon: '📱',
    count: 18,
    color: '#43e97b'
  },
  {
    id: 'lifestyle',
    name: '生活用品',
    icon: '🏠',
    count: 28,
    color: '#fa709a'
  },
  {
    id: 'food',
    name: '美食特产',
    icon: '🍎',
    count: 26,
    color: '#ffecd2'
  }
]

// 处理分类点击
const handleCategoryClick = (categoryId: string) => {
  emit('categoryChange', categoryId)
}
</script>

<template>
  <div class="category-sidebar">
    <div class="sidebar-header">
      <h3 class="sidebar-title">
        <span class="title-icon">📂</span>
        商品分类
      </h3>
    </div>
    
    <div class="category-list">
      <div
        v-for="category in categories"
        :key="category.id"
        class="category-item"
        :class="{ active: activeCategory === category.id }"
        @click="handleCategoryClick(category.id)"
      >
        <div class="category-content">
          <div class="category-main">
            <span 
              class="category-icon"
              :style="{ color: category.color }"
            >
              {{ category.icon }}
            </span>
            <span class="category-name">{{ category.name }}</span>
          </div>
          
          <div class="category-meta">
            <span class="category-count">{{ category.count }}</span>
            <span class="category-arrow">›</span>
          </div>
        </div>
        
        <!-- 活跃状态指示器 -->
        <div 
          class="category-indicator"
          :style="{ backgroundColor: category.color }"
        ></div>
      </div>
    </div>
    
    <!-- 热门推荐 -->
    <div class="hot-recommendations">
      <div class="recommendation-header">
        <h4 class="recommendation-title">
          <span class="title-icon">🔥</span>
          热门推荐
        </h4>
      </div>
      
      <div class="recommendation-list">
        <div class="recommendation-item">
          <div class="recommendation-image">
            <span class="image-placeholder">🎧</span>
          </div>
          <div class="recommendation-info">
            <div class="recommendation-name">蓝牙耳机</div>
            <div class="recommendation-points">2,999积分</div>
          </div>
        </div>
        
        <div class="recommendation-item">
          <div class="recommendation-image">
            <span class="image-placeholder">☕</span>
          </div>
          <div class="recommendation-info">
            <div class="recommendation-name">咖啡券</div>
            <div class="recommendation-points">199积分</div>
          </div>
        </div>
        
        <div class="recommendation-item">
          <div class="recommendation-image">
            <span class="image-placeholder">🎒</span>
          </div>
          <div class="recommendation-info">
            <div class="recommendation-name">旅行背包</div>
            <div class="recommendation-points">1,599积分</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 积分获取提示 -->
    <div class="points-tip">
      <div class="tip-header">
        <span class="tip-icon">💡</span>
        <span class="tip-title">积分小贴士</span>
      </div>
      <div class="tip-content">
        <p class="tip-text">每日签到可获得20积分，连续签到7天额外获得100积分奖励！</p>
        <button class="tip-action">
          立即签到
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.category-sidebar {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  height: fit-content;
  position: sticky;
  top: 20px;
}

/* 侧边栏头部 */
.sidebar-header {
  padding: 20px;
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f2ff 100%);
  border-bottom: 1px solid #e8ecf4;
}

.sidebar-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 18px;
}

/* 分类列表 */
.category-list {
  padding: 8px 0;
}

.category-item {
  position: relative;
  margin: 0 12px 4px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.category-item:hover {
  background: #f8f9ff;
  transform: translateX(2px);
}

.category-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.category-item.active:hover {
  transform: translateX(0);
}

.category-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  position: relative;
  z-index: 2;
}

.category-main {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
}

.category-name {
  font-size: 14px;
  font-weight: 500;
}

.category-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-count {
  font-size: 12px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
}

.category-item.active .category-count {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.category-arrow {
  font-size: 16px;
  opacity: 0.6;
  transition: transform 0.3s ease;
}

.category-item:hover .category-arrow {
  transform: translateX(2px);
}

.category-item.active .category-arrow {
  opacity: 0.8;
}

.category-indicator {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: transparent;
  transition: all 0.3s ease;
}

.category-item.active .category-indicator {
  width: 4px;
}

/* 热门推荐 */
.hot-recommendations {
  margin: 20px 0;
  padding: 0 20px;
}

.recommendation-header {
  margin-bottom: 12px;
}

.recommendation-title {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 6px;
}

.recommendation-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.recommendation-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.recommendation-item:hover {
  background: #f8f9ff;
  transform: translateX(2px);
}

.recommendation-image {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #ffeaa7 0%, #fab1a0 100%);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.image-placeholder {
  font-size: 16px;
}

.recommendation-info {
  flex: 1;
  min-width: 0;
}

.recommendation-name {
  font-size: 12px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.recommendation-points {
  font-size: 11px;
  color: #667eea;
  font-weight: 600;
}

/* 积分提示 */
.points-tip {
  margin: 20px;
  padding: 16px;
  background: linear-gradient(135deg, #ffeaa7 0%, #fab1a0 100%);
  border-radius: 12px;
  color: #2c3e50;
}

.tip-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.tip-icon {
  font-size: 16px;
}

.tip-title {
  font-size: 13px;
  font-weight: 600;
}

.tip-content {
  margin-left: 24px;
}

.tip-text {
  margin: 0 0 12px 0;
  font-size: 12px;
  line-height: 1.4;
  opacity: 0.9;
}

.tip-action {
  background: rgba(255, 255, 255, 0.9);
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 500;
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tip-action:hover {
  background: white;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .category-sidebar {
    position: static;
  }
}

@media (max-width: 768px) {
  .category-sidebar {
    border-radius: 8px;
  }
  
  .sidebar-header {
    padding: 16px;
  }
  
  .sidebar-title {
    font-size: 15px;
  }
  
  .category-content {
    padding: 10px 12px;
  }
  
  .category-name {
    font-size: 13px;
  }
  
  .hot-recommendations {
    margin: 16px 0;
    padding: 0 16px;
  }
  
  .points-tip {
    margin: 16px;
    padding: 12px;
  }
}

@media (max-width: 480px) {
  .category-item {
    margin: 0 8px 2px;
  }
  
  .category-content {
    padding: 8px 12px;
  }
  
  .category-icon {
    font-size: 16px;
    width: 20px;
  }
  
  .category-name {
    font-size: 12px;
  }
  
  .category-count {
    font-size: 11px;
    padding: 1px 6px;
  }
  
  .recommendation-image {
    width: 28px;
    height: 28px;
  }
  
  .image-placeholder {
    font-size: 14px;
  }
  
  .recommendation-name {
    font-size: 11px;
  }
  
  .recommendation-points {
    font-size: 10px;
  }
}
</style>