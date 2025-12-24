import { ElMessage, ElNotification } from 'element-plus'

/**
 * 统一错误处理工具
 */
export class ErrorHandler {
  /**
   * 处理API错误
   * @param {Error} error - 错误对象
   * @param {string} defaultMessage - 默认错误消息
   * @param {boolean} showNotification - 是否显示通知
   */
  static handleApiError(error, defaultMessage = '操作失败', showNotification = false) {
    
    let message = defaultMessage
    let type = 'error'
    
    if (error.response) {
      // 服务器响应错误
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          message = data?.message || '请求参数错误'
          break
        case 401:
          message = '登录已过期，请重新登录'
          // 清除本地存储的token
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          // 跳转到登录页面
          window.location.href = '/login'
          return
        case 403:
          message = '没有权限执行此操作'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 422:
          message = data?.message || '数据验证失败'
          break
        case 429:
          message = '请求过于频繁，请稍后再试'
          break
        case 500:
          message = '服务器内部错误，请稍后再试'
          break
        case 502:
        case 503:
        case 504:
          message = '服务暂时不可用，请稍后再试'
          break
        default:
          message = data?.message || defaultMessage
      }
    } else if (error.request) {
      // 网络错误
      message = '网络连接失败，请检查网络设置'
    } else {
      // 其他错误
      message = error.message || defaultMessage
    }
    
    // 显示错误消息
    if (showNotification) {
      ElNotification({
        title: '错误',
        message,
        type,
        duration: 5000
      })
    } else {
      ElMessage({
        message,
        type,
        duration: 3000
      })
    }
    
    return { message, type, status: error.response?.status }
  }
  
  /**
   * 处理表单验证错误
   * @param {Object} errors - 验证错误对象
   * @param {string} formName - 表单名称
   */
  static handleValidationError(errors, formName = '表单') {
    if (!errors || typeof errors !== 'object') {
      ElMessage.error(`${formName}验证失败`)
      return
    }
    
    // 获取第一个错误字段的错误信息
    const firstError = Object.values(errors)[0]
    const errorMessage = Array.isArray(firstError) ? firstError[0] : firstError
    
    ElMessage.error(errorMessage || `${formName}验证失败`)
  }
  
  /**
   * 处理业务逻辑错误
   * @param {string} message - 错误消息
   * @param {string} type - 消息类型 (error, warning, info)
   * @param {boolean} showNotification - 是否显示通知
   */
  static handleBusinessError(message, type = 'error', showNotification = false) {
    if (showNotification) {
      ElNotification({
        title: type === 'error' ? '错误' : type === 'warning' ? '警告' : '提示',
        message,
        type,
        duration: 5000
      })
    } else {
      ElMessage({
        message,
        type,
        duration: 3000
      })
    }
  }
  
  /**
   * 显示成功消息
   * @param {string} message - 成功消息
   * @param {boolean} showNotification - 是否显示通知
   */
  static showSuccess(message, showNotification = false) {
    if (showNotification) {
      ElNotification({
        title: '成功',
        message,
        type: 'success',
        duration: 3000
      })
    } else {
      ElMessage({
        message,
        type: 'success',
        duration: 2000
      })
    }
  }
  
  /**
   * 显示警告消息
   * @param {string} message - 警告消息
   * @param {boolean} showNotification - 是否显示通知
   */
  static showWarning(message, showNotification = false) {
    if (showNotification) {
      ElNotification({
        title: '警告',
        message,
        type: 'warning',
        duration: 4000
      })
    } else {
      ElMessage({
        message,
        type: 'warning',
        duration: 3000
      })
    }
  }
}

/**
 * 表单验证工具
 */
export class FormValidator {
  /**
   * 验证用户名
   * @param {string} username - 用户名
   * @returns {Object} 验证结果
   */
  static validateUsername(username) {
    if (!username) {
      return { valid: false, message: '请输入用户名' }
    }
    if (username.length < 3 || username.length > 20) {
      return { valid: false, message: '用户名长度在 3 到 20 个字符' }
    }
    if (!/^[a-zA-Z0-9_]+$/.test(username)) {
      return { valid: false, message: '用户名只能包含字母、数字和下划线' }
    }
    return { valid: true }
  }
  
  /**
   * 验证密码强度
   * @param {string} password - 密码
   * @returns {Object} 验证结果
   */
  static validatePassword(password) {
    if (!password) {
      return { valid: false, message: '请输入密码' }
    }
    if (password.length < 6 || password.length > 20) {
      return { valid: false, message: '密码长度在 6 到 20 个字符' }
    }
    
    // 检查密码强度
    let strength = 0
    if (/[a-z]/.test(password)) strength++
    if (/[A-Z]/.test(password)) strength++
    if (/[0-9]/.test(password)) strength++
    if (/[^a-zA-Z0-9]/.test(password)) strength++
    
    if (strength < 2) {
      return { 
        valid: false, 
        message: '密码强度太弱，建议包含字母、数字或特殊字符' 
      }
    }
    
    return { valid: true, strength }
  }
  
  /**
   * 验证手机号
   * @param {string} phone - 手机号
   * @returns {Object} 验证结果
   */
  static validatePhone(phone) {
    if (!phone) {
      return { valid: false, message: '请输入手机号' }
    }
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      return { valid: false, message: '请输入正确的手机号码' }
    }
    return { valid: true }
  }
  
  /**
   * 验证邮箱
   * @param {string} email - 邮箱
   * @returns {Object} 验证结果
   */
  static validateEmail(email) {
    if (!email) {
      return { valid: false, message: '请输入邮箱' }
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      return { valid: false, message: '请输入正确的邮箱地址' }
    }
    return { valid: true }
  }
  
  /**
   * 验证身份证号
   * @param {string} idCard - 身份证号
   * @returns {Object} 验证结果
   */
  static validateIdCard(idCard) {
    if (!idCard) {
      return { valid: false, message: '请输入身份证号' }
    }
    if (!/^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(idCard)) {
      return { valid: false, message: '请输入正确的身份证号码' }
    }
    return { valid: true }
  }
}

export default ErrorHandler