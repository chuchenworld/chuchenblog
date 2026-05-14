<template>
  <MainLayout>
    <div 
      class="min-h-screen messages-page rounded-3xl"
      :style="backgroundStyle"
    >
      <Danmaku :comments="comments" :background-url="backgroundUrl" />

      <div class="max-w-6xl mx-auto px-4 py-8 content-wrapper">
        <div class="grid grid-cols-1 gap-8">
          <div>
            <CommentForm @submit="handleAddComment" />
            <CommentList
              :comments="comments"
              @like="handleLike"
              @reply="handleReply"
            />
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import MainLayout from '@/components/MainLayout.vue'
import CommentForm from '@/components/CommentForm.vue'
import CommentList from '@/components/CommentList.vue'
import Danmaku from '@/components/Danmaku.vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const comments = ref([])
const backgroundUrl = ref('')

const backgroundStyle = computed(() => {
  return {
    background: 'linear-gradient(135deg, #ecfdf5 0%, #ccfbf1 50%, #dcfce7 100%)'
  }
})

const formatComments = (data) => {
  return data.map(comment => ({
    id: comment.id,
    content: comment.content,
    author: comment.nickname || '匿名用户',
    contact: comment.email || '',
    timestamp: formatDate(comment.createTime),
    likes: comment.likes || 0,
    isLiked: false,
    replies: (comment.replies || []).map(reply => ({
      id: reply.id,
      content: reply.content,
      author: reply.nickname || '匿名用户',
      timestamp: formatDate(reply.createTime)
    }))
  }))
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const fetchBackground = async () => {
  try {
    const res = await request.get('/config/messages_bg')
    if (res.data) {
      backgroundUrl.value = res.data
    }
  } catch (error) {
    console.error('获取留言板背景失败:', error)
  }
}

const fetchComments = async () => {
  try {
    const res = await request.get('/comments/all')
    if (res.data) {
      comments.value = formatComments(res.data)
    }
  } catch (error) {
    console.error('获取留言失败:', error)
    comments.value = []
  }
}

const handleAddComment = async (comment) => {
  try {
    const res = await request.post('/comments/guest', {
      nickname: comment.author,
      email: comment.contact,
      content: comment.content,
      isAnonymous: comment.isAnonymous
    })

    if (res.data) {
      const newComment = {
        id: res.data.id,
        content: res.data.content,
        author: res.data.nickname || '匿名用户',
        contact: res.data.email || '',
        timestamp: formatDate(res.data.createTime),
        likes: 0,
        isLiked: false,
        replies: []
      }
      comments.value.unshift(newComment)
      ElMessage.success('留言成功')
    }
  } catch (error) {
    ElMessage.error('留言失败，请重试')
    console.error('留言失败:', error)
  }
}

const handleLike = async (commentId) => {
  try {
    await request.post(`/comments/${commentId}/like`)
    const comment = comments.value.find(c => c.id === commentId)
    if (comment) {
      comment.isLiked = !comment.isLiked
      comment.likes += comment.isLiked ? 1 : -1
    }
  } catch (error) {
    ElMessage.error('点赞失败')
    console.error('点赞失败:', error)
  }
}

const handleReply = async (commentId, content, author) => {
  try {
    const res = await request.post('/comments/guest', {
      nickname: author,
      email: '',
      content: content,
      isAnonymous: false,
      parentId: commentId
    })

    if (res.data) {
      const comment = comments.value.find(c => c.id === commentId)
      if (comment) {
        const newReply = {
          id: res.data.id,
          content: res.data.content,
          author: res.data.nickname || '匿名用户',
          timestamp: formatDate(res.data.createTime)
        }
        comment.replies.push(newReply)
      }
      ElMessage.success('回复成功')
    }
  } catch (error) {
    ElMessage.error('回复失败，请重试')
    console.error('回复失败:', error)
  }
}

onMounted(() => {
  fetchBackground()
  fetchComments()
})
</script>

<style scoped>
.messages-page {
  padding: 0;
  position: relative;
}

.content-wrapper {
  background: transparent;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .content-wrapper {
    padding: 15px;
  }

  .max-w-6xl {
    max-width: 100%;
  }
}

@media (max-width: 480px) {
  .content-wrapper {
    padding: 10px;
  }
}
</style>
