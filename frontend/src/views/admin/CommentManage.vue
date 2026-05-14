<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <h1 class="text-2xl font-bold text-gray-800">{{ isArticleCommentsMode ? '文章评论管理' : '留言板管理' }}</h1>
      </div>
    </div>

    <!-- 留言板背景设置（仅在留言板模式显示） -->
    <div v-if="!isArticleCommentsMode" class="bg-white rounded-lg shadow">
      <div class="p-6 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-800">留言板背景设置</h2>
      </div>
      <div class="p-6">
        <el-form label-width="120px">
          <el-form-item label="当前背景">
            <div v-if="messagesBackgroundUrl" class="mb-4">
              <video
                v-if="isVideo(messagesBackgroundUrl)"
                :src="messagesBackgroundUrl"
                class="w-64 h-36 object-cover rounded"
                autoplay
                muted
                loop
                playsinline
              />
              <img
                v-else
                :src="messagesBackgroundUrl"
                alt="留言板背景"
                class="w-64 h-36 object-cover rounded"
              />
            </div>
            <div v-else class="mb-4 text-gray-500">
              暂无背景，将使用默认渐变背景
            </div>
          </el-form-item>
          <el-form-item label="上传新背景">
            <el-upload
              class="upload-demo"
              action="/api/admin/upload"
              :headers="{ Authorization: `Bearer ${authStore.token}` }"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :limit="1"
              :auto-upload="true"
              accept="image/*,video/mp4"
            >
              <el-button type="primary">点击上传</el-button>
              <template #tip>
                <div class="el-upload__tip">
                  支持上传 JPG/PNG 图片或 MP4 视频作为背景，且不超过 50MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
          <el-form-item label="背景URL">
            <el-input
              v-model="messagesBackgroundUrl"
              placeholder="请输入背景URL或上传文件"
              class="w-full"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveMessagesBackground" :loading="bgLoading">保存设置</el-button>
            <el-button @click="clearMessagesBackground">清除背景</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow">
      <div class="p-6 border-b border-gray-200">
        <div class="flex items-center gap-4">
          <el-input
            v-model="searchQuery"
            placeholder="搜索留言内容或访客昵称"
            prefix-icon="Search"
            class="w-64"
            clearable
          />
          <el-button type="primary" @click="fetchComments">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="comments.length > 0" class="overflow-x-auto hidden lg:block">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">留言者</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">邮箱</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">留言内容</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">匿名</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">时间</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="comment in comments" :key="comment.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ comment.id }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                <div class="flex items-center gap-2">
                  <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center">
                    <span class="text-white text-xs font-medium">{{ getAvatarInitial(comment.nickname) }}</span>
                  </div>
                  {{ comment.nickname }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ comment.email || '-' }}
              </td>
              <td class="px-6 py-4 text-sm text-gray-700 max-w-md">
                <div class="truncate" :title="comment.content">{{ comment.content }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="['px-2 py-1 text-xs rounded-full', comment.isAnonymous ? 'bg-orange-100 text-orange-800' : 'bg-green-100 text-green-800']">
                  {{ comment.isAnonymous ? '是' : '否' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(comment.createTime) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <el-button type="danger" size="small" @click="handleDeleteComment(comment)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-if="comments.length > 0" class="lg:hidden space-y-3">
        <div
          v-for="comment in comments"
          :key="comment.id"
          class="bg-white rounded-lg shadow p-3 border border-gray-200"
        >
          <div class="flex items-center justify-between mb-2">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center">
                <span class="text-white text-xs font-medium">{{ getAvatarInitial(comment.nickname) }}</span>
              </div>
              <div>
                <div class="font-medium text-gray-900 text-sm">{{ comment.nickname }}</div>
                <div class="text-xs text-gray-500">{{ comment.email || '-' }}</div>
              </div>
            </div>
            <span :class="['px-2 py-0.5 text-xs rounded-full', comment.isAnonymous ? 'bg-orange-100 text-orange-800' : 'bg-green-100 text-green-800']">
              {{ comment.isAnonymous ? '匿名' : '实名' }}
            </span>
          </div>

          <div class="text-sm text-gray-700 mb-2 line-clamp-3">{{ comment.content }}</div>

          <div class="flex items-center justify-between text-xs text-gray-500 mb-2">
            <span>{{ formatDate(comment.createTime) }}</span>
          </div>

          <div class="flex gap-2 pt-2 border-t border-gray-100">
            <el-button type="danger" size="mini" @click="handleDeleteComment(comment)">
              删除
            </el-button>
          </div>
        </div>
      </div>

      <div v-else class="p-12 text-center">
        <svg class="w-16 h-16 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
        </svg>
        <p class="text-gray-400">暂无留言</p>
      </div>

      <div v-if="comments.length > 0" class="p-6 border-t border-gray-200 flex items-center justify-between">
        <div class="text-sm text-gray-500">
          共 {{ total }} 条留言
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'
import { useRoute } from 'vue-router'

const authStore = useAuthStore()
const route = useRoute()

// 当前管理的文章ID（如果有）
const currentArticleId = ref(null)
const comments = ref([])
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const messagesBackgroundUrl = ref('')
const bgLoading = ref(false)

// 判断是否是文章评论管理模式
const isArticleCommentsMode = computed(() => {
  return !!currentArticleId.value
})

const isVideo = (url) => {
  return url && url.toLowerCase().endsWith('.mp4')
}

const loadMessagesBackground = async () => {
  try {
    const response = await request.get('/admin/config/messages-bg')
    if (response && response.code === 200 && response.data) {
      messagesBackgroundUrl.value = response.data
    }
  } catch (error) {
    console.error('加载留言板背景失败:', error)
  }
}

const saveMessagesBackground = async () => {
  bgLoading.value = true
  try {
    await request.post('/admin/config/messages-bg', messagesBackgroundUrl.value || '', {
      headers: {
        'Content-Type': 'text/plain'
      }
    })
    ElMessage.success('背景设置已保存')
  } catch (error) {
    ElMessage.error('保存失败')
    console.error('保存留言板背景失败:', error)
  } finally {
    bgLoading.value = false
  }
}

const clearMessagesBackground = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清除留言板背景吗？清除后将使用默认渐变背景。',
      '清除背景',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    messagesBackgroundUrl.value = ''
    await request.post('/admin/config/messages-bg', '', {
      headers: {
        'Content-Type': 'text/plain'
      }
    })
    ElMessage.success('背景已清除')
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('清除失败')
      console.error('清除留言板背景失败:', err)
    }
  }
}

const handleUploadSuccess = (response) => {
  if (response && response.code === 200 && response.data) {
    messagesBackgroundUrl.value = response.data
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('文件上传失败')
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isVideo = file.type === 'video/mp4'
  const isLt50M = file.size / 1024 / 1024 < 50

  if (!isImage && !isVideo) {
    ElMessage.error('只能上传图片或MP4视频文件!')
    return false
  }
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过50MB!')
    return false
  }
  return true
}

async function fetchComments() {
  try {
    let res;
    // 根据模式获取不同的评论列表
    if (isArticleCommentsMode.value) {
      // 获取文章评论
      res = await request.get(`/comments/article/${currentArticleId.value}`)
    } else {
      // 获取留言板留言
      res = await request.get('/comments/all')
    }
    
    if (res.data) {
      let data = Array.isArray(res.data) ? res.data : (res.data.records || [])
      
      // 如果是留言板模式，过滤掉文章和照片评论
      if (!isArticleCommentsMode.value) {
        data = data.filter(c => !c.articleId && !c.photoId)
      }
      
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        data = data.filter(c => 
          (c.nickname && c.nickname.toLowerCase().includes(query)) ||
          (c.content && c.content.toLowerCase().includes(query))
        )
      }

      comments.value = data
      total.value = data.length
    }
  } catch (err) {
    ElMessage.error(isArticleCommentsMode.value ? '获取文章评论失败' : '获取留言列表失败')
    console.error('获取评论列表失败:', err)
  }
}

async function handleDeleteComment(comment) {
  try {
    await ElMessageBox.confirm(
      `确定要删除这条留言吗？\n\n留言者：${comment.nickname}\n内容：${comment.content.substring(0, 50)}${comment.content.length > 50 ? '...' : ''}`,
      '删除留言',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await request.delete(`/comments/${comment.id}`)
    ElMessage.success('留言删除成功')
    fetchComments()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('留言删除失败')
      console.error('留言删除失败:', err)
    }
  }
}

function handleSizeChange(size) {
  pageSize.value = size
  fetchComments()
}

function handleCurrentChange(current) {
  currentPage.value = current
  fetchComments()
}

function getAvatarInitial(nickname) {
  if (!nickname) return '?'
  return nickname.charAt(0).toUpperCase()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  // 检查路由参数，判断是否是文章评论管理模式
  const articleId = route.params.articleId
  if (articleId) {
    currentArticleId.value = parseInt(articleId, 10)
  }

  fetchComments()

  // 只有在留言板模式下才加载背景设置
  if (!isArticleCommentsMode.value) {
    loadMessagesBackground()
  }
})
</script>

<style scoped>
/* 移动端适配 */
@media (max-width: 768px) {
  .w-64 {
    width: 100%;
  }

  .lg\\:hidden .line-clamp-3 {
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .lg\\:hidden .el-button--mini {
    padding: 6px 10px;
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .lg\\:hidden .flex.gap-2 {
    gap: 6px;
  }

  .lg\\:hidden .el-button--mini {
    padding: 5px 8px;
    font-size: 10px;
  }
}
</style>
