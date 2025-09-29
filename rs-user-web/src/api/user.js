import request from '@/utils/request.js'

/**
 * 用户信息管理相关API
 */

// 获取用户信息
export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'GET'
  })
}

// 更新用户信息
export const updateUserInfo = (data) => {
  return request({
    url: '/user/info/update',
    method: 'POST',
    data
  })
}

// 更换手机号 - 确认更换

