import api from './index'

// 支付相关接口
export interface CreatePaymentRequest {
  orderId: number
  paymentMethod: 'alipay' | 'wechat' | 'bank_card' | 'points'
  amount: number
  pointsUsed?: number
}

export interface PaymentInfo {
  id: number
  orderId: number
  paymentNo: string
  method: string
  amount: number
  pointsUsed: number
  actualAmount: number
  status: 'pending' | 'success' | 'failed' | 'cancelled'
  paymentUrl?: string
  qrCode?: string
  createdAt: string
  paidAt?: string
}

// 创建支付
export const createPayment = (data: CreatePaymentRequest): Promise<PaymentInfo> => {
  return api.post('/payment/create', data)
}

// 查询支付状态
export const getPaymentStatus = (paymentId: number): Promise<PaymentInfo> => {
  return api.get(`/payment/${paymentId}/status`)
}

// 取消支付
export const cancelPayment = (paymentId: number) => {
  return api.put(`/payment/${paymentId}/cancel`)
}