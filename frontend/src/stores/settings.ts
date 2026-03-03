import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserSettings } from '@/types'
import { settingsApi } from '@/api/settings'

const DEFAULT_SETTINGS: UserSettings = {
  workDuration: 1500,
  shortBreak: 300,
  longBreak: 900,
  longBreakInterval: 4,
  autoStartBreak: false,
  autoStartWork: false,
  soundEnabled: true,
  notificationEnabled: true
}

export const useSettingsStore = defineStore('settings', () => {
  const settings = ref<UserSettings>({ ...DEFAULT_SETTINGS })

  async function fetchSettings() {
    try {
      const { data } = await settingsApi.get()
      if (data.success && data.data) {
        settings.value = data.data
      }
    } catch {
      // Use defaults on failure
    }
  }

  async function updateSettings(updated: UserSettings) {
    const { data } = await settingsApi.update(updated)
    if (data.success && data.data) {
      settings.value = data.data
    }
    return data
  }

  return { settings, fetchSettings, updateSettings }
})
