import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn, hasRoutePermission } from '@/utils/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/profile'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: {
        title: '登录',
        requiresAuth: false
      }
    },
    {
      path: '/',
      component: () => import('@/views/layout/index.vue'),
      meta: {
        requiresAuth: true
      },
      children: [
        // 个人信息
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/profile/index.vue'),
          meta: {
            title: '个人信息',
            icon: 'User'
          }
        },
        // 用户管理
        {
          path: 'users',
          name: 'Users',
          redirect: '/users/customers',
          meta: {
            title: '用户管理',
            icon: 'UserFilled'
          },
          children: [
            {
              path: 'customers',
              name: 'Customers',
              component: () => import('@/views/users/customers/index.vue'),
              meta: {
                title: 'C端用户管理',
                icon: 'User'
              }
            },
            {
              path: 'admins',
              name: 'Admins',
              component: () => import('@/views/users/admins/index.vue'),
              meta: {
                title: 'B端用户管理',
                icon: 'UserFilled'
              }
            }
          ]
        },
        // 运营管理
        {
          path: 'operation',
          name: 'Operation',
          redirect: '/operation/dashboard',
          meta: {
            title: '运营管理',
            icon: 'Monitor'
          },
          children: [
            {
              path: 'dashboard',
              name: 'Dashboard',
              component: () => import('@/views/operation/dashboard/index.vue'),
              meta: {
                title: '仪表盘',
                icon: 'DataBoard'
              }
            },
            {
              path: 'trains',
              name: 'Trains',
              component: () => import('@/views/operation/trains/index.vue'),
              meta: {
                title: '列车管理',
                icon: 'Van'
              }
            },
            {
              path: 'routes',
              name: 'Routes',
              component: () => import('@/views/operation/routes/index.vue'),
              meta: {
                title: '线路管理',
                icon: 'Share'
              }
            },
            {
              path: 'tickets',
              name: 'Tickets',
              component: () => import('@/views/operation/tickets/index.vue'),
              meta: {
                title: '车票管理',
                icon: 'Ticket'
              }
            },
            {
              path: 'mall',
              name: 'Mall',
              component: () => import('@/views/operation/mall/index.vue'),
              meta: {
                title: '商城管理',
                icon: 'Goods'
              }
            }
          ]
        },
        // 订单管理
        {
          path: 'orders',
          name: 'Orders',
          redirect: '/orders/tickets',
          meta: {
            title: '订单管理',
            icon: 'Tickets'
          },
          children: [
            {
              path: 'tickets',
              name: 'TicketOrders',
              component: () => import('@/views/orders/tickets/index.vue'),
              meta: {
                title: '车票订单',
                icon: 'List'
              }
            },
            {
              path: 'mall',
              name: 'MallOrders',
              component: () => import('@/views/orders/mall/index.vue'),
              meta: {
                title: '商城订单',
                icon: 'ShoppingCart'
              }
            }
          ]
        },
        // 客服回复
        {
          path: 'service',
          name: 'Service',
          component: () => import('@/views/service/index.vue'),
          meta: {
            title: '客服回复',
            icon: 'ChatDotRound'
          }
        },
        // 权限管理
        {
          path: 'permissions',
          name: 'Permissions',
          component: () => import('@/views/permissions/index.vue'),
          meta: {
            title: '权限管理',
            icon: 'Lock'
          }
        }
      ]
    },
    // 403 无权限页面
    {
      path: '/403',
      name: 'Forbidden',
      component: () => import('@/views/error/403.vue'),
      meta: {
        title: '无权限访问',
        requiresAuth: false
      }
    },
    // 404 页面 - 必须放在最后
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/error/404.vue'),
      meta: {
        title: '页面未找到',
        requiresAuth: false
      }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 铁路票务管理系统`
  }

  // 检查是否需要登录
  if (to.meta.requiresAuth !== false) {
    if (!isLoggedIn()) {
      // 未登录,跳转到登录页
      next('/login')
      return
    }

    // 已登录,检查路由权限
    if (to.path !== '/login' && to.path !== '/403' && !hasRoutePermission(to.path)) {
      // 无权限访问,跳转到403页面
      console.warn(`无权限访问: ${to.path}`)
      next('/403')
      return
    }

    next()
  } else {
    // 不需要登录的页面
    if (to.path === '/login' && isLoggedIn()) {
      // 已登录用户访问登录页,跳转到个人信息页面
      next('/profile')
    } else {
      next()
    }
  }
})

export default router
