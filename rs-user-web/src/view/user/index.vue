<template>
  <div class="user-center">
    <!-- 侧边栏 -->
    <UserSidebar 
      :user-info="userInfo"
      :user-level="userLevel"
      :menu-items="menuItems"
    />

    <!-- 主内容区域 -->
    <main class="content-area">
      <RouterView :key="$route.fullPath"></RouterView>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Document, UserFilled, Star, Lock } from '@element-plus/icons-vue'
import { getUserInfo } from '@/api/user.js'
import UserSidebar from '@/view/user/components/UserSidebar.vue'

const userInfo = ref({
  realName: '',
  username: '',
  phone: '',
  email: ''
})

const userLevel = ref('普通用户')

const menuItems = ref([
  {
    path: '/user',
    label: '个人信息',
    icon: User,
    exact: true
  },
  {
    path: '/user/order',
    label: '我的订单',
    icon: Document,
    exact: false
  },
  {
    path: '/user/contact',
    label: '常用联系人',
    icon: UserFilled,
    exact: false
  },
  {
    path: '/user/point',
    label: '积分管理',
    icon: Star,
    exact: false
  },
  {
    path: '/user/security',
    label: '账号安全',
    icon: Lock,
    exact: false
  }
])

const fetchUserInfo = async () => {
  try {
    const data = await getUserInfo()
    if (data.code === 200) {
      userInfo.value = data.data || {}
    } else {
      console.error('获取用户信息失败:', data?.message || '未知错误')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.user-center {
  display: flex;
  min-height: 100vh;
  background-color: #f5f5f5;
  gap: 20px;
  padding: 20px;
}

.content-area {
  flex: 1;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 40px);
}
</style>