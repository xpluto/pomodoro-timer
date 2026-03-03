import api from './axios'
import type { ApiResult, Tag } from '@/types'

export const tagsApi = {
  list() {
    return api.get<ApiResult<Tag[]>>('/tags')
  },
  create(data: { name: string; color: string }) {
    return api.post<ApiResult<Tag>>('/tags', data)
  },
  update(id: number, data: { name: string; color: string }) {
    return api.put<ApiResult<Tag>>(`/tags/${id}`, data)
  },
  remove(id: number) {
    return api.delete<ApiResult<null>>(`/tags/${id}`)
  }
}
