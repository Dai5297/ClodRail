// utils/util.js

/**
 * 格式化时间
 */
const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return `${[year, month, day].map(formatNumber).join('/')} ${[hour, minute, second].map(formatNumber).join(':')}` 
}

/**
 * 格式化日期
 */
const formatDate = (date, format = 'YYYY-MM-DD') => {
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  const second = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
    .replace('ss', second)
}

/**
 * 格式化数字
 */
const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : `0${n}`
}

/**
 * 显示提示信息
 */
const showToast = (title, icon = 'none', duration = 2000) => {
  wx.showToast({
    title,
    icon,
    duration
  })
}

/**
 * 显示加载中
 */
const showLoading = (title = '加载中...') => {
  wx.showLoading({
    title,
    mask: true
  })
}

/**
 * 隐藏加载
 */
const hideLoading = () => {
  wx.hideLoading()
}

/**
 * 显示确认对话框
 */
const showModal = (title, content) => {
  return new Promise((resolve, reject) => {
    wx.showModal({
      title,
      content,
      success: (res) => {
        if (res.confirm) {
          resolve(true)
        } else {
          resolve(false)
        }
      },
      fail: reject
    })
  })
}

/**
 * 网络请求封装
 */
const request = (options) => {
  return new Promise((resolve, reject) => {
    const app = getApp()
    
    wx.request({
      url: app.globalData.baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': wx.getStorageSync('token') || '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else {
          reject(res)
        }
      },
      fail: reject
    })
  })
}

/**
 * 获取用户信息
 */
const getUserInfo = () => {
  return wx.getStorageSync('userInfo') || null
}

/**
 * 设置用户信息
 */
const setUserInfo = (userInfo) => {
  wx.setStorageSync('userInfo', userInfo)
}

/**
 * 清除用户信息
 */
const clearUserInfo = () => {
  wx.removeStorageSync('userInfo')
  wx.removeStorageSync('token')
}

/**
 * 检查登录状态
 */
const checkLogin = () => {
  const userInfo = getUserInfo()
  const token = wx.getStorageSync('token')
  return !!(userInfo && token)
}

/**
 * 跳转到登录页面
 */
const navigateToLogin = () => {
  wx.navigateTo({
    url: '/pages/login/login'
  })
}

/**
 * 防抖函数
 */
const debounce = (func, wait) => {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

/**
 * 节流函数
 */
const throttle = (func, limit) => {
  let inThrottle
  return function() {
    const args = arguments
    const context = this
    if (!inThrottle) {
      func.apply(context, args)
      inThrottle = true
      setTimeout(() => inThrottle = false, limit)
    }
  }
}

/**
 * 深拷贝
 */
const deepClone = (obj) => {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime())
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (typeof obj === 'object') {
    const clonedObj = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
}

/**
 * 获取今天日期
 */
const getToday = () => {
  return formatDate(new Date(), 'YYYY-MM-DD')
}

/**
 * 获取明天日期
 */
const getTomorrow = () => {
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  return formatDate(tomorrow, 'YYYY-MM-DD')
}

/**
 * 计算两个日期之间的天数
 */
const getDaysBetween = (date1, date2) => {
  const oneDay = 24 * 60 * 60 * 1000
  const firstDate = new Date(date1)
  const secondDate = new Date(date2)
  return Math.round(Math.abs((firstDate - secondDate) / oneDay))
}

/**
 * 验证手机号
 */
const validatePhone = (phone) => {
  const phoneReg = /^1[3-9]\d{9}$/
  return phoneReg.test(phone)
}

/**
 * 验证身份证号
 */
const validateIdCard = (idCard) => {
  const idCardReg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  return idCardReg.test(idCard)
}

/**
 * 模拟数据生成
 */
const mockData = {
  // 热门城市
  hotCities: [
    { name: '北京', code: 'BJS' },
    { name: '上海', code: 'SHH' },
    { name: '广州', code: 'GZQ' },
    { name: '深圳', code: 'SZQ' },
    { name: '杭州', code: 'HZH' },
    { name: '南京', code: 'NJH' },
    { name: '武汉', code: 'WHN' },
    { name: '成都', code: 'CDW' }
  ],
  
  // 车次类型
  trainTypes: [
    { name: 'G-高速', code: 'G' },
    { name: 'D-动车', code: 'D' },
    { name: 'C-城际', code: 'C' },
    { name: 'Z-直达', code: 'Z' },
    { name: 'T-特快', code: 'T' },
    { name: 'K-快速', code: 'K' }
  ],
  
  // 座位类型
  seatTypes: [
    { name: '商务座', code: 'SW', price: 1748 },
    { name: '一等座', code: 'YD', price: 933 },
    { name: '二等座', code: 'ED', price: 553 },
    { name: '硬卧', code: 'YW', price: 408 },
    { name: '软卧', code: 'RW', price: 633 },
    { name: '硬座', code: 'YZ', price: 243 }
  ]
}

module.exports = {
  formatTime,
  formatDate,
  formatNumber,
  showToast,
  showLoading,
  hideLoading,
  showModal,
  request,
  getUserInfo,
  setUserInfo,
  clearUserInfo,
  checkLogin,
  navigateToLogin,
  debounce,
  throttle,
  deepClone,
  getToday,
  getTomorrow,
  getDaysBetween,
  validatePhone,
  validateIdCard,
  mockData
}