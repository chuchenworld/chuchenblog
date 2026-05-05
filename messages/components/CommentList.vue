<template>
  <div class="space-y-6">
    <div class="flex items-center gap-3 mb-6">
      <div class="w-1 h-8 bg-gradient-to-b from-emerald-500 to-teal-500 rounded-full"></div>
      <h2 class="text-emerald-800">全部评论 ({{ comments.length }})</h2>
    </div>

    <template v-if="comments.length === 0">
      <div class="text-center py-16 bg-white/60 backdrop-blur-md rounded-2xl border border-emerald-100/50">
        <div class="text-emerald-400 mb-3">
          <MessageCircle class="w-16 h-16 mx-auto" :stroke-width="1.5" />
        </div>
        <p class="text-emerald-600">暂无评论，快来发表第一条评论吧！</p>
      </div>
    </template>

    <template v-else>
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="bg-white/90 backdrop-blur-md rounded-2xl shadow-lg shadow-emerald-500/5 p-6 border border-emerald-100/50 hover:shadow-xl hover:shadow-emerald-500/10 transition-all"
      >
        <div class="flex items-start gap-4">
          <div class="w-12 h-12 rounded-full bg-gradient-to-br from-emerald-400 via-teal-400 to-green-500 flex items-center justify-center text-white flex-shrink-0 shadow-lg">
            {{ comment.author.charAt(0).toUpperCase() }}
          </div>

          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-2">
              <span class="font-medium text-emerald-800">{{ comment.author }}</span>
              <span class="text-sm text-emerald-500">{{ comment.timestamp }}</span>
            </div>

            <p class="mb-4 break-words text-gray-700 leading-relaxed">{{ comment.content }}</p>

            <div class="flex items-center gap-4">
              <button
                @click="$emit('like', comment.id)"
                :class="[
                  'flex items-center gap-1.5 text-sm transition-all px-3 py-1.5 rounded-full',
                  comment.isLiked
                    ? 'text-rose-500 bg-rose-50'
                    : 'text-emerald-600 hover:text-rose-500 hover:bg-rose-50'
                ]"
              >
                <Heart
                  class="w-4 h-4"
                  :fill="comment.isLiked ? 'currentColor' : 'none'"
                />
                <span>{{ comment.likes }}</span>
              </button>

              <button
                @click="replyingTo = replyingTo === comment.id ? null : comment.id"
                class="flex items-center gap-1.5 text-sm text-emerald-600 hover:text-emerald-700 hover:bg-emerald-50 transition-all px-3 py-1.5 rounded-full"
              >
                <MessageCircle class="w-4 h-4" />
                <span>回复</span>
              </button>
            </div>

            <template v-if="replyingTo === comment.id">
              <div class="mt-4 p-4 bg-emerald-50/70 backdrop-blur-sm rounded-xl border border-emerald-100 space-y-3">
                <input
                  type="text"
                  v-model="replyAuthor"
                  placeholder="你的昵称"
                  class="w-full px-4 py-2.5 rounded-lg bg-white border-2 border-emerald-100 focus:outline-none focus:border-emerald-400 transition-all placeholder:text-emerald-300"
                />
                <textarea
                  v-model="replyContent"
                  placeholder="写下你的回复..."
                  rows="3"
                  class="w-full px-4 py-2.5 rounded-lg bg-white border-2 border-emerald-100 focus:outline-none focus:border-emerald-400 resize-none transition-all placeholder:text-emerald-300"
                />
                <div class="flex gap-2">
                  <button
                    @click="handleReplySubmit(comment.id)"
                    class="px-5 py-2 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-lg hover:from-emerald-700 hover:to-teal-700 transition-all shadow-md shadow-emerald-500/30 text-sm"
                  >
                    发送
                  </button>
                  <button
                    @click="cancelReply"
                    class="px-5 py-2 bg-white text-emerald-700 border-2 border-emerald-200 rounded-lg hover:bg-emerald-50 transition-all text-sm"
                  >
                    取消
                  </button>
                </div>
              </div>
            </template>

            <template v-if="comment.replies.length > 0">
              <div class="mt-4 space-y-3">
                <div
                  v-for="reply in comment.replies"
                  :key="reply.id"
                  class="flex gap-3 p-4 bg-gradient-to-r from-emerald-50/50 to-teal-50/50 rounded-xl border border-emerald-100/50"
                >
                  <div class="w-9 h-9 rounded-full bg-gradient-to-br from-teal-400 to-emerald-500 flex items-center justify-center text-white text-sm flex-shrink-0 shadow-md">
                    {{ reply.author.charAt(0).toUpperCase() }}
                  </div>
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="text-sm font-medium text-emerald-700">{{ reply.author }}</span>
                      <span class="text-xs text-emerald-500">{{ reply.timestamp }}</span>
                    </div>
                    <p class="text-sm break-words text-gray-700">{{ reply.content }}</p>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Heart, MessageCircle } from 'lucide-vue-next'
import type { Comment } from '@/stores/comments'

defineProps<{
  comments: Comment[]
}>()

const emit = defineEmits<{
  (e: 'like', commentId: number): void
  (e: 'reply', commentId: number, content: string, author: string): void
}>()

const replyingTo = ref<number | null>(null)
const replyContent = ref('')
const replyAuthor = ref('')

const handleReplySubmit = (commentId: number) => {
  if (!replyContent.value.trim() || !replyAuthor.value.trim()) {
    alert('请填写回复内容和用户名')
    return
  }

  emit('reply', commentId, replyContent.value.trim(), replyAuthor.value.trim())
  replyContent.value = ''
  replyAuthor.value = ''
  replyingTo.value = null
}

const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
  replyAuthor.value = ''
}
</script>