<template>
  <div class="order-info-card">
    <div class="card-header">
      <h3>订单详情</h3>
      <div class="order-status">
        <el-tag type="success" size="large">已支付</el-tag>
      </div>
    </div>
    
    <div class="order-details">
      <div class="detail-row">
        <span class="label">订单号：</span>
        <span class="value">{{ orderInfo.orderNumber }}</span>
        <el-button type="text" @click="copyOrderNumber">
          <el-icon><CopyDocument /></el-icon>
          复制
        </el-button>
      </div>
      
      <div class="detail-row">
        <span class="label">支付金额：</span>
        <span class="value amount">¥{{ orderInfo.amount }}</span>
      </div>
      
      <div class="detail-row">
        <span class="label">支付时间：</span>
        <span class="value">{{ orderInfo.paymentTime }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { CopyDocument } from '@element-plus/icons-vue'

// 定义props
const props = defineProps<{
  orderInfo: {
    orderId: string
    orderNumber: string
    amount: number
    paymentTime: string
  }
}>()

// 复制订单号
const copyOrderNumber = async () => {
  try {
    await navigator.clipboard.writeText(props.orderInfo.orderNumber)
    ElMessage.success('订单号已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败，请手动复制')
  }
}
</script>

<style scoped>
.order-info-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #262626;
}

.order-details {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.label {
  color: #8c8c8c;
  min-width: 80px;
}

.value {
  flex: 1;
  font-weight: 500;
}

.value.amount {
  color: #ff4d4f;
  font-size: 18px;
  font-weight: bold;
}
</style>