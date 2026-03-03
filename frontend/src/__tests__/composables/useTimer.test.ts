import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useTimerStore } from '@/stores/timer'
import { useSettingsStore } from '@/stores/settings'

vi.mock('@/api/pomodoro', () => ({
  pomodoroApi: {
    start: vi.fn(),
    complete: vi.fn().mockResolvedValue({ data: { success: true, data: null, message: null } }),
    interrupt: vi.fn(),
    today: vi.fn()
  }
}))

vi.mock('@/api/settings', () => ({
  settingsApi: {
    get: vi.fn(),
    update: vi.fn()
  }
}))

describe('timer store - countdown logic', () => {
  beforeEach(() => {
    vi.useFakeTimers()
    setActivePinia(createPinia())

    // Set short durations for testing
    const settingsStore = useSettingsStore()
    settingsStore.settings = {
      workDuration: 3,
      shortBreak: 1,
      longBreak: 2,
      longBreakInterval: 4,
      autoStartBreak: false,
      autoStartWork: false,
      soundEnabled: false,
      notificationEnabled: false
    }
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('starts in idle phase', () => {
    const store = useTimerStore()
    expect(store.phase).toBe('idle')
    expect(store.isRunning).toBe(false)
    expect(store.remainingSeconds).toBe(0)
  })

  it('startTimer begins work phase with correct duration', () => {
    const store = useTimerStore()
    store.startTimer()

    expect(store.phase).toBe('work')
    expect(store.isRunning).toBe(true)
    expect(store.remainingSeconds).toBe(3)
  })

  it('countdown decrements remaining seconds', () => {
    const store = useTimerStore()
    store.startTimer()

    vi.advanceTimersByTime(1000)
    expect(store.remainingSeconds).toBe(2)

    vi.advanceTimersByTime(1000)
    expect(store.remainingSeconds).toBe(1)
  })

  it('transitions to short break after work completes', async () => {
    const store = useTimerStore()
    store.startTimer()

    await vi.advanceTimersByTimeAsync(3000)

    expect(store.phase).toBe('shortBreak')
    expect(store.remainingSeconds).toBe(1)
    expect(store.roundCount).toBe(1)
    expect(store.isRunning).toBe(false)
  })

  it('transitions to long break after longBreakInterval rounds', async () => {
    const settingsStore = useSettingsStore()
    settingsStore.settings.longBreakInterval = 2

    const store = useTimerStore()
    store.startTimer()

    // Complete round 1
    await vi.advanceTimersByTimeAsync(3000)
    expect(store.phase).toBe('shortBreak')

    // Skip break, start next work
    store.skipTimer()
    await vi.advanceTimersByTimeAsync(0) // flush microtasks
    store.resumeTimer()

    // Complete round 2
    await vi.advanceTimersByTimeAsync(3000)

    expect(store.phase).toBe('longBreak')
    expect(store.remainingSeconds).toBe(2)
    expect(store.roundCount).toBe(2)
  })

  it('pause and resume work correctly', () => {
    const store = useTimerStore()
    store.startTimer()

    vi.advanceTimersByTime(1000)
    expect(store.remainingSeconds).toBe(2)

    store.pauseTimer()
    expect(store.isRunning).toBe(false)

    vi.advanceTimersByTime(2000)
    expect(store.remainingSeconds).toBe(2) // Didn't decrement while paused

    store.resumeTimer()
    expect(store.isRunning).toBe(true)

    vi.advanceTimersByTime(1000)
    expect(store.remainingSeconds).toBe(1)
  })

  it('resetTimer returns to idle', () => {
    const store = useTimerStore()
    store.startTimer()

    vi.advanceTimersByTime(1000)
    store.resetTimer()

    expect(store.phase).toBe('idle')
    expect(store.isRunning).toBe(false)
    expect(store.remainingSeconds).toBe(0)
    expect(store.roundCount).toBe(0)
  })

  it('skipTimer advances to next phase', async () => {
    const store = useTimerStore()
    store.startTimer()

    store.skipTimer()
    await vi.advanceTimersByTimeAsync(0) // flush microtasks

    expect(store.phase).toBe('shortBreak')
  })

  it('auto-starts break when setting enabled', async () => {
    const settingsStore = useSettingsStore()
    settingsStore.settings.autoStartBreak = true

    const store = useTimerStore()
    store.startTimer()

    await vi.advanceTimersByTimeAsync(3000)

    expect(store.phase).toBe('shortBreak')
    expect(store.isRunning).toBe(true)
  })

  it('auto-starts work when setting enabled', async () => {
    const settingsStore = useSettingsStore()
    settingsStore.settings.autoStartBreak = true
    settingsStore.settings.autoStartWork = true

    const store = useTimerStore()
    store.startTimer()

    // Work completes (3s) -> auto-start break
    await vi.advanceTimersByTimeAsync(3000)
    expect(store.phase).toBe('shortBreak')
    expect(store.isRunning).toBe(true)

    // Break completes (1s) -> auto-start work
    await vi.advanceTimersByTimeAsync(1000)
    expect(store.phase).toBe('work')
    expect(store.isRunning).toBe(true)
  })
})
