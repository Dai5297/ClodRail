<template>
  <header class="flex items-center justify-between px-6 py-4 bg-white/80 backdrop-blur-sm border-b border-slate-100 z-10">
    <div class="flex items-center gap-4">
      <div class="relative">
        <div class="w-12 h-12 rounded-2xl flex items-center justify-center text-2xl shadow-sm border border-slate-100"
             :class="isHumanService ? 'bg-orange-50 text-orange-500' : 'bg-blue-50 text-primary'">
          <i :class="isHumanService ? 'ri-customer-service-2-fill' : 'ri-robot-2-fill'"></i>
        </div>
        <span class="absolute -bottom-1 -right-1 w-3.5 h-3.5 rounded-full border-2 border-white shadow-sm"
              :class="isOnline ? 'bg-emerald-500' : 'bg-slate-300'"></span>
      </div>
      
      <div class="flex flex-col">
        <h3 class="text-base font-bold text-slate-800 flex items-center gap-2">
          {{ chatName }}
          <span v-if="!isOnline" class="px-1.5 py-0.5 rounded text-[10px] bg-slate-100 text-slate-400 font-medium">离线</span>
        </h3>
        <p class="text-xs text-slate-500">{{ chatStatus }}</p>
      </div>
    </div>
    
    <div class="flex items-center gap-2">
      <button
        v-if="!isHumanService"
        class="px-4 py-2 bg-white border border-slate-200 hover:border-blue-200 hover:text-primary hover:bg-blue-50 text-slate-600 rounded-xl text-sm font-medium transition-all flex items-center gap-2"
        @click="$emit('transfer')"
      >
        <i class="ri-customer-service-2-line"></i>
        转人工
      </button>
      
      <div 
        v-if="isHumanService && remainingTime" 
        class="px-3 py-1.5 bg-orange-50 text-orange-600 rounded-lg text-xs font-mono font-medium flex items-center gap-1.5"
      >
        <i class="ri-timer-line"></i>
        {{ formatRemainingTime }}
      </div>
      
      <button
        v-if="isHumanService"
        class="px-4 py-2 bg-orange-50 text-orange-600 hover:bg-orange-100 rounded-xl text-sm font-medium transition-all flex items-center gap-2"
        @click="$emit('end-service')"
      >
        <i class="ri-shut-down-line"></i>
        结束服务
      </button>
      
      <div class="w-px h-6 bg-slate-200 mx-2"></div>
      
      <button
        class="w-9 h-9 rounded-lg flex items-center justify-center text-slate-400 hover:text-red-500 hover:bg-red-50 transition-all"
        @click="$emit('clear')"
        title="清空记录"
      >
        <i class="ri-delete-bin-line text-lg"></i>
      </button>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  isHumanService: {
    type: Boolean,
    default: false
  },
  serviceName: {
    type: String,
    default: ''
  },
  isOnline: {
    type: Boolean,
    default: true
  },
  remainingTime: {
    type: Number,
    default: 0
  }
})

defineEmits(['transfer', 'clear', 'end-service'])

// 聊天名称
const chatName = computed(() => {
  return props.isHumanService 
    ? `人工客服 - ${props.serviceName || '在线客服'}` 
    : 'CloudRail 智能助手'
})

// 聊天状态
const chatStatus = computed(() => {
  if (props.isHumanService) {
    return props.isOnline ? '正在为您服务' : '客服已离线'
  }
  return '24小时在线为您服务'
})

// 格式化剩余时间
const formatRemainingTime = computed(() => {
  const seconds = Math.floor(props.remainingTime / 1000)
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
})
</script>
