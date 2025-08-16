<script setup lang="ts">
import { ref, nextTick, watch, onMounted } from 'vue'
import { ElInput, ElButton, ElIcon } from 'element-plus'
import { Microphone, Picture, Location } from '@element-plus/icons-vue'

// 定义props
interface Message {
  id: number
  type: 'user' | 'ai'
  content: string
  time: string
}

const props = defineProps<{
  messages: Message[]
  currentMessage: string
  isTyping: boolean
}>()

// 定义事件
const emit = defineEmits<{
  sendMessage: [message?: string]
  updateMessage: [message: string]
}>()

// 引用
const chatMessages = ref<HTMLElement>()
const messageInput = ref<HTMLTextAreaElement>()

// 输入建议
const suggestions = [
  '北京到上海的高铁',
  '明天的车票还有吗',
  '如何改签车票',
  '学生票怎么买'
]

// 处理发送消息
const handleSendMessage = () => {
  if (props.currentMessage.trim()) {
    emit('sendMessage')
  }
}

// 处理输入变化
const handleInputChange = (value: string) => {
  emit('updateMessage', value)
}

// 处理建议点击
const handleSuggestionClick = (suggestion: string) => {
  emit('sendMessage', suggestion)
}

// 处理键盘事件
const handleKeydown = (event: Event | KeyboardEvent) => {
  const keyboardEvent = event as KeyboardEvent
  if (keyboardEvent.key === 'Enter' && !keyboardEvent.shiftKey) {
    keyboardEvent.preventDefault()
    handleSendMessage()
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessages.value) {
      chatMessages.value.scrollTop = chatMessages.value.scrollHeight
    }
  })
}

// 监听消息变化，自动滚动到底部
watch(() => props.messages, () => {
  scrollToBottom()
}, { deep: true })

// 组件挂载后滚动到底部
onMounted(() => {
  scrollToBottom()
})
</script>

<template>
  <div class="chat-container">
    <!-- 消息列表 -->
    <div class="chat-messages" ref="chatMessages">
      <div
        v-for="message in messages"
        :key="message.id"
        :class="['message', message.type === 'ai' ? 'ai-message' : 'user-message']"
      >
        <div v-if="message.type === 'ai'" class="message-avatar">
          <div class="ai-avatar-placeholder">🤖</div>
        </div>
        <div class="message-content">
          <div class="message-bubble">
            <p v-for="(line, index) in message.content.split('\n')" :key="index">
              {{ line }}
            </p>
          </div>
          <div class="message-time">{{ message.time }}</div>
        </div>
        <div v-if="message.type === 'user'" class="message-avatar">
          <div class="user-avatar-placeholder">👤</div>
        </div>
      </div>

      <!-- 正在输入指示器 -->
      <div v-if="isTyping" class="message ai-message typing-message">
        <div class="message-avatar">
          <div class="ai-avatar-placeholder">🤖</div>
        </div>
        <div class="message-content">
          <div class="message-bubble typing-bubble">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-container">
      <div class="input-tools">
        <el-button type="text" class="tool-btn" title="语音输入">
          <el-icon><Microphone /></el-icon>
        </el-button>
        <el-button type="text" class="tool-btn" title="图片上传">
          <el-icon><Picture /></el-icon>
        </el-button>
        <el-button type="text" class="tool-btn" title="位置信息">
          <el-icon><Location /></el-icon>
        </el-button>
      </div>

      <div class="input-area">
        <el-input
          ref="messageInput"
          :model-value="currentMessage"
          @input="handleInputChange"
          @keydown="handleKeydown"
          type="textarea"
          :rows="1"
          :autosize="{ minRows: 1, maxRows: 4 }"
          placeholder="请输入您的问题..."
          class="message-input"
        />
        <el-button
          type="primary"
          class="send-btn"
          :disabled="!currentMessage.trim()"
          @click="handleSendMessage"
        >
          ➤
        </el-button>
      </div>

      <div class="input-suggestions">
        <span
          v-for="suggestion in suggestions"
          :key="suggestion"
          class="suggestion-item"
          @click="handleSuggestionClick(suggestion)"
        >
          {{ suggestion }}
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 0; /* 让flex子元素可以正确计算高度 */
}

.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  scroll-behavior: smooth;
}

.message {
  display: flex;
  margin-bottom: 24px;
  align-items: flex-start;
}

.ai-message {
  justify-content: flex-start;
}

.user-message {
  justify-content: flex-end;
}

.message-avatar {
  flex-shrink: 0;
  margin: 0 12px;
}

.ai-avatar-placeholder,
.user-avatar-placeholder {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.ai-avatar-placeholder {
  background: #e6f7ff;
  color: #1890ff;
}

.user-avatar-placeholder {
  background: #f6ffed;
  color: #52c41a;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
}

.user-message .message-content {
  align-items: flex-end;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
  line-height: 1.5;
}

.ai-message .message-bubble {
  background: #f0f0f0;
  color: #262626;
  border-bottom-left-radius: 4px;
}

.user-message .message-bubble {
  background: #1890ff;
  color: white;
  border-bottom-right-radius: 4px;
}

.message-bubble p {
  margin: 0;
}

.message-bubble p:not(:last-child) {
  margin-bottom: 8px;
}

.message-time {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 4px;
  padding: 0 4px;
}

.typing-message {
  opacity: 0.8;
}

.typing-bubble {
  background: #f0f0f0 !important;
  padding: 16px !important;
}

.typing-indicator {
  display: flex;
  gap: 4px;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #8c8c8c;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.chat-input-container {
  border-top: 1px solid #f0f0f0;
  background: white;
}

.input-tools {
  display: flex;
  gap: 8px;
  padding: 12px 16px 0;
}

.tool-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: #8c8c8c;
}

.tool-btn:hover {
  color: #1890ff;
  background: #f0f8ff;
}

.input-area {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  padding: 12px 16px;
}

.message-input {
  flex: 1;
}

.send-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 16px;
  flex-shrink: 0;
}

.input-suggestions {
  display: flex;
  gap: 8px;
  padding: 0 16px 16px;
  flex-wrap: wrap;
}

.suggestion-item {
  padding: 6px 12px;
  background: #f5f5f5;
  border-radius: 16px;
  font-size: 13px;
  color: #595959;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.suggestion-item:hover {
  background: #e6f7ff;
  color: #1890ff;
}

@media (max-width: 768px) {
  .chat-messages {
    padding: 16px;
  }

  .message-content {
    max-width: 85%;
  }

  .input-suggestions {
    padding: 0 12px 12px;
  }

  .suggestion-item {
    font-size: 12px;
    padding: 4px 8px;
  }
}
</style>
