import request from '@/utils/request.js'

/**
 * 订单管理相关API
 */

// 获取订单列表
export const getOrderList = (params) => {
  return request({
    url: '/order/list',
    method: 'GET',
    params
  })
}

// 获取订单详情
export const getOrderDetail = (orderId) => {
  return request({
    url: `/order/${orderId}`,
    method: 'GET'
  })
}