import request from '@/utils/request.js'

/**
 * 用户认证模块API
 * 严格按照API文档实现，只包含文档中明确定义的接口
 */

// 用户登录
export const login = (data) => {
  return request({
    url: '/account/login/username',
    method: 'POST',
    data
  })
}

// 用户登出
export const logout = () => {
  return request({
    url: '/account/logout',
    method: 'POST'
  })
}

// 获取验证码
export const getCaptcha = () => {
  return request({
    url: '/account/captcha',
    method: 'GET'
  })
}

// 获取手机验证码
export const getPhoneCode = (data) => {
  return request({
    url: '/account/captcha/phone',
    method: 'GET',
    params: data
  })
}

// 用户注册
export const register = (data) => {
  return request({
    url: '/account/register',
    method: 'POST',
    data
  })
}

// 重置密码
export const resetPassword = (data) => {
  return request({
    url: '/account/password/reset',
    method: 'POST',
    data
  })
}