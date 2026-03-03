<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 px-4">
    <div class="w-full max-w-md">
      <div class="text-center mb-8">
        <span class="text-5xl">🍅</span>
        <h1 class="text-2xl font-bold text-gray-800 mt-4">Welcome Back</h1>
        <p class="text-gray-500 mt-1">Sign in to your Pomodoro Timer</p>
      </div>

      <form @submit.prevent="handleLogin" class="bg-white rounded-2xl shadow-sm border border-gray-200 p-8 space-y-5">
        <div v-if="successMessage" class="bg-green-50 text-green-600 text-sm rounded-lg p-3">{{ successMessage }}</div>
        <div v-if="error" class="bg-red-50 text-red-600 text-sm rounded-lg p-3">{{ error }}</div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Username</label>
          <input v-model="form.username" type="text" required
            class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Password</label>
          <input v-model="form.password" type="password" required
            class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition" />
        </div>

        <button type="submit" :disabled="loading"
          class="w-full py-2.5 bg-red-500 text-white font-medium rounded-lg hover:bg-red-600 transition disabled:opacity-50">
          {{ loading ? 'Signing in...' : 'Sign In' }}
        </button>

        <p class="text-center text-sm text-gray-500">
          Don't have an account?
          <RouterLink to="/register" class="text-red-500 hover:text-red-600 font-medium">Sign up</RouterLink>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loading = ref(false)
const error = ref('')
const successMessage = ref('')
const form = reactive({ username: '', password: '' })

onMounted(() => {
  if (route.query.registered === 'true') {
    successMessage.value = 'Account created successfully. Please sign in.'
  }
})

async function handleLogin() {
  loading.value = true
  error.value = ''
  successMessage.value = ''
  try {
    const result = await authStore.login(form.username.trim(), form.password)
    if (result.success) {
      router.push('/')
    } else {
      error.value = result.message || 'Login failed'
    }
  } catch {
    error.value = 'Network error, please try again'
  } finally {
    loading.value = false
  }
}
</script>
