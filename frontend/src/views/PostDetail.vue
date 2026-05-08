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
      <div class="w-full max-w-3xl">
        <article class="mx-4 my-4 md:my-8 glass-card p-4 md:p-8">
        <div class="pb-6 md:pb-8 mb-6 md:mb-8 border-b border-gray-200/50">
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
        </div>

        <div class="mb-6 md:mb-8">
          <div
            class="prose-container"
            ref="articleContent"
            v-html="renderedContent"
          ></div>

          <footer class="mt-8 md:mt-12 pt-4 md:pt-6 border-t border-gray-200/50">
            <p class="text-center text-xs md:text-sm text-gray-500">
              © 初尘博客 · 敬请注明出处
            </p>
          </footer>
        </div>
      </article>

        <div class="mx-4 my-4 md:my-8">
          <div class="glass-card p-4 md:p-8 mb-6 md:mb-8">
            <h3 class="text-lg md:text-xl font-bold text-gray-800 mb-4 md:mb-6 flex items-center">
              <svg class="w-5 h-5 mr-2 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
              </svg>
              相关推荐
            </h3>
            <div v-if="relatedPosts.length > 0" class="grid grid-cols-3 gap-4">
              <div
                v-for="post in relatedPosts.slice(0, 3)"
                :key="post.id"
                class="related-card group relative h-40 md:h-48 rounded-xl overflow-hidden cursor-pointer"
                @click="goToPost(post.id)"
              >
                <img :src="post.coverImage || '/default-cover.jpg'" :alt="post.title" class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110" />
                <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex flex-col justify-end p-3 md:p-4">
                  <div class="flex flex-wrap gap-1 mb-2">
                    <span v-for="tag in (Array.isArray(post.tags) ? post.tags : String(post.tags || '').split(',')).filter(t => t).slice(0, 3)" :key="tag" class="px-2 py-0.5 bg-blue-500/90 text-white text-xs rounded-full">{{ tag }}</span>
                  </div>
                  <h4 class="text-white font-medium text-sm line-clamp-2">{{ post.title }}</h4>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-8 text-gray-400">
              <svg class="w-12 h-12 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
              </svg>
              <p>暂无相关文章推荐</p>
            </div>
          </div>

          <div class="glass-card p-4 md:p-8">
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
                  <div class="flex items-center gap-4 mt-3">
                    <button 
                      @click="handleLikeComment(comment)"
                      class="flex items-center gap-1 text-gray-400 hover:text-red-500 transition-colors text-sm"
                    >
                      <svg class="w-4 h-4" :class="comment.liked ? 'fill-current text-red-500' : ''" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                      </svg>
                      <span>{{ comment.likeCount || 0 }}</span>
                    </button>
                  </div>
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
        </div>
      </div>
    </div>

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
import { ref, computed, watch, onMounted, onUnmounted, reactive, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePostStore } from '@/stores/post'
import { useCommentStore } from '@/stores/comment'
import { marked } from 'marked'
import hljs from 'highlight.js'
import { markedHighlight } from 'marked-highlight'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
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
const relatedPosts = ref([])
const postLiked = ref(false)
const postLikeCount = ref(0)

const handleLikeComment = async (comment) => {
  try {
    await commentStore.likeComment(comment.id)
    comment.liked = !comment.liked
    comment.likeCount = (comment.likeCount || 0) + (comment.liked ? 1 : -1)
  } catch (error) {
    console.error('点赞失败:', error)
    showToastMessage('点赞失败，请稍后重试', 'error')
  }
}

const parseTags = (tags) => {
  if (!tags) return []
  if (Array.isArray(tags)) return tags.map(t => t.trim()).filter(t => t)
  if (typeof tags === 'string') return tags.split(',').map(t => t.trim()).filter(t => t)
  return []
}

const fetchRelatedPosts = async () => {
  if (!postStore.currentPost?.tags) return
  
  const tags = parseTags(postStore.currentPost.tags)
  if (tags.length === 0) return
  
  try {
    const res = await request.get('/posts', { params: { pageNum: 1, pageSize: 20 } })
    const allPosts = res.data.records || []
    const currentId = parseInt(route.params.id)
    
    const matchedPosts = allPosts.filter(post => {
      if (post.id === currentId) return false
      const postTags = parseTags(post.tags)
      return tags.some(tag => postTags.includes(tag))
    }).slice(0, 3)
    
    relatedPosts.value = matchedPosts
  } catch (error) {
    console.error('获取相关推荐失败:', error)
  }
}

const goToPost = (postId) => {
  router.push(`/post/${postId}`)
}

marked.use(markedHighlight({
  langPrefix: 'language-',
  highlight(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(code, { language: lang }).value
      } catch (e) {
        console.error('Highlight error:', e)
      }
    }
    return hljs.highlightAuto(code).value
  }
}))

marked.setOptions({
  breaks: true,
  gfm: true,
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

const addCodeCopyButtons = () => {
  if (!articleContent.value) return
  const preElements = articleContent.value.querySelectorAll('pre')
  preElements.forEach((pre) => {
    if (pre.querySelector('.copy-btn')) return
    const wrapper = document.createElement('div')
    wrapper.className = 'code-container'
    pre.parentNode.insertBefore(wrapper, pre)
    wrapper.appendChild(pre)
    
    const codeElement = pre.querySelector('code')
    let language = ''
    if (codeElement) {
      const classList = codeElement.className
      const langMatch = classList.match(/language-(\w+)/)
      if (langMatch) {
        language = langMatch[1].toUpperCase()
      }
    }
    
    
    // 1. 先插入 langBadge（语言标签在最底层）
    const langBadge = document.createElement('span')
    langBadge.className = 'lang-badge'
    langBadge.textContent = language
    wrapper.appendChild(langBadge)
    
    // 2. 再插入 copyBtn（复制按钮）
    const copyBtn = document.createElement('button')
    copyBtn.className = 'copy-btn'
    copyBtn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg>'
    copyBtn.title = '复制代码'
    copyBtn.onclick = async () => {
      const code = pre.querySelector('code')?.textContent || pre.textContent
      try {
        const textarea = document.createElement('textarea')
        textarea.value = code
        textarea.style.position = 'fixed'
        textarea.style.left = '-9999px'
        textarea.style.top = '0'
        document.body.appendChild(textarea)
        textarea.select()
        document.execCommand('copy')
        document.body.removeChild(textarea)
        copyBtn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"></polyline></svg>'
        copyBtn.title = '已复制'
        setTimeout(() => {
          copyBtn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg>'
          copyBtn.title = '复制代码'
        }, 2000)
      } catch (e) {
        console.error('Copy failed:', e)
      }
    }
    wrapper.appendChild(copyBtn)
    
    // 添加放大按钮
    const zoomBtn = document.createElement('button')
    zoomBtn.className = 'zoom-btn'
    zoomBtn.innerHTML = '🔍'
    zoomBtn.title = '放大代码'
    zoomBtn.addEventListener('click', (e) => {
      e.stopPropagation()
      e.preventDefault()
      const codeContent = pre.querySelector('code')?.outerHTML || pre.innerHTML
      const lang = pre.querySelector('code')?.className?.match(/language-(\w+)/)?.[1]?.toUpperCase() || 'CODE'
      const modal = document.createElement('div')
      modal.className = 'code-modal'
      modal.style.zIndex = '2147483647'
      modal.innerHTML = `
        <div class="code-modal-content">
          <div class="code-modal-header">
            <span class="code-modal-lang">${lang}</span>
            <button class="code-modal-close">×</button>
          </div>
          <pre class="code-modal-pre"><code class="language-${lang.toLowerCase()}">${codeContent}</code></pre>
        </div>
      `
      document.body.appendChild(modal)
      modal.querySelector('.code-modal-close').addEventListener('click', (e) => {
        e.stopPropagation()
        modal.remove()
      })
      modal.addEventListener('click', (e) => {
        if (e.target === modal) modal.remove()
      })
      try {
        hljs.highlightElement(modal.querySelector('code'))
      } catch (err) {
        console.error('Highlight failed:', err)
      }
    }, true)
    wrapper.appendChild(zoomBtn)
  })
}

const addTableZoom = () => {
  if (!articleContent.value) return
  const tables = articleContent.value.querySelectorAll('table')
  tables.forEach((table) => {
    if (table.parentElement?.classList.contains('table-wrapper')) return

    const container = document.createElement('div')
    container.className = 'table-container'

    const parent = table.parentNode
    parent?.replaceChild(container, table)

    const tableWrapper = document.createElement('div')
    tableWrapper.className = 'table-wrapper'
    container.appendChild(tableWrapper)
    tableWrapper.appendChild(table)

    const zoomBtn = document.createElement('button')
    zoomBtn.className = 'zoom-btn'
    zoomBtn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line><line x1="11" y1="8" x2="11" y2="14"></line><line x1="8" y1="11" x2="14" y2="11"></line></svg>'
    zoomBtn.title = '放大表格'
    zoomBtn.addEventListener('click', (e) => {
      e.stopPropagation()
      e.preventDefault()
      const modal = document.createElement('div')
      modal.className = 'table-modal'
      modal.style.zIndex = '2147483647'
      modal.innerHTML = `<div class="table-modal-content"><button class="table-modal-close">×</button><div style="margin-top: 20px; overflow-x: auto; max-height: 80vh;">${table.outerHTML}</div></div>`
      document.body.appendChild(modal)
      modal.querySelector('.table-modal-close').addEventListener('click', (e) => {
        e.stopPropagation()
        modal.remove()
      })
      modal.addEventListener('click', (e) => {
        if (e.target === modal) modal.remove()
      })
    }, true)
    container.appendChild(zoomBtn)
  })
}

watch(renderedContent, async () => {
  await nextTick()

  generateToc()
  addCodeCopyButtons()
  addTableZoom()
  fetchRelatedPosts()
}, { immediate: true })

onMounted(() => {
  postStore.fetchPostById(route.params.id)
  commentStore.fetchCommentsByArticleId(route.params.id)
  window.addEventListener('scroll', handleScroll)
  
  // 从 localStorage 读取用户信息自动填充
  const nickname = localStorage.getItem('nickname') || authStore.nickname || ''
  const username = localStorage.getItem('username') || authStore.username || ''
  
  if (nickname) {
    commentForm.nickname = nickname
  }
  if (username) {
    commentForm.email = username
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
  text-align: left;
  overflow: auto;
}

.prose-container::after {
  content: '';
  display: table;
  clear: both;
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

.copy-btn svg {
  text-align: right;
}

.prose-container p code {
  background: rgba(100, 108, 255, 0.15);
  padding: 0.2em 0.4em;
  border-radius: 4px;
  font-size: 0.85em;
  color: #a5b4fc;
}

.table-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(4px);
}

.table-modal-content {
  position: relative;
  background: #1a1a1a;
  padding: 30px;
  border-radius: 16px;
  max-width: 95vw;
  max-height: 90vh;
  overflow: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  scrollbar-width: thin;
  scrollbar-color: #d1d5db #f3f4f6;
}

.table-modal-close {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 36px;
  height: 36px;
  background: linear-gradient(145deg, #ef4444, #dc2626);
  border: none;
  border-radius: 50%;
  color: #fff;
  font-size: 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
}

.table-modal-close:hover {
  transform: rotate(90deg);
  background: linear-gradient(145deg, #dc2626, #b91c1c);
}

.table-modal-content table {
  display: block;
  width: max-content;
  border-collapse: separate;
  border-spacing: 0;
  font-size: 14px;
  line-height: 1.6;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  background: #1a1a1a;
}

.table-modal-content th {
  background-color: #2d2d2d !important;
  color: #e4e4e7 !important;
  font-weight: 600 !important;
  border-bottom: 2px solid #404040 !important;
  border-right: 1px solid #404040 !important;
  padding: 12px 16px !important;
  text-align: left !important;
}

.table-modal-content td {
  border-bottom: 1px solid #404040 !important;
  border-right: 1px solid #404040 !important;
  padding: 12px 16px !important;
  vertical-align: top !important;
  white-space: nowrap !important;
  color: #e4e4e7 !important;
  background: #1a1a1a !important;
}

.table-modal-content th:last-child,
.table-modal-content td:last-child {
  border-right: none !important;
}

.related-card {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.related-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.prose-container {
  overflow-x: hidden;
}

.prose-container :deep(.table-container) {
  position: relative;
  margin: 1rem 0;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 0 0 1px #e0e0e0;
  overflow-x: auto;
  width: 100%;
}

.prose-container :deep(.table-wrapper) {
  overflow-x: auto;
  max-height: none;
}

.prose-container :deep(table) {
  width: max-content !important;
  min-width: 100%;
  border-collapse: collapse;
  background: white;
  margin: 0;
  white-space: nowrap;
}

.prose-container :deep(th),
.prose-container :deep(td) {
  padding: 0.5rem 0.75rem;
  border: 1px solid #d0d7de;
  text-align: left;
  background: white;
  white-space: normal;
  color: #2c3e50;
}

.prose-container :deep(thead th) {
  background-color: #f6f8fa;
  font-weight: 600;
  border-bottom: 2px solid #d0d7de;
}

.prose-container :deep(pre) {
  overflow-x: auto !important;
  background: #2B2B2B !important;
  padding: 1rem !important;
  border-radius: 8px !important;
  margin: 1rem 0 !important;
  float: none !important;
  display: block !important;
  width: 100% !important;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
  position: relative !important;
}

/* 代码块内所有元素的默认样式 */
.prose-container :deep(pre code),
.prose-container :deep(pre code *) {
  color: #A9B7C6 !important;
  font-weight: normal !important;
  font-style: normal !important;
}

/* IDEA Darcula 风格配色 */
.prose-container :deep(.hljs-variable),
.prose-container :deep(.hljs-params),
.prose-container :deep(.hljs-name) {
  color: #FFFFFF !important;
  font-weight: 500 !important;
}

/* YAML 属性键 - 黄色 */
.prose-container :deep(.hljs-attr) {
  color: #FFC66D !important;
  font-weight: 500 !important;
}

.prose-container :deep(.hljs-title),
.prose-container :deep(.hljs-class),
.prose-container :deep(.hljs-type),
.prose-container :deep(.hljs-function),
.prose-container :deep(.hljs-built_in) {
  color: #FFC66D !important;
  font-weight: bold !important;
}

.prose-container :deep(.hljs-keyword),
.prose-container :deep(.hljs-selector-tag) {
  color: #CC7832 !important;
  font-weight: bold !important;
}

.prose-container :deep(.hljs-string),
.prose-container :deep(.hljs-literal) {
  color: #6A8759 !important;
}

.prose-container :deep(.hljs-comment) {
  color: #808080 !important;
  font-style: italic !important;
}

.prose-container :deep(.hljs-number) {
  color: #6897BB !important;
}

/* 行内代码（修复后） */
.prose-container :deep(:not(pre) > code) {
  font-family: 'Fira Code', 'Monaco', 'Consolas', monospace !important;
  background: #f3f4f6 !important;
  padding: 0.2em 0.4em !important;
  border-radius: 4px !important;
  font-size: 0.85em !important;
}

/* --- [IntelliJ IDEA Darcula 风格 - 已修复变量可见性] --- */

/* 1. 代码块基础样式 */
.code-container {
    background: #2B2B2B !important; /* 统一背景 */
    border-radius: 8px !important;
    margin: 1rem 0 !important;
    padding: 0 !important;
    overflow: hidden !important;
    position: relative !important;
}

.code-container pre {
    background: transparent !important; /* 关键：去除内部 Pre 的背景，防止遮挡 */
    padding: 1.2rem !important;
    margin: 0 !important;
    border-radius: 0 !important;
    color: #A9B7C6 !important; /* 默认文字颜色（浅灰白） */
    font-family: 'JetBrains Mono', monospace !important;
}

/* 2. IDEA Darcula 风格完整配色 */

/* 重置所有代码样式 - 最高优先级 */
.code-container pre code *,
.code-container pre code {
    color: #A9B7C6 !important; /* 默认文本颜色 */
    font-weight: normal !important;
    font-style: normal !important;
}

/* 变量、参数 - 纯白色 */
.code-container .hljs-variable,
.code-container .hljs-params,
.code-container .hljs-name,
.code-container .hljs-selector-attr,
.code-container .hljs-selector-pseudo,
.code-container .hljs-doctype {
    color: #FFFFFF !important; /* 纯白色 */
    font-weight: 500 !important;
}

/* 类名、方法名、类型 - 亮黄色 */
.code-container .hljs-title,
.code-container .hljs-class,
.code-container .hljs-type,
.code-container .hljs-function,
.code-container .hljs-built_in,
.code-container .hljs-section,
.code-container .hljs-selector-class {
    color: #FFC66D !important; /* IDEA 亮黄色 */
    font-weight: bold !important;
}

/* 关键字 - 橙色 */
.code-container .hljs-keyword,
.code-container .hljs-selector-tag,
.code-container .hljs-subst,
.code-container .hljs-request,
.code-container .hljs-status {
    color: #CC7832 !important; /* 橙色 */
    font-weight: bold !important;
}

/* 字符串 - 绿色 */
.code-container .hljs-string,
.code-container .hljs-literal,
.code-container .hljs-code,
.code-container .hljs-value {
    color: #6A8759 !important; /* 绿色 */
}

/* 注释 - 灰色斜体 */
.code-container .hljs-comment,
.code-container .hljs-doctag,
.code-container .hljs-quote,
.code-container .hljs-template-comment {
    color: #808080 !important;
    font-style: italic !important;
}

/* 数字 - 蓝色 */
.code-container .hljs-number,
.code-container .hljs-literal {
    color: #6897BB !important; /* 蓝色 */
}

/* 注解/元数据 - 黄绿色 */
.code-container .hljs-meta,
.code-container .hljs-meta-string,
.code-container .hljs-annotation,
.code-container .hljs-meta-keyword {
    color: #BBB529 !important;
}

/* 标签 - 橙色 */
.code-container .hljs-tag,
.code-container .hljs-name {
    color: #CC7832 !important;
}

/* 属性名 - 紫色 */
.code-container .hljs-attribute {
    color: #D0D0FF !important;
}

/* 预处理指令 */
.code-container .hljs-meta-preprocessor,
.code-container .hljs-pragma {
    color: #CC7832 !important;
}

/* 正则表达式 */
.code-container .hljs-regexp {
    color: #6A8759 !important;
}

/* 名称空间 */
.code-container .hljs-namespace {
    color: #E0E0E0 !important;
}

/* YAML 特定样式 - 按照 IDEA YAML 显示样式 */
/* YAML 文档开始标记 (---) */
.code-container .hljs-meta {
    color: #CC7832 !important;
    font-weight: bold !important;
}

/* YAML 键名 (属性) - 黄色，最高优先级 */
/* 使用更具体的选择器确保优先级 */
.code-container pre code .hljs-attr,
.prose-container :deep(.hljs-attr) {
    color: #FFC66D !important; /* 黄色 */
    font-weight: 500 !important;
}

/* YAML 字符串值 */
.code-container .hljs-string {
    color: #6A8759 !important; /* 绿色 */
}

/* YAML 数字值 */
.code-container .hljs-number {
    color: #6897BB !important; /* 蓝色 */
}

/* YAML 布尔值 (true/false) */
.code-container .hljs-literal {
    color: #CC7832 !important; /* 橙色 */
    font-weight: bold !important;
}

/* YAML 注释 */
.code-container .hljs-comment {
    color: #808080 !important;
    font-style: italic !important;
}

/* 强制所有 span 元素继承颜色，但保留语法高亮 */
.code-container pre code span {
    color: inherit !important;
}

/* 确保代码块内语法高亮正确显示 */
.code-container pre code .hljs-keyword { color: #CC7832 !important; font-weight: bold !important; }
.code-container pre code .hljs-string { color: #6A8759 !important; }
.code-container pre code .hljs-number { color: #6897BB !important; }
.code-container pre code .hljs-comment { color: #808080 !important; font-style: italic !important; }
.code-container pre code .hljs-title,
.code-container pre code .hljs-class,
.code-container pre code .hljs-function { color: #FFC66D !important; font-weight: bold !important; }
.code-container pre code .hljs-attr { color: #FFC66D !important; font-weight: 500 !important; }
.code-container pre code .hljs-variable,
.code-container pre code .hljs-params,
.code-container pre code .hljs-name { color: #FFFFFF !important; font-weight: 500 !important; }

/* 确保 prose-container 的语法高亮优先级高于 code-container */
.prose-container :deep(.hljs-keyword) { color: #CC7832 !important; font-weight: bold !important; }
.prose-container :deep(.hljs-string) { color: #6A8759 !important; }
.prose-container :deep(.hljs-number) { color: #6897BB !important; }
.prose-container :deep(.hljs-comment) { color: #808080 !important; font-style: italic !important; }
.prose-container :deep(.hljs-title),
.prose-container :deep(.hljs-class),
.prose-container :deep(.hljs-function) { color: #FFC66D !important; font-weight: bold !important; }
.prose-container :deep(.hljs-attr) { color: #FFC66D !important; font-weight: 500 !important; }
.prose-container :deep(.hljs-variable),
.prose-container :deep(.hljs-params),
.prose-container :deep(.hljs-name) { color: #FFFFFF !important; font-weight: 500 !important; }

/* 3. [核心修复] 语言标签：强制物理靠右 */
.lang-badge {
  position: absolute !important;
  right: 12px !important;
  transform: translateX(0) !important;
  bottom: 8px !important;
  left: auto !important;
  top: auto !important;
  padding: 4px 10px !important;
  background: rgba(0, 0, 0, 0.7) !important;
  color: #ffffffcc !important;
  font-size: 11px !important;
  font-weight: 600 !important;
  border-radius: 4px !important;
  pointer-events: none !important;
  z-index: 100 !important;
}

/* 4. 复制按钮：固定在左下角 */
.copy-btn {
  position: absolute !important;
  bottom: 8px !important;
  left: 12px !important;
  z-index: 99 !important;
  display: flex !important;
  align-items: center !important;
  gap: 4px !important;
  padding: 6px 12px !important;
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 6px !important;
  color: #a1a1aa !important;
  cursor: pointer !important;
}

/* 5. 放大按钮：紧随复制按钮右侧 */
.zoom-btn {
  position: absolute !important;
  bottom: 8px !important;
  left: 70px !important;
  z-index: 98 !important;
  padding: 6px 10px !important;
  background: linear-gradient(145deg, #6366f1, #8b5cf6) !important;
  border: none !important;
  border-radius: 6px !important;
  color: #fff !important;
  font-size: 12px !important;
  cursor: pointer !important;
}

.copy-btn:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  color: #fff !important;
}</style>

<style>
/* 表格放大模态框样式（非scoped，因为动态添加到body） */
.table-modal {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  width: 100vw !important;
  height: 100vh !important;
  background: rgba(0, 0, 0, 0.9) !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  z-index: 9999 !important;
  backdrop-filter: blur(4px);
}

.table-modal-content {
  position: relative !important;
  background: #1a1a1a !important;
  padding: 30px !important;
  border-radius: 16px !important;
  max-width: 95vw !important;
  max-height: 90vh !important;
  overflow: auto !important;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5) !important;
}

.table-modal-close {
  position: absolute !important;
  top: 10px !important;
  right: 15px !important;
  background: #ef4444 !important;
  color: white !important;
  border: none !important;
  border-radius: 50% !important;
  width: 32px !important;
  height: 32px !important;
  font-size: 20px !important;
  cursor: pointer !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  transition: background 0.3s !important;
}

.table-modal-close:hover {
  background: #dc2626 !important;
}

.table-modal-content table {
  display: table !important;
  width: 100% !important;
  background: #1a1a1a !important;
  color: #fff !important;
}

.table-modal-content th,
.table-modal-content td {
  border: 1px solid #404040 !important;
  padding: 12px !important;
  white-space: normal !important;
  background: #2d2d2d !important;
  color: #fff !important;
}

.table-modal-content th:last-child,
.table-modal-content td:last-child {
  border-right: 1px solid #404040 !important;
}

/* 代码放大模态框 */
.code-modal {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  width: 100vw !important;
  height: 100vh !important;
  background: rgba(0, 0, 0, 0.95) !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  z-index: 2147483647 !important;
  backdrop-filter: blur(4px);
}

.code-modal-content {
  position: relative !important;
  background: #1e1e1e !important;
  border-radius: 12px !important;
  max-width: 95vw !important;
  max-height: 90vh !important;
  width: auto !important;
  overflow: hidden !important;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5) !important;
}

.code-modal-header {
  display: flex !important;
  justify-content: space-between !important;
  align-items: center !important;
  padding: 12px 16px !important;
  background: #2d2d2d !important;
  border-bottom: 1px solid #404040 !important;
}

.code-modal-lang {
  color: #10b981 !important;
  font-weight: 600 !important;
  font-size: 14px !important;
}

.code-modal-close {
  background: #ef4444 !important;
  color: white !important;
  border: none !important;
  border-radius: 50% !important;
  width: 28px !important;
  height: 28px !important;
  font-size: 18px !important;
  cursor: pointer !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  transition: background 0.3s !important;
}

.code-modal-close:hover {
  background: #dc2626 !important;
}

.code-modal-pre {
  margin: 0 !important;
  padding: 20px !important;
  background: #1e1e1e !important;
  overflow: auto !important;
  max-height: calc(90vh - 60px) !important;
}

.code-modal-pre code {
  font-family: 'Fira Code', 'Monaco', 'Consolas', monospace !important;
  font-size: 14px !important;
  line-height: 1.6 !important;
  color: #abb2bf !important;
}
</style>
