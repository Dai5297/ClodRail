import request from '@/utils/request'

/**
 * 车票搜索
 * @param {Object} params 搜索参数
 * @param {number} params.originStationId 出发站ID
 * @param {number} params.destinationStationId 到达站ID
 * @param {string} params.date 出发日期 YYYY-MM-DD
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页大小
 * @returns {Promise} 车票搜索结果
 */
export const searchTickets = (params) => {
  return request({
    url: '/customer/tickets/search',
    method: 'get',
    params: {
      originStationId: params.originStationId,
      destinationStationId: params.destinationStationId,
      departureDate: params.departureDate,
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 10
    }
  })
}

/**
 * 获取车票详情
 * @param {number} ticketId 车票ID
 * @returns {Promise} 车票详情数据
 */
export const getTicketDetail = (ticketId) => {
  return request({
    url: '/customer/tickets/detail',
    method: 'get',
    params: { ticketId }
  })
}

/**
 * 获取热门路线
 * @returns {Promise} 热门路线列表
 */
export const getHotRoutes = () => {
  return request({
    url: '/customer/tickets/hot',
    method: 'get'
  })
}

/**
 * 获取可用座位信息
 * @param {Object} params 查询参数
 * @param {number} params.ticketId 车票ID（后端参数）
 * @param {number} params.seatType 座位类型
 * @returns {Promise} 可用座位信息
 */
export const getAvailableSeats = (params) => {
  return request({
    url: '/customer/seats/available',
    method: 'get',
    params: {
      ticketId: params.ticketId,
      seatType: params.seatType
    }
  })
}