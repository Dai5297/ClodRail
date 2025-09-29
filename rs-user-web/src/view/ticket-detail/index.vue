<template>
  <div class="ticket-detail-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <el-button 
          type="text" 
          @click="goBack" 
          class="back-btn"
        >
          <el-icon><ArrowLeft /></el-icon>
          返回车票列表
        </el-button>
        <h2 class="page-title">车票详情</h2>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 主要内容 -->
    <div v-else class="main-content">
      <div class="container">
        <!-- 车次信息卡片 -->
        <div class="train-info-card">
          <div class="train-header">
            <div class="train-number">{{ ticketDetail.trainCode }}</div>
            <div class="train-type">{{ ticketDetail.trainType }}</div>
            <div class="train-date">{{ formatDate(searchParams.date) }}</div>
          </div>
          
          <div class="route-info">
            <div class="station-info departure">
              <div class="time">{{ ticketDetail.startTime }}</div>
              <div class="station">{{ ticketDetail.originStation.name }}</div>
              <div class="platform">{{ ticketDetail.originStation.platform }}站台</div>
            </div>
            
            <div class="journey-info">
              <div class="duration">
                <el-icon><Clock /></el-icon>
                <span>{{ ticketDetail.duration }}</span>
              </div>
              <div class="route-line">
                <div class="line"></div>
                <div class="stops">{{ ticketDetail.stopCount }}站</div>
              </div>
            </div>
            
            <div class="station-info arrival">
              <div class="time">{{ ticketDetail.endTime }}</div>
              <div class="station">{{ ticketDetail.destinationStation.name }}</div>
              <div class="platform">{{ ticketDetail.destinationStation.platform }}站台</div>
            </div>
          </div>
        </div>

        <!-- 座位选择区域 -->
        <div class="seat-selection-card">
          <h3 class="card-title">
            <el-icon><Grid /></el-icon>
            选择座位类型
          </h3>
          
          <div class="seat-types">
            <div 
              v-for="seatType in ticketDetail.seatTypes" 
              :key="seatType.type"
              class="seat-type-item"
              :class="{ 
                'selected': selectedSeatType?.type === seatType.type,
                'sold-out': seatType.remainingSeats === 0 
              }"
              @click="selectSeatType(seatType)"
            >
              <div class="seat-type-header">
                <div class="seat-name">{{ seatType.name }}</div>
                <div class="seat-price">￥{{ seatType.price }}</div>
              </div>
              <div class="seat-info">
                <div class="remaining">
                  {{ seatType.remainingSeats > 0 ? `余票${seatType.remainingSeats}张` : '无票' }}
                </div>
                <div class="features">{{ seatType.features }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 乘客信息区域 -->
        <div class="passenger-info-card">
          <div class="card-header">
            <h3 class="card-title">
              <el-icon><User /></el-icon>
              乘客信息
            </h3>
            <el-button 
              type="primary" 
              size="small" 
              @click="showContactModal = true"
              :disabled="!selectedSeatType"
            >
              <el-icon><Plus /></el-icon>
              添加乘客
            </el-button>
          </div>

          <div v-if="selectedPassengers.length === 0" class="no-passengers">
            <el-empty description="请先选择座位类型，然后添加乘客信息" />
          </div>

          <div v-else class="passenger-list">
            <div 
              v-for="(passenger, index) in selectedPassengers" 
              :key="index"
              class="passenger-item"
            >
              <div class="passenger-info">
                <div class="passenger-name">{{ passenger.name }}</div>
                <div class="passenger-details">
                  <span class="id-type">{{ passenger.idType }}</span>
                  <span class="id-number">{{ passenger.idNumber }}</span>
                  <span class="passenger-type">{{ passenger.passengerType }}</span>
                </div>
              </div>
              <div class="passenger-actions">
                <el-button 
                  type="text" 
                  size="small" 
                  @click="removePassenger(index)"
                >
                  <el-icon><Delete /></el-icon>
                  移除
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 订单摘要 -->
        <div class="order-summary-card">
          <h3 class="card-title">
            <el-icon><Document /></el-icon>
            订单摘要
          </h3>
          
          <div class="summary-content">
            <div class="summary-item">
              <span class="label">车次信息：</span>
              <span class="value">{{ ticketDetail.trainCode }} {{ formatDate(searchParams.date) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">出发到达：</span>
              <span class="value">
                {{ ticketDetail.originStation.name }} → {{ ticketDetail.destinationStation.name }}
              </span>
            </div>
            <div class="summary-item">
              <span class="label">座位类型：</span>
              <span class="value">{{ selectedSeatType?.name || '未选择' }}</span>
            </div>
            <div class="summary-item">
              <span class="label">乘客数量：</span>
              <span class="value">{{ selectedPassengers.length }}人</span>
            </div>
            <div class="summary-item total">
              <span class="label">总金额：</span>
              <span class="value">￥{{ totalAmount }}</span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button size="large" @click="goBack">取消</el-button>
          <el-button 
            type="primary" 
            size="large" 
            @click="proceedToPayment"
            :disabled="!canProceed"
            :loading="submitting"
          >
            立即预订
          </el-button>
        </div>
      </div>
    </div>

    <!-- 联系人选择弹窗 -->
    <el-dialog
      v-model="showContactModal"
      title="选择乘客"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="contact-modal">
        <div class="contact-list">
          <div 
            v-for="contact in contacts" 
            :key="contact.id"
            class="contact-item"
            :class="{ 'selected': isContactSelected(contact) }"
            @click="toggleContact(contact)"
          >
            <div class="contact-info">
              <div class="contact-name">{{ contact.name }}</div>
              <div class="contact-details">
                <span>{{ contact.idType }} {{ contact.idNumber }}</span>
                <span class="contact-type">{{ contact.passengerType }}</span>
              </div>
            </div>
            <div class="contact-actions">
              <el-checkbox 
                :model-value="isContactSelected(contact)"
                @change="toggleContact(contact)"
              />
            </div>
          </div>
        </div>
        
        <div v-if="contacts.length === 0" class="no-contacts">
          <el-empty description="暂无联系人，请先添加联系人信息" />
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showContactModal = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="confirmPassengerSelection"
            :disabled="tempSelectedContacts.length === 0"
          >
            确认选择 ({{ tempSelectedContacts.length }})
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Clock, Grid, User, Plus, Delete, Document 
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(true)
const submitting = ref(false)
const showContactModal = ref(false)

// 搜索参数
const searchParams = reactive({
  trainId: route.query.trainId,
  date: route.query.date,
  originStationId: route.query.originStationId,
  destinationStationId: route.query.destinationStationId
})

// 车票详情数据
const ticketDetail = ref({
  trainCode: '',
  trainType: '',
  startTime: '',
  endTime: '',
  duration: '',
  stopCount: 0,
  originStation: { name: '', platform: '' },
  destinationStation: { name: '', platform: '' },
  seatTypes: []
})

// 选择的座位类型和乘客
const selectedSeatType = ref(null)
const selectedPassengers = ref([])
const contacts = ref([])
const tempSelectedContacts = ref([])

// 计算属性
const totalAmount = computed(() => {
  if (!selectedSeatType.value || selectedPassengers.value.length === 0) {
    return 0
  }
  return selectedSeatType.value.price * selectedPassengers.value.length
})

const canProceed = computed(() => {
  return selectedSeatType.value && selectedPassengers.value.length > 0
})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  const weekday = weekdays[date.getDay()]
  return `${year}-${month}-${day} 周${weekday}`
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 获取车票详情
const loadTicketDetail = async () => {
  try {
    loading.value = true
    const response = await request.post('/tickets/detail', {
      trainId: searchParams.trainId,
      date: searchParams.date,
      originStationId: parseInt(searchParams.originStationId),
      destinationStationId: parseInt(searchParams.destinationStationId)
    })
    
    if (response.success) {
      ticketDetail.value = response.data
    } else {
      ElMessage.error(response.message || '获取车票详情失败')
    }
  } catch (error) {
    console.error('获取车票详情失败:', error)
    ElMessage.error('获取车票详情失败')
  } finally {
    loading.value = false
  }
}

// 获取联系人列表
const loadContacts = async () => {
  try {
    const response = await request.get('/contacts')
    if (response.success) {
      contacts.value = response.data
    }
  } catch (error) {
    console.error('获取联系人列表失败:', error)
  }
}

// 选择座位类型
const selectSeatType = (seatType) => {
  if (seatType.remainingSeats === 0) {
    ElMessage.warning('该座位类型已售完')
    return
  }
  
  selectedSeatType.value = seatType
  // 清空已选乘客，因为座位类型变了
  selectedPassengers.value = []
}

// 检查联系人是否已选择
const isContactSelected = (contact) => {
  return tempSelectedContacts.value.some(c => c.id === contact.id)
}

// 切换联系人选择状态
const toggleContact = (contact) => {
  const index = tempSelectedContacts.value.findIndex(c => c.id === contact.id)
  if (index > -1) {
    tempSelectedContacts.value.splice(index, 1)
  } else {
    // 检查是否超过座位数量限制
    if (selectedSeatType.value && 
        tempSelectedContacts.value.length >= selectedSeatType.value.remainingSeats) {
      ElMessage.warning(`最多只能选择${selectedSeatType.value.remainingSeats}位乘客`)
      return
    }
    tempSelectedContacts.value.push(contact)
  }
}

// 确认乘客选择
const confirmPassengerSelection = () => {
  selectedPassengers.value = [...tempSelectedContacts.value]
  showContactModal.value = false
  tempSelectedContacts.value = []
}

// 移除乘客
const removePassenger = (index) => {
  selectedPassengers.value.splice(index, 1)
}

// 前往支付页面
const proceedToPayment = async () => {
  try {
    submitting.value = true
    
    // 创建订单
    const orderData = {
      trainId: searchParams.trainId,
      date: searchParams.date,
      originStationId: parseInt(searchParams.originStationId),
      destinationStationId: parseInt(searchParams.destinationStationId),
      seatType: selectedSeatType.value.type,
      passengers: selectedPassengers.value.map(p => ({
        name: p.name,
        idType: p.idType,
        idNumber: p.idNumber,
        passengerType: p.passengerType
      }))
    }
    
    const response = await request.post('/orders', orderData)
    
    if (response.success) {
      // 跳转到支付页面
      router.push({
        path: '/ticket-payment',
        query: {
          orderId: response.data.orderId
        }
      })
    } else {
      ElMessage.error(response.message || '创建订单失败')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败')
  } finally {
    submitting.value = false
  }
}

// 组件挂载时初始化
onMounted(async () => {
  // 验证必要参数
  if (!searchParams.trainId || !searchParams.date || 
      !searchParams.originStationId || !searchParams.destinationStationId) {
    ElMessage.error('缺少必要参数')
    router.back()
    return
  }
  
  await Promise.all([
    loadTicketDetail(),
    loadContacts()
  ])
})
</script>

<style scoped>
.ticket-detail-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.page-header {
  background: white;
  padding: 20px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-btn {
  color: #1890ff;
  font-size: 14px;
  margin-bottom: 10px;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.loading-container {
  padding: 40px 20px;
}

.main-content {
  padding: 0 20px 40px;
}

/* 车次信息卡片 */
.train-info-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.train-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.train-number {
  font-size: 28px;
  font-weight: 600;
  color: #1890ff;
}

.train-type {
  background: #f0f9ff;
  color: #1890ff;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
}

.train-date {
  color: #666;
  font-size: 16px;
}

.route-info {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 40px;
  align-items: center;
}

.station-info {
  text-align: center;
}

.station-info.departure {
  text-align: left;
}

.station-info.arrival {
  text-align: right;
}

.time {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.station {
  font-size: 18px;
  color: #333;
  margin-bottom: 4px;
}

.platform {
  font-size: 14px;
  color: #999;
}

.journey-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.duration {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 14px;
}

.route-line {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.line {
  width: 100px;
  height: 2px;
  background: linear-gradient(to right, #1890ff, #52c41a);
  border-radius: 1px;
}

.stops {
  font-size: 12px;
  color: #999;
}

/* 座位选择卡片 */
.seat-selection-card,
.passenger-info-card,
.order-summary-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-title {
  font-size: 18px;
  color: #333;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.seat-types {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.seat-type-item {
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.seat-type-item:hover {
  border-color: #1890ff;
}

.seat-type-item.selected {
  border-color: #1890ff;
  background-color: #f0f9ff;
}

.seat-type-item.sold-out {
  background-color: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.6;
}

.seat-type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.seat-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.seat-price {
  font-size: 20px;
  font-weight: 600;
  color: #ff6b35;
}

.seat-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.remaining {
  font-size: 14px;
  color: #52c41a;
}

.features {
  font-size: 12px;
  color: #999;
}

/* 乘客信息 */
.no-passengers {
  padding: 40px 0;
  text-align: center;
}

.passenger-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.passenger-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  background-color: #fafafa;
}

.passenger-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.passenger-details {
  display: flex;
  gap: 12px;
  font-size: 14px;
  color: #666;
}

/* 订单摘要 */
.summary-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.summary-item.total {
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
  margin-top: 8px;
  font-size: 18px;
  font-weight: 600;
}

.label {
  color: #666;
}

.value {
  color: #333;
  font-weight: 500;
}

.summary-item.total .value {
  color: #ff6b35;
  font-size: 24px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 30px;
}

.action-buttons .el-button {
  min-width: 120px;
  height: 48px;
  font-size: 16px;
}

/* 联系人弹窗 */
.contact-modal {
  max-height: 400px;
  overflow-y: auto;
}

.contact-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.contact-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.contact-item:hover {
  border-color: #1890ff;
  background-color: #f0f9ff;
}

.contact-item.selected {
  border-color: #1890ff;
  background-color: #f0f9ff;
}

.contact-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.contact-details {
  display: flex;
  gap: 12px;
  font-size: 14px;
  color: #666;
}

.contact-type {
  background: #f0f0f0;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.no-contacts {
  padding: 40px 0;
  text-align: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .route-info {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .station-info.departure,
  .station-info.arrival {
    text-align: center;
  }
  
  .seat-types {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    width: 100%;
  }
}
</style>