import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './mock' // 确保最先引入 mock
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import { setupRouterGuard } from './router/guard'
import '@/assets/styles/main.scss'
import './index.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 设置路由守卫
setupRouterGuard(router)

app.mount('#app')
