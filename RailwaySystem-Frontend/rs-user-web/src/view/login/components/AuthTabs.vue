<template>
  <div class="auth-tabs">
    <div class="tabs-header">
      <div
        class="tab-item"
        :class="{ active: activeTab === 'login' }"
        @click="switchTab('login')"
      >
        <el-icon class="tab-icon"><User /></el-icon>
        <span>登录</span>
      </div>
      <div
        class="tab-item"
        :class="{ active: activeTab === 'register' }"
        @click="switchTab('register')"
      >
        <el-icon class="tab-icon"><UserFilled /></el-icon>
        <span>注册</span>
      </div>
      <div class="tab-indicator" :style="indicatorStyle"></div>
    </div>
    
    <div class="tabs-content">
      <transition name="slide" mode="out-in">
        <div v-if="activeTab === 'login'" key="login" class="tab-panel">
          <slot name="login"></slot>
        </div>
        <div v-else key="register" class="tab-panel">
          <slot name="register"></slot>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineEmits, defineProps, watch, nextTick } from 'vue'
import { User, UserFilled } from '@element-plus/icons-vue'

const emit = defineEmits(['tab-change'])

const props = defineProps({
  defaultTab: {
    type: String,
    default: 'login',
    validator: (value) => ['login', 'register'].includes(value)
  }
})

const activeTab = ref(props.defaultTab)
const isAnimating = ref(false)

// 计算指示器位置
const indicatorStyle = computed(() => {
  const left = activeTab.value === 'login' ? '0%' : '50%'
  return {
    transform: `translateX(${left})`,
    width: '50%'
  }
})

const switchTab = async (tab) => {
  if (activeTab.value !== tab && !isAnimating.value) {
    isAnimating.value = true
    activeTab.value = tab
    emit('tab-change', tab)
    
    // 等待动画完成
    await nextTick()
    setTimeout(() => {
      isAnimating.value = false
    }, 500)
  }
}

// 监听默认标签页变化
watch(() => props.defaultTab, (newTab) => {
  activeTab.value = newTab
}, { immediate: true })

// 暴露方法给父组件
defineExpose({
  switchTab,
  activeTab: computed(() => activeTab.value),
  isAnimating: computed(() => isAnimating.value)
})
</script>

<style scoped>
.auth-tabs {
  width: 100%;
  max-width: 420px;
  margin: 0 auto;
}

.tabs-header {
  position: relative;
  display: flex;
  background: #f5f8fc;
  border: 1px solid #dde6f0;
  border-radius: 10px;
  padding: 6px;
  margin-bottom: 40px;
  overflow: hidden;
  box-shadow: none;
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 16px 20px;
  cursor: pointer;
  transition: color 0.18s ease, background-color 0.18s ease;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #5e6b7e;
  position: relative;
  z-index: 2;
  letter-spacing: 0;
}

.tab-item:hover {
  color: #1677ff;
  transform: none;
}

.tab-item.active {
  color: #ffffff;
  background: #0b2559;
  box-shadow: 0 8px 18px rgba(11, 37, 89, 0.12);
  transform: none;
}

.tab-item.active:hover {
  color: #ffffff;
  transform: none;
}

.tab-icon {
  font-size: 20px;
  transition: color 0.18s ease;
}

.tab-item.active .tab-icon {
  filter: none;
}

.tab-indicator {
  position: absolute;
  top: 6px;
  bottom: 6px;
  background: #0b2559;
  border-radius: 12px;
  transition: transform 0.18s ease;
  box-shadow: 0 8px 18px rgba(11, 37, 89, 0.12);
  z-index: 1;
  opacity: 0;
}

.tabs-content {
  position: relative;
  overflow: hidden;
  border-radius: 16px;
}

.tab-panel {
  width: 100%;
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 12px 28px rgba(11, 37, 89, 0.08);
  border: 1px solid #dde6f0;
}

/* 切换动画 */
.slide-enter-active,
.slide-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.slide-enter-from {
  opacity: 0;
  transform: translateX(40px) scale(0.95);
}

.slide-leave-to {
  opacity: 0;
  transform: translateX(-40px) scale(0.95);
}

.slide-enter-to,
.slide-leave-from {
  opacity: 1;
  transform: translateX(0) scale(1);
}

/* 添加微妙的动画效果 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-2px);
  }
}

.auth-tabs:hover .tabs-header {
  animation: float 3s ease-in-out infinite;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .auth-tabs {
    max-width: 100%;
    padding: 0 16px;
  }
  
  .tabs-header {
    margin-bottom: 32px;
    padding: 4px;
  }
  
  .tab-item {
    padding: 14px 16px;
    font-size: 15px;
    gap: 8px;
    font-weight: 500;
  }
  
  .tab-icon {
    font-size: 18px;
  }
  
  .tab-panel {
    padding: 24px 20px;
  }
}

@media (max-width: 360px) {
  .tab-item {
    padding: 12px 14px;
    font-size: 14px;
    gap: 6px;
  }
  
  .tab-icon {
    font-size: 16px;
  }
  
  .tab-panel {
    padding: 20px 16px;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .tabs-header {
    background: #f5f8fc;
    border: 1px solid #dde6f0;
  }
  
  .tab-item {
    color: #5e6b7e;
  }
  
  .tab-item:hover {
    color: #1677ff;
  }
  
  .tab-item.active {
    background: #0b2559;
    color: #ffffff;
  }
  
  .tab-indicator {
    background: #0b2559;
  }
  
  .tab-panel {
    background: #ffffff;
    border: 1px solid #dde6f0;
  }
}

/* 高对比度模式支持 */
@media (prefers-contrast: high) {
  .tabs-header {
    border: 2px solid #000;
  }
  
  .tab-item.active {
    background: #000;
    color: #fff;
  }
  
  .tab-panel {
    border: 2px solid #000;
  }
}

.auth-tabs {
  max-width: 430px;
}

.tabs-header {
  padding: 4px;
  margin-bottom: 24px;
  border: 1px solid #dde6f0;
  border-radius: 10px;
  background: #f5f8fc;
  box-shadow: none;
}

.tab-item {
  min-height: 44px;
  padding: 12px 16px;
  gap: 8px;
  border-radius: 8px;
  color: #5e6b7e;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0;
  transition: color 0.18s ease, background-color 0.18s ease;
}

.tab-item:hover {
  color: #1677ff;
  transform: none;
}

.tab-item.active,
.tab-item.active:hover {
  color: #ffffff;
  background: #0b2559;
  box-shadow: 0 8px 18px rgba(11, 37, 89, 0.12);
  transform: none;
}

.tab-icon {
  font-size: 18px;
  filter: none;
}

.tab-indicator {
  display: none;
}

.tabs-content {
  overflow: visible;
  border-radius: 12px;
}

.tab-panel {
  padding: 28px;
  border: 1px solid #dde6f0;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 12px 28px rgba(11, 37, 89, 0.08);
}

.auth-tabs:hover .tabs-header {
  animation: none;
}

@media (max-width: 480px) {
  .auth-tabs {
    padding: 0;
  }

  .tabs-header {
    margin-bottom: 18px;
  }

  .tab-item {
    min-height: 42px;
    padding: 10px 12px;
    font-size: 14px;
  }

  .tab-panel {
    padding: 22px 16px;
  }
}
</style>
