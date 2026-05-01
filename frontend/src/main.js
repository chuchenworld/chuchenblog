import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router'
import App from './App.vue'
import './style.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 全局错误处理 - 捕获 Vue 应用内部错误
app.config.errorHandler = (err, instance, info) => {
  // 判断是否为外部脚本错误（如浏览器插件）
  const isExternalError = err.stack && (
    err.stack.includes('stadium.js') ||
    err.stack.includes('chrome-extension') ||
    err.stack.includes('extension://') ||
    err.stack.includes('moz-extension')
  )
  
  if (isExternalError) {
    // 外部脚本错误，静默处理，不影响页面正常运行
    console.warn('[外部脚本错误] 已静默处理:', err.message)
    return
  }
  
  // 内部错误，正常输出日志
  console.error('[Vue 错误]', err)
  console.error('[错误信息]', info)
  
  // 如果有组件实例，可以显示友好的错误提示
  if (instance) {
    console.error('[错误组件]', instance.$options.name || '未知组件')
  }
}

// 全局未捕获的 Promise 拒绝处理
window.addEventListener('unhandledrejection', (event) => {
  const reason = event.reason
  const isExternalError = reason && reason.stack && (
    reason.stack.includes('stadium.js') ||
    reason.stack.includes('chrome-extension') ||
    reason.stack.includes('extension://') ||
    reason.stack.includes('moz-extension')
  )
  
  if (isExternalError) {
    console.warn('[外部 Promise 错误] 已静默处理:', reason.message)
    event.preventDefault()
    return
  }
  
  console.error('[未捕获的 Promise 拒绝]', reason)
  event.preventDefault()
})

// 全局同步错误捕获
window.addEventListener('error', (event) => {
  const filename = event.filename || ''
  
  // 判断是否为外部脚本错误
  const isExternalError = (
    filename.includes('stadium.js') ||
    filename.includes('chrome-extension') ||
    filename.includes('extension://') ||
    filename.includes('moz-extension')
  )
  
  if (isExternalError) {
    console.warn('[外部脚本同步错误] 已静默处理:', event.message)
    event.preventDefault()
    return
  }
  
  console.error('[全局同步错误]', event.message, event.filename, event.lineno, event.colno)
})

app.mount('#app')