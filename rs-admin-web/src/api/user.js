import request from '@/utils/request'

/**
 * 用户管理相关API
 */

// 获取用户列表
export const getUserList = (params) => {
  return request({
    url: '/admin/users/list',
    method: 'GET',
    params
  })
}

// 获取用户详情
export const getUserDetail = (userId) => {
  return request({
    url: `/admin/users/${userId}`,
    method: 'GET'
  })
}

// 禁用/启用用户
export const toggleUserStatus = (userId, status) => {
  return request({
    url: `/admin/users/${userId}/status`,
    method: 'PUT',
    data: { status }
  })
}

// 删除用户
export const deleteUser = (userId) => {
  return request({
    url: `/admin/users/${userId}`,
    method: 'DELETE'
  })
}

