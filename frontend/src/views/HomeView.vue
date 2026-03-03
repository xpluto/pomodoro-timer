<template>
  <div class="space-y-6">
    <!-- Timer Section -->
    <div class="bg-white rounded-2xl border border-gray-200 p-8 text-center space-y-6">
      <CircularTimer
        :remaining-seconds="timerStore.remainingSeconds"
        :total-seconds="timerStore.totalSeconds"
        :phase="timerStore.phase"
      />

      <!-- Round indicators -->
      <div class="flex items-center justify-center gap-2">
        <span
          v-for="i in settingsStore.settings.longBreakInterval"
          :key="i"
          class="w-3 h-3 rounded-full transition-colors"
          :class="i <= timerStore.roundCount ? 'bg-red-400' : 'bg-gray-200'"
        />
      </div>

      <TimerControls
        :phase="timerStore.phase"
        :is-running="timerStore.isRunning"
        @start="handleStart"
        @pause="timerStore.pauseTimer"
        @resume="timerStore.resumeTimer"
        @reset="handleReset"
        @skip="timerStore.skipTimer"
      />

      <TaskInput
        v-model:task-name="taskName"
        v-model:selected-tag-id="selectedTagId"
        :tags="tagsStore.tags"
        :disabled="timerStore.phase !== 'idle'"
      />
    </div>

    <!-- Daily Progress -->
    <ProgressBar
      :completed="completedToday"
      :goal="authStore.user?.dailyGoal ?? 8"
      :total-minutes="totalMinutesToday"
    />

    <!-- Today's Records -->
    <div class="bg-white rounded-2xl border border-gray-200 p-6">
      <h2 class="text-lg font-semibold text-gray-800 mb-4">Today's Records</h2>
      <div v-if="timerStore.todayRecords.length === 0" class="text-gray-400 text-sm text-center py-4">
        No pomodoros yet today. Start your first session!
      </div>
      <div v-else class="space-y-3">
        <div
          v-for="record in timerStore.todayRecords"
          :key="record.id"
          class="flex items-center justify-between py-2 border-b border-gray-50 last:border-0"
        >
          <div class="flex items-center gap-3">
            <span v-if="record.status === 'completed'" class="text-green-500">&#10003;</span>
            <span v-else class="text-red-400">&#10007;</span>
            <div>
              <span class="text-gray-700">{{ record.taskName || 'Untitled' }}</span>
              <div class="text-xs text-gray-400">{{ formatTime(record.startedAt) }}</div>
            </div>
            <TagBadge
              v-if="getTagForRecord(record)"
              :name="getTagForRecord(record)!.name"
              :color="getTagForRecord(record)!.color"
            />
          </div>
          <div class="text-sm text-gray-400">
            {{ formatDuration(record.duration) }}
            <span v-if="record.status === 'interrupted'" class="text-red-400 ml-1">(interrupted)</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useTimerStore } from '@/stores/timer'
import { useTagsStore } from '@/stores/tags'
import { useSettingsStore } from '@/stores/settings'
import { useAuthStore } from '@/stores/auth'
import type { PomodoroRecord } from '@/types'
import CircularTimer from '@/components/timer/CircularTimer.vue'
import TimerControls from '@/components/timer/TimerControls.vue'
import TaskInput from '@/components/timer/TaskInput.vue'
import ProgressBar from '@/components/common/ProgressBar.vue'
import TagBadge from '@/components/common/TagBadge.vue'
import { generateDefaultTaskName, formatTime, formatDuration } from '@/utils/format'

const timerStore = useTimerStore()
const tagsStore = useTagsStore()
const settingsStore = useSettingsStore()
const authStore = useAuthStore()

const taskName = ref('')
const selectedTagId = ref<number | null>(null)

const completedToday = computed(() =>
  timerStore.todayRecords.filter((r) => r.status === 'completed').length
)

const totalMinutesToday = computed(() =>
  timerStore.todayRecords.reduce((sum, r) => sum + Math.round(r.duration / 60), 0)
)

function getTagForRecord(record: PomodoroRecord) {
  if (!record.tagId) return null
  return tagsStore.tags.find((t) => t.id === record.tagId) ?? null
}

async function handleStart() {
  try {
    await timerStore.startPomodoro(
      taskName.value.trim() || generateDefaultTaskName(),
      settingsStore.settings.workDuration,
      selectedTagId.value ?? undefined
    )
    timerStore.startTimer()
  } catch (error) {
    console.error('Failed to start pomodoro:', error)
  }
}

function handleReset() {
  if (timerStore.currentPomodoro && timerStore.phase === 'work') {
    timerStore.interruptPomodoro()
  }
  timerStore.resetTimer()
}

onMounted(async () => {
  await Promise.all([
    settingsStore.fetchSettings(),
    tagsStore.fetchTags(),
    timerStore.fetchTodayRecords()
  ])
  if ('Notification' in window && Notification.permission === 'default') {
    Notification.requestPermission()
  }
})
</script>
