<template>
  <div class="relative py-14 bg-[#0B2559] overflow-hidden">
    <div class="absolute inset-x-0 bottom-0 h-px bg-[#DDE6F0]/30"></div>
    <div class="absolute left-0 right-0 bottom-10 h-[2px] bg-white/15"></div>
    <div class="absolute left-[8%] right-[8%] bottom-7 h-[2px] bg-white/10"></div>
    
    <!-- Content -->
    <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-white">
      <p class="text-sm font-semibold text-[#D8E8FF] mb-3">ClodRail 出行积分</p>
      <h1 class="text-3xl md:text-4xl font-extrabold tracking-normal mb-3">
        积分商城
      </h1>
      <p class="text-base md:text-lg text-[#D8E8FF] mb-8 max-w-2xl">
        用您的出行积分兑换精美礼品，让每一次旅程都充满惊喜
      </p>
      
      <!-- Points Display -->
      <div class="inline-flex items-center bg-white border border-white/30 rounded-xl px-5 py-3 shadow-[0_14px_32px_rgba(11,37,89,0.16)]">
        <span class="text-[#0B2559] text-sm mr-3 font-semibold">我的积分</span>
        <div class="flex items-center gap-2">
          <i class="ri-copper-coin-line text-[#FF8200] text-2xl"></i>
          <span class="text-3xl font-bold text-[#0B2559] font-data">{{ userPoints }}</span>
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
