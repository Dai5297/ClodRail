import request from '@/utils/request.js'

/**
 * 积分管理相关API
 */

// 获取积分信息
export const getPointsInfo = () => {
  return request({
    url: '/customer/mall/points/info',
    method: 'GET'
  })
}

// 获取积分明细
export const getPointsHistory = (params) => {
  return request({
    url: '/customer/mall/points/history',
    method: 'GET',
    params
  })
}

/**
 * 积分商城相关API
 */

// 获取商品列表
export const getProductList = (params) => {
  return request({
    url: '/customer/mall/items',
    method: 'GET',
    params
  })
}

// 获取商品分类列表
export const getCategoryList = () => {
  return request({
    url: '/customer/mall/items/categories',
    method: 'GET'
  })
}

// 搜索商品
export const searchProducts = (params) => {
  return request({
    url: '/customer/mall/items/search',
    method: 'GET',
    params
  })
}

// 获取商品详情
export const getProductDetail = (itemId) => {
  return request({
    url: `/customer/mall/items/${itemId}`,
    method: 'GET'
  })
}

// 兑换商品
export const exchangeProduct = (exchangeData) => {
  return request({
    url: '/customer/mall/exchange',
    method: 'POST',
    data: exchangeData
  })
}

// 获取商城订单列表
export const getMallOrders = (params) => {
  return request({
    url: '/customer/mall/orders',
    method: 'GET',
    params
  })
}

// 获取搜索建议
export const getSearchSuggestions = (keyword) => {
  return request({
    url: '/customer/mall/search/suggest',
    method: 'GET',
    params: { keyword, limit: 10 }
  })
}

// 获取热门搜索词
export const getHotKeywords = () => {
  return request({
    url: '/customer/mall/search/hot-keywords',
    method: 'GET'
  })
}

// 搜索埋点
export const trackSearch = (searchData) => {
  return request({
    url: '/customer/mall/search/track',
    method: 'POST',
    data: searchData
  })
}