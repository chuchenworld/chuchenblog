<template>
  <MainLayout>
    <div class="home-container">
      <div class="flex flex-wrap -mx-4">
        <div class="w-full px-4">
          <div class="header">
            <h2>标签：{{ tagName }}</h2>
            <p class="text-gray-400 mt-2">共有 {{ posts.length }} 篇文章</p>
          </div>

          <div v-loading="loading" class="article-list">
            <el-empty v-if="!loading && posts.length === 0" description="暂无文章" />

            <el-card
              v-for="post in posts"
              :key="post.id"
              class="article-card"
              shadow="hover"
              @click="goToDetail(post.id)"
            >
              <div class="article-header">
                <h3 class="article-title">{{ post.title || '无标题' }}</h3>
                <el-tag v-if="post.status === 1" type="success" size="small">已发布</el-tag>
                <el-tag v-else type="info" size="small">草稿</el-tag>
              </div>
              <p class="article-summary">{{ post.summary || post.content?.substring(0, 100) || '暂无内容' }}</p>
              <div class="article-footer">
                <span class="article-info">
                  <el-icon><view /></el-icon>
                  {{ post.viewCount || post.views || 0 }}
                </span>
                <span class="article-info">
                  <el-icon><calendar /></el-icon>
                  {{ formatDate(post.createTime || post.createdAt) }}
                </span>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { View, Calendar } from '@element-plus/icons-vue'
import MainLayout from '@/components/MainLayout.vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const posts = ref([])
const tagName = ref('')

async function fetchPostsByTag() {
  loading.value = true
  tagName.value = route.params.tagName
  console.log('【TagPosts.vue】开始获取标签文章，标签:', tagName.value)

  try {
    const allPostsRes = await request.get('/posts/all')
    if (allPostsRes.data) {
      const allPosts = allPostsRes.data
      posts.value = allPosts.filter(post => {
        if (post.tags && Array.isArray(post.tags)) {
          return post.tags.some(t => t === tagName.value || t === tagName.value)
        }
        return false
      })
      console.log('【TagPosts.vue】标签文章获取成功:', posts.value.length)
    }
  } catch (error) {
    console.error('【TagPosts.vue】标签文章获取失败:', error.message)
  } finally {
    loading.value = false
  }
}

function goToDetail(id) {
  console.log('【TagPosts.vue】跳转到文章详情页，ID:', id)
  router.push(`/post/${id}`)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchPostsByTag()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Ma+Shan+Zheng&display=swap');

.home-container {
  min-height: 100vh;
  background-color: transparent;
  padding-top: 100px;
}

.header {
  margin-bottom: 40px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

.header h2 {
  margin: 0;
  color: #ffffff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.article-list {
  display: grid;
  gap: 30px;
  margin-bottom: 40px;
}

.article-card {
  cursor: pointer;
  transition: all 0.3s;
  background-color: transparent !important;
  box-shadow: none !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  padding: 20px !important;
  border-radius: 8px;
  background-color: rgba(255, 255, 255, 0.05) !important;
}

.article-card:hover {
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.4) !important;
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.article-title {
  margin: 0;
  color: #ffffff;
  font-size: 18px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.article-summary {
  color: #f0f0f0;
  margin: 15px 0;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.article-footer {
  display: flex;
  gap: 20px;
  margin-top: 15px;
  color: #e0e0e0;
  font-size: 14px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.article-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

:deep(.el-card:not(.article-card)) {
  background-color: transparent !important;
  box-shadow: none !important;
  border: none !important;
}

:deep(.article-card .el-tag) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: #ffffff !important;
}

:deep(.el-empty__description) {
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}
</style>
