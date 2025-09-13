<template>
  <div class="layout">
    <!-- 头部导航 -->
    <header class="layout-header">
      <div class="header-container">
        <!-- Logo区域 -->
        <div class="logo-section">
          <router-link to="/" class="logo">
            <el-icon size="32" color="#1890ff">
              <Position />
            </el-icon>
            <span class="logo-text">铁路订票系统</span>
          </router-link>
        </div>

        <!-- 导航菜单 -->
        <nav class="nav-menu">
          <router-link to="/" class="nav-item" :exact="true">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </router-link>
          <router-link to="/search" class="nav-item">
            <el-icon><Search /></el-icon>
            <span>车票</span>
          </router-link>
          <router-link to="/my-orders" class="nav-item">
            <el-icon><Tickets /></el-icon>
            <span>订单</span>
          </router-link>
          <router-link to="/points-mall" class="nav-item">
            <el-icon><Star /></el-icon>
            <span>积分</span>
          </router-link>
          <router-link to="/ai-assistant" class="nav-item">
            <el-icon><ChatDotRound /></el-icon>
            <span>助手</span>
          </router-link>
        </nav>

        <!-- 用户操作区域 -->
        <div class="user-section">
          <template v-if="isLoggedIn">
            <el-dropdown trigger="hover">
              <div class="user-info">
                <el-avatar :size="32" :src="userInfo.avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="username">{{ userInfo.nickname || '用户' }}</span>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goToProfile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item @click="goToOrders">
                    <el-icon><Tickets /></el-icon>
                    我的订单
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <div class="auth-buttons">
              <el-button type="text" @click="goToLogin">登录</el-button>
              <el-button type="primary" @click="goToRegister">注册</el-button>
            </div>
          </template>
        </div>

        <!-- 移动端菜单按钮 -->
        <div class="mobile-menu-btn" @click="toggleMobileMenu">
          <el-icon size="24"><Menu /></el-icon>
        </div>
      </div>

      <!-- 移动端导航菜单 -->
      <div class="mobile-nav" :class="{ 'mobile-nav-open': mobileMenuOpen }">
        <router-link to="/" class="mobile-nav-item" @click="closeMobileMenu" :exact="true">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </router-link>
        <router-link to="/search" class="mobile-nav-item" @click="closeMobileMenu">
          <el-icon><Search /></el-icon>
          <span>车票查询</span>
        </router-link>
        <router-link to="/my-orders" class="mobile-nav-item" @click="closeMobileMenu">
          <el-icon><Tickets /></el-icon>
          <span>我的订单</span>
        </router-link>
        <router-link to="/points-mall" class="mobile-nav-item" @click="closeMobileMenu">
          <el-icon><Star /></el-icon>
          <span>积分商城</span>
        </router-link>
        <router-link to="/ai-assistant" class="mobile-nav-item" @click="closeMobileMenu">
          <el-icon><ChatDotRound /></el-icon>
          <span>AI助手</span>
        </router-link>
        <router-link to="/profile" class="mobile-nav-item" @click="closeMobileMenu" v-if="isLoggedIn">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </router-link>
        <div class="mobile-auth" v-if="!isLoggedIn">
          <el-button type="primary" @click="goToLogin" block>登录</el-button>
          <el-button @click="goToRegister" block style="margin-top: 8px;">注册</el-button>
        </div>
        <div class="mobile-logout" v-if="isLoggedIn">
          <el-button type="danger" @click="handleLogout" block>退出登录</el-button>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="layout-main">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 页脚 -->
    <footer class="layout-footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-section">
            <h4>服务支持</h4>
            <ul>
              <li><a href="#">购票指南</a></li>
              <li><a href="#">退改签规则</a></li>
              <li><a href="#">常见问题</a></li>
              <li><a href="#">联系客服</a></li>
            </ul>
          </div>
          <div class="footer-section">
            <h4>关于我们</h4>
            <ul>
              <li><a href="#">公司介绍</a></li>
              <li><a href="#">服务条款</a></li>
              <li><a href="#">隐私政策</a></li>
              <li><a href="#">招聘信息</a></li>
            </ul>
          </div>
          <div class="footer-section">
            <h4>下载APP</h4>
            <div class="app-download">
              <div class="qr-code">
                <div class="qr-placeholder">二维码</div>
                <p>扫码下载APP</p>
              </div>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 铁路订票系统. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Position,
  House,
  Search,
  Tickets,
  Star,
  ChatDotRound,
  User,
  ArrowDown,
  SwitchButton,
  Menu
} from '@element-plus/icons-vue'

const router = useRouter()

// 移动端菜单状态
const mobileMenuOpen = ref(false)

// 用户登录状态（模拟）
const isLoggedIn = ref(false)
const userInfo = ref({
  nickname: '张三',
  avatar: ''
})

// 切换移动端菜单
const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

// 关闭移动端菜单
const closeMobileMenu = () => {
  mobileMenuOpen.value = false
}

// 导航方法
const goToLogin = () => {
  closeMobileMenu()
  router.push('/login')
}

const goToRegister = () => {
  closeMobileMenu()
  router.push('/register')
}

const goToProfile = () => {
  router.push('/profile')
}

const goToOrders = () => {
  router.push('/my-orders')
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 清除登录状态
    isLoggedIn.value = false
    userInfo.value = { nickname: '', avatar: '' }

    ElMessage.success('已退出登录')

    // 跳转到首页
    router.push('/')
  } catch {
    // 用户取消
  }
}

// 检查登录状态
const checkLoginStatus = () => {
  // 这里应该从localStorage或API检查登录状态
  const token = localStorage.getItem('token')
  if (token) {
    isLoggedIn.value = true
    // 获取用户信息
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedUserInfo) {
      userInfo.value = JSON.parse(savedUserInfo)
    }
  }
}

// 监听点击外部关闭移动端菜单
const handleClickOutside = (event: Event) => {
  const target = event.target as Element
  if (!target.closest('.mobile-nav') && !target.closest('.mobile-menu-btn')) {
    mobileMenuOpen.value = false
  }
}

onMounted(() => {
  checkLoginStatus()
  document.addEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
}

/* 头部样式 */
.layout-header {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-container {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 0 24px;
  display: flex;
  align-items: center;
  height: 64px;
}

/* Logo区域 */
.logo-section {
  flex-shrink: 0;
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

/* 导航菜单 */
.nav-menu {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
  flex: 1;
  margin: 0 48px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  text-decoration: none;
  color: #595959;
  border-radius: 6px;
  transition: all 0.2s;
  font-size: 14px;
}

.nav-item:hover {
  color: #1890ff;
  background: #f0f8ff;
}

.nav-item.router-link-exact-active {
  color: #1890ff;
  background: #e6f7ff;
}

/* 用户区域 */
.user-section {
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #262626;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  font-size: 12px;
  color: #8c8c8c;
}

.auth-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 移动端菜单按钮 */
.mobile-menu-btn {
  display: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background 0.2s;
}

.mobile-menu-btn:hover {
  background: #f5f5f5;
}

/* 移动端导航 */
.mobile-nav {
  display: none;
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 16px 24px;
  transform: translateY(-100%);
  opacity: 0;
  transition: all 0.3s ease;
  pointer-events: none;
}

.mobile-nav-open {
  transform: translateY(0);
  opacity: 1;
  pointer-events: auto;
}

.mobile-nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  text-decoration: none;
  color: #262626;
  border-bottom: 1px solid #f0f0f0;
  font-size: 16px;
}

.mobile-nav-item:last-child {
  border-bottom: none;
}

.mobile-nav-item.router-link-exact-active {
  color: #1890ff;
}

.mobile-auth,
.mobile-logout {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

/* 主要内容区域 */
.layout-main {
  flex: 1;
  width: 100%;
  min-height: calc(100vh - 64px - 200px); /* 减去头部和页脚高度 */
  margin: 0;
  padding: 0;
}

/* 页脚样式 */
.layout-footer {
  background: #001529;
  color: #fff;
  margin-top: auto;
}

.footer-container {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 40px 24px 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 32px;
  margin-bottom: 32px;
}

.footer-section h4 {
  color: #fff;
  margin-bottom: 16px;
  font-size: 16px;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section li {
  margin-bottom: 8px;
}

.footer-section a {
  color: #bfbfbf;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
}

.footer-section a:hover {
  color: #1890ff;
}

.app-download {
  display: flex;
  align-items: center;
  gap: 16px;
}

.qr-code {
  text-align: center;
}

.qr-placeholder {
  width: 80px;
  height: 80px;
  background: #f5f5f5;
  border: 1px solid #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 8px;
}

.qr-code p {
  font-size: 12px;
  color: #bfbfbf;
  margin: 0;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #303030;
  color: #8c8c8c;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }

  .logo-text {
    display: none;
  }

  .nav-menu {
    display: none;
  }

  .user-section {
    display: none;
  }

  .mobile-menu-btn {
    display: block;
    margin-left: auto;
  }

  .mobile-nav {
    display: block;
  }

  .layout-main {
    min-height: calc(100vh - 64px - 100px);
  }

  .footer-container {
    padding: 24px 16px 16px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 24px;
    margin-bottom: 24px;
  }
}

@media (max-width: 480px) {
  .header-container {
    height: 56px;
  }

  .layout-main {
    min-height: calc(100vh - 56px - 100px);
  }
}
</style>
