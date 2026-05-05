<template>
  <div class="bg-white/90 backdrop-blur-md rounded-2xl shadow-xl shadow-emerald-500/10 p-8 mb-8 border border-emerald-100/50">
    <div class="flex items-center gap-3 mb-6">
      <div class="w-1 h-8 bg-gradient-to-b from-emerald-500 to-teal-500 rounded-full"></div>
      <h2 class="text-emerald-800">发表评论</h2>
    </div>

    <form @submit.prevent="handleSubmit" class="space-y-5">
      <div>
        <label for="content" class="block mb-2 text-emerald-700">
          评论内容
        </label>
        <textarea
          id="content"
          v-model="content"
          placeholder="写下你的想法..."
          rows="4"
          class="w-full px-4 py-3 rounded-xl bg-emerald-50/50 border-2 border-emerald-100 focus:outline-none focus:border-emerald-400 focus:bg-white resize-none transition-all placeholder:text-emerald-300"
          required
        />
      </div>

      <div class="flex items-center bg-emerald-50/50 px-4 py-3 rounded-xl border border-emerald-100">
        <input
          type="checkbox"
          id="anonymous"
          v-model="isAnonymous"
          class="w-5 h-5 rounded border-emerald-300 text-emerald-600 focus:ring-2 focus:ring-emerald-400/30"
        />
        <label for="anonymous" class="ml-3 cursor-pointer text-emerald-700">
          匿名评论
        </label>
      </div>

      <template v-if="!isAnonymous">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="author" class="block mb-2 text-emerald-700">
              用户名
            </label>
            <input
              id="author"
              type="text"
              v-model="author"
              placeholder="请输入用户名"
              class="w-full px-4 py-3 rounded-xl bg-emerald-50/50 border-2 border-emerald-100 focus:outline-none focus:border-emerald-400 focus:bg-white transition-all placeholder:text-emerald-300"
              :required="!isAnonymous"
            />
          </div>

          <div>
            <label for="contact" class="block mb-2 text-emerald-700">
              联系方式
            </label>
            <input
              id="contact"
              type="text"
              v-model="contact"
              placeholder="邮箱或手机号（可选）"
              class="w-full px-4 py-3 rounded-xl bg-emerald-50/50 border-2 border-emerald-100 focus:outline-none focus:border-emerald-400 focus:bg-white transition-all placeholder:text-emerald-300"
            />
          </div>
        </div>
      </template>

      <button
        type="submit"
        class="w-full md:w-auto px-8 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl hover:from-emerald-700 hover:to-teal-700 transition-all shadow-lg shadow-emerald-500/30 hover:shadow-xl hover:shadow-emerald-500/40"
      >
        发表评论
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits<{
  (e: 'submit', comment: {
    content: string
    author: string
    contact: string
    isAnonymous: boolean
  }): void
}>()

const content = ref('')
const author = ref('')
const contact = ref('')
const isAnonymous = ref(false)

const handleSubmit = () => {
  if (!content.value.trim()) {
    alert('请输入评论内容')
    return
  }

  if (!isAnonymous.value && !author.value.trim()) {
    alert('请输入用户名或选择匿名评论')
    return
  }

  emit('submit', {
    content: content.value.trim(),
    author: isAnonymous.value ? '匿名用户' : author.value.trim(),
    contact: isAnonymous.value ? '' : contact.value.trim(),
    isAnonymous: isAnonymous.value
  })

  content.value = ''
  if (!isAnonymous.value) {
    author.value = ''
    contact.value = ''
  }
}
</script>