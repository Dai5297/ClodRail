<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="border-b border-slate-200 pb-5">
      <h2 class="text-2xl font-bold text-slate-800">账号安全</h2>
      <p class="mt-2 text-sm text-slate-500">管理您的账号安全设置</p>
    </div>

    <!-- 安全设置列表 -->
    <div class="bg-white rounded-xl shadow-sm border border-slate-200 divide-y divide-slate-100">
      <!-- 密码修改 -->
      <div class="p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div class="flex items-start gap-4">
          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd" />
            </svg>
          </div>
          <div>
            <h3 class="text-base font-medium text-slate-900">登录密码</h3>
            <p class="mt-1 text-sm text-slate-500">定期更换密码，保护账号安全</p>
          </div>
        </div>
        <button 
          @click="showPasswordDialog"
          class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
        >
          修改密码
        </button>
      </div>

      <!-- 手机号绑定 -->
      <div class="p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div class="flex items-start gap-4">
          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-green-50 text-green-600 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z" />
            </svg>
          </div>
          <div>
            <h3 class="text-base font-medium text-slate-900">手机号码</h3>
            <p class="mt-1 text-sm text-slate-500">已绑定手机号：<span class="font-medium text-slate-700">{{ userInfo.phone || '未绑定' }}</span></p>
          </div>
        </div>
        <button 
          @click="showPhoneChangeDialog"
          class="px-4 py-2 bg-white border border-slate-300 hover:bg-slate-50 text-slate-700 text-sm font-medium rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-slate-500 focus:ring-offset-2"
        >
          更换手机号
        </button>
      </div>

      <!-- 邮箱绑定 -->
      <div class="p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div class="flex items-start gap-4">
          <div class="flex-shrink-0 h-10 w-10 rounded-full bg-purple-50 text-purple-600 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z" />
              <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z" />
            </svg>
          </div>
          <div>
            <h3 class="text-base font-medium text-slate-900">邮箱地址</h3>
            <p class="mt-1 text-sm text-slate-500">已绑定邮箱：<span class="font-medium text-slate-700">{{ userInfo.email || '未绑定' }}</span></p>
          </div>
        </div>
        <button 
          @click="showEmailChangeDialog"
          class="px-4 py-2 bg-white border border-slate-300 hover:bg-slate-50 text-slate-700 text-sm font-medium rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-slate-500 focus:ring-offset-2"
        >
          更换邮箱
        </button>
      </div>
    </div>

    <!-- 安全提示 -->
    <div class="bg-blue-50 rounded-xl p-6 border border-blue-100">
      <h3 class="flex items-center text-base font-semibold text-blue-900 mb-4">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-blue-600" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
        </svg>
        安全提示
      </h3>
      <ul class="space-y-2 text-sm text-blue-800 list-disc list-inside ml-1">
        <li>定期更换密码，建议使用字母、数字和符号的组合</li>
        <li>不要在公共场所或他人设备上登录账号</li>
        <li>发现异常登录或操作，请及时修改密码</li>
        <li>保护好您的手机和邮箱，避免账号被盗用</li>
      </ul>
    </div>

    <!-- 修改密码弹窗 -->
    <Transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div v-if="passwordDialogVisible" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6">
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="handleClosePasswordDialog"></div>
        <div class="relative w-full max-w-md bg-white rounded-xl shadow-2xl p-6">
          <div class="mb-6">
            <h3 class="text-xl font-bold text-slate-900">修改密码</h3>
            <p class="mt-1 text-sm text-slate-500">为了您的账号安全，请设置强密码</p>
          </div>
          
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">当前密码</label>
              <input 
                v-model="passwordForm.currentPassword"
                type="password"
                placeholder="请输入当前密码"
                class="block w-full rounded-lg border-slate-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm py-2.5 px-3 border"
              />
              <p v-if="passwordErrors.currentPassword" class="mt-1 text-xs text-red-500">{{ passwordErrors.currentPassword }}</p>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">新密码</label>
              <input 
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                class="block w-full rounded-lg border-slate-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm py-2.5 px-3 border"
              />
              <div class="mt-2">
                <div class="flex items-center justify-between mb-1">
                  <span class="text-xs text-slate-500">密码强度：</span>
                  <span class="text-xs font-medium" :class="passwordStrengthColor">{{ passwordStrengthText }}</span>
                </div>
                <div class="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden">
                  <div 
                    class="h-full transition-all duration-300" 
                    :class="passwordStrengthBgColor"
                    :style="{ width: `${passwordStrength}%` }"
                  ></div>
                </div>
              </div>
              <p v-if="passwordErrors.newPassword" class="mt-1 text-xs text-red-500">{{ passwordErrors.newPassword }}</p>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">确认密码</label>
              <input 
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                class="block w-full rounded-lg border-slate-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm py-2.5 px-3 border"
              />
              <p v-if="passwordErrors.confirmPassword" class="mt-1 text-xs text-red-500">{{ passwordErrors.confirmPassword }}</p>
            </div>
          </div>
          
          <div class="mt-8 flex justify-end gap-3">
            <button 
              @click="handleClosePasswordDialog"
              class="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 focus:outline-none focus:ring-2 focus:ring-slate-500 focus:ring-offset-2"
            >
              取消
            </button>
            <button 
              @click="handleSubmitPassword"
              :disabled="submitting"
              class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
            >
              <svg v-if="submitting" class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              确认修改
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 更换手机号弹窗 -->
    <Transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div v-if="phoneChangeDialogVisible" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6">
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="handleClosePhoneDialog"></div>
        <div class="relative w-full max-w-md bg-white rounded-xl shadow-2xl p-6">
          <div class="mb-6">
            <h3 class="text-xl font-bold text-slate-900">更换手机号</h3>
            <p class="mt-1 text-sm text-slate-500">更换后下次登录请使用新手机号</p>
          </div>
          
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">当前手机号</label>
              <input 
                :value="userInfo.phone || '未绑定'"
                disabled
                class="block w-full rounded-lg border-slate-200 bg-slate-50 text-slate-500 sm:text-sm py-2.5 px-3 border cursor-not-allowed"
              />
            </div>
            
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">新手机号</label>
              <input 
                v-model="phoneForm.newPhone"
                type="tel"
                placeholder="请输入新手机号"
                maxlength="11"
                class="block w-full rounded-lg border-slate-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm py-2.5 px-3 border"
              />
              <p v-if="phoneErrors.newPhone" class="mt-1 text-xs text-red-500">{{ phoneErrors.newPhone }}</p>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">验证码</label>
              <div class="flex gap-3">
                <input 
                  v-model="phoneForm.code"
                  type="text"
                  placeholder="6位数字"
                  maxlength="6"
                  class="block w-full rounded-lg border-slate-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm py-2.5 px-3 border"
                />
                <button 
                  @click="sendPhoneCode"
                  :disabled="phoneCodeCountdown > 0 || sendingPhoneCode"
                  class="flex-shrink-0 w-32 px-3 py-2.5 text-sm font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed border border-blue-200"
                >
                  <span v-if="sendingPhoneCode" class="flex items-center justify-center gap-1">
                    <svg class="animate-spin h-3 w-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                    发送中
                  </span>
                  <span v-else-if="phoneCodeCountdown > 0">{{ phoneCodeCountdown }}s后重发</span>
                  <span v-else>发送验证码</span>
                </button>
              </div>
              <p v-if="phoneErrors.code" class="mt-1 text-xs text-red-500">{{ phoneErrors.code }}</p>
            </div>
          </div>
          
          <div class="mt-8 flex justify-end gap-3">
            <button 
              @click="handleClosePhoneDialog"
              class="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 focus:outline-none focus:ring-2 focus:ring-slate-500 focus:ring-offset-2"
            >
              取消
            </button>
            <button 
              @click="handleSubmitPhoneChange"
              :disabled="submittingPhone"
              class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
            >
              <svg v-if="submittingPhone" class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              确认更换
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 更换邮箱弹窗 -->
    <Transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div v-if="emailChangeDialogVisible" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6">
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="handleCloseEmailDialog"></div>
        <div class="relative w-full max-w-md bg-white rounded-xl shadow-2xl p-6">
          <div class="mb-6">
            <h3 class="text-xl font-bold text-slate-900">更换邮箱</h3>
            <p class="mt-1 text-sm text-slate-500">更换后将使用新邮箱接收通知</p>
          </div>
          
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">当前邮箱</label>
              <input 
                :value="userInfo.email || '未绑定'"
                disabled
                class="block w-full rounded-lg border-slate-200 bg-slate-50 text-slate-500 sm:text-sm py-2.5 px-3 border cursor-not-allowed"
              />
            </div>
            
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">新邮箱</label>
              <input 
                v-model="emailForm.newEmail"
                type="email"
                placeholder="请输入新邮箱地址"
                class="block w-full rounded-lg border-slate-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm py-2.5 px-3 border"
              />
              <p v-if="emailErrors.newEmail" class="mt-1 text-xs text-red-500">{{ emailErrors.newEmail }}</p>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">验证码</label>
              <div class="flex gap-3">
                <input 
                  v-model="emailForm.code"
                  type="text"
                  placeholder="6位数字"
                  maxlength="6"
                  class="block w-full rounded-lg border-slate-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm py-2.5 px-3 border"
                />
                <button 
                  @click="sendEmailCode"
                  :disabled="emailCodeCountdown > 0 || sendingEmailCode"
                  class="flex-shrink-0 w-32 px-3 py-2.5 text-sm font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed border border-blue-200"
                >
                  <span v-if="sendingEmailCode" class="flex items-center justify-center gap-1">
                    <svg class="animate-spin h-3 w-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                    发送中
                  </span>
                  <span v-else-if="emailCodeCountdown > 0">{{ emailCodeCountdown }}s后重发</span>
                  <span v-else>发送验证码</span>
                </button>
              </div>
              <p v-if="emailErrors.code" class="mt-1 text-xs text-red-500">{{ emailErrors.code }}</p>
            </div>
          </div>
          
          <div class="mt-8 flex justify-end gap-3">
            <button 
              @click="handleCloseEmailDialog"
              class="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 focus:outline-none focus:ring-2 focus:ring-slate-500 focus:ring-offset-2"
            >
              取消
            </button>
            <button 
              @click="handleSubmitEmailChange"
              :disabled="submittingEmail"
              class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
            >
              <svg v-if="submittingEmail" class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              确认更换
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { resetPassword, getPhoneCode, changePhone, sendEmailChangeCode, changeEmail } from '@/api/auth.js'
import { getUserInfo } from '@/api/user.js'
import toast from '@/components/Toast'

// 用户信息
const userInfo = ref({})
const fetchUserInfo = async () => {
  try {
    const response = await getUserInfo()
    if (response.code === 200) {
      userInfo.value = response.data
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

// ---------------- 密码修改 ----------------
const passwordDialogVisible = ref(false)
const submitting = ref(false)
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordErrors = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordStrength = computed(() => {
  const password = passwordForm.newPassword
  if (!password) return 0
  
  let score = 0
  if (password.length >= 8) score += 25
  else if (password.length >= 6) score += 15
  
  if (/[a-z]/.test(password)) score += 25
  if (/[A-Z]/.test(password)) score += 25
  if (/\d/.test(password)) score += 15
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) score += 10

  return Math.min(score, 100)
})

const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value
  if (!passwordForm.newPassword) return ''
  if (strength < 30) return '弱'
  if (strength < 60) return '中等'
  if (strength < 80) return '强'
  return '很强'
})

const passwordStrengthColor = computed(() => {
  const strength = passwordStrength.value
  if (!passwordForm.newPassword) return 'text-slate-500'
  if (strength < 30) return 'text-red-500'
  if (strength < 60) return 'text-yellow-500'
  if (strength < 80) return 'text-blue-500'
  return 'text-green-500'
})

const passwordStrengthBgColor = computed(() => {
  const strength = passwordStrength.value
  if (!passwordForm.newPassword) return 'bg-slate-200'
  if (strength < 30) return 'bg-red-500'
  if (strength < 60) return 'bg-yellow-500'
  if (strength < 80) return 'bg-blue-500'
  return 'bg-green-500'
})

const showPasswordDialog = () => {
  passwordDialogVisible.value = true
}

const handleClosePasswordDialog = () => {
  passwordDialogVisible.value = false
  resetPasswordForm()
}

const resetPasswordForm = () => {
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordErrors.currentPassword = ''
  passwordErrors.newPassword = ''
  passwordErrors.confirmPassword = ''
}

const validatePasswordForm = () => {
  let isValid = true
  passwordErrors.currentPassword = ''
  passwordErrors.newPassword = ''
  passwordErrors.confirmPassword = ''

  if (!passwordForm.currentPassword) {
    passwordErrors.currentPassword = '请输入当前密码'
    isValid = false
  } else if (passwordForm.currentPassword.length < 6) {
    passwordErrors.currentPassword = '密码长度不能少于6位'
    isValid = false
  }

  if (!passwordForm.newPassword) {
    passwordErrors.newPassword = '请输入新密码'
    isValid = false
  } else if (passwordForm.newPassword.length < 6) {
    passwordErrors.newPassword = '密码长度不能少于6位'
    isValid = false
  } else if (passwordForm.newPassword === passwordForm.currentPassword) {
    passwordErrors.newPassword = '新密码不能与当前密码相同'
    isValid = false
  }

  if (!passwordForm.confirmPassword) {
    passwordErrors.confirmPassword = '请确认新密码'
    isValid = false
  } else if (passwordForm.confirmPassword !== passwordForm.newPassword) {
    passwordErrors.confirmPassword = '两次输入的密码不一致'
    isValid = false
  }

  return isValid
}

const handleSubmitPassword = async () => {
  if (!validatePasswordForm()) return

  submitting.value = true
  try {
    const response = await resetPassword({
      oldPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    })
    if (response.code === 200) {
      toast.success('密码修改成功')
      handleClosePasswordDialog()
    } else {
      toast.error(response.message || '密码修改失败')
    }
  } catch (error) {
    toast.error('密码修改失败，请检查当前密码是否正确')
  } finally {
    submitting.value = false
  }
}

// ---------------- 手机号修改 ----------------
const phoneChangeDialogVisible = ref(false)
const submittingPhone = ref(false)
const sendingPhoneCode = ref(false)
const phoneCodeCountdown = ref(0)
const phoneForm = reactive({
  newPhone: '',
  code: ''
})
const phoneErrors = reactive({
  newPhone: '',
  code: ''
})

const showPhoneChangeDialog = () => {
  phoneChangeDialogVisible.value = true
}

const handleClosePhoneDialog = () => {
  phoneChangeDialogVisible.value = false
  resetPhoneForm()
}

const resetPhoneForm = () => {
  phoneForm.newPhone = ''
  phoneForm.code = ''
  phoneErrors.newPhone = ''
  phoneErrors.code = ''
}

const sendPhoneCode = async () => {
  phoneErrors.newPhone = ''
  if (!phoneForm.newPhone) {
    phoneErrors.newPhone = '请先输入新手机号'
    return
  }
  if (!/^1[3-9]\d{9}$/.test(phoneForm.newPhone)) {
    phoneErrors.newPhone = '请输入正确的手机号'
    return
  }

  try {
    sendingPhoneCode.value = true
    const response = await getPhoneCode({ phone: phoneForm.newPhone })
    if (response.code === 200) {
      toast.success('验证码已发送')
      startPhoneCountdown()
    } else {
      toast.error(response.message || '发送验证码失败')
    }
  } catch (error) {
    toast.error('发送验证码失败')
  } finally {
    sendingPhoneCode.value = false
  }
}

const startPhoneCountdown = () => {
  phoneCodeCountdown.value = 60
  const timer = setInterval(() => {
    phoneCodeCountdown.value--
    if (phoneCodeCountdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const validatePhoneForm = () => {
  let isValid = true
  phoneErrors.newPhone = ''
  phoneErrors.code = ''

  if (!phoneForm.newPhone) {
    phoneErrors.newPhone = '请输入手机号'
    isValid = false
  } else if (!/^1[3-9]\d{9}$/.test(phoneForm.newPhone)) {
    phoneErrors.newPhone = '请输入正确的手机号'
    isValid = false
  }

  if (!phoneForm.code) {
    phoneErrors.code = '请输入验证码'
    isValid = false
  } else if (!/^\d{6}$/.test(phoneForm.code)) {
    phoneErrors.code = '验证码为6位数字'
    isValid = false
  }

  return isValid
}

const handleSubmitPhoneChange = async () => {
  if (!validatePhoneForm()) return

  submittingPhone.value = true
  try {
    const response = await changePhone({
      newPhone: phoneForm.newPhone,
      code: phoneForm.code
    })

    if (response.code === 200) {
      toast.success('手机号更换成功')
      handleClosePhoneDialog()
      await fetchUserInfo()
    } else {
      toast.error(response.message || '手机号更换失败')
    }
  } catch (error) {
    toast.error('手机号更换失败')
  } finally {
    submittingPhone.value = false
  }
}

// ---------------- 邮箱修改 ----------------
const emailChangeDialogVisible = ref(false)
const submittingEmail = ref(false)
const sendingEmailCode = ref(false)
const emailCodeCountdown = ref(0)
const emailForm = reactive({
  newEmail: '',
  code: ''
})
const emailErrors = reactive({
  newEmail: '',
  code: ''
})

const showEmailChangeDialog = () => {
  emailChangeDialogVisible.value = true
}

const handleCloseEmailDialog = () => {
  emailChangeDialogVisible.value = false
  resetEmailForm()
}

const resetEmailForm = () => {
  emailForm.newEmail = ''
  emailForm.code = ''
  emailErrors.newEmail = ''
  emailErrors.code = ''
}

const sendEmailCode = async () => {
  emailErrors.newEmail = ''
  if (!emailForm.newEmail) {
    emailErrors.newEmail = '请先输入新邮箱地址'
    return
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailForm.newEmail)) {
    emailErrors.newEmail = '请输入正确的邮箱地址'
    return
  }

  try {
    sendingEmailCode.value = true
    const response = await sendEmailChangeCode({ email: emailForm.newEmail })
    if (response.code === 200) {
      toast.success('验证码已发送')
      startEmailCountdown()
    } else {
      toast.error(response.message || '发送验证码失败')
    }
  } catch (error) {
    toast.error('发送验证码失败')
  } finally {
    sendingEmailCode.value = false
  }
}

const startEmailCountdown = () => {
  emailCodeCountdown.value = 60
  const timer = setInterval(() => {
    emailCodeCountdown.value--
    if (emailCodeCountdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const validateEmailForm = () => {
  let isValid = true
  emailErrors.newEmail = ''
  emailErrors.code = ''

  if (!emailForm.newEmail) {
    emailErrors.newEmail = '请输入邮箱地址'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailForm.newEmail)) {
    emailErrors.newEmail = '请输入正确的邮箱地址'
    isValid = false
  }

  if (!emailForm.code) {
    emailErrors.code = '请输入验证码'
    isValid = false
  } else if (!/^\d{6}$/.test(emailForm.code)) {
    emailErrors.code = '验证码为6位数字'
    isValid = false
  }

  return isValid
}

const handleSubmitEmailChange = async () => {
  if (!validateEmailForm()) return

  submittingEmail.value = true
  try {
    const response = await changeEmail({
      newEmail: emailForm.newEmail,
      code: emailForm.code
    })

    if (response.code === 200) {
      toast.success('邮箱更换成功')
      handleCloseEmailDialog()
      await fetchUserInfo()
    } else {
      toast.error(response.message || '邮箱更换失败')
    }
  } catch (error) {
    toast.error('邮箱更换失败')
  } finally {
    submittingEmail.value = false
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>