import request from '@/utils/request'

/**
 * 获取站点列表
 * @returns {Promise} 站点列表数据
 */
export function getStations() {
    return request({
        url: '/customer/stations',
        method: 'get'
    })
}
