<template>
  <el-container class="main-container">
    <el-header class="header">
      <div class="header-content">
        <h1 class="logo" @click="$router.push('/')">初尘博客</h1>
        <div class="menu-container">
          <el-menu mode="horizontal" :ellipsis="false" router>
            <el-menu-item index="/">首页</el-menu-item>

            <li class="el-menu-item category-dropdown" @mouseenter="showCategoryMenu = true" @mouseleave="hideCategoryMenu">
              <span class="dropdown-trigger" @click="goToArticles">
                文章
                <el-icon class="dropdown-icon" :class="{ 'is-open': showCategoryMenu }" @click.stop="toggleCategoryMenu"><arrow-down /></el-icon>
              </span>
              <div class="dropdown-menu" v-show="showCategoryMenu" @mouseenter="showCategoryMenu = true">
                <div class="dropdown-content">
                  <div class="dropdown-item" v-for="cat in categories" :key="cat.id" @click="goToCategory(cat.id)">
                    <span class="category-name">{{ cat.name }}</span>
                    <span class="category-count">{{ cat.articleCount }}</span>
                  </div>
                </div>
              </div>
            </li>

            <el-menu-item index="/life">生活</el-menu-item>
            <el-menu-item index="/about">个人</el-menu-item>
            <el-menu-item index="/messages">留言</el-menu-item>
            <el-menu-item index="/links">友情链接</el-menu-item>
            <el-menu-item v-if="authStore.isAdmin" index="/admin">管理后台</el-menu-item>
            <el-menu-item v-if="!authStore.isAuthenticated" index="/login">登录</el-menu-item>
            <el-menu-item v-else>
              <el-dropdown @command="handleCommand">
                <span class="user-info">
                  {{ authStore.username }}
                  <el-icon><arrow-down /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                    <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-menu-item>
          </el-menu>
        </div>
      </div>
    </el-header>
    <el-main class="main-content">
      <slot />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'

const router = useRouter()
const authStore = useAuthStore()
const categories = ref([])
const showCategoryMenu = ref(false)

async function fetchCategories() {
  try {
    const res = await request.get('/categories')
    categories.value = res.data || []
  } catch (err) {
    console.error('获取分类失败:', err)
    // 使用默认分类数据
    categories.value = [
      { id: 1, name: '前端开发', articleCount: 0 },
      { id: 2, name: '后端开发', articleCount: 0 },
      { id: 3, name: 'DevOps', articleCount: 0 },
      { id: 4, name: 'Life', articleCount: 0 },
      { id: 5, name: 'Others', articleCount: 0 }
    ]
  }
}

function goToCategory(categoryId) {
  showCategoryMenu.value = false
  router.push(`/categories/${categoryId}`)
}

function goToArticles() {
  router.push('/articles')
}

function toggleCategoryMenu() {
  showCategoryMenu.value = !showCategoryMenu.value
}

function hideCategoryMenu() {
  setTimeout(() => {
    showCategoryMenu.value = false
  }, 200)
}

function handleCommand(command) {
  if (command === 'logout') {
    authStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.main-container {
  min-height: 100vh;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  padding: 0;
  background-color: transparent !important;
  background: none !important;
  box-shadow: none !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
  cursor: pointer;
}

.menu-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.main-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 120px 20px 60px;
  background-color: transparent !important;
}

.category-dropdown {
  position: relative !important;
}

.dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.dropdown-icon {
  transition: transform 0.3s;
  font-size: 12px;
}

.dropdown-icon.is-open {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1001;
}

.dropdown-content {
  background: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  padding: 8px 0;
  min-width: 180px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.dropdown-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  cursor: pointer;
  transition: background 0.3s;
  color: #ffffff;
}

.dropdown-item:hover {
  background: rgba(255, 255, 255, 0.15);
}

.category-name {
  font-size: 14px;
}

.category-count {
  font-size: 12px;
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
}

@media (max-width: 768px) {
  .main-content {
    padding: 100px 15px 40px;
  }
}

:deep(.el-menu) {
  background-color: transparent !important;
  border-bottom: none !important;
  display: flex !important;
  align-items: center !important;
  width: auto !important;
}

:deep(.el-menu-item) {
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
  margin: 0 5px !important;
}

:deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

:deep(.el-menu-item.is-active) {
  color: #ffffff !important;
  background-color: rgba(255, 255, 255, 0.2) !important;
}

:deep(.el-dropdown-menu) {
  background-color: rgba(0, 0, 0, 0.8) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
}

:deep(.el-dropdown-item) {
  color: #ffffff !important;
}

:deep(.el-dropdown-item:hover) {
  background-color: rgba(255, 255, 255, 0.2) !important;
}
</style>