<template>
  <aside class="w-80 bg-white border-r border-slate-100 flex flex-col h-full">
    <div class="p-5 border-b border-slate-100">
      <h2 class="text-xl font-bold text-slate-800 mb-4 flex items-center gap-2">
        <i class="ri-chat-smile-2-line text-primary"></i> 对话历史
      </h2>
      <button 
        class="w-full py-3 px-4 bg-slate-900 hover:bg-slate-800 text-white rounded-xl font-medium shadow-lg shadow-slate-200 transition-all duration-200 flex items-center justify-center gap-2 active:scale-[0.98]" 
        @click="$emit('new-chat')"
      >
        <i class="ri-add-line text-lg"></i>
        新建对话
      </button>
    </div>
    
    <div class="flex-1 overflow-y-auto p-3 space-y-2 custom-scrollbar">
      <div
        v-for="session in sessions"
        :key="session.sessionId"
        class="group relative flex items-center gap-3 p-3 rounded-xl transition-all duration-200 cursor-pointer border border-transparent hover:bg-slate-50"
        :class="activeSessionId === session.sessionId ? 'bg-blue-50/60 border-blue-100' : ''"
        @click="$emit('select-session', session.sessionId)"
      >
        <div 
          class="w-10 h-10 rounded-lg flex items-center justify-center text-lg shadow-sm transition-colors"
          :class="activeSessionId === session.sessionId ? 'bg-white text-primary' : 'bg-slate-100 text-slate-500 group-hover:bg-white group-hover:text-primary'"
        >
          <i :class="session.type === 'human' ? 'ri-customer-service-2-line' : 'ri-robot-line'"></i>
        </div>
        
        <div class="flex-1 min-w-0">
          <div 
            class="text-sm font-bold mb-0.5 truncate transition-colors"
            :class="activeSessionId === session.sessionId ? 'text-primary' : 'text-slate-700'"
          >
            {{ session.title || '新对话' }}
          </div>
          <div class="text-xs text-slate-400 truncate">{{ session.lastMessage || '暂无消息' }}</div>
        </div>
        
        <div class="flex flex-col items-end justify-between self-stretch py-1">
           <span class="text-[10px] text-slate-300">{{ formatTime(session.updateTime) }}</span>
           
           <button 
            class="w-6 h-6 rounded flex items-center justify-center text-slate-400 hover:text-red-500 hover:bg-red-50 transition-all opacity-0 group-hover:opacity-100" 
            @click.stop="$emit('delete-session', session.sessionId)"
            title="删除对话"
          >
            <i class="ri-delete-bin-line text-sm"></i>
          </button>
        </div>
      </div>
      
      <div v-if="sessions.length === 0" class="flex flex-col items-center justify-center py-12 text-slate-400">
        <i class="ri-chat-3-line text-4xl mb-3 opacity-20"></i>
        <p class="text-sm">暂无对话历史</p>
      </div>
    </div>
  </aside>
</template>

<script setup>
defineProps({
  sessions: {
    type: Array,
    default: () => []
  },
  activeSessionId: {
    type: String,
    default: null
  }
})

defineEmits(['new-chat', 'select-session', 'delete-session'])

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now - time
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}m`
  if (hours < 24) return `${hours}h`
  if (days === 1) return '昨天'
  if (days < 7) return `${days}d`
  
  return `${time.getMonth() + 1}/${time.getDate()}`
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #f1f5f9;
  border-radius: 2px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}
</style>
