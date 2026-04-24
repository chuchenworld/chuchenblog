import request from '@/utils/request'

export function getPhotos(params) {
  return request.get('/photos', { params })
}

export function getPhotoById(id) {
  return request.get(`/photos/${id}`)
}

export function createPhoto(data) {
  return request.post('/photos', data)
}

export function updatePhoto(id, data) {
  return request.put(`/photos/${id}`, data)
}

export function deletePhoto(id) {
  return request.delete(`/photos/${id}`)
}
