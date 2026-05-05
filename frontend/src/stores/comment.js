import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useCommentStore = defineStore('comment', () => {
  const comments = ref([])

  async function fetchCommentsByArticleId(articleId) {
    console.log('【CommentStore】开始获取文章评论，文章ID:', articleId)
    try {
      const res = await request.get(`/comments/article/${articleId}`)
      console.log('【CommentStore】评论获取成功，数量:', res.data.length)
      comments.value = res.data
      return res
    } catch (error) {
      console.error('【CommentStore】评论获取失败:', error.message)
      throw error
    }
  }

  async function createComment(articleId, content, nickname, email, isAnonymous, notifyAuthor, parentId = null) {
    console.log('【CommentStore】开始提交评论，文章ID:', articleId, '匿名:', isAnonymous, '通知作者:', notifyAuthor)
    try {
      const data = {
        articleId,
        content,
        nickname,
        email,
        isAnonymous,
        notifyAuthor,
        parentId
      }
      const res = await request.post('/comments', data)
      console.log('【CommentStore】评论提交成功')
      return res
    } catch (error) {
      console.error('【CommentStore】评论提交失败:', error.message)
      throw error
    }
  }

  async function likeComment(commentId) {
    console.log('【CommentStore】开始点赞评论，评论ID:', commentId)
    try {
      const res = await request.post(`/comments/${commentId}/like`)
      console.log('【CommentStore】评论点赞成功')
      return res
    } catch (error) {
      console.error('【CommentStore】评论点赞失败:', error.message)
      throw error
    }
  }

  async function deleteComment(commentId) {
    console.log('【CommentStore】开始删除评论，评论ID:', commentId)
    try {
      const res = await request.delete(`/comments/${commentId}`)
      console.log('【CommentStore】评论删除成功')
      return res
    } catch (error) {
      console.error('【CommentStore】评论删除失败:', error.message)
      throw error
    }
  }

  return {
    comments,
    fetchCommentsByArticleId,
    createComment,
    likeComment,
    deleteComment
  }
})
