<template>
  <el-card class="post-card" shadow="hover">
    <!-- 帖子头部：作者信息和时间 -->
    <div class="post-header">
      <div class="post-author">
        <el-tag v-if="post.isTop" type="danger" size="small" class="pinned-tag">置顶</el-tag>
        <el-avatar :src="post?.author?.avatar" :size="40">{{ post.author.username.charAt(0) }}</el-avatar>
        <div class="author-info">
          <div class="author-name">{{ post?.author?.username }}</div>
          <div class="post-time">{{ formatDate(post.createTime) }}</div>
        </div>
      </div>
      <div class="post-actions-dropdown" v-if="canManagePost">
        <el-dropdown trigger="click">
          <el-icon class="more-icon"><MoreFilled /></el-icon>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleEdit" v-if="isAuthor">编辑</el-dropdown-item>
              <el-dropdown-item @click="handleDelete" v-if="isAuthor || isAdmin">删除</el-dropdown-item>
              <el-dropdown-item @click="handlePin" v-if="isAdmin && !post.isTop">置顶</el-dropdown-item>
              <el-dropdown-item @click="handleUnpin" v-if="isAdmin && post.isTop">取消置顶</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 帖子内容 -->
    <div class="post-content" @click="goToDetail">
      <h3 class="post-title">{{ post.title }}</h3>
      <div class="post-text" v-html="post.content"></div>
      
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
    </div>

    <!-- 帖子底部：点赞、评论、收藏 -->
    <div class="post-footer">
      <div 
        class="action-item" 
        :class="{ 'active': post.isLiked }" 
        @click="handleLike"
      >
        <!-- <el-icon><ThumbUp  /></el-icon> -->
        <span>{{ post.likeCount || 0 }}</span>
      </div>
      <div class="action-item" @click="goToDetail">
        <el-icon><ChatLineRound /></el-icon>
        <span>{{ post.commentCount || 0 }}</span>
      </div>
      <div 
        class="action-item" 
        :class="{ 'active': post.isCollected }" 
        @click="handleCollect"
      >
        <el-icon><Star /></el-icon>
        <span>收藏</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatLineRound, Star , MoreFilled } from '@element-plus/icons-vue'
import { likeForumPost, unlikeForumPost, pinForumPost, unpinForumPost, deleteForumPost } from '@/api/forum'

const props = defineProps({
  post: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update:post', 'deleted', 'pinned', 'unpinned'])

const router = useRouter()
const userStore = useUserStore()

// 计算属性：判断当前用户是否是帖子作者
const isAuthor = computed(() => {
  return userStore.userId === props.post.userId
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

// 跳转到帖子详情页
const goToDetail = () => {
  router.push(`/forum/post/${props.post.id}`)
}

// 处理点赞
const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    if (props.post.isLiked) {
      await unlikeForumPost(props.post.id, userStore.userId)
      emit('update:post', { 
        ...props.post, 
        isLiked: false,
        likeCount: props.post.likeCount - 1 
      })
    } else {
      await likeForumPost(props.post.id, userStore.userId)
      emit('update:post', { 
        ...props.post, 
        isLiked: true,
        likeCount: props.post.likeCount + 1 
      })
    }
  } catch (error) {
    console.error('点赞操作失败', error)
  }
}

// 处理收藏
const handleCollect = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  
  // TODO: 实现收藏功能
  emit('update:post', { 
    ...props.post, 
    isCollected: !props.post.isCollected 
  })
}

// 处理编辑
const handleEdit = () => {
  router.push(`/forum/post/edit/${props.post.id}`)
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
      await deleteForumPost(props.post.id, userStore.userId)
      ElMessage.success('删除成功')
      emit('deleted', props.post.id)
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => {})
}

// 置顶帖子
const handlePin = async () => {
  try {
    const res = await pinForumPost(props.post.id)
    if (res.code === 200) {
      ElMessage.success('帖子已置顶')
      // 更新当前帖子的置顶状态
      const updatedPost = { ...props.post, isPinned: true, isTop: 1 }
      emit('update:post', updatedPost)
      emit('pinned', props.post.id)
    }
  } catch (error) {
    console.error('置顶帖子失败:', error)
    ElMessage.error('置顶帖子失败，请稍后重试')
  }
}

// 取消置顶
const handleUnpin = async () => {
  try {
    const res = await unpinForumPost(props.post.id)
    if (res.code === 200) {
      ElMessage.success('已取消置顶')
      // 更新当前帖子的置顶状态
      const updatedPost = { ...props.post, isPinned: false, isTop: 0 }
      emit('update:post', updatedPost)
      emit('unpinned', props.post.id)
    }
  } catch (error) {
    console.error('取消置顶失败:', error)
    ElMessage.error('取消置顶失败，请稍后重试')
  }
}
</script>

<style lang="scss" scoped>
.post-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .post-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 15px;

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

  .post-content {
    margin-bottom: 15px;

    .post-title {
      font-size: 18px;
      font-weight: bold;
      margin-bottom: 10px;
      color: #303133;
    }

    .post-text {
      color: #606266;
      margin-bottom: 15px;
      line-height: 1.6;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
    }

    .post-images {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-top: 10px;

      .post-image {
        width: 120px;
        height: 120px;
        border-radius: 4px;
        object-fit: cover;
      }
    }
  }

  .post-footer {
    display: flex;
    gap: 20px;
    color: #909399;

    .action-item {
      display: flex;
      align-items: center;
      gap: 5px;
      cursor: pointer;
      padding: 5px 0;
      transition: color 0.3s;

      &:hover, &.active {
        color: #409EFF;
      }
    }
  }
}
</style>