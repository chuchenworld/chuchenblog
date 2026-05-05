import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface Reply {
  id: number
  content: string
  author: string
  timestamp: string
}

export interface Comment {
  id: number
  content: string
  author: string
  contact: string
  timestamp: string
  likes: number
  isLiked: boolean
  replies: Reply[]
}

export const useCommentStore = defineStore('comments', () => {
  const comments = ref<Comment[]>([
    {
      id: 1,
      content: '这是一条示例评论，欢迎留言！',
      author: '初尘',
      contact: '',
      timestamp: '2024-01-15 10:30',
      likes: 12,
      isLiked: false,
      replies: [
        {
          id: 1,
          content: '感谢您的留言！',
          author: '管理员',
          timestamp: '2024-01-15 11:00'
        }
      ]
    },
    {
      id: 2,
      content: '这个留言系统看起来很棒！',
      author: '匿名用户',
      contact: '',
      timestamp: '2024-01-14 15:45',
      likes: 8,
      isLiked: true,
      replies: []
    },
    {
      id: 3,
      content: '希望能增加更多功能！',
      author: '前端爱好者',
      contact: 'test@example.com',
      timestamp: '2024-01-13 09:20',
      likes: 5,
      isLiked: false,
      replies: [
        {
          id: 2,
          content: '我们会考虑的！',
          author: '管理员',
          timestamp: '2024-01-13 10:00'
        },
        {
          id: 3,
          content: '期待更新！',
          author: '用户A',
          timestamp: '2024-01-13 10:30'
        }
      ]
    }
  ])

  let nextId = 4
  let nextReplyId = 4

  const addComment = (comment: {
    content: string
    author: string
    contact: string
    isAnonymous: boolean
  }) => {
    const now = new Date()
    const timestamp = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
    
    comments.value.unshift({
      id: nextId++,
      content: comment.content,
      author: comment.author,
      contact: comment.contact,
      timestamp,
      likes: 0,
      isLiked: false,
      replies: []
    })
  }

  const toggleLike = (commentId: number) => {
    const comment = comments.value.find(c => c.id === commentId)
    if (comment) {
      comment.isLiked = !comment.isLiked
      comment.likes += comment.isLiked ? 1 : -1
    }
  }

  const addReply = (commentId: number, content: string, author: string) => {
    const comment = comments.value.find(c => c.id === commentId)
    if (comment) {
      const now = new Date()
      const timestamp = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
      
      comment.replies.push({
        id: nextReplyId++,
        content,
        author,
        timestamp
      })
    }
  }

  return {
    comments,
    addComment,
    toggleLike,
    addReply
  }
})