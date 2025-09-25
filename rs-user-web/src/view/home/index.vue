<script setup>
import BannerSection from './component/BannerSection.vue'
import SearchSection from './component/SearchSection.vue'
import FeaturesSection from './component/FeaturesSection.vue'
import PopularRoutesSection from './component/PopularRoutesSection.vue'

// 由于所有子组件都已经自行处理事件和路由跳转，
// 父组件不再需要定义事件处理函数
</script>

<template>
  <div class="home-page">
    <!-- 轮播图区域 -->
    <BannerSection />
    
    <!-- 搜索区域 -->
    <SearchSection class="search-section" />
    
    <!-- 功能特色区域 -->
    <FeaturesSection />
    
    <!-- 热门路线区域 -->
    <PopularRoutesSection />
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

/* 确保搜索区域可以被平滑滚动定位 */
.search-section {
  scroll-margin-top: 80px; /* 考虑到可能的固定头部高度 */
}

/* 全局样式重置 */
.home-page * {
  box-sizing: border-box;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-section {
    scroll-margin-top: 60px;
  }
}
</style>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

/* 确保搜索区域可以被平滑滚动定位 */
.search-section {
  scroll-margin-top: 80px; /* 考虑到可能的固定头部高度 */
}

/* 全局样式重置 */
.home-page * {
  box-sizing: border-box;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-section {
    scroll-margin-top: 60px;
  }
}
</style>