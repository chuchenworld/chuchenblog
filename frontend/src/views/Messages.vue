<template>
  <MainLayout>
    <div class="messages-page">
      <!-- 滚动评论跑马灯 -->
      <div class="scroll-comment-container">
        <div class="scroll-wrapper" ref="scrollWrapper" @mouseenter="handleScrollHover(true)" @mouseleave="handleScrollHover(false)">
          <div class="scroll-track">
            <div
              v-for="comment in displayComments"
              :key="comment.id"
              class="scroll-item"
              :class="{ 'is-hovered': hoveredCommentId === comment.id }"
              @mouseenter="hoveredCommentId = comment.id"
              @mouseleave="hoveredCommentId = null"
            >
              <div class="scroll-content">
                <div class="scroll-nickname">{{ comment.nickname }}</div>
                <div class="scroll-text">
                  <span v-if="hoveredCommentId !== comment.id">{{ truncateContent(comment.content) }}</span>
                  <span v-else>{{ comment.content }}</span>
                </div>
              </div>
              <div v-if="hoveredCommentId === comment.id && !comment.isAnonymous && comment.email" class="scroll-detail">
                <span class="detail-email">{{ comment.email }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-card class="message-card">
        <template #header>
          <div class="card-header">
            <h3>留下你的评论</h3>
          </div>
        </template>
        <div v-if="!isLoggedIn" class="text-center py-8">
          <p class="text-gray-500">请登录后发布留言</p>
          <el-button type="primary" @click="goToLogin" class="mt-4">去登录</el-button>
        </div>
        <el-form v-else :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="name">
            <el-input
              v-model="form.name"
              :placeholder="!form.isAnonymous ? '已填充用户名，可修改' : '请输入您的姓名'"
            />
          </el-form-item>
          <el-form-item prop="email">
            <el-input
              v-model="form.email"
              :placeholder="!form.isAnonymous ? '已填充邮箱，可修改' : '请输入您的邮箱（匿名时可选）'"
            />
          </el-form-item>
          <el-form-item prop="content">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="4"
              placeholder="请输入您的留言内容（不超过50字）"
            />
            <span class="content-count">{{ form.content.length }}/50</span>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="form.isAnonymous" @change="handleAnonymousChange">匿名评论</el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit">提交留言</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="messages-list">
        <h3>留言列表</h3>
        <el-empty v-if="messages.length === 0" description="暂无留言" />
        <div v-else class="message-items">
          <div v-for="message in messages" :key="message.id" class="message-item">
            <div class="message-header">
              <span class="message-name">{{ message.nickname }}</span>
              <span v-if="message.isAnonymous" class="anonymous-badge">匿名</span>
              <span class="message-time">{{ formatDate(message.createTime) }}</span>
            </div>
            <div class="message-content">{{ message.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElEmpty } from 'element-plus'
import { useRouter } from 'vue-router'
import MainLayout from '@/components/MainLayout.vue'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'

const authStore = useAuthStore()
const router = useRouter()
const formRef = ref()
const scrollWrapper = ref()
const hoveredCommentId = ref(null)
let scrollInterval = null

const isLoggedIn = computed(() => authStore.isAuthenticated)

const form = reactive({
  name: '',
  email: '',
  content: '',
  isAnonymous: false
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [],
  content: [
    { required: true, message: '请输入留言内容', trigger: 'blur' },
    { max: 50, message: '留言内容不能超过50字', trigger: 'blur' }
  ]
}

const messages = ref([])
const displayComments = ref([])

const displayEmailRule = () => {
  if (isLoggedIn.value && !form.isAnonymous) {
    return false
  }
  return true
}

function truncateContent(content) {
  if (!content) return ''
  return content.length > 10 ? content.substring(0, 10) + '...' : content
}

function goToLogin() {
  router.push('/login')
}

function handleAnonymousChange() {
  if (form.isAnonymous) {
    form.name = ''
    form.email = ''
  } else {
    if (isLoggedIn.value) {
      form.name = authStore.nickname || ''
      form.email = authStore.username || ''
    }
  }
}

async function fetchMessages() {
  try {
    const res = await request.get('/comments/all')
    if (res.data) {
      messages.value = res.data
      displayComments.value = [...res.data, ...res.data]
    }
  } catch (err) {
    console.error('获取留言失败:', err)
    messages.value = []
    displayComments.value = []
  }
}

function startScroll() {
  if (scrollInterval) clearInterval(scrollInterval)
  scrollInterval = setInterval(() => {
    if (!scrollWrapper.value || hoveredCommentId.value) return
    const track = scrollWrapper.value.querySelector('.scroll-track')
    if (!track) return
    
    const firstItem = track.firstElementChild
    if (firstItem) {
      track.appendChild(firstItem)
    }
  }, 3000)
}

function handleScrollHover(isEnter) {
  if (isEnter) {
    if (scrollInterval) {
      clearInterval(scrollInterval)
      scrollInterval = null
    }
  } else {
    startScroll()
  }
}

async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await request.post('/comments/guest', {
          nickname: form.name,
          email: form.email,
          content: form.content,
          isAnonymous: form.isAnonymous
        })
        
        if (res.data) {
          messages.value.unshift(res.data)
          displayComments.value = [...messages.value, ...messages.value]
          form.name = isLoggedIn.value && !form.isAnonymous ? (authStore.nickname || '') : ''
          form.email = isLoggedIn.value && !form.isAnonymous ? (authStore.email || '') : ''
          form.content = ''
          ElMessage.success('留言成功')
        }
      } catch (err) {
        ElMessage.error('留言失败，请重试')
        console.error('留言失败:', err)
      }
    }
  })
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  if (isLoggedIn.value) {
    form.name = authStore.nickname || ''
    form.email = authStore.username || ''
  } else {
    const tempUserId = localStorage.getItem('temp_user_id')
    form.name = tempUserId ? `游客${tempUserId}` : ''
  }
  
  fetchMessages()
  
  setTimeout(() => {
    startScroll()
  }, 1000)
})

onUnmounted(() => {
  if (scrollInterval) {
    clearInterval(scrollInterval)
  }
})
</script>

<style scoped>
.messages-page {
  padding: 30px 20px;
  max-width: 800px;
  margin: 0 auto;
}

.header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.header h2 {
  margin: 0;
  color: #f4f9fa;
  font-family: Impact, Charcoal, sans-serif;
  font-weight: 900;
  font-size: 44px;
  box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.25), 0px 4px 8px 0px rgba(0, 0, 0, 0.25), 0px 4px 8px 0px rgba(0, 0, 0, 0.25), 0px 4px 8px 0px rgba(0, 0, 0, 0.25), 0px 4px 8px 0px rgba(0, 0, 0, 0.25), 0px 4px 8px 0px rgba(0, 0, 0, 0.25), 0px 4px 8px 0px rgba(0, 0, 0, 0.25);
}

/* 滚动评论跑马灯样式 */
.scroll-comment-container {
  width: 100%;
  max-width: 800px;
  height: 220px;
  margin: 0 auto 30px;
  background: rgba(144, 238, 144, 0.3);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(144, 238, 144, 0.2);
  border: 1px solid rgba(144, 238, 144, 0.4);
}

.scroll-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.scroll-track {
  display: flex;
  flex-direction: row;
  animation: scrollLeft 5s linear infinite;
}

.scroll-track:hover {
  animation-play-state: paused;
}

@keyframes scrollLeft {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

.scroll-item {
  flex-shrink: 0;
  width: 320px;
  height: 80px;
  padding: 16px 20px;
  margin-right: 20px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.scroll-item.is-hovered {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.02);
}

.scroll-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  height: 100%;
  justify-content: center;
  box-sizing: border-box;
}

.scroll-nickname {
  font-weight: 500;
  color: #f1f2f3;
  font-size: 14px;
  line-height: 1.4;
}

.scroll-text {
  color: #dde1e9;
  font-size: 13px;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.scroll-detail {
  margin-top: 4px;
}

.detail-email {
  font-size: 11px;
  color: #909399;
}

.message-card {
  margin-bottom: 30px;
  transition: all 0.3s;
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
}

.message-card .el-card__header {
  background: transparent !important;
  border-bottom: 1px solid #e4e7ed;
}

.message-card .el-card__body {
  background: transparent !important;
}

.card-header h3 {
  margin: 0;
  color: #f0f2f4;
  font-family: 'KaiTi', 'STKaiti', 'SimKai', 'AR PL UKai CN', 'AR PL UKai HK', 'AR PL UKai TW', 'AR PL UKai TW MBE', 'AR PL KaitiM GB', 'KaiTi_GB2312', 'TW-Kai', serif;
  font-weight: bold;
}

/* 透明玻璃状输入框 */
.message-card :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  height: 45px;
}

.message-card :deep(.el-input__inner) {
  background: transparent !important;
  color: #f1f2f3;
  font-size: 15px;
  height: 100%;
}

.message-card :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #f7f7f8;
  min-height: 180px;
  font-size: 15px;
}

/* 匿名评论复选框文字样式 */
.message-card :deep(.el-checkbox__label) {
  font-family: 'KaiTi', 'STKaiti', 'SimKai', 'AR PL UKai CN', 'AR PL UKai HK', 'AR PL UKai TW', 'AR PL UKai TW MBE', 'AR PL KaitiM GB', 'KaiTi_GB2312', 'TW-Kai', serif;
  font-weight: bold;
}

/* 表单元素间距 */
.message-card :deep(.el-form-item) {
  margin-bottom: 20px;
}

/* 美化提交按钮 */
.message-card :deep(.el-button--primary) {
  width: 120px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.message-card :deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.content-count {
  display: block;
  text-align: right;
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.messages-list h3 {
  margin: 0 0 16px;
  color: #fafafa;
}

.message-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-item {
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
}

.message-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.message-name {
  font-weight: 500;
  color: #f1f2f3;
}

.anonymous-badge {
  font-size: 12px;
  padding: 2px 6px;
  background: #fff7e6;
  color: #d48806;
  border-radius: 4px;
}

.message-time {
  margin-left: auto;
  color: #909399;
  font-size: 14px;
}

.message-content {
  color: #dde1e9;
  line-height: 1.6;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .messages-page {
    padding: 20px 15px;
    max-width: 100%;
  }
  
  .scroll-comment-container {
    height: 140px;
  }
  
  .scroll-item {
    width: 280px;
    height: 70px;
    padding: 12px 16px;
    margin-right: 12px;
  }
  
  .message-card :deep(.el-form-item) {
    margin-bottom: 16px;
  }
  
  .message-card :deep(.el-input__wrapper) {
    width: 100%;
    margin: 0;
  }
  
  .message-card :deep(.el-button--primary) {
    width: 100%;
  }
}
</style>