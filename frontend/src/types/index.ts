export interface User {
  id: number
  username: string
  email: string
  avatarUrl: string | null
  dailyGoal: number
  createdAt: string
}

export interface Tag {
  id: number
  userId: number
  name: string
  color: string
}

export interface PomodoroRecord {
  id: number
  userId: number
  tagId: number | null
  taskName: string
  duration: number
  plannedDuration: number
  status: 'completed' | 'interrupted' | 'abandoned'
  startedAt: string
  finishedAt: string | null
}

export interface UserSettings {
  workDuration: number
  shortBreak: number
  longBreak: number
  longBreakInterval: number
  autoStartBreak: boolean
  autoStartWork: boolean
  soundEnabled: boolean
  notificationEnabled: boolean
}

export interface ApiResult<T> {
  success: boolean
  data: T | null
  message: string | null
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  user: User
}

export interface DailyStats {
  date: string
  totalPomodoros: number
  totalMinutes: number
  completedPomodoros: number
  tagBreakdown: { tagName: string; tagColor: string; count: number }[]
}

export interface LeaderboardEntry {
  rank: number
  username: string
  avatarUrl: string | null
  pomodoroCount: number
  totalMinutes: number
}
