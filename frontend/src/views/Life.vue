<template>
  <MainLayout>
    <div class="life-page">
      <!-- 顶部操作栏 -->
      <div class="header">
        <div class="flex items-center justify-end">
          <div class="flex items-center gap-3">
            <!-- 视图切换按钮 -->
            <div class="view-toggle">
              <button 
                :class="['toggle-btn', { active: currentView === 'album' }]"
                @click="switchView('album')"
              >
                <el-icon class="mr-2"><Folder /></el-icon>
                合集模式
              </button>
              <button 
                :class="['toggle-btn', { active: currentView === 'timeline' }]"
                @click="switchView('timeline')"
              >
                <el-icon class="mr-2"><Clock /></el-icon>
                时间轴模式
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-loading="loading" class="content-area">
        <!-- 错误状态 -->
        <div v-if="!loading && error" class="empty-state">
          <el-empty description="加载失败" :image-size="200" />
          <p class="error-text">{{ error }}</p>
        </div>

        <!-- 合集模式 -->
        <div v-else-if="currentView === 'album' && !loading">
          <div v-if="albumStore.albums.length === 0" class="empty-state">
            <el-empty description="暂无相册" />
            <p class="empty-hint">点击右上角上传照片，或创建新相册</p>
          </div>
          <div v-else class="album-grid">
            <div 
              v-for="album in albumStore.albums" 
              :key="album.id"
              class="album-card"
              @click="openAlbumDetail(album)"
            >
              <div class="album-cover">
                <img 
                  v-if="album.coverUrl" 
                  :src="getImageUrl(album.coverUrl, 'thumbnail')" 
                  :alt="album.name"
                  class="cover-img"
                />
                <div v-else class="cover-placeholder">
                  <el-icon class="text-6xl text-gray-400"><Folder /></el-icon>
                </div>
                <div class="album-photo-count">
                  <el-icon class="mr-1"><Picture /></el-icon>
                  {{ album.photoCount || 0 }}
                </div>
              </div>
              <div class="album-info">
                <h3 class="album-name">{{ album.name }}</h3>
                <p class="album-date">{{ formatDate(album.createTime) }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 时间轴模式 -->
        <div v-else-if="currentView === 'timeline' && !loading">
          <div v-if="groupedPhotos.length === 0" class="empty-state">
            <el-empty description="暂无照片" />
            <p class="empty-hint">点击右上角上传照片</p>
          </div>
          <div v-else class="timeline-container">
            <div 
              v-for="(group, dateKey) in groupedPhotos" 
              :key="dateKey"
              class="timeline-group"
            >
              <!-- 日期分割线 -->
              <div class="timeline-date-divider">
                <div class="date-line"></div>
                <div class="date-badge">
                  <el-icon class="mr-2"><Calendar /></el-icon>
                  {{ formatDateLabel(dateKey) }}
                </div>
                <div class="date-line"></div>
              </div>
              
              <!-- 照片网格 -->
              <div class="timeline-photo-grid">
                <div 
                  v-for="photo in group" 
                  :key="photo.id"
                  class="timeline-photo-item"
                  @click="openPhotoPreview(photo)"
                >
                  <div class="photo-wrapper">
                    <img 
                      :src="getImageUrl(photo.url, 'thumbnail')" 
                      :alt="photo.caption || photo.name"
                      class="timeline-photo"
                    />
                    <div class="photo-overlay">
                      <p v-if="photo.caption" class="photo-caption">{{ photo.caption }}</p>
                    </div>
                  </div>
                  <div class="photo-meta">
                    <span v-if="photo.albumName" class="album-tag">{{ photo.albumName }}</span>
                    <span class="photo-shoot-time">{{ formatTime(photo.shootTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 上传照片模态框 -->
      <el-dialog 
        title="上传照片" 
        :visible.sync="showUploadModal" 
        width="600px"
        :close-on-click-modal="false"
      >
        <div class="space-y-4">
          <!-- 文件上传 -->
          <el-upload
            ref="uploadRef"
            :action="uploadUrl"
            :multiple="true"
            :before-upload="beforeUpload"
            :on-success="onUploadSuccess"
            :on-error="onUploadError"
            :file-list="uploadFiles"
            accept="image/*"
            list-type="picture-card"
            :auto-upload="false"
          >
            <el-icon class="text-4xl text-gray-400"><Plus /></el-icon>
          </el-upload>

          <!-- 元数据填写区域 -->
          <div v-if="uploadFiles.length > 0" class="space-y-4">
            <h4 class="font-medium text-gray-700">填写照片信息</h4>
            <div 
              v-for="(file, index) in uploadFiles" 
              :key="file.uid"
              class="border rounded-lg p-4 bg-gray-50"
            >
              <div class="flex items-start gap-4">
                <img 
                  v-if="file.url" 
                  :src="file.url" 
                  class="w-20 h-20 object-cover rounded"
                />
                <div v-else class="w-20 h-20 bg-gray-200 rounded flex items-center justify-center">
                  <el-icon class="text-gray-400"><Picture /></el-icon>
                </div>
                <div class="flex-1 space-y-3">
                  <div>
                    <label class="block text-sm text-gray-600 mb-1">配文</label>
                    <input 
                      v-model="file.caption" 
                      type="text" 
                      class="w-full px-3 py-2 border rounded-lg text-sm"
                      placeholder="输入照片配文（可选）"
                    />
                  </div>
                  <div>
                    <label class="block text-sm text-gray-600 mb-1">拍摄时间</label>
                    <el-date-picker
                      v-model="file.shootTime"
                      type="datetime"
                      placeholder="选择拍摄时间"
                      class="w-full"
                    />
                  </div>
                  <div>
                    <label class="block text-sm text-gray-600 mb-1">归属合集</label>
                    <el-select 
                      v-model="file.albumId" 
                      placeholder="选择合集（可选）"
                      class="w-full"
                    >
                      <el-option label="不归属任何合集" :value="null" />
                      <el-option 
                        v-for="album in albumStore.albums" 
                        :key="album.id" 
                        :label="album.name" 
                        :value="album.id" 
                      />
                    </el-select>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <template #footer>
          <el-button @click="showUploadModal = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitUpload"
            :disabled="uploadFiles.length === 0"
          >
            确认上传
          </el-button>
        </template>
      </el-dialog>

      <!-- 相册详情弹窗 -->
      <el-dialog 
        :title="selectedAlbum?.name" 
        :visible.sync="showAlbumDetail" 
        width="800px"
        :close-on-click-modal="false"
      >
        <div v-if="selectedAlbum" class="album-detail">
          <div class="detail-header">
            <img 
              v-if="selectedAlbum.coverUrl" 
              :src="selectedAlbum.coverUrl" 
              class="detail-cover"
            />
            <div class="detail-info">
              <h3>{{ selectedAlbum.name }}</h3>
              <p>{{ selectedAlbum.photoCount }} 张照片</p>
              <p>{{ formatDate(selectedAlbum.createTime) }} 创建</p>
            </div>
          </div>
          <div class="detail-photos">
            <div v-if="albumPhotos.length === 0" class="empty-state">
              <el-empty description="该相册暂无照片" />
            </div>
            <div v-else class="detail-photo-grid">
              <div 
                v-for="photo in albumPhotos" 
                :key="photo.id"
                class="detail-photo-item"
                @click="openPhotoPreview(photo)"
              >
                <img 
                  :src="photo.url" 
                  :alt="photo.caption"
                  class="detail-photo"
                />
                <p v-if="photo.caption" class="detail-photo-caption">{{ photo.caption }}</p>
              </div>
            </div>
          </div>
        </div>
      </el-dialog>

      <!-- 照片预览弹窗 -->
      <el-dialog 
        title="" 
        :visible.sync="showPhotoPreview" 
        width="900px"
        :close-on-click-modal="true"
      >
        <div v-if="previewPhoto" class="photo-preview">
          <img 
            :src="previewPhoto.url" 
            :alt="previewPhoto.caption"
            class="preview-img"
          />
          <div class="preview-info">
            <h4>{{ previewPhoto.caption || previewPhoto.name }}</h4>
            <p v-if="previewPhoto.shootTime">拍摄时间：{{ formatDate(previewPhoto.shootTime) }}</p>
            <p v-if="previewPhoto.albumName">所属合集：{{ previewPhoto.albumName }}</p>
          </div>
        </div>
      </el-dialog>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Plus, Folder, Clock, Calendar, Picture 
} from '@element-plus/icons-vue'
import MainLayout from '@/components/MainLayout.vue'
import { usePhotoStore } from '@/stores/photo'
import { useAlbumStore } from '@/stores/album'
import { getImageUrl } from '@/utils/image'

const photoStore = usePhotoStore()
const albumStore = useAlbumStore()
const router = useRouter()

// 视图状态
const currentView = ref('timeline') // 'album' | 'timeline'
const loading = ref(false)
const error = ref('')

// 模态框状态
const showUploadModal = ref(false)
const showAlbumDetail = ref(false)
const showPhotoPreview = ref(false)

// 当前选中的相册和照片
const selectedAlbum = ref(null)
const previewPhoto = ref(null)

// 上传相关
const uploadRef = ref(null)
const uploadFiles = ref([])
const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/photos/upload'

// 计算属性：按日期分组的照片
const groupedPhotos = computed(() => {
  const groups = {}
  photoStore.photos.forEach(photo => {
    const dateStr = photo.shootTime || photo.createTime
    if (dateStr) {
      const date = new Date(dateStr)
      const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
      if (!groups[key]) {
        groups[key] = []
      }
      groups[key].push(photo)
    }
  })
  // 按日期降序排列
  const sortedKeys = Object.keys(groups).sort((a, b) => new Date(b) - new Date(a))
  const result = {}
  sortedKeys.forEach(key => {
    result[key] = groups[key]
  })
  return result
})

// 当前相册的照片
const albumPhotos = computed(() => {
  if (!selectedAlbum.value) return []
  return photoStore.photos.filter(p => p.albumId === selectedAlbum.value.id)
})

// 生命周期
onMounted(() => {
  fetchPhotos()
  fetchAlbums()
})

// 获取照片列表
async function fetchPhotos() {
  loading.value = true
  error.value = ''
  try {
    await photoStore.fetchPhotos(1, 100) // 获取较多照片用于时间轴展示
  } catch (err) {
    console.error('【Life.vue】照片列表获取失败:', err.message)
    error.value = '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 获取相册列表
async function fetchAlbums() {
  try {
    await albumStore.fetchAlbums()
  } catch (err) {
    console.error('【Life.vue】相册列表获取失败:', err.message)
  }
}

// 切换视图
function switchView(view) {
  currentView.value = view
}

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

// 格式化日期标签（用于时间轴）
function formatDateLabel(dateKey) {
  const date = new Date(dateKey)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
}

// 格式化时间
function formatTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 打开相册详情页
function openAlbumDetail(album) {
  router.push(`/album/${album.id}`)
}

// 打开照片详情页
function openPhotoPreview(photo) {
  router.push(`/photo/${photo.id}`)
}

// 文件上传前处理
function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  const isLt20M = file.size / 1024 / 1024 < 20
  if (!isLt20M) {
    ElMessage.error('图片大小不能超过 20MB')
    return false
  }
  return true
}

// 上传成功处理
function onUploadSuccess(response, file, fileList) {
  console.log('上传成功:', response)
}

// 上传失败处理
function onUploadError(error, file, fileList) {
  ElMessage.error('上传失败: ' + error.message)
}

// 提交上传
async function submitUpload() {
  if (!uploadRef.value) return
  
  loading.value = true
  try {
    // 先上传文件
    const uploadPromise = new Promise((resolve) => {
      uploadRef.value.submit()
      setTimeout(resolve, 2000)
    })
    await uploadPromise
    
    // 更新照片信息
    for (const file of uploadFiles.value) {
      if (file.response && file.response.data) {
        const photoId = file.response.data.id
        await photoStore.updatePhoto(photoId, {
          caption: file.caption,
          shootTime: file.shootTime,
          albumId: file.albumId
        })
      }
    }
    
    ElMessage.success('上传成功')
    showUploadModal.value = false
    uploadFiles.value = []
    await fetchPhotos()
    await fetchAlbums()
  } catch (error) {
    ElMessage.error('上传失败: ' + error.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.life-page {
  background-color: transparent;
  padding: 32px;
  min-height: calc(100vh - 100px);
}

.header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.header h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.view-toggle {
  display: flex;
  background-color: rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  padding: 4px;
}

.toggle-btn {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.toggle-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.toggle-btn.active {
  background-color: #2c3e50;
  color: #fff;
}

.content-area {
  min-height: 400px;
}

.empty-state {
  margin: 60px 0;
  text-align: center;
}

.error-text {
  color: #e74c3c;
  margin-top: 16px;
}

.empty-hint {
  color: #999;
  margin-top: 16px;
}

/* 合集模式样式 */
.album-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.album-card {
  background: linear-gradient(145deg, #ffffff, #f5f5f5);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.album-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.album-cover {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.album-card:hover .cover-img {
  transform: scale(1.05);
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
}

.album-photo-count {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  display: flex;
  align-items: center;
}

.album-info {
  padding: 16px;
}

.album-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.album-date {
  margin: 0;
  font-size: 13px;
  color: #95a5a6;
}

/* 时间轴模式样式 */
.timeline-container {
  padding: 20px 0;
}

.timeline-group {
  margin-bottom: 40px;
}

.timeline-date-divider {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.date-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, #ddd, transparent);
}

.date-badge {
  display: flex;
  align-items: center;
  padding: 8px 20px;
  background: linear-gradient(135deg, #2c3e50, #34495e);
  color: #fff;
  border-radius: 30px;
  font-size: 14px;
  font-weight: 500;
  margin: 0 20px;
  box-shadow: 0 4px 15px rgba(44, 62, 80, 0.3);
}

.timeline-photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.timeline-photo-item {
  cursor: pointer;
  transition: all 0.3s ease;
}

.photo-wrapper {
  position: relative;
  width: 100%;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.timeline-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.timeline-photo-item:hover .timeline-photo {
  transform: scale(1.08);
}

.photo-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.timeline-photo-item:hover .photo-overlay {
  opacity: 1;
}

.photo-caption {
  margin: 0;
  color: #fff;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.photo-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 4px;
}

.album-tag {
  background-color: #3498db;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
}

.photo-shoot-time {
  font-size: 12px;
  color: #95a5a6;
}

/* 相册详情弹窗样式 */
.album-detail {
  padding: 10px;
}

.detail-header {
  display: flex;
  gap: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.detail-cover {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.detail-info h3 {
  margin: 0 0 10px 0;
  font-size: 20px;
}

.detail-info p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.detail-photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.detail-photo-item {
  cursor: pointer;
}

.detail-photo {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
  transition: transform 0.3s;
}

.detail-photo-item:hover .detail-photo {
  transform: scale(1.05);
}

.detail-photo-caption {
  margin: 6px 0 0 0;
  font-size: 13px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 照片预览弹窗样式 */
.photo-preview {
  text-align: center;
}

.preview-img {
  max-width: 100%;
  max-height: 500px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.preview-info {
  margin-top: 20px;
}

.preview-info h4 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #2c3e50;
}

.preview-info p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .life-page {
    padding: 16px;
    margin: 10px;
  }
  
  .header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .view-toggle {
    justify-content: center;
  }
  
  .toggle-btn {
    padding: 10px 14px;
    font-size: 13px;
  }
  
  .album-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }
  
  .album-cover {
    height: 150px;
  }
  
  .timeline-photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 10px;
  }
  
  .photo-wrapper {
    height: 140px;
  }
  
  .date-badge {
    padding: 6px 12px;
    font-size: 12px;
    margin: 0 10px;
  }
  
  .detail-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .detail-photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 10px;
  }
  
  .detail-photo {
    height: 100px;
  }
}
</style>
