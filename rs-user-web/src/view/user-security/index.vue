<template>
  <div class="user-security-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">账号安全</h2>
      <p class="page-desc">管理您的账号安全设置</p>
    </div>

    <!-- 安全设置列表 -->
    <div class="security-settings">
      <!-- 密码修改 -->
      <div class="setting-item">
        <div class="setting-info">
          <div class="setting-title">
            <el-icon class="title-icon"><Lock /></el-icon>
            登录密码
          </div>
          <div class="setting-desc">定期更换密码，保护账号安全</div>
        </div>
        <div class="setting-action">
          <el-button type="primary" @click="showPasswordDialog">修改密码</el-button>
        </div>
      </div>

      <!-- 手机号绑定 -->
      <div class="setting-item">
        <div class="setting-info">
          <div class="setting-title">
            <el-icon class="title-icon"><Phone /></el-icon>
            手机号码
          </div>
          <div class="setting-desc">已绑定手机号：{{ userInfo.phone || '未绑定' }}</div>
        </div>
        <div class="setting-action">
          <el-button @click="showPhoneChangeDialog">更换手机号</el-button>
        </div>
      </div>

      <!-- 邮箱绑定 -->
      <div class="setting-item">
        <div class="setting-info">
          <div class="setting-title">
            <el-icon class="title-icon"><Message /></el-icon>
            邮箱地址
          </div>
          <div class="setting-desc">已绑定邮箱：{{ userInfo.email || '未绑定' }}</div>
        </div>
        <div class="setting-action">
          <el-button @click="showEmailChangeDialog">更换邮箱</el-button>
        </div>
      </div>


    </div>

    <!-- 安全提示 -->
    <div class="security-tips">
      <h3 class="tips-title">
        <el-icon><InfoFilled /></el-icon>
        安全提示
      </h3>
      <ul class="tips-list">
        <li>定期更换密码，建议使用字母、数字和符号的组合</li>
        <li>不要在公共场所或他人设备上登录账号</li>
        <li>发现异常登录或操作，请及时修改密码</li>
        <li>保护好您的手机和邮箱，避免账号被盗用</li>
      </ul>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="500px"
      :before-close="handleClosePasswordDialog"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input
            v-model="passwordForm.currentPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
            maxlength="20"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
            maxlength="20"
          />
          <div class="password-strength">
            <span class="strength-label">密码强度：</span>
            <div class="strength-bar">
              <div 
                class="strength-fill" 
                :class="passwordStrengthClass"
                :style="{ width: passwordStrengthWidth }"
              ></div>
            </div>
            <span class="strength-text" :class="passwordStrengthClass">
              {{ passwordStrengthText }}
            </span>
          </div>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
            maxlength="20"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClosePasswordDialog">取消</el-button>
          <el-button type="primary" @click="handleSubmitPassword" :loading="submitting">
            确认修改
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 更换手机号弹窗 -->
    <el-dialog
      v-model="phoneChangeDialogVisible"
      title="更换手机号"
      width="500px"
      :before-close="handleClosePhoneDialog"
    >
      <el-form
        ref="phoneFormRef"
        :model="phoneForm"
        :rules="phoneRules"
        label-width="120px"
      >
        <el-form-item label="当前手机号">
          <el-input :value="userInfo.phone || '未绑定'" disabled />
        </el-form-item>
        <el-form-item label="新手机号" prop="newPhone">
          <el-input
            v-model="phoneForm.newPhone"
            placeholder="请输入新手机号"
            maxlength="11"
          />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <div class="code-input-group">
            <el-input
              v-model="phoneForm.code"
              placeholder="请输入验证码"
              maxlength="6"
            />
            <el-button
              :disabled="phoneCodeCountdown > 0"
              @click="sendPhoneCode"
              :loading="sendingPhoneCode"
            >
              {{ phoneCodeCountdown > 0 ? `${phoneCodeCountdown}s后重发` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClosePhoneDialog">取消</el-button>
          <el-button type="primary" @click="handleSubmitPhoneChange" :loading="submittingPhone">
            确认更换
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 更换邮箱弹窗 -->
    <el-dialog
      v-model="emailChangeDialogVisible"
      title="更换邮箱"
      width="500px"
      :before-close="handleCloseEmailDialog"
    >
      <el-form
        ref="emailFormRef"
        :model="emailForm"
        :rules="emailRules"
        label-width="120px"
      >
        <el-form-item label="当前邮箱">
          <el-input :value="userInfo.email || '未绑定'" disabled />
        </el-form-item>
        <el-form-item label="新邮箱" prop="newEmail">
          <el-input
            v-model="emailForm.newEmail"
            placeholder="请输入新邮箱地址"
          />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <div class="code-input-group">
            <el-input
              v-model="emailForm.code"
              placeholder="请输入验证码"
              maxlength="6"
            />
            <el-button
              :disabled="emailCodeCountdown > 0"
              @click="sendEmailCode"
              :loading="sendingEmailCode"
            >
              {{ emailCodeCountdown > 0 ? `${emailCodeCountdown}s后重发` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseEmailDialog">取消</el-button>
          <el-button type="primary" @click="handleSubmitEmailChange" :loading="submittingEmail">
            确认更换
          </el-button>
        </span>
      </template>
    </el-dialog>


  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Lock, Phone, Message, InfoFilled } from '@element-plus/icons-vue'
import { resetPassword, getPhoneCode } from '@/api/auth.js'
import { 
  getUserInfo
} from '@/api/user.js'

import {
  changePhone,
  sendEmailChangeCode,
  changeEmail
} from '@/api/auth.js'

export default {
  name: 'UserSecurity',
  components: {
    Lock,
    Phone,
    Message,
    InfoFilled
  },
  setup() {
    const submitting = ref(false)
    const passwordDialogVisible = ref(false)
    const passwordFormRef = ref(null)
    const userInfo = ref({})

    // 更换手机号相关
    const phoneChangeDialogVisible = ref(false)
    const phoneFormRef = ref(null)
    const submittingPhone = ref(false)
    const sendingPhoneCode = ref(false)
    const phoneCodeCountdown = ref(0)

    // 更换邮箱相关
    const emailChangeDialogVisible = ref(false)
    const emailFormRef = ref(null)
    const submittingEmail = ref(false)
    const sendingEmailCode = ref(false)
    const emailCodeCountdown = ref(0)



    const passwordForm = reactive({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    const phoneForm = reactive({
      newPhone: '',
      code: ''
    })

    const emailForm = reactive({
      newEmail: '',
      code: ''
    })



    const validateCurrentPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入当前密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        callback()
      }
    }

    const validateNewPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入新密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else if (value === passwordForm.currentPassword) {
        callback(new Error('新密码不能与当前密码相同'))
      } else {
        // 触发确认密码的验证
        if (passwordForm.confirmPassword) {
          passwordFormRef.value.validateField('confirmPassword')
        }
        callback()
      }
    }

    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请确认新密码'))
      } else if (value !== passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    const passwordRules = {
      currentPassword: [
        { validator: validateCurrentPassword, trigger: 'blur' }
      ],
      newPassword: [
        { validator: validateNewPassword, trigger: 'blur' }
      ],
      confirmPassword: [
        { validator: validateConfirmPassword, trigger: 'blur' }
      ]
    }

    // 手机号验证规则
    const phoneRules = {
      newPhone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ],
      code: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { pattern: /^\d{6}$/, message: '验证码为6位数字', trigger: 'blur' }
      ]
    }

    // 邮箱验证规则
    const emailRules = {
      newEmail: [
        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      code: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { pattern: /^\d{6}$/, message: '验证码为6位数字', trigger: 'blur' }
      ]
    }



    // 密码强度计算
    const passwordStrength = computed(() => {
      const password = passwordForm.newPassword
      if (!password) return 0

      let score = 0
      
      // 长度检查
      if (password.length >= 8) score += 25
      else if (password.length >= 6) score += 15
      
      // 包含小写字母
      if (/[a-z]/.test(password)) score += 25
      
      // 包含大写字母
      if (/[A-Z]/.test(password)) score += 25
      
      // 包含数字
      if (/\d/.test(password)) score += 15
      
      // 包含特殊字符
      if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) score += 10

      return Math.min(score, 100)
    })

    const passwordStrengthClass = computed(() => {
      const strength = passwordStrength.value
      if (strength < 30) return 'weak'
      if (strength < 60) return 'medium'
      if (strength < 80) return 'strong'
      return 'very-strong'
    })

    const passwordStrengthWidth = computed(() => {
      return `${passwordStrength.value}%`
    })

    const passwordStrengthText = computed(() => {
      const strength = passwordStrength.value
      if (strength < 30) return '弱'
      if (strength < 60) return '中等'
      if (strength < 80) return '强'
      return '很强'
    })

    const showPasswordDialog = () => {
      passwordDialogVisible.value = true
    }

    const handleClosePasswordDialog = () => {
      passwordDialogVisible.value = false
      resetPasswordForm()
    }

    const resetPasswordForm = () => {
      Object.assign(passwordForm, {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      })
      if (passwordFormRef.value) {
        passwordFormRef.value.clearValidate()
      }
    }

    const handleSubmitPassword = async () => {
      try {
        const valid = await passwordFormRef.value.validate()
        if (!valid) return

        submitting.value = true
        
        const response = await resetPassword({
          oldPassword: passwordForm.currentPassword,
          newPassword: passwordForm.newPassword
        });
        if (response.code === 200) {
          ElMessage.success('密码修改成功')
          passwordDialogVisible.value = false
          resetPasswordForm()
        } else {
          ElMessage.error(response.message || '密码修改失败')
        }
      } catch (error) {
        ElMessage.error('密码修改失败，请检查当前密码是否正确')
      } finally {
        submitting.value = false
      }
    }

    // 获取用户信息
    const fetchUserInfo = async () => {
      try {
        const response = await getUserInfo()
        if (response.code === 200) {
          userInfo.value = response.data
        } else {
          console.error('获取用户信息失败:', response.message)
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    }

    // 显示更换手机号弹窗
    const showPhoneChangeDialog = () => {
      phoneChangeDialogVisible.value = true
    }

    // 关闭更换手机号弹窗
    const handleClosePhoneDialog = () => {
      phoneChangeDialogVisible.value = false
      resetPhoneForm()
    }

    // 重置手机号表单
    const resetPhoneForm = () => {
      Object.assign(phoneForm, {
        newPhone: '',
        code: ''
      })
      if (phoneFormRef.value) {
        phoneFormRef.value.clearValidate()
      }
    }

    // 发送手机验证码
    const sendPhoneCode = async () => {
      if (!phoneForm.newPhone) {
        ElMessage.error('请先输入新手机号')
        return
      }
      if (!/^1[3-9]\d{9}$/.test(phoneForm.newPhone)) {
        ElMessage.error('请输入正确的手机号')
        return
      }

      try {
        sendingPhoneCode.value = true
        const response = await getPhoneCode({ phone: phoneForm.newPhone })
        if (response.code === 200) {
          ElMessage.success('验证码已发送')
          startPhoneCountdown()
        } else {
          ElMessage.error(response.message || '发送验证码失败')
        }
      } catch (error) {
        ElMessage.error('发送验证码失败')
      } finally {
        sendingPhoneCode.value = false
      }
    }

    // 开始手机验证码倒计时
    const startPhoneCountdown = () => {
      phoneCodeCountdown.value = 60
      const timer = setInterval(() => {
        phoneCodeCountdown.value--
        if (phoneCodeCountdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    }

    // 提交更换手机号
    const handleSubmitPhoneChange = async () => {
      try {
        const valid = await phoneFormRef.value.validate()
        if (!valid) return

        submittingPhone.value = true
        const response = await changePhone({
          newPhone: phoneForm.newPhone,
          code: phoneForm.code
        })

        if (response.code === 200) {
          ElMessage.success('手机号更换成功')
          phoneChangeDialogVisible.value = false
          resetPhoneForm()
          await fetchUserInfo()
        } else {
          ElMessage.error(response.message || '手机号更换失败')
        }
      } catch (error) {
        ElMessage.error('手机号更换失败')
      } finally {
        submittingPhone.value = false
      }
    }

    // 显示更换邮箱弹窗
    const showEmailChangeDialog = () => {
      emailChangeDialogVisible.value = true
    }

    // 关闭更换邮箱弹窗
    const handleCloseEmailDialog = () => {
      emailChangeDialogVisible.value = false
      resetEmailForm()
    }

    // 重置邮箱表单
    const resetEmailForm = () => {
      Object.assign(emailForm, {
        newEmail: '',
        code: ''
      })
      if (emailFormRef.value) {
        emailFormRef.value.clearValidate()
      }
    }

    // 发送邮箱验证码
    const sendEmailCode = async () => {
      if (!emailForm.newEmail) {
        ElMessage.error('请先输入新邮箱地址')
        return
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailForm.newEmail)) {
        ElMessage.error('请输入正确的邮箱地址')
        return
      }

      try {
        sendingEmailCode.value = true
        const response = await sendEmailChangeCode({ email: emailForm.newEmail })
        if (response.code === 200) {
          ElMessage.success('验证码已发送')
          startEmailCountdown()
        } else {
          ElMessage.error(response.message || '发送验证码失败')
        }
      } catch (error) {
        ElMessage.error('发送验证码失败')
      } finally {
        sendingEmailCode.value = false
      }
    }

    // 开始邮箱验证码倒计时
    const startEmailCountdown = () => {
      emailCodeCountdown.value = 60
      const timer = setInterval(() => {
        emailCodeCountdown.value--
        if (emailCodeCountdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    }

    // 提交更换邮箱
    const handleSubmitEmailChange = async () => {
      try {
        const valid = await emailFormRef.value.validate()
        if (!valid) return

        submittingEmail.value = true
        const response = await changeEmail({
          newEmail: emailForm.newEmail,
          code: emailForm.code
        })

        if (response.code === 200) {
          ElMessage.success('邮箱更换成功')
          emailChangeDialogVisible.value = false
          resetEmailForm()
          await fetchUserInfo()
        } else {
          ElMessage.error(response.message || '邮箱更换失败')
        }
      } catch (error) {
        ElMessage.error('邮箱更换失败')
      } finally {
        submittingEmail.value = false
      }
    }



    // 页面初始化
    onMounted(() => {
      fetchUserInfo()
    })

    return {
      submitting,
      passwordDialogVisible,
      passwordFormRef,
      passwordForm,
      passwordRules,
      passwordStrengthClass,
      passwordStrengthWidth,
      passwordStrengthText,
      showPasswordDialog,
      handleClosePasswordDialog,
      handleSubmitPassword,
      userInfo,
      // 手机号相关
      phoneChangeDialogVisible,
      phoneFormRef,
      phoneForm,
      phoneRules,
      submittingPhone,
      sendingPhoneCode,
      phoneCodeCountdown,
      showPhoneChangeDialog,
      handleClosePhoneDialog,
      sendPhoneCode,
      handleSubmitPhoneChange,
      // 邮箱相关
      emailChangeDialogVisible,
      emailFormRef,
      emailForm,
      emailRules,
      submittingEmail,
      sendingEmailCode,
      emailCodeCountdown,
      showEmailChangeDialog,
      handleCloseEmailDialog,
      sendEmailCode,
      handleSubmitEmailChange
    }
  }
}
</script>

<style scoped>
.user-security-page {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 8px 0;
}

.page-desc {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.security-settings {
  background: white;
  border-radius: 8px;
  margin-bottom: 24px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f5f5f5;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info {
  flex: 1;
}

.setting-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 8px;
}

.title-icon {
  margin-right: 8px;
  color: #1890FF;
}

.setting-desc {
  font-size: 14px;
  color: #8c8c8c;
  display: flex;
  align-items: center;
  gap: 8px;
}

.auth-name {
  margin-left: 8px;
}

.setting-action {
  margin-left: 16px;
}

.security-tips {
  background: #f6f8fa;
  border-radius: 8px;
  padding: 20px;
}

.tips-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #262626;
  margin: 0 0 16px 0;
}

.tips-title .el-icon {
  margin-right: 8px;
  color: #1890FF;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  color: #595959;
}

.tips-list li {
  margin-bottom: 8px;
  line-height: 1.5;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

.password-strength {
  display: flex;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
}

.strength-label {
  color: #8c8c8c;
  margin-right: 8px;
}

.strength-bar {
  width: 100px;
  height: 4px;
  background: #f5f5f5;
  border-radius: 2px;
  margin-right: 8px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 2px;
  transition: all 0.3s;
}

.strength-fill.weak {
  background: #ff4d4f;
}

.strength-fill.medium {
  background: #fa8c16;
}

.strength-fill.strong {
  background: #1890FF;
}

.strength-fill.very-strong {
  background: #52c41a;
}

.code-input-group {
  display: flex;
  gap: 12px;
}

.code-input-group .el-input {
  flex: 1;
}

.code-input-group .el-button {
  white-space: nowrap;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__inner) {
  border-radius: 6px;
}

:deep(.el-button) {
  border-radius: 6px;
}
</style>