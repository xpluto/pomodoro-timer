<template>
  <header class="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between">
    <div class="flex items-center gap-2">
      <span class="text-2xl">🍅</span>
      <span class="text-lg font-semibold text-gray-800">Pomodoro</span>
    </div>
    <nav class="flex items-center gap-6">
      <RouterLink to="/" class="text-gray-600 hover:text-red-500 transition-colors" active-class="text-red-500 font-medium">Timer</RouterLink>
      <RouterLink to="/stats" class="text-gray-600 hover:text-red-500 transition-colors" active-class="text-red-500 font-medium">Stats</RouterLink>
      <RouterLink to="/leaderboard" class="text-gray-600 hover:text-red-500 transition-colors" active-class="text-red-500 font-medium">Rank</RouterLink>
      <RouterLink to="/settings" class="text-gray-600 hover:text-red-500 transition-colors" active-class="text-red-500 font-medium">Settings</RouterLink>
      <button @click="handleLogout" class="text-gray-400 hover:text-red-500 transition-colors">Logout</button>
    </nav>
  </header>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push({ name: 'login' })
}
</script>
