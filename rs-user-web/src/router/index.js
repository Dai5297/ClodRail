import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'index',
            component: () => import('@/view/layout/index.vue'),
            children: [
                {
                    path: '',
                    name: 'home',
                    component: () => import('@/view/home/index.vue'),
                },
                {
                    path: 'ticket',
                    name: 'ticket',
                    component: () => import('@/view/ticket/index.vue')
                },
                {
                    path: 'ticketSearch',
                    redirect: '/ticket'
                },
                {
                    path: 'ticket-detail',
                    name: 'ticket-detail',
                    component: () => import('@/view/ticket-detail/index.vue')
                },
                {
                    path: 'ticket-payment',
                    name: 'ticket-payment',
                    component: () => import('@/view/ticket-payment/index.vue')
                },
                {
                    path: 'mall',
                    name: 'mall',
                    component: () => import('@/view/mall/index.vue')
                },
                {
                    path: 'user',
                    name: 'user',
                    component: () => import('@/view/user/index.vue'),
                    children: [
                        {
                            path: '',
                            name: 'user-profile',
                            component: () => import('@/view/user-profile/index.vue')
                        },
                        {
                            path: 'order',
                            name: 'user-order',
                            component: () => import('@/view/user-order/index.vue')
                        },
                        {
                            path: 'contact',
                            name: 'user-contact',
                            component: () => import('@/view/user-contact/index.vue')
                        },
                        {
                            path: 'point',
                            name: 'user-point',
                            component: () => import('@/view/user-point/index.vue')
                        },
                        {
                            path: 'security',
                            name: 'user-security',
                            component: () => import('@/view/user-security/index.vue')
                        }
                    ]
                },
                {
                    path: 'assistant',
                    name: 'assistant',
                    component: () => import('@/view/assistant/index.vue')
                }
            ]
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('@/view/login/index.vue')
        },
        {
            path: '/payment/success',
            name: 'payment-success',
            component: () => import('@/view/payment-success/index.vue')
        },
        {
            path: '/alipay/return',
            name: 'alipay-return',
            component: () => import('@/view/payment-success/index.vue')
        }
    ],
})

export default router
