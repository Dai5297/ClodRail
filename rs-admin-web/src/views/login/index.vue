<template>
  <div class="login-page">
    <ParticleBackground />
    
    <div class="login-container">
      <div class="login-box">
        <div class="login-header">
          <div class="logo">
            <img src="/logo-login.png" alt="Logo" />
          </div>
          <h1 class="title">{{ isLogin ? '欢迎回来' : '创建账号' }}</h1>
          <p class="subtitle">{{ isLogin ? '登录您的管理账号' : '注册一个新的管理账号' }}</p>
        </div>

        <div class="form-container">
          <Transition name="fade" mode="out-in">
            <LoginForm v-if="isLogin" key="login" />
            <RegisterForm v-else key="register" @success="isLogin = true" />
          </Transition>
        </div>

        <div class="login-footer">
          <p v-if="isLogin">
            还没有账号？ 
            <span class="link" @click="isLogin = false">立即注册</span>
          </p>
          <p v-else>
            已有账号？ 
            <span class="link" @click="isLogin = true">立即登录</span>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Odometer } from '@element-plus/icons-vue'
import ParticleBackground from './components/ParticleBackground.vue'
import LoginForm from './components/LoginForm.vue'
import RegisterForm from './components/RegisterForm.vue'

const isLogin = ref(true)
</script>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  position: relative;
  overflow: hidden;
  background: #000000;
  display: flex;
}

.login-container {
  position: absolute;
  right: 0;
  top: 0;
  width: 600px;
  height: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  box-shadow: -10px 0 30px rgba(0, 0, 0, 0.1);
}

.login-box {
  width: 100%;
  max-width: 450px;
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 120px;
  height: 120px;
  background: transparent;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 10px;
}

.logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.title {
  font-size: 28px;
  font-weight: 600;
  color: #000000;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 14px;
  color: #666666;
}

.login-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: #666666;
}

.link {
  color: #000000;
  font-weight: 600;
  cursor: pointer;
  margin-left: 4px;
  transition: opacity 0.2s;
}

.link:hover {
  opacity: 0.7;
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>

