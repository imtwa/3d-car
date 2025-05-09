<template>
  <div class="profile">
    <div class="profile-container">
      <!-- 个人信息头部 -->
      <div class="profile-header">
        <el-card class="profile-card">
          <div class="user-info">
            <el-avatar :size="100" :src="userStore.userInfo.username || defaultAvatar">
              {{ userStore.userInfo.username }}
            </el-avatar>
            <div class="info-details">
              <h2>{{ userStore.userInfo.username || '用户名' }}</h2>
              <p>注册时间：{{ formatDate(userStore.userInfo.createTime) || '2023-01-15' }}</p>
              <p>邮箱：{{ userStore.userInfo.email || 'user@example.com' }}</p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 标签页导航 -->
      <div class="profile-content">
        <el-tabs v-model="activeTab" class="profile-tabs" @tab-click="handleTabClick">
          <el-tab-pane label="我的收藏" name="collections">
            <h3 class="section-title">我收藏的帖子</h3>
            <div class="collection-list">
              <el-empty
                v-if="collections.length === 0 && !loadingCollections"
                description="暂无收藏帖子"
              />
              <el-skeleton
                :loading="loadingCollections"
                animated
                :count="3"
                v-if="loadingCollections"
              >
                <template #template>
                  <div style="padding: 14px">
                    <el-skeleton-item variant="h3" style="width: 50%" />
                    <div style="display: flex; align-items: center; margin-top: 16px">
                      <el-skeleton-item
                        variant="circle"
                        style="margin-right: 16px; width: 40px; height: 40px"
                      />
                      <el-skeleton-item variant="text" style="width: 30%" />
                    </div>
                    <el-skeleton-item variant="p" style="width: 100%; margin-top: 16px" />
                    <el-skeleton-item variant="p" style="width: 100%" />
                    <el-skeleton-item variant="p" style="width: 60%" />
                  </div>
                </template>
              </el-skeleton>
              <div v-else class="post-list">
                <div
                  class="forum-post"
                  v-for="collection in collections"
                  :key="collection.id"
                  @click="goToPost(collection.post.id)"
                >
                  <div class="post-header">
                    <div class="post-author">
                      <el-avatar
                        :size="40"
                        :src="collection.post.author?.avatar || defaultAvatar"
                      />
                      <div>
                        <div>{{ collection.post.author?.username }}</div>
                        <div class="post-meta">{{ formatDate(collection.post.createTime) }}</div>
                      </div>
                    </div>
                  </div>
                  <div class="post-content">
                    <h3>{{ collection.post.title }}</h3>
                    <p>{{ collection.post.content }}</p>
                  </div>
                  <div class="post-actions">
                    <div class="post-action">💬 评论 ({{ collection.post.commentCount || 0 }})</div>
                    <div class="post-action">🔖 收藏于 {{ formatDate(collection.createTime) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="我的帖子" name="posts">
            <h3 class="section-title">我发布的帖子</h3>
            <div class="forum-posts">
              <el-empty v-if="posts.length === 0 && !loadingPosts" description="暂无发布帖子" />
              <el-skeleton :loading="loadingPosts" animated :count="3" v-if="loadingPosts">
                <template #template>
                  <div style="padding: 14px">
                    <el-skeleton-item variant="h3" style="width: 50%" />
                    <div style="display: flex; align-items: center; margin-top: 16px">
                      <el-skeleton-item
                        variant="circle"
                        style="margin-right: 16px; width: 40px; height: 40px"
                      />
                      <el-skeleton-item variant="text" style="width: 30%" />
                    </div>
                    <el-skeleton-item variant="p" style="width: 100%; margin-top: 16px" />
                    <el-skeleton-item variant="p" style="width: 100%" />
                    <el-skeleton-item variant="p" style="width: 60%" />
                  </div>
                </template>
              </el-skeleton>
              <div v-else class="post-list">
                <div
                  class="forum-post"
                  v-for="post in posts"
                  :key="post.id"
                  @click="goToPost(post.id)"
                >
                  <div class="post-header">
                    <div class="post-author">
                      <el-avatar :size="40" :src="userStore.userInfo.avatar || defaultAvatar" />
                      <div>
                        <div>{{ userStore.userInfo.username }}</div>
                        <div class="post-meta">{{ formatDate(post.createTime) }}</div>
                      </div>
                    </div>
                  </div>
                  <div class="post-content">
                    <h3>{{ post.title }}</h3>
                    <p>{{ post.content }}</p>
                  </div>
                  <div class="post-actions">
                    <div class="post-action">👍 点赞 ({{ post.likeCount || 0 }})</div>
                    <div class="post-action">💬 评论 ({{ post.commentCount || 0 }})</div>
                    <div class="post-action">👁️ 浏览 ({{ post.viewCount || 0 }})</div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="我的评论" name="comments">
            <h3 class="section-title">我的评论</h3>
            <div class="forum-posts">
              <el-empty
                v-if="comments.length === 0 && !loadingComments"
                description="暂无评论记录"
              />
              <el-skeleton :loading="loadingComments" animated :count="3" v-if="loadingComments">
                <template #template>
                  <div style="padding: 14px">
                    <el-skeleton-item variant="h3" style="width: 50%" />
                    <div style="display: flex; align-items: center; margin-top: 16px">
                      <el-skeleton-item
                        variant="circle"
                        style="margin-right: 16px; width: 40px; height: 40px"
                      />
                      <el-skeleton-item variant="text" style="width: 30%" />
                    </div>
                    <el-skeleton-item variant="p" style="width: 100%; margin-top: 16px" />
                  </div>
                </template>
              </el-skeleton>
              <div v-else class="comment-list">
                <div
                  class="forum-post"
                  v-for="comment in comments"
                  :key="comment.id"
                  @click="goToPost(comment.postId)"
                >
                  <div class="post-header">
                    <div class="post-author">
                      <el-avatar :size="40" :src="userStore.userInfo.avatar || defaultAvatar" />
                      <div>
                        <div>{{ userStore.userInfo.username }}</div>
                        <div class="post-meta">{{ formatDate(comment.createTime) }}</div>
                      </div>
                    </div>
                  </div>
                  <div class="post-content">
                    <p>{{ comment.content }}</p>
                    <div class="post-meta">
                      评论于：《{{ comment.post?.title || '帖子已删除' }}》
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserPosts, getUserComments, getUserCollections } from '@/api/forum'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('collections')
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 分页数据
const collectionPage = ref(1)
const postPage = ref(1)
const commentPage = ref(1)
const pageSize = 10

// 数据列表
const collections = ref([])
const posts = ref([])
const comments = ref([])

// 加载状态
const loadingCollections = ref(false)
const loadingPosts = ref(false)
const loadingComments = ref(false)

// 获取用户收藏的帖子
const fetchUserCollections = async () => {
  if (!userStore.isLoggedIn) return

  loadingCollections.value = true
  try {
    const res = await getUserCollections(userStore.userId, collectionPage.value, pageSize)
    // 处理返回空字符串的情况
    if (res.data === '') {
      collections.value = []
    } else {
      collections.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取收藏失败', error)
    ElMessage.error('获取收藏失败，请稍后重试')
    collections.value = []
  } finally {
    loadingCollections.value = false
  }
}

// 获取用户发布的帖子
const fetchUserPosts = async () => {
  if (!userStore.isLoggedIn) return

  loadingPosts.value = true
  try {
    const res = await getUserPosts(userStore.userId, postPage.value, pageSize)
    // 处理返回空字符串的情况
    if (res.data === '') {
      posts.value = []
    } else {
      posts.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取帖子失败', error)
    ElMessage.error('获取帖子失败，请稍后重试')
    posts.value = []
  } finally {
    loadingPosts.value = false
  }
}

// 获取用户的评论
const fetchUserComments = async () => {
  if (!userStore.isLoggedIn) return

  loadingComments.value = true
  try {
    const res = await getUserComments(userStore.userId, commentPage.value, pageSize)
    // 处理返回空字符串的情况
    if (res.data === '') {
      comments.value = []
    } else {
      comments.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取评论失败', error)
    ElMessage.error('获取评论失败，请稍后重试')
    comments.value = []
  } finally {
    loadingComments.value = false
  }
}

// 格式化日期
const formatDate = dateString => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 跳转到帖子详情
const goToPost = postId => {
  router.push(`/forum/post/${postId}`)
}

// 监听标签页切换，按需加载数据
const handleTabClick = tab => {
  console.log('Tab clicked:', tab.props.name)
  handleTabChange(tab.props.name)
}

const handleTabChange = tab => {
  if (tab === 'collections' && collections.value.length === 0) {
    fetchUserCollections()
  } else if (tab === 'posts' && posts.value.length === 0) {
    fetchUserPosts()
  } else if (tab === 'comments' && comments.value.length === 0) {
    fetchUserComments()
  }
}

// 页面加载时获取当前标签页数据
onMounted(() => {
  if (userStore.isLoggedIn) {
    // 刷新用户信息
    userStore.getUserInfo().then(() => {
      // 加载当前标签页数据
      handleTabChange(activeTab.value)
    })
  }
})
</script>

<style lang="scss" scoped>
.profile {
  min-height: 100vh;
  background-color: #f5f5f5;
  color: #333;
  line-height: 1.6;
  padding: 20px;

  .profile-container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .profile-header {
    margin-bottom: 30px;

    .profile-card {
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 30px;
      padding: 20px;

      .info-details {
        h2 {
          margin: 0 0 10px 0;
          color: #303133;
          font-size: 24px;
        }

        p {
          margin: 5px 0;
          color: #606266;
        }
      }
    }
  }

  .profile-content {
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    padding: 20px;

    .section-title {
      font-size: 20px;
      margin-bottom: 20px;
      color: #333;
      border-bottom: 2px solid #3498db;
      padding-bottom: 10px;
    }

    .collection-list {
      margin-top: 20px;
    }

    .forum-posts {
      margin-top: 20px;
    }

    .post-list,
    .comment-list {
      display: flex;
      flex-direction: column;
      gap: 20px;
    }

    .forum-post {
      background-color: white;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
      padding: 20px;
      transition: all 0.2s ease;
      cursor: pointer;

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
      }

      .post-header {
        margin-bottom: 15px;
      }

      .post-author {
        display: flex;
        align-items: center;
        gap: 15px;

        .post-meta {
          font-size: 12px;
          color: #999;
          margin-top: 5px;
        }
      }

      .post-content {
        h3 {
          margin-bottom: 10px;
          color: #333;
        }

        p {
          margin-bottom: 15px;
          color: #666;
          line-height: 1.6;
          // 内容过长时省略显示
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 3;
          overflow: hidden;
        }

        .post-meta {
          font-size: 13px;
          color: #999;
          margin-top: 10px;
        }
      }

      .post-actions {
        display: flex;
        flex-wrap: wrap;
        gap: 15px;
        margin-top: 15px;
        padding-top: 15px;
        border-top: 1px solid #eee;

        .post-action {
          color: #666;
          font-size: 14px;
        }
      }
    }
  }
}
</style>
