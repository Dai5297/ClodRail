<template>
  <div class="page-header">
    <div class="header-info">
      <h2 class="page-title">个人信息</h2>
      <p class="page-desc">查看和管理您的个人基本信息</p>
    </div>
    <div class="page-actions">
      <el-button v-if="!isEditing" type="primary" @click="handleStartEdit">
        <el-icon><Edit /></el-icon>
        编辑信息
      </el-button>
      <div v-else class="edit-actions">
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          保存修改
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Edit } from '@element-plus/icons-vue'

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

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-info {
  flex: 1;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
}

.page-desc {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.page-actions {
  display: flex;
  gap: 12px;
}

.edit-actions {
  display: flex;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .page-actions {
    justify-content: flex-end;
  }
}
</style>