<template>
  <el-card class="contact-card" shadow="hover">
    <div class="contact-header">
      <div class="contact-name">
        <span class="name">{{ contact.name }}</span>
        <el-tag 
          v-if="contact.isDefault === 1" 
          type="success" 
          size="small"
          class="default-tag"
        >
          默认
        </el-tag>
        <el-tag 
          :type="contact.status === 1 ? 'success' : 'danger'" 
          size="small"
        >
          {{ contact.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </div>
      <div class="contact-actions">
        <el-button 
          type="primary" 
          size="small" 
          text
          @click="handleEdit"
        >
          <el-icon><Edit /></el-icon>
          编辑
        </el-button>
        <el-button 
          type="danger" 
          size="small" 
          text
          @click="handleDelete"
        >
          <el-icon><Delete /></el-icon>
          删除
        </el-button>
      </div>
    </div>
    
    <div class="contact-info">
      <div class="info-row">
        <span class="label">乘客类型：</span>
        <span class="value">{{ formatPassengerType(contact.passengerType) }}</span>
      </div>
      <div class="info-row">
        <span class="label">身份证号：</span>
        <span class="value">{{ formatIdCard(contact.idCard) }}</span>
      </div>
      <div class="info-row">
        <span class="label">手机号码：</span>
        <span class="value">{{ contact.phone }}</span>
      </div>
      <div class="info-row" v-if="contact.email">
        <span class="label">邮箱地址：</span>
        <span class="value">{{ contact.email }}</span>
      </div>
      <div class="info-row" v-if="contact.remark">
        <span class="label">备注信息：</span>
        <span class="value">{{ contact.remark }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { Edit, Delete } from '@element-plus/icons-vue'

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