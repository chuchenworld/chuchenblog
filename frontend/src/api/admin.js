import request from '@/utils/request'

// 登录
export function login(data) {
  return request.post('/auth/login', data)
}

// 获取用户列表
export function getUserList(params) {
  return request.get('/users', { params })
}

// 添加用户
export function addUser(data) {
  return request.post('/users', data)
}

// 更新用户
export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

// 重置密码
export function resetPassword(id) {
  return request.post(`/users/${id}/reset-password`)
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/users/${id}`)
}

// 获取文章列表
export function getPosts(params) {
  return request.get('/posts', { params })
}

// 添加文章
export function addPost(data) {
  return request.post('/posts', data)
}

// 更新文章
export function updatePost(id, data) {
  return request.put(`/posts/${id}`, data)
}

// 删除文章
export function deletePost(id) {
  return request.delete(`/posts/${id}`)
}
