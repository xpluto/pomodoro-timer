<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold text-gray-800">Leaderboard</h1>

    <!-- Tabs -->
    <div class="flex gap-1 bg-gray-100 rounded-lg p-1 w-fit">
      <button
        v-for="tab in tabs" :key="tab.key"
        @click="activeTab = tab.key"
        class="px-4 py-2 rounded-md text-sm font-medium transition"
        :class="activeTab === tab.key
          ? 'bg-white text-gray-800 shadow-sm'
          : 'text-gray-500 hover:text-gray-700'"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Leaderboard Table -->
    <div class="bg-white rounded-2xl border border-gray-200 overflow-hidden">
      <div v-if="loading" class="text-center py-12 text-gray-400">Loading...</div>
      <div v-else-if="entries.length === 0" class="text-center py-12 text-gray-400">No data available</div>
      <table v-else class="w-full">
        <thead>
          <tr class="border-b border-gray-100">
            <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-6 py-3 w-16">Rank</th>
            <th class="text-left text-xs font-medium text-gray-500 uppercase tracking-wider px-6 py-3">User</th>
            <th class="text-right text-xs font-medium text-gray-500 uppercase tracking-wider px-6 py-3">Pomodoros</th>
            <th class="text-right text-xs font-medium text-gray-500 uppercase tracking-wider px-6 py-3">Time</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="entry in entries" :key="entry.rank"
            class="border-b border-gray-50 last:border-0 transition"
            :class="isCurrentUser(entry) ? 'bg-red-50' : 'hover:bg-gray-50'"
          >
            <td class="px-6 py-4">
              <span v-if="entry.rank <= 3" class="text-lg">{{ rankEmoji(entry.rank) }}</span>
              <span v-else class="text-gray-500">{{ entry.rank }}</span>
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center text-sm font-medium text-gray-600">
                  {{ entry.username.charAt(0).toUpperCase() }}
                </div>
                <span class="font-medium text-gray-800" :class="{ 'text-red-600': isCurrentUser(entry) }">
                  {{ entry.username }}
                  <span v-if="isCurrentUser(entry)" class="text-xs text-red-400 ml-1">(you)</span>
                </span>
              </div>
            </td>
            <td class="px-6 py-4 text-right font-semibold text-gray-700">{{ entry.pomodoroCount }}</td>
            <td class="px-6 py-4 text-right text-gray-500">{{ formatDuration(entry.totalMinutes * 60) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { leaderboardApi } from '@/api/leaderboard'
import { useAuthStore } from '@/stores/auth'
import { formatDuration } from '@/utils/format'
import type { LeaderboardEntry } from '@/types'

const authStore = useAuthStore()
const activeTab = ref<'daily' | 'weekly'>('daily')
const entries = ref<LeaderboardEntry[]>([])
const loading = ref(false)

const tabs = [
  { key: 'daily' as const, label: 'Today' },
  { key: 'weekly' as const, label: 'This Week' }
]

function isCurrentUser(entry: LeaderboardEntry) {
  return authStore.user?.username === entry.username
}

function rankEmoji(rank: number) {
  const emojis: Record<number, string> = { 1: '\u{1F947}', 2: '\u{1F948}', 3: '\u{1F949}' }
  return emojis[rank] ?? ''
}

async function fetchLeaderboard() {
  loading.value = true
  try {
    const { data } = activeTab.value === 'daily'
      ? await leaderboardApi.daily()
      : await leaderboardApi.weekly()
    if (data.success && data.data) {
      entries.value = data.data
    }
  } catch (error) {
    console.error('Failed to fetch leaderboard:', error)
    entries.value = []
  } finally {
    loading.value = false
  }
}

watch(activeTab, fetchLeaderboard)
onMounted(fetchLeaderboard)
</script>
