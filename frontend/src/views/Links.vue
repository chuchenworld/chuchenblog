<template>
  <MainLayout>
    <div class="links-page">
      <!-- 友人博客区域 -->
      <div class="friends-section">
        <div class="section-header">
          <el-icon class="friend-icon"><User /></el-icon>
          <h2>友人帐</h2>
          <span class="friend-count">({{ approvedLinks.length }})</span>
        </div>
        
        <div v-if="approvedLinks.length > 0" class="friends-list">
          <a 
            v-for="link in approvedLinks" 
            :key="link.id" 
            :href="link.siteUrl" 
            target="_blank" 
            class="friend-card"
          >
            <div class="friend-avatar">
              <img :src="getAvatarUrl(link)" :alt="link.siteName" @error="(e) => handleAvatarError(e, link)">
            </div>
            <div class="friend-info">
              <div class="friend-name">{{ link.siteName }}</div>
              <div class="friend-desc">{{ link.siteDescript }}</div>
            </div>
            <div class="friend-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </a>
        </div>
        <div v-else class="empty-friends">
          <el-empty description="暂无友人博客" />
        </div>
      </div>

      <!-- 申请区域 -->
      <div class="apply-section">
        <el-card class="apply-card">
          <template #header>
            <div class="card-header">
              <el-icon><Message /></el-icon>
              <h3>申请友链</h3>
            </div>
          </template>
          
          <!-- 博主信息 -->
          <div class="blogger-info">
            <div class="blogger-avatar">
              <img src="/logo.jpg" alt="初尘">
            </div>
            <div class="blogger-detail">
              <div class="blogger-name">
                <span class="nickname">初尘</span>
                <el-tag type="primary" size="small">博主</el-tag>
              </div>
              <p class="apply-hint">申请链接请按照以下格式编写：</p>
              <div class="format-info">
                <p>站点名称：</p>
                <p>站点描述：</p>
                <p>站点链接：</p>
                <p>头像链接：</p>
              </div>
            </div>
          </div>

          <!-- 申请表单 -->
          <el-form :model="form" :rules="rules" ref="formRef" class="apply-form">
            <el-form-item label="站点名称" prop="siteName">
              <el-input v-model="form.siteName" placeholder="请输入您的站点名称" />
            </el-form-item>
            
            <el-form-item label="站点描述" prop="siteDescript">
              <el-input v-model="form.siteDescript" placeholder="请输入站点描述（不超过50字）" />
              <span class="count-text">{{ form.siteDescript.length }}/50</span>
            </el-form-item>
            
            <el-form-item label="站点链接" prop="siteUrl">
              <el-input v-model="form.siteUrl" placeholder="请输入您的博客地址" />
            </el-form-item>
            
            <el-form-item label="站点头像" prop="avatarUrl">
              <el-input v-model="form.avatarUrl" placeholder="请输入头像链接（可选）" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSubmit" :loading="loading">提交申请</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, ArrowRight, Message } from '@element-plus/icons-vue'
import MainLayout from '@/components/MainLayout.vue'
import request from '@/utils/request'

const form = ref({
  siteName: '',
  siteDescript: '',
  siteUrl: '',
  avatarUrl: ''
})

const rules = {
  siteName: [
    { required: true, message: '请输入站点名称', trigger: 'blur' },
    { max: 20, message: '站点名称不能超过20个字符', trigger: 'blur' }
  ],
  siteDescript: [
    { required: true, message: '请输入站点描述', trigger: 'blur' },
    { max: 50, message: '站点描述不能超过50个字符', trigger: 'blur' }
  ],
  siteUrl: [
    { required: true, message: '请输入站点链接', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
  ]
}

const formRef = ref(null)
const loading = ref(false)
const allLinks = ref([])

const approvedLinks = computed(() => {
  return allLinks.value.filter(link => link.status === 1)
})

// 从URL中提取域名
const extractDomain = (url) => {
  if (!url) return ''
  try {
    const urlObj = new URL(url)
    let domain = urlObj.hostname
    // 移除 www. 前缀
    if (domain.startsWith('www.')) {
      domain = domain.slice(4)
    }
    return domain
  } catch {
    // 如果URL解析失败，尝试简单提取
    const match = url.match(/^(?:https?:\/\/)?(?:www\.)?([^\/:\?#]+)/)
    return match ? match[1] : ''
  }
}

// 获取Favicon URL（尝试多个备选API）
const getFaviconUrl = (url) => {
  const domain = extractDomain(url)
  if (!domain) return ''
  
  // 使用备选的Favicon API服务
  // 方案1：尝试网站自身的favicon.ico
  // 方案2：使用本地代理或自建服务
  
  // 返回网站标准favicon路径
  return `https://${domain}/favicon.ico`
}

// 获取头像URL（优先使用Favicon，失败回退到用户填写的头像或默认头像）
const getAvatarUrl = (link) => {
  const faviconUrl = getFaviconUrl(link.siteUrl)
  // 优先使用站点Favicon
  return faviconUrl || link.avatarUrl || '/logo.jpg'
}

// 处理头像加载失败
const handleAvatarError = (event, link) => {
  const target = event.target
  // 如果网站自身的favicon加载失败，尝试用户填写的头像或默认头像
  const domain = extractDomain(link.siteUrl)
  if (target.src.includes(domain + '/favicon.ico')) {
    console.log(`Favicon加载失败，域名: ${domain}，尝试回退头像`)
    if (link.avatarUrl) {
      target.src = link.avatarUrl
    } else {
      target.src = '/logo.jpg'
    }
  } else if (target.src !== '/logo.jpg') {
    target.src = '/logo.jpg'
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    loading.value = true
    
    const response = await request.post('/friend-links', {
      siteName: form.value.siteName,
      siteDescript: form.value.siteDescript,
      siteUrl: form.value.siteUrl,
      avatarUrl: form.value.avatarUrl || null,
      status: 0
    })
    
    if (response.code === 200) {
      ElMessage.success('申请提交成功！等待审核')
      form.value = {
        siteName: '',
        siteDescript: '',
        siteUrl: '',
        avatarUrl: ''
      }
    } else {
      ElMessage.error(response.message || '提交失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    loading.value = false
  }
}

const loadLinks = async () => {
  try {
    const response = await request.get('/friend-links')
    if (response.data) {
      // 将 status 字段转换为数字类型
      allLinks.value = response.data.map(link => ({
        ...link,
        status: parseInt(link.status) || 0
      }))
    }
  } catch (error) {
    console.error('加载友链失败:', error)
  }
}

onMounted(() => {
  loadLinks()
})
</script>

<style scoped>
.links-page {
  padding: 30px 20px;
  max-width: 800px;
  margin: 0 auto;
}

/* 友人博客区域 */
.friends-section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.friend-icon {
  font-size: 24px;
  color: #667eea;
  margin-right: 10px;
}

.section-header h2 {
  margin: 0;
  font-size: 22px;
  color: #303133;
}

.friend-count {
  color: #909399;
  font-size: 14px;
  margin-left: 5px;
}

.friends-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.friend-card {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.2);
  text-decoration: none;
  transition: all 0.3s ease;
}

.friend-card:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);
  border-color: rgba(102, 126, 234, 0.4);
}

.friend-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  background: #f5f7fa;
  flex-shrink: 0;
}

.friend-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.friend-info {
  flex: 1;
  margin-left: 15px;
}

.friend-name {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
}

.friend-desc {
  color: #909399;
  font-size: 14px;
  margin-top: 4px;
}

.friend-arrow {
  color: #ccc;
  transition: all 0.3s ease;
}

.friend-card:hover .friend-arrow {
  color: #667eea;
  transform: translateX(5px);
}

.empty-friends {
  padding: 40px;
}

/* 申请区域 */
.apply-section {
  margin-top: 30px;
}

.apply-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  margin-left: 8px;
  color: #303133;
}

/* 博主信息 */
.blogger-info {
  display: flex;
  padding: 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 8px;
  margin-bottom: 20px;
}

.blogger-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid rgba(102, 126, 234, 0.3);
}

.blogger-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.blogger-detail {
  flex: 1;
  margin-left: 15px;
}

.blogger-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nickname {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.apply-hint {
  margin: 8px 0;
  color: #606266;
  font-size: 14px;
}

.format-info {
  background: rgba(255, 255, 255, 0.8);
  padding: 12px 15px;
  border-radius: 6px;
}

.format-info p {
  margin: 4px 0;
  color: #909399;
  font-size: 13px;
}

/* 表单 */
.apply-form {
  padding: 0 20px 20px;
}

.count-text {
  display: block;
  text-align: right;
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}
</style>