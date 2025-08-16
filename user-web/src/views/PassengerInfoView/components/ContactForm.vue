<template>
  <div class="contact-section">
    <div class="container">
      <h3>联系人信息</h3>
      <el-form 
        ref="formRef" 
        :model="modelValue" 
        :rules="contactRules" 
        label-width="100px"
        class="contact-form"
      >
        <div class="contact-fields">
          <el-form-item prop="name" label="联系人">
            <el-input 
              v-model="modelValue.name" 
              placeholder="请输入联系人姓名"
              maxlength="20"
              @input="updateValue"
            />
          </el-form-item>
          
          <el-form-item prop="phone" label="手机号码">
            <el-input 
              v-model="modelValue.phone" 
              placeholder="请输入联系人手机号码"
              maxlength="11"
              @input="updateValue"
            />
          </el-form-item>
          
          <el-form-item prop="email" label="邮箱">
            <el-input 
              v-model="modelValue.email" 
              placeholder="请输入邮箱地址（可选）"
              maxlength="50"
              @input="updateValue"
            />
          </el-form-item>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

// 定义props
const props = defineProps<{
  modelValue: {
    name: string
    phone: string
    email: string
  }
}>()

// 定义emits
const emit = defineEmits<{
  'update:modelValue': [value: { name: string; phone: string; email: string }]
}>()

// 表单引用
const formRef = ref<FormInstance>()

// 更新值
const updateValue = () => {
  emit('update:modelValue', props.modelValue)
}

// 联系人验证规则
const contactRules: FormRules = {
  name: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 暴露验证方法
const validate = () => {
  return formRef.value?.validate()
}

defineExpose({
  validate
})
</script>

<style scoped>
/* 联系人信息 */
.contact-section {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
}

.contact-section h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
}

.contact-fields {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .contact-fields {
    grid-template-columns: 1fr;
  }
  
  .container {
    padding: 0 16px;
  }
}
</style>