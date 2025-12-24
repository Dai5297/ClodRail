<template>
  <div class="py-24 bg-white relative z-10">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center max-w-3xl mx-auto mb-16">
        <h2 class="text-3xl font-bold text-slate-900 mb-4">为您提供卓越的出行体验</h2>
        <p class="text-slate-500">不仅仅是购票，更是对美好旅程的承诺。</p>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <div v-for="(feature, index) in featureList" :key="index" class="group p-8 rounded-3xl bg-slate-50 hover:bg-white border border-transparent hover:border-slate-100 hover:shadow-soft transition-all duration-300 cursor-pointer" @click="handleFeatureClick(feature)">
          <div class="w-14 h-14 rounded-2xl bg-white border border-slate-100 shadow-sm flex items-center justify-center text-2xl mb-6 group-hover:scale-110 group-hover:rotate-3 transition-transform duration-300" :class="feature.iconColor">
            <i :class="feature.icon"></i>
          </div>
          <h3 class="text-lg font-bold text-slate-900 mb-3">{{ feature.title }}</h3>
          <p class="text-slate-500 text-sm leading-relaxed mb-6">{{ feature.description }}</p>
          
          <div class="flex items-center text-sm font-medium text-primary opacity-0 group-hover:opacity-100 transition-opacity transform translate-y-2 group-hover:translate-y-0 duration-300">
            了解更多 <i class="ri-arrow-right-line ml-1"></i>
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

const featureList = ref([
  {
    id: 'fast-booking',
    title: '智能购票',
    description: '全国铁路实时票务查询，智能推荐最佳出行方案，让您的每次出行都更便捷',
    icon: 'ri-ticket-2-line',
    iconColor: 'text-blue-600'
  },
  {
    id: 'real-time-info',
    title: '实时信息',
    description: '列车时刻表、余票信息、晚点通知实时更新，出行信息一手掌握',
    icon: 'ri-time-line',
    iconColor: 'text-emerald-500'
  },
  {
    id: 'contact-management',
    title: '联系人管理',
    description: '便捷管理常用联系人信息，支持批量购票，家庭出行更轻松',
    icon: 'ri-contacts-book-line',
    iconColor: 'text-purple-600'
  },
  {
    id: 'security',
    title: '安全保障',
    description: '银行级安全防护，多重身份验证，保障您的个人信息和资金安全',
    icon: 'ri-shield-check-line',
    iconColor: 'text-orange-500'
  },
  {
    id: 'quick-refund',
    title: '便民服务',
    description: '在线退票改签、电子发票、座位选择等便民服务，让出行更贴心',
    icon: 'ri-customer-service-2-line',
    iconColor: 'text-teal-500'
  },
  {
    id: 'points-system',
    title: '积分权益',
    description: '出行积分累积，会员等级升级，专享优惠折扣和贴心服务',
    icon: 'ri-gift-line',
    iconColor: 'text-pink-500'
  }
])

const handleFeatureClick = (feature) => {
  switch (feature.id) {
    case 'fast-booking':
    case 'real-time-info':
      router.push('/ticket')
      break
    case 'contact-management':
      router.push('/user/contact')
      break
    case 'security':
      router.push('/user/security')
      break
    case 'quick-refund':
      router.push('/user/order')
      break
    case 'points-system':
      router.push('/user/point')
      break
    default:
  }
}
</script>
