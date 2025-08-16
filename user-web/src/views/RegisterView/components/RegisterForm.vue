<template>
  <div class="register-form-container">
    <div class="register-form">
      <div class="form-header">
        <h2>创建账号</h2>
        <p>请填写以下信息完成注册</p>
      </div>

      <el-form 
        :model="registerForm" 
        :rules="registerRules" 
        ref="registerFormRef"
        size="large"
      >
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            clearable
          />
        </el-form-item>

        <el-form-item prop="smsCode">
          <div class="sms-container">
            <el-input
              v-model="registerForm.smsCode"
              placeholder="请输入验证码"
              prefix-icon="Message"
              clearable
            />
            <el-button 
              :disabled="smsCountdown > 0 || !isValidPhone" 
              @click="sendSmsCode"
              :loading="sendingSms"
            >
              {{ smsCountdown > 0 ? `${smsCountdown}s` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
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
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item prop="realName">
          <el-input
            v-model="registerForm.realName"
            placeholder="请输入真实姓名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="idNumber">
          <el-input
            v-model="registerForm.idNumber"
            placeholder="请输入身份证号"
            prefix-icon="CreditCard"
            clearable
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱（可选）"
            prefix-icon="Message"
            clearable
          />
        </el-form-item>

        <el-form-item prop="agreement">
          <el-checkbox v-model="registerForm.agreement">
            我已阅读并同意
            <el-button type="text" @click="showUserAgreement">
              《用户协议》
            </el-button>
            和
            <el-button type="text" @click="showPrivacyPolicy">
              《隐私政策》
            </el-button>
          </el-checkbox>
        </el-form-item>

        <el-form-item class="register-button-item">
          <el-button 
            type="primary" 
            :loading="loading" 
            @click="handleRegister"
            block
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="form-footer">
        <div class="login-link">
          已有账号？
          <el-button type="text" @click="goToLogin">
            立即登录
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, inject } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { register, sendCode as sendSms } from '@/api/auth'
import type { RegisterRequest } from '@/api/auth'

const router = useRouter()

// 注入父组件提供的方法
const showUserAgreement = inject('showUserAgreement') as () => void
const showPrivacyPolicy = inject('showPrivacyPolicy') as () => void

// 状态
const loading = ref(false)
const sendingSms = ref(false)
const smsCountdown = ref(0)
const registerFormRef = ref<FormInstance>()

// 注册表单
const registerForm = reactive({
  username: '',
  phone: '',
  smsCode: '',
  password: '',
  confirmPassword: '',
  realName: '',
  idNumber: '',
  email: '',
  agreement: false
})

// 验证手机号是否有效
const isValidPhone = computed(() => {
  return /^1[3-9]\d{9}$/.test(registerForm.phone)
})

// 身份证号验证
const validateIdNumber = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入身份证号'))
    return
  }
  
  // 18位身份证号验证
  const idCardRegex = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
  if (!idCardRegex.test(value)) {
    callback(new Error('请输入正确的身份证号'))
    return
  }
  
  callback()
}

// 注册表单验证规则
const registerRules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  smsCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d).+$/, message: '密码必须包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5·]+$/, message: '请输入正确的中文姓名', trigger: 'blur' }
  ],
  idNumber: [
    { validator: validateIdNumber, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  agreement: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请阅读并同意用户协议和隐私政策'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 发送短信验证码
const sendSmsCode = async () => {
  if (!isValidPhone.value) {
    ElMessage.error('请输入正确的手机号')
    return
  }
  
  sendingSms.value = true
  
  try {
    await sendSms({ phone: registerForm.phone })
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

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    
    loading.value = true
    
    const registerData: RegisterRequest = {
      username: registerForm.username,
      phone: registerForm.phone,
      code: registerForm.smsCode,
      password: registerForm.password,
      realName: registerForm.realName,
      idCard: registerForm.idNumber,
      email: registerForm.email || ''
    }
    
    const response = await register(registerData)
    
    ElMessage.success('注册成功！请登录')
    
    // 跳转到登录页面
    router.push('/login')
  } catch (error: any) {
    console.error('注册失败:', error)
    ElMessage.error(error.message || '注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 前往登录
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-form-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.register-form {
  width: 100%;
  max-width: 360px;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 24px;
  color: #262626;
  margin-bottom: 8px;
}

.form-header p {
  color: #8c8c8c;
  font-size: 14px;
}

/* 短信验证码容器 */
.sms-container {
  display: flex;
  gap: 12px;
  width: 100%;
}

.sms-container .el-input {
  flex: 1;
}

/* 注册按钮样式 */
.register-button-item {
  text-align: center;
  margin-bottom: 16px !important;
}

.register-button-item .el-form-item__content {
  margin-left: 0 !important;
}

.register-button-item .el-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  margin: 0;
}

/* 表单底部 */
.form-footer {
  margin-top: 24px;
}

.login-link {
  text-align: center;
  color: #8c8c8c;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-form-container {
    padding: 24px;
  }
  
  .sms-container {
    flex-direction: column;
  }
  
  .sms-container .el-button {
    width: 100%;
  }
}
</style>