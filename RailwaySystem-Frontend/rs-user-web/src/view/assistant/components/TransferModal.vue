<template>
  <el-dialog
    v-model="visible"
    title="转接人工客服"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="transfer-content">
      <!-- 客服状态信息 -->
      <div class="transfer-info">
        <div class="transfer-icon">👤</div>
        <p class="transfer-text" :class="{ 'text-warning': onlineCount === 0 }">
          {{ onlineCount > 0 ? '当前有人工客服在线，预计等待时间 1-3 分钟' : '当前暂无客服在线，请稍后再试' }}
        </p>
        <div class="transfer-stats">
          <div class="stat-item">
            <span class="stat-label">在线客服：</span>
            <span class="stat-value" :class="{ 'value-zero': onlineCount === 0 }">{{ onlineCount }} 人</span>
          </div>
        </div>
      </div>
      
      <!-- 问题描述 -->
      <div class="transfer-form">
        <el-form :model="form" label-position="top">
          <el-form-item label="请简述您的问题（可选）：">
            <el-input
              v-model="form.question"
              type="textarea"
              :rows="3"
              :maxlength="200"
              show-word-limit
              placeholder="例如：订票遇到支付问题..."
            />
          </el-form-item>
        </el-form>
      </div>
    </div>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleConfirm" 
          :loading="loading"
          :disabled="onlineCount === 0"
        >
          确认转接
        </el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 正在连接弹窗 -->
  <el-dialog
    v-model="connecting"
    width="300px"
    :show-close="false"
    :close-on-click-modal="false"
    align-center
  >
    <div class="connecting-content">
      <el-icon class="loading-icon" :size="40"><Loading /></el-icon>
      <p class="connecting-text">正在为您转接人工客服...</p>
      <p class="connecting-hint">请稍候</p>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { Loading } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  serviceStatus: {
    type: [Number, Object],
    default: 0
  }
})

// 计算在线客服数量
const onlineCount = computed(() => {
  // 如果 serviceStatus 是数字,直接返回
  if (typeof props.serviceStatus === 'number') {
    return props.serviceStatus
  }
  // 如果是对象,返回 onlineCount 属性
  return props.serviceStatus?.onlineCount || 0
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const visible = ref(false)
const connecting = ref(false)
const loading = ref(false)

const form = reactive({
  question: ''
})

// 监听 modelValue 变化
watch(() => props.modelValue, (val) => {
  visible.value = val
})

// 监听 visible 变化
watch(visible, (val) => {
  emit('update:modelValue', val)
  if (!val) {
    form.question = ''
  }
})

// 关闭弹窗
const handleClose = () => {
  visible.value = false
}

// 确认转接
const handleConfirm = async () => {
  loading.value = true
  
  try {
    // 关闭主弹窗
    visible.value = false
    
    // 显示连接中弹窗
    connecting.value = true
    
    // 模拟连接延迟（实际项目中会等待后端响应）
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 触发转接事件
    emit('confirm', form.question)
    
    // 关闭连接中弹窗
    connecting.value = false
  } catch (error) {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.transfer-content {
  padding: 10px 0;
}

.transfer-info {
  text-align: center;
  margin-bottom: 24px;
}

.transfer-icon {
  font-size: 64px;
  margin-bottom: 16px;
  display: inline-block;
  background: #f0f7ff;
  width: 100px;
  height: 100px;
  line-height: 100px;
  border-radius: 50%;
}

.transfer-text {
  font-size: 15px;
  color: #1a1a1a;
  margin-bottom: 20px;
  line-height: 1.6;
}

.transfer-text.text-warning {
  color: #ff4d4f;
  font-weight: 500;
}

.transfer-stats {
  display: flex;
  justify-content: center;
  gap: 32px;
  padding: 20px;
  background: #f7f7f7;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  color: #0066ff;
}

.stat-value.value-zero {
  color: #ff4d4f;
}

.transfer-form {
  margin-top: 24px;
}

.connecting-content {
  text-align: center;
  padding: 30px 20px;
}

.loading-icon {
  color: #0066ff;
  margin-bottom: 20px;
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.connecting-text {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.connecting-hint {
  font-size: 13px;
  color: #8c8c8c;
  margin: 0;
}
</style>

