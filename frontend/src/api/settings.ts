import api from './axios'
import type { ApiResult, UserSettings } from '@/types'

export const settingsApi = {
  get() {
    return api.get<ApiResult<UserSettings>>('/settings')
  },
  update(data: UserSettings) {
    return api.put<ApiResult<UserSettings>>('/settings', data)
  }
}
