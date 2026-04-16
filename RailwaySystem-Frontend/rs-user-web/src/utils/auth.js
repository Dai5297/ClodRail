import axios from 'axios'

const ACCESS_TOKEN_KEY = 'token'
const USER_INFO_KEY = 'userInfo'

let refreshPromise = null

export const getAccessToken = () => localStorage.getItem(ACCESS_TOKEN_KEY)

export const setAccessToken = (token) => {
  if (token) {
    localStorage.setItem(ACCESS_TOKEN_KEY, token)
    return
  }
  localStorage.removeItem(ACCESS_TOKEN_KEY)
}

export const clearAuth = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(USER_INFO_KEY)
}

export const setUserInfo = (userInfo) => {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
}

export const refreshAccessToken = async () => {
  if (refreshPromise) {
    return refreshPromise
  }
  refreshPromise = axios.post(
    '/api/customer/auth/refresh',
    {},
    { withCredentials: true }
  ).then((response) => {
    const payload = response.data
    if (payload?.code !== 200 || !payload?.data?.accessToken) {
      throw new Error(payload?.message || '刷新登录状态失败')
    }
    setAccessToken(payload.data.accessToken)
    return payload.data.accessToken
  }).finally(() => {
    refreshPromise = null
  })
  return refreshPromise
}

export const ensureAccessToken = async () => {
  const token = getAccessToken()
  if (token) {
    return token
  }
  return refreshAccessToken()
}
