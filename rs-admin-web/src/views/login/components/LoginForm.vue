<template>
  <div class="login-form">
    <h2 class="form-title">管理员登录</h2>

    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      size="large"
      @submit.prevent="handleLogin"
    >
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          placeholder="请输入用户名"
          prefix-icon="User"
          clearable
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码"
          prefix-icon="Lock"
          show-password
        />
      </el-form-item>

      <!-- 验证码输入框 -->
      <el-form-item v-if="showCaptcha" prop="captcha" class="captcha-item">
        <div class="captcha-container">
          <el-input
            v-model="loginForm.captcha"
            placeholder="请输入验证码"
            prefix-icon="Key"
            clearable
            class="captcha-input"
          />
          <div class="captcha-display" @click="refreshCaptcha">
            <CaptchaDisplay
              :identify-code="captchaCode"
              :content-width="120"
              :content-height="40"
            />
          </div>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          :loading="loading"
          class="login-button"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登录' }}
        </el-button>
      </el-form-item>
    </el-form>

    <div class="form-tips">
      <p>
        <el-icon><InfoFilled /></el-icon>
        请使用管理员账号登录
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin, getCaptcha } from '@/api/auth'
import { setToken, setAdminInfo, setUserRoutes } from '@/utils/auth'
import { InfoFilled, User, Lock, Key } from "@element-plus/icons-vue"
import CaptchaDisplay from '@/components/CaptchaDisplay.vue'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const showCaptcha = ref(false)
const captchaCode = ref('')
const loginFailCount = ref(0)

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
})

// 验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应在3-20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
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
    // 确保规则生效
    if (loginFormRef.value) {
      loginFormRef.value.clearValidate('captcha')
    }
  } else {
    loginForm.captcha = ''
  }
})

// 初始化
onMounted(() => {
  const storedFailCount = localStorage.getItem('adminLoginFailCount')
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
    // 假设后端返回的数据结构是 { code: 200, data: 'ABCD', ... }
    // 如果直接返回字符串，则直接使用
    captchaCode.value = response.data || 'ABCD'
  } catch (error) {
    console.error('获取验证码失败:', error)
    captchaCode.value = 'ABCD' // 默认验证码，防止空白
  }
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    // 验证表单
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    // 如果显示验证码，先验证验证码是否正确（前端简单验证，实际应由后端验证）
    if (showCaptcha.value && loginForm.captcha.toLowerCase() !== captchaCode.value.toLowerCase()) {
      ElMessage.error('验证码错误')
      refreshCaptcha()
      return
    }

    loading.value = true

    // 调用登录接口
    const loginData = {
      username: loginForm.username,
      password: loginForm.password
    }
    
    // 如果后端支持验证码校验，应该传过去
    if (showCaptcha.value) {
      loginData.code = loginForm.captcha
    }

    const response = await adminLogin(loginData)

    if (response.success) {
      // 登录成功
      setToken(response.data.token)
      // The response data itself is the admin info (AdminLoginResDTO)
      setAdminInfo(response.data)
      
      // 保存用户路由权限
      if (response.data.routes && Array.isArray(response.data.routes)) {
        setUserRoutes(response.data.routes)
      }

      ElMessage.success('登录成功')
      
      // 清除失败计数
      loginFailCount.value = 0
      localStorage.removeItem('adminLoginFailCount')

      // 跳转到个人信息页面
      router.push('/profile')
    } else {
      // 登录失败
      handleLoginFailure(response.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    handleLoginFailure('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

// 处理登录失败
const handleLoginFailure = (message) => {
  ElMessage.error(message)
  
  // 增加失败计数
  loginFailCount.value++
  localStorage.setItem('adminLoginFailCount', loginFailCount.value.toString())
  
  // 失败3次后显示验证码
  if (loginFailCount.value >= 3) {
    if (!showCaptcha.value) {
      showCaptcha.value = true
    }
    refreshCaptcha()
  }
  
  // 清空验证码输入
  if (showCaptcha.value) {
    loginForm.captcha = ''
  }
}
</script>

<style scoped>
.login-form {
  width: 100%;
  margin-top: 20px;
}

.form-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin-bottom: 30px;
}

.login-button {
  width: 100%;
  height: 42px;
  font-size: 14px;
  border-radius: 6px;
  background: #000000;
  border-color: #000000;
  font-weight: 600;
  letter-spacing: 1px;
}

.login-button:hover {
  background: #333333;
  border-color: #333333;
}

.form-tips {
  margin-top: 20px;
  text-align: center;
}

.form-tips p {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  color: #909399;
  font-size: 13px;
}

.captcha-container {
  display: flex;
  gap: 12px;
  align-items: stretch;
  height: 40px;
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
  width: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.captcha-display:hover {
  border-color: #409eff;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  padding: 8px 15px;
}

:deep(.el-input__inner) {
  color: #000000 !important;
}

:deep(.el-checkbox__label) {
  color: #000000 !important;
}
</style>
