<template>
  <section class="banner-section">
    <el-carousel 
      :interval="5000" 
      :arrow="always" 
      height="400px"
      indicator-position="inside"
    >
      <el-carousel-item v-for="(item, index) in bannerList" :key="index">
        <div class="banner-item" :style="{ background: item.background }">
          <div class="banner-content">
            <h1 class="banner-title">{{ item.title }}</h1>
            <p class="banner-subtitle">{{ item.subtitle }}</p>
            <div class="banner-actions" v-if="item.showActions">
              <el-button type="primary" size="large" @click="handleStartBooking">
                立即购票
              </el-button>
              <el-button size="large" @click="handleLearnMore">
                了解更多
              </el-button>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 轮播图数据
const bannerList = ref([
  {
    title: '智能出行，从这里开始',
    subtitle: '便捷购票 · 智能推荐 · 贴心服务',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    showActions: true
  },
  {
    title: 'AI智能推荐最优路线',
    subtitle: '大数据分析 · 个性化推荐 · 省时省钱',
    background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    showActions: true
  },
  {
    title: '积分商城精彩好礼',
    subtitle: '购票获积分 · 积分换好礼 · 惊喜不断',
    background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    showActions: false
  }
])

// 处理立即购票点击
const handleStartBooking = () => {
  // 滚动到搜索区域
  const searchElement = document.querySelector('.search-section')
  if (searchElement) {
    searchElement.scrollIntoView({ behavior: 'smooth' })
  }
}

// 处理了解更多点击
const handleLearnMore = () => {
  router.push('/about')
}

// Banner 数据列表
const banners = [
  {
    title: '智能出行，从这里开始',
    subtitle: '便捷购票 · 智能推荐 · 贴心服务',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    showActions: true
  },
  {
    title: 'AI智能推荐最优路线',
    subtitle: '大数据分析 · 个性化推荐 · 省时省钱',
    background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    showActions: true
  },
  {
    title: '积分商城精彩好礼',
    subtitle: '购票获积分 · 积分换好礼 · 惊喜不断',
    background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    showActions: false
  }
]
</script>

<style scoped>
.banner-section {
  width: 100%;
  height: 400px;
  overflow: hidden;
}

.banner-item {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  position: relative;
}

.banner-content {
  text-align: center;
  z-index: 2;
  max-width: 800px;
  padding: 0 20px;
}

.banner-title {
  font-size: 48px;
  margin-bottom: 20px;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  animation: fadeInUp 1s ease-out;
}

.banner-subtitle {
  font-size: 20px;
  opacity: 0.95;
  margin-bottom: 30px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  animation: fadeInUp 1s ease-out 0.2s both;
}

.banner-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  animation: fadeInUp 1s ease-out 0.4s both;
}

.banner-actions .el-button {
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 25px;
  transition: all 0.3s ease;
}

.banner-actions .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

/* 轮播图指示器样式 */
:deep(.el-carousel__indicators) {
  bottom: 20px;
}

:deep(.el-carousel__indicator) {
  width: 12px;
  height: 12px;
}

:deep(.el-carousel__button) {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
}

:deep(.el-carousel__indicator.is-active .el-carousel__button) {
  background-color: white;
}

/* 轮播图箭头样式 */
:deep(.el-carousel__arrow) {
  background-color: rgba(0, 0, 0, 0.3);
  border: none;
  width: 50px;
  height: 50px;
}

:deep(.el-carousel__arrow:hover) {
  background-color: rgba(0, 0, 0, 0.5);
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-title {
    font-size: 32px;
  }
  
  .banner-subtitle {
    font-size: 16px;
  }
  
  .banner-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .banner-actions .el-button {
    width: 200px;
  }
}

@media (max-width: 480px) {
  .banner-title {
    font-size: 24px;
  }
  
  .banner-subtitle {
    font-size: 14px;
  }
}
</style>