<template>
  <div class="flex h-screen bg-gray-50 overflow-hidden">
    <!-- 移动端遮罩层 -->
    <div
      v-if="sidebarOpen"
      class="fixed inset-0 bg-black bg-opacity-50 z-20 lg:hidden"
      @click="sidebarOpen = false"
    ></div>

    <!-- 侧边栏 -->
    <div
      class="w-64 bg-white border-r border-gray-200 flex flex-col fixed h-full z-30 transition-transform duration-300 lg:translate-x-0"
      :class="{ 'translate-x-0': sidebarOpen, '-translate-x-full': !sidebarOpen }"
    >
      <!-- Logo -->
      <div class="h-16 flex items-center justify-center border-b border-gray-200">
        <div class="text-xl font-bold text-blue-600">管理后台</div>
      </div>

      <!-- 菜单 -->
      <div class="flex-1 overflow-y-auto py-4">
        <nav class="px-4 space-y-1">
          <router-link
            to="/admin/dashboard"
            class="flex items-center px-4 py-3 text-gray-700 rounded-lg hover:bg-gray-100 transition duration-200"
            active-class="bg-blue-50 text-blue-600 font-medium"
            @click="closeSidebarOnMobile"
          >
            <el-icon class="mr-3"><HomeFilled /></el-icon>
            <span>仪表盘</span>
          </router-link>

          <router-link
            to="/admin/posts"
            class="flex items-center px-4 py-3 text-gray-700 rounded-lg hover:bg-gray-100 transition duration-200"
            active-class="bg-blue-50 text-blue-600 font-medium"
            @click="closeSidebarOnMobile"
          >
            <el-icon class="mr-3"><Document /></el-icon>
            <span>文章管理</span>
          </router-link>

          <router-link
            to="/admin/photos"
            class="flex items-center px-4 py-3 text-gray-700 rounded-lg hover:bg-gray-100 transition duration-200"
            active-class="bg-blue-50 text-blue-600 font-medium"
            @click="closeSidebarOnMobile"
          >
            <el-icon class="mr-3"><Picture /></el-icon>
            <span>摄影管理</span>
          </router-link>

          <router-link
            to="/admin/settings"
            class="flex items-center px-4 py-3 text-gray-700 rounded-lg hover:bg-gray-100 transition duration-200"
            active-class="bg-blue-50 text-blue-600 font-medium"
            @click="closeSidebarOnMobile"
          >
            <el-icon class="mr-3"><Setting /></el-icon>
            <span>系统设置</span>
          </router-link>

          <router-link
            to="/admin/profile"
            class="flex items-center px-4 py-3 text-gray-700 rounded-lg hover:bg-gray-100 transition duration-200"
            active-class="bg-blue-50 text-blue-600 font-medium"
            @click="closeSidebarOnMobile"
          >
            <el-icon class="mr-3"><User /></el-icon>
            <span>个人设置</span>
          </router-link>

          <router-link
            to="/admin/users"
            class="flex items-center px-4 py-3 text-gray-700 rounded-lg hover:bg-gray-100 transition duration-200"
            active-class="bg-blue-50 text-blue-600 font-medium"
            @click="closeSidebarOnMobile"
          >
            <el-icon class="mr-3"><UserFilled /></el-icon>
            <span>用户管理</span>
          </router-link>

          <router-link
            to="/admin/comments"
            class="flex items-center px-4 py-3 text-gray-700 rounded-lg hover:bg-gray-100 transition duration-200"
            active-class="bg-blue-50 text-blue-600 font-medium"
            @click="closeSidebarOnMobile"
          >
            <el-icon class="mr-3"><Message /></el-icon>
            <span>留言板管理</span>
          </router-link>

          <router-link
            to="/admin/links"
            class="flex items-center px-4 py-3 text-gray-700 rounded-lg hover:bg-gray-100 transition duration-200"
            active-class="bg-blue-50 text-blue-600 font-medium"
            @click="closeSidebarOnMobile"
          >
            <el-icon class="mr-3"><Link /></el-icon>
            <span>友链管理</span>
          </router-link>
        </nav>
      </div>

      <!-- 底部信息 -->
      <div class="p-4 border-t border-gray-200">
        <div class="flex items-center">
          <div class="w-8 h-8 rounded-full bg-gray-300 flex items-center justify-center text-gray-700">
            {{ username.charAt(0).toUpperCase() }}
          </div>
          <div class="ml-3">
            <p class="text-sm font-medium text-gray-700">{{ username }}</p>
            <p class="text-xs text-gray-500">{{ role }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col lg:ml-64">
      <!-- 顶部导航栏 -->
      <header class="bg-white border-b border-gray-200 h-16 flex items-center justify-between px-4 lg:px-6 sticky top-0 z-10">
        <div class="flex items-center">
          <!-- 移动端汉堡菜单按钮 -->
          <button
            class="lg:hidden p-2 mr-2 rounded-lg hover:bg-gray-100"
            @click="sidebarOpen = !sidebarOpen"
          >
            <el-icon class="text-xl"><Menu /></el-icon>
          </button>

          <el-breadcrumb separator="/" class="text-sm hidden sm:block">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRoute.meta.title || '页面' }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="flex items-center space-x-2 lg:space-x-4">
          <el-dropdown @command="handleCommand">
            <span class="flex items-center cursor-pointer">
              <div class="w-8 h-8 rounded-full bg-gray-300 flex items-center justify-center text-gray-700">
                {{ username.charAt(0).toUpperCase() }}
              </div>
              <span class="ml-2 text-sm font-medium text-gray-700 hidden sm:inline">{{ username }}</span>
              <el-icon class="ml-1"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="home">首页</el-dropdown-item>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="flex-1 p-4 lg:p-6 overflow-y-auto">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, Document, Picture, Setting, ArrowDown, User, UserFilled, Message, Link, Menu } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const username = ref('')
const role = ref('')
const sidebarOpen = ref(false)

const currentRoute = computed(() => route)

onMounted(() => {
  username.value = localStorage.getItem('nickname') || localStorage.getItem('username') || '未命名用户'
  role.value = localStorage.getItem('role') || 'admin'

  // 在桌面端默认展开侧边栏
  if (window.innerWidth >= 1024) {
    sidebarOpen.value = true
  }

  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

const handleResize = () => {
  if (window.innerWidth >= 1024) {
    sidebarOpen.value = true
  }
}

const closeSidebarOnMobile = () => {
  if (window.innerWidth < 1024) {
    sidebarOpen.value = false
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('nickname')
    localStorage.removeItem('role')

    ElMessage.success('退出登录成功')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/admin/profile')
  } else if (command === 'home') {
    router.push('/home')
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
