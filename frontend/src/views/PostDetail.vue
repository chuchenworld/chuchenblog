<template>
  <div class="min-h-screen" :style="{
    backgroundImage: postStore.currentPost?.coverImage ? `url(${postStore.currentPost.coverImage})` : 'none',
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    backgroundAttachment: 'fixed'
  }">
    <div class="bg-black bg-opacity-60 min-h-screen">
      <nav class="bg-white bg-opacity-90 shadow-md">
        <div class="container mx-auto px-4 py-4 flex justify-between items-center">
          <h1 class="text-xl font-bold">初尘博客</h1>
          <div>
            <router-link to="/home" class="mr-4 text-gray-700 hover:text-blue-500">首页</router-link>
            <router-link v-if="authStore.isAuthenticated" to="/editor" class="mr-4 text-gray-700 hover:text-blue-500">写文章</router-link>
          </div>
        </div>
      </nav>
      <div class="container mx-auto px-4 py-8">
        <div v-if="postStore.currentPost" class="bg-white bg-opacity-90 p-8 rounded-lg shadow-md">
          <h1 class="text-3xl font-bold mb-4">{{ postStore.currentPost.title }}</h1>
          <div class="flex justify-between text-sm text-gray-500 mb-6">
            <span>作者: {{ postStore.currentPost.authorName || '未知' }}</span>
            <span>阅读: {{ postStore.currentPost.viewCount }}</span>
            <span>{{ postStore.currentPost.createTime }}</span>
          </div>
          <div class="prose max-w-none">
            {{ postStore.currentPost.content }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePostStore } from '@/stores/post'

const route = useRoute()
const authStore = useAuthStore()
const postStore = usePostStore()

onMounted(() => {
  postStore.fetchPostById(route.params.id)
})
</script>
