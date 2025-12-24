<template>
  <Transition
    enter-active-class="transition duration-200 ease-out"
    enter-from-class="opacity-0"
    enter-to-class="opacity-100"
    leave-active-class="transition duration-150 ease-in"
    leave-from-class="opacity-100"
    leave-to-class="opacity-0"
  >
    <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6" role="dialog" aria-modal="true">
      <!-- Backdrop -->
      <div class="fixed inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" @click="handleClose"></div>

      <!-- Modal panel -->
      <div class="relative bg-white rounded-2xl shadow-xl w-full max-w-lg overflow-hidden transform transition-all flex flex-col max-h-[90vh]">
        <!-- Header -->
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-white z-10">
          <h3 class="text-lg font-semibold text-slate-900">
            {{ isEdit ? '编辑联系人' : '添加联系人' }}
          </h3>
          <button 
            @click="handleClose"
            class="text-slate-400 hover:text-slate-500 hover:bg-slate-100 rounded-full p-1 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </button>
        </div>

        <!-- Body -->
        <div class="px-6 py-6 overflow-y-auto custom-scrollbar">
          <form @submit.prevent="handleSubmit" class="space-y-5">
            <!-- Name -->
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">
                姓名 <span class="text-red-500">*</span>
              </label>
              <input 
                v-model="formData.name"
                type="text"
                placeholder="请输入姓名"
                class="w-full px-4 py-2 bg-white border border-slate-300 rounded-lg text-sm shadow-sm placeholder-slate-400
                  focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500
                  disabled:bg-slate-50 disabled:text-slate-500 disabled:border-slate-200 transition-all"
                :class="{'border-red-300 focus:border-red-500 focus:ring-red-500': errors.name}"
                @input="validateField('name')"
              />
              <p v-if="errors.name" class="mt-1 text-xs text-red-500">{{ errors.name }}</p>
            </div>

            <!-- Phone -->
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">
                手机号码 <span class="text-red-500">*</span>
              </label>
              <input 
                v-model="formData.phone"
                type="tel"
                placeholder="请输入手机号码"
                maxlength="11"
                class="w-full px-4 py-2 bg-white border border-slate-300 rounded-lg text-sm shadow-sm placeholder-slate-400
                  focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500
                  disabled:bg-slate-50 disabled:text-slate-500 disabled:border-slate-200 transition-all"
                :class="{'border-red-300 focus:border-red-500 focus:ring-red-500': errors.phone}"
                @input="validateField('phone')"
              />
              <p v-if="errors.phone" class="mt-1 text-xs text-red-500">{{ errors.phone }}</p>
            </div>

            <!-- ID Card -->
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">
                身份证号 <span class="text-red-500">*</span>
              </label>
              <input 
                v-model="formData.idCard"
                type="text"
                placeholder="请输入身份证号"
                maxlength="18"
                class="w-full px-4 py-2 bg-white border border-slate-300 rounded-lg text-sm shadow-sm placeholder-slate-400
                  focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500
                  disabled:bg-slate-50 disabled:text-slate-500 disabled:border-slate-200 transition-all font-mono"
                :class="{'border-red-300 focus:border-red-500 focus:ring-red-500': errors.idCard}"
                @input="validateField('idCard')"
              />
              <p v-if="errors.idCard" class="mt-1 text-xs text-red-500">{{ errors.idCard }}</p>
            </div>

            <!-- Email -->
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">
                邮箱地址
              </label>
              <input 
                v-model="formData.email"
                type="email"
                placeholder="请输入邮箱地址（选填）"
                class="w-full px-4 py-2 bg-white border border-slate-300 rounded-lg text-sm shadow-sm placeholder-slate-400
                  focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500
                  disabled:bg-slate-50 disabled:text-slate-500 disabled:border-slate-200 transition-all"
                :class="{'border-red-300 focus:border-red-500 focus:ring-red-500': errors.email}"
                @input="validateField('email')"
              />
              <p v-if="errors.email" class="mt-1 text-xs text-red-500">{{ errors.email }}</p>
            </div>

            <!-- Passenger Type -->
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">
                乘客类型
              </label>
              <div class="relative">
                <select
                  v-model="formData.passengerType"
                  class="w-full px-4 py-2 bg-white border border-slate-300 rounded-lg text-sm shadow-sm placeholder-slate-400
                    focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500
                    appearance-none cursor-pointer"
                >
                  <option :value="1">成人</option>
                  <option :value="2">儿童</option>
                  <option :value="3">学生</option>
                  <option :value="4">老人</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                  <svg class="h-4 w-4 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                  </svg>
                </div>
              </div>
            </div>

            <!-- Switches -->
            <div class="flex items-center gap-8">
              <!-- Default -->
              <div class="flex items-center justify-between gap-3">
                <span class="text-sm font-medium text-slate-700">设为默认</span>
                <button 
                  type="button"
                  class="relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                  :class="formData.isDefault === 1 ? 'bg-blue-600' : 'bg-slate-200'"
                  role="switch"
                  :aria-checked="formData.isDefault === 1"
                  @click="formData.isDefault = formData.isDefault === 1 ? 0 : 1"
                >
                  <span 
                    class="pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out"
                    :class="formData.isDefault === 1 ? 'translate-x-5' : 'translate-x-0'"
                  ></span>
                </button>
              </div>

              <!-- Status -->
              <div class="flex items-center justify-between gap-3">
                <span class="text-sm font-medium text-slate-700">状态</span>
                <button 
                  type="button"
                  class="relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                  :class="formData.status === 1 ? 'bg-green-500' : 'bg-slate-200'"
                  role="switch"
                  :aria-checked="formData.status === 1"
                  @click="formData.status = formData.status === 1 ? 0 : 1"
                >
                  <span 
                    class="pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out"
                    :class="formData.status === 1 ? 'translate-x-5' : 'translate-x-0'"
                  ></span>
                </button>
              </div>
            </div>

            <!-- Remark -->
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">
                备注信息
              </label>
              <textarea
                v-model="formData.remark"
                rows="3"
                placeholder="请输入备注信息（选填）"
                maxlength="200"
                class="w-full px-4 py-2 bg-white border border-slate-300 rounded-lg text-sm shadow-sm placeholder-slate-400
                  focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500
                  resize-none transition-all"
              ></textarea>
              <div class="flex justify-end mt-1">
                <span class="text-xs text-slate-400">{{ formData.remark ? formData.remark.length : 0 }}/200</span>
              </div>
            </div>
          </form>
        </div>

        <!-- Footer -->
        <div class="px-6 py-4 bg-slate-50 border-t border-slate-100 flex items-center justify-end gap-3">
          <button 
            @click="handleClose"
            class="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all"
          >
            取消
          </button>
          <button 
            @click="handleSubmit"
            :disabled="submitting"
            class="inline-flex items-center px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-sm"
          >
            <svg v-if="submitting" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ isEdit ? '更新' : '添加' }}
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { reactive } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  isEdit: {
    type: Boolean,
    default: false
  },
  formData: {
    type: Object,
    required: true
  },
  submitting: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'submit'])

const errors = reactive({
  name: '',
  phone: '',
  idCard: '',
  email: ''
})

const validateField = (field) => {
  errors[field] = ''
  
  if (field === 'name') {
    if (!props.formData.name) {
      errors.name = '请输入姓名'
    } else if (props.formData.name.length < 2 || props.formData.name.length > 20) {
      errors.name = '姓名长度在 2 到 20 个字符'
    }
  }
  
  if (field === 'phone') {
    if (!props.formData.phone) {
      errors.phone = '请输入手机号'
    } else if (!/^1[3-9]\d{9}$/.test(props.formData.phone)) {
      errors.phone = '请输入正确的手机号'
    }
  }
  
  if (field === 'idCard') {
    if (!props.formData.idCard) {
      errors.idCard = '请输入身份证号'
    } else if (!/^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(props.formData.idCard)) {
      errors.idCard = '请输入正确的身份证号'
    }
  }
  
  if (field === 'email' && props.formData.email) {
    if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(props.formData.email)) {
      errors.email = '请输入正确的邮箱地址'
    }
  }
}

const validateForm = () => {
  validateField('name')
  validateField('phone')
  validateField('idCard')
  validateField('email')
  
  return !Object.values(errors).some(error => error)
}

const handleClose = () => {
  // Clear errors on close
  Object.keys(errors).forEach(key => errors[key] = '')
  emit('close')
}

const handleSubmit = () => {
  if (validateForm()) {
    emit('submit')
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>