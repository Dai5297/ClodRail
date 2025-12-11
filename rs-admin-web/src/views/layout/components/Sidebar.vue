<template>
  <div class="sidebar" :class="{ 'collapsed': collapsed }">
    <!-- 侧边栏头部 -->
    <div class="sidebar-header">
      <div class="logo">
        <img :src="logoSrc" alt="Logo" />
      </div>
      <h3 v-if="!collapsed">{{ $t('common.systemTitle') }}</h3>
    </div>

    <!-- 导航菜单 -->
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      :collapse="collapsed"
      :router="true"
      background-color="transparent"
      text-color="var(--sidebar-text)"
      active-text-color="var(--sidebar-text)"
      :collapse-transition="false"
    >
      <template v-for="route in menuRoutes" :key="route.path">
        <!-- 有子路由 -->
        <el-sub-menu v-if="route.children && route.children.length > 0" :index="route.path">
          <template #title>
            <el-icon v-if="route.meta && route.meta.icon">
              <component :is="route.meta.icon" />
            </el-icon>
            <span>{{ $t(`route.${getRouteKey(route)}`) }}</span>
          </template>
          <el-menu-item 
            v-for="child in filterChildren(route.children, route.path)" 
            :key="child.path" 
            :index="resolvePath(route.path, child.path)"
          >
            <el-icon v-if="child.meta && child.meta.icon">
              <component :is="child.meta.icon" />
            </el-icon>
            <template #title>{{ $t(`route.${getRouteKey(child)}`) }}</template>
          </el-menu-item>
        </el-sub-menu>

        <!-- 无子路由 -->
        <el-menu-item v-else :index="'/' + route.path">
          <el-icon v-if="route.meta && route.meta.icon">
            <component :is="route.meta.icon" />
          </el-icon>
          <template #title>{{ $t(`route.${getRouteKey(route)}`) }}</template>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDark } from '@vueuse/core'
import { hasRoutePermission } from '@/utils/auth'
import { 
  Odometer, 
  User, 
  UserFilled,
  Monitor,
  Tickets, 
  ChatDotRound,
  Lock,
  Menu,
  List,
  Goods,
  ShoppingCart
} from "@element-plus/icons-vue"

defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()
const router = useRouter()
const isDark = useDark()

const logoSrc = computed(() => {
  return isDark.value ? '/logo-dark.png' : '/logo-light.png'
})

// 检查路由是否有权限
const hasPermission = (routePath) => {
  return hasRoutePermission(routePath)
}

// 检查子路由是否有权限
const hasChildPermission = (children, parentPath) => {
  if (!children || children.length === 0) return false
  return children.some(child => {
    const childPath = resolvePath(parentPath, child.path)
    return hasPermission(childPath)
  })
}

// 获取菜单路由(过滤无权限的路由)
const menuRoutes = computed(() => {
  const layoutRoute = router.options.routes.find(r => r.path === '/' && r.children)
  const routes = layoutRoute?.children || []
  
  return routes.filter(r => {
    if (!r.meta || !r.meta.title) return false
    
    const routePath = r.path.startsWith('/') ? r.path : `/${r.path}`
    
    // 如果有子路由,检查是否至少有一个子路由有权限
    if (r.children && r.children.length > 0) {
      return hasChildPermission(r.children, routePath)
    }
    
    // 检查当前路由权限
    return hasPermission(routePath)
  })
})

// 过滤子路由(只显示有权限的)
const filterChildren = (children, parentPath) => {
  if (!children) return []
  
  return children.filter(child => {
    const childPath = resolvePath(parentPath, child.path)
    return hasPermission(childPath)
  })
}

const activeMenu = computed(() => {
  return route.path
})

// Helper to get camelCase key for i18n
const getRouteKey = (route) => {
  const name = route.name || ''
  return name.charAt(0).toLowerCase() + name.slice(1)
}

// 解析路径
const resolvePath = (parentPath, childPath) => {
  if (childPath.startsWith('/')) {
    return childPath
  }
  const basePath = parentPath.startsWith('/') ? parentPath : `/${parentPath}`
  
  if (basePath.endsWith('/')) {
    return `${basePath}${childPath}`
  }
  return `${basePath}/${childPath}`
}
</script>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: 250px;
  height: 100vh;
  background: var(--sidebar-bg);
  color: var(--sidebar-text);
  z-index: 1000;
  transition: width 0.3s ease, background-color 0.3s;
  overflow-y: auto;
  overflow-x: hidden;
  border-right: 1px solid var(--border-color);
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  padding: 24px 20px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  transition: padding 0.3s ease;
}

.sidebar.collapsed .sidebar-header {
  padding: 24px 8px;
}

.logo {
  width: 80px;
  height: 80px;
  background: transparent;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: width 0.3s ease, height 0.3s ease;
}

.sidebar.collapsed .logo {
  width: 48px;
  height: 48px;
}

.logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.sidebar-header h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  white-space: nowrap;
  opacity: 1;
  transition: opacity 0.2s ease;
}

.sidebar-menu {
  border: none;
  padding: 16px 0;
}

:deep(.el-menu-item), :deep(.el-sub-menu__title) {
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  height: 48px;
  line-height: 48px;
}

:deep(.el-menu-item:hover), :deep(.el-sub-menu__title:hover) {
  background-color: var(--sidebar-active-bg) !important;
}

:deep(.el-menu-item.is-active) {
  background-color: var(--sidebar-active-bg) !important;
  font-weight: 500;
}

/* 展开状态下的菜单项样式 */
:deep(.el-menu-item .el-icon), :deep(.el-sub-menu__title .el-icon) {
  margin-right: 8px;
  font-size: 18px;
  width: 24px;
  text-align: center;
}

/* 收起状态下的菜单项样式 */
.sidebar.collapsed :deep(.el-menu-item), .sidebar.collapsed :deep(.el-sub-menu__title) {
  margin: 4px auto !important;
  padding: 0 !important;
  width: 48px !important;
  height: 48px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  text-align: center !important;
}

/* 强制隐藏文字和箭头 */
.sidebar.collapsed :deep(.el-menu-item span), 
.sidebar.collapsed :deep(.el-sub-menu__title span),
.sidebar.collapsed :deep(.el-sub-menu__icon-arrow) {
  display: none !important;
}

.sidebar.collapsed :deep(.el-menu-item .el-icon), 
.sidebar.collapsed :deep(.el-sub-menu__title .el-icon) {
  margin: 0 !important;
  padding: 0 !important;
  font-size: 20px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

/* 针对 tooltip trigger 容器的样式 */
.sidebar.collapsed :deep(.el-menu-item .el-menu-tooltip__trigger) {
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  width: 100% !important;
  height: 100% !important;
  padding: 0 !important;
}

/* 覆盖 Element Plus 的 collapse 模式样式 */
.sidebar.collapsed :deep(.el-menu--collapse .el-menu-item), 
.sidebar.collapsed :deep(.el-menu--collapse .el-sub-menu__title) {
  padding: 0 !important;
  text-align: center !important;
}

.sidebar.collapsed :deep(.el-menu--collapse) {
  width: 64px !important;
}

/* 确保菜单容器本身居中 */
.sidebar.collapsed .sidebar-menu {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 滚动条样式 */
.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-thumb {
  background: var(--sidebar-active-bg);
  border-radius: 3px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>