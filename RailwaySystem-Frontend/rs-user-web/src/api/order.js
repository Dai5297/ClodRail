import request from '@/utils/request.js'

/**
 * 订单管理相关API
 */

// 获取订单列表
export const getOrderList = () => {
  return request({
    url: '/customer/orders/list',
    method: 'GET'
  })
}

// 获取订单列表（分页）
export const getOrderPage = (params) => {
  return request({
    url: '/customer/orders/page',
    method: 'GET',
    params: {
      pageNum: params?.pageNum || params?.page || 1,
      pageSize: params?.pageSize || params?.size || 10,
      orderId: params?.orderId,
      status: params?.status
    }
  })
}

// 生成订单Token (UUID)
export const generateOrderToken = (orderId) => {
  return request({
    url: `/common/orders/assistant/generate/${orderId}`,
    method: 'POST'
  })
}

// 解析订单Token
export const parseOrderToken = (uuid) => {
  return request({
    url: '/common/orders/assistant/prase',
    method: 'GET',
    params: { uuid }
  })
}

// 获取订单详情
export const getOrderDetail = (orderId) => {
  return request({
    url: `/customer/orders/detail/${orderId}`,
    method: 'GET'
  })
}

/**
 * 订单详情响应数据结构
 * @returns {Promise} 订单详情数据
 * @example
 * // 返回数据结构
 * {
 *   orderId: 123456,
 *   status: 1, // 订单状态：0-待支付，1-已支付，2-已取消，3-已退款
 *   totalAmount: 150.00,
 *   createTime: "2024-01-15T10:30:00",
 *   payTime: "2024-01-15T10:35:00",
 *   expireTime: "2024-01-15T11:00:00",
 *   
 *   // 车票ID（需要单独调用 getTicketDetail 获取车票详情）
 *   ticketId: 789,
 *   
 *   // 乘客信息列表
 *   passengers: [
 *     {
 *       passengerId: 456,
 *       name: "张三",
 *       idCard: "110101199001011234",
 *       phone: "13800138000",
 *       passengerType: 1, // 乘客类型：1-成人，2-儿童，3-学生
 *       seatPosition: "3车06A",
 *       actualPrice: 120.00
 *     }
 *   ],
 *   
 *   // 价格详情
 *   priceDetail: {
 *     basePrice: 120.00,
 *     serviceFee: 5.00,
 *     insuranceFee: 0.00,
 *     discount: 0.00,
 *     totalPrice: 125.00
 *   },
 *   
 *   // 操作权限
 *   permissions: {
 *     canCancel: true,
 *     canRefund: false,
 *     canModify: false,
 *     canPrint: true
 *   }
 * }
 */

// 创建订单
export const createOrder = (orderData) => {
  return request({
    url: '/customer/orders/create',
    method: 'POST',
    data: orderData
  })
}

// 支付订单 - 支付宝支付
export const payOrder = (orderId) => {
  return request({
    url: '/customer/ticket/pay/alipay',
    method: 'POST',
    params: {
      orderId: orderId
    }
  })
}

// 支付订单（旧接口，保留用于兼容）
export const payOrderOld = (paymentData) => {
  return request({
    url: '/customer/orders/pay',
    method: 'POST',
    data: paymentData
  })
}

// 取消订单
export const cancelOrder = (orderId) => {
  return request({
    url: `/orders/${orderId}/cancel`,
    method: 'POST'
  })
}