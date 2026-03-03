<template>
  <AppLayout v-if="authStore.isAuthenticated">
    <RouterView />
  </AppLayout>
  <RouterView v-else />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import AppLayout from '@/components/layout/AppLayout.vue'

const authStore = useAuthStore()

onMounted(() => {
  if (authStore.isAuthenticated) {
    authStore.fetchUser()
  }
})
</script>
