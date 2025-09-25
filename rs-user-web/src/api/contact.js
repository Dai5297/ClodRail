import request from '@/utils/request.js'

/**
 * 常用联系人管理相关API
 */

// 获取联系人列表
export const getContactList = (params = {}) => {
  return request({
    url: '/contact/page',
    method: 'GET',
    params
  })
}

// 根据ID获取联系人详情
export const getContactById = (contactId) => {
  return request({
    url: `/contact/${contactId}`,
    method: 'GET'
  })
}

// 添加联系人
export const addContact = (data) => {
  return request({
    url: '/contact/add',
    method: 'POST',
    data
  })
}

// 更新联系人
export const updateContact = (data) => {
  return request({
    url: `/contact/update`,
    method: 'PUT',
    data
  })
}

// 删除联系人
export const deleteContact = (contactId) => {
  return request({
    url: `/contact/${contactId}`,
    method: 'DELETE'
  })
}