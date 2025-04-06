import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || '') + '/api', // 组合环境变量和固定前缀
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // const token = localStorage.getItem('token')
    // if (token) {
    //   // 确保token格式正确，Spring Security默认需要Bearer前缀
    //   // 检查token是否已经包含Bearer前缀，如果没有则添加
    //   config.headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`
    // }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 处理非200状态码的响应
    if (response.data.code !== 200) {
      ElMessage.error(response.data.message)
      return Promise.reject(response.data)
    }
    return response.data
  },
  error => {
    ElMessage.error(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)

export default request
