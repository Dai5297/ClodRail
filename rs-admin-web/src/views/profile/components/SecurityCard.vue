<template>
  <el-card class="info-card">
    <template #header>
      <span>{{ $t('profile.securitySettings') }}</span>
    </template>

    <div class="security-item">
      <div class="security-info">
        <div class="security-title">{{ $t('profile.security.password') }}</div>
        <div class="security-desc">{{ $t('profile.security.passwordDesc') }}</div>
      </div>
      <el-button @click="showDialog">{{ $t('profile.security.changePassword') }}</el-button>
    </div>
  </el-card>

  <!-- 修改密码对话框 -->
  <el-dialog
    v-model="showPasswordDialog"
    :title="$t('profile.security.changePassword')"
    width="500px"
  >
    <el-form
      ref="passwordFormRef"
      :model="passwordForm"
      :rules="passwordRules"
      label-width="100px"
    >
      <el-form-item :label="$t('profile.security.oldPassword')" prop="oldPassword">
        <el-input
          v-model="passwordForm.oldPassword"
          type="password"
          :placeholder="$t('profile.security.passwordPlaceholder')"
          show-password
        />
      </el-form-item>

      <el-form-item :label="$t('profile.security.newPassword')" prop="newPassword">
        <el-input
          v-model="passwordForm.newPassword"
          type="password"
          :placeholder="$t('profile.security.passwordPlaceholder')"
          show-password
        />
      </el-form-item>

      <el-form-item :label="$t('profile.security.confirmPassword')" prop="confirmPassword">
        <el-input
          v-model="passwordForm.confirmPassword"
          type="password"
          :placeholder="$t('profile.security.confirmPlaceholder')"
          show-password
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="showPasswordDialog = false">{{ $t('profile.info.cancel') }}</el-button>
      <el-button type="primary" @click="handlePasswordChange">{{ $t('common.confirm') || 'Confirm' }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { changePassword } from '@/api/auth'

const { t } = useI18n()
const router = useRouter()

const showPasswordDialog = ref(false)
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error(t('profile.security.confirmPlaceholder')))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error(t('profile.security.passwordMismatch')))
  } else {
    callback()
  }
}

const passwordRules = computed(() => ({
  oldPassword: [
    { required: true, message: t('profile.security.passwordPlaceholder'), trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: t('profile.security.passwordPlaceholder'), trigger: 'blur' },
    { min: 6, message: t('profile.security.passwordLength'), trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}))

const showDialog = () => {
  showPasswordDialog.value = true
}

// 修改密码
const handlePasswordChange = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    ElMessage.success(t('profile.security.changeSuccess'))
    showPasswordDialog.value = false
    
    // 重置表单
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('修改密码失败:', error)
      ElMessage.error(t('profile.security.changeError'))
    }
  }
}
</script>

<style scoped>
.info-card {
  background: var(--card-bg);
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.security-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-color);
  margin-bottom: 4px;
}

.security-desc {
  font-size: 13px;
  color: var(--text-color-secondary);
}

:deep(.el-card__header) {
  background: var(--bg-color-secondary);
  border-bottom: 1px solid var(--border-color);
}

/* Button Styles */
:deep(.el-button--primary) {
  background-color: #000000;
  border-color: #000000;
  color: #ffffff;
}

:deep(.el-button--primary:hover) {
  background-color: #333333;
  border-color: #333333;
}

/* Dark mode overrides */
:global(html.dark) .el-dialog :deep(.el-button--primary) {
  background-color: #ffffff;
  border-color: #ffffff;
  color: #000000;
}

:global(html.dark) .el-dialog :deep(.el-button--primary:hover) {
  background-color: #e5e5e5;
  border-color: #e5e5e5;
}
</style>
