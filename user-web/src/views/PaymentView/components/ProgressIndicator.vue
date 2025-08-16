<script setup lang="ts">
// 定义Props
interface Props {
  currentStep: number
}

const props = defineProps<Props>()

// 步骤配置
const steps = [
  {
    step: 1,
    title: '选择车票',
    description: '选择车次和座位'
  },
  {
    step: 2,
    title: '确认支付',
    description: '填写订单信息'
  },
  {
    step: 3,
    title: '支付完成',
    description: '订单处理中'
  }
]
</script>

<template>
  <div class="progress-indicator">
    <div class="progress-container">
      <div 
        v-for="(stepItem, index) in steps" 
        :key="stepItem.step"
        class="progress-step"
        :class="{
          active: stepItem.step === currentStep,
          completed: stepItem.step < currentStep,
          pending: stepItem.step > currentStep
        }"
      >
        <!-- 步骤圆圈 -->
        <div class="step-circle">
          <span v-if="stepItem.step < currentStep" class="step-check">✓</span>
          <span v-else class="step-number">{{ stepItem.step }}</span>
        </div>
        
        <!-- 步骤信息 -->
        <div class="step-info">
          <div class="step-title">{{ stepItem.title }}</div>
          <div class="step-description">{{ stepItem.description }}</div>
        </div>
        
        <!-- 连接线 -->
        <div 
          v-if="index < steps.length - 1" 
          class="step-connector"
          :class="{
            completed: stepItem.step < currentStep
          }"
        ></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.progress-indicator {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
  margin-bottom: 20px;
}

.progress-container {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex: 1;
  max-width: 200px;
}

.step-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.progress-step.pending .step-circle {
  background: #f5f5f5;
  border: 2px solid #d9d9d9;
  color: #8c8c8c;
}

.progress-step.active .step-circle {
  background: #1890ff;
  border: 2px solid #1890ff;
  color: white;
  box-shadow: 0 0 0 4px rgba(24, 144, 255, 0.2);
}

.progress-step.completed .step-circle {
  background: #52c41a;
  border: 2px solid #52c41a;
  color: white;
}

.step-check {
  font-size: 18px;
  font-weight: bold;
}

.step-number {
  font-size: 16px;
  font-weight: 600;
}

.step-info {
  text-align: center;
  max-width: 120px;
}

.step-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  transition: color 0.3s ease;
}

.progress-step.pending .step-title {
  color: #8c8c8c;
}

.progress-step.active .step-title {
  color: #1890ff;
}

.progress-step.completed .step-title {
  color: #52c41a;
}

.step-description {
  font-size: 12px;
  color: #8c8c8c;
  line-height: 1.4;
}

.step-connector {
  position: absolute;
  top: 20px;
  left: 50%;
  right: -50%;
  height: 2px;
  background: #f0f0f0;
  z-index: 1;
  transition: background-color 0.3s ease;
}

.step-connector.completed {
  background: #52c41a;
}

.progress-step:last-child .step-connector {
  display: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .progress-indicator {
    padding: 16px;
  }
  
  .progress-container {
    flex-direction: column;
    gap: 20px;
  }
  
  .progress-step {
    flex-direction: row;
    max-width: none;
    width: 100%;
    text-align: left;
  }
  
  .step-circle {
    margin-bottom: 0;
    margin-right: 16px;
    flex-shrink: 0;
  }
  
  .step-info {
    text-align: left;
    max-width: none;
    flex: 1;
  }
  
  .step-connector {
    display: none;
  }
  
  /* 移动端垂直连接线 */
  .progress-step:not(:last-child)::after {
    content: '';
    position: absolute;
    left: 20px;
    top: 50px;
    width: 2px;
    height: 20px;
    background: #f0f0f0;
    z-index: 1;
  }
  
  .progress-step.completed:not(:last-child)::after {
    background: #52c41a;
  }
}

@media (max-width: 480px) {
  .step-circle {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
  
  .step-check {
    font-size: 16px;
  }
  
  .step-number {
    font-size: 14px;
  }
  
  .step-title {
    font-size: 13px;
  }
  
  .step-description {
    font-size: 11px;
  }
}
</style>