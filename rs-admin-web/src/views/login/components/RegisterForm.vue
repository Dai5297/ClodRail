<template>
  <el-form
    ref="registerFormRef"
    :model="registerForm"
    :rules="registerRules"
    class="login-form"
    size="large"
  >
    <el-form-item prop="username">
      <el-input 
        v-model="registerForm.username" 
        placeholder="用户名"
        :prefix-icon="User"
      />
    </el-form-item>
    
    <el-form-item prop="password">
      <el-input 
        v-model="registerForm.password" 
        type="password" 
        placeholder="密码"
        :prefix-icon="Lock"
        show-password
      />
    </el-form-item>

    <el-form-item prop="confirmPassword">
      <el-input 
        v-model="registerForm.confirmPassword" 
        type="password" 
        placeholder="确认密码"
        :prefix-icon="Lock"
        show-password
      />
    </el-form-item>

    <el-form-item prop="email">
      <el-input 
        v-model="registerForm.email" 
        placeholder="邮箱"
        :prefix-icon="Message"
      />
    </el-form-item>

    <el-form-item>
      <el-button 
        :loading="loading" 
        type="primary" 
        class="login-button" 
        @click="handleRegister"
      >
        注册
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validatePass2, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate((valid, fields) => {
    if (valid) {
      loading.value = true
      // 模拟注册请求
      setTimeout(() => {
        loading.value = false
        ElMessage.success('注册成功，请登录')
        // 触发切换到登录页的事件
        emit('success')
      }, 1500)
    }
  })
}

const emit = defineEmits(['success'])
</script>

<style scoped>
.login-form {
  margin-top: 20px;
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

:deep(.el-input__wrapper) {
  padding: 1px 15px;
  height: 42px;
  border-radius: 6px;
  box-shadow: 0 0 0 1px #E5E7EB inset;
  background-color: #ffffff;
  transition: all 0.2s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #B0B0B0 inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #000000 inset;
}

:deep(.el-input__inner) {
  height: 42px;
  line-height: 42px;
  color: #000000 !important;
}
</style>
