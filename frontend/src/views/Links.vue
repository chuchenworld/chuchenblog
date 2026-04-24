<template>
  <MainLayout>
    <div class="links-page">
      <div class="header">
        <h2>友情链接</h2>
      </div>

      <el-card class="links-card">
        <template #header>
          <div class="card-header">
            <h3>推荐网站</h3>
          </div>
        </template>
        <div class="links-grid">
          <a v-for="link in links" :key="link.id" :href="link.url" target="_blank" class="link-item">
            <div class="link-logo">
              <img :src="link.logo" :alt="link.name">
            </div>
            <div class="link-info">
              <h4 class="link-name">{{ link.name }}</h4>
              <p class="link-description">{{ link.description }}</p>
            </div>
          </a>
        </div>
      </el-card>
    </div>
  </MainLayout>
</template>

<script setup>
import MainLayout from '@/components/MainLayout.vue'

const links = [
  {
    id: 1,
    name: 'Vue 官方文档',
    url: 'https://vuejs.org/',
    description: 'Vue.js 官方网站和文档',
    logo: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Vue%20logo%20simple%20icon&image_size=square'
  },
  {
    id: 2,
    name: 'Element Plus',
    url: 'https://element-plus.org/',
    description: 'Element Plus UI 组件库',
    logo: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Element%20Plus%20logo%20simple%20icon&image_size=square'
  },
  {
    id: 3,
    name: 'Spring Boot',
    url: 'https://spring.io/projects/spring-boot',
    description: 'Spring Boot 官方网站',
    logo: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Spring%20Boot%20logo%20simple%20icon&image_size=square'
  },
  {
    id: 4,
    name: 'GitHub',
    url: 'https://github.com/',
    description: '代码托管平台',
    logo: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=GitHub%20logo%20simple%20icon&image_size=square'
  }
]
</script>

<style scoped>
.links-page {
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

.links-card {
  transition: all 0.3s;
}

.links-card:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.card-header h3 {
  margin: 0;
  color: #303133;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.link-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.3s;
  background-color: #f9f9f9;
}

.link-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.link-logo {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.link-logo img {
  width: 40px;
  height: 40px;
  object-fit: contain;
}

.link-info {
  flex: 1;
}

.link-name {
  margin: 0 0 4px;
  color: #303133;
  font-size: 16px;
}

.link-description {
  margin: 0;
  color: #909399;
  font-size: 14px;
  line-height: 1.4;
}
</style>