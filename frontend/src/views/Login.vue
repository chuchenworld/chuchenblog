<template>
  <div class="login-container">
    <!-- 遮罩层 -->
    <div class="overlay"></div>

    <!-- 登录卡片 -->
    <div class="w-full max-w-md p-8 bg-white/10 backdrop-blur-xl border border-white/20 rounded-2xl shadow-2xl relative z-10">
      <!-- 动态标题区域 -->
      <div class="text-center mb-8">
        <transition name="fade" mode="out-in">
          <div :key="isLogin">
            <h2 class="text-2xl font-bold text-white">{{ isLogin ? '欢迎回来' : '加入我们' }}</h2>
            <p class="text-gray-300 mt-2">{{ isLogin ? '请输入您的账号信息以继续' : '创建您的账户，浏览我的博客吧' }}</p>
          </div>
        </transition>
      </div>

      <!-- 选项卡 -->
      <div class="flex mb-6 border-b border-white/20">
        <button
          @click="isLogin = true"
          class="flex-1 py-3 text-center transition-all duration-300"
          :class="isLogin ? 'text-white border-b-2 border-blue-400 font-medium' : 'text-gray-300 hover:text-white'"
        >
          账号登录
        </button>
        <button
          @click="isLogin = false"
          class="flex-1 py-3 text-center transition-all duration-300"
          :class="!isLogin ? 'text-white border-b-2 border-blue-400 font-medium' : 'text-gray-300 hover:text-white'"
        >
          用户注册
        </button>
      </div>

      <!-- 登录表单 -->
      <form v-if="isLogin" class="space-y-6" @submit.prevent="handleLogin">
        <div>
          <label for="login-account" class="block text-sm font-medium text-gray-200 mb-1">手机号或邮箱</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <el-icon class="text-gray-300"><User /></el-icon>
            </div>
            <input
              type="text"
              id="login-account"
              v-model="loginForm.account"
              class="w-full pl-10 pr-3 py-2 bg-white/10 border-b border-white/30 rounded-lg focus:ring-2 focus:ring-blue-400 focus:border-blue-400 text-white placeholder-gray-400"
              placeholder="请输入手机号或邮箱"
              required
            />
          </div>
        </div>

        <div>
          <label for="login-password" class="block text-sm font-medium text-gray-200 mb-1">密码</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <el-icon class="text-gray-300"><Lock /></el-icon>
            </div>
            <input
              type="password"
              id="login-password"
              v-model="loginForm.password"
              class="w-full pl-10 pr-3 py-2 bg-white/10 border-b border-white/30 rounded-lg focus:ring-2 focus:ring-blue-400 focus:border-blue-400 text-white placeholder-gray-400"
              placeholder="请输入密码"
              required
            />
          </div>
        </div>

        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <input
              type="checkbox"
              id="login-remember"
              v-model="loginForm.remember"
              class="h-4 w-4 text-blue-400 focus:ring-blue-500 border-gray-300 rounded"
            />
            <label for="login-remember" class="ml-2 block text-sm text-gray-200">记住我</label>
          </div>
          <a href="#" class="text-sm text-blue-400 hover:text-blue-300">忘记密码?</a>
        </div>

        <el-button
          type="primary"
          :loading="loading"
          class="w-full bg-blue-600 hover:bg-blue-700 text-white"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登录' }}
        </el-button>
      </form>

      <!-- 注册表单 -->
      <form v-else class="space-y-6" @submit.prevent="handleRegister">
        <div>
          <label for="register-nickname" class="block text-sm font-medium text-gray-200 mb-1">用户名 <span class="text-gray-400 text-xs">(作为昵称展示)</span></label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <el-icon class="text-gray-300"><User /></el-icon>
            </div>
            <input
              type="text"
              id="register-nickname"
              v-model="registerForm.nickname"
              class="w-full pl-10 pr-3 py-2 bg-white/10 border-b border-white/30 rounded-lg focus:ring-2 focus:ring-blue-400 focus:border-blue-400 text-white placeholder-gray-400"
              placeholder="请输入用户名"
              required
            />
          </div>
        </div>

        <div>
          <label for="register-account" class="block text-sm font-medium text-gray-200 mb-1">手机号或邮箱</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <el-icon class="text-gray-300"><Message /></el-icon>
            </div>
            <input
              type="text"
              id="register-account"
              v-model="registerForm.account"
              class="w-full pl-10 pr-3 py-2 bg-white/10 border-b border-white/30 rounded-lg focus:ring-2 focus:ring-blue-400 focus:border-blue-400 text-white placeholder-gray-400"
              placeholder="请输入手机号或邮箱作为登录账号"
              required
            />
          </div>
        </div>

        <div>
          <label for="register-password" class="block text-sm font-medium text-gray-200 mb-1">密码</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <el-icon class="text-gray-300"><Lock /></el-icon>
            </div>
            <input
              type="password"
              id="register-password"
              v-model="registerForm.password"
              class="w-full pl-10 pr-3 py-2 bg-white/10 border-b border-white/30 rounded-lg focus:ring-2 focus:ring-blue-400 focus:border-blue-400 text-white placeholder-gray-400"
              placeholder="请输入密码"
              required
            />
          </div>
        </div>

        <div>
          <label for="register-confirm-password" class="block text-sm font-medium text-gray-200 mb-1">确认密码</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <el-icon class="text-gray-300"><Lock /></el-icon>
            </div>
            <input
              type="password"
              id="register-confirm-password"
              v-model="registerForm.confirmPassword"
              class="w-full pl-10 pr-3 py-2 bg-white/10 border-b border-white/30 rounded-lg focus:ring-2 focus:ring-blue-400 focus:border-blue-400 text-white placeholder-gray-400"
              placeholder="请确认密码"
              required
            />
          </div>
        </div>

        <el-button
          type="primary"
          :loading="loading"
          class="w-full bg-blue-600 hover:bg-blue-700 text-white"
          @click="handleRegister"
        >
          {{ loading ? '注册中...' : '注册' }}
        </el-button>
      </form>

      <!-- 游客登录按钮 - 仅在登录模式下显示 -->
      <div v-if="isLogin" class="mt-8 pt-6 border-t border-white/10">
        <button
          @click="handleGuestLogin"
          class="w-full py-3 bg-transparent border border-white/30 rounded-lg text-white hover:bg-white/10 transition-all duration-300"
        >
          以游客身份浏览
        </button>
        <p class="text-center text-xs text-gray-400 mt-3">不需要账户？立即体验</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const isLogin = ref(true)
const loading = ref(false)

const loginForm = ref({
  account: '',
  password: '',
  remember: false
})

const registerForm = ref({
  nickname: '',
  account: '',
  password: '',
  confirmPassword: ''
})

const handleLogin = async () => {
  loading.value = true
  try {
    await authStore.login({
      account: loginForm.value.account,
      password: loginForm.value.password
    })

    ElMessage.success('登录成功')

    if (authStore.isAdmin) {
      router.push('/admin/dashboard')
    } else {
      router.push('/home')
    }
  } catch (error) {
    ElMessage.error('登录失败，请检查账号和密码')
    console.error('登录错误:', error)
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    await authStore.register({
      nickname: registerForm.value.nickname,
      account: registerForm.value.account,
      password: registerForm.value.password
    })

    ElMessage.success('注册成功，请登录')
    isLogin.value = true
  } catch (error) {
    ElMessage.error('注册失败，请稍后重试')
    console.error('注册错误:', error)
  } finally {
    loading.value = false
  }
}

const handleGuestLogin = () => {
  ElMessage.info('以游客身份登录')
  router.push('/home')
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('@/assets/login.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: #1a1a1a;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 1;
}

.login-container > div:last-of-type {
  z-index: 2;
}

::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

@media (max-width: 768px) {
  .max-w-md {
    max-width: 90%;
  }

  h2 {
    font-size: 1.5rem !important;
  }
}

form {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
