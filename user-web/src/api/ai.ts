import api from './index'

// AI助手接口
export interface ChatMessage {
  id: number
  role: 'user' | 'assistant'
  content: string
  timestamp: string
}

export interface ChatRequest {
  message: string
  context?: string
}

export interface AIRecommendation {
  type: 'route' | 'time' | 'seat' | 'service'
  title: string
  description: string
  data: any
  confidence: number
}

// AI对话
export const chatWithAI = (data: ChatRequest): Promise<{ reply: string; recommendations?: AIRecommendation[] }> => {
  return api.post('/ai/chat', data)
}

// 获取智能推荐
export const getAIRecommendations = (params: { departure?: string; arrival?: string; date?: string }) => {
  return api.get('/ai/recommendations', { params })
}

// 获取聊天历史
export const getChatHistory = (params: { page?: number; size?: number }) => {
  return api.get('/ai/chat-history', { params })
}

// 清除聊天历史
export const clearChatHistory = () => {
  return api.delete('/ai/chat-history')
}