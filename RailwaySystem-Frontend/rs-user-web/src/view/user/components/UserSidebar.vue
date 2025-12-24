<template>
  <div class="flex flex-col gap-6 h-full">
    <!-- 用户信息 -->
    <UserProfile 
      :user-info="userInfo" 
      :user-level="userLevel" 
    />
    
    <!-- 菜单列表 -->
    <div class="bg-white rounded-3xl shadow-sm overflow-hidden p-4 border border-slate-100 flex-1">
      <SidebarMenu :menu-items="menuItems" />
    </div>
  </div>
</template>

<script setup>
import UserProfile from './UserProfile.vue'
import SidebarMenu from './SidebarMenu.vue'

defineProps({
  userInfo: {
    type: Object,
    default: () => ({
      realName: '',
      username: '',
      phone: '',
      email: ''
    })
  },
  userLevel: {
    type: String,
    default: '普通用户'
  },
  menuItems: {
    type: Array,
    default: () => []
  }
})
</script>
