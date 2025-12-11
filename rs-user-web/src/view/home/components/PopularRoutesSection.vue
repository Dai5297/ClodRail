<template>
  <div class="py-24 bg-slate-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-end mb-12">
        <div>
          <h2 class="text-3xl font-bold text-slate-900 mb-2">热门目的地</h2>
          <p class="text-slate-500">探索最受欢迎的旅行胜地</p>
        </div>
        <a @click="handleViewMore" class="text-primary font-medium hover:text-blue-700 flex items-center gap-1 cursor-pointer">
          查看全部 <i class="ri-arrow-right-line"></i>
        </a>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <div 
          v-for="(route, index) in routeList" 
          :key="index" 
          class="group relative h-80 rounded-3xl overflow-hidden cursor-pointer shadow-sm hover:shadow-xl transition-all duration-500"
          @click="handleRouteClick(route)"
        >
          <!-- Image Background -->
          <div class="absolute inset-0 transition-transform duration-700 group-hover:scale-110">
            <img :src="route.image" class="w-full h-full object-cover" alt="Destination" />
          </div>
          <!-- Overlay -->
          <div class="absolute inset-0 bg-gradient-to-t from-slate-900/80 via-slate-900/20 to-transparent"></div>
          
          <div v-if="route.isHot" class="absolute top-6 right-6 bg-white/20 backdrop-blur-md px-3 py-1 rounded-full text-white text-xs font-medium border border-white/20">
            热门
          </div>
          
          <div class="absolute bottom-0 left-0 p-8 w-full">
            <h3 class="text-2xl font-bold text-white mb-2">{{ route.departure }} <i class="ri-arrow-right-line text-sm mx-1 opacity-70"></i> {{ route.arrival }}</h3>
            <div class="flex justify-between items-center">
              <div>
                 <p class="text-white/90 text-sm font-medium">¥{{ route.minPrice }} 起</p>
                 <p class="text-white/70 text-xs mt-1">{{ route.duration }}</p>
              </div>
              <span class="w-10 h-10 rounded-full bg-white text-slate-900 flex items-center justify-center opacity-0 group-hover:opacity-100 transform translate-y-4 group-hover:translate-y-0 transition-all duration-300">
                <i class="ri-arrow-right-line"></i>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const routeList = ref([
  {
    id: 1,
    departure: '北京',
    arrival: '上海',
    minPrice: 553,
    duration: '4小时28分',
    isHot: true,
    image: 'https://images.unsplash.com/photo-1548919973-5cef591cdbc9?auto=format&fit=crop&q=80'
  },
  {
    id: 2,
    departure: '广州',
    arrival: '深圳',
    minPrice: 79,
    duration: '1小时20分',
    isHot: true,
    image: 'https://images.unsplash.com/photo-1583248369069-9d91f1640fe6?auto=format&fit=crop&q=80'
  },
  {
    id: 3,
    departure: '成都',
    arrival: '重庆',
    minPrice: 154,
    duration: '1小时13分',
    isHot: false,
    image: 'https://images.unsplash.com/photo-1629891820623-64974c0c20a1?auto=format&fit=crop&q=80'
  },
  {
    id: 4,
    departure: '杭州',
    arrival: '南京',
    minPrice: 119,
    duration: '1小时17分',
    isHot: false,
    image: 'https://images.unsplash.com/photo-1590625686025-a681c2d0f592?auto=format&fit=crop&q=80'
  },
  {
    id: 5,
    departure: '西安',
    arrival: '郑州',
    minPrice: 174,
    duration: '1小时30分',
    isHot: false,
    image: 'https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?auto=format&fit=crop&q=80'
  },
  {
    id: 6,
    departure: '武汉',
    arrival: '长沙',
    minPrice: 152,
    duration: '1小时20分',
    isHot: false,
    image: 'https://images.unsplash.com/photo-1563720223185-11003d516935?auto=format&fit=crop&q=80'
  }
])

const handleRouteClick = (route) => {
  router.push('/ticketSearch')
}

const handleViewMore = () => {
  router.push('/ticketSearch')
}
</script>
