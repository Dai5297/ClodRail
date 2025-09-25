import api from './index'

/**
 * 验证码响应接口
 */
export interface CaptchaResponse {
  /** 验证码图片的Base64编码 */
  captchaImage: string
  /** 验证码标识符 */
  captchaId?: string
}

/**
 * 短信验证码响应接口
 */
export interface SmsResponse {
  /** 响应消息 */
  message: string
  /** 是否发送成功 */
  success: boolean
}

/**
 * 用户信息接口
 * 与后端UserInfoResDTO保持一致
 */
export interface UserInfo {
  /** 用户ID */
  id?: number
  /** 用户名 */
  username: string
  /** 真实姓名 */
  realName?: string
  /** 身份证号 */
  idCard?: string
  /** 手机号 */
  phone?: string
  /** 邮箱 */
  email?: string
  /** 性别 */
  gender?: string
  /** 生日 - 后端返回LocalDateTime格式 */
  birthday?: Date | string
  /** 头像URL - 后端使用icon字段 */
  icon?: string
  /** 地址 */
  address?: string
  /** 个人简介 */
  introduction?: string
  /** 创建时间 */
  createTime?: Date | string
  /** 最后登录时间 */
  lastLoginTime?: Date | string
}

export interface UserInfoReq {
  id?: number,
  username?: string,
  birthday?: string,
  address?: string,
  introduction?: string,
  icon?: string,
  email?: string,
  phone?: string,
  realName?: string,
  gender?: string,
  idCard?: string,
}

/**
 * 登录请求接口
 */
export interface LoginRequest {
  /** 用户名 */
  username: string
  /** 密码 */
  password: string
  /** 验证码（可选） */
  captcha?: string
  /** 记住我 */
  rememberMe?: boolean
}

/**
 * 注册请求接口
 */
export interface RegisterRequest {
  username: string
  password: string
  email: string
  phone: string
  realName: string
  idCard: string
  code: string
}

/**
 * 登录响应接口
 * 与后端UserLoginResDTO保持一致
 */
export interface LoginResponse {
  /** 用户ID */
  id?: number
  /** 用户名 */
  username: string
  /** 性别 */
  gender?: string
  /** 生日 */
  birthday?: string
  /** 地址 */
  address?: string
  /** 个人简介 */
  introduction?: string
  /** 头像URL */
  icon?: string
  /** 邮箱 */
  email?: string
  /** 手机号 */
  phone?: string
  /** JWT令牌 */
  token: string
}

// 用户认证接口

/**
 * 用户登录
 * @param data 登录请求数据
 * @returns 登录响应
 */
export const login = (data: LoginRequest): Promise<LoginResponse> => {
  return api.post('/user/login/username', data)
}

/**
 * 用户注册
 * @param data 注册请求数据
 * @returns Promise<UserInfo> 注册成功返回用户信息
 */
export const register = (data: RegisterRequest): Promise<UserInfo> => {
  return api.post('/user/register', data)
}

/**
 * 获取当前用户信息
 * @returns 用户信息
 */
export const getUserInfo = (): Promise<UserInfo> => {
  return api.get('/user/info')
}

/**
 * 更新用户信息
 * @param data 用户信息更新数据
 * @returns 更新后的用户信息
 */
export const updateUserInfo = (data: Partial<UserInfoReq>): Promise<UserInfo> => {
  return api.post('/user/info/update', data)
}

/**
 * 发送短信验证码
 * @param data 包含手机号的请求数据
 * @returns 短信发送响应
 */
export const sendCode = (data: { phone: string }): Promise<SmsResponse> => {
  return api.post('/auth/send-code', data)
}

/**
 * 通过手机号重置密码
 * @param data 重置密码请求数据
 * @returns 无返回值
 */
export const resetPasswordByPhone = (data: {
  phone: string;
  code: string;
  newPassword: string
}): Promise<void> => {
  return api.post('/auth/reset-password-by-phone', data)
}

/**
 * 获取图形验证码
 * @returns 验证码响应 - 后端直接返回字符串
 */
export const getCaptcha = (): Promise<string> => {
  return api.get('/user/captcha')
}

/**
 * 重置密码
 * @param data 重置密码请求数据
 * @returns 无返回值
 */
export const resetPassword = (data: {
  oldPassword: string;
  newPassword: string
}): Promise<void> => {
  return api.post('/user/password/reset', data)
}

/**
 * 用户退出登录
 * @returns 无返回值
 */
export const logout = (): Promise<void> => {
  return api.post('/user/logout')
}
