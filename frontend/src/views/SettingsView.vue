<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold text-gray-800">Settings</h1>

    <div v-if="loading" class="text-center py-12 text-gray-400">Loading settings...</div>

    <form v-else @submit.prevent="handleSave" class="space-y-6">
      <!-- Timer Durations -->
      <div class="bg-white rounded-2xl border border-gray-200 p-6 space-y-4">
        <h2 class="text-lg font-semibold text-gray-800">Timer</h2>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Work Duration (min)</label>
            <input v-model.number="form.workMinutes" type="number" min="1" max="120"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Short Break (min)</label>
            <input v-model.number="form.shortBreakMinutes" type="number" min="1" max="30"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Long Break (min)</label>
            <input v-model.number="form.longBreakMinutes" type="number" min="1" max="60"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Long Break Every N</label>
            <input v-model.number="form.longBreakInterval" type="number" min="1" max="10"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition" />
          </div>
        </div>
      </div>

      <!-- Auto-start Toggles -->
      <div class="bg-white rounded-2xl border border-gray-200 p-6 space-y-4">
        <h2 class="text-lg font-semibold text-gray-800">Automation</h2>

        <label class="flex items-center justify-between cursor-pointer">
          <span class="text-gray-700">Auto-start break</span>
          <input v-model="form.autoStartBreak" type="checkbox" class="sr-only peer" />
          <div class="w-11 h-6 bg-gray-200 rounded-full peer peer-checked:bg-red-400 relative after:content-[''] after:absolute after:top-0.5 after:left-0.5 after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:after:translate-x-5" />
        </label>

        <label class="flex items-center justify-between cursor-pointer">
          <span class="text-gray-700">Auto-start work</span>
          <input v-model="form.autoStartWork" type="checkbox" class="sr-only peer" />
          <div class="w-11 h-6 bg-gray-200 rounded-full peer peer-checked:bg-red-400 relative after:content-[''] after:absolute after:top-0.5 after:left-0.5 after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:after:translate-x-5" />
        </label>
      </div>

      <!-- Notifications -->
      <div class="bg-white rounded-2xl border border-gray-200 p-6 space-y-4">
        <h2 class="text-lg font-semibold text-gray-800">Notifications</h2>

        <label class="flex items-center justify-between cursor-pointer">
          <span class="text-gray-700">Sound</span>
          <input v-model="form.soundEnabled" type="checkbox" class="sr-only peer" />
          <div class="w-11 h-6 bg-gray-200 rounded-full peer peer-checked:bg-red-400 relative after:content-[''] after:absolute after:top-0.5 after:left-0.5 after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:after:translate-x-5" />
        </label>

        <label class="flex items-center justify-between cursor-pointer">
          <span class="text-gray-700">Browser notifications</span>
          <input v-model="form.notificationEnabled" type="checkbox" class="sr-only peer" />
          <div class="w-11 h-6 bg-gray-200 rounded-full peer peer-checked:bg-red-400 relative after:content-[''] after:absolute after:top-0.5 after:left-0.5 after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:after:translate-x-5" />
        </label>
      </div>

      <!-- Actions -->
      <div class="flex items-center gap-4">
        <button type="submit" :disabled="saving"
          class="px-6 py-2.5 bg-red-500 text-white font-medium rounded-lg hover:bg-red-600 transition disabled:opacity-50">
          {{ saving ? 'Saving...' : 'Save Settings' }}
        </button>
        <span v-if="saved" class="text-green-600 text-sm">Settings saved!</span>
        <span v-if="error" class="text-red-600 text-sm">{{ error }}</span>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useSettingsStore } from '@/stores/settings'
import type { UserSettings } from '@/types'

const settingsStore = useSettingsStore()
const loading = ref(true)
const saving = ref(false)
const saved = ref(false)
const error = ref('')

const form = reactive({
  workMinutes: 25,
  shortBreakMinutes: 5,
  longBreakMinutes: 15,
  longBreakInterval: 4,
  autoStartBreak: false,
  autoStartWork: false,
  soundEnabled: true,
  notificationEnabled: true
})

function loadFormFromSettings(s: UserSettings) {
  form.workMinutes = Math.round(s.workDuration / 60)
  form.shortBreakMinutes = Math.round(s.shortBreak / 60)
  form.longBreakMinutes = Math.round(s.longBreak / 60)
  form.longBreakInterval = s.longBreakInterval
  form.autoStartBreak = s.autoStartBreak
  form.autoStartWork = s.autoStartWork
  form.soundEnabled = s.soundEnabled
  form.notificationEnabled = s.notificationEnabled
}

function formToSettings(): UserSettings {
  return {
    workDuration: form.workMinutes * 60,
    shortBreak: form.shortBreakMinutes * 60,
    longBreak: form.longBreakMinutes * 60,
    longBreakInterval: form.longBreakInterval,
    autoStartBreak: form.autoStartBreak,
    autoStartWork: form.autoStartWork,
    soundEnabled: form.soundEnabled,
    notificationEnabled: form.notificationEnabled
  }
}

async function handleSave() {
  saving.value = true
  saved.value = false
  error.value = ''

  if (form.workMinutes < 1 || form.workMinutes > 120) {
    error.value = 'Work duration must be between 1 and 120 minutes'
    saving.value = false
    return
  }
  if (form.shortBreakMinutes < 1 || form.shortBreakMinutes > 30) {
    error.value = 'Short break must be between 1 and 30 minutes'
    saving.value = false
    return
  }
  if (form.longBreakMinutes < 1 || form.longBreakMinutes > 60) {
    error.value = 'Long break must be between 1 and 60 minutes'
    saving.value = false
    return
  }
  if (form.longBreakInterval < 1 || form.longBreakInterval > 10) {
    error.value = 'Long break interval must be between 1 and 10'
    saving.value = false
    return
  }

  try {
    const result = await settingsStore.updateSettings(formToSettings())
    if (result.success) {
      saved.value = true
      setTimeout(() => { saved.value = false }, 3000)
    } else {
      error.value = result.message || 'Failed to save settings'
    }
  } catch {
    error.value = 'Network error, please try again'
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await settingsStore.fetchSettings()
  loadFormFromSettings(settingsStore.settings)
  loading.value = false
})
</script>
