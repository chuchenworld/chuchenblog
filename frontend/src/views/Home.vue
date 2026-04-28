<template>
  <MainLayout>
    <div class="home-container">
      <div class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title fade-in-up">{{ homeSlogan || '人生如逆旅，我亦是行人' }}</h1>
        </div>
      </div>

      <div class="w-full px-4 sm:px-6 lg:px-12 relative">
        
        <!-- 文章区域独立：在屏幕中央形成一个舒适的阅读列 -->
        <div class="w-full max-w-3xl mx-auto relative z-10 mb-12">
          <div class="header">
            <h2>{{ selectedCategoryId ? categories.find(c => c.id === selectedCategoryId)?.name || '文章分类' : '最新文章' }}</h2>
          </div>
          
          <div v-loading="loading" class="article-list">
            <el-empty v-if="!loading && postStore.posts.length === 0" description="暂无文章" />
            <el-card v-for="post in postStore.posts" :key="post.id" class="article-card" shadow="hover" @click="goToDetail(post.id)" :style="{ backgroundImage: post.coverImage ? `url(${post.coverImage})` : 'none', backgroundSize: 'cover', backgroundPosition: 'center' }" >
              <div class="article-overlay">
                <div class="article-header">
                  <h3 class="article-title">{{ post.title || '无标题' }}</h3>
                  <el-tag v-if="post.status === 1" type="success" size="small">已发布</el-tag>
                  <el-tag v-else type="info" size="small">草稿</el-tag>
                </div>
                <p class="article-summary">{{ post.summary || post.content?.substring(0, 100) || '暂无内容' }}</p>
                <div class="article-footer">
                  <span class="article-info">
                    <el-icon><View /></el-icon> {{ post.viewCount || post.views || 0 }}
                  </span>
                  <span class="article-info">
                    <el-icon><ChatLineSquare /></el-icon> {{ post.commentCount || 0 }}
                  </span>
                  <span class="article-info">
                    <el-icon><Calendar /></el-icon> {{ formatDate(post.createTime || post.createdAt) }}
                  </span>
                </div>
              </div>
            </el-card>
          </div>

          <div v-if="postStore.posts.length > 0" class="pagination">
            <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]" :total="postStore.total" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange" @current-change="handlePageChange" />
          </div>
        </div>

        <!-- 侧边栏绝对定位：悬浮在页面的最右侧，不再占用文章区域的空间 -->
        <div class="hidden lg:block absolute right-0 top-0 w-56 h-screen overflow-y-auto">
          <div class="sticky top-24 space-y-6">
            <el-card class="mb-6 glass-card">
              <div class="text-center">
                <div class="avatar mb-4">
                  <img :src="adminProfile.avatar || defaultAvatar" alt="博主头像" class="w-24 h-24 rounded-full mx-auto" @error="handleImageError" >
                </div>
                <h3 class="font-bold text-lg text-white text-shadow">{{ adminProfile.nickname || '初尘' }}</h3>
                <p class="text-gray-300 mt-2 text-shadow">{{ adminProfile.bio || '热爱技术，分享生活' }}</p>
                <div class="mt-4 flex justify-center space-x-4">
                  <el-button type="primary" size="small" plain>关注</el-button>
                  <el-button size="small" plain>联系我</el-button>
                </div>
              </div>
            </el-card>
            <el-card class="glass-card">
              <template #header>
                <div class="flex justify-between items-center">
                  <span class="font-bold text-white text-shadow">标签云</span>
                </div>
              </template>
              <div class="tag-cloud">
                <el-tag v-for="tag in tagList" :key="tag" size="small" class="mr-2 mb-2 cursor-pointer tag-hover" effect="plain" @click="handleTagClick(tag)" >
                  {{ tag }}
                </el-tag>
                <span v-if="tagList.length === 0" class="no-tags">暂无标签</span>
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
import { useRouter } from 'vue-router'
import { Calendar, ChatLineSquare, View } from '@element-plus/icons-vue'
import MainLayout from '@/components/MainLayout.vue'
import { usePostStore } from '@/stores/post'
import { getSiteConfig } from '@/api/config'
import request from '@/utils/request'

const router = useRouter()
const postStore = usePostStore()
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const homeSlogan = ref('')
const adminProfile = ref({
  nickname: '',
  avatar: '',
  bio: '',
  tags: ''
})
const defaultAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=profile%20picture%20of%20a%20young%20blogger%20with%20glasses%2C%20friendly%20smile%2C%20professional%20looking&image_size=square'

const categories = ref([])
const selectedCategoryId = ref(null)

const tagList = ref([])

async function fetchTags() {
  console.log('【Home.vue】开始获取标签数据')
  try {
    const response = await request.get('/profile')
    if (response.data && response.data.tags) {
      tagList.value = Array.isArray(response.data.tags) ? response.data.tags : response.data.tags.split(',').map(t => t.trim()).filter(t => t)
      console.log('【Home.vue】标签数据获取成功:', tagList.value)
    }
  } catch (error) {
    console.error('【Home.vue】标签数据获取失败:', error.message)
    tagList.value = ['Vue', 'Java', 'Spring Boot', '前端开发', '后端开发', 'DevOps']
  }
}

async function fetchCategories() {
  console.log('【Home.vue】开始获取分类数据')
  try {
    const response = await request.get('/categories')
    if (response.data) {
      categories.value = response.data
      console.log('【Home.vue】分类数据获取成功:', categories.value)
    }
  } catch (error) {
    console.error('【Home.vue】分类数据获取失败:', error.message)
    categories.value = [
      { id: 1, name: '前端开发', articleCount: 0 },
      { id: 2, name: '后端开发', articleCount: 0 },
      { id: 3, name: 'DevOps', articleCount: 0 },
      { id: 4, name: 'Life', articleCount: 0 },
      { id: 5, name: 'Others', articleCount: 0 }
    ]
  }
}

async function fetchHomeSlogan() {
  console.log('【Home.vue】开始获取首页标语')
  try {
    const response = await getSiteConfig('home_slogan')
    if (response.data && response.data.configValue) {
      homeSlogan.value = response.data.configValue
      console.log('【Home.vue】首页标语获取成功:', homeSlogan.value)
    }
  } catch (error) {
    console.error('【Home.vue】首页标语获取失败:', error.message)
  }
}

async function fetchAdminProfile() {
  console.log('【Home.vue】开始获取管理员信息')
  try {
    const response = await request.get('/config/admin/profile')
    if (response.data) {
      adminProfile.value = { ...adminProfile.value, ...response.data }
      console.log('【Home.vue】管理员信息获取成功:', adminProfile.value)
    }
  } catch (error) {
    console.error('【Home.vue】管理员信息获取失败:', error.message)
  }
}

async function fetchPosts() {
  console.log('【Home.vue】开始获取文章列表，页码:', pageNum.value, '每页数量:', pageSize.value)
  loading.value = true
  try {
    if (selectedCategoryId.value) {
      const response = await request.get(`/categories/${selectedCategoryId.value}/posts`)
      if (response.data) {
        postStore.posts = response.data
        postStore.total = response.data.length
      }
    } else {
      await postStore.fetchPosts(pageNum.value, pageSize.value, 1)
    }
    console.log('【Home.vue】文章列表获取完成')
  } catch (error) {
    console.error('【Home.vue】文章列表获取失败:', error.message)
  } finally {
    loading.value = false
    console.log('【Home.vue】加载状态设置为:', loading.value)
  }
}

function handleTagClick(tag) {
  console.log('标签点击:', tag)
}

function goToDetail(id) {
  console.log('【Home.vue】跳转到文章详情页，ID:', id)
  router.push(`/post/${id}`)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

function handleImageError(event) {
  event.target.src = defaultAvatar
}

function handleSizeChange(size) {
  console.log('【Home.vue】改变每页数量:', size)
  pageSize.value = size
  pageNum.value = 1
  fetchPosts()
}

function handlePageChange(page) {
  console.log('【Home.vue】改变页码:', page)
  pageNum.value = page
  fetchPosts()
}

onMounted(() => {
  console.log('【Home.vue】组件挂载，开始初始化数据')
  fetchHomeSlogan()
  fetchAdminProfile()
  fetchCategories()
  fetchTags()
  fetchPosts()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Ma+Shan+Zheng&display=swap');

.home-container {
  min-height: 100vh;
  background-color: transparent;
  padding-top: 100px;
  padding-bottom: 60px;
  min-width: 1200px;
}

.hero-section {
  position: relative;
  min-height: 60vh;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  padding: 60px 20px;
  overflow: hidden;
  text-align: center;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: transparent;
  margin-bottom: 100px;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 0;
}

.hero-background::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.4));
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 800px;
}

.hero-title {
  font-family: 'Ma Shan Zheng', 'ZCOOL XiaoWei', 'STKaiti', 'KaiTi', 'FangSong', STFangsong, serif;
  font-size: 3rem;
  font-weight: 400;
  color: #ffffff;
  line-height: 1.5;
  letter-spacing: 8px;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.8);
  margin: 0 0 20px 0;
  animation: breathe 3s infinite ease-in-out;
}

.hero-subtitle {
  font-family: 'Ma Shan Zheng', 'ZCOOL XiaoWei', 'STKaiti', 'KaiTi', 'FangSong', STFangsong, serif;
  font-size: 1.2rem;
  color: #ffffff;
  text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.8);
  margin: 0;
}

@keyframes fadeInUp {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in-up {
  animation: fadeInUp 1.2s ease-out forwards;
  opacity: 0;
}

@keyframes breathe {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

.breathe {
  animation: breathe 3s infinite ease-in-out;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 40px 15px;
  }

  .hero-title {
    font-size: 2rem;
    letter-spacing: 4px;
  }

  .hero-subtitle {
    font-size: 1rem;
  }
  
  .article-card {
    width: 100%;
    max-width: none;
  }
  
  .glass-card {
    width: 100%;
  }
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
  padding: 0 !important;
  border-radius: 8px;
  overflow: hidden;
  min-height: 200px;
  width: 100%;
  max-width: none;
}

.article-overlay {
  background-color: rgba(0, 0, 0, 0.6);
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.article-card:hover {
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.4) !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3) !important;
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
  line-clamp: 2;
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

.pagination {
  margin-top: 40px;
  display: flex;
  justify-content: flex-end;
  margin-bottom: 40px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
  margin-bottom: 40px;
}

.tag-hover:hover {
  transform: scale(1.05);
  background-color: rgba(102, 126, 234, 0.3) !important;
}

.no-tags {
  color: #9ca3af;
  font-size: 14px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.glass-card {
  background-color: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(10px);
  border-radius: 12px !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1) !important;
  padding: 20px !important;
  margin-bottom: 20px !important;
}

.text-shadow {
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

:deep(.el-card:not(.glass-card)) {
  background-color: transparent !important;
  box-shadow: none !important;
  border: none !important;
}

:deep(.glass-card .el-card__header) {
  background-color: transparent !important;
  border-bottom: none !important;
  padding: 0 0 10px 0 !important;
}

:deep(.glass-card .el-button) {
  border-color: rgba(255, 255, 255, 0.5) !important;
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

:deep(.glass-card .el-button:hover) {
  border-color: rgba(255, 255, 255, 0.8) !important;
  background-color: rgba(255, 255, 255, 0.1) !important;
}

:deep(.glass-card .el-tag) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

:deep(.el-empty__description) {
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

:deep(.el-pagination__item) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border: none !important;
  color: #ffffff !important;
}

:deep(.el-pagination__item:hover) {
  background-color: rgba(255, 255, 255, 0.2) !important;
}

:deep(.el-pagination__item.active) {
  background-color: rgba(255, 255, 255, 0.3) !important;
}

:deep(.el-pagination__prev),
:deep(.el-pagination__next) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border: none !important;
  color: #ffffff !important;
}

:deep(.el-pagination__prev:hover),
:deep(.el-pagination__next:hover) {
  background-color: rgba(255, 255, 255, 0.2) !important;
}

:deep(.el-tag:not(.glass-card .el-tag)) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border: none !important;
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}
</style>
