<template>
  <div class="flex items-center justify-center gap-4">
    <button v-if="phase === 'idle'" @click="$emit('start')"
      class="px-8 py-3 bg-red-500 text-white font-medium rounded-xl hover:bg-red-600 transition">
      Start
    </button>

    <template v-else>
      <button v-if="isRunning" @click="$emit('pause')"
        class="px-6 py-3 bg-gray-200 text-gray-700 font-medium rounded-xl hover:bg-gray-300 transition">
        Pause
      </button>
      <button v-else @click="$emit('resume')"
        class="px-6 py-3 bg-red-500 text-white font-medium rounded-xl hover:bg-red-600 transition">
        Resume
      </button>

      <button @click="$emit('reset')"
        class="px-6 py-3 bg-gray-100 text-gray-500 font-medium rounded-xl hover:bg-gray-200 transition">
        Reset
      </button>

      <button @click="$emit('skip')"
        class="px-6 py-3 bg-gray-100 text-gray-500 font-medium rounded-xl hover:bg-gray-200 transition">
        Skip
      </button>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { TimerPhase } from '@/stores/timer'

defineProps<{
  phase: TimerPhase
  isRunning: boolean
}>()

defineEmits<{
  start: []
  pause: []
  resume: []
  reset: []
  skip: []
}>()
</script>
