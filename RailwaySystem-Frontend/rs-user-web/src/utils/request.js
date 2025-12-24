import axios from "axios";
import router from "@/router/index.js";
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

request.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

request.interceptors.response.use((response) => {
    const { data, code, message } = response.data
    
    // 处理认证失败
    if (code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
      return Promise.reject(new Error('登录过期'))
    }
    
    // 返回统一格式的响应数据
    return {
      success: code === 200,
      data: data,
      code: code,
      message: message
    }
}, (error) => {
    // 处理网络错误
    return Promise.reject(error)
})

export default request
