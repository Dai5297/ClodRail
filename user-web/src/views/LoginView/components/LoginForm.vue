<template>
  <div class="login-form-container">
    <div class="login-form">
      <div class="form-header">
        <h2>用户登录</h2>
        <p>请输入您的账号和密码</p>
      </div>

      <el-form
        :model="loginForm"
        :rules="loginRules"
        ref="loginFormRef"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item v-if="showCaptcha" prop="captcha">
          <div class="captcha-container">
            <el-input
              placeholder="请输入验证码"
              v-model="loginForm.captcha"
            ></el-input>
            <div class="login-code" width="100%" @click="refreshCaptcha">
              <!--验证码组件-->
              <CaptchaDisplay :identifyCode="captchaCode" />            </div>
          </div>
        </el-form-item>

        <div class="form-options">
          <div></div>
          <el-button type="text" @click="forgotPassword" style="align-content: space-between">忘记密码？</el-button>
        </div>

        <el-form-item class="login-button-item">
          <el-button
            type="primary"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="form-footer">
        <div class="register-link">
          还没有账号？
          <el-button type="text" @click="goToRegister">立即注册</el-button>
        </div>

        <el-divider>其他登录方式</el-divider>

        <div class="social-login">
          <el-button circle @click="wechatLogin">
            <el-icon>
              <ChatDotRound/>
            </el-icon>
          </el-button>
          <el-button circle @click="qqLogin">
            <el-icon>
              <User/>
            </el-icon>
          </el-button>
          <el-button circle @click="alipayLogin">
            <el-icon>
              <CreditCard/>
            </el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, reactive, onMounted} from 'vue'
import {useRouter, useRoute} from 'vue-router'
import {ElMessage} from 'element-plus'
import type {FormInstance, FormRules} from 'element-plus'
import {ChatDotRound, User, CreditCard} from '@element-plus/icons-vue'
import {login, getCaptcha} from '@/api/auth'
import {storageKeys, routeConfig} from '@/config'
import CaptchaDisplay from '@/components/CaptchaDisplay.vue'
import bcrypt from 'bcryptjs'

// 定义登录请求接口类型
interface LoginRequest {
  username: string;
  password: string;
}

const router = useRouter()
const route = useRoute()

// 状态
const loading = ref(false)
const showCaptcha = ref(false)
const captchaCode = ref('')
const loginFormRef = ref<FormInstance>()

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
  captcha: ''
})

// 登录表单验证规则
const loginRules: FormRules = {
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, message: '密码长度不能少于6位', trigger: 'blur'}
  ],
  captcha: [
    {required: true, message: '请输入验证码', trigger: 'blur'}
  ]
}

// 定义事件
const emit = defineEmits(['forgot-password'])

// 初始化
onMounted(() => {
  // 检查是否需要显示验证码
  const failCount = localStorage.getItem(storageKeys.loginFailCount)
  if (failCount && parseInt(failCount) >= 3) {
    showCaptcha.value = true
    refreshCaptcha()
  }
})

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    if (showCaptcha.value === true && loginForm.captcha !== captchaCode.value){
      ElMessage.error('验证码错误')
      refreshCaptcha()
      return
    }

    loading.value = true

    const loginData: LoginRequest = {
      username: loginForm.username,
      password: loginForm.password,
    }

    const response = await login(loginData)

    console.log(response.token)

    if (response.code !== 200){
      ElMessage.error(response.message)
      refreshCaptcha()
      return
    }

    // 保存登录信息
    localStorage.setItem(storageKeys.token, response.data.token)

    // 清除登录失败次数
    localStorage.removeItem(storageKeys.loginFailCount)

    ElMessage.success('登录成功')

    // 跳转到目标页面或首页
    const redirect = route.query[routeConfig.redirectKey] as string
    router.push(redirect || routeConfig.homePath)
  } catch (error: any) {
    console.error('登录失败:', error)

    // 增加失败次数
    const failCount = parseInt(localStorage.getItem(storageKeys.loginFailCount) || '0') + 1
    localStorage.setItem(storageKeys.loginFailCount, failCount.toString())

    // 失败3次后显示验证码
    if (failCount >= 3) {
      showCaptcha.value = true
      refreshCaptcha()
    }

    ElMessage.error(error.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

// 刷新验证码
const refreshCaptcha = async () => {
  try {
    const response = await getCaptcha()
    captchaCode.value = response.data
  } catch (error) {
    console.error('获取验证码失败:', error)
    ElMessage.error('获取验证码失败，请重试')
  }
}

// 忘记密码
const forgotPassword = () => {
  emit('forgot-password')
}

// 前往注册
const goToRegister = () => {
  router.push('/register')
}

// 第三方登录
const wechatLogin = () => {
  ElMessage.info('微信登录功能开发中，敬请期待')
}

const qqLogin = () => {
  ElMessage.info('QQ登录功能开发中，敬请期待')
}

const alipayLogin = () => {
  ElMessage.info('支付宝登录功能开发中，敬请期待')
}
</script>

<style scoped>
.login-form-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-form {
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

/* 验证码容器 */
.captcha-container {
  display: flex;
  gap: 12px;
  width: 100%;
}

.captcha-container .el-input {
  flex: 1;
}

.captcha-display {
  width: 100px;
  height: 40px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  position: relative;
  padding-right: 10px;
}

.captcha-text {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
  letter-spacing: 2px;
}

.refresh-hint {
  font-size: 10px;
  color: #909399;
  margin-top: 2px;
}

.captcha-display:hover {
  background-color: #ecf5ff;
  border-color: #409eff;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

/* 表单底部 */
.form-footer {
  margin-top: 24px;
}

.register-link {
  text-align: center;
  margin-bottom: 24px;
  color: #8c8c8c;
  font-size: 14px;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.social-login .el-button {
  width: 40px;
  height: 40px;
}

/* 登录按钮样式 */
.login-button-item {
  text-align: center;
}

.login-button-item .el-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-form-container {
    padding: 24px;
  }
}
</style>
