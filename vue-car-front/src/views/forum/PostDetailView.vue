<template>
  <div class="post-detail-container">
    <div class="back-link">
      <el-button type="text" @click="router.push('/forum')">
        <el-icon><ArrowLeft /></el-icon> 返回论坛
      </el-button>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>
    
    <!-- 帖子详情 -->
    <template v-else-if="post">
      <div class="post-header">
        <div class="post-author">
          <el-tag v-if="post.isPinned" type="danger" size="small" class="pinned-tag">置顶</el-tag>
          <el-avatar :src="post.author.avatar" :size="40">{{ post.author.username.charAt(0) }}</el-avatar>
          <div class="author-info">
            <div class="author-name">{{ post.author.username }}</div>
            <div class="post-time">{{ formatDate(post.createTime) }}</div>
          </div>
        </div>
        <div class="post-actions" v-if="canManagePost">
          <el-dropdown trigger="click">
            <el-icon class="more-icon"><MoreFilled /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleEdit" v-if="isAuthor">编辑</el-dropdown-item>
                <el-dropdown-item @click="handleDelete" v-if="isAuthor || isAdmin">删除</el-dropdown-item>
                <el-dropdown-item @click="handlePin" v-if="isAdmin && !post.isPinned">置顶</el-dropdown-item>
                <el-dropdown-item @click="handleUnpin" v-if="isAdmin && post.isPinned">取消置顶</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <h1 class="post-title">{{ post.title }}</h1>
      
      <div class="post-content" v-html="post.content"></div>
      
      <!-- 帖子图片 -->
      <div class="post-images" v-if="post.images && post.images.length > 0">
        <el-image 
          v-for="(image, index) in post.images" 
          :key="index" 
          :src="image.url" 
          fit="cover"
          :preview-src-list="post.images.map(img => img.url)"
          :initial-index="index"
          class="post-image"
        />
      </div>
      
      <!-- 帖子操作：收藏、分享 -->
      <div class="post-actions-bar">
        <div 
          class="action-item" 
          :class="{ 'active': post.isCollected }" 
          @click="handleCollect"
        >
          <el-icon><Star /></el-icon>
          <span>收藏</span>
        </div>
        <div class="action-item" @click="sharePost">
          <el-icon><Share /></el-icon>
          <span>分享</span>
        </div>
        
        <!-- 添加编辑和删除按钮，仅在用户是作者时显示 -->
        <div v-if="isAuthor" class="post-management-actions">
          <div class="action-item" @click="handleEdit">
            <el-icon><EditPen /></el-icon>
            <span>编辑</span>
          </div>
          <div class="action-item delete" @click="handleDelete">
            <el-icon><Delete /></el-icon>
            <span>删除</span>
          </div>
        </div>
      </div>
      
      <!-- 评论区 -->
      <comment-section 
        :post-id="postId" 
        :comments="comments" 
        @comment-added="handleCommentAdded" 
        @comment-deleted="handleCommentDeleted"
        @show-login="showLoginDialog"
        @refresh-comments="fetchComments"
      />
    </template>
    
    <!-- 帖子不存在 -->
    <div v-else class="not-found">
      <el-empty description="帖子不存在或已被删除" />
      <el-button type="primary" @click="router.push('/forum')">返回论坛</el-button>
    </div>
    
    <!-- 登录对话框 -->
    <login-dialog v-if="loginDialogVisible" @close="loginDialogVisible = false" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Star, Share, MoreFilled, EditPen, Delete } from '@element-plus/icons-vue'
import { getForumPostDetail, pinForumPost, unpinForumPost, deleteForumPost, getPostComments, collectForumPost, uncollectForumPost } from '@/api/forum'
import CommentSection from './components/CommentSection.vue'
import LoginDialog from '@/components/LoginDialog.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const postId = computed(() => route.params.id)
const post = ref(null)
const comments = ref([])
const loading = ref(true)
const loginDialogVisible = ref(false)

// 计算属性：判断当前用户是否是帖子作者
const isAuthor = computed(() => {
  if (!post.value || !userStore.isLoggedIn) return false
  return userStore.userId === post.value.user.id
})

// 计算属性：判断当前用户是否是管理员
const isAdmin = computed(() => {
  return userStore.userInfo?.role === 'admin'
})

// 计算属性：判断当前用户是否可以管理帖子
const canManagePost = computed(() => {
  return isAuthor.value || isAdmin.value
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取帖子详情
const fetchPostDetail = async () => {
  loading.value = true
  try {
    const response = await getForumPostDetail(postId.value)
    post.value = response.data
    // 从帖子详情中提取评论数据
    if (post.value && post.value.comments) {
      processComments(post.value.comments)
    } else {
      comments.value = []
    }
  } catch (error) {
    console.error('获取帖子详情失败', error)
    ElMessage.error('获取帖子详情失败')
    post.value = null
    comments.value = []
  } finally {
    loading.value = false
  }
}

// 处理评论数据，确保每个评论都有author字段
const processComments = (commentsList) => {
  comments.value = commentsList.map(comment => {
    // 如果评论没有author字段，从user字段构建一个
    if (!comment.author && comment.user) {
      comment.author = {
        id: comment.user.id,
        username: comment.user.username || comment.user.nickname || '未知用户',
        avatar: comment.user.avatar || '/avatar/default.png'
      }
    }
    return comment
  })
}

// 获取评论列表 - 只用于刷新评论，复用帖子详情接口
const fetchComments = async () => {
  try {
    const response = await getForumPostDetail(postId.value)
    if (response.data && response.data.comments) {
      processComments(response.data.comments)
    } else {
      comments.value = []
    }
  } catch (error) {
    console.error('获取评论列表失败', error)
    comments.value = []
  }
}

// 处理收藏
const handleCollect = async () => {
  if (!userStore.isLoggedIn) {
    showLoginDialog()
    return
  }
  
  try {
    if (post.value.isCollected) {
      await uncollectForumPost(post.value.id, userStore.userId)
      post.value = { 
        ...post.value, 
        isCollected: false
      }
      ElMessage.success('已取消收藏')
    } else {
      await collectForumPost(post.value.id, userStore.userId)
      post.value = { 
        ...post.value, 
        isCollected: true
      }
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏操作失败', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 分享帖子
const sharePost = () => {
  // 复制当前页面链接到剪贴板
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制链接失败')
  })
}

// 处理编辑
const handleEdit = () => {
  router.push(`/forum/post/edit/${postId.value}`)
}

// 处理删除
const handleDelete = () => {
  ElMessageBox.confirm(
    '确定要删除这篇帖子吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteForumPost(post.value.id, userStore.userId)
      ElMessage.success('删除成功')
      router.push('/forum')
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => {})
}

// 处理置顶
const handlePin = async () => {
  try {
    await pinForumPost(post.value.id, userStore.userId)
    ElMessage.success('置顶成功')
    post.value.isPinned = true
  } catch (error) {
    console.error('置顶失败', error)
  }
}

// 处理取消置顶
const handleUnpin = async () => {
  try {
    await unpinForumPost(post.value.id, userStore.userId)
    ElMessage.success('取消置顶成功')
    post.value.isPinned = false
  } catch (error) {
    console.error('取消置顶失败', error)
  }
}

// 处理评论添加
const handleCommentAdded = (newComment) => {
  comments.value.unshift(newComment)
  if (post.value) {
    post.value.commentCount = (post.value.commentCount || 0) + 1
  }
}

// 处理评论删除
const handleCommentDeleted = (commentId) => {
  comments.value = comments.value.filter(comment => comment.id !== commentId)
  if (post.value && post.value.commentCount > 0) {
    post.value.commentCount--
  }
}

// 显示登录对话框
const showLoginDialog = () => {
  loginDialogVisible.value = true
}

// 组件挂载时获取帖子详情
onMounted(() => {
  fetchPostDetail()
})
</script>

<style lang="scss" scoped>
.post-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  
  .back-link {
    margin-bottom: 20px;
  }
  
  .loading-container {
    padding: 20px 0;
  }
  
  .post-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;
    
    .post-author {
      display: flex;
      align-items: center;
      gap: 10px;
      
      .pinned-tag {
        margin-right: 5px;
      }
      
      .author-info {
        .author-name {
          font-weight: bold;
          font-size: 16px;
        }
        
        .post-time {
          color: #909399;
          font-size: 12px;
        }
      }
    }
    
    .more-icon {
      font-size: 20px;
      color: #909399;
      cursor: pointer;
      padding: 5px;
      
      &:hover {
        color: #409EFF;
      }
    }
  }
  
  .post-title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
    color: #303133;
  }
  
  .post-content {
    line-height: 1.8;
    color: #606266;
    margin-bottom: 30px;
    overflow-wrap: break-word;
    
    ::v-deep(img) {
      max-width: 100%;
      height: auto;
      margin: 10px 0;
      border-radius: 4px;
    }
    
    ::v-deep(p) {
      margin-bottom: 15px;
    }
  }
  
  .post-images {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 30px;
    
    .post-image {
      width: 150px;
      height: 150px;
      border-radius: 4px;
      object-fit: cover;
    }
  }
  
  .post-actions-bar {
    display: flex;
    gap: 30px;
    padding: 15px 0;
    border-top: 1px solid #ebeef5;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 30px;
    
    .action-item {
      display: flex;
      align-items: center;
      gap: 5px;
      color: #909399;
      cursor: pointer;
      padding: 5px 10px;
      border-radius: 4px;
      transition: all 0.3s;
      
      &:hover, &.active {
        color: #409EFF;
        background-color: #ecf5ff;
      }
      
      &.delete:hover {
        color: #F56C6C;
        background-color: #fef0f0;
      }
    }
    
    .post-management-actions {
      display: flex;
      gap: 15px;
      margin-left: auto;
    }
  }
  
  .not-found {
    text-align: center;
    padding: 50px 0;
    
    .el-button {
      margin-top: 20px;
    }
  }
}
</style>