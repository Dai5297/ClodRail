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
import { ElMessage } from 'element-plus'
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
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow-x: hidden;
  background:
    linear-gradient(90deg, rgba(22, 119, 255, 0.08) 1px, transparent 1px),
    linear-gradient(180deg, rgba(11, 37, 89, 0.05) 1px, transparent 1px),
    #f5f8fc;
  background-size: 56px 56px;
}

.login-container {
  width: 100%;
  max-width: 1120px;
  padding: 32px;
  position: relative;
  z-index: 10;
}

.login-card {
  display: grid;
  grid-template-columns: minmax(320px, 0.9fr) minmax(420px, 1fr);
  min-height: 640px;
  overflow: hidden;
  border: 1px solid #dde6f0;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 18px 42px rgba(11, 37, 89, 0.1);
}

.login-card::before {
  content: "";
  position: absolute;
  inset: auto 32px 32px 32px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #1677ff 18%, #ff8200 50%, #1677ff 82%, transparent);
  opacity: 0.28;
}

.login-card::after {
  content: "";
  position: absolute;
  left: 31%;
  bottom: 27px;
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #ff8200;
  box-shadow: 230px 0 0 #1677ff, 460px 0 0 #0b2559;
}

.login-header,
.login-content {
  position: relative;
  z-index: 1;
}

.login-header {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 44px 40px;
  color: #ffffff;
  background:
    linear-gradient(135deg, rgba(11, 37, 89, 0.96), rgba(11, 37, 89, 0.88)),
    repeating-linear-gradient(90deg, transparent 0 26px, rgba(255, 255, 255, 0.08) 26px 27px);
}

.login-header::after {
  content: "";
  width: min(100%, 340px);
  height: 112px;
  margin-top: auto;
  border-bottom: 3px solid rgba(255, 255, 255, 0.38);
  border-left: 3px solid rgba(255, 255, 255, 0.14);
  transform: skewX(-18deg);
}

.login-header::before {
  content: "ClodRail";
  display: inline-flex;
  align-items: center;
  width: fit-content;
  margin-bottom: 36px;
  padding: 8px 12px;
  border: 1px solid rgba(255, 255, 255, 0.26);
  border-radius: 999px;
  color: rgba(255, 255, 255, 0.86);
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0;
}

.login-title {
  max-width: 9em;
  margin: 0 0 14px;
  color: #ffffff;
  font-size: clamp(32px, 4vw, 46px);
  font-weight: 800;
  line-height: 1.12;
  letter-spacing: 0;
}

.login-subtitle {
  max-width: 24em;
  margin: 0;
  color: rgba(255, 255, 255, 0.78);
  font-size: 15px;
  line-height: 1.7;
}

.login-content {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 44px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
}

.background-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 1;
}

.decoration-circle {
  position: absolute;
  border-radius: 999px;
  border: 1px solid rgba(22, 119, 255, 0.18);
  background: transparent;
}

.circle-1 {
  width: 280px;
  height: 280px;
  top: -96px;
  right: -70px;
}

.circle-2 {
  width: 180px;
  height: 180px;
  bottom: 12%;
  left: -80px;
}

.circle-3 {
  width: 88px;
  height: 88px;
  right: 12%;
  bottom: 10%;
  border-color: rgba(255, 130, 0, 0.24);
}

@media (max-width: 900px) {
  .login-page {
    align-items: flex-start;
  }

  .login-container {
    padding: 18px;
  }

  .login-card {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .login-card::before,
  .login-card::after {
    display: none;
  }

  .login-header {
    min-height: 220px;
    padding: 30px 26px;
  }

  .login-header::after {
    height: 54px;
  }

  .login-title {
    font-size: 30px;
  }

  .login-content {
    padding: 28px 18px 32px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 12px;
  }

  .login-card {
    border-radius: 12px;
  }

  .login-header {
    padding: 24px 20px;
  }

  .login-title {
    font-size: 26px;
  }

  .login-content {
    padding: 24px 10px 28px;
  }
}

/* 背景装饰 */

/* 响应式设计 */

/* 深色模式支持 */
</style>
