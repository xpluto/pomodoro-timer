import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { PomodoroRecord } from '@/types'
import { pomodoroApi } from '@/api/pomodoro'
import { useSettingsStore } from '@/stores/settings'

export type TimerPhase = 'work' | 'shortBreak' | 'longBreak' | 'idle'

export const useTimerStore = defineStore('timer', () => {
  const currentPomodoro = ref<PomodoroRecord | null>(null)
  const todayRecords = ref<PomodoroRecord[]>([])

  // Timer state
  const remainingSeconds = ref(0)
  const phase = ref<TimerPhase>('idle')
  const isRunning = ref(false)
  const roundCount = ref(0)

  let intervalId: ReturnType<typeof setInterval> | null = null

  const totalSeconds = computed(() => {
    const s = useSettingsStore().settings
    switch (phase.value) {
      case 'work': return s.workDuration
      case 'shortBreak': return s.shortBreak
      case 'longBreak': return s.longBreak
      default: return s.workDuration
    }
  })

  // --- Timer interval ---

  function clearTimer() {
    if (intervalId !== null) {
      clearInterval(intervalId)
      intervalId = null
    }
  }

  function startInterval() {
    clearTimer()
    isRunning.value = true
    intervalId = setInterval(tick, 1000)
  }

  function tick() {
    remainingSeconds.value--
    if (remainingSeconds.value <= 0) {
      clearTimer()
      isRunning.value = false
      handlePhaseComplete()
    }
  }

  async function handlePhaseComplete() {
    const s = useSettingsStore().settings

    if (phase.value === 'work') {
      roundCount.value++
      try {
        await completePomodoro()
      } catch (error) {
        console.error('Failed to complete pomodoro:', error)
      }
      playNotification(s.notificationEnabled, s.soundEnabled, 'Work session complete! Time for a break.')

      if (roundCount.value % s.longBreakInterval === 0) {
        phase.value = 'longBreak'
        remainingSeconds.value = s.longBreak
      } else {
        phase.value = 'shortBreak'
        remainingSeconds.value = s.shortBreak
      }
      if (s.autoStartBreak) {
        startInterval()
      }
    } else {
      playNotification(s.notificationEnabled, s.soundEnabled, 'Break is over! Time to focus.')
      phase.value = 'work'
      remainingSeconds.value = s.workDuration
      if (s.autoStartWork) {
        startInterval()
      }
    }
  }

  // --- Timer actions ---

  function startTimer() {
    const s = useSettingsStore().settings
    phase.value = 'work'
    remainingSeconds.value = s.workDuration
    roundCount.value = 0
    startInterval()
  }

  function pauseTimer() {
    clearTimer()
    isRunning.value = false
  }

  function resumeTimer() {
    if (phase.value !== 'idle' && remainingSeconds.value > 0) {
      startInterval()
    }
  }

  function resetTimer() {
    clearTimer()
    isRunning.value = false
    phase.value = 'idle'
    remainingSeconds.value = 0
    roundCount.value = 0
  }

  function skipTimer() {
    clearTimer()
    isRunning.value = false
    remainingSeconds.value = 0
    handlePhaseComplete()
  }

  // --- Pomodoro API ---

  async function startPomodoro(taskName: string, plannedDuration: number, tagId?: number) {
    const payload: { taskName: string; plannedDuration: number; tagId?: number } = {
      taskName,
      plannedDuration
    }
    if (tagId !== undefined) {
      payload.tagId = tagId
    }
    const { data } = await pomodoroApi.start(payload)
    if (data.success && data.data) {
      currentPomodoro.value = data.data
    }
    return data
  }

  async function completePomodoro() {
    if (!currentPomodoro.value) return null
    const { data } = await pomodoroApi.complete(currentPomodoro.value.id)
    if (data.success && data.data) {
      todayRecords.value = [data.data, ...todayRecords.value]
      currentPomodoro.value = null
    }
    return data
  }

  async function interruptPomodoro() {
    if (!currentPomodoro.value) return null
    const { data } = await pomodoroApi.interrupt(currentPomodoro.value.id)
    if (data.success && data.data) {
      todayRecords.value = [data.data, ...todayRecords.value]
      currentPomodoro.value = null
    }
    return data
  }

  async function fetchTodayRecords() {
    const { data } = await pomodoroApi.today()
    if (data.success && data.data) {
      todayRecords.value = data.data
    }
  }

  return {
    // Timer state
    remainingSeconds,
    phase,
    isRunning,
    roundCount,
    totalSeconds,
    // Timer actions
    startTimer,
    pauseTimer,
    resumeTimer,
    resetTimer,
    skipTimer,
    // Pomodoro state
    currentPomodoro,
    todayRecords,
    // Pomodoro actions
    startPomodoro,
    completePomodoro,
    interruptPomodoro,
    fetchTodayRecords
  }
})

// --- Notification helpers (module-level, no component dependency) ---

function playNotification(notificationEnabled: boolean, soundEnabled: boolean, message: string) {
  if (notificationEnabled && 'Notification' in window && Notification.permission === 'granted') {
    new Notification('Pomodoro Timer', { body: message, icon: '/favicon.ico' })
  }
  if (soundEnabled) {
    playBeep()
  }
}

function playBeep() {
  try {
    const ctx = new AudioContext()
    const oscillator = ctx.createOscillator()
    const gain = ctx.createGain()
    oscillator.connect(gain)
    gain.connect(ctx.destination)
    oscillator.frequency.value = 800
    oscillator.type = 'sine'
    gain.gain.value = 0.3
    oscillator.start()
    gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.5)
    oscillator.stop(ctx.currentTime + 0.5)
  } catch (error) {
    console.error('AudioContext not available:', error)
  }
}
