<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-800">文章管理</h1>
      <div class="flex gap-2">
        <el-button @click="showCategoryDialog = true">
          <el-icon class="mr-2"><Collection /></el-icon>
          管理分类
        </el-button>
        <el-button type="primary" @click="handleCreatePost">
          <el-icon class="mr-2"><Plus /></el-icon>
          新建文章
        </el-button>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow">
      <div class="p-6 border-b border-gray-200">
        <div class="flex flex-wrap items-center gap-4">
          <el-input
            v-model="searchQuery"
            placeholder="搜索文章标题"
            prefix-icon="Search"
            class="w-64"
          />
          <el-select v-model="statusFilter" placeholder="状态" class="w-32">
            <el-option label="全部" value="" />
            <el-option label="已发布" value="1" />
            <el-option label="草稿" value="0" />
          </el-select>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>
      </div>

      <!-- 桌面端表格视图 -->
      <div class="overflow-x-auto hidden lg:block">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">标题</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">分类</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">作者</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">阅读量</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">创建时间</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="post in postStore.posts" :key="post.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ post.id }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ post.title }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <el-tag v-if="getCategoryName(post.categoryId)" type="info" effect="plain" size="small">
                  {{ getCategoryName(post.categoryId) }}
                </el-tag>
                <span v-else class="text-gray-400">未分类</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ post.authorName || '未知' }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="['px-2 py-1 text-xs rounded-full', post.status === 1 ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800']">
                  {{ post.status === 1 ? '已发布' : '草稿' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ post.viewCount || 0 }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(post.createTime) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <el-button type="primary" size="small" class="mr-2" @click="handleEditPost(post)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button type="warning" size="small" class="mr-2" @click="handleManageComments(post)">
                  <el-icon><Message /></el-icon>
                  评论
                </el-button>
                <el-button type="danger" size="small" @click="handleDeletePost(post)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 移动端卡片视图 -->
      <div class="lg:hidden space-y-3">
        <div
          v-for="post in postStore.posts"
          :key="post.id"
          class="bg-white rounded-lg shadow p-3 border border-gray-200"
        >
          <!-- 标题行 -->
          <div class="flex items-start justify-between mb-2">
            <h3 class="font-medium text-gray-900 text-sm flex-1 mr-2 line-clamp-2">{{ post.title }}</h3>
            <span :class="['px-2 py-0.5 text-xs rounded-full flex-shrink-0', post.status === 1 ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800']">
              {{ post.status === 1 ? '已发布' : '草稿' }}
            </span>
          </div>

          <!-- 信息行 -->
          <div class="flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-gray-500 mb-2">
            <span>ID: {{ post.id }}</span>
            <span>{{ formatDate(post.createTime) }}</span>
            <span v-if="getCategoryName(post.categoryId)" class="text-blue-600">
              {{ getCategoryName(post.categoryId) }}
            </span>
            <span class="ml-auto">阅读: {{ post.viewCount || 0 }}</span>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-2 pt-2 border-t border-gray-100">
            <el-button type="primary" size="mini" @click="handleEditPost(post)">
              编辑
            </el-button>
            <el-button type="warning" size="mini" @click="handleManageComments(post)">
              评论
            </el-button>
            <el-button type="danger" size="mini" @click="handleDeletePost(post)">
              删除
            </el-button>
          </div>
        </div>
      </div>

      <div class="p-6 border-t border-gray-200 flex items-center justify-between">
        <div class="text-sm text-gray-500">
          共 {{ postStore.total }} 条记录
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="postStore.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <el-dialog v-model="showCategoryDialog" title="管理分类" width="500px">
      <div class="mb-4">
        <el-button type="primary" size="small" @click="showCategoryForm = true">
          <el-icon class="mr-1"><Plus /></el-icon>
          新增分类
        </el-button>
      </div>

      <div v-if="showCategoryForm" class="mb-4 p-4 bg-gray-50 rounded-lg">
        <el-form :model="categoryForm" label-width="80px">
          <el-form-item label="分类名称">
            <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
          </el-form-item>
          <el-form-item label="分类描述">
            <el-input v-model="categoryForm.description" type="textarea" placeholder="请输入分类描述" :rows="2" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSaveCategory" :loading="categoryLoading">保存</el-button>
            <el-button @click="cancelCategoryForm">取消</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="categories" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="articleCount" label="文章数" width="80" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEditCategory(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Search, Edit, Delete, Collection, Message } from '@element-plus/icons-vue'
import { usePostStore } from '@/stores/post'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const searchQuery = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const postStore = usePostStore()
const router = useRouter()

const categories = ref([])
const showCategoryDialog = ref(false)
const showCategoryForm = ref(false)
const categoryLoading = ref(false)
const categoryForm = ref({
  id: null,
  name: '',
  description: ''
})

async function fetchCategories() {
  try {
    const res = await request.get('/categories')
    categories.value = res.data || []
  } catch (err) {
    console.error('获取分类失败:', err)
  }
}

function getCategoryName(categoryId) {
  if (!categoryId) return null
  const cat = categories.value.find(c => c.id === categoryId)
  return cat ? cat.name : null
}

async function fetchPosts() {
  try {
    const status = statusFilter.value !== '' ? parseInt(statusFilter.value) : null
    await postStore.fetchPosts(currentPage.value, pageSize.value, status)
  } catch (error) {
    ElMessage.error('获取文章列表失败')
    console.error('获取文章列表失败:', error)
  }
}

function handleSearch() {
  fetchPosts()
}

function handleSizeChange(size) {
  pageSize.value = size
  fetchPosts()
}

function handleCurrentChange(current) {
  currentPage.value = current
  fetchPosts()
}

function handleCreatePost() {
  router.push('/editor')
}

function handleEditPost(post) {
  router.push(`/editor?id=${post.id}`)
}

function handleManageComments(post) {
  router.push(`/admin/comments/article/${post.id}`)
}

async function handleDeletePost(post) {
  try {
    await ElMessageBox.confirm(
      `确定要删除文章 ${post.title} 吗？`,
      '删除文章',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await postStore.deletePost(post.id)
    ElMessage.success('文章删除成功')
    fetchPosts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('文章删除失败')
      console.error('文章删除失败:', error)
    }
  }
}

function cancelCategoryForm() {
  showCategoryForm.value = false
  categoryForm.value = { id: null, name: '', description: '' }
}

async function handleSaveCategory() {
  if (!categoryForm.value.name) {
    ElMessage.warning('请输入分类名称')
    return
  }

  categoryLoading.value = true
  try {
    if (categoryForm.value.id) {
      await request.put(`/admin/categories/${categoryForm.value.id}`, categoryForm.value)
      ElMessage.success('分类更新成功')
    } else {
      await request.post('/admin/categories', categoryForm.value)
      ElMessage.success('分类创建成功')
    }
    cancelCategoryForm()
    fetchCategories()
  } catch (err) {
    ElMessage.error('操作失败')
    console.error(err)
  } finally {
    categoryLoading.value = false
  }
}

function handleEditCategory(row) {
  categoryForm.value = {
    id: row.id,
    name: row.name,
    description: row.description || ''
  }
  showCategoryForm.value = true
}

async function handleDeleteCategory(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类 "${row.name}" 吗？`,
      '删除分类',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    await request.delete(`/admin/categories/${row.id}`)
    ElMessage.success('分类删除成功')
    fetchCategories()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  fetchPosts()
  fetchCategories()
})
</script>

<style scoped>
/* 移动端适配 */
@media (max-width: 768px) {
  .flex.items-center.justify-between {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .w-64 {
    width: 100%;
  }

  .w-32 {
    width: 100%;
  }

  /* 移动端卡片视图优化 */
  .lg\\:hidden.space-y-3 > div {
    padding: 12px;
  }

  .lg\\:hidden .line-clamp-2 {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .lg\\:hidden .el-button--mini {
    padding: 6px 10px;
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .space-y-6 > div:first-child {
    flex-direction: column;
    align-items: flex-start;
  }

  /* 小屏幕按钮优化 */
  .lg\\:hidden .flex.gap-2 {
    gap: 6px;
  }

  .lg\\:hidden .el-button--mini {
    padding: 5px 8px;
    font-size: 10px;
  }
}
</style>