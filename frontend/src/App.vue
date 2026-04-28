<template>
  <div class="app-container">
    <!-- 全局背景 -->
    <div class="global-background">
      <!-- 移动端使用静态图片 -->
      <img
        v-if="isMobile || !bgUrl || !isVideo(bgUrl)"
        :src="isMobile ? mobileBgUrl : bgUrl"
        class="bg-media"
        alt="背景"
      />
      <!-- 桌面端使用视频 -->
      <video
        v-else
        :src="bgUrl"
        class="bg-media"
        autoplay
        muted
        loop
        playsinline
      />
      <div class="background-overlay"></div>
    </div>
    <router-view />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const bgUrl = ref('')
const isMobile = ref(false)
const mobileBgUrl = 'https://chuchenblog.oss-cn-beijing.aliyuncs.com/uploads/2026/04/27/assets/%E3%80%90%E5%93%B2%E9%A3%8E%E5%A3%81%E7%BA%B8%E3%80%91%E4%BE%A0%E5%AE%A2-%E5%89%91%E6%9D%A5-%E5%8F%A4%E9%A3%8E.png'

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
}

const isVideo = (url) => {
  return url && url.toLowerCase().endsWith('.mp4')
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
