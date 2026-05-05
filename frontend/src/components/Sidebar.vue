<template>
  <div class="space-y-6">
    <!-- 用户信息卡片 -->
    <div class="bg-slate-800/90 backdrop-blur-md rounded-2xl p-6 shadow-xl border border-slate-700/50">
      <div class="flex flex-col items-center text-center">
        <div class="w-24 h-24 rounded-full bg-gradient-to-br from-emerald-400 via-teal-400 to-green-500 flex items-center justify-center text-white shadow-lg mb-4 overflow-hidden">
          <img v-if="userAvatar" :src="userAvatar" :alt="userName" class="w-full h-full object-cover" />
          <span v-else class="text-3xl">{{ getInitial(userName) }}</span>
        </div>

        <h3 class="text-white mb-2">{{ userName || '博主' }}</h3>

        <p class="text-slate-300 text-sm mb-4">{{ userBio || '这个人很懒，什么都没写' }}</p>

        <div class="flex gap-3 w-full">
          <button class="flex-1 px-4 py-2 bg-white/10 hover:bg-white/20 text-white rounded-lg transition-all border border-white/20 text-sm">
            关注
          </button>
          <button class="flex-1 px-4 py-2 bg-white/10 hover:bg-white/20 text-white rounded-lg transition-all border border-white/20 text-sm">
            联系我
          </button>
        </div>
      </div>
    </div>

    <!-- 标签云卡片 -->
    <div class="bg-slate-800/90 backdrop-blur-md rounded-2xl p-6 shadow-xl border border-slate-700/50">
      <h3 class="text-white mb-4">标签云</h3>
      <div class="flex flex-wrap gap-2">
        <span
          v-for="(tag, index) in tags"
          :key="index"
          class="px-3 py-1.5 bg-emerald-500/20 hover:bg-emerald-500/30 text-emerald-300 rounded-lg text-sm cursor-pointer transition-all border border-emerald-500/30 hover:border-emerald-500/50"
        >
          {{ tag }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  userName: {
    type: String,
    default: ''
  },
  userAvatar: {
    type: String,
    default: ''
  },
  userBio: {
    type: String,
    default: ''
  },
  tags: {
    type: Array,
    default: () => []
  }
})

const getInitial = (name) => {
  if (!name) return '?'
  return name.charAt(0)
}
</script>
