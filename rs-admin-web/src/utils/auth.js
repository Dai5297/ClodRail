/**
 * 权限管理工具
 */

// 获取管理员信息
export function getAdminInfo() {
  const adminInfoStr = localStorage.getItem('admin_info')
  try {
    if (adminInfoStr === 'undefined') {
      localStorage.removeItem('admin_info')
      return null
    }
    return adminInfoStr ? JSON.parse(adminInfoStr) : null
  } catch (e) {
    console.error('Failed to parse admin_info:', e)
    // If parsing fails, clear the corrupted data
    localStorage.removeItem('admin_info')
    return null
  }
}

// 设置管理员信息
export function setAdminInfo(adminInfo) {
  localStorage.setItem('admin_info', JSON.stringify(adminInfo))
}

// 获取Token
export function getToken() {
  return localStorage.getItem('admin_token')
}

// 设置Token
export function setToken(token) {
  localStorage.setItem('admin_token', token)
}

// 清除登录信息
export function clearAuth() {
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_info')
  localStorage.removeItem('user_routes')
}

// 检查是否已登录
export function isLoggedIn() {
  return !!getToken()
}

// 设置用户路由权限
export function setUserRoutes(routes) {
  localStorage.setItem('user_routes', JSON.stringify(routes))
}

// 获取用户路由权限
export function getUserRoutes() {
  const routesStr = localStorage.getItem('user_routes')
  try {
    return routesStr ? JSON.parse(routesStr) : []
  } catch (e) {
    console.error('Failed to parse user_routes:', e)
    return []
  }
}

// 检查用户是否有权限访问指定路由
export function hasRoutePermission(path) {
  const userRoutes = getUserRoutes()

  // 如果没有路由权限列表,默认允许访问(向后兼容)
  if (!userRoutes || userRoutes.length === 0) {
    return true
  }

  // 检查精确匹配
  if (userRoutes.includes(path)) {
    return true
  }

  // 检查父路由权限(例如: /users/customers 需要检查 /users)
  const pathSegments = path.split('/').filter(Boolean)
  for (let i = pathSegments.length - 1; i > 0; i--) {
    const parentPath = '/' + pathSegments.slice(0, i).join('/')
    if (userRoutes.includes(parentPath)) {
      return true
    }
  }

  return false
}

// 权限角色定义
export const ROLES = {
  SUPER_ADMIN: 'SUPER_ADMIN',  // 超级管理员
  OPS: 'OPS',                   // 运营管理员
  FINANCE: 'FINANCE',           // 财务管理员
  CS: 'CS'                      // 客服管理员
}

// 检查是否有指定权限
export function hasRole(role) {
  const adminInfo = getAdminInfo()
  if (!adminInfo || !adminInfo.role) {
    return false
  }

  // 超级管理员拥有所有权限
  if (adminInfo.role === ROLES.SUPER_ADMIN) {
    return true
  }

  // 检查是否包含指定角色
  if (Array.isArray(role)) {
    return role.includes(adminInfo.role)
  }

  return adminInfo.role === role
}

// 检查是否有访问某个路由的权限
export function canAccessRoute(routeRoles) {
  if (!routeRoles || routeRoles.length === 0) {
    return true
  }

  const adminInfo = getAdminInfo()
  if (!adminInfo || !adminInfo.role) {
    return false
  }

  // 超级管理员拥有所有权限
  if (adminInfo.role === ROLES.SUPER_ADMIN) {
    return true
  }

  return routeRoles.includes(adminInfo.role)
}

