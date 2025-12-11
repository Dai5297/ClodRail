<template>
  <div class="flex-1 overflow-y-auto p-6 bg-slate-50 space-y-6 custom-scrollbar" ref="messagesRef">
    <!-- Welcome Message -->
    <div v-if="messages.length === 0" class="flex gap-4 animate-fade-in">
      <div class="w-10 h-10 rounded-full bg-blue-100 text-primary flex items-center justify-center text-xl flex-shrink-0 shadow-sm border border-blue-200">
        <i class="ri-robot-2-fill"></i>
      </div>
      <div class="max-w-[80%] space-y-1">
        <div class="bg-white px-5 py-4 rounded-2xl rounded-tl-none shadow-sm text-slate-700 text-sm leading-relaxed border border-slate-100">
          <p class="mb-2 font-medium text-slate-900">您好！我是 CloudRail 智能助手 🚄</p>
          <p class="mb-2">我可以帮您解答关于铁路出行的各种问题，包括：</p>
          <ul class="space-y-1 pl-4 mb-3 list-none">
            <li class="flex items-center gap-2"><i class="ri-ticket-2-line text-blue-500"></i> 车票查询与预订</li>
            <li class="flex items-center gap-2"><i class="ri-refund-2-line text-blue-500"></i> 退票改签流程</li>
            <li class="flex items-center gap-2"><i class="ri-map-pin-line text-blue-500"></i> 车站信息查询</li>
            <li class="flex items-center gap-2"><i class="ri-notification-3-line text-blue-500"></i> 乘车注意事项</li>
          </ul>
          <p>如遇复杂问题，我也可以为您转接人工客服。请问有什么可以帮到您的吗？</p>
        </div>
        <div class="text-xs text-slate-400 pl-1">{{ getCurrentTime() }}</div>
      </div>
    </div>

    <!-- Message List -->
    <div
      v-for="message in messages"
      :key="message.id"
      class="flex gap-4 animate-slide-up"
      :class="{ 'flex-row-reverse': isUser(message) }"
    >
      <!-- Avatar -->
      <div 
        class="w-10 h-10 rounded-full flex items-center justify-center text-xl flex-shrink-0 shadow-sm border"
        :class="[
          isUser(message) 
            ? 'bg-slate-900 text-white border-slate-800' 
            : (message.type === 'human' ? 'bg-orange-100 text-orange-600 border-orange-200' : 'bg-blue-100 text-primary border-blue-200')
        ]"
      >
        <i :class="getAvatarIcon(message.type)"></i>
      </div>

      <!-- Content -->
      <div class="max-w-[80%] space-y-1" :class="{ 'items-end flex flex-col': isUser(message) }">
        <div 
          class="px-5 py-3.5 shadow-sm text-sm leading-relaxed relative break-words"
          :class="[
            isUser(message)
              ? 'bg-slate-900 text-white rounded-2xl rounded-tr-none'
              : 'bg-white text-slate-700 rounded-2xl rounded-tl-none border border-slate-100'
          ]"
        >
          <!-- System Message -->
          <div v-if="message.type === 'system'" class="text-xs text-center text-slate-500 italic bg-slate-100 px-3 py-1 rounded-full mx-auto w-fit">
            {{ message.content }}
          </div>

          <!-- Order Card -->
          <OrderMessageCard
            v-else-if="message.type === 'ORDER'"
            :order="message.content"
          />

          <!-- Text Content -->
          <div v-else class="markdown-body" v-html="formatContent(message.content)"></div>
          
          <!-- Loading Indicator -->
          <div v-if="message.streaming" class="flex gap-1 mt-2">
            <span class="w-1.5 h-1.5 bg-current rounded-full animate-bounce"></span>
            <span class="w-1.5 h-1.5 bg-current rounded-full animate-bounce delay-100"></span>
            <span class="w-1.5 h-1.5 bg-current rounded-full animate-bounce delay-200"></span>
          </div>
        </div>
        
        <div class="text-xs text-slate-400 px-1">{{ formatTime(message.timestamp) }}</div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex gap-4 animate-fade-in">
      <div class="w-10 h-10 rounded-full bg-blue-100 text-primary flex items-center justify-center text-xl flex-shrink-0 shadow-sm border border-blue-200">
        <i class="ri-robot-2-fill"></i>
      </div>
      <div class="bg-white px-5 py-4 rounded-2xl rounded-tl-none shadow-sm border border-slate-100">
        <div class="flex gap-1.5 items-center h-5">
          <span class="w-2 h-2 bg-slate-400 rounded-full animate-bounce"></span>
          <span class="w-2 h-2 bg-slate-400 rounded-full animate-bounce delay-100"></span>
          <span class="w-2 h-2 bg-slate-400 rounded-full animate-bounce delay-200"></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import OrderMessageCard from './OrderMessageCard.vue'

const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const messagesRef = ref(null)

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

watch(() => props.messages.length, scrollToBottom)
watch(() => props.loading, scrollToBottom)

const isUser = (message) => {
  return message.type === 'user' || message.role === 'user'
}

const getAvatarIcon = (type) => {
  const icons = {
    user: 'ri-user-smile-line',
    ai: 'ri-robot-2-fill',
    human: 'ri-customer-service-2-fill',
    system: 'ri-notification-3-fill'
  }
  return icons[type] || 'ri-message-3-line'
}

const formatContent = (content) => {
  if (!content) return ''
  return content
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // Simple bold support
    .replace(/```(.*?)```/gs, '<pre class="bg-slate-100 p-2 rounded my-2 text-xs overflow-x-auto"><code>$1</code></pre>')
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

const getCurrentTime = () => {
  const now = new Date()
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 3px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}

@keyframes slide-up {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-slide-up {
  animation: slide-up 0.3s ease-out forwards;
}

.markdown-body :deep(strong) {
  font-weight: 600;
  color: inherit;
}
</style>
