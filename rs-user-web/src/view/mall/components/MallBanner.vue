<template>
  <div class="relative py-20 bg-background overflow-hidden">
    <!-- Background Gradient -->
    <div class="absolute inset-0 bg-gradient-to-r from-blue-600 to-indigo-700 opacity-90"></div>
    <div class="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1557683316-973673baf926?auto=format&fit=crop&q=80')] bg-cover bg-center mix-blend-overlay opacity-30"></div>
    
    <!-- Content -->
    <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center text-white">
      <h1 class="text-4xl md:text-5xl font-extrabold tracking-tight mb-4 drop-shadow-md">
        积分商城
      </h1>
      <p class="text-lg md:text-xl text-blue-100 mb-10 max-w-2xl mx-auto font-light">
        用您的出行积分兑换精美礼品，让每一次旅程都充满惊喜
      </p>
      
      <!-- Points Display -->
      <div class="inline-flex items-center bg-white/10 backdrop-blur-md border border-white/20 rounded-full px-6 py-3 shadow-lg">
        <span class="text-blue-100 text-sm mr-3 uppercase tracking-wider font-semibold">我的积分</span>
        <div class="flex items-center gap-2">
          <i class="ri-copper-coin-line text-yellow-400 text-2xl"></i>
          <span class="text-3xl font-bold text-white">{{ userPoints }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  userPoints: {
    type: Number,
    default: 0
  }
})
</script>
