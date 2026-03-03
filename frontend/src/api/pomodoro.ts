import api from './axios'
import type { ApiResult, PomodoroRecord } from '@/types'

export const pomodoroApi = {
  start(data: { taskName: string; plannedDuration: number; tagId?: number }) {
    return api.post<ApiResult<PomodoroRecord>>('/pomodoros/start', data)
  },
  complete(id: number) {
    return api.put<ApiResult<PomodoroRecord>>(`/pomodoros/${id}/complete`)
  },
  interrupt(id: number) {
    return api.put<ApiResult<PomodoroRecord>>(`/pomodoros/${id}/interrupt`)
  },
  today() {
    return api.get<ApiResult<PomodoroRecord[]>>('/pomodoros/today')
  }
}
