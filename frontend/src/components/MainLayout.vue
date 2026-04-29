<template>
  <el-container class="main-container">
    <el-header class="header">
      <div class="header-content">
        <h1 class="logo" @click="$router.push('/')">初尘博客</h1>
        
        <!-- PC端菜单 -->
        <div class="menu-container desktop-menu">
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
            <el-menu-item v-else index="/profile">
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

        <!-- 移动端汉堡菜单按钮 -->
        <button class="mobile-menu-btn" @click="toggleMobileMenu" aria-label="菜单">
          <span></span>
          <span></span>
          <span></span>
        </button>
      </div>

      <!-- 移动端菜单面板 -->
      <transition name="slide-down">
        <div v-if="showMobileMenu" class="mobile-menu-panel">
          <div class="mobile-menu-content">
            <div class="mobile-menu-item" @click="handleMobileNav('/')">首页</div>
            <div class="mobile-menu-item has-submenu" @click="toggleMobileCategory">
              文章
              <el-icon :class="{ 'rotate': showMobileCategory }"><arrow-right /></el-icon>
            </div>
            <div v-show="showMobileCategory" class="mobile-submenu">
              <div v-for="cat in categories" :key="cat.id" class="mobile-submenu-item" @click="handleMobileCategoryNav(cat.id)">
                {{ cat.name }}
              </div>
            </div>
            <div class="mobile-menu-item" @click="handleMobileNav('/life')">生活</div>
            <div class="mobile-menu-item" @click="handleMobileNav('/about')">个人</div>
            <div class="mobile-menu-item" @click="handleMobileNav('/messages')">留言</div>
            <div class="mobile-menu-item" @click="handleMobileNav('/links')">友情链接</div>
            <div v-if="authStore.isAdmin" class="mobile-menu-item" @click="handleMobileNav('/admin')">管理后台</div>
            <div v-if="!authStore.isAuthenticated" class="mobile-menu-item" @click="handleMobileNav('/login')">登录</div>
            <div v-else class="mobile-menu-item has-submenu" @click="toggleMobileUser">
              {{ authStore.username }}
              <el-icon :class="{ 'rotate': showMobileUser }"><arrow-right /></el-icon>
            </div>
            <div v-show="showMobileUser" class="mobile-submenu">
              <div class="mobile-submenu-item" @click="handleMobileNav('/profile')">个人中心</div>
              <div class="mobile-submenu-item" @click="handleMobileLogout">退出登录</div>
            </div>
          </div>
        </div>
      </transition>
    </el-header>
    <el-main class="main-content">
      <div class="w-full max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8">
        <slot />
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown, ArrowRight } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'

const router = useRouter()
const authStore = useAuthStore()
const categories = ref([])
const showCategoryMenu = ref(false)

// 移动端状态
const showMobileMenu = ref(false)
const showMobileCategory = ref(false)
const showMobileUser = ref(false)

async function fetchCategories() {
  try {
    const res = await request.get('/categories')
    categories.value = res.data || []
  } catch (err) {
    console.error('获取分类失败:', err)
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

// 移动端菜单函数
function toggleMobileMenu() {
  showMobileMenu.value = !showMobileMenu.value
  if (showMobileMenu.value) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
    showMobileCategory.value = false
    showMobileUser.value = false
  }
}

function toggleMobileCategory() {
  showMobileCategory.value = !showMobileCategory.value
  showMobileUser.value = false
}

function toggleMobileUser() {
  showMobileUser.value = !showMobileUser.value
  showMobileCategory.value = false
}

function handleMobileNav(path) {
  router.push(path)
  showMobileMenu.value = false
  document.body.style.overflow = ''
}

function handleMobileCategoryNav(categoryId) {
  router.push(`/categories/${categoryId}`)
  showMobileMenu.value = false
  document.body.style.overflow = ''
}

function handleMobileLogout() {
  authStore.logout()
  router.push('/login')
  showMobileMenu.value = false
  document.body.style.overflow = ''
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
  background-color: rgba(0, 0, 0, 0.6) !important;
  backdrop-filter: blur(10px);
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
  min-width: 1200px;
  min-height: 100vh;
  padding: 120px 0 60px;
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

/* 移动端汉堡菜单按钮 */
.mobile-menu-btn {
  display: none;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  padding: 0;
}

.mobile-menu-btn span {
  width: 24px;
  height: 2px;
  background: #fff;
  margin: 3px 0;
  transition: all 0.3s ease;
}

.mobile-menu-btn:hover span {
  background: #ccc;
}

.mobile-menu-btn.active span:nth-child(1) {
  transform: rotate(-45deg) translate(-5px, 6px);
}

.mobile-menu-btn.active span:nth-child(2) {
  opacity: 0;
}

.mobile-menu-btn.active span:nth-child(3) {
  transform: rotate(45deg) translate(-5px, -6px);
}

/* 移动端菜单面板 */
.mobile-menu-panel {
  position: fixed;
  top: 60px;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.95);
  backdrop-filter: blur(10px);
  z-index: 999;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.mobile-menu-content {
  padding: 10px 0;
}

.mobile-menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  min-height: 44px;
  box-sizing: border-box;
}

.mobile-menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.mobile-submenu {
  background: rgba(255, 255, 255, 0.05);
}

.mobile-submenu-item {
  display: flex;
  align-items: center;
  padding: 14px 48px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 15px;
  cursor: pointer;
  min-height: 44px;
  box-sizing: border-box;
}

.mobile-submenu-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.rotate {
  transform: rotate(90deg);
  transition: transform 0.3s;
}

/* 动画 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 媒体查询 */
@media (max-width: 768px) {
  .desktop-menu {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .header {
    padding: 5px 0;
  }

  .header-content {
    padding: 0 15px;
  }

  .logo {
    font-size: 18px;
  }

  .main-content {
    min-width: auto;
    padding: 80px 15px 40px;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 0 12px;
  }

  .logo {
    font-size: 16px;
  }

  .mobile-menu-item {
    padding: 14px 16px;
    font-size: 15px;
  }

  .mobile-submenu-item {
    padding: 12px 32px;
    font-size: 14px;
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
