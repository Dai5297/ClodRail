<template>
  <el-dialog
    v-model="visible"
    title="兑换成功"
    width="500px"
    :show-close="false"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
  >
    <div class="success-content">
      <div class="success-icon">
        <el-icon :size="80" color="#67c23a"><SuccessFilled /></el-icon>
      </div>
      <h3 class="success-title">兑换成功！</h3>
      <p class="success-message">您的商品正在准备中，我们会尽快为您发货</p>
      <div class="success-details">
        <p><strong>兑换单号:</strong> {{ exchangeOrderNumber }}</p>
        <p><strong>预计发货时间:</strong> 1-3个工作日</p>
      </div>
    </div>

    <template #footer>
      <div class="success-actions">
        <el-button @click="handleViewOrder">查看订单</el-button>
        <el-button type="primary" @click="handleContinueShopping">继续购物</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { computed } from 'vue'
import { SuccessFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

export default {
  name: 'SuccessModal',
  components: {
    SuccessFilled
  },
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    exchangeOrderNumber: {
      type: String,
      default: ''
    }
  },
  emits: ['update:modelValue', 'continue-shopping'],
  setup(props, { emit }) {
    const router = useRouter()

    const visible = computed({
      get: () => props.modelValue,
      set: (val) => emit('update:modelValue', val)
    })

    const handleViewOrder = () => {
      visible.value = false
      router.push('/user/order')
    }

    const handleContinueShopping = () => {
      visible.value = false
      emit('continue-shopping')
    }

    return {
      visible,
      handleViewOrder,
      handleContinueShopping
    }
  }
}
</script>

<style scoped>
.success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  margin-bottom: 20px;
}

.success-title {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.success-message {
  margin: 0 0 24px 0;
  font-size: 16px;
  color: #666;
}

.success-details {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  margin: 0 auto;
  max-width: 400px;
  text-align: left;
}

.success-details p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}

.success-details strong {
  color: #333;
  font-weight: 600;
}

.success-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
}
</style>
