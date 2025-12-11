<template>
  <div class="min-h-screen flex flex-col bg-background font-sans">
    <header class="bg-white/80 backdrop-blur-md border-b border-slate-100 fixed w-full top-0 z-50 h-20 shadow-sm transition-all duration-300">
      <Header
          :username="username"
          :user-avatar="userAvatar"
          :on-go-to-user-center="goToUserCenter"
          :on-logout="handleLogout"
      />
    </header>
    <main class="flex-grow pt-20">
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

const router = useRouter()

// 用户信息数据
const username = ref('用户名')
const userAvatar = ref('')

// 跳转到用户中心的方法
const goToUserCenter = () => {
  router.push('/user')
}

// 退出登录的方法 handle
const handleLogout = async () => {
  try {
    // 直接清除本地存储并跳转，不依赖API响应
    await logout()
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    message.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    // 即使API调用失败，也要清除本地存储并跳转
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }
}

// 获取用户信息
const userInfo = async () => {
  try {
    const response = await getUserInfo()
    if (response.code === 200) {
      username.value = response.data.realName == null ? response.data.username : response.data.realName
      userAvatar.value = response.data.avatar
    }
  } catch (error) {
    message.error('获取用户信息失败')
  }
}

onMounted(() => {
  // 检查是否已经登录
  const token = localStorage.getItem('token')
  if (!token) {
    // 如果没有登录，跳转到登录页面
    router.push('/login')
  }
  // 获取用户信息
  userInfo()
})
</script>
