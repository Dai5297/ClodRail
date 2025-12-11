<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <Sidebar :collapsed="sidebarCollapsed" />

    <!-- 主内容区域 -->
    <div class="main-content" :class="{ 'collapsed': sidebarCollapsed }">
      <!-- 顶部导航栏 -->
      <Navbar @toggle-sidebar="toggleSidebar" />

      <!-- 页面内容 -->
      <div class="page-content">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'

const sidebarCollapsed = ref(false)

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-color-secondary);
}

.main-content {
  flex: 1;
  margin-left: 250px;
  transition: margin-left 0.3s ease;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  width: calc(100% - 250px);
}

.main-content.collapsed {
  margin-left: 64px;
}

.page-content {
  flex: 1;
  padding: 24px;
}
</style>

