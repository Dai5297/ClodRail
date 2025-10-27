<template>
  <div class="login-form">
    <h2 class="form-title">用户登录</h2>
    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      class="login-form-content"
      @submit.prevent="handleLogin"
    >
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          placeholder="请输入用户名/手机号"
          size="large"
          prefix-icon="User"
          clearable
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码"
          size="large"
          prefix-icon="Lock"
          show-password
          clearable
        />
      </el-form-item>

      <!-- 验证码输入框 -->
      <el-form-item v-if="showCaptcha" prop="captcha" class="captcha-item">
        <div class="captcha-container">
          <el-input
            v-model="loginForm.captcha"
            placeholder="请输入验证码"
            size="large"
            prefix-icon="Key"
            clearable
            class="captcha-input"
          />
          <div class="captcha-display" @click="refreshCaptcha">
            <CaptchaDisplay
              :identify-code="captchaCode"
              :content-width="120"
              :content-height="48"
            />
          </div>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          size="large"
          class="login-button"
          :loading="loading"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登录' }}
        </el-button>
      </el-form-item>

      <div class="form-footer">
        <el-link type="primary" @click="$emit('switch-to-register')">
          还没有账号？立即注册
        </el-link>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, defineEmits, defineProps, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import CaptchaDisplay from '@/components/CaptchaDisplay.vue'
import { login, getCaptcha } from '@/api/auth.js'
import { ErrorHandler, FormValidator } from '@/utils/errorHandler.js'

const emit = defineEmits(['login-success', 'switch-to-register', 'login-failed'])

const props = defineProps({
  showCaptcha: {
    type: Boolean,
    default: false
  },
  captchaCode: {
    type: String,
    default: ''
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const loginFormRef = ref()
const loading = ref(false)
const loginFailCount = ref(0)
const showCaptcha = ref(false)
const captchaCode = ref('')

const loginForm = reactive({
  username: '',
  password: '',
  captcha: ''
})

const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名或手机号', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        // 支持用户名或手机号登录
        if (!value) {
          callback(new Error('请输入用户名或手机号'))
          return
        }
        
        // 如果是手机号格式，验证手机号
        if (/^1[3-9]\d{9}$/.test(value)) {
          const phoneResult = FormValidator.validatePhone(value)
          if (!phoneResult.valid) {
            callback(new Error(phoneResult.message))
          } else {
            callback()
          }
        } else {
          // 否则验证用户名格式
          const usernameResult = FormValidator.validateUsername(value)
          if (!usernameResult.valid) {
            callback(new Error(usernameResult.message))
          } else {
            callback()
          }
        }
      }, 
      trigger: 'blur' 
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  captcha: [
    { 
      validator: (rule, value, callback) => {
        if (showCaptcha.value) {
          if (!value) {
            callback(new Error('请输入验证码'))
          } else if (value.length !== 4) {
            callback(new Error('验证码长度为 4 位'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
})

// 监听showCaptcha变化，动态调整验证码规则
watch(() => showCaptcha.value, (newVal) => {
  if (newVal) {
    loginRules.captcha[0].required = true
  } else {
    loginRules.captcha[0].required = false
    loginForm.captcha = ''
  }
})

// 初始化登录失败计数
onMounted(() => {
  const storedFailCount = localStorage.getItem('loginFailCount')
  loginFailCount.value = storedFailCount ? parseInt(storedFailCount) : 0
  
  // 如果失败次数>=3，显示验证码
  if (loginFailCount.value >= 3) {
    showCaptcha.value = true
    refreshCaptcha()
  }
})

// 获取验证码
const refreshCaptcha = async () => {
  try {
    const response = await getCaptcha()
    captchaCode.value = response.data || 'ABCD'
  } catch (error) {
    console.error('获取验证码失败:', error)
    captchaCode.value = 'ABCD' // 默认验证码
  }
}

// 处理登录
const handleLogin = async () => {
  try {
    // 表单验证
    const valid = await loginFormRef.value.validate()
    if (!valid) {
      ErrorHandler.handleValidationError({}, '登录表单')
      return
    }
    
    loading.value = true
    
    // 准备登录数据
    const loginData = {
      username: loginForm.username,
      password: loginForm.password
    }
    
    // 如果显示验证码，添加验证码参数
    if (showCaptcha.value) {
      loginData.captcha = loginForm.captcha
    }
    
    // 调用登录API
    const response = await login(loginData)
    console.log(response)
    if (response.code === 200) {
      // 登录成功
      ErrorHandler.showSuccess('登录成功')
      
      // 清除失败计数
      loginFailCount.value = 0
      localStorage.removeItem('loginFailCount')
      
      // 保存token和用户信息
      if (response.data.token) {
        localStorage.setItem('token', response.data.token)
      }
      
      // 保存用户信息（直接使用response.data，因为它包含了完整的用户信息）
      localStorage.setItem('userInfo', JSON.stringify(response.data))
      
      // 触发登录成功事件
      emit('login-success', response.data)
    } else {
      // 登录失败
      handleLoginFailure(response.message || '登录失败')
    }
  } catch (error) {
    // 处理登录错误
    handleLoginFailure()
    ErrorHandler.handleApiError(error, '登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理登录失败
const handleLoginFailure = (message = '用户名或密码错误') => {
  ElMessage.error(message)
  // 增加失败计数
  loginFailCount.value++
  localStorage.setItem('loginFailCount', loginFailCount.value.toString())
  
  // 失败3次后显示验证码
  if (loginFailCount.value >= 3) {
    showCaptcha.value = true
    refreshCaptcha()
  }
  
  // 清空验证码输入
  if (showCaptcha.value) {
    loginForm.captcha = ''
  }
  
  // 触发登录失败事件
  emit('login-failed', { message, failCount: loginFailCount.value })
}

// 清空表单
const resetForm = () => {
  if (loginFormRef.value) {
    loginFormRef.value.resetFields()
  }
}

// 暴露方法给父组件
defineExpose({
  resetForm
})
</script>

<style scoped>
.login-form {
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

.login-form-content {
  width: 100%;
}

.login-form-content .el-form-item {
  margin-bottom: 24px;
}

/* 确保所有输入框等宽 */
.login-form-content .el-input {
  width: 100%;
}

.captcha-item {
  margin-bottom: 24px;
}

.captcha-container {
  display: flex;
  gap: 12px;
  align-items: stretch; /* 改为stretch确保子元素高度一致 */
  height: 40px; /* 修正容器高度为48px */
  width: 100%;
}

.captcha-input {
  flex: 1;
}

.captcha-display {
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 120px; /* 明确设置验证码展示框宽度 */
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0; /* 防止验证码框被压缩 */
}

.captcha-display:hover {
  border-color: #409eff;
}

.login-button {
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
  .login-form {
    max-width: 100%;
    padding: 0 16px;
  }
  
  .form-title {
    font-size: 20px;
    margin-bottom: 24px;
  }
  
  .captcha-container {
    flex-direction: column;
    gap: 8px;
  }
  
  .captcha-input {
    width: 100%;
  }
}
</style>