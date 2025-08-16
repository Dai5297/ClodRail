import api from './index'

// 积分商城接口
export interface PointsProduct {
  id: number
  name: string
  description: string
  image: string
  pointsRequired: number
  originalPrice: number
  stock: number
  category: string
  status: 'active' | 'inactive' | 'sold_out'
  createdAt: string
}

export interface PointsExchangeRequest {
  productId: number
  quantity: number
  address?: {
    recipient: string
    phone: string
    address: string
  }
}

export interface PointsRecord {
  id: number
  type: 'earn' | 'spend' | 'refund'
  points: number
  description: string
  orderId?: number
  createdAt: string
}

export interface PointsExchangeRecord {
  id: number
  productId: number
  productName: string
  pointsUsed: number
  quantity: number
  status: 'pending' | 'processing' | 'shipped' | 'delivered' | 'cancelled'
  address?: {
    recipient: string
    phone: string
    address: string
  }
  createdAt: string
  deliveredAt?: string
}

// 获取积分商品列表
export const getPointsProducts = (params: { category?: string; page?: number; size?: number }) => {
  return api.get('/points/products', { params })
}

// 获取积分商品详情
export const getPointsProductDetail = (productId: number): Promise<PointsProduct> => {
  return api.get(`/points/products/${productId}`)
}

// 积分兑换
export const exchangePoints = (data: PointsExchangeRequest) => {
  return api.post('/points/exchange', data)
}

// 获取积分记录
export const getPointsRecords = (params: { page?: number; size?: number; type?: string }) => {
  return api.get('/points/records', { params })
}

// 获取兑换记录
export const getExchangeRecords = (params: { page?: number; size?: number }) => {
  return api.get('/points/exchange-records', { params })
}

// 获取用户积分
export const getUserPoints = () => {
  return api.get('/points/balance')
}