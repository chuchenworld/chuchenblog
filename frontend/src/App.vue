<template>
  <div class="app-container">
    <!-- 全局背景 -->
    <div class="global-background">
      <!-- 移动端使用静态图片 -->
      <img
        v-if="isMobile"
        :src="mobileBgUrl"
        class="bg-media"
        alt="背景"
        @error="handleMobileBgError"
      />
      <!-- 桌面端：如果有视频URL则使用视频，否则使用图片 -->
      <video
        v-else-if="bgUrl && isVideo(bgUrl)"
        :src="bgUrl"
        class="bg-media"
        autoplay
        muted
        loop
        playsinline
        @error="handleVideoError"
      />
      <img
        v-else
        :src="bgUrl || mobileBgUrl"
        class="bg-media"
        alt="背景"
        @error="handleDesktopBgError"
      />
      <div class="background-overlay"></div>
    </div>
    <router-view />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import mobileBackground from '@/assets/【哲风壁纸】植物-清新-窗帘.png'

const bgUrl = ref('')
const isMobile = ref(false)
// 移动端背景图片 - 使用本地图片
const mobileBgUrl = mobileBackground
// 备用背景图片
const fallbackBgUrl = 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1920&q=80'

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
}

const isVideo = (url) => {
  return url && url.toLowerCase().endsWith('.mp4')
}

const handleMobileBgError = (event) => {
  console.log('移动端背景图片加载失败，使用备用图片')
  event.target.src = fallbackBgUrl
}

const handleDesktopBgError = (event) => {
  console.log('桌面端背景图片加载失败，使用移动端背景')
  event.target.src = mobileBgUrl
}

const handleVideoError = (event) => {
  console.log('背景视频加载失败，切换到图片模式')
  // 触发重新渲染，使用图片模式
  bgUrl.value = ''
}

async function fetchHomeBackground() {
  console.log('【App.vue】开始获取全局背景')
  try {
    const response = await fetch('/api/config/home_bg')
    if (response.ok) {
      const text = await response.text()
      try {
        const data = JSON.parse(text)
        if (data.code === 200 && data.data) {
          let url = data.data
          if (url && !url.startsWith('http')) {
            url = window.location.origin + url
          }
          bgUrl.value = url
          console.log('【App.vue】全局背景获取成功 (JSON):', bgUrl.value)
        } else {
          console.warn('【App.vue】全局背景响应格式异常:', data)
        }
      } catch (jsonError) {
        let url = text.trim()
        if (url && !url.startsWith('http')) {
          url = window.location.origin + url
        }
        bgUrl.value = url
        console.log('【App.vue】全局背景获取成功 (String):', bgUrl.value)
      }
    }
  } catch (error) {
    console.error('【App.vue】全局背景获取失败:', error.message)
  }
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  fetchHomeBackground()
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style>
.app-container {
  position: relative;
  min-height: 100vh;
}

.global-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: -1;
  background-color: #1a1a1a;
  overflow: hidden;
}

.bg-media {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  object-fit: cover;
  object-position: center;
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 1;
}
</style>
