import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as authApi from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || userInfo.value?.nickname || '')
  const userId = computed(() => userInfo.value?.userId || userInfo.value?.id || null)

  async function login(credentials) {
    const res = await authApi.login(credentials)
    // Backend: { code: 200, data: { token, userId, username } }
    const payload = res.data || res
    token.value = payload.token
    userInfo.value = {
      userId: payload.userId,
      username: payload.username,
      ...(payload.user || payload.userInfo || {}),
    }
    localStorage.setItem('token', payload.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    return res
  }

  async function register(credentials) {
    const res = await authApi.register(credentials)
    const payload = res.data || res
    token.value = payload.token
    userInfo.value = {
      userId: payload.userId,
      username: payload.username,
      ...(payload.user || payload.userInfo || {}),
    }
    localStorage.setItem('token', payload.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    username,
    userId,
    login,
    register,
    logout,
  }
})
