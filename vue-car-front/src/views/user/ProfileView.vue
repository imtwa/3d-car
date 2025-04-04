<template>
  <div class="profile">
    <div class="profile-container">
      <!-- 个人信息头部 -->
      <div class="profile-header">
        <el-card class="profile-card">
          <div class="user-info">
            <el-avatar :size="100" :src="userStore.userInfo.avatar || defaultAvatar" />
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
        <el-tabs v-model="activeTab" class="profile-tabs">
          <el-tab-pane label="我的收藏" name="collections">
            <h3 class="section-title">我收藏的车型</h3>
            <div class="collection-grid">
              <el-empty v-if="collections.length === 0" description="暂无收藏车型" />
              <el-row :gutter="20" v-else>
                <el-col :xs="24" :sm="12" :md="8" v-for="(item, index) in collections" :key="index">
                  <div class="model-card">
                    <div class="model-image">{{ item.image }}</div>
                    <div class="model-info">
                      <h3>{{ item.name }}</h3>
                      <p>{{ item.description }}</p>
                      <el-button type="primary" @click="router.push(item.link)">查看详情</el-button>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="我的帖子" name="posts">
            <h3 class="section-title">我发布的帖子</h3>
            <div class="forum-posts">
              <el-empty v-if="posts.length === 0" description="暂无发布帖子" />
              <div v-else class="post-list">
                <div class="forum-post" v-for="(post, index) in posts" :key="index">
                  <div class="post-header">
                    <div class="post-author">
                      <el-avatar :size="40" :src="userStore.userInfo.avatar || defaultAvatar" />
                      <div>
                        <div>{{ userStore.userInfo.username }}</div>
                        <div class="post-meta">{{ post.createTime }}</div>
                      </div>
                    </div>
                  </div>
                  <div class="post-content">
                    <h3>{{ post.title }}</h3>
                    <p>{{ post.content }}</p>
                  </div>
                  <div class="post-actions">
                    <div class="post-action">👍 点赞 ({{ post.likes }})</div>
                    <div class="post-action">💬 评论 ({{ post.comments }})</div>
                    <div class="post-action">🔖 收藏</div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="我的评论" name="comments">
            <h3 class="section-title">我的评论</h3>
            <div class="forum-posts">
              <el-empty v-if="comments.length === 0" description="暂无评论记录" />
              <div v-else class="comment-list">
                <div class="forum-post" v-for="(comment, index) in comments" :key="index">
                  <div class="post-header">
                    <div class="post-author">
                      <el-avatar :size="40" :src="userStore.userInfo.avatar || defaultAvatar" />
                      <div>
                        <div>{{ userStore.userInfo.username }}</div>
                        <div class="post-meta">{{ comment.createTime }}</div>
                      </div>
                    </div>
                  </div>
                  <div class="post-content">
                    <p>{{ comment.content }}</p>
                    <div class="post-meta">评论于：《{{ comment.postTitle }}》</div>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('collections')
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 模拟数据 - 收藏的车型
const collections = ref([
  {
    name: '奔驰 C级',
    description: '豪华轿车的典范之作',
    image: '奔驰 C级 图片',
    link: '/models/1'
  },
  {
    name: '奥迪 A4L',
    description: '科技与优雅的完美结合',
    image: '奥迪 A4L 图片',
    link: '/models/2'
  }
])

// 模拟数据 - 发布的帖子
const posts = ref([
  {
    title: '奔驰C级和奥迪A4L选哪个更好？',
    content: '最近打算换车，纠结于奔驰C级和奥迪A4L之间，两款车各有特点。奔驰C级外观更运动，内饰更豪华；奥迪A4L科技感更强，操控更精准。大家有什么建议吗？',
    createTime: '2023-05-20 15:45',
    likes: 15,
    comments: 6
  }
])

// 模拟数据 - 评论记录
const comments = ref([
  {
    content: '我觉得奔驰C级的内饰做工更精致，座椅也更舒适。如果更注重乘坐舒适性，可以考虑C级。',
    createTime: '2023-05-21 10:30',
    postTitle: '奔驰C级和奥迪A4L选哪个更好？'
  }
])
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

    .collection-grid {
      margin-top: 20px;

      .model-card {
        background-color: white;
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        transition: transform 0.3s;
        cursor: pointer;
        margin-bottom: 20px;
        height: 100%;

        &:hover {
          transform: translateY(-5px);
        }

        .model-image {
          height: 200px;
          background-color: #f0f0f0;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 18px;
          color: #666;
        }

        .model-info {
          padding: 20px;

          h3 {
            margin-bottom: 10px;
            color: #333;
          }

          p {
            color: #666;
            margin-bottom: 15px;
          }

          .el-button {
            background-color: #3498db;
            border-color: #3498db;
            &:hover {
              background-color: #2980b9;
              border-color: #2980b9;
            }
          }
        }
      }
    }

    .forum-posts {
      margin-top: 20px;

      .forum-post {
        background-color: white;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        margin-bottom: 20px;
        padding: 20px;

        .post-header {
          margin-bottom: 15px;

          .post-author {
            display: flex;
            align-items: center;
            gap: 15px;

            .post-meta {
              font-size: 14px;
              color: #909399;
              margin-top: 5px;
            }
          }
        }

        .post-content {
          margin-bottom: 15px;

          h3 {
            margin-bottom: 10px;
            color: #333;
          }

          p {
            color: #606266;
          }

          .post-meta {
            font-size: 14px;
            color: #909399;
            margin-top: 10px;
            font-style: italic;
          }
        }

        .post-actions {
          display: flex;
          gap: 20px;
          border-top: 1px solid #ebeef5;
          padding-top: 15px;

          .post-action {
            color: #606266;
            cursor: pointer;
            transition: color 0.3s;

            &:hover {
              color: #3498db;
            }
          }
        }
      }
    }
  }
}
</style>
