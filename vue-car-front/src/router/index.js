import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/home/index.vue'
import MainLayout from '@/layouts/MainLayout.vue'

const routes = [
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: Home
      },
      {
        path: 'brands',
        name: 'Brands',
        component: () => import('@/views/brands/index.vue')
      },
      {
        path: 'brands/:id',
        name: 'BrandDetail',
        component: () => import('@/views/brands/BrandDetailView.vue')
      },
      {
        path: 'cars/:id',
        name: 'CarDetail',
        component: () => import('@/views/brands/CarDetailView.vue')
      },
      {
        path: 'ar',
        name: 'AR',
        component: () => import('@/views/ar/index.vue')
      },
      {
        path: 'forum',
        name: 'Forum',
        component: () => import('@/views/forum/index.vue')
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/ProfileView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'forum/post',
        name: 'ForumPost',
        component: () => import('@/views/forum/PostView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'forum/post/:id',
        name: 'PostDetail',
        component: () => import('@/views/forum/PostDetailView.vue')
      },
      {
        path: 'forum/post/edit/:id',
        name: 'EditPost',
        component: () => import('@/views/forum/PostView.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/carAR/:id',
    name: 'CarAR',
    component: () => import('@/views/carAR/index.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
