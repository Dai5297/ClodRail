<template>
  <div class="bg-white rounded-xl shadow-sm border border-slate-200 hover:shadow-md transition-all duration-300 overflow-hidden group">
    <div class="p-5 border-b border-slate-100 flex justify-between items-start bg-slate-50/50">
      <div class="flex items-center gap-3">
        <div class="h-10 w-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold text-lg">
          {{ contact.name ? contact.name.charAt(0) : '' }}
        </div>
        <div>
          <div class="flex items-center gap-2">
            <span class="font-bold text-slate-800 text-lg">{{ contact.name }}</span>
            <span 
              v-if="contact.isDefault === 1" 
              class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-800"
            >
              默认
            </span>
          </div>
          <div class="mt-1">
            <span 
              :class="[
                'inline-flex items-center px-2 py-0.5 rounded text-xs font-medium',
                contact.status === 1 ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
              ]"
            >
              {{ contact.status === 1 ? '启用' : '禁用' }}
            </span>
          </div>
        </div>
      </div>
      <div class="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
        <button 
          @click="handleEdit"
          class="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors"
          title="编辑"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
          </svg>
        </button>
        <button 
          @click="handleDelete"
          class="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors"
          title="删除"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
          </svg>
        </button>
      </div>
    </div>
    
    <div class="p-5 space-y-3">
      <div class="flex justify-between items-center text-sm">
        <span class="text-slate-500">乘客类型</span>
        <span class="font-medium text-slate-700">{{ formatPassengerType(contact.passengerType) }}</span>
      </div>
      <div class="flex justify-between items-center text-sm">
        <span class="text-slate-500">身份证号</span>
        <span class="font-medium text-slate-700 font-mono">{{ formatIdCard(contact.idCard) }}</span>
      </div>
      <div class="flex justify-between items-center text-sm">
        <span class="text-slate-500">手机号码</span>
        <span class="font-medium text-slate-700 font-mono">{{ contact.phone }}</span>
      </div>
      <div v-if="contact.email" class="flex justify-between items-center text-sm">
        <span class="text-slate-500">邮箱地址</span>
        <span class="font-medium text-slate-700 truncate max-w-[180px]" :title="contact.email">{{ contact.email }}</span>
      </div>
      <div v-if="contact.remark" class="pt-2 border-t border-slate-100 mt-2">
        <p class="text-xs text-slate-500 mb-1">备注信息</p>
        <p class="text-sm text-slate-600 line-clamp-2">{{ contact.remark }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  contact: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['edit', 'delete'])

const formatIdCard = (idCard) => {
  if (!idCard) return ''
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
}

const formatPassengerType = (type) => {
  const typeMap = {
    1: '成人',
    2: '儿童',
    3: '学生',
    4: '老人'
  }
  return typeMap[type] || '未知'
}

const handleEdit = () => {
  emit('edit', props.contact)
}

const handleDelete = () => {
  emit('delete', props.contact)
}
</script>

<style scoped>
.contact-card {
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.contact-card:hover {
  transform: translateY(-2px);
}

.contact-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.contact-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.name {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.default-tag {
  margin-left: 4px;
}

.contact-actions {
  display: flex;
  gap: 8px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  display: flex;
  align-items: center;
}

.label {
  font-size: 14px;
  color: #8c8c8c;
  width: 80px;
  flex-shrink: 0;
}

.value {
  font-size: 14px;
  color: #262626;
  flex: 1;
}
</style>