import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useTimerStore } from '@/stores/timer'

vi.mock('@/api/pomodoro', () => ({
  pomodoroApi: {
    start: vi.fn(),
    complete: vi.fn(),
    interrupt: vi.fn(),
    today: vi.fn()
  }
}))

import { pomodoroApi } from '@/api/pomodoro'

describe('timer store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('startPomodoro sets currentPomodoro on success', async () => {
    const mockRecord = {
      id: 1,
      userId: 1,
      taskName: 'Test task',
      duration: 0,
      plannedDuration: 1500,
      status: 'completed',
      startedAt: '2026-03-02T10:00:00Z',
      finishedAt: null,
      tagId: null
    }
    vi.mocked(pomodoroApi.start).mockResolvedValue({
      data: { success: true, data: mockRecord, message: null }
    })

    const store = useTimerStore()
    await store.startPomodoro('Test task', 1500)

    expect(pomodoroApi.start).toHaveBeenCalledWith({
      taskName: 'Test task',
      plannedDuration: 1500
    })
    expect(store.currentPomodoro).toEqual(mockRecord)
  })

  it('completePomodoro adds record to todayRecords and clears current', async () => {
    const completedRecord = {
      id: 1,
      userId: 1,
      taskName: 'Test task',
      duration: 1500,
      plannedDuration: 1500,
      status: 'completed',
      startedAt: '2026-03-02T10:00:00Z',
      finishedAt: '2026-03-02T10:25:00Z',
      tagId: null
    }

    vi.mocked(pomodoroApi.complete).mockResolvedValue({
      data: { success: true, data: completedRecord, message: null }
    })

    const store = useTimerStore()
    store.currentPomodoro = { id: 1 } as any

    await store.completePomodoro()

    expect(pomodoroApi.complete).toHaveBeenCalledWith(1)
    expect(store.currentPomodoro).toBeNull()
    expect(store.todayRecords).toHaveLength(1)
    expect(store.todayRecords[0]).toEqual(completedRecord)
  })

  it('completePomodoro returns null when no current pomodoro', async () => {
    const store = useTimerStore()
    const result = await store.completePomodoro()
    expect(result).toBeNull()
    expect(pomodoroApi.complete).not.toHaveBeenCalled()
  })

  it('fetchTodayRecords populates todayRecords', async () => {
    const records = [
      { id: 1, taskName: 'Task 1', status: 'completed' },
      { id: 2, taskName: 'Task 2', status: 'interrupted' }
    ]
    vi.mocked(pomodoroApi.today).mockResolvedValue({
      data: { success: true, data: records, message: null }
    })

    const store = useTimerStore()
    await store.fetchTodayRecords()

    expect(store.todayRecords).toEqual(records)
  })
})
