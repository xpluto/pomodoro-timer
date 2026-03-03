import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'

vi.mock('@/api/auth', () => ({
  authApi: {
    login: vi.fn(),
    register: vi.fn(),
    me: vi.fn()
  }
}))

import { authApi } from '@/api/auth'

describe('auth store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('login stores tokens and sets user', async () => {
    const mockResponse = {
      data: {
        success: true,
        data: {
          accessToken: 'access123',
          refreshToken: 'refresh123',
          user: { id: 1, username: 'test' }
        }
      }
    }
    vi.mocked(authApi.login).mockResolvedValue(mockResponse)

    const store = useAuthStore()
    await store.login('test', 'password')

    expect(authApi.login).toHaveBeenCalledWith('test', 'password')
    expect(store.isAuthenticated).toBe(true)
    expect(store.user?.username).toBe('test')
    expect(localStorage.getItem('accessToken')).toBe('access123')
    expect(localStorage.getItem('refreshToken')).toBe('refresh123')
  })

  it('login with success=false does not set auth state', async () => {
    const mockResponse = {
      data: {
        success: false,
        data: null,
        message: 'Invalid credentials'
      }
    }
    vi.mocked(authApi.login).mockResolvedValue(mockResponse)

    const store = useAuthStore()
    const result = await store.login('test', 'wrong')

    expect(store.isAuthenticated).toBe(false)
    expect(store.user).toBeNull()
    expect(localStorage.getItem('accessToken')).toBeNull()
    expect(result.success).toBe(false)
    expect(result.message).toBe('Invalid credentials')
  })

  it('register returns API result', async () => {
    const mockResponse = {
      data: {
        success: true,
        data: {
          accessToken: 'access456',
          refreshToken: 'refresh456',
          user: { id: 2, username: 'newuser' }
        }
      }
    }
    vi.mocked(authApi.register).mockResolvedValue(mockResponse)

    const store = useAuthStore()
    const result = await store.register('newuser', 'new@example.com', 'password')

    expect(authApi.register).toHaveBeenCalledWith('newuser', 'new@example.com', 'password')
    expect(result.success).toBe(true)
  })

  it('fetchUser sets user on success', async () => {
    const mockResponse = {
      data: {
        success: true,
        data: { id: 1, username: 'test', email: 'test@example.com' }
      }
    }
    vi.mocked(authApi.me).mockResolvedValue(mockResponse)

    const store = useAuthStore()
    await store.fetchUser()

    expect(authApi.me).toHaveBeenCalled()
    expect(store.user?.username).toBe('test')
  })

  it('fetchUser triggers logout on error', async () => {
    vi.mocked(authApi.me).mockRejectedValue(new Error('Unauthorized'))
    localStorage.setItem('accessToken', 'expired-token')

    const store = useAuthStore()
    await store.fetchUser()

    expect(store.isAuthenticated).toBe(false)
    expect(store.user).toBeNull()
    expect(localStorage.getItem('accessToken')).toBeNull()
  })

  it('logout clears state', () => {
    localStorage.setItem('accessToken', 'token')
    localStorage.setItem('refreshToken', 'refresh')
    const store = useAuthStore()
    store.logout()

    expect(store.isAuthenticated).toBe(false)
    expect(store.user).toBeNull()
    expect(localStorage.getItem('accessToken')).toBeNull()
    expect(localStorage.getItem('refreshToken')).toBeNull()
  })
})
