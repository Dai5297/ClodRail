<template>
  <div class="bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 border border-transparent hover:border-slate-100 overflow-hidden group h-full flex flex-col cursor-pointer" @click="handleClick">
    <!-- Image -->
    <div class="relative pt-[75%] bg-slate-50 overflow-hidden">
      <img 
        :src="getDisplayImage()" 
        :alt="product.name" 
        class="absolute inset-0 w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
      />
      
      <!-- Tags -->
      <div class="absolute top-3 left-3 flex flex-wrap gap-2 z-10">
        <span 
          v-for="tag in product.tags" 
          :key="tag" 
          class="px-2 py-1 bg-blue-600/90 backdrop-blur-sm text-white text-xs font-medium rounded-lg"
        >
          {{ tag }}
        </span>
      </div>
      
      <div v-if="product.isAd" class="absolute top-3 right-3 px-2 py-1 bg-orange-500/90 backdrop-blur-sm text-white text-xs font-medium rounded-lg z-10">
        推广
      </div>
      
      <div v-if="product.stock <= 10 && product.stock > 0" class="absolute bottom-3 right-3 px-2 py-1 bg-red-500/90 backdrop-blur-sm text-white text-xs font-medium rounded-lg z-10">
        仅剩 {{ product.stock }} 件
      </div>
      
      <!-- Overlay on hover -->
      <div class="absolute inset-0 bg-black/0 group-hover:bg-black/5 transition-colors duration-300"></div>
    </div>

    <!-- Content -->
    <div class="p-5 flex-1 flex flex-col">
      <div class="mb-1">
        <span v-if="product.brand" class="text-xs text-slate-400 bg-slate-100 px-2 py-0.5 rounded-md mr-2">{{ product.brand }}</span>
      </div>
      
      <h3 class="text-base font-bold text-slate-800 mb-2 line-clamp-2 min-h-[3rem] group-hover:text-primary transition-colors">
        {{ product.name }}
      </h3>

      <div class="mt-auto">
        <div class="flex items-baseline gap-1 mb-3">
          <span class="text-xl font-bold text-blue-600">{{ product.price }}</span>
          <span class="text-xs text-slate-500">积分</span>
        </div>

        <div class="flex items-center justify-between text-xs text-slate-400 mb-4">
          <span class="flex items-center gap-1">
            <i class="ri-shopping-bag-3-line"></i> 已兑 {{ formatNumber(product.sold) }}
          </span>
          <span class="flex items-center gap-1">
            <i class="ri-message-2-line"></i> {{ formatNumber(product.commentCount) }}
          </span>
        </div>

        <button 
          class="w-full py-2.5 rounded-xl font-bold text-sm transition-all duration-300 flex items-center justify-center gap-2"
          :class="[
            isAvailable 
              ? 'bg-primary hover:bg-blue-700 text-white shadow-lg shadow-blue-500/20 hover:shadow-blue-500/40' 
              : 'bg-slate-100 text-slate-400 cursor-not-allowed'
          ]"
          :disabled="!isAvailable"
          @click.stop="handleExchange"
        >
          {{ buttonText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['product-click'])

const defaultImage = 'https://via.placeholder.com/300x200?text=商品图片'

const isAvailable = computed(() => {
  return props.product.status === 1 && props.product.stock > 0
})

const buttonText = computed(() => {
  if (props.product.status !== 1) return '已下架'
  if (props.product.stock <= 0) return '已售罄'
  return '立即兑换'
})

const getDisplayImage = () => {
  return props.product.image || props.product.images || defaultImage
}

const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num
}

const handleClick = () => {
  emit('product-click', props.product)
}

const handleExchange = () => {
  if (isAvailable.value) {
    emit('product-click', props.product)
  }
}
</script>
