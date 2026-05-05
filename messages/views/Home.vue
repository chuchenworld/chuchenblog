<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-teal-50 to-green-50">
    <Danmaku :comments="comments" />

    <div class="max-w-6xl mx-auto px-4 py-8">
      <div class="grid grid-cols-1 lg:grid-cols-4 gap-8">
        <div class="lg:col-span-3">
          <CommentForm @submit="handleAddComment" />
          <CommentList
            :comments="comments"
            @like="handleLike"
            @reply="handleReply"
          />
        </div>

        <div class="lg:col-span-1">
          <div class="sticky top-8">
            <Sidebar
              :user-name="userName"
              :user-bio="userBio"
              :tags="tags"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import CommentForm from '@/components/CommentForm.vue'
import CommentList from '@/components/CommentList.vue'
import Danmaku from '@/components/Danmaku.vue'
import Sidebar from '@/components/Sidebar.vue'
import { useCommentStore } from '@/stores/comments'

const commentStore = useCommentStore()

const comments = computed(() => commentStore.comments)

const userName = '初尘'
const userBio = '正在努力做个人博客中'
const tags = ['前端', '后端', '算法', 'React', 'Vue', 'Node.js']

const handleAddComment = (comment: {
  content: string
  author: string
  contact: string
  isAnonymous: boolean
}) => {
  commentStore.addComment(comment)
}

const handleLike = (commentId: number) => {
  commentStore.toggleLike(commentId)
}

const handleReply = (commentId: number, content: string, author: string) => {
  commentStore.addReply(commentId, content, author)
}
</script>