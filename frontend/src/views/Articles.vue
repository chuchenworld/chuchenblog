<template>
  <MainLayout>
    <div class="articles-page">
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

      <div class="flex flex-wrap -mx-4">
        <div :class="['px-4', sidebarVisible ? 'w-full md:w-2/3' : 'w-full']">
          <div v-loading="loading" class="article-list">
            <el-empty v-if="!loading && error" description="加载失败" :image-size="200" />
            <el-empty v-else-if="!loading && filteredPosts.length === 0" description="暂无文章" />

            <template v-else>
              <transition-group name="fade" tag="div" class="card-list" v-if="viewMode === 'card'">
                <article-card
                  v-for="post in paginatedPosts"
                  :key="post.id"
                  :post="post"
                  @click="goToDetail(post.id)"
                />
              </transition-group>

              <div class="timeline-container" v-else>
                <timeline-view
                  v-for="(yearPosts, year) in timelineData"
                  :key="year"
                  :year="year"
                  :posts="yearPosts"
                  @post-click="goToDetail"
                />
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

        <div v-if="sidebarVisible" class="w-full md:w-1/3 px-4 mt-6 md:mt-0">
          <div class="sticky top-4">
            <el-card class="archive-card" shadow="hover">
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
                        <el-tag type="primary" size="small" effect="plain">{{ year.total }}</el-tag>
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

            <el-card class="mt-4 category-card" shadow="hover">
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
                  <el-tag type="info" size="small">{{ cat.articleCount || 0 }}</el-tag>
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
import { ref, computed, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { Calendar, Grid, Operation, PriceTag, ArrowRight, RefreshRight } from '@element-plus/icons-vue'
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

const ArticleCard = {
  props: ['post'],
  template: `
    <div class="article-card" @click="$emit('click')">
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
              v-for="tag in post.tags.split(',').slice(0, 3)"
              :key="tag"
              size="small"
              type="info"
              effect="plain"
            >{{ tag.trim() }}</el-tag>
          </div>
        </div>
      </div>
    </div>
  `,
  methods: {
    formatDate(dateStr) {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
    }
  },
  emits: ['click']
}

const TimelineView = {
  props: ['year', 'posts'],
  emits: ['postClick'],
  template: `
    <div class="timeline-year">
      <div class="timeline-header">
        <div class="timeline-year-badge">{{ year }}</div>
        <div class="timeline-year-line"></div>
      </div>
      <div class="timeline-items">
        <div
          v-for="post in posts"
          :key="post.id"
          class="timeline-item"
          @click="$emit('postClick', post.id)"
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
  `,
  methods: {
    getDay(dateStr) {
      if (!dateStr) return ''
      return new Date(dateStr).getDate()
    },
    getMonth(dateStr) {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleDateString('zh-CN', { month: 'short' })
    }
  }
}

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
    posts.value = res.data || []
    total.value = posts.value.length
  } catch (err) {
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

onMounted(() => {
  fetchPosts()
  fetchCategories()
  if (window.innerWidth < 768) {
    sidebarVisible.value = false
  }
})
</script>

<style scoped>
.articles-page {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
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

.view-toggle :deep(.el-radio-button__inner) {
  border-radius: 20px;
}

.card-list {
  display: grid;
  gap: 20px;
}

.article-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.article-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.card-inner {
  display: flex;
  flex-direction: row;
}

.card-image {
  width: 200px;
  min-height: 160px;
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
  color: #1f2937;
  line-height: 1.4;
  flex: 1;
  transition: color 0.3s;
}

.article-card:hover .card-title {
  color: #667eea;
}

.card-summary {
  color: #6b7280;
  margin: 0 0 16px;
  line-height: 1.8;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  flex: 1;
}

.card-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
  color: #9ca3af;
  font-size: 14px;
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
}

.archive-card,
.category-card {
  border-radius: 16px;
  border: none;
  overflow: hidden;
}

.archive-card :deep(.el-card__header),
.category-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 16px 20px;
}

.archive-card :deep(.el-card__header) span,
.category-card :deep(.el-card__header) span {
  color: white;
}

.custom-collapse :deep(.el-collapse-item__header) {
  background: transparent;
  border: none;
  height: 48px;
  line-height: 48px;
  padding: 0 8px;
  font-size: 15px;
}

.custom-collapse :deep(.el-collapse-item__wrap) {
  border: none;
  background: transparent;
}

.custom-collapse :deep(.el-collapse-item__content) {
  padding-bottom: 8px;
}

.year-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 8px 0;
}

.year-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #374151;
}

.year-arrow {
  transition: transform 0.3s;
  font-size: 14px;
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
  padding: 10px 16px;
  margin: 4px 0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: rgba(102, 126, 234, 0.05);
}

.month-item:hover {
  background: rgba(102, 126, 234, 0.15);
  transform: translateX(4px);
}

.month-item.is-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.month-item.is-active .month-count {
  background: rgba(255, 255, 255, 0.3);
  color: white;
}

.month-label {
  font-size: 14px;
}

.month-count {
  background: #e5e7eb;
  color: #6b7280;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.archive-actions {
  padding: 12px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  background: #f9fafb;
}

.category-item:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateX(4px);
}

.category-item.is-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.category-item.is-active .el-tag {
  background: rgba(255, 255, 255, 0.3);
  border-color: transparent;
  color: white;
}

.timeline-container {
  padding: 20px 0;
}

.timeline-year {
  margin-bottom: 40px;
}

.timeline-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.timeline-year-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 8px 24px;
  border-radius: 24px;
  font-size: 20px;
  font-weight: 700;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.timeline-year-line {
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, transparent 100%);
  margin-left: 20px;
}

.timeline-items {
  position: relative;
  padding-left: 30px;
}

.timeline-items::before {
  content: '';
  position: absolute;
  left: 8px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 20px;
  padding: 16px 20px;
  margin-bottom: 16px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.timeline-item:hover {
  transform: translateX(8px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.timeline-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 50px;
}

.date-day {
  font-size: 28px;
  font-weight: 700;
  color: #667eea;
  line-height: 1;
}

.date-month {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 4px;
}

.timeline-dot {
  position: absolute;
  left: -26px;
  top: 24px;
  width: 14px;
  height: 14px;
  background: white;
  border: 3px solid #667eea;
  border-radius: 50%;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.2);
}

.timeline-content {
  flex: 1;
}

.timeline-title {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.timeline-summary {
  color: #6b7280;
  font-size: 14px;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.timeline-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.meta-views {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #9ca3af;
  font-size: 13px;
}

.pagination {
  margin-top: 32px;
  display: flex;
  justify-content: center;
  padding: 20px;
}

.pagination :deep(.el-pagination) {
  padding: 0;
}

.pagination :deep(.el-pagination button) {
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.pagination :deep(.el-pagination .el-pager li) {
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin: 0 4px;
}

.pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.pagination :deep(.el-pagination__total) {
  background: rgba(255, 255, 255, 0.7);
  padding: 6px 12px;
  border-radius: 8px;
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

@media (max-width: 768px) {
  .articles-page {
    padding: 16px;
    border-radius: 0;
  }

  .card-inner {
    flex-direction: column;
  }

  .card-image {
    width: 100%;
    height: 160px;
  }

  .card-meta {
    flex-wrap: wrap;
    gap: 12px;
  }

  .header h2 {
    font-size: 22px;
  }

  .timeline-item {
    flex-direction: column;
    gap: 12px;
  }

  .timeline-date {
    flex-direction: row;
    gap: 8px;
  }

  .date-day {
    font-size: 20px;
  }
}
</style>