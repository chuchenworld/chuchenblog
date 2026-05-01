import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

// 生成唯一的 UUID
function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const role = ref(localStorage.getItem('role') || '')
  
  // 游客模式相关
  const tempUserId = ref(localStorage.getItem('temp_user_id') || '')
  
  // 初始化临时用户 ID
  if (!tempUserId.value) {
    tempUserId.value = generateUUID()
    localStorage.setItem('temp_user_id', tempUserId.value)
  }

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'admin')
  const isGuest = computed(() => !isAuthenticated.value && !!tempUserId.value)

  async function login(loginForm) {
    const res = await request.post('/auth/login', loginForm)
    token.value = res.data.token
    username.value = res.data.username
    nickname.value = res.data.nickname
    role.value = res.data.role
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', res.data.username)
    localStorage.setItem('nickname', res.data.nickname)
    localStorage.setItem('role', res.data.role)
    return res
  }

  async function register(registerForm) {
    return await request.post('/auth/register', registerForm)
  }

  function logout() {
    token.value = ''
    username.value = ''
    nickname.value = ''
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('nickname')
    localStorage.removeItem('role')
  }

  return {
    token,
    username,
    nickname,
    role,
    tempUserId,
    isAuthenticated,
    isAdmin,
    isGuest,
    login,
    register,
    logout
  }
})