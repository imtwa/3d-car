<template>
  <div class="main-layout">
    <el-header class="header">
      <el-menu
        mode="horizontal"
        :ellipsis="false"
        class="nav-menu"
        :default-active="activeMenu"
        router
      >
        <!-- Logo和导航部分 -->
        <el-menu-item index="/" class="logo-item">
          <span class="logo-text">3D汽车</span>
        </el-menu-item>
        <el-menu-item index="/brands">品牌专区</el-menu-item>
        <el-menu-item index="/ar">增强现实</el-menu-item>
        <el-menu-item index="/forum">论坛交流</el-menu-item>

        <div class="flex-grow" />

        <!-- 登录状态判断 -->
        <el-menu-item index="user">
          <template v-if="isLoggedIn">
            <el-dropdown>
              <el-avatar :size="40" :src="avatarUrl" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/user')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="router.push('/login')">登录</el-button>
          </template>
        </el-menu-item>
      </el-menu>
    </el-header>

    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const route = useRoute()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const activeMenu = computed(() => {
  return route.path
})

const avatarUrl = computed(
  () =>
    userStore.userInfo.avatar ||
    'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
)

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<style lang="scss" scoped>
.main-layout {
  width: 100%;
  background-color: #ffffff;

  .header {
    background: rgba(28, 28, 30, 0.85);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    position: sticky;
    width: 100%;
    top: 0;
    z-index: 100;
    height: 60px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.12);
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  }

  .main-content {
    flex: 1;
  }

  .nav-menu {
    max-width: 1200px;
    background: transparent;
    width: 100%;
    margin: 0 auto;
    display: flex;
    align-items: center;
    height: 60px;
    padding: 0 20px;
    box-sizing: border-box;
  }

  .logo-item {
    display: flex;
    align-items: center;
    padding: 0 32px;
    transition: all 0.3s ease;

    &:hover {
      transform: scale(1.05);
    }
  }

  .logo-text {
    font-size: 24px;
    font-weight: bold;
    color: #2c75d4;
    text-shadow: 0 0 10px rgba(10, 85, 184, 0.3);
  }

  .flex-grow {
    flex-grow: 1;
  }

  :deep(.el-menu) {
    border-bottom: none;
    width: 100%;
    --el-menu-bg-color: transparent;
    --el-menu-text-color: #f5f5f7;
    --el-menu-hover-text-color: #1E56A0;
    --el-menu-active-color: #1E56A0;
  }

  :deep(.el-menu-item) {
    font-size: 16px;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.08) !important;
      transform: translateY(-2px);
    }

    &.is-active {
      font-weight: 500;
      position: relative;
      
    }
  }

  :deep(.el-dropdown) {
    display: flex;
    align-items: center;
    cursor: pointer;

    .el-avatar {
      border: 2px solid rgba(30, 86, 160, 0.3);
      transition: all 0.3s ease;

      &:hover {
        transform: scale(1.1);
        border-color: #1E56A0;
      }
    }
  }
}
</style>
