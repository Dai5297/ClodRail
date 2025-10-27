<template>
  <div class="payment-notice-card">
    <h4 class="notice-title">
      <el-icon><InfoFilled /></el-icon>
      支付说明
    </h4>
    <ul class="notice-list">
      <li>请在{{ minutes }}分钟内完成支付，超时订单将自动取消</li>
      <li>支付成功后，车票信息将发送至您的手机和邮箱</li>
      <li>如遇支付问题，请联系客服：400-123-4567</li>
      <li>退票改签请遵循铁路部门相关规定</li>
    </ul>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { InfoFilled } from '@element-plus/icons-vue'

const props = defineProps({
  remainingTime: {
    type: Number,
    required: true
  }
})

const minutes = computed(() => Math.floor(props.remainingTime / 60))
</script>

<style scoped>
.payment-notice-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.notice-title {
  font-size: 14px;
  color: #333;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.notice-list {
  margin: 0;
  padding-left: 16px;
  color: #666;
  font-size: 12px;
  line-height: 1.6;
}

.notice-list li {
  margin-bottom: 4px;
}
</style>

