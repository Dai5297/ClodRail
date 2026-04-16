import axios from "axios";
import router from "@/router/index.js";
import { ElMessage } from 'element-plus'
import { clearAuth, getAccessToken, refreshAccessToken } from '@/utils/auth.js'

const request = axios.create({
  baseURL: '/api',
  timeout: 5000,
  withCredentials: true
})

request.interceptors.request.use((config) => {
    const token = getAccessToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

request.interceptors.response.use((response) => {
    const { data, code, message } = response.data
    
    // 处理认证失败
    if (code === 401) {
      return Promise.reject(Object.assign(new Error(message || '登录过期'), {
        response,
        config: response.config
      }))
    }
    
    // 返回统一格式的响应数据
    return {
      success: code === 200,
      data: data,
      code: code,
      message: message
    }
}, async (error) => {
    const originalRequest = error.config
    const responseCode = error.response?.data?.code
    const responseStatus = error.response?.status

    if ((responseCode === 401 || responseStatus === 401)
      && originalRequest
      && !originalRequest.__isRetryRequest
      && !originalRequest.url?.includes('/customer/auth/refresh')
      && !originalRequest.url?.includes('/customer/auth/login/username')) {
      try {
        originalRequest.__isRetryRequest = true
        const token = await refreshAccessToken()
        originalRequest.headers = originalRequest.headers || {}
        originalRequest.headers.Authorization = `Bearer ${token}`
        return request(originalRequest)
      } catch (refreshError) {
        clearAuth()
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
        return Promise.reject(refreshError)
      }
    }

    if (responseCode === 401 || responseStatus === 401) {
      clearAuth()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    }

    return Promise.reject(error)
})

export default request
