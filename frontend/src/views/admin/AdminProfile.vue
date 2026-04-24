<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold text-gray-800">个人设置</h1>

    <div class="bg-white rounded-lg shadow">
      <div class="p-6 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-800">个人资料</h2>
      </div>
      <div class="p-6">
        <div class="flex items-center mb-6">
          <div class="relative">
            <img
              :src="profile.avatar || defaultAvatar"
              alt="头像"
              class="w-24 h-24 rounded-full object-cover border-2 border-gray-200"
              @error="handleImageError"
            />
            <input
              type="file"
              accept="image/*"
              class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
              @change="handleAvatarUpload"
            />
            <div class="absolute bottom-0 right-0 bg-blue-500 text-white rounded-full w-8 h-8 flex items-center justify-center text-sm">
              更换
            </div>
          </div>
          <div class="ml-6">
            <h3 class="text-xl font-bold text-gray-800">{{ profile.nickname || username }}</h3>
            <p class="text-gray-500">{{ role }}</p>
          </div>
        </div>

        <el-form label-width="120px">
          <el-form-item label="登录账号">
            <el-input v-model="profile.username" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="profile.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="profile.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input
              v-model="profile.bio"
              type="textarea"
              rows="3"
              placeholder="请输入个人简介"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveProfile" :loading="loading">保存信息</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow">
      <div class="p-6 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-800">个人标签管理</h2>
        <p class="text-sm text-gray-500 mt-1">这些标签将显示在首页侧边栏的"标签云"中，代表您的技术栈或兴趣</p>
      </div>
      <div class="p-6">
        <div class="mb-4">
          <el-input
            v-model="newTag"
            placeholder="输入标签后点击添加"
            class="mb-2"
            @keyup.enter="addTag"
          >
            <template #append>
              <el-button @click="addTag" :disabled="!newTag.trim()">添加标签</el-button>
            </template>
          </el-input>
        </div>

        <div class="tag-container">
          <el-tag
            v-for="(tag, index) in tags"
            :key="index"
            class="mr-2 mb-2"
            closable
            @close="removeTag(index)"
          >
            {{ tag }}
          </el-tag>
          <span v-if="tags.length === 0" class="text-gray-400 text-sm">暂无标签</span>
        </div>

        <div class="mt-4">
          <el-button type="primary" @click="saveTags" :loading="tagLoading">保存标签</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const username = ref('')
const role = ref('')
const loading = ref(false)
const tagLoading = ref(false)
const defaultAvatar = '/src/assets/login.png'

const profile = ref({
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  bio: '',
  tags: ''
})

const tags = ref([])
const newTag = ref('')

onMounted(() => {
  username.value = localStorage.getItem('username') || '管理员'
  role.value = localStorage.getItem('role') || 'admin'
  fetchProfile()
})

const fetchProfile = async () => {
  try {
    const res = await request.get('/admin/profile')
    profile.value = res.data
    if (res.data.tags) {
      tags.value = res.data.tags.split(',').map(t => t.trim()).filter(t => t)
    }
  } catch (error) {
    console.error('获取个人资料失败:', error)
    ElMessage.error('获取个人资料失败')
  }
}

const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await request.post('/admin/upload', formData)
    profile.value.avatar = res.data
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  }
}

const handleImageError = (event) => {
  event.target.src = defaultAvatar
}

const saveProfile = async () => {
  loading.value = true
  try {
    await request.put('/admin/profile', profile.value)
    ElMessage.success('个人信息保存成功')
  } catch (error) {
    console.error('保存个人信息失败:', error)
    ElMessage.error('保存个人信息失败')
  } finally {
    loading.value = false
  }
}

const addTag = () => {
  const tag = newTag.value.trim()
  if (tag && !tags.value.includes(tag)) {
    tags.value.push(tag)
    newTag.value = ''
  } else if (tags.value.includes(tag)) {
    ElMessage.warning('标签已存在')
  }
}

const removeTag = (index) => {
  tags.value.splice(index, 1)
}

const saveTags = async () => {
  tagLoading.value = true
  try {
    profile.value.tags = tags.value.join(',')
    await request.put('/admin/profile', { tags: profile.value.tags })
    ElMessage.success('标签保存成功')
  } catch (error) {
    console.error('保存标签失败:', error)
    ElMessage.error('保存标签失败')
  } finally {
    tagLoading.value = false
  }
}
</script>

<style scoped>
.tag-container {
  min-height: 40px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>