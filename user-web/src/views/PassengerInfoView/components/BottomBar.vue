<template>
  <div class="bottom-bar">
    <div class="container">
      <div class="price-summary">
        <div class="price-item">
          <span>车票费用：</span>
          <span>¥{{ orderInfo.totalPrice }}</span>
        </div>
        <div class="price-item" v-if="servicePrice > 0">
          <span>增值服务：</span>
          <span>¥{{ servicePrice }}</span>
        </div>
        <div class="total-price">
          <span>总计：</span>
          <span class="price">¥{{ finalPrice }}</span>
        </div>
      </div>
      
      <div class="actions">
        <el-button @click="$emit('go-back')">返回</el-button>
        <el-button 
          type="primary" 
          @click="$emit('submit-order')"
          :loading="submitting"
        >
          提交订单
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 定义props
const props = defineProps<{
  orderInfo: {
    totalPrice: number
  }
  servicePrice: number
  finalPrice: number
  submitting: boolean
}>()

// 定义emits
const emit = defineEmits<{
  'go-back': []
  'submit-order': []
}>()
</script>

<style scoped>
/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-top: 1px solid #e8e8e8;
  padding: 16px 0;
  z-index: 100;
}

.bottom-bar .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-summary {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  min-width: 200px;
  font-size: 14px;
}

.total-price {
  display: flex;
  justify-content: space-between;
  font-size: 18px;
  font-weight: bold;
}

.price {
  color: #ff4d4f;
}

.actions {
  display: flex;
  gap: 12px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .bottom-bar .container {
    flex-direction: column;
    gap: 16px;
  }
  
  .price-summary {
    width: 100%;
  }
  
  .container {
    padding: 0 16px;
  }
}
</style>