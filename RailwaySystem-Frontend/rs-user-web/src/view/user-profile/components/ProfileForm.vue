<template>
  <div class="bg-white rounded-xl shadow-sm p-8 border border-gray-100">
    <form @submit.prevent class="space-y-8">
      <div class="space-y-6">
        <h3 class="text-lg font-semibold text-gray-900 border-b-2 border-gray-100 pb-3 flex items-center gap-2">
          <svg class="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
          基本信息
        </h3>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- 用户名 (只读) -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">用户名</label>
            <input 
              type="text" 
              v-model="formData.username" 
              disabled
              class="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg text-gray-500 cursor-not-allowed focus:outline-none"
            />
          </div>

          <!-- 真实姓名 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">
              真实姓名 <span class="text-red-500">*</span>
            </label>
            <input 
              type="text" 
              v-model="formData.realName" 
              class="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-colors"
              :class="{'border-red-500 focus:border-red-500 focus:ring-red-500/20': errors.realName}"
              placeholder="请输入真实姓名"
              @input="clearError('realName')"
            />
            <p v-if="errors.realName" class="mt-1 text-xs text-red-500">{{ errors.realName }}</p>
          </div>

          <!-- 手机号 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">
              手机号 <span class="text-red-500">*</span>
            </label>
            <input 
              type="tel" 
              v-model="formData.phone" 
              class="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-colors"
              :class="{'border-red-500 focus:border-red-500 focus:ring-red-500/20': errors.phone}"
              placeholder="请输入手机号"
              @input="clearError('phone')"
            />
            <p v-if="errors.phone" class="mt-1 text-xs text-red-500">{{ errors.phone }}</p>
          </div>

          <!-- 邮箱 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">
              邮箱 <span class="text-red-500">*</span>
            </label>
            <input 
              type="email" 
              v-model="formData.email" 
              class="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-colors"
              :class="{'border-red-500 focus:border-red-500 focus:ring-red-500/20': errors.email}"
              placeholder="请输入邮箱"
              @input="clearError('email')"
            />
            <p v-if="errors.email" class="mt-1 text-xs text-red-500">{{ errors.email }}</p>
          </div>

          <!-- 性别 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">性别</label>
            <select 
              v-model="formData.gender"
              class="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-colors appearance-none"
            >
              <option value="" disabled>请选择性别</option>
              <option value="男">男</option>
              <option value="女">女</option>
            </select>
          </div>

          <!-- 生日 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">生日</label>
            <input 
              type="date" 
              v-model="formData.birthday" 
              class="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-colors"
            />
          </div>

          <!-- 身份证号 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">
              身份证号 <span class="text-red-500">*</span>
            </label>
            <input 
              type="text" 
              v-model="formData.idCard" 
              class="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-colors"
              :class="{'border-red-500 focus:border-red-500 focus:ring-red-500/20': errors.idCard}"
              placeholder="请输入身份证号"
              @input="clearError('idCard')"
            />
            <p v-if="errors.idCard" class="mt-1 text-xs text-red-500">{{ errors.idCard }}</p>
          </div>

          <!-- 头像链接 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">头像链接</label>
            <input 
              type="text" 
              v-model="formData.icon" 
              class="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-colors"
              placeholder="请输入头像链接"
            />
          </div>

          <!-- 地址 -->
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 mb-1.5">地址</label>
            <input 
              type="text" 
              v-model="formData.address" 
              class="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-colors"
              placeholder="请输入详细地址"
            />
          </div>

          <!-- 个人简介 -->
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 mb-1.5">个人简介</label>
            <textarea 
              v-model="formData.introduction" 
              rows="4"
              class="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-colors resize-y"
              placeholder="请输入个人简介"
            ></textarea>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const props = defineProps({
  formData: {
    type: Object,
    default: () => ({})
  }
})

// 错误信息状态
const errors = reactive({
  realName: '',
  phone: '',
  email: '',
  idCard: ''
})

// 清除错误
const clearError = (field) => {
  errors[field] = ''
}

// 验证逻辑
const validate = () => {
  let isValid = true
  
  // 验证真实姓名
  if (!props.formData.realName) {
    errors.realName = '请输入真实姓名'
    isValid = false
  } else if (props.formData.realName.length < 2 || props.formData.realName.length > 20) {
    errors.realName = '真实姓名长度在 2 到 20 个字符'
    isValid = false
  }

  // 验证手机号
  if (!props.formData.phone) {
    errors.phone = '请输入手机号'
    isValid = false
  } else if (!/^1[3-9]\d{9}$/.test(props.formData.phone)) {
    errors.phone = '请输入正确的手机号'
    isValid = false
  }

  // 验证邮箱
  if (!props.formData.email) {
    errors.email = '请输入邮箱'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(props.formData.email)) {
    errors.email = '请输入正确的邮箱格式'
    isValid = false
  }

  // 验证身份证号
  if (!props.formData.idCard) {
    errors.idCard = '请输入身份证号'
    isValid = false
  } else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(props.formData.idCard)) {
    errors.idCard = '请输入正确的身份证号'
    isValid = false
  }

  return Promise.resolve(isValid)
}

// 清除验证状态
const clearValidate = () => {
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
}

// 暴露方法给父组件
defineExpose({
  validate,
  clearValidate
})
</script>