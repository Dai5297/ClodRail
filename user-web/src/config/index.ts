/**
 * 应用配置管理
 */

// 开发环境标识
export const isDev = import.meta.env.DEV
export const isProd = import.meta.env.PROD

// API配置
export const apiConfig = {
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:18080',
  timeout: parseInt(import.meta.env.VITE_REQUEST_TIMEOUT) || 10000,
  useMock: import.meta.env.VITE_USE_MOCK === 'true'
}

// 开发服务器配置
export const serverConfig = {
  port: parseInt(import.meta.env.VITE_DEV_PORT) || 3000
}

// 应用信息
export const appInfo = {
  name: '铁路售票系统',
  version: '1.0.0',
  description: '在线火车票预订系统'
}

// 存储键名
export const storageKeys = {
  token: 'token',
  userInfo: 'userInfo',
  loginFailCount: 'loginFailCount',
  language: 'language',
  theme: 'theme'
}

// 路由配置
export const routeConfig = {
  loginPath: '/login',
  homePath: '/',
  redirectKey: 'redirect'
}

export default {
  isDev,
  isProd,
  apiConfig,
  serverConfig,
  appInfo,
  storageKeys,
  routeConfig
}