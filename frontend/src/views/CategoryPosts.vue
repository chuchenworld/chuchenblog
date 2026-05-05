<template>
  <MainLayout>
    <div class="category-posts-page">
      <div class="header">
        <h2>{{ categoryName }}</h2>
        <p class="description" v-if="categoryDescription">{{ categoryDescription }}</p>
        <p class="count">共有 {{ posts.length }} 篇文章</p>
      </div>

      <div v-loading="loading" class="article-list">
        <template v-if="!loading">
          <el-empty v-if="posts.length === 0" description="该分类下暂无文章" />
          <div
            v-else
            v-for="post in paginatedPosts"
            :key="post.id"
            class="article-card"
            @click="goToDetail(post.id)"
          >
            <div class="card-inner">
              <div class="card-image" v-if="post.coverImage">
                <img :src="post.coverImage" :alt="post.title" />
              </div>
              <div class="card-content">
                <div class="card-header">
                  <h3 class="card-title">{{ post.title }}</h3>
                </div>
                <p class="card-summary">{{ post.summary || '暂无摘要' }}</p>
                <div class="card-meta">
                  <span class="meta-item">
                    <el-icon><Calendar /></el-icon>
                    {{ formatDate(post.createTime) }}
                  </span>
                  <span class="meta-item">
                    <el-icon><View /></el-icon>
                    {{ post.viewCount || 0 }}
                  </span>
                  <span class="meta-item">
                    <el-icon><ChatLineSquare /></el-icon>
                    {{ post.commentCount || 0 }}
                  </span>
                </div>
                <div class="card-tags" v-if="post.tags && post.tags.length > 0">
                  <el-tag
                    v-for="tag in post.tags.slice(0, 3)"
                    :key="tag"
                    size="small"
                    type="info"
                    effect="plain"
                  >{{ tag.trim() }}</el-tag>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <div v-if="posts.length > 0" class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="posts.length"
          layout="total, sizes, prev, pager, next"
          background
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MainLayout from '@/components/MainLayout.vue'
import request from '@/utils/request'
import { Calendar, View, ChatLineSquare } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const posts = ref([])
const categoryName = ref('')
const categoryDescription = ref('')
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

const paginatedPosts = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  const end = start + pageSize.value
  return posts.value.slice(start, end)
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

async function fetchCategoryInfo(categoryId) {
  try {
    const res = await request.get(`/categories/${categoryId}`)
    if (res.data) {
      categoryName.value = res.data.name
      categoryDescription.value = res.data.description
    }
  } catch (err) {
    console.error('获取分类信息失败:', err)
  }
}

async function fetchPostsByCategory(categoryId) {
  loading.value = true
  try {
    console.log('【CategoryPosts】开始获取分类文章，categoryId:', categoryId)
    const res = await request.get(`/categories/${categoryId}/posts`)
    if (res && res.data) {
      posts.value = res.data
    } else {
      posts.value = []
    }
    console.log('【CategoryPosts】获取分类文章成功，数量:', posts.value.length)
  } catch (err) {
    console.error('获取分类文章失败:', err)
    posts.value = []
  } finally {
    loading.value = false
  }
}

function goToDetail(id) {
  router.push(`/post/${id}`)
}

function handleSizeChange(size) {
  pageSize.value = size
  pageNum.value = 1
}

function handlePageChange(page) {
  pageNum.value = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  const categoryId = route.params.categoryId
  console.log('【CategoryPosts】页面加载，路由参数categoryId:', categoryId, '类型:', typeof categoryId)
  if (categoryId) {
    const id = parseInt(categoryId, 10)
    console.log('【CategoryPosts】转换后的categoryId:', id, '类型:', typeof id)
    fetchCategoryInfo(id)
    fetchPostsByCategory(id)
  }
})
</script>

<style scoped>
.category-posts-page {
  background: transparent;
  border-radius: 16px;
  padding: 24px;
  min-height: calc(100vh - 120px);
}

.header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid rgba(64, 158, 255, 0.1);
}

.header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.description {
  color: #6b7280;
  margin: 8px 0 0;
}

.count {
  color: #9ca3af;
  font-size: 14px;
  margin: 8px 0 0;
}

.article-list {
  display: grid;
  gap: 20px;
}

.article-card {
  background: transparent;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  max-width: 800px;
  margin: 0 auto 20px;
}

.article-card:hover {
  transform: translateY(-4px);
}

.card-inner {
  display: flex;
  flex-direction: row;
}

.card-image {
  width: 180px;
  min-height: 140px;
  flex-shrink: 0;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.article-card:hover .card-image img {
  transform: scale(1.05);
}

.card-content {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 8px;
}

.card-header {
  margin-bottom: 8px;
}

.card-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  transition: color 0.3s;
}

.article-card:hover .card-title {
  color: #667eea;
}

.card-summary {
  color: #374151;
  margin: 0 0 12px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  flex: 1;
}

.card-meta {
  display: flex;
  gap: 16px;
  color: #4b5563;
  font-size: 13px;
  margin-bottom: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.card-tags .el-tag {
  border-radius: 8px;
}

.pagination {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .card-inner {
    flex-direction: column;
  }

  .card-image {
    width: 100%;
    height: 160px;
  }
}
</style>
