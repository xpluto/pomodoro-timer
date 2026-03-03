import api from './axios'
import type { ApiResult } from '@/types'

export interface WeeklyDataPoint {
  date: string
  completedCount: number
  totalMinutes: number
}

export interface DailyDetail {
  completedCount: number
  totalMinutes: number
  tagBreakdown: { tagId: number; tagName: string; tagColor: string; count: number; minutes: number }[]
}

export interface OverviewStats {
  totalPomodoros: number
  totalHours: number
  currentStreak: number
  bestStreak: number
}

export const statsApi = {
  daily(date: string) {
    return api.get<ApiResult<DailyDetail>>('/stats/daily', { params: { date } })
  },
  weekly(date: string) {
    return api.get<ApiResult<WeeklyDataPoint[]>>('/stats/weekly', { params: { date } })
  },
  overview() {
    return api.get<ApiResult<OverviewStats>>('/stats/overview')
  }
}
