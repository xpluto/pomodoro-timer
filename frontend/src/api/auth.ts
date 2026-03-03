import api from './axios'
import type { ApiResult, AuthResponse, User } from '@/types'

export const authApi = {
  login(username: string, password: string) {
    return api.post<ApiResult<AuthResponse>>('/auth/login', { username, password })
  },
  register(username: string, email: string, password: string) {
    return api.post<ApiResult<AuthResponse>>('/auth/register', { username, email, password })
  },
  refresh(refreshToken: string) {
    return api.post<ApiResult<AuthResponse>>('/auth/refresh', { refreshToken })
  },
  me() {
    return api.get<ApiResult<User>>('/auth/me')
  }
}
