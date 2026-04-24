<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">用户管理</h1>
        <p class="text-gray-500 mt-1">管理系统用户账号</p>
      </div>
      <el-button type="primary" @click="handleAddUser">
        <el-icon class="mr-1"><Plus /></el-icon>
        新增用户
      </el-button>
    </div>
    
    <div class="bg-white rounded-lg shadow">
      <div class="p-6 border-b border-gray-200">
        <div class="flex flex-wrap gap-4">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户名"
            prefix-icon="Search"
            class="w-64"
          />
          <el-button type="primary" @click="handleSearch">
            搜索
          </el-button>
        </div>
      </div>
      <div class="p-6">
        <el-table
          :data="users"
          style="width: 100%"
          border
        >
          <el-table-column
            prop="id"
            label="ID"
            width="80"
            align="center"
          />
          <el-table-column
            prop="username"
            label="用户名"
            min-width="120"
            align="center"
          />
          <el-table-column
            prop="email"
            label="邮箱"
            min-width="180"
            align="center"
          />
          <el-table-column
            prop="role"
            label="角色"
            width="120"
            align="center"
          >
            <template #default="{ row }">
              <el-tag :type="getRoleType(row.role)">
                {{ getRoleText(row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="status"
            label="状态"
            width="180"
            align="center"
          >
            <template #default="{ row }">
              <div class="flex items-center justify-center gap-2">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
                <el-switch
                  v-model="row.status"
                  :active-value="1"
                  :inactive-value="0"
                  @change="handleStatusChange(row)"
                  :disabled="row.username === 'admin'"
                />
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="createTime"
            label="创建时间"
            min-width="180"
            align="center"
          >
            <template #default="{ row }">
              {{ formatDate(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            width="250"
            align="center"
          >
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                @click="handleEditUser(row)"
                class="mr-2"
              >
                <el-icon class="mr-1"><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                type="warning"
                size="small"
                @click="handleResetPassword(row)"
                class="mr-2"
              >
                <el-icon class="mr-1"><Key /></el-icon>
                重置密码
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleDeleteUser(row)"
                :disabled="row.username === 'admin'"
                :title="row.username === 'admin' ? '不能操作当前账号' : ''"
              >
                <el-icon class="mr-1"><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="mt-4 flex justify-between items-center">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
    
    <!-- 新增/编辑用户弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="管理员" value="admin" />
            <el-option label="编辑" value="editor" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="正常" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 重置密码确认框 -->
    <el-dialog
      v-model="resetPasswordDialogVisible"
      title="重置密码"
      width="400px"
    >
      <p>确定要将用户 <span class="font-medium">{{ resetPasswordUser?.username }}</span> 的密码重置为 <span class="font-medium text-red-500">123456</span> 吗？</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmResetPassword">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { Plus, Search, Edit, Key, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'

// 表格数据
const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

// 弹窗数据
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const form = reactive({
  id: null,
  username: '',
  password: '',
  email: '',
  role: 'user',
  status: 1
})
const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})
const formRef = ref(null)

// 重置密码数据
const resetPasswordDialogVisible = ref(false)
const resetPasswordUser = ref(null)

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 获取角色类型
const getRoleType = (role) => {
  switch (role) {
    case 'admin':
      return 'danger'
    case 'editor':
      return 'warning'
    case 'user':
      return 'success'
    default:
      return 'info'
  }
}

// 获取角色文本
const getRoleText = (role) => {
  switch (role) {
    case 'admin':
      return '管理员'
    case 'editor':
      return '编辑'
    case 'user':
      return '普通用户'
    default:
      return role
  }
}

// 获取用户列表
const getUsers = async () => {
  try {
    const response = await request.get('/users', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value
      }
    })
    users.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    console.error('获取用户列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getUsers()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getUsers()
}

// 当前页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getUsers()
}

// 状态变化
const handleStatusChange = async (row) => {
  const originalStatus = row.status
  
  const loading = ElLoading.service({
    text: '更新状态中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    await request.put(`/users/${row.id}`, {
      id: row.id,
      username: row.username,
      email: row.email,
      role: row.role,
      status: row.status
    })
    
    loading.close()
    ElMessage.success('状态更新成功')
  } catch (error) {
    loading.close()
    ElMessage.error('状态更新失败')
    console.error('状态更新失败:', error)
    row.status = originalStatus
  }
}

// 新增用户
const handleAddUser = () => {
  dialogTitle.value = '新增用户'
  Object.assign(form, {
    id: null,
    username: '',
    password: '',
    email: '',
    role: 'user',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑用户
const handleEditUser = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(form, {
    id: row.id,
    username: row.username,
    email: row.email,
    role: row.role,
    status: row.status
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (form.id) {
      // 编辑用户
      await request.put(`/users/${form.id}`, form)
      ElMessage.success('用户编辑成功')
    } else {
      // 新增用户
      await request.post('/users', form)
      ElMessage.success('用户新增成功')
    }
    
    dialogVisible.value = false
    getUsers()
  } catch (error) {
    if (error.name === 'Error') {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('操作失败')
    }
    console.error('提交表单失败:', error)
  }
}

// 重置密码
const handleResetPassword = (row) => {
  resetPasswordUser.value = row
  resetPasswordDialogVisible.value = true
}

// 确认重置密码
const confirmResetPassword = async () => {
  if (!resetPasswordUser.value) return
  
  try {
    await request.post(`/users/${resetPasswordUser.value.id}/reset-password`)
    ElMessage.success('密码重置成功')
    resetPasswordDialogVisible.value = false
  } catch (error) {
    ElMessage.error('密码重置失败')
    console.error('密码重置失败:', error)
  }
}

// 删除用户
const handleDeleteUser = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 ${row.username} 吗？`,
      '删除用户',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await request.delete(`/users/${row.id}`)
    ElMessage.success('用户删除成功')
    getUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('用户删除失败')
      console.error('用户删除失败:', error)
    }
  }
}

// 初始化
onMounted(() => {
  getUsers()
})
</script>
