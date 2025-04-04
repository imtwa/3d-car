import request from './request'

export const userApi = {
  // 用户登录
  login(data) {
    return request({
      url: `/car/user/login`,
      method: 'post',
      data
    })
  },

  // 用户注册
  register(data) {
    return request({
      url: `/car/user/register`,
      method: 'post',
      data
    })
  },

  // 获取用户信息
  getUserInfo() {
    return request({
      url: `/car/user/info`,
      method: 'get'
    })
  },

  // 退出登录
  logout() {
    return request({
      url: `/car/user/logout`,
      method: 'post'
    })
  },

  // 验证用户名是否存在
  checkUsername(username) {
    return request({
      url: `/car/user/check/${username}`,
      method: 'get'
    })
  },

  // 验证滑块验证码
  verifySlideCode(slideVerifyFlag) {
    return request({
      url: `/car/user/verify/${slideVerifyFlag}`,
      method: 'get'
    })
  }
}
