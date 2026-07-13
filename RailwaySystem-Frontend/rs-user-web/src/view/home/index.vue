<script setup>
import HeroSection from '@/view/home/components/HeroSection.vue'
import FeaturesSection from '@/view/home/components/FeaturesSection.vue'
import PopularRoutesSection from '@/view/home/components/PopularRoutesSection.vue'
</script>

<template>
  <div class="home-page">
    <HeroSection />

    <FeaturesSection />

    <PopularRoutesSection />
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: var(--rail-background);
}
</style>
