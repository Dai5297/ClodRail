<script setup lang="ts">
import { ref } from 'vue'
import { ElDialog, ElButton, ElInput, ElMessage } from 'element-plus'

// 定义Props
interface Props {
  visible: boolean
}

const props = defineProps<Props>()

// 定义事件
const emit = defineEmits<{
  'update:visible': [value: boolean]
  close: []
}>()

// 评分和反馈
const rating = ref(0)
const feedback = ref('')
const hoveredRating = ref(0)

// 评分标签
const ratingLabels = [
  '',
  '很不满意',
  '不满意', 
  '一般',
  '满意',
  '非常满意'
]

// 处理关闭
const handleClose = () => {
  emit('update:visible', false)
  emit('close')
  // 重置表单
  rating.value = 0
  feedback.value = ''
  hoveredRating.value = 0
}

// 处理星星点击
const handleStarClick = (star: number) => {
  rating.value = star
}

// 处理星星悬停
const handleStarHover = (star: number) => {
  hoveredRating.value = star
}

// 处理星星离开
const handleStarLeave = () => {
  hoveredRating.value = 0
}

// 提交评价
const submitRating = () => {
  if (rating.value === 0) {
    ElMessage.warning('请选择评分')
    return
  }
  
  // 这里可以发送评价到服务器
  const ratingData = {
    rating: rating.value,
    feedback: feedback.value,
    timestamp: new Date().toISOString()
  }
  
  console.log('提交评价:', ratingData)
  
  // 模拟提交成功
  ElMessage.success('感谢您的评价！')
  handleClose()
}

// 获取当前显示的评分（悬停时显示悬停评分，否则显示选中评分）
const displayRating = (star: number) => {
  const currentRating = hoveredRating.value || rating.value
  return star <= currentRating
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    title="服务评价"
    width="450px"
    :before-close="handleClose"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="rating-content">
      <div class="rating-header">
        <h3>请为本次AI助手服务打分</h3>
        <p>您的评价将帮助我们改进服务质量</p>
      </div>
      
      <div class="rating-section">
        <div class="stars-container">
          <div 
            v-for="star in 5" 
            :key="star"
            class="star"
            :class="{ 
              active: displayRating(star),
              hovered: hoveredRating > 0 && star <= hoveredRating
            }"
            @click="handleStarClick(star)"
            @mouseenter="handleStarHover(star)"
            @mouseleave="handleStarLeave"
          >
            ★
          </div>
        </div>
        
        <div class="rating-label">
          {{ ratingLabels[hoveredRating || rating] }}
        </div>
      </div>
      
      <div class="feedback-section">
        <label class="feedback-label">意见建议（可选）</label>
        <el-input
          v-model="feedback"
          type="textarea"
          :rows="4"
          placeholder="请输入您的意见和建议，帮助我们提供更好的服务..."
          maxlength="500"
          show-word-limit
          resize="none"
        />
      </div>
      
      <div class="rating-tips">
        <div class="tip-item">
          <span class="tip-icon">💡</span>
          <span class="tip-text">您的评价将完全匿名</span>
        </div>
        <div class="tip-item">
          <span class="tip-icon">🔒</span>
          <span class="tip-text">我们重视您的隐私保护</span>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          type="primary" 
          @click="submitRating"
          :disabled="rating === 0"
        >
          提交评价
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.rating-content {
  padding: 8px 0;
}

.rating-header {
  text-align: center;
  margin-bottom: 32px;
}

.rating-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}

.rating-header p {
  margin: 0;
  font-size: 14px;
  color: #8c8c8c;
}

.rating-section {
  text-align: center;
  margin-bottom: 32px;
}

.stars-container {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
}

.star {
  font-size: 32px;
  color: #d9d9d9;
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
}

.star:hover {
  transform: scale(1.1);
}

.star.active {
  color: #faad14;
}

.star.hovered {
  color: #ffc53d;
}

.rating-label {
  height: 24px;
  font-size: 16px;
  font-weight: 500;
  color: #595959;
  transition: all 0.3s ease;
}

.feedback-section {
  margin-bottom: 24px;
}

.feedback-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.rating-tips {
  display: flex;
  justify-content: center;
  gap: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 8px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tip-icon {
  font-size: 14px;
}

.tip-text {
  font-size: 12px;
  color: #8c8c8c;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .rating-tips {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
  
  .stars-container {
    gap: 4px;
  }
  
  .star {
    font-size: 28px;
  }
}
</style>