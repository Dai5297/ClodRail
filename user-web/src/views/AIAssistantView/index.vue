<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElButton, ElInput } from 'element-plus'
import AIHeader from './components/AIHeader.vue'
import QuickActions from './components/QuickActions.vue'
import ChatContainer from './components/ChatContainer.vue'
import SidebarInfo from './components/SidebarInfo.vue'
import SettingsModal from './components/SettingsModal.vue'
import RatingModal from './components/RatingModal.vue'

// 页面状态
const loading = ref(false)
const showSettingsModal = ref(false)
const showRatingModal = ref(false)

// 定义Message接口
interface Message {
  id: number
  type: 'user' | 'ai'
  content: string
  time: string
}

// 聊天相关状态
const messages = ref<Message[]>([
  {
    id: 1,
    type: 'ai' as const,
    content: '您好！我是小铁助手，很高兴为您服务！🚄\n\n我可以帮您：\n• 查询车次信息和票价\n• 制定出行计划和路线\n• 提供购票指导和建议\n• 解答出行相关问题\n\n请问有什么可以帮助您的吗？',
    time: '刚刚'
  }
])

const currentMessage = ref('')
const isTyping = ref(false)

// 快捷功能处理
const handleQuickAction = (action: string) => {
  const actionMessages: Record<string, string> = {
    'search-train': '我想查询车次信息',
    'ticket-info': '我需要购票咨询',
    'route-plan': '请帮我规划路线',
    'weather-info': '查询天气信息',
    'station-info': '我想了解车站信息',
    'travel-guide': '需要出行指南'
  }
  
  if (actionMessages[action]) {
    sendMessage(actionMessages[action])
  }
}

// 发送消息
const sendMessage = async (message?: string) => {
  const messageText = message || currentMessage.value.trim()
  if (!messageText) return

  // 添加用户消息
  messages.value.push({
    id: Date.now(),
    type: 'user' as const,
    content: messageText,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  })

  currentMessage.value = ''
  isTyping.value = true

  // 模拟AI回复
  setTimeout(() => {
    const aiResponse = generateAIResponse(messageText)
    messages.value.push({
      id: Date.now() + 1,
      type: 'ai' as const,
      content: aiResponse,
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })
    isTyping.value = false
  }, 1500)
}

// 生成AI回复（模拟）
const generateAIResponse = (userMessage: string): string => {
  const responses: Record<string, string> = {
    '车次': '我可以帮您查询车次信息。请告诉我您的出发地和目的地，以及出行日期。',
    '购票': '关于购票，我建议您：\n1. 提前30天购买可享受更多选择\n2. 关注12306官方渠道\n3. 学生、军人等可享受优惠票价\n\n还有什么具体问题吗？',
    '路线': '我来为您规划最佳出行路线。请提供：\n• 出发城市\n• 目的地城市\n• 出行时间\n• 是否有中转偏好',
    '天气': '我可以为您查询目的地天气情况，帮助您准备合适的行装。请告诉我您要查询哪个城市的天气？',
    '车站': '我可以提供车站的详细信息，包括：\n• 车站位置和交通\n• 候车室分布\n• 周边设施\n• 进站流程\n\n请问您需要了解哪个车站？',
    '指南': '出行指南包括：\n• 购票流程\n• 进站须知\n• 行李规定\n• 退改签政策\n\n您想了解哪方面的内容？'
  }

  for (const [key, response] of Object.entries(responses)) {
    if (userMessage.includes(key)) {
      return response
    }
  }

  return '感谢您的咨询！我正在学习中，如果没能完全理解您的问题，请您换个方式描述，或者联系人工客服获得更专业的帮助。'
}

// 清空对话
const clearChat = () => {
  messages.value = [
    {
      id: 1,
      type: 'ai',
      content: '对话已清空。有什么可以帮助您的吗？',
      time: '刚刚'
    }
  ]
  ElMessage.success('对话已清空')
}

// 模态框控制
const openSettings = () => {
  showSettingsModal.value = true
}

const openRating = () => {
  showRatingModal.value = true
}

// 页面初始化
onMounted(() => {
  console.log('AIAssistantView initialized')
})
</script>

<template>
  <div class="ai-assistant-page">
    <div class="ai-container">
      <!-- AI助手头部 -->
      <AIHeader 
        @clear-chat="clearChat"
        @open-settings="openSettings"
      />

      <!-- 快捷功能区 -->
      <QuickActions @quickAction="handleQuickAction" />

      <!-- 对话区域 -->
      <ChatContainer 
        :messages="messages"
        :current-message="currentMessage"
        :is-typing="isTyping"
        @send-message="sendMessage"
        @update-message="currentMessage = $event"
      />
    </div>

    <!-- 侧边栏信息 -->
    <SidebarInfo @open-rating="openRating" />

    <!-- 设置模态框 -->
    <SettingsModal 
      :visible="showSettingsModal"
      @close="showSettingsModal = false"
      @update:visible="showSettingsModal = $event"
    />

    <!-- 评价模态框 -->
    <RatingModal 
      :visible="showRatingModal"
      @close="showRatingModal = false"
      @update:visible="showRatingModal = $event"
    />
  </div>
</template>

<style scoped>
.ai-assistant-page {
  min-height: calc(100vh - 64px);
  display: flex;
  gap: 24px;
  padding: 24px;
  background: #f5f5f5;
}

.ai-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

@media (max-width: 1024px) {
  .ai-assistant-page {
    flex-direction: column;
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .ai-assistant-page {
    padding: 12px;
    gap: 16px;
  }
}
</style>
