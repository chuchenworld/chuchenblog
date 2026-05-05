<template>
  <div
    ref="containerRef"
    class="danmaku-container relative w-full backdrop-blur-md overflow-hidden shadow-lg shadow-black/10 rounded-2xl"
    :style="containerStyle"
  >
    <img
      v-if="backgroundUrl && !isVideo"
      :src="backgroundUrl"
      class="absolute inset-0 w-full h-full object-cover"
    />
    <video
      v-else-if="backgroundUrl && isVideo"
      :src="backgroundUrl"
      class="absolute inset-0 w-full h-full object-cover"
      autoplay
      muted
      loop
      playsinline
    />
    <div class="absolute inset-0 bg-gradient-to-t from-black/50 via-black/15 to-transparent rounded-2xl"></div>
    
    <div
      v-for="(item, index) in danmakuList"
      :key="item.id"
      class="danmaku-item absolute whitespace-nowrap cursor-pointer"
      :class="{ 'paused': hoveredId === item.id }"
      :style="{
        top: `${(index % 4) * 45 + 20}px`,
        animationDuration: `${item.speed}s`,
        animationDelay: `${item.delay}s`
      }"
      @mouseenter="hoveredId = item.id"
      @mouseleave="hoveredId = null"
    >
      <div
        :class="[
          'inline-flex items-center gap-2 px-5 py-2.5 rounded-full backdrop-blur-md transition-all duration-300',
          hoveredId === item.id
            ? 'bg-white/95 shadow-xl shadow-emerald-500/30 scale-110 border-2 border-white'
            : 'bg-white/60 shadow-md border border-white/50'
        ]"
      >
        <span :class="[
          'text-sm font-medium transition-colors',
          hoveredId === item.id ? 'text-emerald-700' : 'text-emerald-700'
        ]">
          {{ item.author }}:
        </span>
        <span :class="[
          'text-sm transition-all',
          hoveredId === item.id ? 'text-gray-800 whitespace-normal max-w-2xl' : 'text-gray-700 max-w-xs truncate'
        ]">
          {{ item.content }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'

const props = defineProps({
  comments: {
    type: Array,
    default: () => []
  },
  backgroundUrl: {
    type: String,
    default: ''
  }
})

const danmakuList = ref([])
const hoveredId = ref(null)
const containerRef = ref(null)

const isVideo = computed(() => {
  return props.backgroundUrl && typeof props.backgroundUrl === 'string' && props.backgroundUrl.toLowerCase().endsWith('.mp4')
})

const containerStyle = computed(() => {
  return {
    height: '360px'
  }
})

const generateDanmaku = () => {
  if (!props.comments || props.comments.length === 0) {
    danmakuList.value = []
    return
  }

  const items = props.comments.map((comment, index) => ({
    id: comment.id,
    content: comment.content || '',
    author: comment.author || '匿名',
    speed: 15 + Math.random() * 25,
    delay: index * 2
  }))

  danmakuList.value = items
}

onMounted(() => {
  generateDanmaku()
})

watch(() => props.comments, () => {
  generateDanmaku()
}, { deep: true })
</script>

<style scoped>
.danmaku-container {
  position: relative;
}

.danmaku-item {
  animation: scrollLeft linear infinite;
  white-space: nowrap;
  z-index: 10;
}

.danmaku-item.paused {
  animation-play-state: paused;
}

@keyframes scrollLeft {
  0% {
    transform: translateX(100vw);
  }
  100% {
    transform: translateX(-100%);
  }
}
</style>
