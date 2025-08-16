<script setup lang="ts">
import { computed } from 'vue'
import { ElCard, ElSkeleton, ElIcon, ElTag, ElDivider } from 'element-plus'
import { 
  User, 
  CreditCard, 
  Phone,
  Message,
  UserFilled
} from '@element-plus/icons-vue'

// 定义乘客接口
interface Passenger {
  id: string
  name: string
  idType: string
  idNumber: string
  phone: string
  passengerType: string
  ticketType: string
  seatNumber: string
  ticketPrice: number
}

// 定义属性
interface Props {
  passengers: Passenger[]
  loading: boolean
}

const props = withDefaults(defineProps<Props>(), {
  passengers: () => [],
  loading: false
})

// 证件类型映射
const idTypeMap: { [key: string]: string } = {
  'id_card': '身份证',
  'passport': '护照',
  'military_id': '军官证',
  'police_id': '警官证',
  'student_id': '学生证',
  'other': '其他'
}

// 乘客类型映射
const passengerTypeMap: { [key: string]: string } = {
  'adult': '成人',
  'child': '儿童',
  'student': '学生',
  'senior': '老人',
  'disabled': '残疾人'
}

// 票种类型映射
const ticketTypeMap: { [key: string]: string } = {
  'adult': '成人票',
  'child': '儿童票',
  'student': '学生票',
  'senior': '老人票',
  'disabled': '残疾人票'
}

// 获取证件类型显示名称
const getIdTypeName = (idType: string) => {
  return idTypeMap[idType] || idType
}

// 获取乘客类型显示名称
const getPassengerTypeName = (passengerType: string) => {
  return passengerTypeMap[passengerType] || passengerType
}

// 获取票种类型显示名称
const getTicketTypeName = (ticketType: string) => {
  return ticketTypeMap[ticketType] || ticketType
}

// 获取乘客类型颜色
const getPassengerTypeColor = (passengerType: string) => {
  const colorMap: { [key: string]: string } = {
    'adult': '#1890ff',
    'child': '#52c41a',
    'student': '#722ed1',
    'senior': '#fa8c16',
    'disabled': '#eb2f96'
  }
  return colorMap[passengerType] || '#8c8c8c'
}

// 脱敏处理身份证号
const maskIdNumber = (idNumber: string) => {
  if (!idNumber) return ''
  if (idNumber.length <= 8) return idNumber
  
  const start = idNumber.substring(0, 4)
  const end = idNumber.substring(idNumber.length - 4)
  const middle = '*'.repeat(idNumber.length - 8)
  
  return `${start}${middle}${end}`
}

// 脱敏处理手机号
const maskPhone = (phone: string) => {
  if (!phone) return ''
  if (phone.length !== 11) return phone
  
  return `${phone.substring(0, 3)}****${phone.substring(7)}`
}

// 计算总票价
const totalPrice = computed(() => {
  return props.passengers.reduce((sum, passenger) => sum + passenger.ticketPrice, 0)
})
</script>

<template>
  <ElCard class="passenger-details" shadow="never">
    <template #header>
      <div class="card-header">
        <ElIcon :size="20" color="#1890ff">
          <UserFilled />
        </ElIcon>
        <span class="header-title">乘客信息</span>
        <ElTag class="passenger-count" type="info">
          共{{ passengers.length }}人
        </ElTag>
      </div>
    </template>
    
    <!-- 加载状态 -->
    <ElSkeleton v-if="loading" animated>
      <template #template>
        <div class="skeleton-content">
          <el-skeleton-item 
            v-for="i in 2" 
            :key="i"
            variant="rect" 
            style="width: 100%; height: 120px; margin-bottom: 16px;" 
          />
        </div>
      </template>
    </ElSkeleton>
    
    <!-- 乘客列表 -->
    <div v-else class="passenger-content">
      <div 
        v-for="(passenger, index) in passengers" 
        :key="passenger.id"
        class="passenger-item"
      >
        <!-- 乘客基本信息 -->
        <div class="passenger-header">
          <div class="passenger-info">
            <div class="passenger-name">
              <ElIcon :size="18" color="#1890ff">
                <User />
              </ElIcon>
              <span class="name">{{ passenger.name }}</span>
              <ElTag
                :color="getPassengerTypeColor(passenger.passengerType)"
                class="type-tag"
                size="small"
              >
                {{ getPassengerTypeName(passenger.passengerType) }}
              </ElTag>
            </div>
            
            <div class="ticket-info">
              <span class="ticket-type">{{ getTicketTypeName(passenger.ticketType) }}</span>
              <span class="ticket-price">¥{{ passenger.ticketPrice.toFixed(2) }}</span>
            </div>
          </div>
          
          <div class="seat-info">
            <span class="seat-label">座位</span>
            <ElTag class="seat-number" type="warning">
              {{ passenger.seatNumber }}
            </ElTag>
          </div>
        </div>
        
        <!-- 乘客详细信息 -->
        <div class="passenger-details-grid">
          <div class="detail-item">
            <ElIcon :size="16" color="#666">
              <CreditCard />
            </ElIcon>
            <div class="detail-content">
              <span class="detail-label">证件信息</span>
              <span class="detail-value">
                {{ getIdTypeName(passenger.idType) }} {{ maskIdNumber(passenger.idNumber) }}
              </span>
            </div>
          </div>
          
          <div class="detail-item">
            <ElIcon :size="16" color="#666">
              <Phone />
            </ElIcon>
            <div class="detail-content">
              <span class="detail-label">联系电话</span>
              <span class="detail-value">{{ maskPhone(passenger.phone) }}</span>
            </div>
          </div>
        </div>
        
        <!-- 分隔线 -->
        <ElDivider 
          v-if="index < passengers.length - 1" 
          class="passenger-divider" 
        />
      </div>
      
      <!-- 费用汇总 -->
      <div class="price-summary">
        <ElDivider class="summary-divider">
          <ElIcon :size="16" color="#1890ff">
            <Message />
          </ElIcon>
          <span class="summary-title">费用明细</span>
        </ElDivider>
        
        <div class="summary-content">
          <div class="summary-row">
            <span class="summary-label">票价小计</span>
            <span class="summary-value">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          
          <div class="summary-row">
            <span class="summary-label">服务费</span>
            <span class="summary-value">¥0.00</span>
          </div>
          
          <div class="summary-row total">
            <span class="summary-label">合计金额</span>
            <span class="summary-value total-amount">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
        </div>
        
        <!-- 温馨提示 -->
        <div class="tips">
          <div class="tip-item">
            <span class="tip-icon">💡</span>
            <span class="tip-text">请携带有效证件原件乘车，证件信息需与购票信息一致</span>
          </div>
          <div class="tip-item">
            <span class="tip-icon">⏰</span>
            <span class="tip-text">建议提前30分钟到达车站，为安检和检票预留充足时间</span>
          </div>
        </div>
      </div>
    </div>
  </ElCard>
</template>

<style scoped>
.passenger-details {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1a1a1a;
}

.header-title {
  font-size: 16px;
  flex: 1;
}

.passenger-count {
  font-size: 12px;
  font-weight: 500;
}

.skeleton-content {
  padding: 0;
}

.passenger-content {
  padding: 0;
}

.passenger-item {
  margin-bottom: 0;
}

.passenger-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.passenger-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.passenger-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.name {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.type-tag {
  font-size: 12px;
  font-weight: 500;
  color: white !important;
  border: none;
  border-radius: 12px;
  padding: 2px 8px;
}

.ticket-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ticket-type {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.ticket-price {
  font-size: 16px;
  font-weight: 700;
  color: #ff4d4f;
  font-family: 'Arial', sans-serif;
}

.seat-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.seat-label {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
}

.seat-number {
  font-size: 14px;
  font-weight: 700;
  font-family: 'Courier New', monospace;
}

.passenger-details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.detail-label {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
}

.detail-value {
  font-size: 14px;
  color: #1a1a1a;
  font-weight: 600;
  font-family: 'Courier New', monospace;
}

.passenger-divider {
  margin: 24px 0;
  border-color: #f0f0f0;
}

.price-summary {
  margin-top: 24px;
}

.summary-divider {
  margin: 20px 0;
}

.summary-title {
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
  margin-left: 4px;
}

.summary-content {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.summary-row:last-child {
  margin-bottom: 0;
}

.summary-row.total {
  border-top: 1px solid #e8e8e8;
  padding-top: 8px;
  margin-top: 8px;
}

.summary-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.summary-value {
  font-size: 14px;
  color: #1a1a1a;
  font-weight: 600;
  font-family: 'Arial', sans-serif;
}

.total-amount {
  font-size: 18px;
  font-weight: 700;
  color: #ff4d4f;
}

.tips {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px 12px;
  background: #f6ffed;
  border-radius: 6px;
  border: 1px solid #d9f7be;
}

.tip-icon {
  font-size: 14px;
  flex-shrink: 0;
  margin-top: 1px;
}

.tip-text {
  font-size: 12px;
  color: #52c41a;
  font-weight: 500;
  line-height: 1.4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .passenger-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .seat-info {
    align-self: flex-start;
    flex-direction: row;
    gap: 8px;
  }
  
  .passenger-details-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .detail-item {
    padding: 10px;
  }
  
  .name {
    font-size: 16px;
  }
  
  .ticket-price {
    font-size: 14px;
  }
  
  .total-amount {
    font-size: 16px;
  }
}
</style>