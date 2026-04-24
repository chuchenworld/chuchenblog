<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white p-8 rounded-lg shadow-md w-96">
      <h2 class="text-2xl font-bold mb-6 text-center">注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="mb-4">
          <label class="block text-gray-700 mb-2">用户名 <span class="text-gray-500 text-sm">(作为昵称展示)</span></label>
          <input v-model="form.nickname" type="text" class="w-full px-3 py-2 border rounded-lg" required />
        </div>
        <div class="mb-4">
          <label class="block text-gray-700 mb-2">手机号或邮箱</label>
          <input v-model="form.account" type="text" placeholder="请输入手机号或邮箱作为登录账号" class="w-full px-3 py-2 border rounded-lg" required />
        </div>
        <div class="mb-6">
          <label class="block text-gray-700 mb-2">密码</label>
          <input v-model="form.password" type="password" class="w-full px-3 py-2 border rounded-lg" required />
        </div>
        <button type="submit" class="w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600">
          注册
        </button>
      </form>
      <p class="mt-4 text-center text-gray-600">
        已有账号? <router-link to="/login" class="text-blue-500">登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const form = ref({
  nickname: '',
  account: '',
  password: ''
})

async function handleRegister() {
  try {
    await authStore.register(form.value)
    alert('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    alert('注册失败: ' + error.message)
  }
}
</script>