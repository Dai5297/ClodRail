<template>
  <div class="common-layout">
    <el-container>
      <el-header class="header">
        <Header
            :username="username"
            :user-avatar="userAvatar"
            :on-go-to-user-center="goToUserCenter"
            :on-logout="handleLogout"
        />
      </el-header>
      <el-main class="main">
        <router-view/>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import Header from './component/Header.vue'
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
    ElMessage.success('退出登录成功')
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
    console.log(response)
    if (response.code === 200) {
      username.value = response.data.realName == null ? response.data.username : response.data.realName
      userAvatar.value = response.data.avatar
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
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

<style scoped>
.common-layout {
  width: 100%;
  height: 100vh;
  margin: 0;
  padding: 0;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e8e8e8;
  padding: 0 24px;
  height: 64px !important;
  line-height: 64px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.main {
  padding: 0;
  margin: 0;
  height: calc(100vh - 64px);
  overflow-y: auto;
}

</style>