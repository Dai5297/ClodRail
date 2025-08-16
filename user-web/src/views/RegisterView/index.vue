<template>
  <div class="register-page">
    <div class="register-container">
      <!-- 左侧背景 -->
      <RegisterBackground />
      
      <!-- 右侧注册表单 -->
      <RegisterForm />
    </div>

    <!-- 用户协议对话框 -->
    <UserAgreementDialog v-model:visible="agreementDialogVisible" />

    <!-- 隐私政策对话框 -->
    <PrivacyPolicyDialog v-model:visible="privacyDialogVisible" />
  </div>
</template>

<script setup lang="ts">
import { ref, provide } from 'vue'
import RegisterBackground from './components/RegisterBackground.vue'
import RegisterForm from './components/RegisterForm.vue'
import UserAgreementDialog from './components/UserAgreementDialog.vue'
import PrivacyPolicyDialog from './components/PrivacyPolicyDialog.vue'

// 对话框状态
const agreementDialogVisible = ref(false)
const privacyDialogVisible = ref(false)

// 显示用户协议
const showUserAgreement = () => {
  agreementDialogVisible.value = true
}

// 显示隐私政策
const showPrivacyPolicy = () => {
  privacyDialogVisible.value = true
}

// 提供给子组件使用
provide('showUserAgreement', showUserAgreement)
provide('showPrivacyPolicy', showPrivacyPolicy)
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 0;
}

.register-container {
  width: 100%;
  max-width: 1000px;
  min-height: 700px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
    min-height: auto;
    max-width: 400px;
    margin: 20px;
  }
}
</style>