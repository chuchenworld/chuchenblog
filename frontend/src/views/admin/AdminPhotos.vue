<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-800">摄影管理</h1>
      <el-button type="primary">
        <el-icon class="mr-2"><Plus /></el-icon>
        上传照片
      </el-button>
    </div>
    
    <div class="bg-white rounded-lg shadow p-6">
      <div v-loading="loading" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <div v-for="photo in photoStore.photos" :key="photo.id" class="border rounded-lg overflow-hidden">
          <div class="h-48 bg-gray-200 flex items-center justify-center">
            <img v-if="photo.url" :src="photo.url" class="w-full h-full object-cover" />
            <el-icon v-else class="text-gray-400 text-4xl"><Picture /></el-icon>
          </div>
          <div class="p-4">
            <h3 class="font-medium text-gray-800">{{ photo.name }}</h3>
            <p class="text-sm text-gray-500 mt-1">{{ formatDate(photo.createTime) }}</p>
            <div class="mt-4 flex space-x-2">
              <el-button type="primary" size="small">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>
        <div v-if="!loading && photoStore.photos.length === 0" class="col-span-full flex items-center justify-center h-48 bg-gray-50 rounded">
          <el-empty description="暂无照片" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Picture, Edit, Delete } from '@element-plus/icons-vue'
import { usePhotoStore } from '@/stores/photo'

const loading = ref(false)
const photoStore = usePhotoStore()

async function fetchPhotos() {
  loading.value = true
  try {
    await photoStore.fetchPhotos()
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchPhotos()
})
</script>
