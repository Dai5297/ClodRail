<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-full">
    <div class="flex justify-between h-full items-center">
      <!-- Logo -->
      <router-link to="/" class="flex items-center gap-3 group">
        <img src="/logo-light.png" alt="CloudRail" class="h-10 w-auto" />
        <span class="text-xl font-bold tracking-tight text-slate-800 group-hover:text-primary transition-colors">CloudRail</span>
      </router-link>

      <!-- Navigation -->
      <nav class="hidden md:flex space-x-1">
        <router-link 
          v-for="item in menuItems" 
          :key="item.path" 
          :to="item.path"
          class="px-4 py-2 rounded-full text-sm font-medium transition-all duration-200"
          :class="[isActive(item.path) ? 'bg-blue-50 text-primary' : 'text-slate-500 hover:text-slate-900 hover:bg-slate-50']"
        >
          {{ item.label }}
        </router-link>
      </nav>

      <!-- Right Actions -->
      <div class="flex items-center gap-4">
        
        <a-dropdown>
          <div class="flex items-center gap-3 cursor-pointer pl-2 pr-1 py-1 rounded-full hover:bg-white hover:shadow-sm border border-transparent hover:border-slate-100 transition-all">
            <a-avatar :size="32" :src="userAvatar" class="bg-gradient-to-tr from-blue-500 to-indigo-600 flex items-center justify-center text-white font-bold text-xs shadow-md">
              <template #icon v-if="!userAvatar"><i class="ri-user-line"></i></template>
            </a-avatar>
            <span class="text-sm font-medium text-slate-700 hidden sm:block">{{ username }}</span>
            <i class="ri-arrow-down-s-line text-slate-400"></i>
          </div>
          <template #overlay>
            <a-menu class="rounded-xl shadow-lg border border-slate-100 p-2">
              <a-menu-item key="user" @click="onGoToUserCenter" class="rounded-lg">
                <template #icon><i class="ri-user-line"></i></template>
                个人中心
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="logout" @click="onLogout" class="rounded-lg text-red-500">
                <template #icon><i class="ri-logout-box-r-line"></i></template>
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()

const props = defineProps({
  username: {
    type: String,
    default: '用户名'
  },
  userAvatar: {
    type: String,
    default: ''
  },
  onGoToUserCenter: {
    type: Function,
    required: true
  },
  onLogout: {
    type: Function,
    required: true
  }
})

const menuItems = [
  { path: '/', label: '首页' },
  { path: '/ticket', label: '车票' },
  { path: '/mall', label: '积分商城' },
  { path: '/user', label: '个人中心' },
  { path: '/assistant', label: 'AI助手' }
]

const isActive = (path) => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}
</script>
