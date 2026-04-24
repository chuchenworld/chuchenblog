<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-gray-800">仪表盘</h1>
      <p class="text-gray-500 mt-1">欢迎回来，{{ username }}！</p>
    </div>
    
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <!-- 统计卡片 -->
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">总文章数</p>
            <h3 class="text-2xl font-bold text-gray-800 mt-1">128</h3>
          </div>
          <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center text-blue-600">
            <el-icon class="text-xl"><Document /></el-icon>
          </div>
        </div>
        <div class="mt-4 flex items-center">
          <span class="text-green-600 text-sm font-medium flex items-center">
            <el-icon class="mr-1"><ArrowUp /></el-icon> 12%
          </span>
          <span class="text-gray-500 text-sm ml-2">较上月</span>
        </div>
      </div>
      
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">总访问量</p>
            <h3 class="text-2xl font-bold text-gray-800 mt-1">25,642</h3>
          </div>
          <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center text-green-600">
            <el-icon class="text-xl"><View /></el-icon>
          </div>
        </div>
        <div class="mt-4 flex items-center">
          <span class="text-green-600 text-sm font-medium flex items-center">
            <el-icon class="mr-1"><ArrowUp /></el-icon> 8%
          </span>
          <span class="text-gray-500 text-sm ml-2">较上月</span>
        </div>
      </div>
      
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">用户数</p>
            <h3 class="text-2xl font-bold text-gray-800 mt-1">42</h3>
          </div>
          <div class="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center text-purple-600">
            <el-icon class="text-xl"><User /></el-icon>
          </div>
        </div>
        <div class="mt-4 flex items-center">
          <span class="text-green-600 text-sm font-medium flex items-center">
            <el-icon class="mr-1"><ArrowUp /></el-icon> 5%
          </span>
          <span class="text-gray-500 text-sm ml-2">较上月</span>
        </div>
      </div>
      
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">留言数</p>
            <h3 class="text-2xl font-bold text-gray-800 mt-1">256</h3>
          </div>
          <div class="w-12 h-12 bg-yellow-100 rounded-full flex items-center justify-center text-yellow-600">
            <el-icon class="text-xl"><ChatLineSquare /></el-icon>
          </div>
        </div>
        <div class="mt-4 flex items-center">
          <span class="text-green-600 text-sm font-medium flex items-center">
            <el-icon class="mr-1"><ArrowUp /></el-icon> 15%
          </span>
          <span class="text-gray-500 text-sm ml-2">较上月</span>
        </div>
      </div>
    </div>
    
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 最近文章 -->
      <div class="bg-white rounded-lg shadow">
        <div class="p-6 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-800">最近文章</h2>
        </div>
        <div class="p-6">
          <div class="space-y-4">
            <div class="flex items-start">
              <div class="w-12 h-12 bg-gray-200 rounded flex items-center justify-center text-gray-500 mr-4">
                <el-icon><Document /></el-icon>
              </div>
              <div class="flex-1">
                <h3 class="font-medium text-gray-800">Vue 3 组合式 API 最佳实践</h3>
                <p class="text-sm text-gray-500 mt-1">2026-04-20</p>
              </div>
              <div class="text-sm text-gray-500">
                <el-icon class="mr-1"><View /></el-icon> 128
              </div>
            </div>
            <div class="flex items-start">
              <div class="w-12 h-12 bg-gray-200 rounded flex items-center justify-center text-gray-500 mr-4">
                <el-icon><Document /></el-icon>
              </div>
              <div class="flex-1">
                <h3 class="font-medium text-gray-800">Spring Boot 3 新特性介绍</h3>
                <p class="text-sm text-gray-500 mt-1">2026-04-18</p>
              </div>
              <div class="text-sm text-gray-500">
                <el-icon class="mr-1"><View /></el-icon> 96
              </div>
            </div>
            <div class="flex items-start">
              <div class="w-12 h-12 bg-gray-200 rounded flex items-center justify-center text-gray-500 mr-4">
                <el-icon><Document /></el-icon>
              </div>
              <div class="flex-1">
                <h3 class="font-medium text-gray-800">Tailwind CSS 实用技巧</h3>
                <p class="text-sm text-gray-500 mt-1">2026-04-15</p>
              </div>
              <div class="text-sm text-gray-500">
                <el-icon class="mr-1"><View /></el-icon> 84
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 访问统计 -->
      <div class="bg-white rounded-lg shadow">
        <div class="p-6 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-800">访问统计</h2>
        </div>
        <div class="p-6">
          <div class="h-64 bg-gray-50 rounded flex items-center justify-center">
            <p class="text-gray-500">图表区域</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document, View, User, ChatLineSquare, ArrowUp } from '@element-plus/icons-vue'

const username = ref('')

onMounted(() => {
  username.value = localStorage.getItem('username') || '管理员'
})
</script>
