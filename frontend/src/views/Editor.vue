<template>
  <div class="editor-container">
    <div class="editor-header">
      <h1 class="editor-title">{{ isEdit ? '编辑文章' : '写文章' }}</h1>
      <div class="editor-actions">
        <div class="status-selector">
          <el-radio-group v-model="form.status" size="small">
            <el-radio-button :value="1">发布</el-radio-button>
            <el-radio-button :value="0">存草稿</el-radio-button>
          </el-radio-group>
        </div>
        <button @click="handleSubmit" class="publish-btn">
          {{ form.status === 1 ? (isEdit ? '更新' : '发布') : '存草稿' }}
        </button>
        <router-link to="/admin/posts" class="back-link">
          返回列表
        </router-link>
      </div>
    </div>
    <div class="editor-content">
      <!-- 左侧：富文本编辑器 -->
      <div class="editor-left">
        <input
          v-model="form.title"
          type="text"
          class="title-input"
          placeholder="请输入文章标题"
          required
        />
        <div ref="editorRef" class="vditor-container"></div>
      </div>

      <!-- 右侧：表单区域 -->
      <div class="editor-right">
        <div class="form-panel">
          <div class="form-item">
            <label class="form-label">文章分类</label>
            <el-select
              v-model="form.categoryId"
              placeholder="请选择分类"
              class="w-full"
              clearable
            >
              <el-option
                v-for="cat in categories"
                :key="cat.id"
                :label="cat.name"
                :value="cat.id"
              />
            </el-select>
          </div>

          <div class="form-item">
            <label class="form-label">背景图</label>
            <div class="image-upload-container">
              <input
                type="file"
                accept="image/*"
                class="image-upload-input"
                @change="handleCoverImageUpload"
              />
              <div v-if="form.coverImage" class="image-preview">
                <img :src="form.coverImage" alt="背景图预览" />
                <button type="button" class="remove-image-btn" @click="form.coverImage = ''">
                  移除
                </button>
              </div>
              <div v-else class="image-upload-placeholder">
                点击上传背景图
              </div>
            </div>
          </div>

          <div class="form-item">
            <label class="form-label">文章标签</label>
            <el-select
              v-model="form.tags"
              multiple
              filterable
              allow-create
              default-first-option
              placeholder="请输入标签，按回车添加"
              class="w-full"
            >
              <el-option
                v-for="tag in form.tags"
                :key="tag"
                :label="tag"
                :value="tag"
              />
            </el-select>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { usePostStore } from '@/stores/post'
import { ElMessage } from 'element-plus'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const postStore = usePostStore()
const editorRef = ref(null)
const vditor = ref(null)

const isEdit = ref(false)
const postId = ref(null)
const categories = ref([])

const form = ref({
  title: '',
  summary: '',
  coverImage: '',
  content: '',
  status: 1,
  tags: [],
  categoryId: null
})

async function fetchCategories() {
  try {
    const res = await request.get('/categories')
    categories.value = res.data || []
  } catch (err) {
    console.error('获取分类失败:', err)
  }
}

onMounted(async () => {
  await fetchCategories()

  vditor.value = new Vditor(editorRef.value, {
    mode: 'ir',
    height: 'calc(100vh - 280px)',
    cache: { enable: false },
    toolbarConfig: {
      pin: true,
      icon: 'material'
    },
    toolbar: [
      'emoji',
      'headings',
      'bold',
      'italic',
      'strike',
      'link',
      'list',
      'ordered-list',
      'check',
      'outdent',
      'indent',
      'quote',
      'line',
      'code',
      'inline-code',
      'insert-after',
      'insert-before',
      'upload',
      'table',
      'record',
      'edit-mode',
      'preview',
      'fullscreen',
      'both',
      'export'
    ],
    upload: {
      url: '/api/admin/upload',
      fieldName: 'file',
      max: 50 * 1024 * 1024,
      multiple: false,
      accept: 'image/*',
      token: localStorage.getItem('token') || '',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
      },
      success: (response) => {
        const data = typeof response === 'string' ? JSON.parse(response) : response
        return data.data || data
      }
    },
    after: () => {
      console.log('【Vditor】编辑器初始化完成，绑定粘贴事件')
      bindEditorEvents()
    }
  })

  const id = route.query.id
  if (id) {
    isEdit.value = true
    postId.value = parseInt(id, 10)
    await loadPostData(postId.value)
  }
})

// 绑定编辑器事件
function bindEditorEvents() {
  if (!editorRef.value) {
    console.log('【编辑器事件】编辑器容器不存在')
    return
  }
  
  console.log('【编辑器事件】开始绑定事件到:', editorRef.value)
  
  // 绑定粘贴事件
  editorRef.value.addEventListener('paste', handlePaste)
  console.log('【编辑器事件】粘贴事件绑定成功')
  
  // 绑定拖拽事件
  editorRef.value.addEventListener('dragover', handleDragOver)
  editorRef.value.addEventListener('drop', handleDrop)
  console.log('【编辑器事件】拖拽事件绑定成功')
}

// 粘贴事件处理 - 支持粘贴截图
async function handlePaste(event) {
  console.log('【粘贴事件】检测到粘贴事件:', event)
  
  // 获取剪贴板数据
  const clipboardData = event.clipboardData || window.clipboardData
  console.log('【粘贴事件】剪贴板数据:', clipboardData)
  
  if (!clipboardData) {
    console.log('【粘贴事件】无法获取剪贴板数据')
    return
  }

  const items = clipboardData.items
  console.log('【粘贴事件】剪贴板items:', items)
  
  if (!items || items.length === 0) {
    console.log('【粘贴事件】剪贴板中没有数据')
    return
  }

  console.log('【粘贴事件】剪贴板项目数量:', items.length)
  
  for (let i = 0; i < items.length; i++) {
    const item = items[i]
    console.log(`【粘贴事件】项目 ${i}: type=${item.type}, kind=${item.kind}`)
    
    if (item.type.startsWith('image/')) {
      event.preventDefault()
      const file = item.getAsFile()
      console.log('【粘贴事件】获取到图片文件:', file)
      
      if (file) {
        console.log('【粘贴事件】开始上传图片:', file.name, file.size)
        await uploadAndInsertImage(file)
      } else {
        console.log('【粘贴事件】无法从剪贴板获取文件')
      }
    }
  }
}

// 拖拽事件处理
function handleDragOver(event) {
  event.preventDefault()
  event.dataTransfer.dropEffect = 'copy'
}

// 放置事件处理 - 支持拖拽上传
async function handleDrop(event) {
  event.preventDefault()
  const files = event.dataTransfer?.files
  if (!files) return

  for (const file of files) {
    if (file.type.startsWith('image/')) {
      await uploadAndInsertImage(file)
    }
  }
}

// 上传图片并插入到编辑器
async function uploadAndInsertImage(file) {
  console.log('【图片上传】开始处理图片:', file.name, '大小:', file.size, '类型:', file.type)
  
  // 检查文件大小
  const maxSize = 50 * 1024 * 1024 // 50MB
  if (file.size > maxSize) {
    console.log('【图片上传】文件大小超过限制')
    ElMessage.error('图片大小不能超过 50MB')
    return
  }

  try {
    const formData = new FormData()
    formData.append('file', file)
    console.log('【图片上传】FormData 已创建，文件:', file.name)

    console.log('【图片上传】开始请求上传接口:', '/api/admin/upload')
    // request.js 的响应拦截器返回的是 response.data，即 { code, message, data }
    const res = await request.post('/api/admin/upload', formData, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
      }
    })

    console.log('【图片上传】上传成功，完整响应:', res)
    console.log('【图片上传】res.data (图片URL):', res?.data)
    console.log('【图片上传】res.code:', res?.code)
    console.log('【图片上传】vditor.value:', !!vditor.value)
    
    // res 的结构是 { code: 200, message: "xxx", data: "图片URL" }
    if (res?.code === 200 && res?.data && vditor.value) {
      const imageUrl = res.data
      console.log('【图片上传】获取到图片URL:', imageUrl)
      
      // 使用 Vditor 的 insert 方法插入图片（Markdown 格式）
      const markdownImage = `![${file.name}](${imageUrl})`
      console.log('【图片上传】插入 Markdown 图片:', markdownImage)
      vditor.value.insert(markdownImage)
      console.log('【图片上传】插入成功!')
      ElMessage.success('图片上传成功')
    } else {
      console.log('【图片上传】插入失败，条件不满足:')
      console.log('  - res.code === 200:', res?.code === 200)
      console.log('  - res.data 存在:', !!res?.data)
      console.log('  - vditor.value 存在:', !!vditor.value)
    }
  } catch (error) {
    console.error('【图片上传】上传失败，错误:', error)
    ElMessage.error('图片上传失败: ' + (error?.message || '未知错误'))
  }
}

onUnmounted(() => {
  if (vditor.value) {
    vditor.value.destroy()
  }
  // 移除事件监听
  if (editorRef.value) {
    editorRef.value.removeEventListener('paste', handlePaste)
    editorRef.value.removeEventListener('dragover', handleDragOver)
    editorRef.value.removeEventListener('drop', handleDrop)
  }
})

async function loadPostData(id) {
  try {
    const res = await postStore.fetchPostById(id)
    if (!res.data) {
      console.error('文章数据为空')
      ElMessage.error('文章不存在')
      return
    }
    const post = res.data
    form.value.title = post.title || ''
    form.value.content = post.content || ''
    form.value.summary = post.summary || ''
    form.value.coverImage = post.coverImage || ''
    form.value.status = post.status || 1
    form.value.tags = post.tags || []
    form.value.categoryId = post.categoryId || null
    if (vditor.value) {
      vditor.value.setValue(post.content || '')
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('文章加载失败')
  }
}

async function handleCoverImageUpload(event) {
  const file = event.target.files[0]
  if (!file) return

  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过 50MB')
    return
  }

  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await request.post('/admin/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
      }
    })
    form.value.coverImage = response.data
  } catch (error) {
    console.error('上传背景图失败:', error)
    ElMessage.error('上传背景图失败')
  }
}

async function handleSubmit() {
  try {
    form.value.content = vditor.value.getValue()

    if (isEdit.value && postId.value) {
      await postStore.updatePost(postId.value, form.value)
      alert('更新成功')
    } else {
      await postStore.createPost(form.value)
      alert('发布成功')
    }
    router.push('/admin/posts')
  } catch (error) {
    alert('操作失败: ' + error.message)
  }
}
</script>

<style scoped>
.editor-container {
  min-height: 100vh;
  background-color: #f8f9fa;
  display: flex;
  flex-direction: column;
}

.editor-header {
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 100;
}

.editor-title {
  font-size: 20px;
  font-weight: bold;
  color: #333333;
  margin: 0;
}

.editor-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.publish-btn {
  background-color: #409eff;
  color: #ffffff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.3s;
}

.publish-btn:hover {
  background-color: #66b1ff;
}

.back-link {
  color: #606266;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.back-link:hover {
  color: #409eff;
}

.editor-content {
  flex: 1;
  display: flex;
  gap: 24px;
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

/* 左侧：编辑器区域 */
.editor-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.title-input {
  font-size: 24px;
  font-weight: bold;
  padding: 12px 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 20px;
  outline: none;
  background-color: #ffffff;
  color: #333333;
  width: 100%;
  box-sizing: border-box;
}

.title-input::placeholder {
  color: #909399;
  font-weight: normal;
}

/* 右侧：表单区域 */
.editor-right {
  width: 320px;
  flex-shrink: 0;
}

.form-panel {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.form-item {
  margin-bottom: 20px;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.image-upload-container {
  position: relative;
  width: 100%;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  background-color: #fafafa;
  cursor: pointer;
  transition: all 0.3s;
}

.image-upload-container:hover {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.image-upload-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  z-index: 1;
}

.image-upload-placeholder {
  color: #909399;
  font-size: 14px;
}

.image-preview {
  position: relative;
  display: inline-block;
  max-width: 100%;
}

.image-preview img {
  max-width: 100%;
  max-height: 200px;
  border-radius: 4px;
}

.remove-image-btn {
  position: absolute;
  top: -10px;
  right: -10px;
  background-color: #ff4d4f;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}

.remove-image-btn:hover {
  background-color: #ff7875;
}

.vditor-container {
  flex: 1;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background-color: #ffffff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-radio-button__inner) {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
  color: #ffffff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

:deep(.el-radio-button__orig-radio:checked + .el-radio-button__inner) {
  background-color: #409eff;
  border-color: #409eff;
  color: #ffffff;
}

:deep(.vditor-ir) {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.6;
  font-size: 16px;
  color: #333333;
}

:deep(.vditor-ir h1),
:deep(.vditor-ir h2),
:deep(.vditor-ir h3),
:deep(.vditor-ir h4),
:deep(.vditor-ir h5),
:deep(.vditor-ir h6) {
  margin-top: 1.5em;
  margin-bottom: 0.8em;
  font-weight: 600;
  line-height: 1.4;
}

:deep(.vditor-ir p) {
  margin: 0 0 1em 0;
}

:deep(.vditor-ir code) {
  background-color: #f5f7fa;
  padding: 0.2em 0.4em;
  border-radius: 3px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 0.9em;
  color: #e96900;
}

:deep(.vditor-ir pre) {
  background-color: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 1em 0;
}

:deep(.vditor-ir pre code) {
  background-color: transparent;
  padding: 0;
  color: inherit;
}

:deep(.vditor-ir blockquote) {
  border-left: 4px solid #dfe2e5;
  padding-left: 16px;
  margin: 1em 0;
  color: #606266;
}

:deep(.vditor-ir ul),
:deep(.vditor-ir ol) {
  margin: 1em 0;
  padding-left: 2em;
}

:deep(.vditor-ir li) {
  margin: 0.5em 0;
}

:deep(.vditor-ir table) {
  border-collapse: collapse;
  margin: 1em 0;
  width: 100%;
}

:deep(.vditor-ir th),
:deep(.vditor-ir td) {
  border: 1px solid #e4e7ed;
  padding: 8px 12px;
  text-align: left;
}

:deep(.vditor-ir th) {
  background-color: #f5f7fa;
  font-weight: 600;
}

@media (max-width: 768px) {
  .editor-header {
    padding: 12px 16px;
  }

  .editor-content {
    flex-direction: column;
    padding: 16px;
  }

  .editor-left {
    width: 100%;
  }

  .editor-right {
    width: 100%;
  }

  .title-input {
    font-size: 20px;
  }

  .vditor-container {
    height: calc(100vh - 380px) !important;
  }
}
</style>
