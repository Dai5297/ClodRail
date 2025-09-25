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
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 16px;
  padding: 6px;
  margin-bottom: 40px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #64748b;
  position: relative;
  z-index: 2;
  letter-spacing: 0.5px;
}

.tab-item:hover {
  color: #3b82f6;
  transform: translateY(-1px);
}

.tab-item.active {
  color: #ffffff;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 
    0 4px 20px rgba(102, 126, 234, 0.4),
    0 2px 8px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.tab-item.active:hover {
  color: #ffffff;
  transform: translateY(-2px);
}

.tab-icon {
  font-size: 20px;
  transition: all 0.3s ease;
}

.tab-item.active .tab-icon {
  filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.3));
}

.tab-indicator {
  position: absolute;
  top: 6px;
  bottom: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 
    0 4px 20px rgba(102, 126, 234, 0.4),
    0 2px 8px rgba(0, 0, 0, 0.1);
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
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

/* 切换动画 */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
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
    background: linear-gradient(135deg, rgba(30, 41, 59, 0.9) 0%, rgba(51, 65, 85, 0.8) 100%);
    border: 1px solid rgba(148, 163, 184, 0.2);
  }
  
  .tab-item {
    color: #cbd5e1;
  }
  
  .tab-item:hover {
    color: #60a5fa;
  }
  
  .tab-item.active {
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
    color: #ffffff;
  }
  
  .tab-indicator {
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  }
  
  .tab-panel {
    background: rgba(30, 41, 59, 0.95);
    border: 1px solid rgba(148, 163, 184, 0.2);
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
</style>