<template>
  <div class="forum-container">
    <div class="forum-header">
      <h1>论坛交流</h1>
      <el-button type="primary" @click="router.push('/forum/post')" v-if="userStore.isLoggedIn">
        <el-icon><Plus /></el-icon> 发布帖子
      </el-button>
      <el-button type="primary" @click="showLoginDialog" v-else>
        登录发帖
      </el-button>
    </div>

    <div class="forum-filters">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="最新" name="latest"></el-tab-pane>
        <el-tab-pane label="热门" name="hot"></el-tab-pane>
      </el-tabs>
      
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索帖子"
          clearable
          @keyup.enter="searchPosts"
        >
          <template #append>
            <el-button @click="searchPosts">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 置顶帖子 -->
    <div class="pinned-posts" v-if="pinnedPosts.length > 0">
      <h2 class="section-title">置顶帖子</h2>
      <post-card
        v-for="(post, index) in pinnedPosts"
        :key="post.id"
        :post="post"
        @deleted="handlePostDeleted"
        @unpinned="handlePostUnpinned"
        @update:post="(updatedPost) => handlePostUpdate(updatedPost, index, 'pinned')"
      />
    </div>

    <!-- 帖子列表 -->
    <div class="post-list">
      <h2 class="section-title">{{ getTabTitle }}</h2>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
        <el-skeleton :rows="3" animated />
      </div>
      
      <template v-else-if="posts.length > 0">
        <post-card
          v-for="(post, index) in posts"
          :key="post.id"
          :post="post"
          @deleted="handlePostDeleted"
          @pinned="handlePostPinned"
          @update:post="(updatedPost) => handlePostUpdate(updatedPost, index, 'regular')"
        />
        
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </template>
      
      <div v-else class="empty-state">
        <el-empty description="暂无帖子" />
        <el-button type="primary" @click="router.push('/forum/post')" v-if="userStore.isLoggedIn">
          发布第一篇帖子
        </el-button>
      </div>
    </div>
    
    <!-- 登录对话框 -->
    <login-dialog v-if="loginDialogVisible" @close="loginDialogVisible = false" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Plus, Search } from '@element-plus/icons-vue'
import { getForumPosts, getPinnedPosts } from '@/api/forum'
import { ElMessage } from 'element-plus'
import PostCard from './components/PostCard.vue'
import LoginDialog from '@/components/LoginDialog.vue'

// Handle post updates for both pinned and regular posts
const handlePostUpdate = (updatedPost, index, type) => {
  if (type === 'pinned') {
    pinnedPosts.value[index] = updatedPost
  } else {
    posts.value[index] = updatedPost
  }
}

const router = useRouter()
const userStore = useUserStore()

// 状态变量
const activeTab = ref('latest')
const searchKeyword = ref('')
const posts = ref([
  {
    id: 1,
    title: '分享我的新车体验',
    content: '最近入手了一台新车，来分享一下使用体验...',
    author: {
      id: 1,
      username: 'user1',
      avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    },
    createTime: '2024-03-15',
    commentCount: 10,
    likeCount: 25,
    isPinned: false,
    isLiked: false
  }
])
const pinnedPosts = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loginDialogVisible = ref(false)

// 计算属性：获取当前标签页标题
const getTabTitle = computed(() => {
  const titles = {
    latest: '最新帖子',
    hot: '热门帖子'
  }
  return titles[activeTab.value] || '帖子列表'
})

// 加载帖子列表
const fetchPosts = async () => {
  loading.value = true
  try {
    // // 先加载置顶帖子
    // if (currentPage.value === 1 && !searchKeyword.value) {
    //   await fetchPinnedPosts()
    // } else {
    //   pinnedPosts.value = []
    // }
    
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      userId: userStore.userId || null
    }
    
    // 根据标签页添加查询条件
    if (activeTab.value === 'hot') {
      params.orderBy = 'likeCount'
    }
    
    // 添加搜索关键词
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    
    const res = await getForumPosts(params)
    if (res.code === 200) {
      const data = res.data
      
      // 只获取非置顶帖子
      posts.value = data.records || []
      total.value = data.total
    }
  } catch (error) {
    console.error('加载帖子失败:', error)
    ElMessage.error('加载帖子失败，请稍后重试')
    posts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 获取置顶帖子
const fetchPinnedPosts = async () => {
  try {
    const limit = 5
    const response = await getPinnedPosts(limit, userStore.userId)
    // 适配后端返回的数据结构
    if (response.data && Array.isArray(response.data)) {
      pinnedPosts.value = response.data
    } else {
      console.error('获取置顶帖子时，后端返回的数据结构不符合预期', response)
      pinnedPosts.value = []
    }
  } catch (error) {
    console.error('获取置顶帖子失败', error)
    pinnedPosts.value = []
  }
}

// 搜索帖子
const searchPosts = () => {
  currentPage.value = 1
  fetchPosts()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchPosts()
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchPosts()
}

// 处理标签页切换
const handleTabChange = () => {
  // 标签页变化时重置页码，但不需要在这里调用fetchPosts
  // 因为watch已经监听了activeTab的变化并会调用fetchPosts
  currentPage.value = 1
}

// 加载置顶帖子
const loadPinnedPosts = async () => {
  try {
    const res = await getPinnedPosts(5, userStore.userId || null)
    if (res.code === 200) {
      pinnedPosts.value = res.data || []
    }
  } catch (error) {
    console.error('加载置顶帖子失败:', error)
  }
}

// 处理帖子删除
const handlePostDeleted = (postId) => {
  // 从普通帖子列表中移除
  posts.value = posts.value.filter(post => post.id !== postId)
  
  // 从置顶帖子列表中移除
  pinnedPosts.value = pinnedPosts.value.filter(post => post.id !== postId)
  
  // 更新总数
  if (total.value > 0) {
    total.value--
  }
}

// 处理帖子置顶
const handlePostPinned = (postId) => {
  // 重新加载置顶帖子和普通帖子列表
  loadPinnedPosts()
  loadPosts()
}

// 处理帖子取消置顶
const handlePostUnpinned = (postId) => {
  // 重新加载置顶帖子和普通帖子列表
  loadPinnedPosts()
  loadPosts()
}

// 显示登录对话框
const showLoginDialog = () => {
  loginDialogVisible.value = true
}

// 组件挂载时获取帖子列表
onMounted(() => {
  // 清空初始的示例数据
  posts.value = []
  fetchPosts()
})
</script>

<style lang="scss" scoped>
.forum-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
  
  .forum-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h1 {
      font-size: 24px;
      font-weight: bold;
      color: #303133;
    }
  }
  
  .forum-filters {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 15px;
    
    .el-tabs {
      flex: 1;
    }
    
    .search-box {
      width: 250px;
    }
  }
  
  .section-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 2px solid #3498db;
  }
  
  .pinned-posts {
    margin-bottom: 30px;
  }
  
  .loading-container {
    padding: 20px 0;
  }
  
  .pagination-container {
    margin-top: 30px;
    display: flex;
    justify-content: center;
  }
  
  .empty-state {
    padding: 40px 0;
    text-align: center;
    
    .el-button {
      margin-top: 20px;
    }
  }
}
</style>
