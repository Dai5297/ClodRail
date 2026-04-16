<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h1 class="login-title">铁路订票系统</h1>
          <p class="login-subtitle">Railway Ticket Booking System</p>
        </div>
        
        <div class="login-content">
          <AuthTabs 
            ref="authTabsRef"
            :default-tab="currentTab"
            @tab-change="handleTabChange"
          >
            <template #login>
              <LoginForm
                ref="loginFormRef"
                @login-success="handleLoginSuccess"
                @switch-to-register="switchToRegister"
                @login-failed="handleLoginFailed"
              />
            </template>
            
            <template #register>
              <RegisterForm
                ref="registerFormRef"
                @register-success="handleRegisterSuccess"
                @switch-to-login="switchToLogin"
              />
            </template>
          </AuthTabs>
        </div>
      </div>
    </div>
    
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AuthTabs from '@/view/login/components/AuthTabs.vue'
import LoginForm from '@/view/login/components/LoginForm.vue'
import RegisterForm from '@/view/login/components/RegisterForm.vue'
import { ensureAccessToken } from '@/utils/auth.js'

const router = useRouter()

// 组件引用
const authTabsRef = ref()
const loginFormRef = ref()
const registerFormRef = ref()

// 当前选中的标签页
const currentTab = ref('login')

// 标签页切换处理
const handleTabChange = (tab) => {
  currentTab.value = tab
  
  // 切换时清空表单
  if (tab === 'login' && loginFormRef.value) {
    loginFormRef.value.resetForm()
  } else if (tab === 'register' && registerFormRef.value) {
    registerFormRef.value.resetForm()
  }
}

// 切换到注册页面
const switchToRegister = () => {
  if (authTabsRef.value) {
    authTabsRef.value.switchTab('register')
  }
}

// 切换到登录页面
const switchToLogin = () => {
  if (authTabsRef.value) {
    authTabsRef.value.switchTab('login')
  }
}

// 登录成功处理
const handleLoginSuccess = (userData) => {
  
  // 跳转到首页或用户指定页面
  const redirect = router.currentRoute.value.query.redirect || '/'
  router.push(redirect)
}

// 登录失败处理
const handleLoginFailed = (error) => {
  // 登录失败的处理逻辑已在LoginForm组件中实现
}

// 注册成功处理
const handleRegisterSuccess = (userData) => {
  
  // 显示注册成功提示
  ElMessage.success(userData.message || '注册成功！')
  
  // 注册成功后自动切换到登录页面
  switchToLogin()
  
  // 可以预填用户名
  setTimeout(() => {
    if (loginFormRef.value && userData.username) {
      loginFormRef.value.loginForm.username = userData.username
    }
  }, 100)
}

// 页面初始化
onMounted(async () => {
  try {
    await ensureAccessToken()
    router.push('/')
  } catch (error) {
    // ignore
  }
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-container {
  width: 100%;
  max-width: 480px;
  padding: 20px;
  position: relative;
  z-index: 10;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  padding: 40px 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.login-subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
  font-weight: 300;
}

.login-content {
  padding: 0;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 0.3;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    max-width: 100%;
    padding: 16px;
  }
  
  .login-card {
    border-radius: 16px;
  }
  
  .login-header {
    padding: 32px 24px 16px;
  }
  
  .login-title {
    font-size: 24px;
  }
  
  .login-subtitle {
    font-size: 13px;
  }
  
  .decoration-circle {
    display: none;
  }
}

@media (max-width: 480px) {
  .login-header {
    padding: 24px 20px 12px;
  }
  
  .login-title {
    font-size: 22px;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .login-card {
    background: rgba(30, 30, 30, 0.95);
    border: 1px solid rgba(255, 255, 255, 0.1);
  }
}
</style>
