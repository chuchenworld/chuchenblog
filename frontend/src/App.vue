<template>
  <div class="app-container">
    <!-- 全局背景图 -->
    <div class="global-background" :style="bgUrl ? { backgroundImage: `url(${bgUrl})` } : {}">
      <div class="background-overlay"></div>
    </div>
    <router-view />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const bgUrl = ref('')

async function fetchHomeBackground() {
  console.log('【App.vue】开始获取全局背景图')
  try {
    const response = await fetch('/api/config/home-bg')
    if (response.ok) {
      const text = await response.text()
      try {
        // 尝试解析为 JSON
        const data = JSON.parse(text)
        if (data.code === 200 && data.data) {
          let url = data.data
          if (url && !url.startsWith('http')) {
            url = window.location.origin + url
          }
          bgUrl.value = url
          console.log('【App.vue】全局背景图获取成功 (JSON):', bgUrl.value)
        } else {
          console.warn('【App.vue】全局背景图响应格式异常:', data)
        }
      } catch (jsonError) {
        // 如果解析失败，直接使用文本作为 URL
        let url = text.trim()
        if (url && !url.startsWith('http')) {
          url = window.location.origin + url
        }
        bgUrl.value = url
        console.log('【App.vue】全局背景图获取成功 (String):', bgUrl.value)
      }
    }
  } catch (error) {
    console.error('【App.vue】全局背景图获取失败:', error.message)
  }
}

onMounted(() => {
  fetchHomeBackground()
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
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: #1a1a1a; /* 备用背景色 */
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4); /* 半透明黑色遮罩 */
}
</style>