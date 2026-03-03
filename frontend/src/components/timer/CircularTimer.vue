<template>
  <div class="flex flex-col items-center">
    <svg :width="size" :height="size" :viewBox="`0 0 ${size} ${size}`">
      <!-- Background circle -->
      <circle
        :cx="center" :cy="center" :r="radius"
        fill="none" :stroke="bgStroke" :stroke-width="strokeWidth"
      />
      <!-- Progress circle -->
      <circle
        :cx="center" :cy="center" :r="radius"
        fill="none" :stroke="progressColor" :stroke-width="strokeWidth"
        stroke-linecap="round"
        :stroke-dasharray="circumference"
        :stroke-dashoffset="dashOffset"
        class="transition-[stroke-dashoffset] duration-1000 ease-linear"
        :transform="`rotate(-90 ${center} ${center})`"
      />
      <!-- Time text -->
      <text
        :x="center" :y="center"
        text-anchor="middle" dominant-baseline="central"
        class="font-mono text-4xl font-bold"
        :fill="progressColor"
      >
        {{ formattedTime }}
      </text>
      <!-- Phase label -->
      <text
        :x="center" :y="center + 30"
        text-anchor="middle" dominant-baseline="central"
        class="text-sm"
        fill="#9CA3AF"
      >
        {{ phaseLabel }}
      </text>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { TimerPhase } from '@/stores/timer'

const props = withDefaults(defineProps<{
  remainingSeconds: number
  totalSeconds: number
  phase: TimerPhase
  size?: number
}>(), {
  size: 280
})

const strokeWidth = 8
const center = computed(() => props.size / 2)
const radius = computed(() => (props.size - strokeWidth) / 2)
const circumference = computed(() => 2 * Math.PI * radius.value)
const bgStroke = '#E5E7EB'

const progress = computed(() => {
  if (props.totalSeconds === 0) return 0
  if (props.phase === 'idle') return 1
  return props.remainingSeconds / props.totalSeconds
})

const dashOffset = computed(() => {
  return circumference.value * (1 - progress.value)
})

const progressColor = computed(() => {
  return props.phase === 'work' ? '#FF6347' : '#10B981'
})

const phaseLabel = computed(() => {
  switch (props.phase) {
    case 'work': return 'Focus'
    case 'shortBreak': return 'Short Break'
    case 'longBreak': return 'Long Break'
    default: return 'Ready'
  }
})

const displaySeconds = computed(() => {
  return props.phase === 'idle' ? props.totalSeconds : props.remainingSeconds
})

const formattedTime = computed(() => {
  const mins = Math.floor(displaySeconds.value / 60)
  const secs = displaySeconds.value % 60
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
})
</script>
