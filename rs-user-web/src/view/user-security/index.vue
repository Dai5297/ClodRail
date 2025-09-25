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
          <div class="setting-desc">已绑定手机号：138****8888</div>
        </div>
        <div class="setting-action">
          <el-button>更换手机号</el-button>
        </div>
      </div>

      <!-- 邮箱绑定 -->
      <div class="setting-item">
        <div class="setting-info">
          <div class="setting-title">
            <el-icon class="title-icon"><Message /></el-icon>
            邮箱地址
          </div>
          <div class="setting-desc">已绑定邮箱：user@example.com</div>
        </div>
        <div class="setting-action">
          <el-button>更换邮箱</el-button>
        </div>
      </div>

      <!-- 实名认证 -->
      <div class="setting-item">
        <div class="setting-info">
          <div class="setting-title">
            <el-icon class="title-icon"><UserFilled /></el-icon>
            实名认证
          </div>
          <div class="setting-desc">
            <el-tag type="success" size="small">已认证</el-tag>
            <span class="auth-name">张**</span>
          </div>
        </div>
        <div class="setting-action">
          <el-button disabled>已认证</el-button>
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
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Lock, Phone, Message, UserFilled, InfoFilled } from '@element-plus/icons-vue'
import { resetPassword } from '@/api/auth.js'

export default {
  name: 'UserSecurity',
  components: {
    Lock,
    Phone,
    Message,
    UserFilled,
    InfoFilled
  },
  setup() {
    const submitting = ref(false)
    const passwordDialogVisible = ref(false)
    const passwordFormRef = ref(null)

    const passwordForm = reactive({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
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
        
        await resetPassword({
          currentPassword: passwordForm.currentPassword,
          newPassword: passwordForm.newPassword
        })
        
        ElMessage.success('密码修改成功')
        passwordDialogVisible.value = false
        resetPasswordForm()
      } catch (error) {
        ElMessage.error('密码修改失败，请检查当前密码是否正确')
      } finally {
        submitting.value = false
      }
    }

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
      handleSubmitPassword
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

.strength-text {
  font-weight: 500;
}

.strength-text.weak {
  color: #ff4d4f;
}

.strength-text.medium {
  color: #fa8c16;
}

.strength-text.strong {
  color: #1890FF;
}

.strength-text.very-strong {
  color: #52c41a;
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