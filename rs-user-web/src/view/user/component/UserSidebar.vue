<template>
  <aside class="sidebar">
    <!-- 用户信息 -->
    <UserProfile 
      :user-info="userInfo" 
      :user-level="userLevel" 
    />
    
    <!-- 菜单列表 -->
    <SidebarMenu :menu-items="menuItems" />
  </aside>
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
.sidebar {
  width: 280px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
  min-height: calc(100vh - 40px);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 100%;
  }
}
</style>