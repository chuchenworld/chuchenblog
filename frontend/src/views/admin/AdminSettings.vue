<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold text-gray-800">系统设置</h1>

    <div class="bg-white rounded-lg shadow">
      <div class="p-6 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-800">首页标语设置</h2>
      </div>
      <div class="p-6">
        <el-form label-width="120px">
          <el-form-item label="首页标语">
            <el-input
              v-model="homeSlogan"
              type="textarea"
              placeholder="请输入首页显示的标语"
              :rows="4"
              class="font-chinese"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveHomeSlogan" :loading="sloganLoading">保存设置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow">
      <div class="p-6 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-800">首页背景设置</h2>
      </div>
      <div class="p-6">
        <el-form label-width="120px">
          <el-form-item label="当前背景">
            <div v-if="homeBackgroundUrl" class="mb-4">
              <video
                v-if="isVideo(homeBackgroundUrl)"
                :src="homeBackgroundUrl"
                alt="首页背景"
                class="w-64 h-36 object-cover rounded"
                autoplay
                muted
                loop
                playsinline
              />
              <img
                v-else
                :src="homeBackgroundUrl"
                alt="首页背景"
                class="w-64 h-36 object-cover rounded"
              />
            </div>
            <div v-else class="mb-4 text-gray-500">
              暂无背景
            </div>
          </el-form-item>
          <el-form-item label="上传新背景">
            <el-upload
              class="upload-demo"
              action="/api/admin/upload"
              :headers="{ Authorization: `Bearer ${authStore.token}` }"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :limit="1"
              :auto-upload="true"
              accept="image/*,video/mp4"
            >
              <el-button type="primary">点击上传</el-button>
              <template #tip>
                <div class="el-upload__tip">
                  支持上传 JPG/PNG 图片或 MP4 视频作为背景，且不超过 50MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
          <el-form-item label="背景URL">
            <el-input
              v-model="homeBackgroundUrl"
              placeholder="请输入背景URL"
              class="w-full"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveHomeBackground" :loading="bgLoading">保存设置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow">
      <div class="p-6 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-800">基本设置</h2>
      </div>
      <div class="p-6">
        <el-form label-width="120px">
          <el-form-item label="网站标题">
            <el-input v-model="settings.siteTitle" placeholder="请输入网站标题" />
          </el-form-item>
          <el-form-item label="网站描述">
            <el-input
              v-model="settings.siteDescription"
              type="textarea"
              placeholder="请输入网站描述"
              rows="3"
            />
          </el-form-item>
          <el-form-item label="网站关键词">
            <el-input v-model="settings.siteKeywords" placeholder="请输入网站关键词，多个用逗号分隔" />
          </el-form-item>
          <el-form-item label="联系邮箱">
            <el-input v-model="settings.contactEmail" placeholder="请输入联系邮箱" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveSettings">保存设置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSiteConfig, updateSiteConfig } from '@/api/config'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'

const authStore = useAuthStore()
const homeSlogan = ref('')
const homeBackgroundUrl = ref('')
const sloganLoading = ref(false)
const bgLoading = ref(false)

const settings = ref({
  siteTitle: '个人博客',
  siteDescription: '记录生活和技术',
  siteKeywords: '博客,技术,生活',
  contactEmail: 'admin@example.com'
})

const isVideo = (url) => {
  return url && url.toLowerCase().endsWith('.mp4')
}

const loadHomeSlogan = async () => {
  try {
    const response = await getSiteConfig('home_slogan')
    if (response.data) {
      homeSlogan.value = response.data.configValue
    }
  } catch (error) {
    console.error('【AdminSettings】加载首页标语失败:', error)
  }
}

const loadHomeBackground = async () => {
  try {
    const response = await request.get('/admin/config/home-bg')
    if (response && response.code === 200 && response.data) {
      homeBackgroundUrl.value = response.data
    }
  } catch (error) {
    console.error('【AdminSettings】加载首页背景失败:', error)
    ElMessage.error('加载首页背景失败')
  }
}

const saveHomeSlogan = async () => {
  sloganLoading.value = true
  try {
    await updateSiteConfig('home_slogan', homeSlogan.value)
    ElMessage.success('配置已更新')
  } catch (error) {
    ElMessage.error('保存失败')
    console.error('【AdminSettings】保存首页标语失败:', error)
  } finally {
    sloganLoading.value = false
  }
}

const saveHomeBackground = async () => {
  bgLoading.value = true
  try {
    await request.post('/admin/config/home-bg', homeBackgroundUrl.value, {
      headers: {
        'Content-Type': 'text/plain'
      }
    })
    ElMessage.success('配置已更新')
  } catch (error) {
    ElMessage.error('保存失败')
    console.error('【AdminSettings】保存首页背景失败:', error)
  } finally {
    bgLoading.value = false
  }
}

const handleUploadSuccess = (response) => {
  if (response && response.code === 200 && response.data) {
    homeBackgroundUrl.value = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败: ' + (response.message || '未知错误'))
  }
}

const handleUploadError = (error) => {
  console.error('【AdminSettings】上传失败:', error)
  ElMessage.error('上传失败')
}

const beforeUpload = (file) => {
  const allowedTypes = [
    'image/jpeg',
    'image/png',
    'image/webp',
    'video/mp4'
  ]

  const isAllowedType = allowedTypes.includes(file.type)
  if (!isAllowedType) {
    ElMessage.error('只能上传 JPG、PNG、WebP 图片或 MP4 视频')
    return false
  }

  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过 50MB')
    return false
  }

  return true
}

const saveSettings = () => {
  ElMessage.success('设置保存成功')
}

onMounted(() => {
  loadHomeSlogan()
  loadHomeBackground()
})
</script>

<style scoped>
.font-chinese {
  font-family: 'Ma Shan Zheng', 'ZCOOL XiaoWei', 'STKaiti', 'KaiTi', serif;
  font-size: 16px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .el-form {
    label-width: 80px !important;
  }

  .w-64 {
    width: 100%;
    height: auto;
    max-height: 200px;
  }

  .el-upload__tip {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .el-form {
    label-width: 70px !important;
  }

  .space-y-6 > div:first-child h1 {
    font-size: 1.5rem;
  }
}
</style>
