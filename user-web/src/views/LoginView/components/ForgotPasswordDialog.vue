<template>
  <el-dialog
    v-model="dialogVisible"
    title="找回密码"
    width="400px"
    center
  >
    <el-form :model="forgotForm" :rules="forgotRules" ref="forgotFormRef">
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="forgotForm.phone" placeholder="请输入注册手机号" />
      </el-form-item>
      
      <el-form-item label="图形验证码" prop="captcha">
        <div class="captcha-container">
          <el-input v-model="forgotForm.captcha" placeholder="请输入图形验证码" />
          <CaptchaDisplay 
            :captcha-text="captchaCode"
            @refresh="refreshCaptcha"
            class="captcha-display"
          />
        </div>
      </el-form-item>
      
      <el-form-item label="短信验证码" prop="code">
        <div class="sms-container">
          <el-input v-model="forgotForm.code" placeholder="请输入短信验证码" />
          <el-button 
            :disabled="smsCountdown > 0 || !forgotForm.captcha" 
            @click="sendSmsCode"
            :loading="sendingSms"
          >
            {{ smsCountdown > 0 ? `${smsCountdown}s` : '发送验证码' }}
          </el-button>
        </div>
      </el-form-item>
      
      <el-form-item label="新密码" prop="newPassword">
        <el-input 
          v-model="forgotForm.newPassword" 
          type="password" 
          placeholder="请输入新密码"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input 
          v-model="forgotForm.confirmPassword" 
          type="password" 
          placeholder="请再次输入新密码"
          show-password
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="resetPassword" :loading="resetting">
          重置密码
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { sendCode, resetPasswordByPhone } from '@/api/auth'

// 定义props
const props = defineProps<{
  visible: boolean
}>()

// 定义事件
const emit = defineEmits(['update:visible'])

// 状态
const sendingSms = ref(false)
const resetting = ref(false)
const smsCountdown = ref(0)
const forgotFormRef = ref<FormInstance>()
const dialogVisible = ref(false)

// 忘记密码表单
const forgotForm = reactive({
  phone: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

// 忘记密码表单验证规则
const forgotRules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== forgotForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 监听props变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
})

// 监听dialogVisible变化
watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
})

// 发送短信验证码
const sendSmsCode = async () => {
  if (!forgotForm.phone) {
    ElMessage.error('请输入手机号')
    return
  }
  
  if (!/^1[3-9]\d{9}$/.test(forgotForm.phone)) {
    ElMessage.error('请输入正确的手机号')
    return
  }
  
  sendingSms.value = true
  
  try {
    await sendCode({ phone: forgotForm.phone })
    ElMessage.success('验证码已发送')
    
    // 开始倒计时
    smsCountdown.value = 60
    const timer = setInterval(() => {
      smsCountdown.value--
      if (smsCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error: any) {
    console.error('发送验证码失败:', error)
    ElMessage.error(error.message || '发送验证码失败')
  } finally {
    sendingSms.value = false
  }
}

// 重置密码
const resetPassword = async () => {
  if (!forgotFormRef.value) return
  
  try {
    await forgotFormRef.value.validate()
    
    resetting.value = true
    
    await resetPasswordByPhone({
      phone: forgotForm.phone,
      code: forgotForm.code,
      newPassword: forgotForm.newPassword
    })
    
    ElMessage.success('密码重置成功，请使用新密码登录')
    closeDialog()
  } catch (error: any) {
    console.error('重置密码失败:', error)
    ElMessage.error(error.message || '重置密码失败')
  } finally {
    resetting.value = false
  }
}

// 关闭对话框
const closeDialog = () => {
  dialogVisible.value = false
  
  // 清空表单
  Object.assign(forgotForm, {
    phone: '',
    code: '',
    newPassword: '',
    confirmPassword: ''
  })
  
  // 重置倒计时
  smsCountdown.value = 0
}
</script>

<style scoped>
/* 短信验证码容器 */
.sms-container {
  display: flex;
  gap: 12px;
  width: 100%;
}

.sms-container .el-input {
  flex: 1;
}
</style>