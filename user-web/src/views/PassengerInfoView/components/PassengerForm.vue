<template>
  <div class="passenger-form-container">
    <div class="container">
      <div class="form-header">
        <h3>乘客信息</h3>
        <div class="form-tips">
          <el-icon><InfoFilled /></el-icon>
          <span>请确保乘客信息与有效身份证件一致</span>
        </div>
      </div>

      <el-form 
        ref="formRef" 
        :model="modelValue" 
        :rules="formRules" 
        label-width="100px"
        class="passenger-form"
      >
        <div 
          v-for="(passenger, index) in modelValue.passengers" 
          :key="index"
          class="passenger-item"
        >
          <div class="passenger-header">
            <h4>乘客{{ index + 1 }}</h4>
            <div class="seat-number">{{ orderInfo.seatNumbers[index] }}</div>
          </div>
          
          <div class="passenger-fields">
            <el-form-item 
              :prop="`passengers.${index}.name`" 
              label="姓名"
              :rules="formRules.name"
            >
              <el-input 
                v-model="passenger.name" 
                placeholder="请输入真实姓名"
                maxlength="20"
                show-word-limit
                @input="updateValue"
              />
            </el-form-item>
            
            <el-form-item 
              label="身份证号" 
              :prop="`passengers.${index}.idCard`"
              :rules="getIdCardRules()"
            >
              <el-input v-model="passenger.idCard" placeholder="请输入身份证号" @input="updateValue" />
            </el-form-item>
            

            
            <el-form-item 
              :prop="`passengers.${index}.phone`" 
              label="手机号码"
              :rules="formRules.phone"
            >
              <el-input 
                v-model="passenger.phone" 
                placeholder="请输入手机号码"
                maxlength="11"
                @input="updateValue"
              />
            </el-form-item>
            
            <el-form-item 
              :prop="`passengers.${index}.passengerType`" 
              label="乘客类型"
              :rules="formRules.passengerType"
            >
              <el-select v-model="passenger.passengerType" placeholder="请选择乘客类型" @change="updateValue">
                <el-option label="成人" value="成人" />
                <el-option label="儿童" value="儿童" />
                <el-option label="学生" value="学生" />
                <el-option label="残疾军人" value="残疾军人" />
              </el-select>
            </el-form-item>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { InfoFilled } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { PassengerInfo } from '@/api/order'

// 定义props
const props = defineProps<{
  modelValue: {
    passengers: PassengerInfo[]
  }
  orderInfo: {
    seatNumbers: string[]
  }
}>()

// 定义emits
const emit = defineEmits<{
  'update:modelValue': [value: { passengers: PassengerInfo[] }]
}>()

// 表单引用
const formRef = ref<FormInstance>()

// 更新值
const updateValue = () => {
  emit('update:modelValue', props.modelValue)
}

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  idType: [
    { required: true, message: '请选择证件类型', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  passengerType: [
    { required: true, message: '请选择乘客类型', trigger: 'change' }
  ]
}

// 动态身份证号验证规则
const getIdCardRules = () => {
  return [
    { required: true, message: '请输入身份证号码', trigger: 'blur' },
    {
      pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
      message: '请输入正确的身份证号码',
      trigger: 'blur'
    }
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
/* 表单容器 */
.passenger-form-container {
  padding: 24px 0;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.form-header h3 {
  margin: 0;
  font-size: 18px;
}

.form-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1890FF;
  font-size: 14px;
}

/* 乘客信息 */
.passenger-form {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
}

.passenger-item {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 24px;
  margin-bottom: 24px;
}

.passenger-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.passenger-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.passenger-header h4 {
  margin: 0;
  font-size: 16px;
  color: #262626;
}

.seat-number {
  background: #e6f7ff;
  color: #1890FF;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
}

.passenger-fields {
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
  .passenger-fields {
    grid-template-columns: 1fr;
  }
  
  .container {
    padding: 0 16px;
  }
}
</style>