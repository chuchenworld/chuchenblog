import request from '@/utils/request'

export function getSiteConfig(configKey) {
  return request.get(`/admin/config/${configKey}`)
}

export function updateSiteConfig(configKey, configValue) {
  return request.post(`/admin/config/${configKey}`, { configValue })
}
