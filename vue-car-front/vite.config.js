import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // 获取请求前缀
  const env = loadEnv(mode, process.cwd())
  const baseApi = env.VITE_API_PREFIX
  
  return {
    plugins: [vue()], // Vue 插件支持
    assetsInclude: ['**/*.glb', '**/*.FBX', '**/*.fbx', '**/*.hdr', '**/*.gltf'],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src')
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/assets/styles/variables.scss" as *;`
        }
      }
    },
    server: {
      port: 8082, // 前台服务器端口
      open: true, // 自动打开浏览器
      proxy: {
        [baseApi]: {
          target: 'http://localhost:8080',
          changeOrigin: true,
          rewrite: p => p.replace(baseApi, '')
        }
      }
    },
    build: {
      rollupOptions: {
        input: {
          main: path.resolve(__dirname, 'index.html')
        }
      }
    }
  }
})
