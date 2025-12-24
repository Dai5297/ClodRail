<template>
  <el-dialog
    :model-value="visible"
    title="选择订单"
    width="600px"
    :before-close="handleClose"
    class="order-selector-dialog"
    append-to-body
  >
    <div class="order-list" v-loading="loading">
      <div v-if="orders.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无订单" />
      </div>
      
      <div
        v-for="order in orders"
        :key="order.id"
        class="order-item"
        @click="handleSelect(order)"
      >
        <div class="order-header">
          <span class="order-id">订单号: {{ order.orderId }}</span>
          <el-tag :type="getStatusType(order.status)" size="small">
            {{ getStatusText(order.status) }}
          </el-tag>
        </div>
        
        <div class="order-content">
          <div class="station-info">
            <div class="station">
              <span class="name">{{ order.startStation }}</span>
              <span class="time">{{ formatTime(order.startTime) }}</span>
            </div>
            <div class="arrow">
              <span class="train-no">{{ order.trainCode || 'G123' }}</span> <!-- Mock train code if not in DTO -->
              <el-icon><Right /></el-icon>
            </div>
            <div class="station">
              <span class="name">{{ order.endStation }}</span>
              <span class="time">{{ formatTime(order.endTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Right } from '@element-plus/icons-vue'
import { getOrderList } from '@/api/order'
import dayjs from 'dayjs'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'select'])

const loading = ref(false)
const orders = ref([])

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getOrderList()
    if (res.code === 200) {
      orders.value = res.data || []
    }
  } catch (error) {
  } finally {
    loading.value = false
  }
}

// 监听 visible 变化，显示时加载数据
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadOrders()
  }
})

const handleClose = () => {
  emit('update:visible', false)
}

const handleSelect = (order) => {
  emit('select', order)
  handleClose()
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return dayjs(time).format('MM-DD HH:mm')
}

// 状态显示
const getStatusType = (status) => {
  const map = {
    0: 'warning', // 待支付
    1: 'success', // 已支付
    2: 'success', // 已出票
    3: 'info',    // 已取消
    4: 'danger',  // 已退票
    5: 'primary'  // 已改签
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    0: '待支付',
    1: '已支付',
    2: '已出票',
    3: '已取消',
    4: '已退票',
    5: '已改签'
  }
  return map[status] || '未知'
}
</script>

<style scoped>
.order-list {
  max-height: 500px;
  overflow-y: auto;
  padding: 10px;
}

.order-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.order-item:hover {
  border-color: var(--el-color-primary);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.order-id {
  font-size: 13px;
  color: #909399;
}

.station-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.station {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.station .name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.station .time {
  font-size: 14px;
  color: #606266;
}

.arrow {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #909399;
}

.train-no {
  font-size: 12px;
  margin-bottom: 4px;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}
</style>
