import request from '@/utils/request'

/**
 * 管理员认证相关API
 */

// 管理员登录
export const adminLogin = (data) => {
  return request({
    url: '/admin/auth/login',
    method: 'POST',
    data
  })
}

// 管理员登出
export const adminLogout = () => {
  return request({
    url: '/admin/auth/logout',
    method: 'POST'
  })
}

// 修改密码
export const changePassword = (data) => {
  return request({
    url: '/admin/password/change',
    method: 'POST',
    data
  })
}

// 获取验证码
export const getCaptcha = () => {
  return request({
    url: '/customer/auth/captcha',
    method: 'GET'
  })
}

