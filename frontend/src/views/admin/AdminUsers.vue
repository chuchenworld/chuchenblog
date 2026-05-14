<template>
  <AdminLayout>
    <div class="admin-users">
      <div class="header">
        <h2>用户管理</h2>
      </div>

        <!-- 桌面端表格视图 -->
      <div class="hidden lg:block">
        <el-table :data="users" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="role" label="角色" width="100">
            <template #default="{ row }">
              <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'" size="small">
                {{ row.role === 'admin' ? '管理员' : '用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="handleDisable(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div class="lg:hidden space-y-3">
        <div
          v-for="user in users"
          :key="user.id"
          class="bg-white rounded-lg shadow p-3 border border-gray-200"
        >
          <div class="flex items-center justify-between mb-2">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center">
                <span class="text-white text-xs font-medium">{{ user.username.charAt(0).toUpperCase() }}</span>
              </div>
              <div>
                <div class="font-medium text-gray-900 text-sm">{{ user.username }}</div>
                <div class="text-xs text-gray-500">{{ user.email }}</div>
              </div>
            </div>
            <span :class="['px-2 py-0.5 text-xs rounded-full', user.status === 1 ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800']">
              {{ user.status === 1 ? '正常' : '禁用' }}
            </span>
          </div>

          <div class="flex items-center gap-2 text-xs text-gray-500 mb-2">
            <el-tag :type="user.role === 'admin' ? 'danger' : 'primary'" size="mini">
              {{ user.role === 'admin' ? '管理员' : '用户' }}
            </el-tag>
            <span>创建时间: {{ formatDate(user.createTime) }}</span>
          </div>

          <div class="flex gap-2 pt-2 border-t border-gray-100">
            <el-button type="danger" size="mini" @click="handleDisable(user)">
              {{ user.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AdminLayout from '@/components/AdminLayout.vue'

const users = ref([])
const loading = ref(false)

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}

function handleDisable(row) {
  // TODO: 实现禁用/启用功能
  ElMessage.info('功能开发中')
}

onMounted(() => {
  // TODO: 获取用户列表
  users.value = []
})
</script>

<style scoped>
.admin-users {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.header {
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  color: #303133;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .admin-users {
    padding: 12px;
  }

  .header {
    margin-bottom: 16px;
  }

  .lg\\:hidden .el-button--mini {
    padding: 6px 10px;
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .admin-users {
    padding: 10px;
  }

  .lg\\:hidden .flex.gap-2 {
    gap: 6px;
  }

  .lg\\:hidden .el-button--mini {
    padding: 5px 8px;
    font-size: 10px;
  }
}
</style>