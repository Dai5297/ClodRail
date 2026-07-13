<template>
  <Modal v-model="visible" title="选择乘车人">
    <div class="mb-4 rounded-lg border border-primary/20 bg-[color-mix(in_srgb,var(--rail-primary)_5%,var(--rail-surface))] px-4 py-3">
      <p class="text-sm font-semibold text-navy">为本次行程添加乘车人</p>
      <p class="mt-1 text-xs leading-5 text-secondary">
        当前席别：{{ selectedSeatType?.name || '未选择' }}，最多可按该席别余票数量选择。
      </p>
    </div>

    <div class="max-h-[420px] overflow-y-auto pr-1">
      <div v-if="contacts.length" class="space-y-2">
        <div
          v-for="contact in contacts"
          :key="contact.id"
          class="flex cursor-pointer items-center justify-between gap-4 rounded-lg border p-4 transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-1"
          :class="isContactSelected(contact) ? 'border-primary bg-[color-mix(in_srgb,var(--rail-primary)_5%,var(--rail-surface))]' : 'border-line bg-white hover:border-primary-hover hover:bg-[color-mix(in_srgb,var(--rail-primary)_3%,var(--rail-surface))]'"
          role="checkbox"
          tabindex="0"
          :aria-checked="isContactSelected(contact)"
          @click="handleToggleContact(contact)"
          @keydown.enter="handleToggleContact(contact)"
          @keydown.space.prevent="handleToggleContact(contact)"
        >
          <div class="min-w-0 flex-1">
            <div class="flex flex-wrap items-center gap-2">
              <span class="font-bold text-ink">{{ contact.name }}</span>
              <span class="rounded bg-primary-soft px-2 py-0.5 text-xs font-semibold text-primary-active">{{ getPassengerTypeText(contact.passengerType) }}</span>
            </div>
            <p class="data-number mt-1 truncate text-sm text-secondary">{{ contact.phone || '未填写联系电话' }}</p>
          </div>
          <span
            class="flex h-6 w-6 shrink-0 items-center justify-center rounded-md border-2 transition-colors"
            :class="isContactSelected(contact) ? 'border-primary bg-primary' : 'border-secondary/40 bg-white'"
            aria-hidden="true"
          >
            <svg v-if="isContactSelected(contact)" class="h-3.5 w-3.5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" /></svg>
          </span>
        </div>
      </div>

      <div v-else class="rounded-lg border border-dashed border-line bg-[color-mix(in_srgb,var(--rail-primary)_3%,var(--rail-surface))] px-5 py-10 text-center">
        <svg class="mx-auto h-9 w-9 text-secondary/60" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" /></svg>
        <p class="mt-3 font-semibold text-secondary">暂无常用联系人</p>
        <p class="mt-1 text-sm text-secondary/80">请先在个人中心添加联系人信息</p>
      </div>
    </div>

    <template #footer>
      <div class="flex flex-col-reverse gap-3 border-t border-line pt-4 sm:flex-row sm:items-center sm:justify-between">
        <p class="text-xs text-secondary">已选择 <span class="data-number font-bold text-primary">{{ tempSelectedContacts.length }}</span> 人</p>
        <div class="flex gap-3">
          <button
            type="button"
            class="flex-1 rounded-lg border border-line bg-white px-4 py-2.5 text-sm font-semibold text-secondary transition-colors hover:border-primary hover:text-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary sm:flex-none"
            @click="handleClose"
          >
            取消
          </button>
          <button
            type="button"
            class="flex-1 rounded-lg bg-primary px-4 py-2.5 text-sm font-bold text-white transition-colors hover:bg-primary-active focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:bg-secondary/40 sm:flex-none"
            :disabled="tempSelectedContacts.length === 0"
            @click="handleConfirm"
          >
            确认选择（{{ tempSelectedContacts.length }}）
          </button>
        </div>
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

<style scoped>
.data-number {
  font-family: "DIN Alternate", "Arial Narrow", Arial, sans-serif;
  font-variant-numeric: tabular-nums;
}
</style>
