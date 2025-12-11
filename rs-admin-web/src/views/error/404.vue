<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-code">404</div>
      <div class="error-title">页面未找到</div>
      <div class="error-description">
        抱歉,您访问的页面不存在或已被删除
      </div>
      <div class="error-actions">
        <el-button type="primary" @click="goHome" class="action-btn">
          返回首页
        </el-button>
        <el-button @click="goBack" class="action-btn">
          返回上一页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/profile')
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.error-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-color);
}

.error-container {
  text-align: center;
  max-width: 600px;
  padding: 60px 40px;
}

.error-code {
  font-size: 120px;
  font-weight: 700;
  color: var(--text-color);
  line-height: 1;
  margin-bottom: 20px;
  letter-spacing: -4px;
  background: linear-gradient(135deg, #000000 0%, #333333 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.error-title {
  font-size: 32px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 16px;
}

.error-description {
  font-size: 16px;
  color: var(--text-color-secondary);
  margin-bottom: 40px;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.action-btn {
  min-width: 140px;
  height: 42px;
  font-size: 14px;
  border-radius: 6px;
  font-weight: 500;
}

:deep(.el-button--primary) {
  background: #000000;
  border-color: #000000;
}

:deep(.el-button--primary:hover) {
  background: #333333;
  border-color: #333333;
}

:deep(.el-button--default) {
  background: transparent;
  border-color: var(--border-color);
  color: var(--text-color);
}

:deep(.el-button--default:hover) {
  background: var(--bg-color-secondary);
  border-color: var(--text-color);
}
</style>
