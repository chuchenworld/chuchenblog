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
              placeholder="请输入标签，按回车添加，或选择已有标签"
              class="w-full"
            >
              <el-option
                v-for="tag in allTags"
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
const allTags = ref([])

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

async function fetchTags() {
  try {
    const res = await request.get('/tags')
    allTags.value = (res.data || []).map(tag => tag.name)
  } catch (err) {
    console.error('获取标签失败:', err)
    allTags.value = []
  }
}

onMounted(async () => {
  await fetchCategories()
  await fetchTags()

  // 确保 editorRef 已经挂载
  if (!editorRef.value) {
    console.error('【Vditor】editorRef 未初始化')
    return
  }

  console.log('【Vditor】开始初始化，容器:', editorRef.value)

  try {
    console.log('【Vditor】开始初始化')
    vditor.value = new Vditor(editorRef.value, {
      mode: 'ir',
    height: 'calc(100vh - 280px)',
    cache: { enable: false },
    toolbarConfig: { 
      pin: true
    },
    toolbar: [
      { name: 'emoji', tip: 'emoji' },
      { name: 'headings', tip: '标题' },
      { name: 'bold', tip: '粗体' },
      { name: 'italic', tip: '斜体' },
      { name: 'strike', tip: '删除线' },
      { name: 'link', tip: '链接' },
      { name: 'list', tip: '无序列表' },
      { name: 'ordered-list', tip: '有序列表' },
      { name: 'check', tip: '任务列表' },
      { name: 'outdent', tip: '减少缩进' },
      { name: 'indent', tip: '增加缩进' },
      { name: 'quote', tip: '引用' },
      { name: 'line', tip: '分割线' },
      { name: 'code', tip: '代码块' },
      { name: 'inline-code', tip: '行内代码' },
      { name: 'table', tip: '表格' },
      { name: 'upload', tip: '上传' },
      { name: 'record', tip: '录音' },
      { name: 'edit-mode', tip: '编辑模式' },
      { name: 'both', tip: '分屏模式' },
      { name: 'fullscreen', tip: '全屏' },
      { name: 'preview', tip: '预览' },
      { name: 'export', tip: '导出' }
    ],
    // 禁用 Vditor 的上传功能，使用自定义处理
    upload: false,
    // 禁用所有预览功能，防止 base64 显示
    preview: {
      markdown: {
        toc: false,
      },
      mode: 'editor',  // 只显示编辑器，不显示预览
      actions: [],
    },
    // 禁用图片相关功能
    image: {
      lazyLoad: false,
      isPreview: false,
      preview: false,
    },
    // 禁用链接预览
    link: {
      isPreview: false,
    },
    // 禁用 HTML 预览
    html: {
      preview: false,
    },
    after: () => {
      console.log('【Vditor】初始化完成')

      // 绑定自定义粘贴和拖拽处理
      bindCustomPasteHandler()

      // 移除 Vditor 的图片点击处理器，防止显示 base64
      removeVditorImageHandlers()

      const id = route.query.id
      if (id) {
        isEdit.value = true
        postId.value = parseInt(id, 10)
        loadPostData(postId.value)
      }
    }
  })
} catch (error) {
  console.error('【Vditor】初始化失败:', error)
  alert('编辑器初始化失败: ' + error.message)
}
})

// 自定义粘贴处理函数
function bindCustomPasteHandler() {
  if (!vditor.value) {
    console.warn('Vditor 实例不存在')
    return
  }

  // 获取 Vditor 的编辑区域
  const editableDiv = editorRef.value.querySelector('.vditor-ir') || editorRef.value.querySelector('[contenteditable="true"]')
  if (!editableDiv) {
    console.error('【粘贴】未找到编辑区域')
    return
  }

  console.log('【粘贴】绑定自定义粘贴处理')

  // 监听粘贴事件 - 更彻底地阻止 Vditor 的默认行为
  editableDiv.addEventListener('paste', async (event) => {
    // 阻止 Vditor 的默认粘贴处理
    event.stopImmediatePropagation()
    event.preventDefault()

    const clipboardData = event.clipboardData || window.clipboardData
    if (!clipboardData) return

    const items = clipboardData.items
    for (let i = 0; i < items.length; i++) {
      const item = items[i]
      if (item.type.indexOf('image') !== -1) {
        const file = item.getAsFile()
        if (file) {
          console.log('【粘贴】检测到图片:', file.name)

          // 上传图片
          try {
            const url = await uploadImage(file)
            console.log('【粘贴】上传成功:', url)

            // 在光标位置插入图片 - 使用正确的 Vditor API
            if (vditor.value && typeof vditor.value.insertValue === 'function') {
              console.log('【粘贴】使用 vditor.value.insertValue')
              vditor.value.insertValue(`![${file.name}](${url})`)
            }
          } catch (error) {
            console.error('【粘贴】上传失败:', error)
            alert('图片上传失败: ' + error.message)
          }
          return
        }
      }
    }

    // 如果是文本粘贴，允许正常处理
    console.log('【粘贴】文本粘贴，允许处理')
  }, true)  // 捕获阶段，优先于 Vditor

  // 监听拖拽事件
  editableDiv.addEventListener('dragover', (event) => {
    event.preventDefault()
    event.dataTransfer.dropEffect = 'copy'
  })

  editableDiv.addEventListener('drop', async (event) => {
    event.preventDefault()
    const files = event.dataTransfer?.files
    if (!files) return

    for (const file of files) {
      if (file.type.startsWith('image/')) {
        console.log('【拖拽】检测到图片:', file.name)

        try {
          const url = await uploadImage(file)
          console.log('【拖拽】上传成功:', url)
          console.log('【拖拽】Vditor 实例:', vditor.value)
          console.log('【拖拽】insertValue 方法:', typeof vditor.value?.insertValue)
          console.log('【拖拽】vditor.vditor.insertValue 方法:', typeof vditor.value?.vditor?.insertValue)

          // 在光标位置插入图片 - 使用正确的 Vditor API
          if (vditor.value) {
            // vditor.value 就是 Vditor 实例，直接调用 insertValue
            if (typeof vditor.value.insertValue === 'function') {
              console.log('【拖拽】使用 vditor.value.insertValue')
              vditor.value.insertValue(`![${file.name}](${url})`)
            } else {
              console.error('【拖拽】无法找到 insertValue 方法')
              console.error('【拖拽】Vditor 实例:', vditor.value)
              console.error('【拖拽】Vditor 对象结构:', Object.keys(vditor.value))
              alert('插入图片失败：编辑器 API 不可用')
            }
          }
        } catch (error) {
          console.error('【拖拽】上传失败:', error)
          alert('图片上传失败: ' + error.message)
        }
      }
    }
  })

  // 监听图片点击事件，只阻止 base64 弹窗，不阻止图片本身
  editableDiv.addEventListener('click', (event) => {
    const target = event.target

    // 如果点击的是图片，允许正常行为，但阻止可能的 base64 弹窗
    if (target.tagName === 'IMG') {
      console.log('【图片点击】图片正常点击，允许显示')
      // 不阻止默认行为，让图片正常显示
      // 但阻止事件冒泡，防止触发 Vditor 的 base64 处理
      event.stopPropagation()
      return true
    }

    // 如果点击的是包含 base64 文本的元素（不是图片），阻止显示
    const text = target.textContent || target.innerText || ''
    if (text.includes('base64') && target.tagName !== 'IMG') {
      event.stopImmediatePropagation()
      event.preventDefault()
      console.log('【点击】阻止 base64 文本显示')
      return false
    }
  }, true)  // true 表示在捕获阶段处理，优先于 Vditor

  // 也监听整个文档的点击事件，防止事件冒泡到 Vditor
  document.addEventListener('click', (event) => {
    const target = event.target
    if (target.tagName === 'IMG' && target.closest('.vditor-ir')) {
      event.stopImmediatePropagation()
      event.preventDefault()
      console.log('【文档点击】阻止 base64 显示')
      return false
    }
  }, true)

  // 全局拦截：只拦截 base64 弹窗，不拦截图片点击
  document.addEventListener('click', (event) => {
    const target = event.target

    // 检查是否点击了 base64 弹窗（不是图片本身）
    if (target.classList && (
      target.classList.contains('vditor-tip') ||
      target.classList.contains('vditor-panel')
    )) {
      event.stopImmediatePropagation()
      event.preventDefault()
      console.log('【全局拦截】阻止 base64 弹窗显示')
      return false
    }

    // 检查是否点击了包含 base64 文本的元素（不是图片）
    const text = target.textContent || target.innerText || ''
    if (text.includes('base64') && target.tagName !== 'IMG') {
      event.stopImmediatePropagation()
      event.preventDefault()
      console.log('【全局拦截】阻止 base64 文本显示')
      return false
    }
  }, true)  // 捕获阶段，优先于其他事件

  // 监听键盘事件（删除操作）- 允许删除，但监控 base64 显示
  document.addEventListener('keydown', (event) => {
    // 检查是否是删除键（Delete、Backspace）
    if (event.key === 'Delete' || event.key === 'Backspace') {
      console.log('【键盘】检测到删除操作，允许正常删除')
      // 不阻止删除操作，让删除正常进行
      // 但会在删除后通过 MutationObserver 移除可能的 base64 弹窗
    }
  }, true)

  // 在文档级别拦截粘贴事件，防止 Vditor 处理
  document.addEventListener('paste', async (event) => {
    // 检查是否在编辑器内粘贴
    const activeElement = document.activeElement
    if (activeElement && activeElement.closest('.vditor-ir')) {
      console.log('【文档粘贴】在编辑器内粘贴，阻止 Vditor 处理')
      event.stopImmediatePropagation()
      event.preventDefault()

      const clipboardData = event.clipboardData || window.clipboardData
      if (!clipboardData) return

      const items = clipboardData.items
      for (let i = 0; i < items.length; i++) {
        const item = items[i]
        if (item.type.indexOf('image') !== -1) {
          const file = item.getAsFile()
          if (file) {
            console.log('【文档粘贴】检测到图片:', file.name)

            // 上传图片
            try {
              const url = await uploadImage(file)
              console.log('【文档粘贴】上传成功:', url)

              // 在光标位置插入图片
              if (vditor.value && typeof vditor.value.insertValue === 'function') {
                vditor.value.insertValue(`![${file.name}](${url})`)
              }
            } catch (error) {
              console.error('【文档粘贴】上传失败:', error)
              alert('图片上传失败: ' + error.message)
            }
            return
          }
        }
      }
    }
  }, true)  // 捕获阶段，优先于 Vditor
}

// 移除 Vditor 的图片点击处理器
function removeVditorImageHandlers() {
  // 使用 MutationObserver 监控 DOM 变化，阻止 base64 显示
  if (!editorRef.value) return

  const observer = new MutationObserver((mutations) => {
    mutations.forEach((mutation) => {
      // 检查是否添加了 base64 弹窗
      mutation.addedNodes.forEach((node) => {
        if (node.nodeType === 1) { // 元素节点
          // 移除所有 Vditor 弹窗（包括 base64 显示）
          if (node.classList && (
            node.classList.contains('vditor-tip') ||
            node.classList.contains('vditor-panel') ||
            node.classList.contains('vditor-image__drag') ||
            node.classList.contains('vditor-image__resize')
          )) {
            console.log('【监控】检测到 Vditor 弹窗，移除元素')
            node.remove()
            return
          }

          // 检查是否包含 base64 文本的元素（不是图片本身）
          const text = node.textContent || node.innerText || ''
          if (text.includes('base64') && node.tagName !== 'IMG') {
            console.log('【监控】检测到 base64 文本，移除元素')
            node.remove()
            return
          }

          // 检查子元素是否包含 base64
          if (node.querySelectorAll) {
            const base64Elements = node.querySelectorAll('*')
            base64Elements.forEach(el => {
              const elText = el.textContent || el.innerText || ''
              if (elText.includes('base64') && el.tagName !== 'IMG') {
                console.log('【监控】子元素包含 base64，移除')
                el.remove()
              }
            })
          }
        }
      })

      // 也检查属性变化（Vditor 可能通过修改属性显示 base64）
      if (mutation.type === 'attributes' && mutation.target) {
        const target = mutation.target
        if (target.classList && (
          target.classList.contains('vditor-tip') ||
          target.classList.contains('vditor-panel')
        )) {
          console.log('【监控】检测到 Vditor 弹窗属性变化，移除元素')
          target.remove()
        }
      }
    })
  })

  // 监控编辑器区域的变化
  const editableDiv = editorRef.value.querySelector('.vditor-ir') || editorRef.value.querySelector('[contenteditable="true"]')
  if (editableDiv) {
    observer.observe(editableDiv, {
      childList: true,
      subtree: true
    })
    console.log('【监控】MutationObserver 已启动')
  }
}

// 上传图片函数
async function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)

  const response = await fetch('/api/admin/upload', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
    },
    body: formData
  })

  const text = await response.text()
  console.log('【上传】服务器响应:', text)

  try {
    const res = JSON.parse(text)

    // 检查是否是 Vditor 标准格式
    if (res && res.succMap) {
      // 返回第一个图片的 URL
      const keys = Object.keys(res.succMap)
      if (keys.length > 0) {
        return res.succMap[keys[0]]
      }
    }

    // 兼容旧格式: { code: 200, message: "success", data: "url" }
    if (res && res.code === 200 && res.data) {
      return res.data
    }

    // 其他情况
    const errorMsg = res.message || '上传失败'
    throw new Error(errorMsg)
  } catch (e) {
    console.error('【上传】JSON 解析失败:', e)
    throw new Error('服务器返回格式错误')
  }
}

onUnmounted(() => {
  if (vditor.value) {
    vditor.value.destroy()
  }
  // 移除事件监听（现在事件绑定到 Vditor 内部元素，destroy 时会自动清理）
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
    
    await fetchTags()
    router.push('/admin/posts')
  } catch (error) {
    alert('操作失败: ' + error.message)
  }
}
</script>

<style scoped>
/* 隐藏 base64 相关的弹窗和提示，但不影响正常编辑 */
:deep(.vditor-tip),
:deep(.vditor-panel) {
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
}

/* 确保图片可以正常显示、点击和删除 */
:deep(.vditor-ir img) {
  pointer-events: auto !important;
  display: inline-block !important;
  visibility: visible !important;
  opacity: 1 !important;
}

/* 允许图片被选中和删除 */
:deep(.vditor-ir img selected) {
  outline: 2px solid #409eff !important;
}

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

/* 确保编辑器内的图片可以被选中并调整大小 */
:deep(.vditor-ir img) {
  cursor: pointer; /* 鼠标移上去变成手型 */
  max-width: 100%;
  height: auto;
  /* 如果图片周围有奇怪的边框或阴影，可以在这里重置 */
  outline: none;
  box-shadow: none;
  display: inline-block;
}

/* 图片调整大小的手柄 */
:deep(.vditor-ir__node--selected) {
  position: relative;
}

:deep(.vditor-ir__node--selected::after) {
  content: '';
  position: absolute;
  bottom: 0;
  right: 0;
  width: 10px;
  height: 10px;
  background: #409eff;
  cursor: nwse-resize;
  border-radius: 2px;
}

/* 图片选中时的边框 */
:deep(.vditor-ir__node--active) {
  outline: 2px solid #409eff !important;
  outline-offset: 2px;
}

/* 确保图片可以拖拽调整大小 */
:deep(.vditor-ir img) {
  resize: both;
  overflow: auto;
  min-width: 50px;
  min-height: 50px;
  border: 2px solid transparent;
  transition: border-color 0.2s;
}

/* 图片悬停时显示调整边框 */
:deep(.vditor-ir img:hover) {
  border-color: #409eff;
}

/* 图片调整大小时的视觉反馈 */
:deep(.vditor-ir img:active) {
  border-color: #66b1ff;
}

/* 确保粘贴的图片正确显示 */
:deep(.vditor-ir p img) {
  display: inline-block;
  margin: 8px 0;
}

/* 图片工具栏提示 */
:deep(.vditor-image__img) {
  cursor: nwse-resize;
}

/* 修复 Vditor 选中图片时的样式 */
:deep(.vditor-ir__node--active) {
  outline: 2px solid #409eff !important; /* 选中时的蓝色边框 */
}

/* 确保 Vditor 工具栏 tooltip 正常显示 */
:deep(.vditor-tooltip) {
  display: block !important;
  opacity: 1 !important;
  visibility: visible !important;
  pointer-events: auto !important;
}

:deep(.vditor-toolbar button) {
  position: relative;
}

:deep(.vditor-toolbar button:hover) {
  cursor: pointer;
}
</style>
