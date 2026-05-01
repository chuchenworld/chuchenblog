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
    
    // 如果响应是数组或不包含 code 字段，直接返回数据（可能是后端直接返回的列表）
    if (Array.isArray(res) || !res.code) {
      console.log('【响应拦截器】请求成功（直接数据）')
      return { code: 200, message: 'success', data: res }
    }
    
    // 后端 Result 类使用 code: 200 表示成功
    if (res.code !== 200) {
      const errorMsg = res.message || res.msg || '请求失败'
      console.warn('【响应拦截器】业务逻辑失败:', res.code, errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
    
    console.log('【响应拦截器】请求成功:', res.message || '操作成功')
    return res
  },
  error => {
    // 提取具体的错误信息
    let errorMsg = '网络请求失败'
    if (error.response) {
      const { status, data, config } = error.response
      console.warn('【响应拦截器】HTTP错误:', status, '请求:', config.method.toUpperCase(), config.url)
      
      // 提取业务错误信息
      if (data) {
        errorMsg = data.message || data.msg || `HTTP ${status}`
      } else {
        errorMsg = `HTTP ${status}`
      }
      
      if (status === 401) {
        console.warn('【响应拦截器】未授权，跳转到登录页面')
        const authStore = useAuthStore()
        authStore.logout()
        router.push('/login')
      } else if (status === 403) {
        errorMsg = '没有权限访问'
      }
    } else if (error.message) {
      errorMsg = error.message
    }
    
    console.warn('【响应拦截器】错误详情:', errorMsg)
    return Promise.reject(new Error(errorMsg))
  }
)

export default request