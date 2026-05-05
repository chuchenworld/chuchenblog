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
    // 【核心修复】直接使用 Vditor 内置的上传配置，不要自己手写 render
    upload: {
      url: '/api/admin/upload', // 你的上传接口
      fieldName: 'file', // 根据后端调整
      paste: false, // 禁用 Vditor 自带的粘贴上传，交由自定义的 handlePaste 处理
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
      },
      // 【关键】转换后端返回的数据格式为 Vditor 标准格式
      // Vditor 默认期望格式: { "msg": "", "code": 0, "data": { "errFiles": [], "succMap": {} } }
      format: (files) => {
        console.log('【上传】待上传文件:', files);
        return files;
      },
      // 【关键】处理服务器响应，转换为 Vditor 能识别的格式
      // 假设后端返回: { "code": 200, "data": "https://url/to/image.png" }
      response: (text, name) => {
        console.log('【上传】服务器响应:', text);
        
        // 容错处理：空响应
        if (!text || text.trim() === '') {
          console.error('【上传】服务器返回空响应');
          return { errFiles: ['服务器返回空响应'], succMap: {} };
        }
        
        let res;
        try {
          res = JSON.parse(text);
        } catch (e) {
          console.error('【上传】JSON 解析失败:', e);
          return { errFiles: ['服务器返回格式错误'], succMap: {} };
        }
        
        // 容错处理：空数据
        if (!res || typeof res !== 'object') {
          console.error('【上传】响应数据为空或不是对象');
          return { errFiles: ['响应数据格式错误'], succMap: {} };
        }
        
        // 检查是否成功
        // 支持多种成功码：200, "200", 0, "0", "success"
        const successCodes = [200, '200', 0, '0', 'success', 'SUCCESS'];
        const isSuccess = successCodes.includes(res.code) || res.success;
        
        if (isSuccess && res.data) {
          // 如果 data 是字符串（直接返回 URL）
          if (typeof res.data === 'string') {
            return { errFiles: [], succMap: { [name]: res.data } };
          }
          // 如果 data 是对象（包含 succMap）
          if (typeof res.data === 'object' && res.data.succMap) {
            return { 
              errFiles: res.data.errFiles || [], 
              succMap: res.data.succMap 
            };
          }
          // 其他情况
          console.error('【上传】data 字段格式不支持:', res.data);
          return { errFiles: ['返回数据格式不支持'], succMap: {} };
        } else {
          const errorMsg = res.message || res.msg || '上传失败';
          console.error('【上传】失败:', errorMsg);
          return { errFiles: [errorMsg], succMap: {} };
        }
      }
    },
    after: () => {
      console.log('Vditor 初始化完成')
      bindEditorEvents()
      
      // 为工具栏按钮添加悬浮提示
      setTimeout(() => {
        const toolbarButtons = document.querySelectorAll('.vditor-toolbar button')
        toolbarButtons.forEach(btn => {
          const icon = btn.querySelector('use')
          if (icon) {
            const href = icon.getAttribute('xlink:href') || icon.getAttribute('href')
            if (href && href.includes('emoji')) {
              btn.setAttribute('title', 'emoji')
            } else if (href && href.includes('heading')) {
              btn.setAttribute('title', '标题')
            } else if (href && href.includes('bold')) {
              btn.setAttribute('title', '粗体')
            } else if (href && href.includes('italic')) {
              btn.setAttribute('title', '斜体')
            } else if (href && href.includes('strike')) {
              btn.setAttribute('title', '删除线')
            } else if (href && href.includes('link')) {
              btn.setAttribute('title', '链接')
            } else if (href && href.includes('list')) {
              btn.setAttribute('title', '无序列表')
            } else if (href && href.includes('ordered')) {
              btn.setAttribute('title', '有序列表')
            } else if (href && href.includes('check')) {
              btn.setAttribute('title', '任务列表')
            } else if (href && href.includes('outdent')) {
              btn.setAttribute('title', '减少缩进')
            } else if (href && href.includes('indent')) {
              btn.setAttribute('title', '增加缩进')
            } else if (href && href.includes('quote')) {
              btn.setAttribute('title', '引用')
            } else if (href && href.includes('line')) {
              btn.setAttribute('title', '分割线')
            } else if (href && href.includes('code')) {
              btn.setAttribute('title', '代码块')
            } else if (href && href.includes('inline-code')) {
              btn.setAttribute('title', '行内代码')
            } else if (href && href.includes('table')) {
              btn.setAttribute('title', '表格')
            } else if (href && href.includes('upload')) {
              btn.setAttribute('title', '上传')
            } else if (href && href.includes('record')) {
              btn.setAttribute('title', '录音')
            } else if (href && href.includes('edit-mode')) {
              btn.setAttribute('title', '编辑模式')
            } else if (href && href.includes('both')) {
              btn.setAttribute('title', '分屏模式')
            } else if (href && href.includes('fullscreen')) {
              btn.setAttribute('title', '全屏')
            } else if (href && href.includes('preview')) {
              btn.setAttribute('title', '预览')
            } else if (href && href.includes('export')) {
              btn.setAttribute('title', '导出')
            }
          }
        })
      }, 100)
      
      const id = route.query.id
      if (id) {
        isEdit.value = true
        postId.value = parseInt(id, 10)
        loadPostData(postId.value)
      }
    }
  })
})

// 修复后的事件绑定函数
function bindEditorEvents() {
  if (!vditor.value) {
    console.warn('Vditor 实例不存在，无法绑定事件')
    return
  }

  // 延长延迟至 500ms，确保 Vditor 内部完全初始化
  setTimeout(() => {
    if (!vditor.value) {
      console.warn('Vditor 实例已销毁')
      return
    }

    const editableDiv = editorRef.value.querySelector('.vditor-ir') || editorRef.value.querySelector('[contenteditable="true"]')
    if (editableDiv) {
      console.log('【编辑器事件】找到内部编辑区域，绑定粘贴事件:', editableDiv)
      editableDiv.addEventListener('paste', handlePaste)
      editableDiv.addEventListener('dragover', handleDragOver)
      editableDiv.addEventListener('drop', handleDrop)
    } else {
      console.error('【编辑器事件】未找到内部可编辑区域')
      // 如果还是找不到，递归重试（最多3次）
      let retryCount = 0;
      const retryInterval = setInterval(() => {
        const el = editorRef.value.querySelector('.vditor-ir');
        if (el || retryCount > 3) {
          clearInterval(retryInterval);
          if (el) bindEditorEvents(); // 重新绑定
        }
        retryCount++;
      }, 200);
    }
  }, 500) // 修改此处为 500
}

// 修复后的粘贴处理函数（增加了 try-catch）
function handlePaste(event) {
  try {
    const clipboardData = event.clipboardData || window.clipboardData;
    if (!clipboardData) return;

    const items = clipboardData.items;
    for (let i = 0; i < items.length; i++) {
      const item = items[i];
      if (item.type.indexOf('image') !== -1) {
        const file = item.getAsFile();
        if (file) {
          console.log('【粘贴】检测到图片:', file.name);
          event.preventDefault();
          
          // 【关键修复】增加对 tip 对象的检查
          if (vditor.value && vditor.value.vditor && vditor.value.vditor.tip) {
            vditor.value.vditor.tip.paste(file);
          } else {
            console.error('Vditor tip 对象未初始化，请检查 Vditor 是否加载完成');
            alert('编辑器正在初始化，请稍后再试粘贴');
          }
          return;
        }
      }
    }
  } catch (error) {
    console.error('【粘贴功能】发生致命错误:', error);
    alert('图片粘贴失败：' + error.message);
  }
}

// 拖拽事件处理
function handleDragOver(event) {
  event.preventDefault()
  event.dataTransfer.dropEffect = 'copy'
}

// 放置事件处理 - 支持拖拽上传
function handleDrop(event) {
  event.preventDefault()
  const files = event.dataTransfer?.files
  if (!files) return

  for (const file of files) {
    if (file.type.startsWith('image/')) {
      // 使用 Vditor 内置的粘贴处理器来处理拖拽文件
      vditor.value.vditor.tip.paste(file)
    }
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
