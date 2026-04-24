<template>
  <MainLayout>
    <div class="profile-page">
      <div class="header">
        <h2>关于我</h2>
      </div>

      <div class="profile-content">
        <div class="profile-info">
          <div class="avatar-container">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20portrait%20of%20a%20young%20developer%2C%20friendly%20smile%2C%20modern%20style&image_size=square" alt="个人头像" class="avatar">
          </div>
          <div class="info-details">
            <h3 class="name">初尘</h3>
            <p class="subtitle">热爱技术，分享生活</p>
            <div class="social-links">
              <el-button type="primary" size="small" plain class="mr-2">
                <el-icon><ChatLineSquare /></el-icon>
                微信
              </el-button>
              <el-button type="primary" size="small" plain class="mr-2">
                <el-icon><Message /></el-icon>
                QQ
              </el-button>
              <el-button type="primary" size="small" plain>
                <el-icon><Link /></el-icon>
                GitHub
              </el-button>
            </div>
          </div>
        </div>

        <el-card class="mt-6">
          <template #header>
            <div class="card-header">
              <h3>个人简介</h3>
            </div>
          </template>
          <div class="bio-content">
            <p>大家好，我是初尘，一名热爱技术的全栈开发工程师。</p>
            <p class="mt-3">我擅长前端开发（Vue、React）和后端开发（Spring Boot、Node.js），喜欢探索新技术，分享学习心得。</p>
            <p class="mt-3">这个博客是我记录学习过程、分享技术经验和生活感悟的地方，希望能与大家一起成长。</p>
            <p class="mt-3">如果您有任何问题或建议，欢迎随时联系我！</p>
          </div>
        </el-card>

        <el-card class="mt-6">
          <template #header>
            <div class="card-header">
              <h3>技术栈</h3>
            </div>
          </template>
          <div class="tech-stack">
            <el-tag v-for="tech in techStack" :key="tech" class="mr-2 mb-2">
              {{ tech }}
            </el-tag>
          </div>
        </el-card>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref } from 'vue'
import { ChatLineSquare, Message, Link } from '@element-plus/icons-vue'
import MainLayout from '@/components/MainLayout.vue'

// 技术栈数据
const techStack = ref([
  'Vue 3', 'React', 'JavaScript', 'TypeScript',
  'Spring Boot', 'Java', 'Node.js', 'Express',
  'MySQL', 'MongoDB', 'Redis', 'Docker',
  'Git', 'GitHub', 'Linux'
])
</script>

<style scoped>
.profile-page {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
}

.header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.header h2 {
  margin: 0;
  color: #303133;
}

.profile-info {
  display: flex;
  align-items: center;
  gap: 30px;
}

.avatar-container {
  flex-shrink: 0;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
}

.info-details {
  flex: 1;
}

.name {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.subtitle {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #606266;
}

.social-links {
  display: flex;
  gap: 10px;
}

.card-header h3 {
  margin: 0;
  color: #303133;
}

.bio-content {
  line-height: 1.8;
  color: #606266;
}

.tech-stack {
  display: flex;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .profile-info {
    flex-direction: column;
    text-align: center;
  }
}
</style>