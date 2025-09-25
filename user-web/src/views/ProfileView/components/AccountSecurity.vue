<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import {
  ElCard,
  ElForm,
  ElFormItem,
  ElInput,
  ElButton,
  ElDialog,
  ElMessage,
  ElMessageBox,
  ElSkeleton,
  ElIcon,
  ElDivider,
  ElAlert,
  ElSteps,
  ElStep,
  ElTag
} from 'element-plus'
import {
  Lock,
  Phone,
  Message,
  Key,
  Warning,
  CircleCheck,
  View,
  Hide
} from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { resetPassword, resetPasswordByPhone, sendCode as sendSmsCode, getCaptcha } from '@/api/auth'
import type { SmsResponse, CaptchaResponse } from '@/api/auth'

// 定义安全信息接口
export interface SecurityInfo {
  hasPassword: boolean
  phoneVerified: boolean
  emailVerified: boolean
  realNameVerified: boolean
  phone: string
  email: string
  realName: string
  idCard: string
}

// 定义属性
interface Props {
  securityInfo: SecurityInfo | null
  loading: boolean
}

// 定义事件
interface Emits {
  changePassword: [data: { oldPassword: string; newPassword: string }]
  bindPhone: [data: { phone: string; code: string }]
  bindEmail: [data: { email: string; code: string }]
  realNameAuth: [data: { realName: string; idCard: string }]
  sendVerifyCode: [type: 'phone' | 'email', target: string]
  logoutDevice: [deviceId: string]
}

const props = withDefaults(defineProps<Props>(), {
  securityInfo: null,
  loading: false
})

const emit = defineEmits<Emits>()

// 对话框状态
const dialogVisible = ref({
  password: false,
  phone: false,
  email: false,
  realName: false,
  devices: false
})

// 表单引用
const passwordFormRef = ref<FormInstance>()
const phoneFormRef = ref<FormInstance>()
const emailFormRef = ref<FormInstance>()
const realNameFormRef = ref<FormInstance>()

// 密码可见性
const passwordVisible = ref({
  old: false,
  new: false,
  confirm: false
})

// 验证码倒计时
const countdown = ref({
  phone: 0,
  email: 0
})

// 实名认证步骤
const realNameStep = ref(0)

// 表单数据
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const phoneForm = reactive({
  phone: '',
  code: ''
})

const emailForm = reactive({
  email: '',
  code: ''
})

const realNameForm = reactive({
  realName: '',
  idCard: ''
})

// 表单验证规则
const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度在 8 到 20 个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const phoneRules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

const emailRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

const realNameRules: FormRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号码', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号码', trigger: 'blur' }
  ]
}

// 计算属性
const displaySecurityInfo = computed(() => props.securityInfo || {
  hasPassword: false,
  phoneVerified: false,
  emailVerified: false,
  realNameVerified: false,
  phone: '',
  email: '',
  realName: '',
  idCard: '',
  lastPasswordChange: '',
  loginDevices: []
} as SecurityInfo)

// 安全等级计算
const securityLevel = computed(() => {
  if (!props.securityInfo) return { level: 0, text: '未知', color: '#d9d9d9' }

  let score = 0
  if (props.securityInfo.hasPassword) score += 25
  if (props.securityInfo.phoneVerified) score += 25
  if (props.securityInfo.emailVerified) score += 25
  if (props.securityInfo.realNameVerified) score += 25

  if (score >= 100) return { level: score, text: '高', color: '#52c41a' }
  if (score >= 75) return { level: score, text: '中高', color: '#1890ff' }
  if (score >= 50) return { level: score, text: '中', color: '#faad14' }
  if (score >= 25) return { level: score, text: '低', color: '#ff4d4f' }
  return { level: score, text: '极低', color: '#ff4d4f' }
})

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 脱敏处理手机号
const maskPhone = (phone: string) => {
  if (!phone) return '-'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 脱敏处理邮箱
const maskEmail = (email: string) => {
  if (!email) return '-'
  const [username, domain] = email.split('@')
  const maskedUsername = username.length > 2
    ? username.substring(0, 2) + '*'.repeat(username.length - 2)
    : username
  return `${maskedUsername}@${domain}`
}

// 脱敏处理身份证号
const maskIdCard = (idCard: string) => {
  if (!idCard) return '-'
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
}

// 打开对话框
const openDialog = (type: keyof typeof dialogVisible.value) => {
  dialogVisible.value[type] = true

  // 重置表单
  if (type === 'password') {
    Object.assign(passwordForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
  } else if (type === 'phone') {
    Object.assign(phoneForm, { phone: '', code: '' })
  } else if (type === 'email') {
    Object.assign(emailForm, { email: '', code: '' })
  } else if (type === 'realName') {
    Object.assign(realNameForm, { realName: '', idCard: '' })
    realNameStep.value = 0
  }
}

// 关闭对话框
const closeDialog = (type: keyof typeof dialogVisible.value) => {
  dialogVisible.value[type] = false
}

// 发送验证码
const sendCode = async (type: 'phone' | 'email') => {
  const target = type === 'phone' ? phoneForm.phone : emailForm.email

  if (!target) {
    ElMessage.error(`请先输入${type === 'phone' ? '手机号' : '邮箱地址'}`)
    return
  }

  try {
    emit('sendVerifyCode', type, target)

    // 开始倒计时
    countdown.value[type] = 60
    const timer = setInterval(() => {
      countdown.value[type]--
      if (countdown.value[type] <= 0) {
        clearInterval(timer)
      }
    }, 1000)

    ElMessage.success('验证码发送成功')
  } catch (error) {
    console.error('发送验证码失败:', error)
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    emit('changePassword', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    closeDialog('password')
    ElMessage.success('密码修改成功')
  } catch (error) {
    console.error('密码修改失败:', error)
  }
}

// 绑定手机号
const bindPhone = async () => {
  if (!phoneFormRef.value) return

  try {
    await phoneFormRef.value.validate()
    emit('bindPhone', {
      phone: phoneForm.phone,
      code: phoneForm.code
    })
    closeDialog('phone')
    ElMessage.success('手机号绑定成功')
  } catch (error) {
    console.error('手机号绑定失败:', error)
  }
}

// 绑定邮箱
const bindEmail = async () => {
  if (!emailFormRef.value) return

  try {
    await emailFormRef.value.validate()
    emit('bindEmail', {
      email: emailForm.email,
      code: emailForm.code
    })
    closeDialog('email')
    ElMessage.success('邮箱绑定成功')
  } catch (error) {
    console.error('邮箱绑定失败:', error)
  }
}

// 实名认证
const realNameAuth = async () => {
  if (!realNameFormRef.value) return

  try {
    await realNameFormRef.value.validate()
    emit('realNameAuth', {
      realName: realNameForm.realName,
      idCard: realNameForm.idCard
    })
    realNameStep.value = 1
    setTimeout(() => {
      closeDialog('realName')
      ElMessage.success('实名认证提交成功，请等待审核')
    }, 2000)
  } catch (error) {
    console.error('实名认证失败:', error)
  }
}

// 登出设备
const logoutDevice = async (deviceId: string, deviceName: string) => {
  try {
    await ElMessageBox.confirm(
      `确定要登出设备 "${deviceName}" 吗？`,
      '确认登出',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    emit('logoutDevice', deviceId)
    ElMessage.success('设备登出成功')
  } catch {
    // 用户取消登出
  }
}
</script>

<template>
  <div class="account-security">
    <ElCard class="security-card" shadow="hover">
      <!-- 卡片头部 -->
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <ElIcon :size="20" color="#1890ff">
              <Lock />
            </ElIcon>
            <span class="header-title">账户安全</span>
          </div>
          <div class="security-level">
            <span class="level-text">安全等级：</span>
            <ElTag :color="securityLevel.color" class="level-tag">
              {{ securityLevel.text }}
            </ElTag>
            <span class="level-score">{{ securityLevel.level }}/100</span>
          </div>
        </div>
      </template>

      <!-- 加载状态 -->
      <ElSkeleton v-if="loading" animated>
        <template #template>
          <div class="security-skeleton">
            <el-skeleton-item v-for="i in 6" :key="i" variant="rect" style="height: 80px; margin-bottom: 16px;" />
          </div>
        </template>
      </ElSkeleton>

      <!-- 安全设置内容 -->
      <div v-else class="security-content">
        <!-- 安全提醒 -->
        <ElAlert
          v-if="securityLevel.level < 75"
          title="安全提醒"
          type="warning"
          :description="`您的账户安全等级为${securityLevel.text}，建议完善安全设置以提高账户安全性。`"
          show-icon
          :closable="false"
          class="security-alert"
        />

        <!-- 安全设置列表 -->
        <div class="security-list">
          <!-- 登录密码 -->
          <div class="security-item">
            <div class="item-left">
              <ElIcon :size="24" color="#1890ff">
                <Lock />
              </ElIcon>
              <div class="item-info">
                <h4 class="item-title">登录密码</h4>
                <p class="item-desc">
{{ (displaySecurityInfo as SecurityInfo).hasPassword ? '已设置密码' : '未设置密码' }}
                  {{ displaySecurityInfo.lastPasswordChange ? `，上次修改：${formatTime(displaySecurityInfo.lastPasswordChange)}` : '' }}
                </p>
              </div>
            </div>
            <div class="item-right">
              <div class="item-status">
                <ElIcon :color="displaySecurityInfo.hasPassword ? '#52c41a' : '#ff4d4f'">
                  <component :is="displaySecurityInfo.hasPassword ? CircleCheck : Warning" />
                </ElIcon>
              </div>
              <ElButton type="primary" @click="openDialog('password')">
                {{ displaySecurityInfo.hasPassword ? '修改密码' : '设置密码' }}
              </ElButton>
            </div>
          </div>

          <!-- 手机号验证 -->
          <div class="security-item">
            <div class="item-left">
              <ElIcon :size="24" color="#1890ff">
                <Phone />
              </ElIcon>
              <div class="item-info">
                <h4 class="item-title">手机号验证</h4>
                <p class="item-desc">
                  {{ displaySecurityInfo.phoneVerified ? `已绑定：${maskPhone(displaySecurityInfo.phone)}` : '未绑定手机号' }}
                </p>
              </div>
            </div>
            <div class="item-right">
              <div class="item-status">
                <ElIcon :color="displaySecurityInfo.phoneVerified ? '#52c41a' : '#ff4d4f'">
                  <component :is="displaySecurityInfo.phoneVerified ? CircleCheck : Warning" />
                </ElIcon>
              </div>
              <ElButton type="primary" @click="openDialog('phone')">
                {{ displaySecurityInfo.phoneVerified ? '更换手机' : '绑定手机' }}
              </ElButton>
            </div>
          </div>

          <!-- 邮箱验证 -->
          <div class="security-item">
            <div class="item-left">
              <ElIcon :size="24" color="#1890ff">
                <Message />
              </ElIcon>
              <div class="item-info">
                <h4 class="item-title">邮箱验证</h4>
                <p class="item-desc">
                  {{ displaySecurityInfo.emailVerified ? `已绑定：${maskEmail(displaySecurityInfo.email)}` : '未绑定邮箱' }}
                </p>
              </div>
            </div>
            <div class="item-right">
              <div class="item-status">
                <ElIcon :color="displaySecurityInfo.emailVerified ? '#52c41a' : '#ff4d4f'">
                  <component :is="displaySecurityInfo.emailVerified ? CircleCheck : Warning" />
                </ElIcon>
              </div>
              <ElButton type="primary" @click="openDialog('email')">
                {{ displaySecurityInfo.emailVerified ? '更换邮箱' : '绑定邮箱' }}
              </ElButton>
            </div>
          </div>

          <!-- 实名认证 -->
          <div class="security-item">
            <div class="item-left">
              <ElIcon :size="24" color="#1890ff">
                <Key />
              </ElIcon>
              <div class="item-info">
                <h4 class="item-title">实名认证</h4>
                <p class="item-desc">
                  {{ displaySecurityInfo.realNameVerified ? `已认证：${displaySecurityInfo.realName}（${maskIdCard(displaySecurityInfo.idCard)}）` : '未进行实名认证' }}
                </p>
              </div>
            </div>
            <div class="item-right">
              <div class="item-status">
                <ElIcon :color="displaySecurityInfo.realNameVerified ? '#52c41a' : '#ff4d4f'">
                  <component :is="displaySecurityInfo.realNameVerified ? CircleCheck : Warning" />
                </ElIcon>
              </div>
              <ElButton
                type="primary"
                @click="openDialog('realName')"
                :disabled="displaySecurityInfo.realNameVerified"
              >
                {{ displaySecurityInfo.realNameVerified ? '已认证' : '立即认证' }}
              </ElButton>
            </div>
          </div>

          <!-- 登录设备 -->
          <div class="security-item">
            <div class="item-left">
              <ElIcon :size="24" color="#1890ff">
                <Monitor />
              </ElIcon>
              <div class="item-info">
                <h4 class="item-title">登录设备</h4>
                <p class="item-desc">
                  当前有 {{ displaySecurityInfo.loginDevices?.length || 0 }} 台设备登录
                </p>
              </div>
            </div>
            <div class="item-right">
              <ElButton type="primary" @click="openDialog('devices')">
                管理设备
              </ElButton>
            </div>
          </div>
        </div>
      </div>
    </ElCard>

    <!-- 修改密码对话框 -->
    <ElDialog
      v-model="dialogVisible.password"
      title="修改密码"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElForm ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <ElFormItem label="当前密码" prop="oldPassword">
          <ElInput
            v-model="passwordForm.oldPassword"
            :type="passwordVisible.old ? 'text' : 'password'"
            placeholder="请输入当前密码"
            show-password
          />
        </ElFormItem>
        <ElFormItem label="新密码" prop="newPassword">
          <ElInput
            v-model="passwordForm.newPassword"
            :type="passwordVisible.new ? 'text' : 'password'"
            placeholder="请输入新密码"
            show-password
          />
        </ElFormItem>
        <ElFormItem label="确认密码" prop="confirmPassword">
          <ElInput
            v-model="passwordForm.confirmPassword"
            :type="passwordVisible.confirm ? 'text' : 'password'"
            placeholder="请再次输入新密码"
            show-password
          />
        </ElFormItem>
      </ElForm>

      <template #footer>
        <div class="dialog-footer">
          <ElButton @click="closeDialog('password')">取消</ElButton>
          <ElButton type="primary" @click="changePassword" :loading="loading">
            确定
          </ElButton>
        </div>
      </template>
    </ElDialog>

    <!-- 绑定手机号对话框 -->
    <ElDialog
      v-model="dialogVisible.phone"
      title="绑定手机号"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElForm ref="phoneFormRef" :model="phoneForm" :rules="phoneRules" label-width="100px">
        <ElFormItem label="手机号" prop="phone">
          <ElInput v-model="phoneForm.phone" placeholder="请输入手机号码" />
        </ElFormItem>
        <ElFormItem label="验证码" prop="code">
          <div class="code-input">
            <ElInput v-model="phoneForm.code" placeholder="请输入验证码" />
            <ElButton
              @click="sendCode('phone')"
              :disabled="countdown.phone > 0"
              class="code-btn"
            >
              {{ countdown.phone > 0 ? `${countdown.phone}s` : '发送验证码' }}
            </ElButton>
          </div>
        </ElFormItem>
      </ElForm>

      <template #footer>
        <div class="dialog-footer">
          <ElButton @click="closeDialog('phone')">取消</ElButton>
          <ElButton type="primary" @click="bindPhone" :loading="loading">
            确定
          </ElButton>
        </div>
      </template>
    </ElDialog>

    <!-- 绑定邮箱对话框 -->
    <ElDialog
      v-model="dialogVisible.email"
      title="绑定邮箱"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElForm ref="emailFormRef" :model="emailForm" :rules="emailRules" label-width="100px">
        <ElFormItem label="邮箱地址" prop="email">
          <ElInput v-model="emailForm.email" placeholder="请输入邮箱地址" />
        </ElFormItem>
        <ElFormItem label="验证码" prop="code">
          <div class="code-input">
            <ElInput v-model="emailForm.code" placeholder="请输入验证码" />
            <ElButton
              @click="sendCode('email')"
              :disabled="countdown.email > 0"
              class="code-btn"
            >
              {{ countdown.email > 0 ? `${countdown.email}s` : '发送验证码' }}
            </ElButton>
          </div>
        </ElFormItem>
      </ElForm>

      <template #footer>
        <div class="dialog-footer">
          <ElButton @click="closeDialog('email')">取消</ElButton>
          <ElButton type="primary" @click="bindEmail" :loading="loading">
            确定
          </ElButton>
        </div>
      </template>
    </ElDialog>

    <!-- 实名认证对话框 -->
    <ElDialog
      v-model="dialogVisible.realName"
      title="实名认证"
      width="600px"
      :close-on-click-modal="false"
    >
      <ElSteps :active="realNameStep" class="auth-steps">
        <ElStep title="填写信息" />
        <ElStep title="提交审核" />
      </ElSteps>

      <div v-if="realNameStep === 0" class="auth-form">
        <ElAlert
          title="实名认证说明"
          type="info"
          :closable="false"
          class="auth-alert"
        >
          <template #default>
            <p>• 实名认证后可享受更多服务功能</p>
            <p>• 请确保填写的信息真实有效</p>
            <p>• 认证信息一经提交不可修改</p>
          </template>
        </ElAlert>

        <ElForm ref="realNameFormRef" :model="realNameForm" :rules="realNameRules" label-width="100px">
          <ElFormItem label="真实姓名" prop="realName">
            <ElInput v-model="realNameForm.realName" placeholder="请输入真实姓名" />
          </ElFormItem>
          <ElFormItem label="身份证号" prop="idCard">
            <ElInput v-model="realNameForm.idCard" placeholder="请输入身份证号码" />
          </ElFormItem>
        </ElForm>
      </div>

      <div v-else class="auth-success">
        <div class="success-icon">
          <ElIcon :size="60" color="#52c41a">
            <CircleCheck />
          </ElIcon>
        </div>
        <h3>提交成功</h3>
        <p>您的实名认证信息已提交，我们将在1-3个工作日内完成审核。</p>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <ElButton @click="closeDialog('realName')">取消</ElButton>
          <ElButton
            v-if="realNameStep === 0"
            type="primary"
            @click="realNameAuth"
            :loading="loading"
          >
            提交认证
          </ElButton>
        </div>
      </template>
    </ElDialog>

    <!-- 登录设备管理对话框 -->
    <ElDialog
      v-model="dialogVisible.devices"
      title="登录设备管理"
      width="700px"
    >
      <div class="devices-list">
        <div
          v-for="device in displaySecurityInfo.loginDevices"
          :key="device.id"
          class="device-item"
        >
          <div class="device-info">
            <div class="device-name">
              {{ device.deviceName }}
              <ElTag v-if="device.isCurrent" type="success" size="small">当前设备</ElTag>
            </div>
            <div class="device-details">
              <span class="device-location">{{ device.location }}</span>
              <span class="device-time">最后登录：{{ formatTime(device.lastLogin) }}</span>
            </div>
          </div>
          <div class="device-actions">
            <ElButton
              v-if="!device.isCurrent"
              type="danger"
              size="small"
              @click="logoutDevice(device.id, device.deviceName)"
            >
              登出
            </ElButton>
          </div>
        </div>

        <div v-if="!displaySecurityInfo.loginDevices?.length" class="empty-devices">
          <p>暂无登录设备</p>
        </div>
      </div>
    </ElDialog>
  </div>
</template>

<style scoped>
.account-security {
  max-width: 800px;
}

.security-card {
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.security-level {
  display: flex;
  align-items: center;
  gap: 8px;
}

.level-text {
  font-size: 14px;
  color: #595959;
}

.level-tag {
  border: none;
  color: white;
  font-weight: 500;
}

.level-score {
  font-size: 14px;
  color: #8c8c8c;
}

.security-skeleton {
  padding: 8px 0;
}

.security-content {
  padding: 8px 0;
}

.security-alert {
  margin-bottom: 24px;
}

.security-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.security-item:hover {
  background: #f5f5f5;
  border-color: #d9d9d9;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.item-info {
  flex: 1;
}

.item-title {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.item-desc {
  margin: 0;
  font-size: 14px;
  color: #8c8c8c;
  line-height: 1.4;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-status {
  display: flex;
  align-items: center;
}

.code-input {
  display: flex;
  gap: 8px;
  width: 100%;
}

.code-btn {
  flex-shrink: 0;
  min-width: 100px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.auth-steps {
  margin-bottom: 24px;
}

.auth-form {
  margin-top: 24px;
}

.auth-alert {
  margin-bottom: 24px;
}

.auth-success {
  text-align: center;
  padding: 40px 20px;
}

.success-icon {
  margin-bottom: 16px;
}

.auth-success h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #262626;
}

.auth-success p {
  margin: 0;
  color: #8c8c8c;
  line-height: 1.5;
}

.devices-list {
  max-height: 400px;
  overflow-y: auto;
}

.device-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.device-item:last-child {
  border-bottom: none;
}

.device-info {
  flex: 1;
}

.device-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.device-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.device-location,
.device-time {
  font-size: 14px;
  color: #8c8c8c;
}

.device-actions {
  flex-shrink: 0;
}

.empty-devices {
  text-align: center;
  padding: 40px 20px;
  color: #8c8c8c;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .security-level {
    justify-content: center;
  }

  .security-item {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .item-left {
    gap: 12px;
  }

  .item-right {
    justify-content: space-between;
  }

  .device-item {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .device-details {
    flex-direction: row;
    gap: 16px;
  }
}

@media (max-width: 480px) {
  .code-input {
    flex-direction: column;
  }

  .code-btn {
    min-width: auto;
  }

  .device-details {
    flex-direction: column;
    gap: 2px;
  }
}
</style>
