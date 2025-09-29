import request from '@/utils/request'

/**
 * 获取站点列表
 * @returns {Promise} 站点列表数据
 */
export const getStations = () => {
  return request({
    url: '/stations',
    method: 'get'
  })
}

/**
 * 根据关键词搜索站点
 * @param {string} keyword 搜索关键词
 * @returns {Promise} 匹配的站点列表
 */
export const searchStations = (keyword) => {
  return request({
    url: '/stations/search',
    method: 'get',
    params: {
      keyword
    }
  })
}

/**
 * 获取热门站点
 * @returns {Promise} 热门站点列表
 */
export const getHotStations = () => {
  return request({
    url: '/stations/hot',
    method: 'get'
  })
}