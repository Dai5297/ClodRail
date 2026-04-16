/**
 * WebSocket 管理类
 * 用于人工客服实时通信
 * 超时时间：3分钟
 */

export class WebSocketManager {
  constructor() {
    this.ws = null
    this.sessionId = null
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 3
    this.reconnectDelay = 3000
    this.heartbeatInterval = null
    this.heartbeatTimeout = null
    this.connectionTimeout = 3 * 60 * 1000 // 3分钟
    this.connectionTimer = null
    this.isManualClose = false

    // 事件回调
    this.onMessage = null
    this.onOpen = null
    this.onClose = null
    this.onError = null
    this.onTimeout = null
  }

  /**
   * 连接 WebSocket
   * @param {string} sessionId - 会话ID
   */
  connect(sessionId) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      return
    }

    this.sessionId = sessionId
    this.isManualClose = false

    const token = localStorage.getItem('token')
    const wsUrl = `ws://localhost:18085/ws/assistant/user?sessionId=${sessionId}&token=${token}`

    try {
      this.ws = new WebSocket(wsUrl)

      this.ws.onopen = () => {
        this.reconnectAttempts = 0
        this.startHeartbeat()
        this.startConnectionTimer()

        if (this.onOpen) {
          this.onOpen()
        }
      }


      this.ws.onmessage = (event) => {

        try {
          // 直接解析 JSON 格式
          const data = JSON.parse(event.data)

          // 处理业务消息
          if (this.onMessage) {
            this.onMessage(data)
          }
        } catch (error) {
        }
      }

      this.ws.onerror = (error) => {

        if (this.onError) {
          this.onError(error)
        }
      }

      this.ws.onclose = (event) => {

        this.stopHeartbeat()
        this.stopConnectionTimer()

        if (this.onClose) {
          this.onClose(event)
        }

        // 如果不是手动关闭，尝试重连
        if (!this.isManualClose && this.reconnectAttempts < this.maxReconnectAttempts) {
          this.reconnect()
        }
      }
    } catch (error) {

      if (this.onError) {
        this.onError(error)
      }
    }
  }

  /**
   * 解析 Message(type=xxx, content=xxx, from=xxx, to=xxx) 格式
   * @param {string} messageStr - Message 字符串
   * @returns {Object|null} 解析后的消息对象
   */
  parseMessageString(messageStr) {
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

      return message
    } catch (error) {
      return null
    }
  }

  /**
   * 发送消息
   * @param {Object} data - 消息数据
   */
  send(data) {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      return false
    }

    try {
      this.ws.send(JSON.stringify(data))
      return true
    } catch (error) {
      return false
    }
  }

  /**
   * 关闭连接
   */
  close() {
    this.isManualClose = true
    this.stopHeartbeat()
    this.stopConnectionTimer()

    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  /**
   * 重连
   */
  reconnect() {
    this.reconnectAttempts++

    setTimeout(() => {
      if (this.sessionId) {
        this.connect(this.sessionId)
      }
    }, this.reconnectDelay)
  }

  /**
   * 开始心跳
   * 注意: 浏览器 WebSocket API 不支持发送 PingWebSocketFrame
   * WebSocket 协议会自动处理 Ping/Pong 心跳
   * 这里只保留心跳检测逻辑,不主动发送 ping
   */
  startHeartbeat() {
    // 移除主动发送 ping 的逻辑
    // 浏览器会自动处理 WebSocket 协议层面的 Ping/Pong
    // 如果需要应用层心跳,后端应该主动发送 ping,前端响应
  }

  /**
   * 停止心跳
   */
  stopHeartbeat() {
    if (this.heartbeatInterval) {
      clearInterval(this.heartbeatInterval)
      this.heartbeatInterval = null
    }

    if (this.heartbeatTimeout) {
      clearTimeout(this.heartbeatTimeout)
      this.heartbeatTimeout = null
    }
  }

  /**
   * 重置心跳超时
   */
  resetHeartbeatTimeout() {
    if (this.heartbeatTimeout) {
      clearTimeout(this.heartbeatTimeout)
      this.heartbeatTimeout = null
    }
  }

  /**
   * 开始连接计时器（3分钟）
   */
  startConnectionTimer() {
    this.connectionTimer = setTimeout(() => {

      if (this.onTimeout) {
        this.onTimeout()
      }

      this.close()
    }, this.connectionTimeout)
  }

  /**
   * 停止连接计时器
   */
  stopConnectionTimer() {
    if (this.connectionTimer) {
      clearTimeout(this.connectionTimer)
      this.connectionTimer = null
    }
  }

  /**
   * 获取连接状态
   */
  getReadyState() {
    if (!this.ws) return WebSocket.CLOSED
    return this.ws.readyState
  }

  /**
   * 是否已连接
   */
  isConnected() {
    return this.ws && this.ws.readyState === WebSocket.OPEN
  }
}

export default WebSocketManager
