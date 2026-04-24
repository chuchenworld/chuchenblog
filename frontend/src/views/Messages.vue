<template>
  <MainLayout>
    <div class="messages-page">
      <div class="header">
        <h2>留言板</h2>
      </div>

      <el-card class="message-card">
        <template #header>
          <div class="card-header">
            <h3>留下你的评论</h3>
          </div>
        </template>
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="name">
            <el-input v-model="form.name" placeholder="请输入您的姓名" />
          </el-form-item>
          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="请输入您的邮箱" />
          </el-form-item>
          <el-form-item prop="content">
            <el-input
              v-model="form.content"
              type="textarea"
              rows="4"
              placeholder="请输入您的留言内容"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit">提交留言</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="messages-list">
        <h3>留言列表</h3>
        <el-empty v-if="messages.length === 0" description="暂无留言" />
        <div v-else class="message-items">
          <div v-for="message in messages" :key="message.id" class="message-item">
            <div class="message-header">
              <span class="message-name">{{ message.name }}</span>
              <span class="message-time">{{ message.time }}</span>
            </div>
            <div class="message-content">{{ message.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import MainLayout from '@/components/MainLayout.vue'

const formRef = ref()
const form = reactive({
  name: '',
  email: '',
  content: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  content: [{ required: true, message: '请输入留言内容', trigger: 'blur' }]
}

const messages = ref([
  {
    id: 1,
    name: '访客',
    time: '2024-01-15 14:30',
    content: '网站设计得很美观，内容也很丰富，继续加油！'
  },
  {
    id: 2,
    name: '小明',
    time: '2024-01-10 09:15',
    content: '学习了很多技术知识，感谢分享！'
  }
])

async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      const newMessage = {
        id: messages.value.length + 1,
        name: form.name,
        time: new Date().toLocaleString('zh-CN'),
        content: form.content
      }
      messages.value.unshift(newMessage)
      form.name = ''
      form.email = ''
      form.content = ''
      ElMessage.success('留言成功')
    }
  })
}
</script>

<style scoped>
.messages-page {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
}

.header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.header h2 {
  margin: 0;
  color: #303133;
}

.message-card {
  margin-bottom: 30px;
  transition: all 0.3s;
}

.message-card:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.card-header h3 {
  margin: 0;
  color: #303133;
}

.messages-list h3 {
  margin: 0 0 16px;
  color: #303133;
}

.message-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-item {
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
}

.message-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.message-name {
  font-weight: 500;
  color: #303133;
}

.message-time {
  color: #909399;
  font-size: 14px;
}

.message-content {
  color: #606266;
  line-height: 1.6;
}
</style>