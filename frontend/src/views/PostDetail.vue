<template>
  <div 
    class="min-h-screen bg-black/40" 
    :style="{
      backgroundImage: `url(${postStore.currentPost?.coverImage})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundAttachment: 'fixed'
    }"
  >
    <nav class="bg-white/80 backdrop-blur-md shadow-sm sticky top-0 z-50">
      <div class="container mx-auto px-4 py-4 flex justify-between items-center">
        <h1 class="text-xl font-bold text-gray-800">初尘博客</h1>
        <div class="flex items-center gap-2 md:gap-4">
          <router-link to="/home" class="text-gray-600 hover:text-blue-500 transition-colors text-sm md:text-base">首页</router-link>
          <router-link v-if="authStore.isAuthenticated" to="/editor" class="text-gray-600 hover:text-blue-500 transition-colors text-sm md:text-base">写文章</router-link>
        </div>
      </div>
    </nav>

    <div class="flex justify-center relative">
      <article class="w-full mx-4 my-4 md:my-8 glass-card">
        <header class="pb-4 md:pb-6 border-b border-gray-200/50">
          <h1 class="text-2xl md:text-3xl lg:text-4xl font-bold text-gray-800 mb-4 md:mb-6 leading-tight" v-if="postStore.currentPost">
            {{ postStore.currentPost.title }}
          </h1>
          <div class="flex flex-wrap items-center gap-3 md:gap-6 text-xs md:text-sm text-gray-500">
            <span>作者: {{ postStore.currentPost?.authorName || '未知' }}</span>
            <span class="flex items-center gap-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
              </svg>
              {{ postStore.currentPost?.viewCount || 0 }}
            </span>
            <span class="flex items-center gap-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
              </svg>
              {{ commentCount }}
            </span>
            <span>{{ postStore.currentPost?.createTime }}</span>
          </div>
        </header>

        <div 
          class="prose-container" 
          ref="articleContent"
          v-html="renderedContent"
        ></div>

        <footer class="mt-8 md:mt-12 pt-4 md:pt-6 border-t border-gray-200/50">
          <p class="text-center text-xs md:text-sm text-gray-500">
            © 2024 初尘博客 · 转载请注明出处
          </p>
        </footer>

        <section class="mt-8 md:mt-12 pt-6 border-t border-gray-200/50">
          <h3 class="text-lg md:text-xl font-bold text-gray-800 mb-4 md:mb-6 flex items-center">
            <svg class="w-5 h-5 mr-2 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
            </svg>
            评论区
            <span class="ml-2 text-sm font-normal text-gray-500">({{ commentCount }}条评论)</span>
          </h3>

          <div class="mb-6">
            <div class="bg-gray-50 rounded-xl p-4 md:p-6">
              <textarea
                v-model="commentForm.content"
                class="w-full h-24 md:h-32 px-4 py-3 border border-gray-200 rounded-lg resize-none focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-700 placeholder-gray-400"
                placeholder="写下你的评论..."
              ></textarea>
              
              <div class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <input
                    v-model="commentForm.nickname"
                    type="text"
                    class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-700"
                    placeholder="昵称"
                  />
                </div>
                <div>
                  <input
                    v-model="commentForm.email"
                    type="email"
                    class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-700"
                    placeholder="邮箱"
                  />
                </div>
              </div>

              <div class="mt-4 flex flex-col md:flex-row items-start md:items-center justify-between gap-3">
                <div class="flex flex-col md:flex-row gap-3">
                  <label class="flex items-center gap-2 cursor-pointer">
                    <input
                      v-model="commentForm.isAnonymous"
                      type="checkbox"
                      class="w-4 h-4 text-blue-500 border-gray-300 rounded focus:ring-blue-500"
                    />
                    <span class="text-sm text-gray-600">匿名评论</span>
                  </label>
                  <label class="flex items-center gap-2 cursor-pointer">
                    <input
                      v-model="commentForm.notifyAuthor"
                      type="checkbox"
                      class="w-4 h-4 text-blue-500 border-gray-300 rounded focus:ring-blue-500"
                    />
                    <span class="text-sm text-gray-600">通过邮件通知作者</span>
                  </label>
                </div>
                <button
                  @click="submitComment"
                  :disabled="!commentForm.content.trim() || !commentForm.nickname.trim() || !commentForm.email.trim()"
                  class="px-6 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed text-sm font-medium"
                >
                  发布评论
                </button>
              </div>
            </div>
          </div>

          <div v-if="commentStore.comments.length > 0" class="space-y-4">
            <div
              v-for="comment in commentStore.comments"
              :key="comment.id"
              class="bg-gray-50 rounded-xl p-4 md:p-5"
            >
              <div class="flex items-start gap-3">
                <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center flex-shrink-0">
                  <span class="text-white font-medium text-sm">{{ getAvatarInitial(comment.nickname) }}</span>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2 mb-2">
                    <span class="font-medium text-gray-800 text-sm md:text-base">
                      {{ comment.nickname }}
                    </span>
                    <span class="text-xs text-gray-400">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <p class="text-gray-600 text-sm md:text-base leading-relaxed">{{ comment.content }}</p>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="text-center py-8">
            <svg class="w-12 h-12 mx-auto text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
            </svg>
            <p class="text-gray-400">暂无评论，快来发表第一条评论吧！</p>
          </div>
        </section>
      </article>

      <aside class="hidden lg:block fixed right-4 xl:right-8 top-24 w-48 xl:w-56 z-40">
        <div class="toc-glass-card rounded-xl p-3 md:p-4">
          <h3 class="font-bold text-gray-800 mb-3 md:mb-4 flex items-center text-sm md:text-base">
            <span class="w-1 h-4 bg-blue-500 rounded mr-2"></span>
            文章目录
          </h3>
          <nav class="space-y-1 max-h-[60vh] overflow-y-auto" v-if="tocItems.length > 0">
            <ul>
              <li v-for="(item, index) in tocItems" :key="index">
                <a
                  :href="`#${item.id}`"
                  :class="[
                    'block py-1.5 px-2 rounded text-xs md:text-sm transition-all duration-200 cursor-pointer',
                    item.level === 1 ? 'text-gray-800 font-medium pl-0' : '',
                    item.level === 2 ? 'text-gray-600 pl-3' : '',
                    item.level === 3 ? 'text-gray-500 pl-6 text-xs' : '',
                    activeHeading === item.id ? 'bg-blue-50 text-blue-600 font-medium' : 'hover:bg-gray-100'
                  ]"
                  @click.prevent="scrollToHeading(item.id)"
                >
                  {{ item.text }}
                </a>
              </li>
            </ul>
          </nav>
          <p class="text-xs md:text-sm text-gray-400 text-center" v-else>
            暂无目录
          </p>
        </div>
      </aside>
    </div>

    <div 
      v-if="showToast"
      :class="[
        'fixed top-20 right-4 z-50 px-6 py-3 rounded-lg shadow-lg transition-all duration-300 transform',
        toastType === 'success' ? 'bg-green-500 text-white' : 'bg-red-500 text-white'
      ]"
    >
      {{ toastMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePostStore } from '@/stores/post'
import { useCommentStore } from '@/stores/comment'
import { marked } from 'marked'

const route = useRoute()
const authStore = useAuthStore()
const postStore = usePostStore()
const commentStore = useCommentStore()
const articleContent = ref(null)
const tocItems = ref([])
const activeHeading = ref('')

const commentForm = reactive({
  content: '',
  nickname: '',
  email: '',
  isAnonymous: false,
  notifyAuthor: false
})

const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

marked.setOptions({
  breaks: true,
  gfm: true
})

const renderedContent = computed(() => {
  if (!postStore.currentPost?.content) return ''
  return marked(postStore.currentPost.content)
})

const commentCount = computed(() => commentStore.comments.length)

const generateToc = () => {
  if (!articleContent.value) return
  
  tocItems.value = []
  
  const headings = articleContent.value.querySelectorAll('h1, h2, h3')
  tocItems.value = Array.from(headings).map((heading, index) => {
    const id = `heading-${index}`
    heading.id = id
    return {
      id,
      text: heading.textContent,
      level: parseInt(heading.tagName.charAt(1))
    }
  })
}

const scrollToHeading = (id) => {
  const element = document.getElementById(id)
  if (element) {
    const offsetTop = element.offsetTop - 80
    window.scrollTo({
      top: offsetTop,
      behavior: 'smooth'
    })
    activeHeading.value = id
  }
}

const handleScroll = () => {
  const headings = document.querySelectorAll('h1, h2, h3')
  let currentHeading = ''
  
  headings.forEach((heading) => {
    const rect = heading.getBoundingClientRect()
    if (rect.top <= 150) {
      currentHeading = heading.id
    }
  })
  
  activeHeading.value = currentHeading
}

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  
  setTimeout(() => {
    showToast.value = false
  }, 2000)
}

const submitComment = async () => {
  if (!commentForm.content.trim()) return
  
  if (!commentForm.nickname.trim() || !commentForm.email.trim()) {
    showToastMessage('请填写昵称和邮箱', 'error')
    return
  }
  
  try {
    await commentStore.createComment(
      route.params.id,
      commentForm.content,
      commentForm.nickname,
      commentForm.email,
      commentForm.isAnonymous,
      commentForm.notifyAuthor
    )
    
    commentForm.content = ''
    await commentStore.fetchCommentsByArticleId(route.params.id)
    
    postStore.incrementCommentCount(route.params.id)
    
    if (commentForm.notifyAuthor) {
      showToastMessage('评论成功，邮件已发送')
    } else {
      showToastMessage('评论成功')
    }
  } catch (error) {
    console.error('提交评论失败:', error)
    showToastMessage('评论提交失败，请稍后重试', 'error')
  }
}

const getAvatarInitial = (nickname) => {
  if (!nickname) return '?'
  return nickname.charAt(0).toUpperCase()
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

watch(renderedContent, () => {
  setTimeout(() => {
    generateToc()
  }, 100)
})

onMounted(() => {
  postStore.fetchPostById(route.params.id)
  commentStore.fetchCommentsByArticleId(route.params.id)
  window.addEventListener('scroll', handleScroll)
  
  if (authStore.isAuthenticated) {
    commentForm.nickname = authStore.username
    commentForm.email = authStore.username + '@example.com'
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  border-radius: 12px;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.4);
  padding: 24px;
}

@media (min-width: 768px) {
  .glass-card {
    max-width: 800px;
    padding: 40px;
  }
}

.toc-glass-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.prose-container {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 
               'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 16px;
  line-height: 1.8;
  color: #2c3e50;
  text-align: justify;
}

@media (min-width: 768px) {
  .prose-container {
    font-size: 17px;
  }
}

.prose-container h1 {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  margin-top: 1.5em;
  margin-bottom: 0.6em;
  padding-bottom: 0.4rem;
  border-bottom: 2px solid #e8e8e8;
}

@media (min-width: 768px) {
  .prose-container h1 {
    font-size: 28px;
    margin-top: 2em;
    margin-bottom: 0.8em;
    padding-bottom: 0.5rem;
  }
}

.prose-container h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  margin-top: 1.5em;
  margin-bottom: 0.6em;
  padding-bottom: 0.3rem;
  border-bottom: 1px solid #f0f0f0;
}

@media (min-width: 768px) {
  .prose-container h2 {
    font-size: 22px;
    margin-top: 1.8em;
    margin-bottom: 0.8em;
  }
}

.prose-container h3 {
  font-size: 17px;
  font-weight: 600;
  color: #2c3e50;
  margin-top: 1.2em;
  margin-bottom: 0.5em;
}

@media (min-width: 768px) {
  .prose-container h3 {
    font-size: 19px;
    margin-top: 1.5em;
    margin-bottom: 0.8em;
  }
}

.prose-container h4 {
  font-size: 16px;
  font-weight: 600;
  color: #34495e;
  margin-top: 1em;
  margin-bottom: 0.5em;
}

@media (min-width: 768px) {
  .prose-container h4 {
    font-size: 17px;
    margin-top: 1.2em;
    margin-bottom: 0.6em;
  }
}

.prose-container p {
  margin-bottom: 1em;
}

@media (min-width: 768px) {
  .prose-container p {
    margin-bottom: 1.2em;
  }
}

.prose-container ul,
.prose-container ol {
  margin-bottom: 1em;
  padding-left: 1.2rem;
}

@media (min-width: 768px) {
  .prose-container ul,
  .prose-container ol {
    margin-bottom: 1.2em;
    padding-left: 1.5rem;
  }
}

.prose-container li {
  margin: 0.4rem 0;
}

.prose-container a {
  color: #2563eb;
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: border-color 0.2s;
  word-break: break-all;
}

.prose-container a:hover {
  border-bottom-color: #2563eb;
}

.prose-container pre {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 1rem;
  border-radius: 8px;
  overflow-x: auto;
  margin: 1rem 0;
  font-size: 13px;
}

@media (min-width: 768px) {
  .prose-container pre {
    padding: 1.2rem 1.5rem;
    margin: 1.5rem 0;
    font-size: 14px;
  }
}

.prose-container code {
  font-family: 'Fira Code', 'Monaco', 'Consolas', 'Liberation Mono', monospace;
  font-size: 13px;
}

@media (min-width: 768px) {
  .prose-container code {
    font-size: 14px;
  }
}

.prose-container p code {
  background: #f3f4f6;
  padding: 0.15em 0.3em;
  border-radius: 4px;
  font-size: 0.85em;
  color: #c7254e;
}

.prose-container blockquote {
  border-left: 4px solid #2563eb;
  padding: 0.8rem 1rem;
  margin: 1rem 0;
  color: #6b7280;
  font-style: italic;
  background: #f9fafb;
  border-radius: 0 8px 8px 0;
}

.prose-container img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 1rem 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

@media (min-width: 768px) {
  .prose-container img {
    margin: 1.5rem 0;
  }
}

.prose-container table {
  width: 100%;
  border-collapse: collapse;
  margin: 1rem 0;
  display: block;
  overflow-x: auto;
}

.prose-container th,
.prose-container td {
  border: 1px solid #e5e7eb;
  padding: 0.5rem;
  text-align: left;
  min-width: 80px;
}

@media (min-width: 768px) {
  .prose-container th,
  .prose-container td {
    padding: 0.75rem;
  }
}

.prose-container th {
  background: #f9fafb;
  font-weight: 600;
}

.prose-container hr {
  border: none;
  border-top: 1px solid #e5e7eb;
  margin: 1.5rem 0;
}

.toc-glass-card nav::-webkit-scrollbar {
  width: 4px;
}

.toc-glass-card nav::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 2px;
}

.toc-glass-card nav::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 2px;
}

.toc-glass-card nav::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}

@media (max-width: 767px) {
  aside {
    display: none !important;
  }
}

@media (min-width: 768px) and (max-width: 1023px) {
  aside {
    right: 4px !important;
    width: 44vw !important;
    max-width: 200px !important;
  }
}
</style>
