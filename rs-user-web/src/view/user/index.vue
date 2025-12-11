<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex flex-col lg:flex-row gap-8 h-[calc(100vh-80px)] overflow-hidden">
    <!-- 侧边栏 -->
    <aside class="w-full lg:w-72 flex-shrink-0 overflow-y-auto hidden-scrollbar">
      <UserSidebar 
        :user-info="userInfo"
        :user-level="userLevel"
        :menu-items="menuItems"
      />
    </aside>

    <!-- 主内容区域 -->
    <main class="flex-1 h-full flex flex-col overflow-hidden">
      <RouterView :key="$route.fullPath"></RouterView>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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
    icon: 'ri-user-line',
    exact: true
  },
  {
    path: '/user/order',
    label: '我的订单',
    icon: 'ri-file-list-line',
    exact: false
  },
  {
    path: '/user/contact',
    label: '常用联系人',
    icon: 'ri-team-line',
    exact: false
  },
  {
    path: '/user/point',
    label: '积分管理',
    icon: 'ri-copper-coin-line',
    exact: false
  },
  {
    path: '/user/security',
    label: '账号安全',
    icon: 'ri-shield-check-line',
    exact: false
  }
])

const fetchUserInfo = async () => {
  try {
    const data = await getUserInfo()
    if (data.code === 200) {
      userInfo.value = data.data || {}
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>
