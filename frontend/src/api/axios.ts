import axios from 'axios'

function clearAuthAndRedirect() {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  window.location.href = '/login'
}

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let refreshPromise: Promise<{ data: { success: boolean; data: { accessToken: string; refreshToken: string } } }> | null = null

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      if (!refreshPromise) {
        const refreshToken = localStorage.getItem('refreshToken')
        if (refreshToken) {
          refreshPromise = axios
            .post('/api/auth/refresh', { refreshToken })
            .finally(() => {
              refreshPromise = null
            })
        }
      }

      if (refreshPromise) {
        try {
          const { data } = await refreshPromise
          if (data.success) {
            localStorage.setItem('accessToken', data.data.accessToken)
            localStorage.setItem('refreshToken', data.data.refreshToken)
            originalRequest.headers.Authorization = `Bearer ${data.data.accessToken}`
            return api(originalRequest)
          } else {
            clearAuthAndRedirect()
          }
        } catch {
          clearAuthAndRedirect()
        }
      }
    }
    return Promise.reject(error)
  }
)

export default api
