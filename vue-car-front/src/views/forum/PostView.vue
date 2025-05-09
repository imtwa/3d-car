<template>
  <div class="forum-post">
    <div class="post-header">
      <h1>{{ isEdit ? '编辑帖子' : '发布帖子' }}</h1>
      <el-button @click="router.push('/forum')">
        <el-icon><Close /></el-icon> 取消
      </el-button>
    </div>

    <el-form
      :model="form"
      :rules="rules"
      ref="formRef"
      label-width="80px"
      @submit.prevent="submitPost"
    >
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="form.title"
          placeholder="请输入标题（5-50个字符）"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>

      <rich-text-editor
        v-model="form.content"
        label="内容"
        prop="content"
        placeholder="请输入帖子内容..."
      />

      <el-form-item>
        <el-button type="primary" @click="submitPost" :loading="submitting">
          {{ isEdit ? '保存修改' : '发布帖子' }}
        </el-button>
        <el-button @click="router.push('/forum')">取消</el-button>
      </el-form-item>
    </el-form>

    <!-- 登录对话框 -->
    <login-dialog v-if="loginDialogVisible" @close="loginDialogVisible = false" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { Close } from '@element-plus/icons-vue'
import { createForumPost, updateForumPost, getForumPostDetail } from '@/api/forum'
import RichTextEditor from './components/RichTextEditor.vue'
import LoginDialog from '@/components/LoginDialog.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const submitting = ref(false)
const loginDialogVisible = ref(false)

// 判断是否为编辑模式
const isEdit = computed(() => {
  return route.params.id !== undefined
})

// 表单数据
const form = reactive({
  title: '',
  content: ''
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 5, max: 50, message: '标题长度应在5-50个字符之间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 10, message: '内容不能少于10个字符', trigger: 'blur' }
  ]
}

// 提交帖子
const submitPost = async () => {
  // 检查用户是否登录
  if (!userStore.isLoggedIn) {
    loginDialogVisible.value = true
    return
  }

  // 表单验证
  await formRef.value.validate(async valid => {
    if (!valid) return

    submitting.value = true

    try {
      const postData = {
        title: form.title,
        content: form.content,
        userId: userStore.userId // 添加用户ID字段
      }

      if (isEdit.value) {
        // 更新帖子
        await updateForumPost(route.params.id, postData)
        ElMessage.success('帖子更新成功')
      } else {
        // 创建新帖子
        const response = await createForumPost(postData)
        ElMessage.success('帖子发布成功')
        // 跳转到帖子详情页
        router.push(`/forum`)
        return
      }

      // 返回论坛页面
      router.push('/forum')
    } catch (error) {
      console.error('发布帖子失败', error)
      ElMessage.error('发布失败，请稍后重试')
    } finally {
      submitting.value = false
    }
  })
}

// 获取帖子详情（编辑模式）
const fetchPostDetail = async () => {
  if (!isEdit.value) return

  try {
    const response = await getForumPostDetail(route.params.id)
    const postData = response.data

    // 检查是否有权限编辑
    if (postData.user.id !== userStore.userId && userStore.userInfo?.role !== 'admin') {
      ElMessage.error('您没有权限编辑此帖子')
      router.push('/forum')
      return
    }

    // 填充表单数据
    form.title = postData.title
    form.content = postData.content

    // 记录已上传的图片
    if (postData.images && postData.images.length > 0) {
      uploadedImages.value = [...postData.images]
    }
  } catch (error) {
    console.error('获取帖子详情失败', error)
    ElMessage.error('获取帖子详情失败')
    router.push('/forum')
  }
}

// 组件挂载时检查登录状态并获取帖子详情（如果是编辑模式）
onMounted(() => {
  if (!userStore.isLoggedIn) {
    loginDialogVisible.value = true
    return
  }

  if (isEdit.value) {
    fetchPostDetail()
  }
})
</script>

<style lang="scss" scoped>
.forum-post {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;

  .post-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h1 {
      font-size: 24px;
      font-weight: bold;
      margin: 0;
    }
  }
}
</style>
