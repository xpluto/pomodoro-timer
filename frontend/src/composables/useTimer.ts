import { ref, computed } from 'vue'
import type { UserSettings } from '@/types'

export type TimerPhase = 'work' | 'shortBreak' | 'longBreak' | 'idle'

export interface TimerCallbacks {
  onWorkComplete?: () => void
  onBreakComplete?: () => void
}

export function useTimer(settings: () => UserSettings, callbacks: TimerCallbacks = {}) {
  const remainingSeconds = ref(0)
  const phase = ref<TimerPhase>('idle')
  const isRunning = ref(false)
  const roundCount = ref(0)

  let intervalId: ReturnType<typeof setInterval> | null = null

  const totalSeconds = computed(() => {
    const s = settings()
    switch (phase.value) {
      case 'work': return s.workDuration
      case 'shortBreak': return s.shortBreak
      case 'longBreak': return s.longBreak
      default: return s.workDuration
    }
  })

  function clearTimer() {
    if (intervalId !== null) {
      clearInterval(intervalId)
      intervalId = null
    }
  }

  function tick() {
    remainingSeconds.value--
    if (remainingSeconds.value <= 0) {
      clearTimer()
      isRunning.value = false
      handlePhaseComplete()
    }
  }

  function handlePhaseComplete() {
    const s = settings()
    if (phase.value === 'work') {
      roundCount.value++
      callbacks.onWorkComplete?.()
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
    } else if (phase.value === 'shortBreak' || phase.value === 'longBreak') {
      callbacks.onBreakComplete?.()
      phase.value = 'work'
      remainingSeconds.value = s.workDuration
      if (s.autoStartWork) {
        startInterval()
      }
    }
  }

  function startInterval() {
    clearTimer()
    isRunning.value = true
    intervalId = setInterval(tick, 1000)
  }

  function start() {
    const s = settings()
    phase.value = 'work'
    remainingSeconds.value = s.workDuration
    roundCount.value = 0
    startInterval()
  }

  function pause() {
    clearTimer()
    isRunning.value = false
  }

  function resume() {
    if (phase.value !== 'idle' && remainingSeconds.value > 0) {
      startInterval()
    }
  }

  function reset() {
    clearTimer()
    isRunning.value = false
    phase.value = 'idle'
    remainingSeconds.value = 0
    roundCount.value = 0
  }

  function skip() {
    clearTimer()
    isRunning.value = false
    remainingSeconds.value = 0
    handlePhaseComplete()
  }

  function dispose() {
    clearTimer()
  }

  return {
    remainingSeconds,
    phase,
    isRunning,
    roundCount,
    totalSeconds,
    start,
    pause,
    resume,
    reset,
    skip,
    dispose
  }
}
