<template>
  <MainLayout>
    <div 
      class="album-detail-page"
      :style="backgroundStyle"
    >
      <!-- 遮罩层 -->
      <div class="overlay"></div>
      
      <!-- 内容区域 -->
      <div class="content-wrapper">
        <!-- 相册头部信息 -->
        <div class="album-header">
          <div class="back-button" @click="goBack">
            <el-icon class="back-icon"><ArrowLeft /></el-icon>
            <span>返回</span>
          </div>
          <div class="album-info">
            <h1 class="album-title">{{ album?.name || '相册' }}</h1>
            <p class="album-stats">
              <el-icon class="mr-1"><Picture /></el-icon>
              {{ photos.length }} 张照片
              <span class="separator">|</span>
              <el-icon class="mr-1"><Calendar /></el-icon>
              {{ formatDate(album?.createTime) }}
            </p>
          </div>
        </div>

        <!-- 照片网格 -->
        <div v-if="loading" class="loading-state">
          <el-loading :text="'加载中...'" />
        </div>
        
        <div v-else-if="error" class="error-state">
          <el-empty description="加载失败" :image-size="200" />
          <p class="error-text">{{ error }}</p>
        </div>

        <div v-else-if="photos.length === 0" class="empty-state">
          <el-empty description="该相册暂无照片" />
        </div>

        <div v-else class="photo-grid">
          <div 
            v-for="photo in photos" 
            :key="photo.id"
            class="photo-card"
            @click="openPhotoDetail(photo.id)"
          >
            <div class="photo-wrapper">
              <img 
                :src="getImageUrl(photo.url, 'thumbnail')" 
                :alt="photo.caption || photo.name"
                class="photo-img"
              />
              <div class="photo-overlay">
                <p v-if="photo.caption" class="photo-caption">{{ photo.caption }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="total > pageSize" class="pagination">
          <el-pagination 
            v-model:current-page="pageNum" 
            v-model:page-size="pageSize" 
            :total="total" 
            layout="prev, pager, next" 
            @current-change="loadPhotos"
          />
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Picture, Calendar } from '@element-plus/icons-vue'
import MainLayout from '@/components/MainLayout.vue'
import request from '@/utils/request'
import { getImageUrl } from '@/utils/image'

const route = useRoute()
const router = useRouter()

// 状态
const loading = ref(false)
const error = ref('')
const album = ref(null)
const photos = ref([])
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 背景样式
const backgroundStyle = computed(() => {
  if (album.value?.coverUrl) {
    return {
      backgroundImage: `url(${album.value.coverUrl})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundAttachment: 'fixed'
    }
  }
  return {}
})

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 返回上一页
function goBack() {
  router.back()
}

// 打开照片详情
function openPhotoDetail(photoId) {
  router.push(`/photo/${photoId}`)
}

// 加载相册信息
async function loadAlbum() {
  const albumId = route.params.id
  if (!albumId) return
  
  loading.value = true
  error.value = ''
  
  try {
    const res = await request.get(`/albums/${albumId}`)
    if (res.data) {
      album.value = res.data
    }
  } catch (err) {
    console.error('【AlbumDetail】相册信息获取失败:', err.message)
    error.value = '加载失败，请稍后重试'
  }
}

// 加载照片列表
async function loadPhotos() {
  const albumId = route.params.id
  if (!albumId) return
  
  loading.value = true
  error.value = ''
  
  try {
    const res = await request.get(`/photos/album/${albumId}`, {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value
      }
    })
    if (res.data) {
      photos.value = res.data.records || res.data
      total.value = res.data.total || res.data.length
    }
  } catch (err) {
    console.error('【AlbumDetail】照片列表获取失败:', err.message)
    error.value = '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadAlbum()
  loadPhotos()
})
</script>

<style scoped>
.album-detail-page {
  min-height: 100vh;
  position: relative;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    180deg,
    rgba(0, 0, 0, 0.7) 0%,
    rgba(0, 0, 0, 0.5) 30%,
    rgba(0, 0, 0, 0.6) 100%
  );
  z-index: 1;
}

.content-wrapper {
  position: relative;
  z-index: 2;
  padding: 40px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.album-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 40px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.back-button:hover {
  background: rgba(255, 255, 255, 0.2);
}

.back-icon {
  font-size: 18px;
}

.album-info {
  flex: 1;
}

.album-title {
  margin: 0 0 10px 0;
  font-size: 36px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.album-stats {
  margin: 0;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  gap: 15px;
}

.separator {
  color: rgba(255, 255, 255, 0.5);
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

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #fff;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
}

.photo-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.photo-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.4);
}

.photo-wrapper {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 正方形比例 */
}

.photo-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.photo-card:hover .photo-img {
  transform: scale(1.08);
}

.photo-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.photo-card:hover .photo-overlay {
  opacity: 1;
}

.photo-caption {
  margin: 0;
  color: #fff;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination {
  margin-top: 40px;
  text-align: center;
}

.pagination :deep(.el-pagination) {
  display: inline-flex;
}

.pagination :deep(.el-pagination button) {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}

.pagination :deep(.el-pagination .el-pager li) {
  color: #fff;
}

.pagination :deep(.el-pagination .el-pager li.active) {
  background: rgba(255, 255, 255, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-wrapper {
    padding: 20px 16px;
  }
  
  .album-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .album-title {
    font-size: 28px;
  }
  
  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 10px;
  }
  
  .back-button {
    padding: 8px 16px;
    font-size: 14px;
  }
}
</style>