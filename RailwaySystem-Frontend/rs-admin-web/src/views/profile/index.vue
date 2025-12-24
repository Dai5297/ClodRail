<template>
  <div class="profile-container">
    <div class="profile-header">
      <h2>{{ $t('profile.title') }}</h2>
    </div>

    <div class="profile-content">
      <!-- 左侧头像区域 -->
      <div class="avatar-wrapper">
        <AvatarCard 
          :user-info="userInfo" 
          @avatar-updated="handleAvatarUpdate"
        />
      </div>

      <!-- 右侧信息区域 -->
      <div class="info-section">
        <!-- 基本信息卡片 -->
        <BasicInfoCard 
          :user-info="userInfo"
          @info-updated="handleInfoUpdate"
        />

        <!-- 安全设置卡片 -->
        <SecurityCard />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminInfo as getLocalAdminInfo, setAdminInfo } from '@/utils/auth'
import { getAdminInfo } from '@/api/profile'
import AvatarCard from './components/AvatarCard.vue'
import BasicInfoCard from './components/BasicInfoCard.vue'
import SecurityCard from './components/SecurityCard.vue'

const userInfo = ref({
  id: null,
  username: '',
  realName: '',
  idCard: '',
  icon: '',
  role: null,
  roleName: '',
  status: 1,
  createTime: ''
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await getAdminInfo()
    if (res.success) {
      userInfo.value = { ...res.data }
      // 更新本地存储
      setAdminInfo(res.data)
    }
  } catch (error) {
    console.error('Failed to load user info from API:', error)
    // 降级使用本地存储
    const localInfo = getLocalAdminInfo()
    if (localInfo) {
      userInfo.value = { ...localInfo }
    }
  }
}

// 处理头像更新
const handleAvatarUpdate = (newAvatarUrl) => {
  // 更新用户信息中的头像
  userInfo.value.icon = newAvatarUrl
  // 更新localStorage中的用户信息
  setAdminInfo(userInfo.value)
  // 触发 storage 事件，通知其他组件（如 Navbar）更新
  window.dispatchEvent(new StorageEvent('storage', {
    key: 'admin_info',
    newValue: JSON.stringify(userInfo.value)
  }))
}

// 处理信息更新
const handleInfoUpdate = (updatedData) => {
  Object.assign(userInfo.value, updatedData)
  // 更新localStorage中的用户信息
  setAdminInfo(userInfo.value)
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  margin-bottom: 24px;
}

.profile-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.avatar-wrapper {
  width: 100%;
}

.info-section {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

@media (max-width: 768px) {
  .profile-content {
    grid-template-columns: 1fr;
  }
}
</style>
