<template>
  <MainLayout>
    <div class="about-page">
      <div class="header">
        <h2>关于我</h2>
      </div>

      <el-card class="about-card">
        <div class="about-content">
          <div class="avatar-section">
            <div class="avatar">
              <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20portrait%20of%20a%20software%20developer&image_size=square" alt="头像">
            </div>
            <h3>初尘</h3>
            <p>全栈开发者</p>
          </div>

          <div class="info-section">
            <h4>个人简介</h4>
            <p>热爱编程，喜欢分享技术知识和生活感悟。专注于前端和后端技术，致力于打造优质的开源项目。</p>

            <h4>技术栈</h4>
            <div class="tech-stack">
              <el-tag type="primary">Vue 3</el-tag>
              <el-tag type="success">React</el-tag>
              <el-tag type="warning">Node.js</el-tag>
              <el-tag type="danger">Spring Boot</el-tag>
              <el-tag type="info">MySQL</el-tag>
              <el-tag type="primary">MongoDB</el-tag>
            </div>

            <h4>联系方式</h4>
            <div class="contact-info">
              <div class="contact-item">
                <el-icon><message /></el-icon>
                <span>邮箱：example@example.com</span>
              </div>
              <div class="contact-item">
                <el-icon><phone /></el-icon>
                <span>电话：138****8888</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </MainLayout>
</template>

<script setup>
import { Message, Phone } from '@element-plus/icons-vue'
import MainLayout from '@/components/MainLayout.vue'
</script>

<style scoped>
.about-page {
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

.about-card {
  transition: all 0.3s;
}

.about-card:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.about-content {
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
}

.avatar-section {
  flex: 0 0 200px;
  text-align: center;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto 16px;
  border: 3px solid #f0f0f0;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-section h3 {
  margin: 0 0 8px;
  color: #303133;
}

.avatar-section p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.info-section {
  flex: 1;
  min-width: 300px;
}

.info-section h4 {
  margin: 20px 0 12px;
  color: #303133;
  font-size: 16px;
}

.info-section p {
  margin: 0 0 16px;
  color: #606266;
  line-height: 1.6;
}

.tech-stack {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #606266;
}

@media (max-width: 768px) {
  .about-content {
    flex-direction: column;
    align-items: center;
  }

  .info-section {
    width: 100%;
  }
}
</style>