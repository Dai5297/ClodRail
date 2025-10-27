<template>
  <el-dialog
    :model-value="visible"
    :title="isEdit ? '编辑联系人' : '添加联系人'"
    width="600px"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      label-position="left"
    >
      <el-form-item label="姓名" prop="name">
        <el-input
          v-model="formData.name"
          placeholder="请输入姓名"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>
      
      <el-form-item label="手机号码" prop="phone">
        <el-input
          v-model="formData.phone"
          placeholder="请输入手机号码"
          maxlength="11"
        />
      </el-form-item>
      
      <el-form-item label="身份证号" prop="idCard">
        <el-input
          v-model="formData.idCard"
          placeholder="请输入身份证号"
          maxlength="18"
        />
      </el-form-item>
      
      <el-form-item label="邮箱地址" prop="email">
        <el-input
          v-model="formData.email"
          placeholder="请输入邮箱地址（选填）"
          maxlength="100"
        />
      </el-form-item>
      
      <el-form-item label="乘客类型" prop="passengerType">
        <el-select
          v-model="formData.passengerType"
          placeholder="请选择乘客类型"
          style="width: 100%"
        >
          <el-option label="成人" :value="1" />
          <el-option label="儿童" :value="2" />
          <el-option label="学生" :value="3" />
          <el-option label="老人" :value="4" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="设为默认">
        <el-switch
          v-model="formData.isDefault"
          :active-value="1"
          :inactive-value="0"
          active-text="是"
          inactive-text="否"
        />
      </el-form-item>
      
      <el-form-item label="状态">
        <el-switch
          v-model="formData.status"
          :active-value="1"
          :inactive-value="0"
          active-text="启用"
          inactive-text="禁用"
        />
      </el-form-item>
      
      <el-form-item label="备注信息" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息（选填）"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          type="primary" 
          :loading="submitting"
          @click="handleSubmit"
        >
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'

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

const formRef = ref(null)

const formRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  passengerType: [
    { required: true, message: '请选择乘客类型', trigger: 'change' }
  ],
  remark: [
    { max: 200, message: '备注不能超过200个字符', trigger: 'blur' }
  ]
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      emit('submit')
    }
  })
}

const handleClose = () => {
  emit('close')
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>