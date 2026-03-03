import api from './axios'
import type { ApiResult, LeaderboardEntry } from '@/types'

export const leaderboardApi = {
  daily() {
    return api.get<ApiResult<LeaderboardEntry[]>>('/leaderboard/daily')
  },
  weekly() {
    return api.get<ApiResult<LeaderboardEntry[]>>('/leaderboard/weekly')
  }
}
