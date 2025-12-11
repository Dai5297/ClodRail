<template>
  <div class="service-page">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">{{ $t('service.title') }}</h1>
        <span class="page-description">{{ $t('service.description') }}</span>
      </div>
      <div class="header-right">
        <el-switch
          v-model="isOnline"
          :active-text="$t('service.statusOnline')"
          :inactive-text="$t('service.statusOffline')"
          inline-prompt
          style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
          @change="handleStatusChange"
        />
      </div>
    </div>

    <el-card class="chat-container" :body-style="{ padding: '0px', height: '100%', width: '100%', display: 'flex' }">
      <div class="sidebar">
        <SessionList
          :sessions="sessions"
          :current-session-id="currentSessionId"
          @select-session="handleSelectSession"
        />
      </div>
      <div class="main-area">
        <ChatWindow
          :session="currentSession"
          :messages="currentMessages"
          @send-message="handleSendMessage"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import SessionList from './components/SessionList.vue'
import ChatWindow from './components/ChatWindow.vue'
import { getMemoryList, getMemoryDetail } from '@/api/memory'

const { t } = useI18n()

const isOnline = ref(false)
const currentSessionId = ref(null)
const sessions = ref([])
const messagesMap = ref({})
let ws = null

onMounted(async () => {
  // 加载历史会话
  await loadMemorySessions()
})


onUnmounted(() => {
  disconnectWebSocket()
})

const currentSession = computed(() => {
  return sessions.value.find(s => s.id === currentSessionId.value)
})

const currentMessages = computed(() => {
  return messagesMap.value[currentSessionId.value] || []
})

// 加载历史会话
const loadMemorySessions = async () => {
  try {
    const res = await getMemoryList()
    if (res.code === 200 && res.data) {
      // 将历史会话转换为 sessions 格式
      sessions.value = res.data.map(memory => ({
        id: memory.userId,
        sessionId: memory.sessionId,
        name: memory.userName || `用户${memory.userId}`,
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        lastMessage: memory.lastContent || '',
        lastMessageTime: Date.now(),
        unread: 0,
        online: false
      }))
    }
  } catch (error) {
    console.error('加载历史会话失败:', error)
  }
}

// 解析 Message(type=xxx, content=xxx, from=xxx, to=xxx) 格式
const parseMessageString = (messageStr) => {
  try {
    // 移除 "Message(" 和最后的 ")"
    const content = messageStr.substring(8, messageStr.length - 1)
    
    // 解析各个字段
    const message = {
      type: null,
      content: null,
      from: null,
      to: null
    }
    
    // 使用正则表达式提取字段
    // type
    const typeMatch = content.match(/type=([^,]+?)(?:,|$)/)
    if (typeMatch) {
      message.type = typeMatch[1].trim()
    }
    
    // from
    const fromMatch = content.match(/from=([^,]+?)(?:,|$)/)
    if (fromMatch) {
      const fromValue = fromMatch[1].trim()
      message.from = fromValue === 'null' ? null : fromValue
    }
    
    // to
    const toMatch = content.match(/to=([^,)]+?)(?:,|$|\))/)
    if (toMatch) {
      const toValue = toMatch[1].trim()
      message.to = toValue === 'null' ? null : toValue
    }
    
    // content - 最复杂，因为可能包含逗号和特殊字符
    const contentMatch = content.match(/content=(.+?)(?:,\s*from=|$)/)
    if (contentMatch) {
      message.content = contentMatch[1].trim()
    }
    
    console.log('解析结果:', message)
    return message
  } catch (error) {
    console.error('parseMessageString 失败:', error)
    return null
  }
}


// WebSocket 连接
const connectWebSocket = () => {
  const token = localStorage.getItem('admin_token')
  if (!token) {
    ElMessage.error('未找到登录凭证')
    isOnline.value = false
    return
  }

  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const wsUrl = `${protocol}//${window.location.host}/ws/assistant/agent?token=${token}`

  try {
    ws = new WebSocket(wsUrl)

    ws.onopen = () => {
      console.log('WebSocket连接已建立')
      ElMessage.success(t('service.onlineSuccess'))
    }

    ws.onmessage = (event) => {
      try {
        console.log('WebSocket原始消息:', event.data)
        
        // 直接解析 JSON 格式
        const message = JSON.parse(event.data)
        console.log('解析后的消息对象:', message)
        
        // 忽略 PING 消息
        if (message.type === 'PING' || message.content === 'PING') {
          return
        }
        
        if (message) {
          handleIncomingMessage(message)
        }
      } catch (error) {
        console.error('解析消息失败:', error)
        console.error('原始消息:', event.data)
      }
    }

    ws.onerror = (error) => {
      console.error('WebSocket错误:', error)
      ElMessage.error('连接失败')
      isOnline.value = false
    }

    ws.onclose = () => {
      console.log('WebSocket连接已关闭')
      if (isOnline.value) {
        ElMessage.warning('连接已断开')
        isOnline.value = false
      }
    }
  } catch (error) {
    console.error('创建WebSocket失败:', error)
    ElMessage.error('连接失败')
    isOnline.value = false
  }
}

// 断开 WebSocket
const disconnectWebSocket = () => {
  if (ws && ws.readyState === WebSocket.OPEN) {
    try {
      // 发送关闭帧通知服务器
      const closeFrame = {
        type: 'CLOSE',
        content: 'Agent offline',
        from: null,
        to: null
      }
      ws.send(JSON.stringify(closeFrame))
      console.log('已发送 CloseWebSocketFrame:', closeFrame)
      
      // 将所有用户设置为离线状态
      sessions.value.forEach(session => {
        session.online = false
      })
      console.log('已将所有用户设置为离线状态')
      
      // 等待一小段时间确保消息发送完成,然后关闭连接
      setTimeout(() => {
        if (ws) {
          ws.close()
          ws = null
        }
      }, 100)
    } catch (error) {
      console.error('发送关闭帧失败:', error)
      // 即使发送失败也要关闭连接
      ws.close()
      ws = null
      
      // 仍然将所有用户设置为离线
      sessions.value.forEach(session => {
        session.online = false
      })
    }
  } else if (ws) {
    // 如果连接不是 OPEN 状态,直接关闭
    ws.close()
    ws = null
    
    // 将所有用户设置为离线
    sessions.value.forEach(session => {
      session.online = false
    })
  }
}

// 处理收到的消息
const handleIncomingMessage = (message) => {
  console.log('收到消息:', message)
  
  // 处理用户下线消息
  if (message.type === 'CLOSE') {
    console.log('检测到用户下线消息')
    const userId = message.from
    
    if (userId) {
      // 查找对应的会话
      const session = sessions.value.find(s => s.id.toString() === userId)
      if (session) {
        // 设置用户为离线状态
        session.online = false
        console.log(`用户 ${session.name} 已离线`)
        
        // 添加系统消息到聊天记录
        if (!messagesMap.value[session.id]) {
          messagesMap.value[session.id] = []
        }
        messagesMap.value[session.id].push({
          id: Date.now(),
          content: '用户已离线',
          timestamp: Date.now(),
          isSystem: true
        })
      }
    }
    return
  }
  
  // 处理系统消息 - 用户接入 (支持 system、SYSTEM 和 SYSTEM_TYPE 三种类型)
  if (message.type === 'system' || message.type === 'SYSTEM' || message.type === 'SYSTEM_TYPE') {
    console.log('检测到系统消息，开始处理用户接入')
    handleUserConnectMessage(message)
    return
  }
  
  // 处理普通用户消息和ORDER消息
  const userId = message.from
  
  if (!userId) {
    console.warn('消息缺少 from 字段，无法处理:', message)
    return
  }
  
  // 确定实际内容和消息类型
  let actualContent = message.content
  let messageType = message.type
  
  // 查找或创建会话
  let session = sessions.value.find(s => s.id.toString() === userId)
  if (!session) {
    // 创建新会话
    session = {
      id: parseInt(userId),
      name: `用户${userId}`,
      avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      lastMessage: messageType === 'ORDER' ? '[订单消息]' : actualContent,
      lastMessageTime: Date.now(),
      unread: 1,
      online: true
    }
    sessions.value.unshift(session)
    messagesMap.value[session.id] = []
  } else {
    // 更新现有会话
    session.lastMessage = messageType === 'ORDER' ? '[订单消息]' : actualContent
    session.lastMessageTime = Date.now()
    if (currentSessionId.value !== session.id) {
      session.unread = (session.unread || 0) + 1
    }
    
    // 移到顶部
    const index = sessions.value.indexOf(session)
    sessions.value.splice(index, 1)
    sessions.value.unshift(session)
  }
  
  // 添加消息到聊天记录
  if (!messagesMap.value[session.id]) {
    messagesMap.value[session.id] = []
  }
  messagesMap.value[session.id].push({
    id: Date.now(),
    content: actualContent,
    type: messageType, // 保留消息类型(ORDER或message等)
    timestamp: Date.now(),
    isSelf: false
  })
}

// 处理用户连接系统消息
const handleUserConnectMessage = (message) => {
  try {
    console.log('开始解析用户连接消息:', message.content)
    
    // 解析消息内容：用户{user json}已接入客服{assistant}
    const content = message.content
    const userMatch = content.match(/用户({.+?})已接入客服/)
    
    if (userMatch && userMatch[1]) {
      const userJson = userMatch[1]
      console.log('提取到的用户JSON:', userJson)
      
      const user = JSON.parse(userJson)
      console.log('解析后的用户对象:', user)
      
      const userId = user.id || user.userId
      
      if (!userId) {
        console.error('用户对象中没有找到 id 字段')
        ElMessage.error('无法识别用户ID')
        return
      }
      
      // 检查会话是否已存在
      let session = sessions.value.find(s => s.id.toString() === userId.toString())
      
      if (!session) {
        // 创建新会话
        session = {
          id: parseInt(userId),
          name: user.username || user.realName || user.nickname || `用户${userId}`,
          avatar: user.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
          lastMessage: '用户已接入',
          lastMessageTime: Date.now(),
          unread: 0,
          online: true
        }
        sessions.value.unshift(session)
        messagesMap.value[session.id] = []
        
        console.log('创建新会话:', session)
        
        // 显示通知
        ElMessage.success(`新用户 ${session.name} 已接入`)
      } else {
        console.log('会话已存在:', session)
      }
      
      // 添加系统消息到聊天记录
      if (!messagesMap.value[session.id]) {
        messagesMap.value[session.id] = []
      }
      messagesMap.value[session.id].push({
        id: Date.now(),
        content: `用户已接入客服`,
        timestamp: Date.now(),
        isSystem: true
      })
      
      console.log('用户连接处理完成，当前会话列表:', sessions.value)
    } else {
      console.error('无法从消息中提取用户JSON，正则匹配失败')
      console.error('消息内容:', content)
      // 降级处理：显示原始系统消息
      ElMessage.info(message.content)
    }
  } catch (error) {
    console.error('解析用户连接消息失败:', error)
    console.error('错误堆栈:', error.stack)
    // 降级处理：显示原始系统消息
    ElMessage.warning(`系统消息: ${message.content}`)
  }
}

const handleStatusChange = (val) => {
  if (val) {
    connectWebSocket()
  } else {
    disconnectWebSocket()
    ElMessage.warning(t('service.offlineSuccess'))
  }
}

const handleSelectSession = async (session) => {
  currentSessionId.value = session.id
  // 清除未读消息
  const s = sessions.value.find(s => s.id === session.id)
  if (s) {
    s.unread = 0
  }
  
  // 如果该会话没有消息记录，或者是历史会话（刚加载时），则请求详情
  if (!messagesMap.value[session.id] || messagesMap.value[session.id].length === 0) {
    try {
      const res = await getMemoryDetail(session.sessionId)
      if (res.code === 200 && res.data && res.data.content) {
        // 解析历史消息
        const contentArray = JSON.parse(res.data.content)
        const messages = []
        
        contentArray.forEach((msg, index) => {
          // 过滤掉 SYSTEM 类型的消息
          if (msg.type === 'SYSTEM') return
          
          let content = ''
          if (msg.type === 'USER' && Array.isArray(msg.contents)) {
             // 处理 C 端发来的复杂 USER 消息结构
             const textMsg = msg.contents.find(c => c.type === 'TEXT')
             content = textMsg ? textMsg.text : ''
          } else {
             // 处理简单的文本消息 (如 ASSISTANT_TYPE 或简单的 USER 消息)
             content = msg.content || msg.text || ''
          }
          
          if (content) {
            messages.push({
              id: Date.now() + index,
              content: content,
              type: msg.type === 'USER' ? 'USER' : 'ASSISTANT_TYPE',
              timestamp: Date.now(), // 历史消息暂时没有时间戳，使用当前时间或需要后端提供
              isSelf: msg.type !== 'USER' // 非 USER 类型即为客服自己发的
            })
          }
        })
        
        messagesMap.value[session.id] = messages
      }
    } catch (error) {
      console.error('获取会话详情失败:', error)
    }
  }
}

const handleSendMessage = (content) => {
  if (!currentSessionId.value) {
    ElMessage.warning('请先选择一个会话')
    return
  }
  
  if (!content || !content.trim()) {
    return
  }
  
  // 通过 WebSocket 发送消息
  if (ws && ws.readyState === WebSocket.OPEN) {
    try {
      // 获取客服 ID
      const adminInfo = JSON.parse(localStorage.getItem('admin_info') || '{}')
      const adminId = adminInfo.id || adminInfo.userId || '1'
      
      // 构造 Message 对象
      const message = {
        type: 'ASSISTANT_TYPE',
        content: content,
        from: adminId.toString(),
        to: currentSessionId.value.toString(),
        sessionId: currentSessionId.value.toString()
      }
      
      // 发送 JSON 格式消息
      const messageJson = JSON.stringify(message)
      ws.send(messageJson)
      console.log('WebSocket 发送消息:', message)
    } catch (error) {
      console.error('发送消息失败:', error)
      ElMessage.error('发送消息失败')
      return
    }
  } else {
    console.error('WebSocket 未连接')
    ElMessage.error('WebSocket 未连接，请确保已上线')
    return
  }
  
  // 添加消息到当前会话（本地显示）
  if (!messagesMap.value[currentSessionId.value]) {
    messagesMap.value[currentSessionId.value] = []
  }
  
  messagesMap.value[currentSessionId.value].push({
    id: Date.now(),
    content: content,
    timestamp: Date.now(),
    isSelf: true
  })
  
  // 更新会话列表显示的最后一条消息
  const session = sessions.value.find(s => s.id === currentSessionId.value)
  if (session) {
    session.lastMessage = content
    session.lastMessageTime = Date.now()
    // 将该会话移到顶部
    const index = sessions.value.indexOf(session)
    sessions.value.splice(index, 1)
    sessions.value.unshift(session)
  }
}
</script>

<style scoped>
.service-page {
  height: calc(100vh - 112px); /* 100vh - 64px(navbar) - 48px(layout padding) */
  display: flex;
  flex-direction: column;
  padding: 0; /* Remove padding as layout already provides it, or keep it inside? */
  box-sizing: border-box;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color);
  margin: 0;
}

.page-description {
  color: var(--text-color-secondary);
  font-size: 13px;
  margin: 0;
}

.chat-container {
  flex: 1;
  overflow: hidden;
  border: none;
}

.sidebar {
  width: 300px;
  height: 100%;
  flex-shrink: 0;
}

.main-area {
  flex: 1;
  height: 100%;
  min-width: 0;
}
</style>

