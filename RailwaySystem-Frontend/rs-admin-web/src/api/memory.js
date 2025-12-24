import request from '../utils/request'

/**
 * B端会话历史管理 API
 */

// 获取所有会话列表
export function getMemoryList() {
    return request({
        url: '/admin/memory/list',
        method: 'get'
    })
}

// 获取会话详情
export function getMemoryDetail(sessionId) {
    return request({
        url: `/admin/memory/detail/${sessionId}`,
        method: 'get'
    })
}
