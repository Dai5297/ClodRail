<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { 
  ElDialog, 
  ElForm, 
  ElFormItem, 
  ElInput, 
  ElRadioGroup, 
  ElRadio, 
  ElButton, 
  ElAlert,
  ElIcon,
  ElTag
} from 'element-plus'
import { RefreshLeft, Warning, InfoFilled } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

// 定义属性
interface Order {
  id: string
  orderNumber: string
  trainNumber: string
  departure: string
  arrival: string
  departureTime: string
  totalAmount: number
  passengers: Array<{ name: string; idCard: string }>
}

interface Props {
  visible: boolean
  order: Order | null
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  order: null
})

// 定义事件
const emit = defineEmits<{
  'update:visible': [visible: boolean]
  submit: [refundData: any]
}>()

// 表单引用
const formRef = ref<FormInstance>()

// 表单数据
const formData = ref({
  reason: '',
  customReason: '',
  refundAmount: 0
})

// 提交状态
const submitting = ref(false)

// 退票原因选项
const refundReasons = [
  { value: 'schedule_change', label: '行程变更' },
  { value: 'emergency', label: '紧急情况' },
  { value: 'illness', label: '身体不适' },
  { value: 'weather', label: '天气原因' },
  { value: 'work', label: '工作安排' },
  { value: 'other', label: '其他原因' }
]

// 表单验证规则
const formRules: FormRules = {
  reason: [
    { required: true, message: '请选择退票原因', trigger: 'change' }
  ],
  customReason: [
    {
      validator: (rule, value, callback) => {
        if (formData.value.reason === 'other' && !value) {
          callback(new Error('请填写具体原因'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 计算退票手续费
const refundFee = computed(() => {
  if (!props.order) return 0
  
  const departureTime = new Date(props.order.departureTime)
  const now = new Date()
  const timeDiff = departureTime.getTime() - now.getTime()
  const hoursDiff = timeDiff / (1000 * 60 * 60)
  
  // 根据退票时间计算手续费
  if (hoursDiff > 48) {
    return props.order.totalAmount * 0.05 // 5%手续费
  } else if (hoursDiff > 24) {
    return props.order.totalAmount * 0.10 // 10%手续费
  } else if (hoursDiff > 2) {
    return props.order.totalAmount * 0.20 // 20%手续费
  } else {
    return props.order.totalAmount * 0.50 // 50%手续费
  }
})

// 计算实际退款金额
const actualRefundAmount = computed(() => {
  if (!props.order) return 0
  return Math.max(0, props.order.totalAmount - refundFee.value)
})

// 获取退票规则提示
const refundRuleText = computed(() => {
  if (!props.order) return ''
  
  const departureTime = new Date(props.order.departureTime)
  const now = new Date()
  const timeDiff = departureTime.getTime() - now.getTime()
  const hoursDiff = timeDiff / (1000 * 60 * 60)
  
  if (hoursDiff > 48) {
    return '距离发车时间超过48小时，收取5%手续费'
  } else if (hoursDiff > 24) {
    return '距离发车时间24-48小时，收取10%手续费'
  } else if (hoursDiff > 2) {
    return '距离发车时间2-24小时，收取20%手续费'
  } else if (hoursDiff > 0) {
    return '距离发车时间不足2小时，收取50%手续费'
  } else {
    return '发车时间已过，无法退票'
  }
})

// 是否可以退票
const canRefund = computed(() => {
  if (!props.order) return false
  
  const departureTime = new Date(props.order.departureTime)
  const now = new Date()
  
  return departureTime.getTime() > now.getTime()
})

// 关闭模态框
const handleClose = () => {
  emit('update:visible', false)
  resetForm()
}

// 重置表单
const resetForm = () => {
  formData.value = {
    reason: '',
    customReason: '',
    refundAmount: 0
  }
  formRef.value?.resetFields()
}

// 提交退票申请
const handleSubmit = async () => {
  if (!formRef.value || !props.order) return
  
  try {
    await formRef.value.validate()
    
    submitting.value = true
    
    const refundData = {
      orderId: props.order.id,
      reason: formData.value.reason === 'other' ? formData.value.customReason : refundReasons.find(r => r.value === formData.value.reason)?.label,
      amount: actualRefundAmount.value,
      fee: refundFee.value
    }
    
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    emit('submit', refundData)
    
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

// 监听visible变化
watch(() => props.visible, (newVal) => {
  if (newVal && props.order) {
    formData.value.refundAmount = actualRefundAmount.value
  } else if (!newVal) {
    resetForm()
  }
})
</script>

<template>
  <ElDialog
    :model-value="visible"
    title="申请退票"
    width="600px"
    :before-close="handleClose"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div v-if="order" class="refund-modal">
      <!-- 订单信息 -->
      <div class="order-summary">
        <div class="summary-header">
          <ElIcon><RefreshLeft /></ElIcon>
          <span>退票订单信息</span>
        </div>
        
        <div class="summary-content">
          <div class="trip-info">
            <div class="route">
              <span class="station">{{ order.departure }}</span>
              <span class="arrow">→</span>
              <span class="station">{{ order.arrival }}</span>
            </div>
            <div class="details">
              <ElTag type="info">{{ order.trainNumber }}</ElTag>
              <span class="time">{{ order.departureTime }}</span>
            </div>
          </div>
          
          <div class="amount-info">
            <div class="original-amount">
              <span class="label">订单金额：</span>
              <span class="amount">¥{{ order.totalAmount.toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 退票规则提示 -->
      <ElAlert
        :title="refundRuleText"
        type="warning"
        :closable="false"
        show-icon
        class="refund-rule-alert"
      />
      
      <!-- 无法退票提示 -->
      <ElAlert
        v-if="!canRefund"
        title="该订单已无法退票"
        description="发车时间已过，无法申请退票"
        type="error"
        :closable="false"
        show-icon
        class="no-refund-alert"
      />
      
      <!-- 退票表单 -->
      <ElForm
        v-if="canRefund"
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="refund-form"
      >
        <ElFormItem label="退票原因" prop="reason">
          <ElRadioGroup v-model="formData.reason" class="reason-group">
            <ElRadio
              v-for="reason in refundReasons"
              :key="reason.value"
              :label="reason.value"
              class="reason-radio"
            >
              {{ reason.label }}
            </ElRadio>
          </ElRadioGroup>
        </ElFormItem>
        
        <ElFormItem
          v-if="formData.reason === 'other'"
          label="具体原因"
          prop="customReason"
        >
          <ElInput
            v-model="formData.customReason"
            type="textarea"
            :rows="3"
            placeholder="请详细说明退票原因..."
            maxlength="200"
            show-word-limit
          />
        </ElFormItem>
      </ElForm>
      
      <!-- 费用明细 -->
      <div v-if="canRefund" class="fee-details">
        <div class="details-header">
          <ElIcon><InfoFilled /></ElIcon>
          <span>退款明细</span>
        </div>
        
        <div class="details-content">
          <div class="fee-item">
            <span class="fee-label">订单金额</span>
            <span class="fee-amount">¥{{ order.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="fee-item">
            <span class="fee-label">退票手续费</span>
            <span class="fee-amount fee-deduction">-¥{{ refundFee.toFixed(2) }}</span>
          </div>
          <div class="fee-item total">
            <span class="fee-label">实际退款</span>
            <span class="fee-amount refund-amount">¥{{ actualRefundAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>
      
      <!-- 温馨提示 -->
      <div v-if="canRefund" class="tips">
        <div class="tips-header">
          <ElIcon><Warning /></ElIcon>
          <span>温馨提示</span>
        </div>
        <ul class="tips-list">
          <li>退票申请提交后，我们将在1-3个工作日内处理</li>
          <li>退款将原路返回至您的支付账户</li>
          <li>退票成功后，车票将自动失效</li>
          <li>如有疑问，请联系客服：400-123-4567</li>
        </ul>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="handleClose">取消</ElButton>
        <ElButton
          v-if="canRefund"
          type="primary"
          :loading="submitting"
          @click="handleSubmit"
        >
          {{ submitting ? '提交中...' : '确认退票' }}
        </ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<style scoped>
.refund-modal {
  padding: 0;
}

.order-summary {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid #f0f0f0;
}

.summary-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 12px;
}

.summary-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.trip-info {
  flex: 1;
}

.route {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.station {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.arrow {
  color: #8c8c8c;
}

.details {
  display: flex;
  align-items: center;
  gap: 12px;
}

.time {
  font-size: 14px;
  color: #666;
}

.amount-info {
  text-align: right;
}

.original-amount {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.label {
  font-size: 12px;
  color: #8c8c8c;
}

.amount {
  font-size: 18px;
  font-weight: 600;
  color: #ff4d4f;
}

.refund-rule-alert {
  margin-bottom: 16px;
}

.no-refund-alert {
  margin-bottom: 16px;
}

.refund-form {
  margin-bottom: 16px;
}

.reason-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reason-radio {
  margin-right: 0;
  margin-bottom: 8px;
}

.fee-details {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid #e9ecef;
}

.details-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 12px;
}

.details-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.fee-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.fee-item.total {
  border-top: 1px solid #e9ecef;
  padding-top: 12px;
  margin-top: 4px;
}

.fee-label {
  font-size: 14px;
  color: #666;
}

.fee-amount {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
}

.fee-deduction {
  color: #ff4d4f;
}

.refund-amount {
  font-size: 16px;
  color: #52c41a;
}

.tips {
  background: #e6f4ff;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #91caff;
}

.tips-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 8px;
}

.tips-list {
  margin: 0;
  padding-left: 16px;
  color: #666;
}

.tips-list li {
  font-size: 12px;
  line-height: 1.6;
  margin-bottom: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .summary-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .amount-info {
    text-align: left;
    width: 100%;
  }
  
  .original-amount {
    flex-direction: row;
    align-items: center;
    gap: 8px;
  }
}
</style>