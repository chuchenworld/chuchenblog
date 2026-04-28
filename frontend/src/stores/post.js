import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const usePostStore = defineStore('post', () => {
  const posts = ref([])
  const currentPost = ref(null)
  const total = ref(0)

  async function fetchPosts(pageNum = 1, pageSize = 10, status = null) {
    console.log('【PostStore】开始获取文章列表，页码:', pageNum, '每页数量:', pageSize, '状态筛选:', status)
    try {
      const params = { pageNum, pageSize }
      if (status !== null) {
        params.status = status
      }
      const res = await request.get('/posts', { params })
      console.log('【PostStore】文章列表获取成功，总数:', res.data.total, '当前页数量:', res.data.records.length)
      posts.value = res.data.records
      total.value = res.data.total
      return res
    } catch (error) {
      console.error('【PostStore】文章列表获取失败:', error.message)
      throw error
    }
  }

  async function fetchPostById(id) {
    console.log('【PostStore】开始获取文章详情，文章ID:', id)
    try {
      const res = await request.get(`/posts/${id}`)
      console.log('【PostStore】文章详情获取成功，标题:', res.data.title)
      currentPost.value = res.data
      return res
    } catch (error) {
      console.error('【PostStore】文章详情获取失败:', error.message)
      throw error
    }
  }

  async function createPost(postData) {
    console.log('【PostStore】开始创建文章，标题:', postData.title)
    try {
      const res = await request.post('/posts', postData)
      console.log('【PostStore】文章创建成功')
      return res
    } catch (error) {
      console.error('【PostStore】文章创建失败:', error.message)
      throw error
    }
  }

  async function updatePost(id, postData) {
    console.log('【PostStore】开始更新文章，ID:', id, '标题:', postData.title)
    try {
      const res = await request.put(`/posts/${id}`, postData)
      console.log('【PostStore】文章更新成功')
      return res
    } catch (error) {
      console.error('【PostStore】文章更新失败:', error.message)
      throw error
    }
  }

  async function deletePost(id) {
    console.log('【PostStore】开始删除文章，ID:', id)
    try {
      const res = await request.delete(`/posts/${id}`)
      console.log('【PostStore】文章删除成功')
      return res
    } catch (error) {
      console.error('【PostStore】文章删除失败:', error.message)
      throw error
    }
  }

  function incrementCommentCount(articleId) {
    console.log('【PostStore】增加文章评论数，文章ID:', articleId)
    const post = posts.value.find(p => p.id === parseInt(articleId))
    if (post) {
      post.commentCount = (post.commentCount || 0) + 1
      console.log('【PostStore】评论数更新成功')
    }
  }

  function decrementCommentCount(articleId) {
    console.log('【PostStore】减少文章评论数，文章ID:', articleId)
    const post = posts.value.find(p => p.id === parseInt(articleId))
    if (post && post.commentCount > 0) {
      post.commentCount = post.commentCount - 1
      console.log('【PostStore】评论数更新成功')
    }
  }

  return {
    posts,
    currentPost,
    total,
    fetchPosts,
    fetchPostById,
    createPost,
    updatePost,
    deletePost,
    incrementCommentCount,
    decrementCommentCount
  }
})