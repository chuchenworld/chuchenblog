<template>
  <div class="editor-container">
    <div class="editor-header">
      <h1 class="editor-title">{{ isEdit ? '编辑文章' : '写文章' }}</h1>
      <div class="editor-actions">
        <div class="status-selector">
          <el-radio-group v-model="form.status" size="small">
            <el-radio-button :label="1">发布</el-radio-button>
            <el-radio-button :label="0">存草稿</el-radio-button>
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
      <input
        v-model="form.title"
        type="text"
        class="title-input"
        placeholder="请输入文章标题"
        required
      />

      <div class="form-row">
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
      </div>

      <div class="tags-input-container">
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
      <div ref="editorRef" class="vditor-container"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { usePostStore } from '@/stores/post'
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
      max: 10 * 1024 * 1024,
      multiple: false,
      accept: 'image/*',
      token: () => {
        return localStorage.getItem('token') || ''
      },
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
      },
      success: (response) => {
        const data = JSON.parse(response)
        return data.data
      }
    }
  })

  const id = route.query.id
  if (id) {
    isEdit.value = true
    postId.value = id
    await loadPostData(id)
  }
})

onUnmounted(() => {
  if (vditor.value) {
    vditor.value.destroy()
  }
})

async function loadPostData(id) {
  try {
    const post = await postStore.fetchPostById(id)
    form.value.title = post.data.title
    form.value.content = post.data.content
    form.value.summary = post.data.summary
    form.value.coverImage = post.data.coverImage
    form.value.status = post.data.status || 1
    form.value.tags = post.data.tags || []
    form.value.categoryId = post.data.categoryId || null
    vditor.value.setValue(post.data.content)
  } catch (error) {
    console.error('加载文章失败:', error)
    alert('加载文章失败')
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
  flex-direction: column;
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.title-input {
  font-size: 24px;
  font-weight: bold;
  padding: 12px 0;
  border: none;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
  outline: none;
  background-color: transparent;
  color: #333333;
  width: 100%;
  box-sizing: border-box;
}

.title-input::placeholder {
  color: #909399;
  font-weight: normal;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-item {
  flex: 1;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.tags-input-container {
  margin-bottom: 20px;
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
    padding: 16px;
  }

  .title-input {
    font-size: 20px;
  }

  .vditor-container {
    height: calc(100vh - 140px) !important;
  }
}
</style>