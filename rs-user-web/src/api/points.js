import request from '@/utils/request.js'

/**
 * 积分管理相关API
 */

// 获取积分信息
export const getPointsInfo = () => {
  return request({
    url: '/points/info',
    method: 'GET'
  })
}

// 获取积分明细
export const getPointsHistory = (params) => {
  return request({
    url: '/points/history',
    method: 'GET',
    params
  })
}