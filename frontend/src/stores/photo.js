import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const usePhotoStore = defineStore('photo', () => {
  const photos = ref([])
  const currentPhoto = ref(null)
  const total = ref(0)

  async function fetchPhotos(pageNum = 1, pageSize = 10) {
    const res = await request.get('/photos', { params: { pageNum, pageSize } })
    photos.value = res.data.records
    total.value = res.data.total
    return res
  }

  async function fetchPhotoById(id) {
    const res = await request.get(`/photos/${id}`)
    currentPhoto.value = res.data
    return res
  }

  async function createPhoto(photoData) {
    return await request.post('/photos', photoData)
  }

  async function updatePhoto(id, photoData) {
    return await request.put(`/photos/${id}`, photoData)
  }

  async function deletePhoto(id) {
    return await request.delete(`/photos/${id}`)
  }

  return {
    photos,
    currentPhoto,
    total,
    fetchPhotos,
    fetchPhotoById,
    createPhoto,
    updatePhoto,
    deletePhoto
  }
})
