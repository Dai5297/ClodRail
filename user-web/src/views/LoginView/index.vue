<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧背景 -->
      <LoginBackground />
      
      <!-- 右侧登录表单 -->
      <LoginForm @forgot-password="showForgotDialog" />
    </div>

    <!-- 忘记密码对话框 -->
    <ForgotPasswordDialog v-model:visible="forgotDialogVisible" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import LoginBackground from './components/LoginBackground.vue'
import LoginForm from './components/LoginForm.vue'
import ForgotPasswordDialog from './components/ForgotPasswordDialog.vue'

// 忘记密码对话框状态
const forgotDialogVisible = ref(false)

// 显示忘记密码对话框
const showForgotDialog = () => {
  forgotDialogVisible.value = true
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container {
  width: 100%;
  max-width: 1000px;
  height: 600px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    height: auto;
    max-width: 400px;
    margin: 20px;
  }
}
</style>