<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="bg-white rounded-3xl shadow-sm border border-slate-100 overflow-hidden flex h-[calc(100vh-160px)]">
      <!-- Sidebar -->
      <ChatSidebar
        :sessions="sessions"
        :active-session-id="activeSessionId"
        @new-chat="handleNewChat"
        @select-session="handleSelectSession"
        @delete-session="handleDeleteSession"
        class="flex-shrink-0"
      />

      <!-- Main Chat Area -->
      <div class="flex-1 flex flex-col min-w-0 bg-slate-50 relative">
        <!-- Chat Header -->
        <ChatHeader
          :is-human-service="isHumanService"
          :service-name="serviceName"
          :is-online="isOnline"
          :remaining-time="remainingTime"
          @transfer="handleTransfer"
          @clear="handleClearMessages"
          @end-service="handleEndService"
        />

        <!-- Message List -->
        <MessageList
          :messages="currentMessages"
          :loading="loading"
          class="flex-1"
        />

        <!-- Input Area -->
        <MessageInput
          :disabled="loading || (isHumanService && !isOnline)"
          :sending="loading"
          @send="handleSendMessage"
          @emoji="handleEmoji"
          @image="handleImageUpload"
          @voice="handleVoiceInput"
          @order="handleOrderSelect"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import ChatSidebar from './components/ChatSidebar.vue'
import ChatHeader from './components/ChatHeader.vue'
import MessageList from './components/MessageList.vue'
import MessageInput from './components/MessageInput.vue'
import { 
  getChatSessions, 
  getChatHistory, 
  createChatSession, 
  deleteChatSession, 
  sendMessage,
  getCustomerServiceStatus
} from '@/api/assistant'

// Data
const sessions = ref([])
const activeSessionId = ref(null)
const currentMessages = ref([])
const loading = ref(false)
const isHumanService = ref(false)
const serviceName = ref('')
const isOnline = ref(true)
const remainingTime = ref(0)
const socket = ref(null)

// Methods
const loadSessions = async () => {
  try {
    const res = await getChatSessions()
    if (res.code === 200) {
      sessions.value = res.data || []
      if (sessions.value.length > 0 && !activeSessionId.value) {
        handleSelectSession(sessions.value[0].sessionId)
      }
    }
  } catch (error) {
    console.error('Failed to load sessions:', error)
  }
}

const handleNewChat = async () => {
  try {
    const res = await createChatSession()
    if (res.code === 200) {
      const newSession = {
        sessionId: res.data.sessionId,
        title: '新对话',
        lastMessage: '',
        updateTime: new Date().toISOString(),
        type: 'ai'
      }
      sessions.value.unshift(newSession)
      activeSessionId.value = newSession.sessionId
      currentMessages.value = []
      isHumanService.value = false
    }
  } catch (error) {
    message.error('创建会话失败')
  }
}

const handleSelectSession = async (id) => {
  if (activeSessionId.value === id) return
  
  // Close socket if switching sessions
  if (socket.value) {
    socket.value.close()
    socket.value = null
  }
  isHumanService.value = false
  isOnline.value = false
  
  activeSessionId.value = id
  loading.value = true
  try {
    const res = await getChatHistory(id)
    if (res.code === 200) {
      currentMessages.value = (res.data || []).map(msg => ({
        id: msg.id || Date.now() + Math.random(),
        type: msg.type,
        content: msg.content,
        timestamp: msg.timestamp
      }))
      
      // Update session type if backend returns it
      const session = sessions.value.find(s => s.sessionId === id)
      if (session) {
        // Assuming backend might return session type in history or we infer it
        // For now, default to AI unless we have specific flags
      }
    }
  } catch (error) {
    message.error('加载历史消息失败')
  } finally {
    loading.value = false
  }
}

const handleDeleteSession = async (id) => {
  try {
    const res = await deleteChatSession(id)
    if (res.code === 200) {
      const index = sessions.value.findIndex(s => s.sessionId === id)
      if (index > -1) {
        sessions.value.splice(index, 1)
        if (activeSessionId.value === id) {
          activeSessionId.value = null
          currentMessages.value = []
          if (sessions.value.length > 0) {
            handleSelectSession(sessions.value[0].sessionId)
          }
        }
        message.success('会话已删除')
      }
    }
  } catch (error) {
    message.error('删除会话失败')
  }
}

const handleSendMessage = async (text) => {
  if (!activeSessionId.value) {
    await handleNewChat()
  }

  // Add user message immediately
  currentMessages.value.push({
    id: Date.now(),
    type: 'user',
    content: text,
    timestamp: Date.now()
  })

  // Update session preview
  const session = sessions.value.find(s => s.sessionId === activeSessionId.value)
  if (session) {
    session.lastMessage = text
    session.updateTime = new Date().toISOString()
    if (session.title === '新对话') {
      session.title = text.length > 10 ? text.substring(0, 10) + '...' : text
    }
  }

  loading.value = true
  
  try {
    if (isHumanService.value) {
      if (socket.value && socket.value.readyState === WebSocket.OPEN) {
        const msgObj = {
          type: 'USER',
          content: text,
          sessionId: activeSessionId.value
        }
        socket.value.send(JSON.stringify(msgObj))
      } else {
        message.error('连接已断开，无法发送')
      }
      loading.value = false
    } else {
      // AI Chat Stream
      const stream = await sendMessage({
        memoryId: activeSessionId.value,
        message: text
      })
      
      const reader = stream.getReader()
      const decoder = new TextDecoder()
      
      // Create a placeholder for AI response
      const aiMessage = {
        id: Date.now() + 1,
        type: 'ai',
        content: '',
        timestamp: Date.now(),
        streaming: true
      }
      currentMessages.value.push(aiMessage)
      
      loading.value = false // Stop loading spinner, show streaming
      
      while (true) {
        const { done, value } = await reader.read()
        if (done) break
        
        const chunk = decoder.decode(value)
        aiMessage.content += chunk
        
        // Update session preview with AI response
        if (session) {
          session.lastMessage = aiMessage.content
        }
      }
      
      aiMessage.streaming = false
    }
  } catch (error) {
    console.error(error)
    message.error('发送消息失败')
    loading.value = false
  }
}

const connectWebSocket = () => {
  const token = localStorage.getItem('token')
  const sessionId = activeSessionId.value
  if (!token || !sessionId) return

  // Close existing socket if any
  if (socket.value) {
    socket.value.close()
  }

  const wsUrl = `ws://localhost:18085/ws/assistant/user?token=${token}&sessionId=${sessionId}`
  socket.value = new WebSocket(wsUrl)

  socket.value.onopen = () => {
    isHumanService.value = true
    isOnline.value = true
    message.success('已连接到人工客服')
    currentMessages.value.push({
        id: Date.now(),
        type: 'system',
        content: '已连接到人工客服',
        timestamp: Date.now()
    })
  }

  socket.value.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      // msg structure: { content, type, from, to }
      if (msg.type === 'SYSTEM') {
         currentMessages.value.push({
            id: Date.now(),
            type: 'system',
            content: msg.content,
            timestamp: Date.now()
         })
      } else if (msg.type === 'ASSISTANT_TYPE' || msg.type === 'agent') {
         currentMessages.value.push({
            id: Date.now(),
            type: 'ai',
            content: msg.content,
            timestamp: Date.now()
         })
      }
    } catch (e) {
      console.error('WS parse error', e)
    }
  }

  socket.value.onclose = () => {
    isOnline.value = false
    message.info('人工客服连接已断开')
    isHumanService.value = false
  }

  socket.value.onerror = (e) => {
    console.error('WS Error', e)
    isOnline.value = false
  }
}

const handleTransfer = async () => {
  if (!activeSessionId.value) return
  
  try {
    const res = await getCustomerServiceStatus()
    if (res.code === 200) {
      if (res.data > 0) {
        connectWebSocket()
      } else {
        message.warning('当前暂无人工客服在线')
      }
    }
  } catch (error) {
    message.error('获取客服状态失败')
  }
}

const handleClearMessages = () => {
  // Usually we don't clear history on backend unless we delete session
  // For frontend only:
  currentMessages.value = []
}

const handleEndService = () => {
  isHumanService.value = false
  currentMessages.value.push({
    id: Date.now(),
    type: 'system',
    content: '人工服务已结束',
    timestamp: Date.now()
  })
}

const handleEmoji = () => message.info('功能开发中')
const handleImageUpload = () => message.info('功能开发中')
const handleVoiceInput = () => message.info('功能开发中')
const handleOrderSelect = () => message.info('功能开发中')

onMounted(() => {
  loadSessions()
})
</script>
