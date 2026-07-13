<template>
  <div class="min-h-screen flex flex-col bg-background font-sans">
    <header class="portal-header">
      <Header
          :username="username"
          :user-avatar="userAvatar"
          :on-go-to-user-center="goToUserCenter"
          :on-logout="handleLogout"
      />
    </header>
    <main class="portal-main">
      <router-view/>
    </main>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {message} from 'ant-design-vue'
import Header from '@/view/layout/components/Header.vue'
import {getUserInfo} from "@/api/user.js";
import {logout} from "@/api/auth.js";
import { clearAuth, ensureAccessToken } from '@/utils/auth.js'

const router = useRouter()

// 用户信息数据
const username = ref('用户名')
const userAvatar = ref('')

const applyStoredUserInfo = () => {
  try {
    const stored = JSON.parse(localStorage.getItem('userInfo') || '{}')
    username.value = stored.realName || stored.username || username.value
    userAvatar.value = stored.avatar || stored.icon || userAvatar.value
    return Boolean(stored.realName || stored.username || stored.avatar || stored.icon)
  } catch (error) {
    // ignore invalid local user info
  }
  return false
}

// 跳转到用户中心的方法
const goToUserCenter = () => {
  router.push('/user')
}

// 退出登录的方法 handle
const handleLogout = async () => {
  try {
    // 直接清除本地存储并跳转，不依赖API响应
    await logout()
    clearAuth()
    message.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    // 即使API调用失败，也要清除本地存储并跳转
    clearAuth()
    router.push('/login')
  }
}

// 获取用户信息
const userInfo = async () => {
  if (applyStoredUserInfo()) return
  try {
    const response = await getUserInfo()
    if (response.code === 200) {
      username.value = response.data.realName == null ? response.data.username : response.data.realName
      userAvatar.value = response.data.avatar
    }
  } catch (error) {
    applyStoredUserInfo()
    message.error('获取用户信息失败')
  }
}

onMounted(async () => {
  try {
    await ensureAccessToken()
    applyStoredUserInfo()
    await userInfo()
  } catch (error) {
    clearAuth()
    router.push('/login')
  }
})
</script>

<style scoped>
.portal-header {
  position: fixed;
  z-index: 50;
  top: 0;
  left: 0;
  width: 100%;
  height: 72px;
  border-bottom: 1px solid var(--rail-line);
  background: #fff;
}

.portal-main {
  flex-grow: 1;
  padding-top: 72px;
}
</style>
