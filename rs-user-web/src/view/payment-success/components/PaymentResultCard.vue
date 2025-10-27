<template>
  <div class="result-card">
    <div class="result-icon">
      <el-icon v-if="paymentSuccess" size="80" color="#52c41a">
        <CircleCheck />
      </el-icon>
      <el-icon v-else size="80" color="#ff4d4f">
        <CircleClose />
      </el-icon>
    </div>

    <div class="result-content">
      <h1 class="result-title">
        {{ paymentSuccess ? '支付成功！' : '支付失败' }}
      </h1>
      <p class="result-message">
        {{ resultMessage }}
      </p>

      <!-- 订单信息 -->
      <OrderInfoDisplay v-if="orderInfo" :order-info="orderInfo" />

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button size="large" @click="handleGoToOrders">
          <el-icon><Document /></el-icon>
          查看订单
        </el-button>
        <el-button type="primary" size="large" @click="handleGoToHome">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
      </div>

      <!-- 温馨提示 -->
      <TipsSection v-if="paymentSuccess" />
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <el-icon class="is-loading" size="40">
        <Loading />
      </el-icon>
      <p>正在处理支付结果...</p>
    </div>
  </div>
</template>

<script setup>
import { CircleCheck, CircleClose, Document, HomeFilled, Loading } from '@element-plus/icons-vue'
import OrderInfoDisplay from './OrderInfoDisplay.vue'
import TipsSection from './TipsSection.vue'

const props = defineProps({
  paymentSuccess: {
    type: Boolean,
    required: true
  },
  resultMessage: {
    type: String,
    required: true
  },
  orderInfo: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['go-to-orders', 'go-to-home'])

const handleGoToOrders = () => {
  emit('go-to-orders')
}

const handleGoToHome = () => {
  emit('go-to-home')
}
</script>

<style scoped>
.result-card {
  background: white;
  border-radius: 16px;
  padding: 48px 32px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  text-align: center;
  position: relative;
}

.result-icon {
  margin-bottom: 24px;
  animation: scaleIn 0.5s ease-out;
}

@keyframes scaleIn {
  from {
    transform: scale(0);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.result-content {
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

@keyframes fadeInUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.result-title {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
}

.result-message {
  font-size: 16px;
  color: #666;
  margin: 0 0 32px 0;
  line-height: 1.6;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 32px;
}

.action-buttons .el-button {
  flex: 1;
  max-width: 200px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  z-index: 10;
}

.loading-overlay p {
  margin-top: 16px;
  font-size: 16px;
  color: #666;
}

@media (max-width: 768px) {
  .result-card {
    padding: 32px 20px;
  }

  .result-title {
    font-size: 24px;
  }

  .result-message {
    font-size: 14px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    max-width: 100%;
  }
}
</style>

