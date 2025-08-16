<template>
  <el-dialog
    v-model="dialogVisible"
    title="隐私政策"
    width="600px"
    center
  >
    <div class="privacy-content">
      <h3>1. 信息收集</h3>
      <p>我们会收集您在注册和使用服务过程中提供的个人信息，包括但不限于姓名、手机号、身份证号等。</p>
      
      <h3>2. 信息使用</h3>
      <p>我们使用收集的信息为您提供更好的服务，包括身份验证、订单处理、客户服务等。</p>
      
      <h3>3. 信息保护</h3>
      <p>我们采用行业标准的安全措施保护您的个人信息，防止未经授权的访问、使用或泄露。</p>
      
      <h3>4. 信息共享</h3>
      <p>除法律法规要求外，我们不会向第三方共享您的个人信息。</p>
      
      <h3>5. 权利行使</h3>
      <p>您有权查询、更正、删除您的个人信息，如有需要请联系我们的客服。</p>
    </div>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="closeDialog">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

// 定义props
const props = defineProps<{
  visible: boolean
}>()

// 定义事件
const emit = defineEmits(['update:visible'])

// 状态
const dialogVisible = ref(false)

// 监听props变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
})

// 监听dialogVisible变化
watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
})

// 关闭对话框
const closeDialog = () => {
  dialogVisible.value = false
}
</script>

<style scoped>
/* 隐私政策内容 */
.privacy-content {
  max-height: 400px;
  overflow-y: auto;
  line-height: 1.6;
}

.privacy-content h3 {
  color: #262626;
  margin: 16px 0 8px 0;
  font-size: 16px;
}

.privacy-content p {
  color: #595959;
  margin: 0 0 12px 0;
  font-size: 14px;
}
</style>