import api from './index'

// 车次查询接口
export interface TrainSearchRequest {
  departure: string
  arrival: string
  date: string
  trainType?: string
  page?: number
  size?: number
}

export interface SeatInfo {
  type: string
  price: number
  available: number
  status: 'available' | 'limited' | 'sold-out'
}

export interface TrainInfo {
  id: number
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
  duration: string
  seats: SeatInfo[]
  minPrice: number
  hasTicket: boolean
}

export interface TrainSearchResponse {
  trains: TrainInfo[]
  total: number
  page: number
  size: number
}

export interface TrainDetail {
  id: number
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
  duration: string
  stops: Array<{
    station: string
    arrivalTime: string
    departureTime: string
    stayTime: string
  }>
  seats: SeatInfo[]
  facilities: string[]
}

// 车次搜索
export const searchTrains = (params: TrainSearchRequest): Promise<TrainSearchResponse> => {
  return api.get('/train/search', { params })
}

// 获取车次详情
export const getTrainDetail = (trainId: number): Promise<TrainDetail> => {
  return api.get(`/train/${trainId}`)
}

// 获取座位图
export const getSeatMap = (trainId: number, carriage: number) => {
  return api.get(`/train/${trainId}/seat-map/${carriage}`)
}

// 获取热门线路
export const getPopularRoutes = () => {
  return api.get('/train/popular-routes')
}