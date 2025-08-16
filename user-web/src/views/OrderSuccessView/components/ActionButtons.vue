<template>
  <div class="action-buttons">
    <el-button 
      type="primary" 
      size="large" 
      @click="viewOrderDetail"
      class="action-btn"
    >
      <el-icon><Document /></el-icon>
      查看订单详情
    </el-button>
    
    <el-button 
      size="large" 
      @click="goToMyOrders"
      class="action-btn"
    >
      <el-icon><List /></el-icon>
      我的订单
    </el-button>
    
    <el-button 
      size="large" 
      @click="goToHome"
      class="action-btn"
    >
      <el-icon><HomeFilled /></el-icon>
      返回首页
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Document, List, HomeFilled } from '@element-plus/icons-vue'

// 定义props
const props = defineProps<{
  orderId: string
}>()

const router = useRouter()

// 查看订单详情
const viewOrderDetail = () => {
  router.push({
    name: 'OrderDetail',
    params: { id: props.orderId }
  })
}

// 跳转到我的订单
const goToMyOrders = () => {
  router.push({ name: 'MyOrders' })
}

// 返回首页
const goToHome = () => {
  router.push({ name: 'Home' })
}
</script>

<style scoped>
.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 40px;
}

.action-btn {
  min-width: 140px;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .action-btn {
    width: 100%;
    max-width: 280px;
  }
}
</style>