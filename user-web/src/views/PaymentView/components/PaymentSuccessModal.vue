<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElDialog, ElButton } from 'element-plus'

// 定义Props
interface Props {
  visible: boolean
  orderNumber: string
}

const props = defineProps<Props>()

// 定义事件
const emit = defineEmits<{
  'update:visible': [value: boolean]
  action: [action: 'home' | 'orders']
}>()

// 动画状态
const showAnimation = ref(false)

// 处理关闭
const handleClose = () => {
  emit('update:visible', false)
}

// 处理操作
const handleAction = (action: 'home' | 'orders') => {
  emit('action', action)
}

// 监听visible变化，触发动画
watch(() => props.visible, (newVisible) => {
  if (newVisible) {
    setTimeout(() => {
      showAnimation.value = true
    }, 100)
  } else {
    showAnimation.value = false
  }
})
</script>

<template>
  <el-dialog
    :model-value="visible"
    title=""
    width="450px"
    :show-close="false"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="payment-success">
      <!-- 成功动画 -->
      <div class="success-animation">
        <div class="success-circle" :class="{ animate: showAnimation }">
          <div class="success-checkmark" :class="{ animate: showAnimation }">
            <div class="checkmark-stem"></div>
            <div class="checkmark-kick"></div>
          </div>
        </div>
      </div>
      
      <!-- 成功信息 -->
      <div class="success-info">
        <h2 class="success-title">支付成功！</h2>
        <p class="success-message">恭喜您，订单支付成功，车票正在为您出票中</p>
        
        <div class="order-info">
          <div class="info-item">
            <span class="info-label">订单号</span>
            <span class="info-value">{{ orderNumber }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">支付时间</span>
            <span class="info-value">{{ new Date().toLocaleString('zh-CN') }}</span>
          </div>
        </div>
      </div>
      
      <!-- 温馨提示 -->
      <div class="success-tips">
        <h4 class="tips-title">温馨提示</h4>
        <ul class="tips-list">
          <li>车票将在5分钟内发送到您的手机</li>
          <li>请提前30分钟到达车站候车</li>
          <li>携带有效身份证件进站乘车</li>
          <li>如需改签退票，请在开车前办理</li>
        </ul>
      </div>
      
      <!-- 操作按钮 -->
      <div class="success-actions">
        <el-button 
          size="large" 
          @click="handleAction('home')"
        >
          返回首页
        </el-button>
        <el-button 
          type="primary" 
          size="large" 
          @click="handleAction('orders')"
        >
          查看订单
        </el-button>
      </div>
      
      <!-- 额外服务 -->
      <div class="extra-services">
        <div class="service-item">
          <span class="service-icon">📱</span>
          <span class="service-text">下载APP获取更多优惠</span>
        </div>
        <div class="service-item">
          <span class="service-icon">⭐</span>
          <span class="service-text">本次消费获得{{ Math.floor(Math.random() * 100) + 50 }}积分</span>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<style scoped>
.payment-success {
  text-align: center;
  padding: 20px 0;
}

/* 成功动画样式 */
.success-animation {
  margin-bottom: 24px;
}

.success-circle {
  width: 80px;
  height: 80px;
  border: 4px solid #52c41a;
  border-radius: 50%;
  margin: 0 auto;
  position: relative;
  transform: scale(0);
  transition: transform 0.3s ease-in-out;
}

.success-circle.animate {
  transform: scale(1);
}

.success-checkmark {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
}

.success-checkmark.animate .checkmark-stem {
  animation: checkmark-stem 0.3s ease-in-out 0.3s both;
}

.success-checkmark.animate .checkmark-kick {
  animation: checkmark-kick 0.2s ease-in-out 0.5s both;
}

.checkmark-stem {
  position: absolute;
  width: 3px;
  height: 18px;
  background: #52c41a;
  left: 18px;
  top: 18px;
  transform: rotate(45deg);
  transform-origin: bottom;
  opacity: 0;
}

.checkmark-kick {
  position: absolute;
  width: 12px;
  height: 3px;
  background: #52c41a;
  left: 12px;
  top: 30px;
  transform: rotate(-45deg);
  transform-origin: bottom;
  opacity: 0;
}

@keyframes checkmark-stem {
  0% {
    height: 0;
    opacity: 1;
  }
  100% {
    height: 18px;
    opacity: 1;
  }
}

@keyframes checkmark-kick {
  0% {
    width: 0;
    opacity: 1;
  }
  100% {
    width: 12px;
    opacity: 1;
  }
}

/* 成功信息样式 */
.success-info {
  margin-bottom: 24px;
}

.success-title {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 600;
  color: #52c41a;
}

.success-message {
  margin: 0 0 20px 0;
  font-size: 16px;
  color: #595959;
  line-height: 1.5;
}

.order-info {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 8px;
  padding: 16px;
  margin: 0 auto;
  max-width: 300px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 14px;
  color: #8c8c8c;
}

.info-value {
  font-size: 14px;
  color: #262626;
  font-weight: 500;
}

/* 温馨提示样式 */
.success-tips {
  text-align: left;
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.tips-title {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #d46b08;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tips-title::before {
  content: '💡';
  font-size: 16px;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  list-style: none;
}

.tips-list li {
  position: relative;
  font-size: 13px;
  color: #8c8c8c;
  line-height: 1.6;
  margin-bottom: 4px;
}

.tips-list li::before {
  content: '•';
  color: #faad14;
  position: absolute;
  left: -12px;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

/* 操作按钮样式 */
.success-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 24px;
}

.success-actions .el-button {
  min-width: 120px;
}

/* 额外服务样式 */
.extra-services {
  display: flex;
  justify-content: center;
  gap: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.service-icon {
  font-size: 14px;
}

.service-text {
  font-size: 12px;
  color: #8c8c8c;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .payment-success {
    padding: 16px 0;
  }
  
  .success-circle {
    width: 60px;
    height: 60px;
  }
  
  .success-checkmark {
    width: 30px;
    height: 30px;
  }
  
  .checkmark-stem {
    height: 14px;
    left: 14px;
    top: 14px;
  }
  
  .checkmark-kick {
    width: 9px;
    left: 9px;
    top: 23px;
  }
  
  @keyframes checkmark-stem {
    0% {
      height: 0;
      opacity: 1;
    }
    100% {
      height: 14px;
      opacity: 1;
    }
  }
  
  @keyframes checkmark-kick {
    0% {
      width: 0;
      opacity: 1;
    }
    100% {
      width: 9px;
      opacity: 1;
    }
  }
  
  .success-title {
    font-size: 20px;
  }
  
  .success-message {
    font-size: 14px;
  }
  
  .success-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .success-actions .el-button {
    width: 200px;
  }
  
  .extra-services {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
}
</style>