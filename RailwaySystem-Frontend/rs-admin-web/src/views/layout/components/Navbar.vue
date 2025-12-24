<template>
  <div class="navbar">
    <div class="left">
      <div class="toggle-btn" @click="$emit('toggle-sidebar')">
        <el-icon :size="20"><Fold /></el-icon>
      </div>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">{{ $t('navbar.home') }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ $t(`route.${currentRouteName}`) }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="right">
      <!-- Theme Toggle -->
      <div class="action-item" @click="toggleDark()">
        <el-icon :size="20">
          <Moon v-if="isDark" />
          <Sunny v-else />
        </el-icon>
      </div>

      <!-- Language Toggle -->
      <el-dropdown @command="handleLanguageChange">
        <div class="action-item">
          <el-icon :size="20"><Operation /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="zh-cn">中文</el-dropdown-item>
            <el-dropdown-item command="en">English</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <el-dropdown @command="handleDropdownCommand">
        <div class="avatar-container">
          <el-avatar :size="32" :src="userAvatar" />
          <span class="username">{{ username }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">{{ $t('navbar.logout') }}</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDark, useToggle } from '@vueuse/core'
import { useI18n } from 'vue-i18n'
import { ElMessageBox, ElMessage } from 'element-plus'
import { clearAuth, getAdminInfo } from '@/utils/auth'
import { adminLogout } from '@/api/auth'
import { Fold, Moon, Sunny, Operation } from '@element-plus/icons-vue'

defineEmits(['toggle-sidebar'])

const route = useRoute()
const router = useRouter()
const { locale } = useI18n()

const isDark = useDark()
const toggleDark = useToggle(isDark)

const defaultAvatar = '/default-avatar.png'

// 用户信息
const userInfo = ref(null)

// 加载用户信息
const loadUserInfo = () => {
  const adminInfo = getAdminInfo()
  if (adminInfo) {
    userInfo.value = adminInfo
  }
}

// 计算属性：用户头像
const userAvatar = computed(() => {
  return userInfo.value?.icon || defaultAvatar
})

// 计算属性：用户名
const username = computed(() => {
  return userInfo.value?.realName || userInfo.value?.username || 'Admin'
})

onMounted(() => {
  loadUserInfo()
  // 监听 storage 事件，当其他标签页更新用户信息时同步
  window.addEventListener('storage', (e) => {
    if (e.key === 'admin_info') {
      loadUserInfo()
    }
  })
})

const currentRouteName = computed(() => {
  // Convert route name to camelCase for i18n key (e.g., 'TicketOrders' -> 'ticketOrders')
  const name = route.name || ''
  return name.charAt(0).toLowerCase() + name.slice(1)
})

const handleLanguageChange = (lang) => {
  locale.value = lang
}

// 处理下拉菜单命令
const handleDropdownCommand = (command) => {
  if (command === 'logout') {
    handleLogout()
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用后端登出接口
    try {
      await adminLogout()
    } catch (apiError) {
        console.error('调用登出接口失败:', apiError)
      // 即使接口调用失败,也继续清除本地数据
    }
    
    // 清除本地存储的用户信息
    clearAuth()
    
    ElMessage.success('退出登录成功')
    
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    // 用户取消退出
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
    }
  }
}
</script>

<style scoped>
.navbar {
  height: 64px;
  background: var(--header-bg);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  transition: background-color 0.3s, border-color 0.3s;
}

.left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.toggle-btn {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: var(--text-color);
}

.right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-item {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: var(--text-color);
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.action-item:hover {
  background-color: var(--bg-color-secondary);
}

.avatar-container {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-color);
}

:deep(.el-breadcrumb__inner) {
  color: var(--text-color-secondary) !important;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: var(--text-color) !important;
}
</style>
