<template>
  <div class="bg-white rounded-xl shadow-sm p-6 mb-6 flex flex-col md:flex-row justify-between items-start md:items-center gap-4 border border-gray-100">
    <div class="flex-1">
      <h2 class="text-2xl font-bold text-gray-900 mb-2">个人信息</h2>
      <p class="text-gray-500 text-sm">查看和管理您的个人基本信息</p>
    </div>
    <div class="flex gap-3">
      <button 
        v-if="!isEditing" 
        class="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium shadow-sm hover:shadow active:scale-95"
        @click="handleStartEdit"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
        </svg>
        编辑信息
      </button>
      <div v-else class="flex gap-3">
        <button 
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium shadow-sm hover:shadow active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          @click="handleSubmit" 
          :disabled="loading"
        >
          <svg v-if="loading" class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          {{ loading ? '保存中...' : '保存修改' }}
        </button>
        <button 
          class="px-4 py-2 bg-white border border-gray-200 text-gray-700 rounded-lg hover:bg-gray-50 hover:border-gray-300 transition-colors font-medium active:scale-95"
          @click="handleCancel"
        >
          取消
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
// Props
defineProps({
  isEditing: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  }
})

// Events
const emit = defineEmits(['start-edit', 'submit', 'cancel'])

const handleStartEdit = () => {
  emit('start-edit')
}

const handleSubmit = () => {
  emit('submit')
}

const handleCancel = () => {
  emit('cancel')
}
</script>
