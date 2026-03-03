import { createRouter, createWebHistory } from 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    auth?: boolean
    guest?: boolean
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { guest: true }
    },
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { auth: true }
    },
    {
      path: '/stats',
      name: 'stats',
      component: () => import('@/views/StatsView.vue'),
      meta: { auth: true }
    },
    {
      path: '/leaderboard',
      name: 'leaderboard',
      component: () => import('@/views/LeaderboardView.vue'),
      meta: { auth: true }
    },
    {
      path: '/settings',
      name: 'settings',
      component: () => import('@/views/SettingsView.vue'),
      meta: { auth: true }
    }
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('accessToken')
  if (to.meta.auth && !token) {
    return { name: 'login' }
  }
  if (to.meta.guest && token) {
    return { name: 'home' }
  }
})

export default router
