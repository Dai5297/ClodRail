<template>
  <div class="user-sidebar flex flex-col gap-6 h-full">
    <!-- 用户信息 -->
    <UserProfile 
      :user-info="userInfo" 
      :user-level="userLevel" 
    />
    
    <!-- 菜单列表 -->
    <div class="sidebar-menu-wrap bg-white rounded-3xl shadow-sm overflow-hidden p-4 border border-slate-100 flex-1">
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

<style scoped>
.user-sidebar {
  gap: 18px;
}

.sidebar-menu-wrap {
  border-color: #dde6f0;
  border-radius: 12px;
  box-shadow: 0 10px 24px rgba(11, 37, 89, 0.05);
}
</style>
