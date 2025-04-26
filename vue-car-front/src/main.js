import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { setupRouterGuard } from './router/guard'
import '@/assets/styles/main.scss'
import { useUserStore } from './stores/user'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)

// 初始化用户信息
const userStore = useUserStore()
userStore.initStore()

app.use(router)
app.use(ElementPlus)

// 设置路由守卫
setupRouterGuard(router)

app.mount('#app')
