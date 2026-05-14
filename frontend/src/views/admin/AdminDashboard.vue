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
            <h3 class="text-2xl font-bold text-gray-800 mt-1">{{ stats.totalPosts || 0 }}</h3>
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
            <h3 class="text-2xl font-bold text-gray-800 mt-1">{{ formatNumber(stats.totalViews || 0) }}</h3>
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
            <h3 class="text-2xl font-bold text-gray-800 mt-1">{{ stats.totalUsers || 0 }}</h3>
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
            <h3 class="text-2xl font-bold text-gray-800 mt-1">{{ stats.totalComments || 0 }}</h3>
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
          <div class="space-y-4" v-if="stats.recentPosts && stats.recentPosts.length > 0">
            <div class="flex items-start" v-for="post in stats.recentPosts" :key="post.id">
              <div class="w-12 h-12 bg-gray-200 rounded flex items-center justify-center text-gray-500 mr-4">
                <el-icon><Document /></el-icon>
              </div>
              <div class="flex-1">
                <h3 class="font-medium text-gray-800">{{ post.title }}</h3>
                <p class="text-sm text-gray-500 mt-1">{{ formatDate(post.createTime) }}</p>
              </div>
              <div class="text-sm text-gray-500">
                <el-icon class="mr-1"><View /></el-icon> {{ post.viewCount || 0 }}
              </div>
            </div>
          </div>
          <div v-else class="text-center py-8">
            <p class="text-gray-500">暂无文章</p>
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
import request from '@/utils/request'

const username = ref('')
const stats = ref({
  totalPosts: 0,
  totalViews: 0,
  totalUsers: 0,
  totalComments: 0,
  recentPosts: []
})

onMounted(() => {
  username.value = localStorage.getItem('username') || '管理员'
  fetchDashboardStats()
})

async function fetchDashboardStats() {
  try {
    const response = await request.get('/admin/dashboard')
    if (response.data) {
      stats.value = response.data
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  }
}

function formatNumber(num) {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}
</script>

<style scoped>
/* 移动端适配 */
@media (max-width: 768px) {
  .grid-cols-1.md\\:grid-cols-2.lg\\:grid-cols-4 {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .grid-cols-1.lg\\:grid-cols-2 {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .bg-white.rounded-lg.shadow.p-6 {
    padding: 12px;
  }

  .text-2xl.font-bold {
    font-size: 1.25rem;
  }

  .text-lg.font-semibold {
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .grid-cols-1.md\\:grid-cols-2.lg\\:grid-cols-4 {
    grid-template-columns: 1fr;
  }

  .bg-white.rounded-lg.shadow.p-6 {
    padding: 10px;
  }

  .w-12.h-12 {
    width: 36px;
    height: 36px;
  }

  .text-xl {
    font-size: 1rem;
  }
}
</style>
