<template>
  <div class="register-form">
    <h2 class="form-title">用户注册</h2>
    <el-form
      ref="registerFormRef"
      :model="registerForm"
      :rules="registerRules"
      class="register-form-content"
      @submit.prevent="handleRegister"
    >
      <el-form-item prop="username">
        <el-input
          v-model="registerForm.username"
          placeholder="请输入用户名"
          size="large"
          prefix-icon="User"
          clearable
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          placeholder="请输入密码"
          size="large"
          prefix-icon="Lock"
          show-password
          clearable
        />
      </el-form-item>

      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          placeholder="请确认密码"
          size="large"
          prefix-icon="Lock"
          show-password
          clearable
        />
      </el-form-item>

      <el-form-item prop="realName">
        <el-input
          v-model="registerForm.realName"
          placeholder="请输入真实姓名"
          size="large"
          prefix-icon="UserFilled"
          clearable
        />
      </el-form-item>

      <el-form-item prop="phone">
        <el-input
          v-model="registerForm.phone"
          placeholder="请输入手机号"
          size="large"
          prefix-icon="Phone"
          clearable
        />
      </el-form-item>

      <el-form-item prop="email">
        <el-input
          v-model="registerForm.email"
          placeholder="请输入邮箱地址"
          size="large"
          prefix-icon="Message"
          clearable
        />
      </el-form-item>

      <el-form-item prop="idCard">
        <el-input
          v-model="registerForm.idCard"
          placeholder="请输入身份证号"
          size="large"
          prefix-icon="CreditCard"
          clearable
        />
      </el-form-item>

      <!-- 验证码输入框 -->
      <el-form-item prop="verificationCode" class="verification-item">
        <div class="verification-container">
          <el-input
            v-model="registerForm.verificationCode"
            placeholder="请输入验证码"
            size="large"
            prefix-icon="Key"
            clearable
            class="verification-input"
          />
          <el-button
            type="primary"
            size="large"
            class="send-code-btn"
            :loading="sendingCode"
            :disabled="countdown > 0"
            @click="sendVerificationCode"
          >
            {{ countdown > 0 ? `${countdown}s后重发` : '发送验证码' }}
          </el-button>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          size="large"
          class="register-button"
          :loading="loading"
          @click="handleRegister"
        >
          {{ loading ? '注册中...' : '注册' }}
        </el-button>
      </el-form-item>

      <div class="form-footer">
        <el-link type="primary" @click="$emit('switch-to-login')">
          已有账号？立即登录
        </el-link>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'
import {getPhoneCode, register} from '@/api/auth.js'
import { ErrorHandler, FormValidator } from '@/utils/errorHandler.js'

const emit = defineEmits(['register-success', 'switch-to-login'])

const registerFormRef = ref()
const loading = ref(false)
const countdown = ref(0)
const countdownTimer = ref(null)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: '',
  idCard: '',
  verificationCode: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  const result = FormValidator.validatePhone(value)
  if (!result.valid) {
    callback(new Error(result.message))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  const result = FormValidator.validateEmail(value)
  if (!result.valid) {
    callback(new Error(result.message))
  } else {
    callback()
  }
}

const validateIdCard = (rule, value, callback) => {
  const result = FormValidator.validateIdCard(value)
  if (!result.valid) {
    callback(new Error(result.message))
  } else {
    callback()
  }
}

const registerRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        const result = FormValidator.validateUsername(value)
        if (!result.valid) {
          callback(new Error(result.message))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        const result = FormValidator.validatePassword(value)
        if (!result.valid) {
          callback(new Error(result.message))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5·]+$/, message: '请输入正确的中文姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { validator: validateIdCard, trigger: 'blur' }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为 6 位', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码只能包含数字', trigger: 'blur' }
  ]
})

const sendVerificationCode = async () => {
  // 验证手机号
  try {
    await registerFormRef.value.validateField('phone')
  } catch (error) {
    return
  }

  try {
    const response = await getPhoneCode({phone: registerForm.phone})
    if (response.code === 200) {
      ElMessage.success('验证码已发送到您的手机')
    } else {
      ElMessage.error(response.message || '获取验证码失败')
    }
  } catch (error) {
    ElMessage.error('获取验证码失败，请稍后重试')
  }

  // 模拟发送验证码（实际项目中应该调用API）
  ElMessage.success('验证码已发送到您的手机')
  
  // 开始倒计时
  countdown.value = 60
  countdownTimer.value = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer.value)
      countdownTimer.value = null
    }
  }, 1000)
}

// 处理注册
const handleRegister = async () => {
  try {
    // 表单验证
    const valid = await registerFormRef.value.validate()
    if (!valid) {
      ErrorHandler.handleValidationError({}, '注册表单')
      return
    }
    
    loading.value = true
    
    // 准备注册数据
    const registerData = {
      username: registerForm.username,
      password: registerForm.password,
      realName: registerForm.realName,
      phone: registerForm.phone,
      email: registerForm.email,
      idCard: registerForm.idCard,
      verificationCode: registerForm.verificationCode
    }
    
    // 调用注册API
    const response = await register(registerData)
    
    if (response.success) {
      // 注册成功
      ErrorHandler.showSuccess('注册成功，请登录')
      
      // 触发注册成功事件
      emit('register-success', {
        username: registerForm.username,
        message: '注册成功'
      })
      
      // 重置表单
      resetForm()
    } else {
      // 注册失败
      ErrorHandler.handleBusinessError(response.message || '注册失败，请稍后重试')
    }
  } catch (error) {
    // 处理注册错误
    ErrorHandler.handleApiError(error, '注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 清空表单
const resetForm = () => {
  if (registerFormRef.value) {
    registerFormRef.value.resetFields()
  }
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
    countdownTimer.value = null
    countdown.value = 0
  }
}

// 暴露方法给父组件
defineExpose({
  resetForm
})
</script>

<style scoped>
.register-form {
  width: 100%;
  max-width: 400px;
}

.form-title {
  text-align: center;
  margin-bottom: 32px;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.register-form-content {
  width: 100%;
}

.register-form-content .el-form-item {
  margin-bottom: 20px;
}

.verification-item {
  margin-bottom: 20px;
}

.verification-container {
  display: flex;
  gap: 12px;
  align-items: center;
  width: 100%;  
}

.verification-input {
  flex: 1;
}

.send-code-btn {
  width: 120px;
  flex-shrink: 0;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
}

.form-footer {
  text-align: center;
  margin-top: 16px;
}

.form-footer .el-link {
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-form {
    max-width: 100%;
    padding: 0 16px;
  }
  
  .form-title {
    font-size: 20px;
    margin-bottom: 24px;
  }
  
  .verification-container {
    flex-direction: column;
    gap: 8px;
  }
  
  .verification-input {
    width: 100%;
  }
  
  .send-code-btn {
    width: 100%;
  }
  
  .register-form-content .el-form-item {
    margin-bottom: 16px;
  }
}
</style>