<template>
  <el-dialog
      v-model="visible"
      title="选择乘客"
      width="600px"
      :close-on-click-modal="false"
      @close="handleClose"
  >
    <div class="contact-modal">
      <div class="contact-list">
        <div
            v-for="contact in contacts"
            :key="contact.id"
            class="contact-item"
            :class="{ 'selected': isContactSelected(contact) }"
            @click="handleToggleContact(contact)"
        >
          <div class="contact-info">
            <div class="contact-name">
              {{ contact.name }}
              <span class="contact-type-tag">
                {{ getPassengerTypeText(contact.passengerType) }}
              </span>
            </div>
            <div class="contact-details">
              <span class="contact-phone">{{ contact.phone }}</span>
            </div>
          </div>
          <div class="contact-actions">
            <el-checkbox
                :model-value="isContactSelected(contact)"
                @change="handleToggleContact(contact)"
            />
          </div>
        </div>
      </div>

      <div v-if="contacts.length === 0" class="no-contacts">
        <el-empty description="暂无联系人，请先添加联系人信息"/>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button
            type="primary"
            @click="handleConfirm"
            :disabled="tempSelectedContacts.length === 0"
        >
          确认选择 ({{ tempSelectedContacts.length }})
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

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
.contact-modal {
  max-height: 400px;
  overflow-y: auto;
}

.contact-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.contact-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.contact-item:hover {
  border-color: #1890ff;
  background-color: #f0f9ff;
}

.contact-item.selected {
  border-color: #1890ff;
  background-color: #f0f9ff;
}

.contact-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-type-tag {
  font-size: 12px;
  font-weight: 400;
  color: #1890ff;
  background-color: #f0f9ff;
  border: 1px solid #d4edda;
  border-radius: 12px;
  padding: 2px 8px;
}

.contact-details {
  display: flex;
  gap: 12px;
  font-size: 14px;
  color: #666;
}

.no-contacts {
  padding: 40px 0;
  text-align: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>

