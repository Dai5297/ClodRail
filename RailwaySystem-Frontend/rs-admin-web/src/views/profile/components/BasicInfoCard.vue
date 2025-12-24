<template>
  <el-card class="info-card">
    <template #header>
      <div class="card-header">
        <span>{{ $t('profile.basicInfo') }}</span>
        <el-button 
          v-if="!isEditing" 
          type="primary" 
          size="small" 
          @click="startEdit"
        >
          {{ $t('profile.info.edit') }}
        </el-button>
        <div v-else class="edit-actions">
          <el-button size="small" @click="cancelEdit">{{ $t('profile.info.cancel') }}</el-button>
          <el-button type="primary" size="small" @click="saveEdit">{{ $t('profile.info.save') }}</el-button>
        </div>
      </div>
    </template>

    <el-form :model="editForm" label-width="100px" class="info-form">
      <el-form-item :label="$t('profile.info.username')">
        <span class="info-value">{{ userInfo.username }}</span>
      </el-form-item>

      <el-form-item :label="$t('profile.info.realName')">
        <el-input 
          v-if="isEditing" 
          v-model="editForm.realName" 
          :placeholder="$t('profile.info.realName')"
        />
        <span v-else class="info-value">{{ userInfo.realName || '-' }}</span>
      </el-form-item>

      <el-form-item :label="$t('profile.info.idCard')">
        <el-input 
          v-if="isEditing" 
          v-model="editForm.idCard" 
          :placeholder="$t('profile.info.idCard')"
          maxlength="18"
        />
        <span v-else class="info-value">{{ userInfo.idCard || '-' }}</span>
      </el-form-item>

      <el-form-item :label="$t('profile.info.role')">
        <span class="info-value">{{ userInfo.roleName || '-' }}</span>
      </el-form-item>



      <el-form-item :label="$t('profile.info.status')">
        <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
          {{ userInfo.status === 1 ? $t('profile.info.statusActive') : $t('profile.info.statusDisabled') }}
        </el-tag>
      </el-form-item>

      <el-form-item :label="$t('profile.info.createTime')">
        <span class="info-value">{{ formatDate(userInfo.createTime) }}</span>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { updateProfile } from '@/api/profile'

const { t } = useI18n()

const props = defineProps({
  userInfo: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['info-updated'])

const isEditing = ref(false)
const editForm = reactive({
  realName: '',
  idCard: ''
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  if (isNaN(date.getTime())) return '-'
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 开始编辑
const startEdit = () => {
  isEditing.value = true
  editForm.realName = props.userInfo.realName
  editForm.idCard = props.userInfo.idCard
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  editForm.realName = props.userInfo.realName
  editForm.idCard = props.userInfo.idCard
}

// 保存编辑
const saveEdit = async () => {
  try {
    // 调用更新接口
    await updateProfile({ 
      realName: editForm.realName,
      idCard: editForm.idCard
    })
    
    emit('info-updated', { 
      realName: editForm.realName,
      idCard: editForm.idCard
    })
    isEditing.value = false
    ElMessage.success(t('profile.info.saveSuccess'))
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(t('profile.info.saveError'))
  }
}
</script>

<style scoped>
.info-card {
  background: var(--card-bg);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: var(--text-color);
}

.edit-actions {
  display: flex;
  gap: 8px;
}

.info-form {
  padding: 12px 0;
}

.info-value {
  color: var(--text-color);
  font-size: 14px;
}

:deep(.el-card__header) {
  background: var(--bg-color-secondary);
  border-bottom: 1px solid var(--border-color);
}

:deep(.el-form-item__label) {
  color: var(--text-color-secondary);
}

/* Button Styles */
:deep(.el-button--primary) {
  background-color: #000000;
  border-color: #000000;
  color: #ffffff;
}

:deep(.el-button--primary:hover) {
  background-color: #333333;
  border-color: #333333;
}

/* Dark mode overrides */
:global(html.dark) .info-card :deep(.el-button--primary) {
  background-color: #ffffff;
  border-color: #ffffff;
  color: #000000;
}

:global(html.dark) .info-card :deep(.el-button--primary:hover) {
  background-color: #e5e5e5;
  border-color: #e5e5e5;
}
</style>
