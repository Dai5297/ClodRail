<script setup>
import HeroSection from '@/view/home/components/HeroSection.vue'
import FeaturesSection from '@/view/home/components/FeaturesSection.vue'
import PopularRoutesSection from '@/view/home/components/PopularRoutesSection.vue'
</script>

<template>
  <div class="home-page">
    <!-- Hero & Search Section -->
    <HeroSection />
    
    <!-- 热门路线区域 -->
    <PopularRoutesSection />
    
    <!-- 功能特色区域 -->
    <FeaturesSection />
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}
</style>
