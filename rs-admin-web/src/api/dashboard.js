import request from '@/utils/request'

/**
 * 仪表板数据API
 */

// 获取统计数据
export const getStatistics = () => {
  return request({
    url: '/admin/dashboard/statistics',
    method: 'GET'
  })
}

// 获取最近订单
export const getRecentOrders = (params) => {
  return request({
    url: '/admin/dashboard/recent-orders',
    method: 'GET',
    params
  })
}

// 获取销售趋势数据
export const getSalesTrend = (params) => {
  return request({
    url: '/admin/dashboard/sales-trend',
    method: 'GET',
    params
  })
}

