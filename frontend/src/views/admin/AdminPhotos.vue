<template>
  <div class="space-y-6">
    <!-- 顶部操作栏 -->
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-800">摄影管理</h1>
      <div class="flex space-x-3">
        <el-button type="primary" @click="showAlbumModal = true">
              <el-icon class="mr-2"><Folder /></el-icon>
              管理合集
            </el-button>
        <el-button type="primary" @click="showUploadModal = true">
          <el-icon class="mr-2"><Plus /></el-icon>
          上传照片
        </el-button>
      </div>
    </div>
    
    <!-- 照片网格展示 -->
    <div class="bg-white rounded-lg shadow p-6">
      <div v-loading="loading" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <div 
          v-for="photo in photoStore.photos" 
          :key="photo.id" 
          class="border rounded-lg overflow-hidden hover:shadow-md transition-shadow"
        >
          <div class="h-48 bg-gray-200 flex items-center justify-center relative">
            <img 
              v-if="photo.url" 
              :src="photo.url" 
              class="w-full h-full object-cover" 
            />
            <el-icon v-else class="text-gray-400 text-4xl"><Picture /></el-icon>
            <div 
              v-if="photo.albumName" 
              class="absolute top-2 left-2 px-2 py-1 bg-black/60 text-white text-xs rounded"
            >
              {{ photo.albumName }}
            </div>
          </div>
          <div class="p-4">
            <p class="text-sm text-gray-600 line-clamp-2">{{ truncateText(photo.caption, 50) || '暂无配文' }}</p>
            <div v-if="getTagsArray(photo.tags).length > 0" class="flex flex-wrap gap-1 mt-2">
              <span 
                v-for="tag in getTagsArray(photo.tags)" 
                :key="tag"
                class="text-xs px-2 py-0.5 bg-blue-100 text-blue-600 rounded"
              >
                {{ tag }}
              </span>
            </div>
            <p class="text-xs text-gray-400 mt-2">{{ formatDate(photo.shootTime) || formatDate(photo.createTime) }}</p>
            <div class="mt-4 flex flex-wrap gap-2">
              <el-button type="primary" size="small" @click="editPhoto(photo)">
                <el-icon><Edit /></el-icon>
                <span class="hidden sm:inline">编辑</span>
              </el-button>
              <el-button type="warning" size="small" @click="viewPhotoComments(photo)">
                <el-icon><Message /></el-icon>
                <span class="hidden sm:inline">评论</span>
              </el-button>
              <el-button type="danger" size="small" @click="deletePhoto(photo.id)">
                <el-icon><Delete /></el-icon>
                <span class="hidden sm:inline">删除</span>
              </el-button>
            </div>
          </div>
        </div>
        <div 
          v-if="!loading && photoStore.photos.length === 0" 
          class="col-span-full flex items-center justify-center h-48 bg-gray-50 rounded"
        >
          <el-empty description="暂无照片" />
        </div>
      </div>
    </div>

    <!-- 上传照片模态框 -->
    <el-dialog 
      title="上传照片" 
      v-model="showUploadModal" 
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
          @change="onUploadFilesChange"
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
                v-if="file.url || file.response?.url" 
                :src="file.url || file.response.url" 
                class="w-20 h-20 object-cover rounded"
              />
              <div v-else class="w-20 h-20 bg-gray-200 rounded flex items-center justify-center">
                <el-icon class="text-gray-400"><Picture /></el-icon>
              </div>
              <div class="flex-1 space-y-3">
                <div>
                  <label class="block text-sm text-gray-600 mb-1">照片描述</label>
                  <el-input 
                    type="textarea" 
                    v-model="file.description" 
                    placeholder="请输入照片描述" 
                    class="w-full"
                    :rows="3"
                  />
                </div>
                <div>
                  <label class="block text-sm text-gray-600 mb-1">标签</label>
                  <el-select
                    v-model="file.tags"
                    multiple
                    filterable
                    allow-create
                    default-first-option
                    placeholder="请输入标签，按回车添加"
                    class="w-full"
                  />
                </div>
                <div>
                  <label class="block text-sm text-gray-600 mb-1">分类</label>
                  <el-select 
                    v-model="file.albumId" 
                    placeholder="请选择分类"
                    class="w-full"
                  >
                    <el-option label="不归属任何合集" :value="''" />
                    <el-option 
                      v-for="item in albumList" 
                      :key="item.id" 
                      :label="item.name" 
                      :value="item.id" 
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

    <!-- 编辑照片模态框 -->
    <el-dialog 
      title="编辑照片" 
      v-model="showEditModal" 
      width="500px"
    >
      <div v-if="editingPhoto" class="space-y-4">
        <div class="text-center">
          <img 
            :src="editingPhoto.url" 
            class="w-32 h-32 object-cover rounded-lg mx-auto"
          />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">配文</label>
          <input 
            v-model="editingForm.caption" 
            type="text" 
            class="w-full px-3 py-2 border rounded-lg"
            placeholder="输入照片配文"
          />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">标签</label>
          <el-select
            v-model="editingForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="输入标签，按回车添加"
            class="w-full"
          />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">拍摄时间</label>
          <el-date-picker
            v-model="editingForm.shootTime"
            type="datetime"
            placeholder="选择拍摄时间"
            class="w-full"
          />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">归属合集</label>
          <el-select 
            v-model="editingForm.albumId" 
            placeholder="选择合集"
            class="w-full"
          >
            <el-option label="不归属任何合集" :value="''" />
            <el-option 
              v-for="item in albumList" 
              :key="item.id" 
              :label="item.name" 
              :value="item.id" 
            />
          </el-select>
        </div>
      </div>

      <template #footer>
        <el-button @click="showEditModal = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存修改</el-button>
      </template>
    </el-dialog>

    <!-- 合集管理模态框 -->
    <el-dialog 
      title="管理合集" 
      v-model="showAlbumModal" 
      width="700px"
    >
      <div class="space-y-6">
        <!-- 新建相册表单 -->
        <div v-if="showCreateAlbum" class="border rounded-lg p-4 bg-gray-50 space-y-4">
          <h4 class="font-medium text-gray-700">新建相册</h4>
          <div>
            <label class="block text-sm text-gray-600 mb-1">相册名称</label>
            <input 
              v-model="newAlbum.name" 
              type="text" 
              class="w-full px-3 py-2 border rounded-lg"
              placeholder="输入相册名称"
            />
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">封面图片</label>
            <!-- 封面预览 -->
            <div v-if="newAlbum.coverUrl" class="mb-3">
              <img :src="newAlbum.coverUrl" class="w-24 h-24 object-cover rounded" />
            </div>
            <el-upload
              :action="uploadUrl"
              :before-upload="beforeAlbumCoverUpload"
              :on-success="onAlbumCoverUploadSuccess"
              :on-error="onAlbumCoverUploadError"
              :file-list="albumCoverFiles"
              accept="image/*"
              list-type="picture-card"
              :auto-upload="false"
              @change="onAlbumCoverChange"
            >
              <el-icon class="text-4xl text-gray-400"><Plus /></el-icon>
            </el-upload>
          </div>
          <div class="flex justify-end space-x-2">
            <el-button size="small" @click="cancelCreateAlbum">取消</el-button>
            <el-button type="primary" size="small" @click="createAlbum">创建</el-button>
          </div>
        </div>

        <!-- 相册列表 -->
        <div class="flex justify-between items-center">
          <h4 class="font-medium text-gray-700">相册列表</h4>
          <el-button 
            v-if="!showCreateAlbum"
            type="primary" 
            size="small" 
            @click="showCreateAlbum = true"
          >
            <el-icon><Plus /></el-icon>
            新建相册
          </el-button>
        </div>

        <div v-if="albumStore.albums.length === 0" class="text-center py-8">
          <el-empty description="暂无相册" />
        </div>

        <div v-else class="space-y-3">
          <div 
            v-for="album in albumStore.albums" 
            :key="album.id"
            class="flex items-center gap-4 p-3 border rounded-lg hover:bg-gray-50 transition-colors cursor-pointer"
            @click="handleOpenAlbum(album.id)"
          >
            <img 
              v-if="album.coverUrl" 
              :src="album.coverUrl" 
              class="w-16 h-16 object-cover rounded"
            />
            <div v-else class="w-16 h-16 bg-gray-200 rounded flex items-center justify-center">
              <el-icon class="text-gray-400"><Folder /></el-icon>
            </div>
            <div class="flex-1">
              <h5 class="font-medium text-gray-800">{{ album.name }}</h5>
              <p class="text-xs text-gray-500">{{ album.photoCount || 0 }} 张照片</p>
            </div>
            <div class="flex space-x-2">
              <el-button type="primary" size="small" @click.stop="editAlbum(album)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" @click.stop="deleteAlbum(album.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="showAlbumModal = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑相册模态框 -->
    <el-dialog 
      title="编辑相册" 
      v-model="showEditAlbumModal" 
      width="500px"
    >
      <div v-if="editingAlbum" class="space-y-4">
        <div>
          <label class="block text-sm text-gray-600 mb-1">相册名称</label>
          <input 
            v-model="editingAlbumForm.name" 
            type="text" 
            class="w-full px-3 py-2 border rounded-lg"
            placeholder="输入相册名称"
          />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">封面图片</label>
          <!-- 当前封面预览 -->
          <div v-if="editingAlbumForm.coverUrl" class="mb-3">
            <img :src="editingAlbumForm.coverUrl" class="w-24 h-24 object-cover rounded" />
          </div>
          <el-upload
            :action="uploadUrl"
            :before-upload="beforeAlbumCoverUpload"
            :on-success="onEditAlbumCoverUploadSuccess"
            :on-error="onAlbumCoverUploadError"
            :file-list="editingAlbumCoverFiles"
            accept="image/*"
            list-type="picture-card"
            :auto-upload="false"
            @change="onEditAlbumCoverChange"
          >
            <el-icon class="text-4xl text-gray-400"><Plus /></el-icon>
          </el-upload>
        </div>
      </div>

      <template #footer>
        <el-button @click="cancelEditAlbum">取消</el-button>
        <el-button type="primary" @click="submitEditAlbum">保存修改</el-button>
      </template>
    </el-dialog>

    <!-- 照片评论管理模态框 -->
    <el-dialog 
      title="照片评论管理" 
      v-model="showCommentModal" 
      width="800px"
    >
      <div v-if="currentPhoto" class="space-y-4">
        <!-- 照片信息 -->
        <div class="flex items-center gap-4 p-4 bg-gray-50 rounded-lg">
          <img :src="currentPhoto.url" class="w-20 h-20 object-cover rounded-lg" />
          <div>
            <h4 class="font-medium text-gray-800">{{ currentPhoto.caption || '暂无配文' }}</h4>
            <p class="text-sm text-gray-500">{{ photoComments.length }} 条评论</p>
          </div>
        </div>

        <!-- 评论列表 -->
        <div v-if="photoComments.length > 0" class="space-y-3 max-h-96 overflow-y-auto">
          <div 
            v-for="comment in photoComments" 
            :key="comment.id"
            class="flex items-start gap-4 p-3 border rounded-lg hover:bg-gray-50 transition-colors"
          >
            <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center flex-shrink-0">
              <span class="text-white text-sm font-medium">{{ getAvatarInitial(comment.nickname) }}</span>
            </div>
            <div class="flex-1">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="font-medium text-gray-800">{{ comment.nickname }}</span>
                  <span v-if="comment.isAnonymous" class="text-xs px-2 py-0.5 bg-orange-100 text-orange-600 rounded">匿名</span>
                </div>
                <span class="text-xs text-gray-400">{{ formatDate(comment.createTime) }}</span>
              </div>
              <p class="text-sm text-gray-600 mt-2">{{ comment.content }}</p>
              <div class="flex items-center gap-4 mt-2">
                <span class="text-xs text-gray-400">邮箱: {{ comment.email || '-' }}</span>
                <button 
                  @click="handleDeletePhotoComment(comment)"
                  class="text-xs text-red-500 hover:text-red-700"
                >
                  删除
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-8">
          <el-empty description="暂无评论" />
        </div>
      </div>

      <template #footer>
        <el-button @click="showCommentModal = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Picture, Edit, Delete, Folder, Message 
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { usePhotoStore } from '@/stores/photo'
import { useAlbumStore } from '@/stores/album'
import request from '@/utils/request'

const loading = ref(false)
const photoStore = usePhotoStore()
const albumStore = useAlbumStore()
const router = useRouter()

// 计算属性：处理相册列表，确保数据格式正确
const albumList = computed(() => {
  if (!albumStore.albums || !Array.isArray(albumStore.albums)) {
    return []
  }
  return albumStore.albums
    .filter(item => item && item.id && typeof item.id === 'number')
    .map(item => ({
      id: item.id,
      name: item.name || '未命名相册',
      coverUrl: item.coverUrl || ''
    }))
})

// 模态框状态
const showUploadModal = ref(false)
const showEditModal = ref(false)
const showAlbumModal = ref(false)
const showCreateAlbum = ref(false)
const showEditAlbumModal = ref(false)

// 上传相关
const uploadRef = ref(null)
const uploadFiles = ref([])
const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/photos/upload'

// 编辑相关
const editingPhoto = ref(null)
const editingForm = reactive({
  caption: '',
  tags: [],
  shootTime: null,
  albumId: null
})

// 新建相册相关
const newAlbum = reactive({
  name: '',
  coverUrl: ''
})
const albumCoverFiles = ref([])

// 编辑相册相关
const editingAlbum = ref(null)
const editingAlbumForm = reactive({
  name: '',
  coverUrl: ''
})
const editingAlbumCoverFiles = ref([])

// 用于模板中可能引用的 album 变量
const album = ref(null)

// 评论管理相关
const showCommentModal = ref(false)
const currentPhoto = ref(null)
const photoComments = ref([])

// 生命周期
onMounted(() => {
  fetchPhotos()
  fetchAlbums()
})

// 获取照片列表
async function fetchPhotos() {
  loading.value = true
  try {
    await photoStore.fetchPhotos()
  } finally {
    loading.value = false
  }
}

// 获取相册列表
async function fetchAlbums() {
  try {
    await albumStore.fetchAlbums()
    console.log('相册列表:', albumStore.albums)
  } catch (error) {
    console.error('获取相册列表失败:', error)
  }
}

// 打开相册详情页
function handleOpenAlbum(albumId) {
  if (albumId) {
    router.push(`/photos?albumId=${albumId}`)
  }
}

// 处理标签字段，兼容数组和字符串格式
function getTagsArray(tags) {
  if (!tags) return []
  if (Array.isArray(tags)) {
    return tags.filter(t => t && t.trim())
  } else if (typeof tags === 'string') {
    return tags.split(',').filter(t => t.trim())
  }
  return []
}

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 截取文本
function truncateText(text, maxLength) {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
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

// 文件列表变化处理
function onUploadFilesChange(file, fileList) {
  uploadFiles.value = fileList
    .filter(f => f && f.uid) // 过滤无效的文件对象
    .map(f => ({
      ...f,
      description: f.description || '',
      tags: f.tags || [],
      albumId: f.albumId || ''
    }))
}

// 上传成功处理
function onUploadSuccess(response, file, fileList) {
  console.log('上传成功:', response)
  // 更新文件列表中的URL
  const index = uploadFiles.value.findIndex(f => f.uid === file.uid)
  if (index !== -1 && response.data && response.data.url) {
    uploadFiles.value[index].url = response.data.url
  }
}

// 上传失败处理
function onUploadError(error, file, fileList) {
  ElMessage.error('上传失败: ' + error.message)
}

// 提交上传
async function submitUpload() {
  if (!uploadRef.value || uploadFiles.value.length === 0) return
  
  loading.value = true
  try {
    let successCount = 0
    
    for (const file of uploadFiles.value) {
      // 创建 FormData 用于文件上传
      const formData = new FormData()
      formData.append('file', file.raw)
      
      try {
        // 上传文件到服务器
        // 注意：不要手动设置 Content-Type，让 axios 自动处理
        const uploadRes = await request.post('/photos/upload', formData)
        
        if (uploadRes.data && uploadRes.data.url) {
          // 文件上传成功，创建照片记录
          // 将空字符串转换为 null，确保 albumId 是数字类型
          const albumId = file.albumId === '' || file.albumId === null || file.albumId === undefined 
            ? null 
            : Number(file.albumId)
          // tags 为空时传 null，避免 JSON 格式错误
          const tags = file.tags && file.tags.length > 0 ? file.tags : null
          
          // 调试信息
          console.log('准备提交照片数据:', {
            name: file.name,
            url: uploadRes.data.url,
            caption: file.description || '',
            tags: tags,
            albumId: albumId,
            albumIdType: typeof albumId
          })
          
          await photoStore.createPhoto({
            name: file.name,
            url: uploadRes.data.url,
            caption: file.description || '',
            tags: tags,
            albumId: albumId
          })
          successCount++
        }
      } catch (uploadError) {
        console.error(`上传文件 ${file.name} 失败:`, uploadError)
      }
    }
    
    if (successCount > 0) {
      ElMessage.success(`成功上传 ${successCount} 张照片`)
      showUploadModal.value = false
      uploadFiles.value = []
      await fetchPhotos()
      await fetchAlbums()
    } else {
      ElMessage.error('所有照片上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 编辑照片
function editPhoto(photo) {
  editingPhoto.value = photo
  editingForm.caption = photo.caption || ''
  editingForm.tags = getTagsArray(photo.tags)
  editingForm.shootTime = photo.shootTime ? new Date(photo.shootTime) : null
  editingForm.albumId = photo.albumId || null
  showEditModal.value = true
}

// 提交编辑
async function submitEdit() {
  if (!editingPhoto.value) return
  
  loading.value = true
  try {
    // 将空字符串转换为 null
    const albumId = editingForm.albumId === '' ? null : editingForm.albumId
    // tags 为空时传 null，避免 JSON 格式错误
    const tags = editingForm.tags && editingForm.tags.length > 0 ? editingForm.tags.join(',') : null
    await photoStore.updatePhoto(editingPhoto.value.id, {
      caption: editingForm.caption,
      tags: tags,
      shootTime: editingForm.shootTime,
      albumId: albumId
    })
    ElMessage.success('修改成功')
    showEditModal.value = false
    editingPhoto.value = null
    await fetchPhotos()
  } catch (error) {
    ElMessage.error('修改失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 删除照片
async function deletePhoto(id) {
  ElMessageBox.confirm(
    '确定要删除这张照片吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true
    try {
      await photoStore.deletePhoto(id)
      ElMessage.success('删除成功')
      await fetchPhotos()
    } catch (error) {
      ElMessage.error('删除失败: ' + error.message)
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 相册封面变化处理
function onAlbumCoverChange(file, fileList) {
  albumCoverFiles.value = fileList
}

// 相册封面上传前处理
function beforeAlbumCoverUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('封面图片大小不能超过 5MB')
    return false
  }
  return true
}

// 相册封面上传成功
function onAlbumCoverUploadSuccess(response, file, fileList) {
  if (response.data && response.data.url) {
    newAlbum.coverUrl = response.data.url
  }
}

// 相册封面上传失败
function onAlbumCoverUploadError(error) {
  ElMessage.error('封面上传失败: ' + error.message)
}

// 取消创建相册
function cancelCreateAlbum() {
  showCreateAlbum.value = false
  newAlbum.name = ''
  newAlbum.coverUrl = ''
  albumCoverFiles.value = []
}

// 创建相册
async function createAlbum() {
  if (!newAlbum.name.trim()) {
    ElMessage.warning('请输入相册名称')
    return
  }
  
  loading.value = true
  try {
    // 如果有封面图片，先上传
    if (albumCoverFiles.value.length > 0 && albumCoverFiles.value[0].raw) {
      const formData = new FormData()
      formData.append('file', albumCoverFiles.value[0].raw)
      
      // 注意：不要手动设置 Content-Type，让 axios 自动处理
      const uploadRes = await request.post('/photos/upload', formData)
      
      if (uploadRes.data && uploadRes.data.url) {
        newAlbum.coverUrl = uploadRes.data.url
      }
    }
    
    await albumStore.createAlbum({
      name: newAlbum.name,
      coverUrl: newAlbum.coverUrl || null
    })
    ElMessage.success('相册创建成功')
    cancelCreateAlbum()
  } catch (error) {
    ElMessage.error('创建失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 编辑相册
function editAlbum(album) {
  editingAlbum.value = album
  editingAlbumForm.name = album.name || ''
  editingAlbumForm.coverUrl = album.coverUrl || ''
  editingAlbumCoverFiles.value = []
  showEditAlbumModal.value = true
}

// 编辑相册封面变化处理
function onEditAlbumCoverChange(file, fileList) {
  editingAlbumCoverFiles.value = fileList
}

// 编辑相册封面上传成功
function onEditAlbumCoverUploadSuccess(response) {
  if (response.data && response.data.url) {
    editingAlbumForm.coverUrl = response.data.url
  }
}

// 取消编辑相册
function cancelEditAlbum() {
  showEditAlbumModal.value = false
  editingAlbum.value = null
  editingAlbumForm.name = ''
  editingAlbumForm.coverUrl = ''
  editingAlbumCoverFiles.value = []
}

// 提交编辑相册
async function submitEditAlbum() {
  if (!editingAlbum.value || !editingAlbumForm.name.trim()) {
    ElMessage.warning('请输入相册名称')
    return
  }
  
  loading.value = true
  try {
    // 如果有新封面图片，先上传
    if (editingAlbumCoverFiles.value.length > 0 && editingAlbumCoverFiles.value[0].raw) {
      const formData = new FormData()
      formData.append('file', editingAlbumCoverFiles.value[0].raw)
      
      // 注意：不要手动设置 Content-Type，让 axios 自动处理
      const uploadRes = await request.post('/photos/upload', formData)
      
      if (uploadRes.data && uploadRes.data.url) {
        editingAlbumForm.coverUrl = uploadRes.data.url
      }
    }
    
    await albumStore.updateAlbum(editingAlbum.value.id, {
      name: editingAlbumForm.name,
      coverUrl: editingAlbumForm.coverUrl || null
    })
    ElMessage.success('相册更新成功')
    cancelEditAlbum()
    await fetchAlbums()
  } catch (error) {
    ElMessage.error('更新失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 删除相册
async function deleteAlbum(id) {
  ElMessageBox.confirm(
    '确定要删除这个相册吗？删除后照片将不再关联到此相册，但不会被删除。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true
    try {
      await albumStore.deleteAlbum(id)
      ElMessage.success('删除成功')
      await fetchPhotos() // 刷新照片列表
    } catch (error) {
      ElMessage.error('删除失败: ' + error.message)
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 查看照片评论
async function viewPhotoComments(photo) {
  currentPhoto.value = photo
  showCommentModal.value = true
  await fetchPhotoComments(photo.id)
}

// 获取照片评论列表
async function fetchPhotoComments(photoId) {
  try {
    const res = await request.get(`/comments/photo/${photoId}`)
    if (res.data) {
      photoComments.value = res.data.records || res.data
    }
  } catch (error) {
    ElMessage.error('获取评论列表失败')
    console.error('获取照片评论失败:', error)
  }
}

// 删除照片评论
async function handleDeletePhotoComment(comment) {
  try {
    await ElMessageBox.confirm(
      `确定要删除这条评论吗？\n\n评论者：${comment.nickname}\n内容：${comment.content.substring(0, 50)}${comment.content.length > 50 ? '...' : ''}`,
      '删除评论',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await request.delete(`/comments/${comment.id}`)
    ElMessage.success('评论删除成功')
    await fetchPhotoComments(currentPhoto.value.id)
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('评论删除失败')
      console.error('删除照片评论失败:', err)
    }
  }
}

// 获取头像首字母
function getAvatarInitial(nickname) {
  if (!nickname) return '?'
  return nickname.charAt(0).toUpperCase()
}
</script>

<style scoped>
.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .grid-cols-1.sm\\:grid-cols-2.md\\:grid-cols-3.lg\\:grid-cols-4 {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .mt-4.flex.flex-wrap.gap-2 {
    gap: 6px;
  }

  .el-button--small {
    padding: 6px 10px;
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .grid-cols-1.sm\\:grid-cols-2.md\\:grid-cols-3.lg\\:grid-cols-4 {
    grid-template-columns: 1fr;
  }

  .el-button--small {
    padding: 5px 8px;
    font-size: 10px;
  }
}
</style>
