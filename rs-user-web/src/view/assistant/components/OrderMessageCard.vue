<template>
  <div class="order-message-card">
    <div class="card-header">
      <span class="title">订单详情</span>
      <el-tag :type="getStatusType(order.status)" size="small" effect="dark">
        {{ getStatusText(order.status) }}
      </el-tag>
    </div>
    
    <div class="card-content">
      <div class="station-row">
        <div class="station start">
          <span class="time">{{ formatTime(order.startTime) }}</span>
          <span class="name">{{ getStationName(order.startStation) }}</span>
        </div>
        <div class="train-arrow">
          <span class="train-code">{{ order.trainCode || 'G123' }}</span>
          <div class="arrow-line"></div>
        </div>
        <div class="station end">
          <span class="time">{{ formatTime(order.endTime) }}</span>
          <span class="name">{{ getStationName(order.endStation) }}</span>
        </div>
      </div>
      
      <div class="info-row">
        <span class="label">订单号：</span>
        <span class="value">{{ order.orderId }}</span>
      </div>
      
      <div class="price-row">
        <span class="label">总价：</span>
        <span class="price">¥{{ getPrice() }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import dayjs from 'dayjs'
import { getOrderDetail } from '@/api/order'
import { getStations } from '@/api/station'

const props = defineProps({
  order: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

const orderDetail = ref({})
const stationMap = ref({})

// 初始化数据
const initData = async () => {
  // 1. 获取站点列表映射
  try {
    const res = await getStations()
    if (res.code === 200 && res.data) {
      res.data.forEach(station => {
        stationMap.value[station.id] = station.name
        // 同时也映射字符串类型的ID
        stationMap.value[String(station.id)] = station.name
      })
    }
  } catch (error) {
  }

  // 2. 获取订单详情（为了获取价格等缺失信息）
  if (props.order.orderId) {
    try {
      const res = await getOrderDetail(props.order.orderId)
      if (res.code === 200 && res.data) {
        orderDetail.value = res.data
      }
    } catch (error) {
    }
  }
}

onMounted(() => {
  initData()
})

// 监听 props 变化
watch(() => props.order, () => {
  initData()
}, { deep: true })

const getStationName = (nameOrId) => {
  if (!nameOrId) return '未知站点'
  // 如果是数字或数字字符串，尝试从映射中获取
  if (/^\d+$/.test(String(nameOrId))) {
    return stationMap.value[String(nameOrId)] || nameOrId
  }
  return nameOrId
}

const getPrice = () => {
  // 优先使用详情中的价格
  if (orderDetail.value.totalAmount !== undefined) {
    return orderDetail.value.totalAmount
  }
  if (orderDetail.value.priceDetail && orderDetail.value.priceDetail.totalPrice !== undefined) {
    return orderDetail.value.priceDetail.totalPrice
  }
  // 其次使用 props 中的价格
  return props.order.amount || props.order.totalPrice || '0.00'
}

const formatTime = (time) => {
  if (!time) return '--:--'
  return dayjs(time).format('HH:mm')
}

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
.order-message-card {
  width: 280px;
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #ebeef5;
}

.card-header {
  padding: 10px 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.card-content {
  padding: 16px;
}

.station-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.station {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.station .time {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.station .name {
  font-size: 13px;
  color: #606266;
  margin-top: 4px;
}

.train-arrow {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  padding: 0 10px;
}

.train-code {
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

.arrow-line {
  width: 100%;
  height: 1px;
  background: #dcdfe6;
  position: relative;
}

.arrow-line::after {
  content: '';
  position: absolute;
  right: 0;
  top: -3px;
  width: 6px;
  height: 6px;
  border-top: 1px solid #dcdfe6;
  border-right: 1px solid #dcdfe6;
  transform: rotate(45deg);
}

.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 8px;
  color: #606266;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #ebeef5;
}

.price-row .label {
  font-size: 13px;
  color: #606266;
}

.price-row .price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}
</style>
