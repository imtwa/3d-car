import request from './request'

// 获取环境变量中的API前缀和业务前缀
const apiPrefix = import.meta.env.VITE_API_PREFIX || '/api'
const carPrefix = import.meta.env.VITE_API_CAR_PREFIX || '/car'

export const userApi = {
  // 用户登录
  login(data) {
    return request({
      url: `${apiPrefix}${carPrefix}/user/login`,
      method: 'post',
      data
    })
  },

  // 用户注册
  register(data) {
    return request({
      url: `${apiPrefix}${carPrefix}/user/register`,
      method: 'post',
      data
    })
  },

  // 获取用户信息
  getUserInfo() {
    return request({
      url: `${apiPrefix}${carPrefix}/user/info`,
      method: 'get'
    })
  },

  // 退出登录
  logout() {
    return request({
      url: `${apiPrefix}${carPrefix}/user/logout`,
      method: 'post'
    })
  },

  // 验证用户名是否存在
  checkUsername(username) {
    return request({
      url: `${apiPrefix}${carPrefix}/user/check/${username}`,
      method: 'get'
    })
  },

  // 验证滑块验证码
  verifySlideCode(slideVerifyFlag) {
    return request({
      url: `${apiPrefix}${carPrefix}/user/verify/${slideVerifyFlag}`,
      method: 'get'
    })
  }
}
