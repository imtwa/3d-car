<template>
  <div class="comment-section">
    <h3 class="comment-title">评论 ({{ comments.length }})</h3>
    
    <!-- 评论输入框 -->
    <div class="comment-form" v-if="userStore.isLoggedIn">
      <el-avatar :size="40" :src="userStore.userInfo?.avatar">
        {{ userStore.userInfo?.username?.charAt(0) || 'U' }}
      </el-avatar>
      <div class="comment-input-wrapper">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="2"
          placeholder="写下你的评论..."
          :maxlength="500"
          show-word-limit
        />
        <div class="comment-actions">
          <el-button type="primary" @click="submitComment" :disabled="!commentContent.trim()">
            {{ replyTo ? '回复' : '发布评论' }}
          </el-button>
          <el-button v-if="replyTo" @click="cancelReply">取消回复</el-button>
        </div>
      </div>
    </div>
    <div class="login-tip" v-else>
      请<el-button type="text" @click="showLoginDialog">登录</el-button>后发表评论
    </div>
    
    <!-- 评论列表 -->
    <div class="comment-list" v-if="comments.length > 0">
      <div 
        v-for="comment in comments" 
        :key="comment.id" 
        class="comment-item"
      >
        <el-avatar :size="40" :src="comment.author.avatar">
          {{ comment.author.username.charAt(0) }}
        </el-avatar>
        <div class="comment-content">
          <div class="comment-header">
            <span class="comment-author">{{ comment.author.username }}</span>
            <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
          </div>
          <div class="comment-text">
            <span v-if="comment.replyTo" class="reply-to">回复 <span class="reply-name">@{{ comment.replyTo.username }}</span>：</span>
            {{ comment.content }}
          </div>
          <div class="comment-actions">
            <span class="action-item" @click="handleReply(comment)">
              <el-icon><ChatLineRound /></el-icon> 回复
            </span>
            <span 
              class="action-item delete" 
              v-if="canDeleteComment(comment)" 
              @click="handleDeleteComment(comment.id)"
            >
              <el-icon><Delete /></el-icon> 删除
            </span>
          </div>
        </div>
      </div>
    </div>
    <div class="empty-comments" v-else>
      暂无评论，快来发表第一条评论吧！
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatLineRound, Delete } from '@element-plus/icons-vue'
import { addComment, deleteComment } from '@/api/forum'

const props = defineProps({
  postId: {
    type: [Number, String],
    required: true
  },
  comments: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['comment-added', 'comment-deleted', 'show-login'])

const userStore = useUserStore()
const commentContent = ref('')
const replyTo = ref(null)

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

// 判断是否可以删除评论
const canDeleteComment = (comment) => {
  return userStore.isLoggedIn && (
    comment.author.id === userStore.userId || 
    userStore.userInfo?.role === 'admin'
  )
}

// 处理回复
const handleReply = (comment) => {
  if (!userStore.isLoggedIn) {
    showLoginDialog()
    return
  }
  
  replyTo.value = {
    id: comment.author.id,
    username: comment.author.username
  }
  commentContent.value = ''
  // 滚动到评论框
  document.querySelector('.comment-form').scrollIntoView({ behavior: 'smooth' })
}

// 取消回复
const cancelReply = () => {
  replyTo.value = null
}

// 提交评论
const submitComment = async () => {
  if (!commentContent.value.trim()) {
    return
  }
  
  try {
    const commentData = {
      content: commentContent.value,
      postId: props.postId
    }
    
    if (replyTo.value) {
      commentData.replyToId = replyTo.value.id
    }
    
    const response = await addComment(props.postId, commentData)
    
    // 构建新评论对象
    const newComment = {
      id: response.data.id,
      content: commentContent.value,
      createTime: new Date().toISOString(),
      author: {
        id: userStore.userId,
        username: userStore.userInfo.username,
        avatar: userStore.userInfo.avatar
      },
      replyTo: replyTo.value
    }
    
    emit('comment-added', newComment)
    commentContent.value = ''
    replyTo.value = null
    ElMessage.success('评论发布成功')
  } catch (error) {
    console.error('评论发布失败', error)
    ElMessage.error('评论发布失败')
  }
}

// 删除评论
const handleDeleteComment = (commentId) => {
  ElMessageBox.confirm(
    '确定要删除这条评论吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteComment(commentId)
      emit('comment-deleted', commentId)
      ElMessage.success('评论删除成功')
    } catch (error) {
      console.error('评论删除失败', error)
      ElMessage.error('评论删除失败')
    }
  }).catch(() => {})
}

// 显示登录对话框
const showLoginDialog = () => {
  emit('show-login')
}
</script>

<style lang="scss" scoped>
.comment-section {
  margin-top: 30px;
  
  .comment-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .comment-form {
    display: flex;
    gap: 15px;
    margin-bottom: 30px;
    
    .comment-input-wrapper {
      flex: 1;
      
      .comment-actions {
        display: flex;
        justify-content: flex-end;
        margin-top: 10px;
        gap: 10px;
      }
    }
  }
  
  .login-tip {
    text-align: center;
    padding: 20px;
    color: #909399;
    background-color: #f5f7fa;
    border-radius: 4px;
    margin-bottom: 20px;
  }
  
  .comment-list {
    .comment-item {
      display: flex;
      gap: 15px;
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #ebeef5;
      
      &:last-child {
        border-bottom: none;
      }
      
      .comment-content {
        flex: 1;
        
        .comment-header {
          display: flex;
          justify-content: space-between;
          margin-bottom: 5px;
          
          .comment-author {
            font-weight: bold;
            color: #303133;
          }
          
          .comment-time {
            color: #909399;
            font-size: 12px;
          }
        }
        
        .comment-text {
          margin-bottom: 10px;
          line-height: 1.6;
          color: #606266;
          
          .reply-to {
            color: #909399;
            
            .reply-name {
              color: #409EFF;
              font-weight: bold;
            }
          }
        }
        
        .comment-actions {
          display: flex;
          gap: 15px;
          
          .action-item {
            display: flex;
            align-items: center;
            gap: 5px;
            color: #909399;
            font-size: 13px;
            cursor: pointer;
            
            &:hover {
              color: #409EFF;
            }
            
            &.delete:hover {
              color: #F56C6C;
            }
          }
        }
      }
    }
  }
  
  .empty-comments {
    text-align: center;
    padding: 30px;
    color: #909399;
    background-color: #f5f7fa;
    border-radius: 4px;
  }
}
</style>