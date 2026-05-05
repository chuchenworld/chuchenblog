<template>
  <div
    ref="containerRef"
    class="relative w-full h-48 bg-gradient-to-r from-emerald-900/30 via-green-800/30 to-teal-900/30 backdrop-blur-sm overflow-hidden border-b border-emerald-400/20"
  >
    <div
      v-for="(item, index) in danmakuList"
      :key="item.id"
      class="absolute whitespace-nowrap cursor-pointer"
      :style="{
        top: `${(index % 4) * 40 + 15}px`,
        animationName: 'scroll-left',
        animationDuration: `${item.speed}s`,
        animationTimingFunction: 'linear',
        animationIterationCount: 'infinite',
        animationDelay: `${item.delay}s`,
        animationPlayState: hoveredId === item.id ? 'paused' : 'running'
      }"
      @mouseenter="hoveredId = item.id"
      @mouseleave="hoveredId = null"
    >
      <div
        :class="[
          'inline-flex items-center gap-2 px-5 py-2.5 rounded-full backdrop-blur-md transition-all duration-300',
          hoveredId === item.id
            ? 'bg-emerald-100/95 shadow-xl shadow-emerald-500/30 scale-110 border-2 border-emerald-400'
            : 'bg-white/70 shadow-md border border-white/50'
        ]"
        :style="{
          position: 'relative',
          zIndex: hoveredId === item.id ? 50 : 1
        }"
      >
        <span :class="[
          'text-sm font-medium transition-colors',
          hoveredId === item.id ? 'text-emerald-700' : 'text-emerald-600'
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

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'

interface DanmakuItem {
  id: number
  content: string
  author: string
  speed: number
  delay: number
}

const props = defineProps<{
  comments: Array<{ id: number; content: string; author: string }>
}>()

const danmakuList = ref<DanmakuItem[]>([])
const hoveredId = ref<number | null>(null)
const containerRef = ref<HTMLElement | null>(null)

const generateDanmaku = () => {
  if (props.comments.length === 0) return

  const items = props.comments.map((comment, index) => ({
    ...comment,
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