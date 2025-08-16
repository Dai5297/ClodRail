<template>
  <el-dialog
    v-model="dialogVisible"
    title="用户协议"
    width="600px"
    center
  >
    <div class="agreement-content">
      <h3>1. 服务条款</h3>
      <p>欢迎使用铁路订票系统。在使用我们的服务前，请仔细阅读本用户协议。</p>
      
      <h3>2. 账户注册</h3>
      <p>用户需要提供真实、准确的个人信息进行注册，并对所提供信息的真实性负责。</p>
      
      <h3>3. 服务使用</h3>
      <p>用户应当合法、合理地使用本系统提供的服务，不得进行任何违法违规行为。</p>
      
      <h3>4. 隐私保护</h3>
      <p>我们承诺保护用户的个人隐私信息，不会未经授权向第三方泄露用户信息。</p>
      
      <h3>5. 免责声明</h3>
      <p>在法律允许的范围内，本系统对因使用服务而产生的任何直接或间接损失不承担责任。</p>
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
/* 协议内容 */
.agreement-content {
  max-height: 400px;
  overflow-y: auto;
  line-height: 1.6;
}

.agreement-content h3 {
  color: #262626;
  margin: 16px 0 8px 0;
  font-size: 16px;
}

.agreement-content p {
  color: #595959;
  margin: 0 0 12px 0;
  font-size: 14px;
}
</style>