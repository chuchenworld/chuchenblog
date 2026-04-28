import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    console.log('【请求拦截器】发送请求:', config.method.toUpperCase(), config.url, '参数:', config.params || config.data)
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
      console.log('【请求拦截器】添加认证令牌')
    } else if (authStore.tempUserId) {
      config.headers['X-Temp-User-Id'] = authStore.tempUserId
      console.log('【请求拦截器】添加临时用户 ID')
    }
    return config
  },
  error => {
    console.error('【请求拦截器】请求发送失败:', error.message)
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    console.log('【响应拦截器】收到响应:', response.config.method.toUpperCase(), response.config.url, '状态码:', response.status)
    const res = response.data
    if (res.code !== 200) {
      console.error('【响应拦截器】请求失败:', res.code, res.message)
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    console.log('【响应拦截器】请求成功:', res.message || '操作成功')
    return res
  },
  error => {
    console.error('【响应拦截器】响应错误:', error.message)
    if (error.response) {
      const { status, config } = error.response
      console.error('【响应拦截器】错误状态码:', status, '请求:', config.method.toUpperCase(), config.url)
      if (status === 401) {
        console.error('【响应拦截器】未授权，跳转到登录页面')
        const authStore = useAuthStore()
        authStore.logout()
        router.push('/login')
      } else if (status === 403) {
        console.error('【响应拦截器】没有权限访问')
      }
    }
    return Promise.reject(error)
  }
)

export default request