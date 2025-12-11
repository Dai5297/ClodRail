<template>
  <el-dialog
    v-model="visible"
    title="确认兑换"
    width="600px"
    :before-close="handleClose"
  >
    <div v-if="exchangeData" class="exchange-confirmation">
      <!-- 商品确认卡片 -->
      <div class="confirm-product">
        <img :src="exchangeData.product.image" :alt="exchangeData.product.name" />
        <div class="confirm-info">
          <h4>{{ exchangeData.product.name }}</h4>
          <p class="confirm-quantity">数量: {{ exchangeData.quantity }}</p>
          <p class="confirm-points">所需积分: {{ exchangeData.totalPoints }}</p>
        </div>
      </div>

      <!-- 积分余额计算 -->
      <div class="points-balance">
        <div class="balance-item">
          <span class="balance-label">当前积分:</span>
          <span class="balance-value">{{ userPoints }}</span>
        </div>
        <div class="balance-item">
          <span class="balance-label">兑换后余额:</span>
          <span class="balance-value" :class="{ warning: afterExchangeBalance < 0 }">
            {{ afterExchangeBalance }}
          </span>
        </div>
      </div>

      <!-- 收货信息表单 -->
      <div class="delivery-info">
        <h4>收货信息</h4>
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="收货人" prop="recipientName">
            <el-input v-model="form.recipientName" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="联系电话" prop="recipientPhone">
            <el-input v-model="form.recipientPhone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="收货地址" prop="recipientAddress">
            <el-input
              v-model="form.recipientAddress"
              type="textarea"
              :rows="3"
              placeholder="请输入详细收货地址"
            />
          </el-form-item>
        </el-form>
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="loading">
          确认兑换
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { ref, computed, watch } from 'vue'

export default {
  name: 'ExchangeModal',
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    exchangeData: {
      type: Object,
      default: null
    },
    userPoints: {
      type: Number,
      default: 0
    }
  },
  emits: ['update:modelValue', 'confirm-exchange'],
  setup(props, { emit }) {
    const visible = computed({
      get: () => props.modelValue,
      set: (val) => emit('update:modelValue', val)
    })

    const formRef = ref(null)
    const loading = ref(false)
    const form = ref({
      recipientName: '',
      recipientPhone: '',
      recipientAddress: ''
    })

    const rules = {
      recipientName: [
        { required: true, message: '请输入收货人姓名', trigger: 'blur' }
      ],
      recipientPhone: [
        { required: true, message: '请输入联系电话', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ],
      recipientAddress: [
        { required: true, message: '请输入收货地址', trigger: 'blur' },
        { min: 10, message: '地址长度不能少于10个字符', trigger: 'blur' }
      ]
    }

    const afterExchangeBalance = computed(() => {
      return props.userPoints - (props.exchangeData?.totalPoints || 0)
    })

    // 重置表单
    watch(visible, (newVal) => {
      if (!newVal) {
        form.value = {
          recipientName: '',
          recipientPhone: '',
          recipientAddress: ''
        }
        formRef.value?.resetFields()
      }
    })

    const handleClose = () => {
      visible.value = false
    }

    const handleConfirm = async () => {
      try {
        await formRef.value.validate()
        
        emit('confirm-exchange', {
          ...props.exchangeData,
          ...form.value
        })
      } catch (error) {
      }
    }

    return {
      visible,
      formRef,
      loading,
      form,
      rules,
      afterExchangeBalance,
      handleClose,
      handleConfirm
    }
  }
}
</script>

<style scoped>
.exchange-confirmation {
  padding: 10px 0;
}

.confirm-product {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.confirm-product img {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  object-fit: cover;
}

.confirm-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.confirm-quantity,
.confirm-points {
  margin: 4px 0;
  font-size: 14px;
  color: #666;
}

.confirm-points {
  color: #667eea;
  font-weight: 500;
}

.points-balance {
  display: flex;
  justify-content: space-around;
  padding: 20px;
  background: #f0f8ff;
  border-radius: 8px;
  margin-bottom: 24px;
}

.balance-item {
  text-align: center;
}

.balance-label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.balance-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
}

.balance-value.warning {
  color: #f56c6c;
}

.delivery-info h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.dialog-footer {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}
</style>
