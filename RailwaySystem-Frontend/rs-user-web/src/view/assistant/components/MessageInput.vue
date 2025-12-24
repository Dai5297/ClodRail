<template>
  <div class="px-6 pb-6 pt-2 bg-white">
    <div class="bg-white border border-slate-200 shadow-sm rounded-2xl focus-within:ring-2 focus-within:ring-blue-100 focus-within:border-blue-400 transition-all duration-300 relative">
      <!-- Toolbar -->
      <div class="flex items-center gap-1 p-2 border-b border-slate-50">
        <button 
          class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:text-blue-500 hover:bg-blue-50 transition-all"
          @click="$emit('emoji')" 
          title="表情"
        >
          <i class="ri-emotion-line text-lg"></i>
        </button>
        <button 
          class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:text-blue-500 hover:bg-blue-50 transition-all"
          @click="$emit('image')" 
          title="上传图片"
        >
          <i class="ri-image-add-line text-lg"></i>
        </button>
        <button 
          class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:text-blue-500 hover:bg-blue-50 transition-all"
          @click="$emit('order')" 
          title="发送订单"
        >
          <i class="ri-ticket-2-line text-lg"></i>
        </button>
        <div class="w-px h-4 bg-slate-200 mx-1"></div>
        <button 
          class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:text-blue-500 hover:bg-blue-50 transition-all"
          @click="$emit('voice')" 
          title="语音输入"
        >
          <i class="ri-mic-line text-lg"></i>
        </button>
      </div>

      <!-- Input Area -->
      <div class="p-3 relative">
        <textarea
          v-model="inputMessage"
          rows="3"
          class="w-full resize-none border-none outline-none bg-transparent text-slate-700 text-sm leading-relaxed placeholder:text-slate-400 custom-scrollbar pr-12"
          placeholder="请输入您的问题... (Shift + Enter 换行)"
          @keydown="handleKeyDown"
          :disabled="disabled"
        ></textarea>

        <!-- Send Button -->
        <button
          class="absolute bottom-3 right-3 w-10 h-10 rounded-xl flex items-center justify-center transition-all duration-300 shadow-md hover:shadow-lg disabled:shadow-none disabled:cursor-not-allowed group"
          :class="[
            inputMessage.trim() && !disabled 
              ? 'bg-slate-900 text-white hover:bg-slate-800 hover:scale-105' 
              : 'bg-slate-100 text-slate-300'
          ]"
          @click="handleSend"
          :disabled="!inputMessage.trim() || disabled || sending"
        >
          <i v-if="!sending" class="ri-send-plane-fill text-lg group-hover:translate-x-0.5 group-hover:-translate-y-0.5 transition-transform"></i>
          <i v-else class="ri-loader-4-line animate-spin text-lg"></i>
        </button>
      </div>
    </div>
    
    <div class="text-center mt-3 text-xs text-slate-300">
      AI 内容由大模型生成，请仔细甄别
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  },
  sending: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['send', 'emoji', 'image', 'voice', 'order'])

const inputMessage = ref('')

// 发送消息
const handleSend = () => {
  const message = inputMessage.value.trim()
  if (message && !props.disabled) {
    emit('send', message)
    inputMessage.value = ''
  }
}

// 处理键盘事件
const handleKeyDown = (event) => {
  // Enter 发送，Shift+Enter 换行
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    handleSend()
  }
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
  background: #e2e8f0;
  border-radius: 2px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}
</style>
