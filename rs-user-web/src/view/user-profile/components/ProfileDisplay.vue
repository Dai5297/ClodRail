<template>
  <div class="profile-display">
    <div class="info-section">
      <h3 class="section-title">基本信息</h3>
      <div class="info-grid">
        <div class="info-item">
          <label class="info-label">用户名</label>
          <div class="info-value">{{ userInfo.username || '-' }}</div>
        </div>
        <div class="info-item">
          <label class="info-label">真实姓名</label>
          <div class="info-value">{{ userInfo.realName || '-' }}</div>
        </div>
        <div class="info-item">
          <label class="info-label">手机号</label>
          <div class="info-value">{{ userInfo.phone || '-' }}</div>
        </div>
        <div class="info-item">
          <label class="info-label">邮箱</label>
          <div class="info-value">{{ userInfo.email || '-' }}</div>
        </div>
        <div class="info-item">
          <label class="info-label">性别</label>
          <div class="info-value">{{ userInfo.gender || '-' }}</div>
        </div>
        <div class="info-item">
          <label class="info-label">生日</label>
          <div class="info-value">{{ userInfo.birthday || '-' }}</div>
        </div>
        <div class="info-item">
          <label class="info-label">身份证号</label>
          <div class="info-value">{{ formatIdCard(userInfo.idCard) || '-' }}</div>
        </div>
        <div class="info-item">
          <label class="info-label">地址</label>
          <div class="info-value">{{ userInfo.address || '-' }}</div>
        </div>
      </div>
    </div>
    
    <div v-if="userInfo.introduction" class="info-section">
      <h3 class="section-title">个人简介</h3>
      <div class="introduction-content">
        {{ userInfo.introduction }}
      </div>
    </div>
  </div>
</template>

<script setup>
// Props
defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

// 格式化身份证号（隐藏中间部分）
const formatIdCard = (idCard) => {
  if (!idCard) return ''
  if (idCard.length === 18) {
    return idCard.substring(0, 6) + '********' + idCard.substring(14)
  }
  return idCard
}
</script>

<style scoped>
.profile-display {
  padding: 24px;
}

.info-section {
  margin-bottom: 32px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
}

.info-value {
  font-size: 16px;
  color: #1f2937;
  padding: 8px 0;
  border-bottom: 1px solid #f3f4f6;
  min-height: 24px;
}

.introduction-content {
  padding: 16px;
  background: #f9fafb;
  border-radius: 6px;
  border-left: 4px solid #3b82f6;
  color: #374151;
  line-height: 1.6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-display {
    padding: 16px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>