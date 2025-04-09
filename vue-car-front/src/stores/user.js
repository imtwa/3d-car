import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({})
  const isLoggedIn = ref(!!token.value)

  // 登录方法
  const login = async (loginData) => {
    try {
      const res = await userApi.login(loginData)
      if (res.code === 200) {
        // 确保token包含Bearer前缀，这是Spring Security的标准格式
        const tokenWithPrefix = res.data.token.startsWith('Bearer ') ? res.data.token : `Bearer ${res.data.token}`
        token.value = tokenWithPrefix
        userInfo.value = res.data.user
        
        isLoggedIn.value = true
        localStorage.setItem('token', tokenWithPrefix)
        return true
      }
      return false
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const res = await userApi.getUserInfo()
      if (res.code === 200) {
        userInfo.value = res.data
        return true
      }
      return false
    } catch (error) {
      return false
    }
  }

  // 退出登录方法
  const logout = async () => {
    try {
      await userApi.logout()
    } finally {
      token.value = ''
      userInfo.value = {}
      isLoggedIn.value = false
      localStorage.removeItem('token')
    }
  }

  // 注册方法
  const register = async (registerData) => {
    try {
      const res = await userApi.register(registerData)
      if (res.code === 200) {
        return {
          success: true,
          message: '注册成功'
        }
      }
      return {
        success: false,
        message: res.message || '注册失败'
      }
    } catch (error) {
      console.error('注册失败:', error)
      return {
        success: false,
        message: error.message || '注册失败'
      }
    }
  }

  // 检查用户名是否存在
  const checkUsername = async (username) => {
    try {
      const res = await userApi.checkUsername(username)
      return res.code === 200 ? res.data : false
    } catch (error) {
      console.error('检查用户名失败:', error)
      return false
    }
  }

  // 验证滑块验证码
  const verifySlideCode = async (slideVerifyFlag) => {
    try {
      const res = await userApi.verifySlideCode(slideVerifyFlag)
      return res.code === 200 ? res.data : false
    } catch (error) {
      console.error('验证滑块验证码失败:', error)
      return false
    }
  }

  // 计算属性：获取用户ID
  const userId = computed(() => userInfo.value?.id)

  return {
    token,
    userInfo,
    isLoggedIn,
    userId,
    login,
    getUserInfo,
    logout,
    register,
    checkUsername,
    verifySlideCode
  }
})
