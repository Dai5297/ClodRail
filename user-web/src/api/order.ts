import api from './index'

// 订单相关接口
export interface PassengerInfo {
  name: string
  idCard: string
  phone: string
  passengerType: 'adult' | 'child' | 'student'
}

export interface CreateOrderRequest {
  trainId: number
  date: string
  passengers: PassengerInfo[]
  seats: Array<{
    carriageNo: number
    seatNo: string
    seatType: string
    price: number
  }>
}

export interface OrderInfo {
  id: number
  orderNo: string
  status: 'pending' | 'paid' | 'cancelled' | 'completed' | 'refunded'
  trainInfo: {
    number: string
    type: string
    departure: {
      station: string
      time: string
    }
    arrival: {
      station: string
      time: string
    }
    date: string
  }
  passengers: Array<{
    name: string
    idCard: string
    phone: string
    passengerType: string
    seat: {
      carriageNo: number
      seatNo: string
      seatType: string
      price: number
    }
  }>
  totalAmount: number
  paymentInfo?: {
    method: string
    transactionId: string
    paidAt: string
  }
  createdAt: string
  updatedAt: string
}

export interface OrderListResponse {
  orders: OrderInfo[]
  total: number
  page: number
  size: number
}

// 创建订单
export const createOrder = (data: CreateOrderRequest): Promise<{ orderId: number; orderNo: string }> => {
  return api.post('/order/create', data)
}

// 获取订单列表
export const getOrderList = (params: { page?: number; size?: number; status?: string }) => {
  return api.get('/order/list', { params })
}

// 获取订单详情
export const getOrderDetail = (orderId: number): Promise<OrderInfo> => {
  return api.get(`/order/${orderId}`)
}

// 取消订单
export const cancelOrder = (orderId: number) => {
  return api.put(`/order/${orderId}/cancel`)
}

// 申请退票
export const refundOrder = (orderId: number, reason: string) => {
  return api.put(`/order/${orderId}/refund`, { reason })
}