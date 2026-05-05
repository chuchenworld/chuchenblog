<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-800">标签管理</h1>
      <el-button type="primary" @click="handleCreateTag">
        <el-icon class="mr-2"><Plus /></el-icon>
        新建标签
      </el-button>
    </div>

    <div class="bg-white rounded-lg shadow">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">标签名称</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">颜色</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">文章数量</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="tag in tags" :key="tag.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ tag.id }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                <el-tag :color="tag.color" effect="dark">{{ tag.name }}</el-tag>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <span class="inline-flex items-center gap-2">
                  <span class="w-4 h-4 rounded" :style="{ backgroundColor: tag.color }"></span>
                  {{ tag.color }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ tag.articleCount || 0 }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <el-button type="primary" size="small" class="mr-2" @click="handleEditTag(tag)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click="handleDeleteTag(tag)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="tags.length === 0" class="p-6 text-center text-gray-500">
        暂无标签，请点击"新建标签"创建
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="tagForm" label-width="80px">
        <el-form-item label="标签名称">
          <el-input v-model="tagForm.name" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="标签颜色">
          <el-color-picker v-model="tagForm.color" />
          <span class="ml-3 text-sm text-gray-500">{{ tagForm.color }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const tags = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建标签')
const loading = ref(false)
const isEdit = ref(false)

const tagForm = ref({
  id: null,
  name: '',
  color: '#409EFF'
})

async function fetchTags() {
  try {
    const res = await request.get('/admin/tags')
    tags.value = res.data || []
  } catch (error) {
    console.error('获取标签列表失败:', error)
    ElMessage.error('获取标签列表失败')
  }
}

function handleCreateTag() {
  dialogTitle.value = '新建标签'
  isEdit.value = false
  tagForm.value = {
    id: null,
    name: '',
    color: '#409EFF'
  }
  dialogVisible.value = true
}

function handleEditTag(tag) {
  dialogTitle.value = '编辑标签'
  isEdit.value = true
  tagForm.value = {
    id: tag.id,
    name: tag.name,
    color: tag.color || '#409EFF'
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!tagForm.value.name || !tagForm.value.name.trim()) {
    ElMessage.warning('请输入标签名称')
    return
  }

  loading.value = true
  try {
    if (isEdit.value) {
      await request.put(`/admin/tags/${tagForm.value.id}`, tagForm.value)
      ElMessage.success('标签更新成功')
    } else {
      await request.post('/admin/tags', tagForm.value)
      ElMessage.success('标签创建成功')
    }
    dialogVisible.value = false
    fetchTags()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    loading.value = false
  }
}

async function handleDeleteTag(tag) {
  try {
    await ElMessageBox.confirm(
      `确定要删除标签"${tag.name}"吗？`,
      '删除标签',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await request.delete(`/admin/tags/${tag.id}`)
    ElMessage.success('标签删除成功')
    fetchTags()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除标签失败:', error)
      ElMessage.error('删除标签失败')
    }
  }
}

onMounted(() => {
  fetchTags()
})
</script>
