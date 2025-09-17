import axios from 'axios'
import router from "@/router";

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 5000000
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 —— ✅ 修复重点在这里
api.interceptors.response.use(
  (response) => {
    // 如果后端返回 { data, code, message } 结构，直接返回 data
    if (response.data.code === 401) {
      router.push('/login')
    }
    return response.data
  },
  (error) => {
    console.log(error.response.data.code)
    if (error.response.data.code === 401) {
      router.push('/login')
    }
    return Promise.reject(error) // 依然抛出错误，让调用方可以 catch
  }
)

export default api
