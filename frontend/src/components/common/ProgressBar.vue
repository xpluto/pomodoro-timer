<template>
  <div class="bg-white rounded-xl border border-gray-200 p-4">
    <div class="flex items-center justify-between mb-2">
      <span class="text-sm text-gray-600">
        Today: <span class="font-semibold text-gray-800">{{ completed }}/{{ goal }}</span> pomodoros
      </span>
      <span class="text-sm text-gray-500">{{ totalMinutes }}m</span>
    </div>
    <div class="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
      <div
        class="h-full bg-red-400 rounded-full transition-all duration-500"
        :style="{ width: `${percentage}%` }"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  completed: number
  goal: number
  totalMinutes: number
}>()

const percentage = computed(() => {
  if (props.goal === 0) return 0
  return Math.min(100, (props.completed / props.goal) * 100)
})
</script>
