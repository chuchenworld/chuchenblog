import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useAlbumStore = defineStore('album', () => {
  const albums = ref([])
  const currentAlbum = ref(null)

  async function fetchAlbums() {
    const res = await request.get('/albums')
    albums.value = res.data
    return res
  }

  async function fetchAlbumById(id) {
    const res = await request.get(`/albums/${id}`)
    currentAlbum.value = res.data
    return res
  }

  async function createAlbum(albumData) {
    const res = await request.post('/albums', albumData)
    await fetchAlbums()
    return res
  }

  async function updateAlbum(id, albumData) {
    const res = await request.put(`/albums/${id}`, albumData)
    await fetchAlbums()
    return res
  }

  async function deleteAlbum(id) {
    const res = await request.delete(`/albums/${id}`)
    await fetchAlbums()
    return res
  }

  return {
    albums,
    currentAlbum,
    fetchAlbums,
    fetchAlbumById,
    createAlbum,
    updateAlbum,
    deleteAlbum
  }
})
