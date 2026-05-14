<template>
  <MainLayout>
    <div class="articles-page w-full px-4 sm:px-6 lg:px-8 py-8 min-h-screen flex flex-col overflow-x-hidden">
      <div class="header">
        <div class="flex items-center justify-between flex-wrap gap-4">
          <h2>文章归档</h2>
          <div class="view-toggle">
            <el-radio-group v-model="viewMode" size="default">
              <el-radio-button value="card">
                <el-icon><Grid /></el-icon> 卡片视图
              </el-radio-button>
              <el-radio-button value="timeline">
                <el-icon><Operation /></el-icon> 时间轴
              </el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </div>

      <div class="flex flex-col lg:flex-row items-start gap-6 lg:gap-8 flex-1">
        <div class="flex-1 w-full">
          <div v-loading="loading" class="article-list">
            <el-empty v-if="!loading && error" description="加载失败" :image-size="200" />
            <el-empty v-else-if="!loading && filteredPosts.length === 0" description="暂无文章" />

            <template v-else>
              <transition-group name="fade" tag="div" class="card-list" v-if="viewMode === 'card'">
                <div
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
                        <el-tag v-if="post.categoryName" type="primary" size="small" effect="plain">
                          {{ post.categoryName }}
                        </el-tag>
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
                      <div class="card-tags" v-if="post.tags">
                        <el-tag
                          v-for="tag in (Array.isArray(post.tags) ? post.tags : String(post.tags || '').split(',')).slice(0, 3)"
                          :key="Array.isArray(post.tags) ? tag : tag.trim()"
                          size="small"
                          type="info"
                          effect="plain"
                        >{{ Array.isArray(post.tags) ? tag : tag.trim() }}</el-tag>
                      </div>
                    </div>
                  </div>
                </div>
              </transition-group>

              <div class="timeline-container" v-else>
                <div v-for="(yearPosts, year) in timelineData" :key="year" class="timeline-year">
                  <div class="timeline-header">
                    <div class="timeline-year-badge">{{ year }}</div>
                    <div class="timeline-year-line"></div>
                  </div>
                  <div class="timeline-items">
                    <div
                      v-for="post in yearPosts"
                      :key="post.id"
                      class="timeline-item"
                      @click="goToDetail(post.id)"
                    >
                      <div class="timeline-date">
                        <span class="date-day">{{ getDay(post.createTime) }}</span>
                        <span class="date-month">{{ getMonth(post.createTime) }}</span>
                      </div>
                      <div class="timeline-dot"></div>
                      <div class="timeline-content">
                        <h4 class="timeline-title">{{ post.title }}</h4>
                        <p class="timeline-summary">{{ post.summary }}</p>
                        <div class="timeline-meta">
                          <el-tag v-if="post.categoryName" size="small">{{ post.categoryName }}</el-tag>
                          <span class="meta-views"><el-icon><View /></el-icon> {{ post.viewCount }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </div>

          <div v-if="filteredPosts.length > 0" class="pagination">
            <el-pagination
              v-model:current-page="pageNum"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              :total="filteredPosts.length"
              layout="total, sizes, prev, pager, next, jumper"
              background
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </div>

        <div class="sidebar w-full lg:flex-[0_0_300px] flex-shrink-0 mt-8 lg:mt-0">
          <div class="sticky top-4">
            <el-card class="archive-card glass-card" shadow="hover">
              <template #header>
                <div class="flex justify-between items-center">
                  <span class="font-bold text-lg">
                    <el-icon class="mr-2"><Calendar /></el-icon>年份归档
                  </span>
                  <el-tag type="info" size="small">{{ total }} 篇</el-tag>
                </div>
              </template>
              <div class="archive-list">
                <el-collapse v-model="activeYears" class="custom-collapse">
                  <el-collapse-item
                    v-for="year in archives"
                    :key="year.year"
                    :name="year.year"
                  >
                    <template #title>
                      <div class="year-header" @click.stop="toggleYear(year.year)">
                        <span class="year-label">
                          <el-icon class="year-arrow" :class="{ 'is-active': activeYears.includes(year.year) }">
                            <ArrowRight />
                          </el-icon>
                          {{ year.year }}年
                        </span>
                        <span class="year-count">{{ year.total }}</span>
                      </div>
                    </template>
                    <ul class="month-list">
                      <li
                        v-for="month in year.months"
                        :key="month.month"
                        class="month-item"
                        :class="{ 'is-active': selectedYear === year.year && selectedMonth === month.month }"
                        @click="selectMonth(year.year, month.month)"
                      >
                        <span class="month-label">{{ month.month }}月</span>
                        <span class="month-count">{{ month.count }}</span>
                      </li>
                    </ul>
                  </el-collapse-item>
                </el-collapse>
              </div>

              <div class="archive-actions" v-if="selectedYear || selectedMonth">
                <el-button text size="small" @click="clearFilter">
                  <el-icon><RefreshRight /></el-icon>
                  清除筛选
                </el-button>
              </div>
            </el-card>

            <el-card class="mt-4 category-card glass-card" shadow="hover">
              <template #header>
                <span class="font-bold text-lg">
                  <el-icon class="mr-2"><PriceTag /></el-icon>分类筛选
                </span>
              </template>
              <div class="category-list">
                <div
                  v-for="cat in categories"
                  :key="cat.id"
                  class="category-item"
                  :class="{ 'is-active': selectedCategory === cat.id }"
                  @click="selectCategory(cat.id)"
                >
                  <span class="category-name">{{ cat.name }}</span>
                  <span class="category-count">{{ cat.articleCount || 0 }}</span>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Calendar, Grid, Operation, PriceTag, ArrowRight, RefreshRight, View, ChatLineSquare } from '@element-plus/icons-vue'
import MainLayout from '@/components/MainLayout.vue'
import request from '@/utils/request'

const router = useRouter()

const posts = ref([])
const categories = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const error = ref('')
const viewMode = ref('card')
const sidebarVisible = ref(true)
const activeYears = ref([])
const selectedYear = ref(null)
const selectedMonth = ref(null)
const selectedCategory = ref(null)



const allPosts = computed(() => posts.value)
const filteredPosts = computed(() => {
  let result = allPosts.value
  if (selectedYear.value && selectedMonth.value) {
    result = result.filter(p => {
      const date = new Date(p.createTime)
      return date.getFullYear() === selectedYear.value && date.getMonth() + 1 === selectedMonth.value
    })
  } else if (selectedYear.value) {
    result = result.filter(p => new Date(p.createTime).getFullYear() === selectedYear.value)
  }
  if (selectedCategory.value) {
    result = result.filter(p => p.categoryId === selectedCategory.value)
  }
  return result
})

const paginatedPosts = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredPosts.value.slice(start, end)
})

const timelineData = computed(() => {
  const data = {}
  for (const post of filteredPosts.value) {
    const year = new Date(post.createTime).getFullYear()
    if (!data[year]) data[year] = []
    data[year].push(post)
  }
  return data
})

const archives = computed(() => {
  const yearMap = {}
  for (const post of posts.value) {
    const date = new Date(post.createTime)
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    if (!yearMap[year]) {
      yearMap[year] = { year, total: 0, months: {} }
    }
    yearMap[year].total++
    yearMap[year].months[month] = (yearMap[year].months[month] || 0) + 1
  }
  return Object.values(yearMap)
    .map(y => ({
      ...y,
      months: Object.entries(y.months)
        .map(([month, count]) => ({ month: parseInt(month), count }))
        .sort((a, b) => b.month - a.month)
    }))
    .sort((a, b) => b.year - a.year)
})

async function fetchPosts() {
  loading.value = true
  error.value = ''
  try {
    const res = await request.get('/posts/all')
    console.log('接口返回数据:', res)
    console.log('res.data:', res.data)
    // 检查数据结构，可能需要根据实际返回结构调整
    posts.value = Array.isArray(res.data) ? res.data : (res.data?.list || res.data?.records || [])
    total.value = posts.value.length
    console.log('处理后的数据:', posts.value)
    console.log('数据长度:', total.value)
  } catch (err) {
    console.error('加载失败:', err)
    error.value = '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function fetchCategories() {
  try {
    const res = await request.get('/categories')
    categories.value = res.data || []
  } catch (err) {
    console.error('分类加载失败:', err)
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

function toggleYear(year) {
  const index = activeYears.value.indexOf(year)
  if (index > -1) {
    activeYears.value.splice(index, 1)
  } else {
    activeYears.value.push(year)
  }
}

function selectMonth(year, month) {
  if (selectedYear.value === year && selectedMonth.value === month) {
    selectedMonth.value = null
  } else {
    selectedYear.value = year
    selectedMonth.value = month
  }
  pageNum.value = 1
}

function selectCategory(catId) {
  selectedCategory.value = selectedCategory.value === catId ? null : catId
  pageNum.value = 1
}

function clearFilter() {
  selectedYear.value = null
  selectedMonth.value = null
  selectedCategory.value = null
  pageNum.value = 1
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

function getDay(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).getDate()
}

function getMonth(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', { month: 'short' })
}

onMounted(() => {
  fetchPosts()
  fetchCategories()
  if (window.innerWidth < 768) {
    sidebarVisible.value = false
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Ma+Shan+Zheng&display=swap');

.articles-page {
  background: transparent;
  min-height: calc(100vh - 120px);
}

.header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.view-toggle :deep(.el-radio-button__inner) {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
  color: #ffffff;
}

.view-toggle :deep(.el-radio-button__orig-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: #ffffff;
}

.card-list {
  display: grid;
  gap: 20px;
}

.article-card {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.article-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  border-color: rgba(255, 255, 255, 0.4);
}

.card-inner {
  display: flex;
  flex-direction: row;
  box-sizing: border-box;
}

.card-image {
  width: 200px;
  min-height: 160px;
  flex-shrink: 0;
  overflow: hidden;
  box-sizing: border-box;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.article-card:hover .card-image img {
  transform: scale(1.1);
}

.card-content {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.card-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  line-height: 1.4;
  flex: 1;
  transition: color 0.3s;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.article-card:hover .card-title {
  color: #667eea;
}

.card-summary {
  color: #e0e0e0;
  margin: 0 0 16px;
  line-height: 1.8;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  flex: 1;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.card-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
  color: #b0b0b0;
  font-size: 14px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-item .el-icon {
  font-size: 16px;
}

.card-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.card-tags .el-tag {
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.glass-card {
  background-color: transparent !important;
  backdrop-filter: none;
  border-radius: 8px !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  box-shadow: none !important;
  padding: 16px !important;
  margin-bottom: 16px !important;
}

.archive-card :deep(.el-card__header),
.category-card :deep(.el-card__header) {
  background: transparent;
  color: #ffffff;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
}

.archive-card :deep(.el-card__header) span,
.category-card :deep(.el-card__header) span {
  color: #ffffff;
  font-weight: 600;
}

.custom-collapse :deep(.el-collapse-item__header) {
  background: transparent;
  border: none;
  height: 44px;
  line-height: 44px;
  padding: 0;
  font-size: 14px;
  color: #ffffff;
}

.custom-collapse :deep(.el-collapse-item__wrap) {
  border: none;
  background: transparent;
}

.custom-collapse :deep(.el-collapse-item__content) {
  padding-bottom: 0;
}

.year-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 6px 0;
}

.year-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  color: #ffffff;
}

.year-arrow {
  transition: transform 0.3s;
  font-size: 12px;
}

.year-arrow.is-active {
  transform: rotate(90deg);
}

.month-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.month-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  margin: 2px 0;
  cursor: pointer;
  transition: all 0.2s;
  color: #a0a0a0;
}

.month-item:hover {
  background: rgba(255, 255, 255, 0.05);
  padding: 10px 8px;
  border-radius: 4px;
}

.month-item.is-active {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.08);
  padding: 10px 8px;
  border-radius: 4px;
}

.year-count {
  color: #888888;
  font-size: 12px;
  font-weight: 400;
}

.month-count {
  color: #888888;
  font-size: 12px;
  font-weight: 400;
}

.archive-actions {
  padding: 12px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
}

.archive-actions :deep(.el-button) {
  color: #a0a0a0 !important;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  cursor: pointer;
  transition: all 0.2s;
  color: #a0a0a0;
}

.category-item:hover {
  background: rgba(255, 255, 255, 0.05);
  padding: 10px 8px;
  border-radius: 4px;
}

.category-item.is-active {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.08);
  padding: 10px 8px;
  border-radius: 4px;
}

.category-item .category-count {
  color: #888888;
  font-size: 12px;
  font-weight: 400;
}

.timeline-container {
  padding: 16px 0;
}

.timeline-year {
  margin-bottom: 32px;
}

.timeline-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.timeline-year-badge {
  color: #ffffff;
  padding: 6px 20px;
  border-radius: 4px;
  font-size: 18px;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.timeline-year-line {
  flex: 1;
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin-left: 16px;
}

.timeline-items {
  position: relative;
  padding-left: 24px;
}

.timeline-items::before {
  content: '';
  position: absolute;
  left: 3px;
  top: 0;
  bottom: 0;
  width: 1px;
  background: rgba(255, 255, 255, 0.1);
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 16px;
  padding: 14px 16px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(4px);
}

.timeline-item:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
}

.timeline-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 44px;
}

.date-day {
  font-size: 24px;
  font-weight: 600;
  color: #a0a0a0;
  line-height: 1;
}

.date-month {
  font-size: 12px;
  color: #888888;
  margin-top: 4px;
}

.timeline-dot {
  position: absolute;
  left: -22px;
  top: 22px;
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
}

.timeline-content {
  flex: 1;
}

.timeline-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 500;
  color: #ffffff;
}

.timeline-summary {
  color: #a0a0a0;
  font-size: 13px;
  margin: 0 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.timeline-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.timeline-meta .el-tag {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.meta-views {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #b0b0b0;
  font-size: 13px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.pagination {
  margin-top: 32px;
  display: flex;
  justify-content: center;
  padding: 20px;
  width: 100%;
  box-sizing: border-box;
}

.pagination :deep(.el-pagination) {
  padding: 0;
}

.pagination :deep(.el-pagination button) {
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pagination :deep(.el-pagination .el-pager li) {
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin: 0 4px;
}

.pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
}

.pagination :deep(.el-pagination__total) {
  background: rgba(255, 255, 255, 0.1);
  padding: 6px 12px;
  border-radius: 8px;
  color: #ffffff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.4s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

:deep(.el-empty__description) {
  color: #ffffff !important;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

@media (max-width: 768px) {
  .articles-page {
    padding: 12px;
    /* 移除移动端独立背景，使用 App.vue 的全局背景 */
    background: transparent;
    min-height: 100vh;
  }

  .header {
    margin-bottom: 16px;
    padding-bottom: 12px;
  }

  .header h2 {
    font-size: 22px;
  }

  .view-toggle {
    width: 100%;
  }

  .card-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .article-card {
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    border: 1px solid rgba(255, 255, 255, 0.15);
  }

  .card-inner {
    flex-direction: column;
  }

  .card-image {
    width: 100%;
    min-height: 140px;
    border-radius: 16px 16px 0 0;
  }

  .card-content {
    padding: 16px;
    background: rgba(0, 0, 0, 0.7);
    backdrop-filter: blur(10px);
    border-radius: 0 0 16px 16px;
  }

  .card-header {
    margin-bottom: 10px;
  }

  .card-title {
    font-size: 17px;
    font-weight: 600;
  }

  .card-summary {
    font-size: 14px;
    line-height: 1.6;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    margin-bottom: 12px;
    color: rgba(255, 255, 255, 0.85);
  }

  /* 移动端文章元数据缩小 */
  .card-meta {
    gap: 12px;
    font-size: 12px;
    margin-bottom: 10px;
    color: #888;
    text-shadow: none;
    opacity: 0.8;
  }

  .card-meta .el-icon {
    font-size: 12px;
  }

  /* 移动端标签缩小 */
  .card-tags {
    gap: 6px;
  }

  .card-tags .el-tag {
    font-size: 11px !important;
    padding: 2px 8px !important;
    color: #888 !important;
    border-color: rgba(255, 255, 255, 0.2) !important;
  }

  /* 隐藏侧边栏 */
  .sidebar {
    display: none;
  }

  .timeline-container {
    padding: 10px 0;
  }

  .timeline-year {
    margin-bottom: 24px;
  }

  .timeline-header {
    margin-bottom: 16px;
  }

  .timeline-year-badge {
    font-size: 16px;
    padding: 6px 16px;
  }

  .timeline-year-line {
    margin-left: 12px;
  }

  .timeline-items {
    padding-left: 20px;
  }

  .timeline-items::before {
    left: 6px;
  }

  .timeline-item {
    flex-direction: column;
    padding: 14px;
    gap: 12px;
    background: rgba(0, 0, 0, 0.6);
    backdrop-filter: blur(10px);
    border-radius: 12px;
  }

  .timeline-date {
    flex-direction: row;
    min-width: auto;
    gap: 8px;
  }

  .date-day {
    font-size: 20px;
  }

  .date-month {
    margin-top: 0;
    font-size: 11px;
  }

  .timeline-dot {
    left: -18px;
    top: 50%;
    transform: translateY(-50%);
    width: 12px;
    height: 12px;
  }

  .timeline-title {
    font-size: 16px;
    margin-bottom: 6px;
  }

  .timeline-summary {
    font-size: 13px;
    margin-bottom: 10px;
    color: rgba(255, 255, 255, 0.85);
  }

  .timeline-meta {
    gap: 10px;
  }

  .timeline-meta .el-tag {
    font-size: 11px !important;
    padding: 2px 8px !important;
    color: #888 !important;
  }

  .meta-views {
    font-size: 12px;
    color: #888;
  }

  .pagination {
    padding: 10px;
    margin-top: 20px;
  }
}

@media (max-width: 480px) {
  .articles-page {
    padding: 8px;
  }

  .header h2 {
    font-size: 20px;
  }

  .card-content {
    padding: 12px;
  }

  .card-title {
    font-size: 16px;
  }

  .card-summary {
    font-size: 13px;
    line-height: 1.6;
  }

  .card-meta {
    gap: 10px;
    font-size: 11px;
  }

  .timeline-item {
    padding: 12px;
  }

  .timeline-title {
    font-size: 15px;
  }

  .timeline-summary {
    font-size: 12px;
  }
}
</style>
