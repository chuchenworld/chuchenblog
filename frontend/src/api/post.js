import request from '@/utils/request'

export function getPosts(params) {
  return request.get('/posts', { params })
}

export function getPostById(id) {
  return request.get(`/posts/${id}`)
}

export function createPost(data) {
  return request.post('/posts', data)
}

export function updatePost(id, data) {
  return request.put(`/posts/${id}`, data)
}

export function deletePost(id) {
  return request.delete(`/posts/${id}`)
}

export function getUserPosts(userId) {
  return request.get(`/posts/user/${userId}`)
}