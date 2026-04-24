import request from '@/utils/request'

export function getSiteConfig(configKey) {
  return request.get(`/config/${configKey}`)
}

export function updateSiteConfig(configKey, configValue) {
  return request.put(`/config/${configKey}`, { configValue })
}
