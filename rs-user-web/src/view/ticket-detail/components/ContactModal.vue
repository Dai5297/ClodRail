<template>
  <Modal
      v-model="visible"
      title="选择乘客"
  >
    <div class="max-h-[400px] overflow-y-auto">
      <div class="flex flex-col gap-2">
        <div
            v-for="contact in contacts"
            :key="contact.id"
            class="flex justify-between items-center p-4 border rounded-lg cursor-pointer transition-all duration-300"
            :class="{
              'border-blue-500 bg-blue-50/50': isContactSelected(contact),
              'border-gray-100 hover:border-blue-300 hover:bg-gray-50': !isContactSelected(contact)
            }"
            @click="handleToggleContact(contact)"
        >
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-1">
              <span class="text-base font-semibold text-gray-900">{{ contact.name }}</span>
              <span class="text-xs font-medium text-blue-600 bg-blue-50 border border-blue-100 rounded-full px-2 py-0.5">
                {{ getPassengerTypeText(contact.passengerType) }}
              </span>
            </div>
            <div class="flex gap-3 text-sm text-gray-500">
              <span>{{ contact.phone }}</span>
            </div>
          </div>
          <div class="ml-4">
            <div class="w-5 h-5 border-2 rounded flex items-center justify-center transition-colors"
                 :class="isContactSelected(contact) ? 'bg-blue-600 border-blue-600' : 'border-gray-300 bg-white'">
              <svg v-if="isContactSelected(contact)" class="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"></path></svg>
            </div>
          </div>
        </div>
      </div>

      <div v-if="contacts.length === 0" class="py-10 text-center">
        <div class="bg-gray-50 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-3">
          <svg class="w-8 h-8 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
        </div>
        <p class="text-gray-500 text-sm">暂无联系人，请先添加联系人信息</p>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-3">
        <button
            @click="handleClose"
            class="px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          取消
        </button>
        <button
            @click="handleConfirm"
            :disabled="tempSelectedContacts.length === 0"
            class="px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          确认选择 ({{ tempSelectedContacts.length }})
        </button>
      </div>
    </template>
  </Modal>
</template>

<script setup>
import { ref, watch } from 'vue'
import Modal from '@/components/Modal/Modal.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  contacts: {
    type: Array,
    required: true
  },
  selectedSeatType: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const visible = ref(props.modelValue)
const tempSelectedContacts = ref([])

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    tempSelectedContacts.value = []
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 检查联系人是否已选择
const isContactSelected = (contact) => {
  return tempSelectedContacts.value.some(c => c.id === contact.id)
}

// 切换联系人选择状态
const handleToggleContact = (contact) => {
  const index = tempSelectedContacts.value.findIndex(c => c.id === contact.id)
  if (index > -1) {
    tempSelectedContacts.value.splice(index, 1)
  } else {
    // 检查是否超过座位数量限制
    if (props.selectedSeatType &&
        tempSelectedContacts.value.length >= props.selectedSeatType.remainingSeats) {
      return
    }
    tempSelectedContacts.value.push(contact)
  }
}

// 获取乘客类型文本
const getPassengerTypeText = (type) => {
  const typeMap = {
    1: '成人',
    2: '儿童', 
    3: '学生',
    4: '老人'
  }
  return typeMap[type] || '未知'
}

const handleClose = () => {
  visible.value = false
}

const handleConfirm = () => {
  emit('confirm', [...tempSelectedContacts.value])
  visible.value = false
}
</script>

