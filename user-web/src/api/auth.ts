import api from './index'

// 用户认证接口
export interface LoginRequest {
  username: string
  password: string
  captcha?: string
  rememberMe?: boolean
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
  phone: string
  realName: string
  idCard: string
  code: string
}

export interface UserInfo {
  id: number
  username: string
  email: string
  phone: string
  realName: string
  idCard: string
  points: number
  level: string
  status: string
  createdAt: string
  lastLoginAt: string
}

// 用户登录
export const login = (data: LoginRequest) => {
  return api.post('/user/login', data)
}

// 用户注册
export const register = (data: RegisterRequest) => {
  return api.post('/user/register', data)
}

// 获取用户信息
export const getUserInfo = () => {
  return api.get('/user/info')
}

// 更新用户信息
export const updateUserInfo = (data: Partial<UserInfo>) => {
  return api.put('/user/info', data)
}

// 修改密码
export const resetPassword = (data: { oldPassword: string; newPassword: string }) => {
  return api.put('/user/password', data)
}

// 用户登出
export const logout = () => {
  return api.post('/user/logout')
}

// 发送验证码
export const sendCode = (data: { phone: string }) => {
  return api.post('/user/sms', data)
}

// 通过手机号重置密码
export const resetPasswordByPhone = (data: { phone: string; code: string; newPassword: string }) => {
  return api.post('/user/reset-password', data)
}

// 获取验证码
export const getCaptcha = () => {
  return api.get('/user/captcha')
}
