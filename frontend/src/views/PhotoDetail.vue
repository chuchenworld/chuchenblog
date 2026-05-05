<template>
  <MainLayout>
    <div class="photo-detail-page">
      <!-- 返回按钮 -->
      <div class="back-button" @click="goBack">
        <el-icon class="back-icon"><ArrowLeft /></el-icon>
        <span>返回</span>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state" v-loading="loading" element-loading-text="加载中...">
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="error-state">
        <el-empty description="加载失败" :image-size="200" />
        <p class="error-text">{{ error }}</p>
      </div>

      <!-- 照片详情 -->
      <div v-else-if="photo" class="photo-content">
        <div class="photo-main">
          <img 
            :src="photo.url" 
            :alt="photo.caption || photo.name"
            class="main-photo"
          />
        </div>

        <div class="photo-info">
          <h1 class="photo-title">{{ photo.caption || photo.name || '照片' }}</h1>
          
          <div class="photo-meta">
            <span v-if="photo.albumName" class="album-tag">
              <el-icon class="mr-1"><Folder /></el-icon>
              {{ photo.albumName }}
            </span>
            <span v-if="photo.shootTime" class="shoot-time">
              <el-icon class="mr-1"><Calendar /></el-icon>
              {{ formatDate(photo.shootTime) }}
            </span>
          </div>

          <div v-if="photo.tags && photo.tags.length > 0" class="photo-tags">
            <el-tag 
              v-for="tag in photo.tags" 
              :key="tag" 
              size="small"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
          </div>

          <p v-if="photo.description" class="photo-description">
            {{ photo.description }}
          </p>
        </div>

        <!-- 评论区域 -->
        <div class="comment-section">
          <h2 class="comment-title">
            <el-icon class="mr-2"><ChatLineSquare /></el-icon>
            评论区
          </h2>

          <!-- 评论输入 -->
          <div class="comment-input-area">
            <textarea 
              v-model="commentForm.content"
              class="comment-input"
              placeholder="写下你的评论..."
              rows="3"
              maxlength="500"
            ></textarea>
            <div class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <input
                  v-model="commentForm.nickname"
                  type="text"
                  class="comment-input-field"
                  placeholder="昵称"
                />
              </div>
              <div>
                <input
                  v-model="commentForm.email"
                  type="email"
                  class="comment-input-field"
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
                    class="comment-checkbox"
                  />
                  <span class="text-sm text-gray-600">匿名评论</span>
                </label>
                <label class="flex items-center gap-2 cursor-pointer">
                  <input
                    v-model="commentForm.notifyAuthor"
                    type="checkbox"
                    class="comment-checkbox"
                  />
                  <span class="text-sm text-gray-600">通过邮件通知作者</span>
                </label>
              </div>
              <div class="flex items-center gap-3">
                <span class="char-count">{{ commentForm.content.length }}/500</span>
                <button
                  @click="submitComment"
                  :disabled="!commentForm.content.trim() || !commentForm.nickname.trim() || !commentForm.email.trim()"
                  class="comment-submit-btn"
                >
                  发布评论
                </button>
              </div>
            </div>
          </div>

          <!-- 评论列表 -->
          <div v-if="comments.length === 0" class="empty-comments">
            <el-empty description="暂无评论，快来发表第一条评论吧" :image-size="100" />
          </div>

          <div v-else class="comment-list">
            <div 
              v-for="comment in comments" 
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-avatar">
                <span class="avatar-initial">{{ getAvatarInitial(comment.nickname) }}</span>
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.nickname || '匿名用户' }}</span>
                  <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-actions">
                  <button class="like-btn" @click="likeComment(comment.id)">
                    <el-icon><Star /></el-icon>
                    <span>{{ comment.likeCount || 0 }}</span>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 评论分页 -->
          <div v-if="commentTotal > commentPageSize" class="comment-pagination">
            <el-pagination 
              v-model:current-page="commentPageNum" 
              v-model:page-size="commentPageSize" 
              :total="commentTotal" 
              layout="prev, pager, next" 
              @current-change="loadComments"
            />
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Folder, Calendar, ChatLineSquare, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import MainLayout from '@/components/MainLayout.vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

// 状态
const loading = ref(false)
const error = ref('')
const photo = ref(null)
const comments = ref([])
const commentTotal = ref(0)
const commentPageNum = ref(1)
const commentPageSize = ref(10)

// 评论表单
const commentForm = reactive({
  content: '',
  nickname: '',
  email: '',
  isAnonymous: false,
  notifyAuthor: false
})

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 返回上一页
function goBack() {
  router.back()
}

// 加载照片信息
async function loadPhoto() {
  const photoId = route.params.id
  if (!photoId) return
  
  loading.value = true
  error.value = ''
  
  try {
    const res = await request.get(`/photos/${photoId}`)
    if (res.data) {
      photo.value = res.data
    }
  } catch (err) {
    console.error('【PhotoDetail】照片信息获取失败:', err.message)
    error.value = '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 加载评论列表
async function loadComments() {
  const photoId = route.params.id
  if (!photoId) return
  
  try {
    const res = await request.get(`/comments/photo/${photoId}`, {
      params: {
        pageNum: commentPageNum.value,
        pageSize: commentPageSize.value
      }
    })
    if (res.data) {
      comments.value = res.data.records || res.data
      commentTotal.value = res.data.total || res.data.length
    }
  } catch (err) {
    console.error('【PhotoDetail】评论列表获取失败:', err.message)
  }
}

// 获取头像首字母
function getAvatarInitial(nickname) {
  if (!nickname) return '匿'
  return nickname.charAt(0).toUpperCase()
}

// 提交评论
async function submitComment() {
  if (!commentForm.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  if (!commentForm.nickname.trim() || !commentForm.email.trim()) {
    ElMessage.warning('请填写昵称和邮箱')
    return
  }
  
  const photoId = route.params.id
  if (!photoId) return
  
  try {
    await request.post('/comments/photo', {
      photoId: photoId,
      content: commentForm.content.trim(),
      nickname: commentForm.nickname.trim(),
      email: commentForm.email.trim(),
      isAnonymous: commentForm.isAnonymous,
      notifyAuthor: commentForm.notifyAuthor
    })
    
    // 清空表单
    commentForm.content = ''
    commentForm.nickname = ''
    commentForm.email = ''
    commentForm.isAnonymous = false
    commentForm.notifyAuthor = false
    
    // 刷新评论列表
    commentPageNum.value = 1
    await loadComments()
    
    ElMessage.success('评论成功')
    
    if (commentForm.notifyAuthor) {
      ElMessage.info('已通知作者')
    }
  } catch (err) {
    console.error('【PhotoDetail】评论提交失败:', err.message)
    ElMessage.error('评论失败，请稍后重试')
  }
}

// 点赞评论
async function likeComment(commentId) {
  try {
    await request.post(`/comments/${commentId}/like`)
    await loadComments()
  } catch (err) {
    console.error('【PhotoDetail】点赞失败:', err.message)
  }
}

// 生命周期
onMounted(() => {
  loadPhoto()
  loadComments()
  
  // 从 localStorage 读取用户信息自动填充
  const nickname = localStorage.getItem('nickname') || ''
  const username = localStorage.getItem('username') || ''
  
  if (nickname) {
    commentForm.nickname = nickname
  }
  if (username) {
    commentForm.email = username
  }
})
</script>

<style scoped>
.photo-detail-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 8px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.back-button:hover {
  background: rgba(255, 255, 255, 0.9);
}

.back-icon {
  font-size: 18px;
}

.loading-state {
  padding: 60px 0;
}

.error-state {
  text-align: center;
  padding: 60px 0;
}

.error-text {
  color: #e74c3c;
  margin-top: 16px;
}

.photo-content {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.photo-main {
  width: 100%;
  max-height: 600px;
  overflow: hidden;
}

.main-photo {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: transparent;
}

.photo-info {
  padding: 24px;
}

.photo-title {
  margin: 0 0 16px 0;
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
}

.photo-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.album-tag {
  display: flex;
  align-items: center;
  padding: 4px 12px;
  background: #3498db;
  color: #fff;
  border-radius: 20px;
  font-size: 13px;
}

.shoot-time {
  display: flex;
  align-items: center;
  color: #95a5a6;
  font-size: 14px;
}

.photo-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.tag-item {
  background: rgba(255, 255, 255, 0.5);
  color: #2c3e50;
}

.photo-description {
  margin: 0;
  color: #666;
  font-size: 15px;
  line-height: 1.7;
}

.comment-section {
  margin-top: 30px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.comment-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.comment-input-area {
  margin-bottom: 24px;
}

.comment-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.comment-input:focus {
  outline: none;
  border-color: #3498db;
}

.comment-input-field {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.comment-input-field:focus {
  outline: none;
  border-color: #3498db;
}

.comment-checkbox {
  width: 16px;
  height: 16px;
  color: #3498db;
  border-color: #ddd;
}

.comment-submit-btn {
  padding: 8px 16px;
  background: #3498db;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.comment-submit-btn:hover:not(:disabled) {
  background: #2980b9;
}

.comment-submit-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}

.char-count {
  font-size: 13px;
  color: #95a5a6;
}

.empty-comments {
  text-align: center;
  padding: 40px 0;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3498db, #9b59b6);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-initial {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: #2c3e50;
}

.comment-time {
  font-size: 12px;
  color: #95a5a6;
}

.comment-text {
  margin: 0 0 10px 0;
  color: #444;
  line-height: 1.6;
}

.comment-actions {
  display: flex;
  gap: 20px;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  color: #95a5a6;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.3s;
}

.like-btn:hover {
  color: #e74c3c;
}

.comment-pagination {
  margin-top: 20px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .photo-detail-page {
    padding: 16px;
  }
  
  .photo-info {
    padding: 16px;
  }
  
  .photo-title {
    font-size: 20px;
  }
  
  .comment-section {
    padding: 16px;
  }
  
  .comment-item {
    padding: 12px 0;
  }
  
  .avatar-img {
    width: 40px;
    height: 40px;
  }
}
</style>