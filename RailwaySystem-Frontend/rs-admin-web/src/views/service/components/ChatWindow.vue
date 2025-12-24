<template>
  <div class="chat-window">
    <template v-if="session">
      <!-- Chat Header -->
      <div class="chat-header">
        <div class="header-info">
          <span class="customer-name">{{ session.name }}</span>
          <span class="customer-status" :class="{ online: session.online }">
            {{ session.online ? $t('service.statusOnline') : $t('service.statusOffline') }}
          </span>
        </div>
      </div>

      <!-- Messages Area -->
      <div class="messages-area" ref="messagesContainer">
        <div v-for="msg in messages" :key="msg.id" class="message-wrapper" :class="{ 'my-message': msg.isSelf, 'system-message': msg.isSystem }">
          <div v-if="msg.isSystem" class="system-message-content">
            <span class="system-message-text">{{ msg.content }}</span>
            <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
          </div>
          <template v-else>
            <!-- User Avatar (Left) -->
            <el-avatar v-if="!msg.isSelf" :size="36" :src="session.avatar" class="message-avatar" />
            
            <div class="message-content">
              <OrderMessageCard
                v-if="msg.type === 'ORDER' && msg.orderDetail"
                :order="msg.orderDetail"
              />
              <div v-else class="message-bubble">
                {{ msg.content }}
              </div>
              <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
            </div>
            
            <!-- Agent Avatar (Right) -->
            <el-avatar v-if="msg.isSelf" :size="36" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="message-avatar" />
          </template>
        </div>
      </div>

      <!-- Input Area -->
      <div class="input-area">
        <!-- Toolbar -->
        <div class="input-toolbar">
          <el-button text @click="toggleEmojiPicker" title="表情" size="small">
            😊
          </el-button>
          <el-button text @click="showUserInfo" :icon="User" title="用户信息" size="small" />
        </div>
        
        <!-- Emoji Picker -->
        <div v-if="showEmojiPicker" class="emoji-picker">
          <div class="emoji-picker-header">
            <span>常用表情</span>
            <el-button text @click="showEmojiPicker = false" :icon="Close" size="small" />
          </div>
          <div class="emoji-grid">
            <button
              v-for="emoji in emojiList"
              :key="emoji"
              class="emoji-item"
              @click="insertEmoji(emoji)"
            >
              {{ emoji }}
            </button>
          </div>
        </div>
        
        <div class="input-wrapper">
          <el-input
            v-model="inputMessage"
            :placeholder="$t('service.inputPlaceholder')"
            @keydown.enter="handleSend"
            class="message-input"
            clearable
          />
          <el-button 
            type="primary" 
            @click="handleSend" 
            :disabled="!inputMessage.trim()"
            class="send-button"
          >
            {{ $t('service.send') }}
          </el-button>
        </div>
      </div>
    </template>
    
    <div v-else class="empty-state">
      <el-icon :size="64" color="#dcdfe6"><ChatDotRound /></el-icon>
      <p>{{ $t('service.selectSession') }}</p>
    </div>
    
    <!-- User Info Dialog -->
    <el-dialog
      v-model="userInfoVisible"
      title="用户信息"
      width="500px"
      :close-on-click-modal="true"
    >
      <div v-if="session" class="user-info-content">
        <div class="user-info-avatar">
          <el-avatar :size="80" :src="session.avatar" />
        </div>
        <div class="user-info-item">
          <label>用户名：</label>
          <span>{{ session.name }}</span>
        </div>
        <div class="user-info-item">
          <label>用户ID：</label>
          <span>{{ session.userId }}</span>
        </div>
        <div class="user-info-item">
          <label>在线状态：</label>
          <el-tag :type="session.online ? 'success' : 'info'" size="small">
            {{ session.online ? '在线' : '离线' }}
          </el-tag>
        </div>
        <div class="user-info-item">
          <label>会话开始时间：</label>
          <span>{{ formatDateTime(session.createTime) }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { ChatDotRound, User, Close } from '@element-plus/icons-vue'
import OrderMessageCard from './OrderMessageCard.vue'
import { parseAssistantOrder } from '@/api/order'

const props = defineProps({
  session: {
    type: Object,
    default: null
  },
  messages: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['send-message'])

const inputMessage = ref('')
const messagesContainer = ref(null)
const showEmojiPicker = ref(false)
const userInfoVisible = ref(false)

// 常用表情列表
const emojiList = [
  '😊', '😂', '😍', '😘', '😜', '😎', '😇', '🤗', '🤔', '😳',
  '😢', '😭', '😡', '😱', '😴', '🤒', '🤕', '👍', '👎', '👏',
  '🙏', '💪', '✌️', '🤝', '👋', '🎉', '🎊', '🎁', '❤️', '💔',
  '💯', '✅', '❌', '⭐', '🔥', '💡', '📝', '📌', '🔔', '⚠️'
]

// 自动滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

watch(() => props.messages, async (newMessages) => {
  scrollToBottom()
  
  // 处理 ORDER 类型的消息
  // content 是 UUID，需要调用 API 获取订单详情
  if (newMessages && newMessages.length > 0) {
    for (const msg of newMessages) {
      if (msg.type === 'ORDER' && !msg.orderDetail && msg.content) {
        try {
          // content 是 UUID 字符串，调用 API 解析
          const res = await parseAssistantOrder(msg.content)
          if (res.code === 200) {
            msg.orderDetail = res.data
          }
        } catch (error) {
          console.error('Failed to parse order:', error)
        }
      }
    }
  }
}, { deep: true })

watch(() => props.session, () => {
  scrollToBottom()
})

const handleSend = () => {
  const content = inputMessage.value.trim()
  if (!content) return
  
  emit('send-message', content)
  inputMessage.value = ''
}

const handleNewLine = (event) => {
  // Shift + Enter 允许换行,不做任何处理
  // 浏览器会自动插入换行符
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  return new Date(timestamp).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false })
}

const formatDateTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit', 
    minute: '2-digit'
  })
}

// 切换表情选择器
const toggleEmojiPicker = () => {
  showEmojiPicker.value = !showEmojiPicker.value
}

// 插入表情
const insertEmoji = (emoji) => {
  inputMessage.value += emoji
  showEmojiPicker.value = false
}

// 显示用户信息
const showUserInfo = () => {
  userInfoVisible.value = true
}
</script>

<style scoped>
.chat-window {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--bg-color);
}

.chat-header {
  padding: 16px 24px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-color);
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.customer-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.customer-status {
  font-size: 12px;
  color: var(--text-color-secondary);
  display: flex;
  align-items: center;
}

.customer-status::before {
  content: '';
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #909399;
  margin-right: 4px;
}

.customer-status.online::before {
  background-color: #67c23a;
}

.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: var(--bg-color-page);
}

.message-wrapper {
  display: flex;
  justify-content: flex-start;
  gap: 12px;
}

.message-wrapper.my-message {
  justify-content: flex-end;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.my-message .message-content {
  align-items: flex-end;
}

.message-bubble {
  padding: 10px 16px;
  border-radius: 8px;
  background: white;
  color: #333;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  border: 1px solid var(--border-color-light);
}

.my-message .message-bubble {
  background: var(--el-color-primary);
  color: white;
  border: none;
}

.message-content :deep(.order-message-card) {
  margin-bottom: 4px;
}

:global(html.dark) .message-bubble {
  background: var(--bg-color-overlay);
  color: var(--text-color);
  border-color: var(--border-color);
}

:global(html.dark) .my-message .message-bubble {
  background: var(--el-color-primary);
  color: white;
}

.message-time {
  font-size: 12px;
  color: var(--text-color-secondary);
  padding: 0 4px;
}

.message-wrapper.system-message {
  justify-content: center;
}

.system-message-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.system-message-text {
  padding: 6px 12px;
  background: var(--bg-color-overlay);
  color: var(--text-color-secondary);
  font-size: 12px;
  border-radius: 12px;
  border: 1px solid var(--border-color-light);
}




.input-area {
  position: relative;
  padding: 12px 16px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-color);
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: center;
}

.message-input {
  flex: 1;
}

.message-input :deep(.el-textarea__inner) {
  padding: 10px 12px;
  font-size: 14px;
  line-height: 1.5;
  border-radius: 8px;
}

.send-button {
  flex-shrink: 0;
}

.input-hint {
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-color-placeholder);
  text-align: right;
}

/* Toolbar Styles */
.input-toolbar {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
  padding: 4px 0;
  border-bottom: 1px solid var(--border-color-light);
}

.input-toolbar .el-button {
  color: var(--text-color);
  font-size: 16px;
  padding: 4px 8px;
  min-height: 28px;
  border-radius: 6px;
  border: 1px solid var(--border-color-light);
  background: var(--bg-color);
  transition: all 0.3s ease;
}

.input-toolbar .el-button:hover {
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-5);
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
}

.input-toolbar .el-button:active {
  transform: translateY(0);
}

/* Emoji Picker Styles */
.emoji-picker {
  position: absolute;
  bottom: 100%;
  left: 16px;
  margin-bottom: 12px;
  width: 380px;
  max-height: 340px;
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  z-index: 100;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.emoji-picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px;
  border-bottom: 1px solid var(--border-color);
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color);
  background: var(--bg-color-overlay);
  border-radius: 12px 12px 0 0;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 6px;
  padding: 16px;
  overflow-y: auto;
  max-height: 280px;
}

.emoji-grid::-webkit-scrollbar {
  width: 6px;
}

.emoji-grid::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 3px;
}

.emoji-grid::-webkit-scrollbar-thumb:hover {
  background: var(--text-color-secondary);
}

.emoji-item {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.emoji-item:hover {
  background: var(--el-color-primary-light-9);
  transform: scale(1.15);
}

.emoji-item:active {
  transform: scale(1.05);
}

/* User Info Dialog Styles */
.user-info-content {
  padding: 24px;
}

.user-info-avatar {
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
  padding: 24px 0;
  border-bottom: 2px solid var(--border-color-light);
  position: relative;
}

.user-info-avatar::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 2px;
  background: var(--el-color-primary);
}

.user-info-item {
  display: flex;
  align-items: center;
  padding: 18px 20px;
  margin-bottom: 12px;
  background: var(--bg-color-overlay);
  border-radius: 10px;
  border: 1px solid var(--border-color-light);
  transition: all 0.3s ease;
}

.user-info-item:hover {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-5);
  transform: translateX(4px);
}

.user-info-item:last-child {
  margin-bottom: 0;
}

.user-info-item label {
  min-width: 130px;
  font-weight: 600;
  color: var(--text-color);
  font-size: 14px;
  display: flex;
  align-items: center;
}

.user-info-item label::before {
  content: '•';
  margin-right: 8px;
  color: var(--el-color-primary);
  font-size: 18px;
}

.user-info-item span {
  color: var(--text-color-secondary);
  font-size: 14px;
  flex: 1;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-color-secondary);
  gap: 16px;
}
</style>
```
