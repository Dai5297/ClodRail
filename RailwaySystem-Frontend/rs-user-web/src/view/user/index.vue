<template>
  <div class="user-shell max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex flex-col lg:flex-row gap-8 h-[calc(100vh-80px)] overflow-hidden">
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

<style scoped>
.user-shell {
  min-height: calc(100vh - 72px);
  padding-top: 24px;
  padding-bottom: 24px;
  color: #172033;
}

.user-shell aside {
  border-radius: 12px;
}

.user-shell main {
  border: 1px solid #dde6f0;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 12px 28px rgba(11, 37, 89, 0.06);
}

@media (max-width: 1023px) {
  .user-shell {
    height: auto;
    min-height: calc(100vh - 72px);
    overflow: visible;
  }

  .user-shell aside {
    display: block;
  }

  .user-shell main {
    min-height: 560px;
    overflow: visible;
  }
}

@media (max-width: 640px) {
  .user-shell {
    padding: 14px 12px 24px;
    gap: 16px;
  }
}
</style>
