<template>
  <el-card class="avatar-card">
    <div class="avatar-section">
      <el-avatar :size="100" :src="avatarUrl" />
      <div class="avatar-info">
        <h3>{{ userInfo.realName || userInfo.username }}</h3>
        <p class="role-tag">{{ userInfo.roleName }}</p>
      </div>
      <div class="avatar-actions">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          :http-request="handleAvatarUpload"
        >
          <el-button type="primary" size="small">{{ $t('profile.avatar.change') }}</el-button>
        </el-upload>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import {uploadAvatar} from "@/api/profile.js";

const { t } = useI18n()

const props = defineProps({
  userInfo: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['avatar-updated'])

const defaultAvatar = '/default-avatar.png'

// 计算属性：头像URL，确保响应式更新
const avatarUrl = computed(() => {
  return props.userInfo.icon || defaultAvatar
})

// 上传头像前的验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error(t('profile.avatar.typeError'))
    return false
  }
  if (!isLt2M) {
    ElMessage.error(t('profile.avatar.sizeError'))
    return false
  }
  return true
}

// 处理头像上传
const handleAvatarUpload = async (options) => {
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    const response = await uploadAvatar(formData)
    
    // 发送事件通知父组件更新头像
    emit('avatar-updated', response.data)
    
    ElMessage.success(t('profile.avatar.uploadSuccess'))
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error(t('profile.avatar.uploadError'))
  }
}
</script>

<style scoped>
.avatar-card {
  margin-bottom: 24px;
}

.avatar-section {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  gap: 24px;
}

.avatar-section .el-avatar {
  border: 3px solid var(--border-color);
  flex-shrink: 0;
}

.avatar-info {
  flex: 1;
  text-align: left;
}

.avatar-info h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color);
}

.role-tag {
  margin: 0;
  padding: 4px 12px;
  background: var(--bg-color-secondary);
  color: var(--text-color-secondary);
  border-radius: 12px;
  font-size: 13px;
  display: inline-block;
}

.avatar-actions {
  margin-left: auto;
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
:global(html.dark) .avatar-card :deep(.el-button--primary) {
  background-color: #ffffff;
  border-color: #ffffff;
  color: #000000;
}

:global(html.dark) .avatar-card :deep(.el-button--primary:hover) {
  background-color: #e5e5e5;
  border-color: #e5e5e5;
}
</style>
