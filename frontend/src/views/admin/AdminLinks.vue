<template>
  <div class="admin-links">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>友链管理</h2>
      <p class="text-gray-500 text-sm">管理友情链接申请和已通过的友链</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon bg-blue-500">
          <el-icon><Link /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ totalCount }}</p>
          <p class="stat-label">全部友链</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-yellow-500">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ pendingCount }}</p>
          <p class="stat-label">待审核</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-green-500">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ approvedCount }}</p>
          <p class="stat-label">已通过</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-gray-500">
          <el-icon><Lock /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ disabledCount }}</p>
          <p class="stat-label">已停用</p>
        </div>
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="全部" name="all">
          <span class="badge">{{ totalCount }}</span>
        </el-tab-pane>
        <el-tab-pane label="待审核" name="pending">
          <span class="badge badge-warning">{{ pendingCount }}</span>
        </el-tab-pane>
        <el-tab-pane label="已通过" name="approved">
          <span class="badge badge-success">{{ approvedCount }}</span>
        </el-tab-pane>
        <el-tab-pane label="已停用" name="disabled">
          <span class="badge badge-gray">{{ disabledCount }}</span>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <!-- 桌面端表格视图 -->
      <div class="hidden lg:block">
        <el-table :data="filteredLinks" border class="links-table">
          <el-table-column prop="siteName" label="站点名称" min-width="120">
            <template #default="scope">
              <span>{{ scope.row.siteName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="siteDescript" label="站点描述" min-width="150">
            <template #default="scope">
              <span class="truncate-text">{{ scope.row.siteDescript }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="siteUrl" label="站点链接" min-width="180">
            <template #default="scope">
              <a :href="scope.row.siteUrl" target="_blank" class="url-link">
                {{ scope.row.siteUrl }}
              </a>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag
                :type="getStatusType(scope.row.status)"
                size="small"
              >
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="申请时间" width="170">
            <template #default="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220">
            <template #default="scope">
              <el-button
                v-if="scope.row.status === 0"
                type="success"
                size="small"
                @click="handleApprove(scope.row)"
              >
                <el-icon><Check /></el-icon>
                通过
              </el-button>
              <el-button
                v-if="scope.row.status === 1"
                type="warning"
                size="small"
                @click="handleDisable(scope.row)"
              >
                <el-icon><Lock /></el-icon>
                停用
              </el-button>
              <el-button
                v-if="scope.row.status === 2"
                type="primary"
                size="small"
                @click="handleEnable(scope.row)"
              >
                <el-icon><Unlock /></el-icon>
                启用
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleReject(scope.row)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div class="lg:hidden space-y-3">
        <div
          v-for="link in filteredLinks"
          :key="link.id"
          class="bg-white rounded-lg shadow p-3 border border-gray-200"
        >
          <div class="flex items-center justify-between mb-2">
            <div class="font-medium text-gray-900 text-sm">{{ link.siteName }}</div>
            <el-tag
              :type="getStatusType(link.status)"
              size="mini"
            >
              {{ getStatusText(link.status) }}
            </el-tag>
          </div>

          <div class="text-xs text-gray-500 mb-2 line-clamp-2">{{ link.siteDescript }}</div>

          <div class="text-xs text-blue-600 mb-2 truncate">
            <a :href="link.siteUrl" target="_blank">{{ link.siteUrl }}</a>
          </div>

          <div class="text-xs text-gray-400 mb-2">
            申请时间: {{ formatDate(link.createTime) }}
          </div>

          <div class="flex flex-wrap gap-2 pt-2 border-t border-gray-100">
            <el-button
              v-if="link.status === 0"
              type="success"
              size="mini"
              @click="handleApprove(link)"
            >
              通过
            </el-button>
            <el-button
              v-if="link.status === 1"
              type="warning"
              size="mini"
              @click="handleDisable(link)"
            >
              停用
            </el-button>
            <el-button
              v-if="link.status === 2"
              type="primary"
              size="mini"
              @click="handleEnable(link)"
            >
              启用
            </el-button>
            <el-button
              type="danger"
              size="mini"
              @click="handleReject(link)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredLinks.length === 0" class="empty-state">
        <el-empty :description="getEmptyDescription()" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Link, Clock, Check, Delete, Lock, Unlock } from '@element-plus/icons-vue'
import request from '@/utils/request'

const activeTab = ref('all')
const links = ref([])

const totalCount = computed(() => links.value.length)
const pendingCount = computed(() => links.value.filter(l => l.status === 0).length)
const approvedCount = computed(() => links.value.filter(l => l.status === 1).length)
const disabledCount = computed(() => links.value.filter(l => l.status === 2).length)

const filteredLinks = computed(() => {
  if (activeTab.value === 'pending') {
    return links.value.filter(l => l.status === 0)
  } else if (activeTab.value === 'approved') {
    return links.value.filter(l => l.status === 1)
  } else if (activeTab.value === 'disabled') {
    return links.value.filter(l => l.status === 2)
  }
  return links.value
})

const handleTabChange = () => {
  loadLinks()
}

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'info'
    default: return 'default'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 0: return '待审核'
    case 1: return '已通过'
    case 2: return '已停用'
    default: return '未知'
  }
}

const getEmptyDescription = () => {
  switch (activeTab.value) {
    case 'pending': return '暂无待审核申请'
    case 'approved': return '暂无已通过友链'
    case 'disabled': return '暂无已停用友链'
    default: return '暂无友链'
  }
}

const handleApprove = async (link) => {
  await ElMessageBox.confirm(
    '确定要通过这条友链申请吗？',
    '确认通过',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  )

  try {
    const response = await request.put(`/friend-links/${link.id}/status`, {
      status: 1
    })
    
    if (response.code === 200) {
      ElMessage.success('审核通过成功')
      loadLinks()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleDisable = async (link) => {
  await ElMessageBox.confirm(
    '确定要停用这条友链吗？停用后前端将不再显示该友链。',
    '确认停用',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )

  try {
    const response = await request.put(`/friend-links/${link.id}/status`, {
      status: 2
    })
    
    if (response.code === 200) {
      ElMessage.success('停用成功')
      loadLinks()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleEnable = async (link) => {
  await ElMessageBox.confirm(
    '确定要启用这条友链吗？启用后前端将重新显示该友链。',
    '确认启用',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  )

  try {
    const response = await request.put(`/friend-links/${link.id}/status`, {
      status: 1
    })
    
    if (response.code === 200) {
      ElMessage.success('启用成功')
      loadLinks()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleReject = async (link) => {
  await ElMessageBox.confirm(
    '确定要删除这条友链吗？',
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )

  try {
    const response = await request.delete(`/friend-links/${link.id}`)
    
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadLinks()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const loadLinks = async () => {
  try {
    const response = await request.get('/friend-links/admin')
    if (response.data) {
      // 将 status 字段转换为数字类型
      links.value = response.data.map(link => ({
        ...link,
        status: parseInt(link.status) || 0
      }))
    }
  } catch (error) {
    console.error('加载友链失败:', error)
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  try {
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) {
      return '-'
    }
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (e) {
    console.error('日期格式化失败:', e)
    return '-'
  }
}

onMounted(() => {
  loadLinks()
})
</script>

<style scoped>
.admin-links {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

/* 统计卡片 */
.stats-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.stat-card {
  flex: 1;
  min-width: 200px;
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  margin: 4px 0 0;
  font-size: 14px;
  color: #909399;
}

/* 筛选标签 */
.filter-tabs {
  margin-bottom: 20px;
}

.badge {
  margin-left: 8px;
  padding: 2px 8px;
  background: #f5f7fa;
  border-radius: 10px;
  font-size: 12px;
  color: #606266;
}

.badge-warning {
  background: #fef08a;
  color: #854d0e;
}

.badge-success {
  background: #bbf7d0;
  color: #166534;
}

.badge-gray {
  background: #e5e7eb;
  color: #4b5563;
}

/* 表格容器 */
.table-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.links-table {
  width: 100%;
}

.site-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.mini-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.truncate-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
}

.url-link {
  color: #409eff;
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
}

.url-link:hover {
  text-decoration: underline;
}

.empty-state {
  padding: 40px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .admin-links {
    padding: 12px;
  }

  .stats-cards {
    gap: 12px;
  }

  .stat-card {
    min-width: calc(50% - 6px);
    padding: 12px;
  }

  .stat-icon {
    width: 36px;
    height: 36px;
    font-size: 18px;
    margin-right: 10px;
  }

  .stat-value {
    font-size: 18px;
  }

  .stat-label {
    font-size: 12px;
  }

  .table-container {
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
  .stats-cards {
    flex-direction: column;
  }

  .stat-card {
    min-width: 100%;
  }

  .lg\\:hidden .flex.flex-wrap.gap-2 {
    gap: 6px;
  }

  .lg\\:hidden .el-button--mini {
    padding: 5px 8px;
    font-size: 10px;
  }
}
</style>
