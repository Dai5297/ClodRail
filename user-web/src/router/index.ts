import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/Layout.vue'
import HomeView from '../views/HomeView/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  linkActiveClass: 'router-link-active',
  linkExactActiveClass: 'router-link-exact-active',
  routes: [
    {
      path: '/',
      component: Layout,
      children: [
        {
          path: '',
          name: 'home',
          component: HomeView,
        },
        {
          path: 'search',
          name: 'train-search',
          component: () => import('@/views/TrainSearchView/index.vue'),
        },
        {
          path: 'seat-selection',
          name: 'seat-selection',
          component: () => import('@/views/SeatSelectionView/index.vue'),
        },
        {
          path: 'passenger-info',
          name: 'passenger-info',
          component: () => import('@/views/PassengerInfoView/index.vue'),
        },
        {
          path: 'payment',
          name: 'payment',
          component: () => import('@/views/PaymentView/index.vue'),
        },
        {
          path: 'order-success',
          name: 'order-success',
          component: () => import('@/views/OrderSuccessView/index.vue'),
        },
        {
          path: 'my-orders',
          name: 'my-orders',
          component: () => import('@/views/MyOrdersView/index.vue'),
        },
        {
          path: 'order-detail/:id',
          name: 'order-detail',
          component: () => import('@/views/OrderDetailView/index.vue'),
        },
        {
          path: 'points-mall',
          name: 'points-mall',
          component: () => import('@/views/PointsMallView/index.vue'),
        },
        {
          path: 'ai-assistant',
          name: 'ai-assistant',
          component: () => import('@/views/AIAssistantView/index.vue'),
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/ProfileView/index.vue'),
        },
      ],
    },
    // 独立页面（不使用布局）
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView/index.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView/index.vue'),
    },
  ],
})

export default router
