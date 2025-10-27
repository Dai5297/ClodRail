<template>
  <div class="user-profile-page">
    <!-- 页面标题 -->
    <PageHeader
      :is-editing="isEditing"
      :loading="loading"
      @start-edit="startEdit"
      @submit="handleSubmit"
      @cancel="cancelEdit"
    />

    <!-- 个人信息展示/编辑 -->
    <div class="profile-content">
      <!-- 信息展示模式 -->
      <ProfileDisplay v-if="!isEditing" :user-info="userInfo" />

      <!-- 编辑模式 -->
      <ProfileForm
        v-else
        ref="profileFormRef"
        :form-data="profileForm"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUserInfo } from '@/api/user.js'
import PageHeader from '@/view/user-profile/components/PageHeader.vue'
import ProfileDisplay from '@/view/user-profile/components/ProfileDisplay.vue'
import ProfileForm from '@/view/user-profile/components/ProfileForm.vue'

// 表单引用
const profileFormRef = ref()
const loading = ref(false)
const isEditing = ref(false)

// 用户信息数据
const userInfo = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  gender: '',
  birthday: '',
  idCard: '',
  icon: '',
  address: '',
  introduction: ''
})

// 表单数据
const profileForm = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  gender: '',
  birthday: '',
  idCard: '',
  icon: '',
  address: '',
  introduction: ''
})

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await getUserInfo()
    if (response.code === 200) {
      Object.assign(userInfo, response.data)
      Object.assign(profileForm, response.data)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 开始编辑
const startEdit = () => {
  isEditing.value = true
  // 将当前用户信息复制到表单中
  Object.assign(profileForm, userInfo)
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  // 重置表单数据为原始用户信息
  Object.assign(profileForm, userInfo)
  if (profileFormRef.value) {
    profileFormRef.value.clearValidate()
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!profileFormRef.value) return
  
  try {
    const isValid = await profileFormRef.value.validate()
    if (!isValid) return
    
    loading.value = true
    
    const response = await updateUserInfo(profileForm)
    if (response.code === 200) {
      ElMessage.success('个人信息更新成功')
      // 更新用户信息并退出编辑模式
      Object.assign(userInfo, profileForm)
      isEditing.value = false
      // 刷新页面以获取最新数据
      setTimeout(() => {
        window.location.reload()
      }, 1000) // 延迟1秒刷新，让用户看到成功提示
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
    ElMessage.error('更新个人信息失败')
  } finally {
    loading.value = false
  }
}

// 页面加载时获取用户信息
onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.user-profile-page {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.profile-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-profile-page {
    padding: 16px;
  }
}
</style>