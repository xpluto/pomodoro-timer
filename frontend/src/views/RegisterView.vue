<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 px-4">
    <div class="w-full max-w-md">
      <div class="text-center mb-8">
        <span class="text-5xl">🍅</span>
        <h1 class="text-2xl font-bold text-gray-800 mt-4">Create Account</h1>
        <p class="text-gray-500 mt-1">Start tracking your focus sessions</p>
      </div>

      <form @submit.prevent="handleRegister" class="bg-white rounded-2xl shadow-sm border border-gray-200 p-8 space-y-5">
        <div v-if="error" class="bg-red-50 text-red-600 text-sm rounded-lg p-3">{{ error }}</div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Username</label>
          <input v-model="form.username" type="text" required
            class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
          <input v-model="form.email" type="email" required
            class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Password</label>
          <input v-model="form.password" type="password" required minlength="6"
            class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Confirm Password</label>
          <input v-model="form.confirmPassword" type="password" required
            class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-400 focus:border-transparent outline-none transition"
            :class="{ 'border-red-400': form.confirmPassword && form.password !== form.confirmPassword }" />
          <p v-if="form.confirmPassword && form.password !== form.confirmPassword"
            class="text-red-500 text-xs mt-1">Passwords do not match</p>
        </div>

        <button type="submit" :disabled="loading || (!!form.confirmPassword && form.password !== form.confirmPassword)"
          class="w-full py-2.5 bg-red-500 text-white font-medium rounded-lg hover:bg-red-600 transition disabled:opacity-50">
          {{ loading ? 'Creating account...' : 'Sign Up' }}
        </button>

        <p class="text-center text-sm text-gray-500">
          Already have an account?
          <RouterLink to="/login" class="text-red-500 hover:text-red-600 font-medium">Sign in</RouterLink>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const error = ref('')
const form = reactive({ username: '', email: '', password: '', confirmPassword: '' })

async function handleRegister() {
  if (form.password !== form.confirmPassword) {
    error.value = 'Passwords do not match'
    return
  }
  loading.value = true
  error.value = ''
  try {
    const result = await authStore.register(form.username.trim(), form.email.trim(), form.password)
    if (result.success) {
      router.push({ name: 'login', query: { registered: 'true' } })
    } else {
      error.value = result.message || 'Registration failed'
    }
  } catch {
    error.value = 'Network error, please try again'
  } finally {
    loading.value = false
  }
}
</script>
