import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data, code, message } = response.data

    // 处理业务层面的状态码
    if (code === 401) {
      ElMessage.error(message || '登录已过期，请重新登录')
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_info')
      localStorage.removeItem('user_routes')
      router.push('/login')
      return Promise.reject(new Error(message || '登录过期'))
    }

    if (code === 403) {
      ElMessage.error(message || '无权限访问')
      router.push('/403')
      return Promise.reject(new Error(message || '无权限'))
    }

    if (code === 404) {
      ElMessage.error(message || '请求的资源不存在')
      router.push('/404')
      return Promise.reject(new Error(message || '资源不存在'))
    }

    // 处理其他业务错误
    if (code !== 200) {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message || '请求失败'))
    }

    // 返回统一格式的响应数据
    return {
      success: code === 200,
      data: data,
      code: code,
      message: message
    }
  },
  (error) => {
    // 处理网络错误和HTTP状态码错误
    console.error('响应错误:', error)

    if (error.response) {
      // 请求已发出，但服务器响应状态码不在 2xx 范围内
      const { status, data } = error.response
      const errorMessage = data?.message || error.message

      switch (status) {
        case 401:
          ElMessage.error(errorMessage || '未授权，请重新登录')
          localStorage.removeItem('admin_token')
          localStorage.removeItem('admin_info')
          localStorage.removeItem('user_routes')
          router.push('/login')
          break
        case 403:
          ElMessage.error(errorMessage || '拒绝访问')
          router.push('/403')
          break
        case 404:
          ElMessage.error(errorMessage || '请求的资源不存在')
          router.push('/404')
          break
        case 500:
          ElMessage.error(errorMessage || '服务器内部错误')
          break
        default:
          ElMessage.error(errorMessage || '请求失败')
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      ElMessage.error('网络错误，请检查您的网络连接')
    } else {
      // 在设置请求时发生了错误
      ElMessage.error('请求配置错误')
    }

    return Promise.reject(error)
  }
)

export default request

