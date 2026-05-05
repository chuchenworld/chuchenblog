/**
 * 为图片 URL 添加 OSS 缩略图参数
 * 支持阿里云 OSS、腾讯云 COS、七牛云 Kodo 等主流云存储服务
 * @param {string} url - 原始图片 URL
 * @param {number} width - 目标宽度，默认 400px
 * @returns {string} 添加缩略图参数后的 URL
 */
export function addOSSThumbnailParams(url, width = 400) {
  if (!url || typeof url !== 'string') {
    return url
  }

  // 检查是否已经有参数
  const hasQuery = url.includes('?')
  const separator = hasQuery ? '&' : '?'

  // 检测云厂商并添加相应的缩略图参数
  if (url.includes('aliyuncs.com')) {
    // 阿里云 OSS
    // 格式: ?x-oss-process=image/resize,w_400
    return `${url}${separator}x-oss-process=image/resize,w_${width}`
  } else if (url.includes('cos.ap-') || url.includes('cos.myqcloud.com')) {
    // 腾讯云 COS
    // 格式: ?imageMogr2/thumbnail/400x/
    return `${url}${separator}imageMogr2/thumbnail/${width}x/`
  } else if (url.includes('qiniucdn.com') || url.includes('qnssl.com')) {
    // 七牛云 Kodo
    // 格式: ?imageView2/1/w/400
    return `${url}${separator}imageView2/1/w/${width}`
  } else if (url.includes('oss-cn-') || url.includes('aliyun.com')) {
    // 阿里云 OSS 其他域名格式
    return `${url}${separator}x-oss-process=image/resize,w_${width}`
  } else if (url.includes('cloudflare.com')) {
    // Cloudflare Images
    // 格式: ?width=400
    return `${url}${separator}width=${width}`
  } else if (url.includes('s3.amazonaws.com') || url.includes('.amazonaws.com')) {
    // AWS S3 (使用 CloudFront 或 Lambda@Edge 处理)
    // 这里假设使用了 Serverless Image Handler
    return `${url}${separator}width=${width}`
  } else if (url.includes('storage.googleapis.com')) {
    // Google Cloud Storage
    // 格式: ?width=400&crop=scale
    return `${url}${separator}width=${width}&crop=scale`
  } else {
    // 默认处理：尝试检测常见图片格式并添加参数
    // 如果无法识别云厂商，直接返回原 URL（避免破坏非 OSS URL）
    return url
  }
}

/**
 * 获取适合缩略图展示的 URL
 * 对于小图（如头像）使用较小尺寸，对于大图使用标准尺寸
 * @param {string} url - 原始图片 URL
 * @param {string} type - 图片类型: 'thumbnail'(缩略图), 'avatar'(头像), 'preview'(预览图)
 * @returns {string} 处理后的 URL
 */
export function getImageUrl(url, type = 'thumbnail') {
  const sizeMap = {
    avatar: 100,
    thumbnail: 400,
    preview: 800
  }
  const width = sizeMap[type] || 400
  return addOSSThumbnailParams(url, width)
}