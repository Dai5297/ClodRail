import request from "@/utils/request.js";

// 获取管理员信息
export const getAdminInfo = () => {
    return request({
        url: '/admin/profile/info',
        method: 'GET'
    })
}

export const uploadAvatar = (data) => {
    return request({
        url: '/admin/profile/avatar',
        method: 'PUT',
        data
    })
}

// 更新个人信息
export const updateProfile = (data) => {
    return request({
        url: '/admin/profile/info',
        method: 'PUT',
        data
    })
}
