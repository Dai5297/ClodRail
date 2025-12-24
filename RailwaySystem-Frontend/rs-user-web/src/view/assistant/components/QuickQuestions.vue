<template>
  <div class="quick-questions">
    <div class="quick-question-title">
      <el-icon><Lightning /></el-icon>
      常见问题
    </div>
    <div class="quick-question-list">
      <button
        v-for="(question, index) in questions"
        :key="index"
        class="quick-question-btn"
        @click="$emit('select-question', question)"
      >
        {{ question }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { Lightning } from '@element-plus/icons-vue'

defineProps({
  questions: {
    type: Array,
    default: () => [
      '如何查询车次信息？',
      '网上订票流程是什么？',
      '如何退票和改签？',
      '学生票如何购买？',
      '忘记取票密码怎么办？',
      '携带儿童乘车的规定？'
    ]
  }
})

defineEmits(['select-question'])
</script>

<style scoped>
.quick-questions {
  padding: 20px 24px;
  background: #ffffff;
  border-bottom: 1px solid #f0f0f0;
}

.quick-question-title {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.quick-question-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.quick-question-btn {
  padding: 8px 16px;
  background: #f7f7f7;
  color: #1a1a1a;
  border: 1px solid transparent;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.quick-question-btn:hover {
  background: #f0f7ff;
  color: #0066ff;
  border-color: #e6f0ff;
  transform: translateY(-1px);
}
</style>

