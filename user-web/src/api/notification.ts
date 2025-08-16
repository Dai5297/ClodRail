import api from './index'

// 通知接口
export interface NotificationMessage {
  id: number
  type: 'order' | 'payment' | 'system' | 'promotion'
  title: string
  content: string
  isRead: boolean
  orderId?: number
  createdAt: string
}

export interface NotificationSettings {
  orderUpdates: boolean
  paymentNotifications: boolean
  promotions: boolean
  systemMessages: boolean
  emailNotifications: boolean
  smsNotifications: boolean
}

// 获取通知列表
export const getNotifications = (params: { page?: number; size?: number; type?: string; isRead?: boolean }) => {
  return api.get('/notifications', { params })
}

// 获取通知详情
export const getNotificationDetail = (notificationId: number): Promise<NotificationMessage> => {
  return api.get(`/notifications/${notificationId}`)
}

// 标记通知为已读
export const markAsRead = (notificationId: number) => {
  return api.put(`/notifications/${notificationId}/read`)
}

// 批量标记为已读
export const markAllAsRead = () => {
  return api.put('/notifications/read-all')
}

// 删除通知
export const deleteNotification = (notificationId: number) => {
  return api.delete(`/notifications/${notificationId}`)
}

// 获取通知设置
export const getNotificationSettings = (): Promise<NotificationSettings> => {
  return api.get('/notifications/settings')
}

// 更新通知设置
export const updateNotificationSettings = (settings: NotificationSettings) => {
  return api.put('/notifications/settings', settings)
}

// 获取未读通知数量
export const getUnreadCount = (): Promise<{ count: number }> => {
  return api.get('/notifications/unread-count')
}