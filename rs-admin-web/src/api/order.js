import request from '@/utils/request'

// 解析订单Token获取详情
export function parseAssistantOrder(uuid) {
    return request({
        url: '/common/orders/assistant/prase',
        method: 'get',
        params: { uuid }
    })
}
