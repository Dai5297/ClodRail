import request from '@/utils/request.js'

/**
 * AI助手相关API
 */

// AI智能问答（流式响应）
export async function sendMessage({ memoryId, message }) {
  try {
    if (memoryId) {
      localStorage.setItem('chatMemoryId', memoryId)
    }
    const response = await fetch('/api/customer/assistant/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({ memoryId, message }),
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || `HTTP error! status: ${response.status}`)
    }

    if (!response.body) {
      throw new Error('ReadableStream not supported in this browser')
    }

    return response.body
  } catch (error) {
    throw error
  }
}


// 获取会话历史列表
export const getChatSessions = () => {
  return request({
    url: '/customer/assistant/sessions',
    method: 'GET'
  })
}

// 获取会话消息历史
export const getChatHistory = (sessionId) => {
  return request({
    url: `/customer/assistant/history/${sessionId}`,
    method: 'GET'
  })
}

// 创建新会话
export const createChatSession = () => {
  return request({
    url: '/customer/assistant/session',
    method: 'POST'
  })
}

// 删除会话
export const deleteChatSession = (sessionId) => {
  return request({
    url: `/customer/assistant/session/${sessionId}`,
    method: 'DELETE'
  })
}

// 获取客服状态
export const getCustomerServiceStatus = () => {
  return request({
    url: '/customer/assistant/service/status',
    method: 'GET'
  })
}

// 申请转接人工客服
export const requestTransfer = (sessionId, question) => {
  return request({
    url: '/customer/assistant/service/transfer',
    method: 'POST',
    data: {
      sessionId,
      question
    }
  })
}

