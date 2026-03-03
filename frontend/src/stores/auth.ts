import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User } from '@/types'
import { authApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const isAuthenticated = ref(!!localStorage.getItem('accessToken'))

  async function login(username: string, password: string) {
    const { data } = await authApi.login(username, password)
    if (data.success && data.data) {
      localStorage.setItem('accessToken', data.data.accessToken)
      localStorage.setItem('refreshToken', data.data.refreshToken)
      user.value = data.data.user
      isAuthenticated.value = true
    }
    return data
  }

  async function register(username: string, email: string, password: string) {
    const { data } = await authApi.register(username, email, password)
    return data
  }

  async function fetchUser() {
    try {
      const { data } = await authApi.me()
      if (data.success) {
        user.value = data.data
      }
    } catch (error) {
      console.error('Failed to fetch user:', error)
      logout()
    }
  }

  function logout() {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    user.value = null
    isAuthenticated.value = false
  }

  return { user, isAuthenticated, login, register, fetchUser, logout }
})
