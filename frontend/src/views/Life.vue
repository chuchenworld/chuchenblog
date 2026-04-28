<template>
  <MainLayout>
    <div class="life-page">
      <div class="header">
        <h2>生活相册</h2>
      </div>

      <div v-loading="loading" class="photo-gallery">
        <div v-if="!loading && error" class="empty-state">
          <el-empty description="加载失败" :image-size="200" />
        </div>
        <div v-else-if="!loading && photoStore.photos.length === 0" class="empty-state">
          <el-empty description="暂无照片" />
        </div>
        <div v-else class="photo-grid">
          <div 
            v-for="photo in photoStore.photos" 
            :key="photo.id"
            class="photo-item"
          >
            <el-card shadow="hover" class="photo-card">
              <div class="photo-img-container">
                <img 
                  :src="photo.url || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20landscape%20photo%2C%20natural%20scenery&image_size=square'" 
                  :alt="photo.name"
                  class="photo-img"
                />
              </div>
              <div class="photo-info">
                <h3 class="photo-name">{{ photo.name }}</h3>
                <p class="photo-date">{{ formatDate(photo.createTime) }}</p>
              </div>
            </el-card>
          </div>
        </div>

        <div v-if="photoStore.photos.length > 0" class="pagination">
          <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 30]"
            :total="photoStore.total"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MainLayout from '@/components/MainLayout.vue'
import { usePhotoStore } from '@/stores/photo'

const photoStore = usePhotoStore()
const loading = ref(false)
const error = ref('')
const pageNum = ref(1)
const pageSize = ref(10)

async function fetchPhotos() {
  console.log('【Life.vue】开始获取照片列表，页码:', pageNum.value, '每页数量:', pageSize.value)
  loading.value = true
  error.value = ''
  try {
    await photoStore.fetchPhotos(pageNum.value, pageSize.value)
    console.log('【Life.vue】照片列表获取成功，数量:', photoStore.photos.length)
  } catch (err) {
    console.error('【Life.vue】照片列表获取失败:', err.message)
    error.value = '加载失败，请稍后重试'
  } finally {
    loading.value = false
    console.log('【Life.vue】加载状态设置为:', loading.value)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

function handleSizeChange(size) {
  pageSize.value = size
  fetchPhotos()
}

function handlePageChange(page) {
  pageNum.value = page
  fetchPhotos()
}

onMounted(() => {
  fetchPhotos()
})
</script>

<style scoped>
.life-page {
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

.header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.photo-gallery {
  min-height: 400px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.photo-card {
  transition: all 0.3s;
  overflow: hidden;
  background-color: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.photo-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.15);
  background-color: rgba(255, 255, 255, 0.9);
}

.photo-img-container {
  width: 100%;
  height: 220px;
  overflow: hidden;
  border-radius: 8px 8px 0 0;
}

.photo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.photo-card:hover .photo-img {
  transform: scale(1.08);
}

.photo-info {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.3);
}

.photo-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  text-shadow: 1px 1px 1px rgba(255, 255, 255, 0.8);
}

.photo-date {
  margin: 0;
  font-size: 14px;
  color: #606266;
  text-shadow: 1px 1px 1px rgba(255, 255, 255, 0.8);
}

.empty-state {
  margin: 40px 0;
  text-align: center;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
}
</style>