<template>

  <div class="logo-section">
    <el-row>
      <el-col :span="6">
        <router-link to="/" class="logo">
          <el-icon size="32" color="#1890ff">
            <Position/>
          </el-icon>
          <span class="logo-text">铁路订票系统</span>
        </router-link>
      </el-col>
      <el-col :span="12">
        <el-menu
          :default-active="getActiveMenuItem"
          mode="horizontal"
          :ellipsis="false"
          router
          class="header-menu"
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/ticket">车票</el-menu-item>
          <el-menu-item index="/order">订单</el-menu-item>
          <el-menu-item index="/mall">积分商城</el-menu-item>
          <el-menu-item index="/user">个人中心</el-menu-item>
          <el-menu-item index="/assistant">AI助手</el-menu-item>
        </el-menu>
      </el-col>
      <el-col :span="6">
        <el-dropdown trigger="click" class="user-dropdown">
          <div class="user-info">
            <el-avatar :size="32" :src="userAvatar" class="user-avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <span class="username">{{ username }}</span>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="onGoToUserCenter">
                <el-icon><User /></el-icon>
                用户中心
              </el-dropdown-item>
              <el-dropdown-item divided @click="onLogout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { Position, User, ArrowDown, SwitchButton } from "@element-plus/icons-vue"
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()

// 定义props接收父组件传入的数据和方法
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

// 获取当前激活的菜单项 - 使用computed确保响应式更新
const getActiveMenuItem = computed(() => {
  const currentPath = route.path
  
  // 如果路径以 /user 开头，则激活个人中心菜单
  if (currentPath.startsWith('/user')) {
    return '/user'
  }
  
  // 其他路径直接返回当前路径
  return currentPath
})
</script>

<style scoped>
/* Logo区域 */
.logo-section {
  flex-shrink: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
}

.logo-section .el-row {
  width: 100%;
  height: 100%;
}

.logo-section .el-col {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #262626;
  font-weight: 600;
  font-size: 18px;
}

.logo-text {
  white-space: nowrap;
}

/* 菜单样式 */
.header-menu {
  border-bottom: none;
  background-color: transparent;
  justify-content: center;
}

.header-menu .el-menu-item {
  border-bottom: none;
  font-weight: 500;
  font-size: 16px;
}

.header-menu .el-menu-item:hover {
  background-color: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.header-menu .el-menu-item.is-active {
  color: #1890ff;
  border-bottom: 2px solid #1890ff;
}

/* 用户信息样式 */
.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: rgba(24, 144, 255, 0.1);
}

.user-avatar {
  flex-shrink: 0;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
  white-space: nowrap;
}

.dropdown-icon {
  font-size: 12px;
  color: #8c8c8c;
  transition: transform 0.3s;
}

.user-dropdown.is-active .dropdown-icon {
  transform: rotate(180deg);
}
</style>